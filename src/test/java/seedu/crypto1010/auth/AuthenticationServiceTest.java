package seedu.crypto1010.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.storage.AccountStorage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AuthenticationServiceTest {
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        System.setProperty("crypto1010.dataDir", tempDir.toString());
    }

    @AfterEach
    void tearDown() {
        System.clearProperty("crypto1010.dataDir");
    }

    @Test
    void registerThenAuthenticate_persistsAndAuthenticatesCaseInsensitively() throws Exception {
        AuthenticationService authenticationService =
                new AuthenticationService(new AccountStorage(AuthenticationServiceTest.class));
        authenticationService.load();

        assertFalse(authenticationService.hasRegisteredAccounts());
        assertEquals("alice_1", authenticationService.register("Alice_1", "secret1", "secret1"));
        assertTrue(authenticationService.hasRegisteredAccounts());
        assertEquals("alice_1", authenticationService.authenticate("ALICE_1", "secret1"));

        AuthenticationService reloadedService =
                new AuthenticationService(new AccountStorage(AuthenticationServiceTest.class));
        reloadedService.load();

        assertEquals("alice_1", reloadedService.authenticate("alice_1", "secret1"));
    }

    @Test
    void register_duplicateUsername_throwsException() throws Exception {
        AuthenticationService authenticationService =
                new AuthenticationService(new AccountStorage(AuthenticationServiceTest.class));
        authenticationService.load();
        authenticationService.register("alice", "secret1", "secret1");

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> authenticationService.register("ALICE", "another1", "another1"));

        assertEquals("Error: Username already exists.", exception.getMessage());
    }

    @Test
    void authenticate_wrongPassword_throwsException() throws Exception {
        AuthenticationService authenticationService =
                new AuthenticationService(new AccountStorage(AuthenticationServiceTest.class));
        authenticationService.load();
        authenticationService.register("alice", "secret1", "secret1");

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> authenticationService.authenticate("alice", "wrongpw"));

        assertEquals("Error: Invalid username or password.", exception.getMessage());
    }

    @Test
    void register_invalidUsername_throwsException() throws Exception {
        AuthenticationService authenticationService =
                new AuthenticationService(new AccountStorage(AuthenticationServiceTest.class));
        authenticationService.load();

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> authenticationService.register("ab", "secret1", "secret1"));

        assertEquals("Error: Username must be 3-20 characters with no spaces, using letters, numbers, '_' or '-'.",
                exception.getMessage());
    }

    @Test
    void register_shortPassword_throwsException() throws Exception {
        AuthenticationService authenticationService =
                new AuthenticationService(new AccountStorage(AuthenticationServiceTest.class));
        authenticationService.load();

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> authenticationService.register("alice", "123", "123"));

        assertEquals("Error: Password must be at least 6 characters.", exception.getMessage());
    }

    @Test
    void load_invalidStoredUsername_throwsIOException() throws Exception {
        Path credentialsFile = tempDir.resolve("accounts").resolve("credentials.txt");
        Files.createDirectories(credentialsFile.getParent());
        Files.writeString(
                credentialsFile,
                "U|..\\evil|00112233445566778899aabbccddeeff|abcdef0123456789" + System.lineSeparator(),
                StandardCharsets.UTF_8);

        AuthenticationService authenticationService =
                new AuthenticationService(new AccountStorage(AuthenticationServiceTest.class));

        IOException exception = assertThrows(IOException.class, authenticationService::load);
        assertTrue(exception.getMessage().startsWith("Invalid account data: username"));
    }
}
