package seedu.crypto1010.auth;

/**
 * Represents an authentication or account-validation error that should be shown to the user.
 */
public class AuthenticationException extends Exception {
    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message);
    }
}
