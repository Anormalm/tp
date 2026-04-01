package seedu.crypto1010.auth;

import java.util.Objects;

public record AccountCredentials(String username, String saltHex, String passwordHashHex) {
    public AccountCredentials {
        Objects.requireNonNull(username, "username must not be null");
        Objects.requireNonNull(saltHex, "saltHex must not be null");
        Objects.requireNonNull(passwordHashHex, "passwordHashHex must not be null");
    }
}
