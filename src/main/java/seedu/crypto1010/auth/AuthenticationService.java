package seedu.crypto1010.auth;

import seedu.crypto1010.storage.AccountStorage;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Validates account input and coordinates loading, registering, and authenticating users.
 */
public class AuthenticationService {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{3,20}$");
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofSeconds(30);
    private static final String LOCKED_OUT_ERROR =
            "Error: Too many failed login attempts. Try again in 30 seconds.";

    private final AccountStorage accountStorage;
    private final Map<String, AccountCredentials> accountsByUsername;
    private final Map<String, FailedAttemptState> failedAttemptsByUsername;
    private final Clock clock;

    public AuthenticationService(AccountStorage accountStorage) {
        this(accountStorage, Clock.systemUTC());
    }

    AuthenticationService(AccountStorage accountStorage, Clock clock) {
        this.accountStorage = Objects.requireNonNull(accountStorage);
        this.clock = Objects.requireNonNull(clock);
        this.accountsByUsername = new LinkedHashMap<>();
        this.failedAttemptsByUsername = new LinkedHashMap<>();
    }

    public void load() throws IOException {
        accountsByUsername.clear();
        for (AccountCredentials credentials : accountStorage.load()) {
            String normalizedUsername = normalizeUsername(credentials.username());
            try {
                validateUsername(normalizedUsername);
            } catch (AuthenticationException e) {
                throw new IOException("Invalid account data: username '" + normalizedUsername + "' is invalid.", e);
            }
            if (accountsByUsername.containsKey(normalizedUsername)) {
                throw new IOException("Invalid account data: duplicate username '" + normalizedUsername + "'.");
            }
            accountsByUsername.put(normalizedUsername, credentials);
        }
    }

    public boolean hasRegisteredAccounts() {
        return !accountsByUsername.isEmpty();
    }

    public String authenticate(String username, String password) throws AuthenticationException {
        String normalizedUsername = normalizeUsername(username);
        enforceLockoutIfActive(normalizedUsername);

        AccountCredentials credentials = accountsByUsername.get(normalizedUsername);
        if (credentials == null || !PasswordHasher.matches(password, credentials)) {
            recordFailedAttempt(normalizedUsername);
            throw new AuthenticationException("Error: Invalid username or password.");
        }
        failedAttemptsByUsername.remove(normalizedUsername);
        return normalizedUsername;
    }

    public String register(String username, String password, String passwordConfirmation)
            throws AuthenticationException, IOException {
        String normalizedUsername = normalizeUsername(username);
        validateUsername(normalizedUsername);
        validatePassword(password);
        if (!password.equals(passwordConfirmation)) {
            throw new AuthenticationException("Error: Password confirmation does not match.");
        }
        if (accountsByUsername.containsKey(normalizedUsername)) {
            throw new AuthenticationException("Error: Username already exists.");
        }

        String saltHex = PasswordHasher.generateSaltHex();
        String passwordHashHex = PasswordHasher.hash(password, saltHex);
        AccountCredentials credentials = new AccountCredentials(normalizedUsername, saltHex, passwordHashHex);
        accountsByUsername.put(normalizedUsername, credentials);
        accountStorage.save(accountsByUsername.values());
        return normalizedUsername;
    }

    public String validateNewUsername(String username) throws AuthenticationException {
        String normalizedUsername = normalizeUsername(username);
        validateUsername(normalizedUsername);
        if (accountsByUsername.containsKey(normalizedUsername)) {
            throw new AuthenticationException("Error: Username already exists.");
        }
        return normalizedUsername;
    }

    private void validateUsername(String username) throws AuthenticationException {
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new AuthenticationException(
                    "Error: Username must be 3-20 characters with no spaces, using letters, numbers, '_' or '-'.");
        }
    }

    private void validatePassword(String password) throws AuthenticationException {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new AuthenticationException("Error: Password must be at least 6 characters.");
        }
    }

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    private void enforceLockoutIfActive(String normalizedUsername) throws AuthenticationException {
        FailedAttemptState state = failedAttemptsByUsername.get(normalizedUsername);
        if (state == null) {
            return;
        }

        Instant now = clock.instant();
        if (state.lockedUntil != null && now.isBefore(state.lockedUntil)) {
            throw new AuthenticationException(LOCKED_OUT_ERROR);
        }
        if (state.lockedUntil != null && !now.isBefore(state.lockedUntil)) {
            failedAttemptsByUsername.remove(normalizedUsername);
        }
    }

    private void recordFailedAttempt(String normalizedUsername) {
        FailedAttemptState state = failedAttemptsByUsername.computeIfAbsent(
                normalizedUsername,
                ignored -> new FailedAttemptState());
        state.count++;
        if (state.count >= MAX_FAILED_ATTEMPTS) {
            state.lockedUntil = clock.instant().plus(LOCKOUT_DURATION);
            state.count = 0;
        }
    }

    private static final class FailedAttemptState {
        private int count;
        private Instant lockedUntil;
    }
}
