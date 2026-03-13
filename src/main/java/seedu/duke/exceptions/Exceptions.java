package seedu.duke.exceptions;

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

    /**
     * Prints an error message to the console.
     *
     * @param message the error message to print
     */
    public static void printError(String message) {
        System.out.println(message);
    }
}
