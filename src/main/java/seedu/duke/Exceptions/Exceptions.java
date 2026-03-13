package seedu.duke.Exceptions;

/**
 * Custom exception type used throughout the Crypto1010 application.
 * Allows user-friendly and personality-filled error messages.
 */
public class Exceptions extends Exception {
    /**
     * Creates a new {@code Exceptions} with the given message.
     *
     * @param message detail message describing the error
     */
    public Exceptions(String message) {
        super(message);
    }
}
