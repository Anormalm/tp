package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.ui.CliVisuals;

import java.util.Scanner;

/**
 * Ends the current account session after explicit confirmation.
 */
public class LogoutCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: logout
            Logs out of the current account and returns to account access.
            You will be prompted to confirm with y or n.
            """;
    private static final String CONFIRMATION_PROMPT = "Confirm logout? (y/n)";
    private static final String INVALID_CONFIRMATION =
            "Error: Please enter 'y' to confirm logout or 'n' to stay logged in.";
    private static final String LOGOUT_CANCELLED_MESSAGE = "Logout cancelled.";
    private static final String LOGGING_OUT_MESSAGE = "Logging out...";
    private static final String INTERACTIVE_INPUT_REQUIRED_ERROR =
            "Error: Logout confirmation requires interactive input.";

    private boolean logoutConfirmed;

    public LogoutCommand() {
        super(HELP_DESCRIPTION);
    }

    @Override
    public void execute(Blockchain blockchain, Scanner in) throws Crypto1010Exception {
        if (in == null) {
            throw new Crypto1010Exception(INTERACTIVE_INPUT_REQUIRED_ERROR);
        }

        logoutConfirmed = false;
        while (true) {
            CliVisuals.printInfo(CONFIRMATION_PROMPT);
            if (!in.hasNextLine()) {
                CliVisuals.printInfo(LOGOUT_CANCELLED_MESSAGE);
                return;
            }

            String confirmation = in.nextLine().strip();
            if ("y".equalsIgnoreCase(confirmation)) {
                logoutConfirmed = true;
                CliVisuals.printInfo(LOGGING_OUT_MESSAGE);
                return;
            }
            if ("n".equalsIgnoreCase(confirmation)) {
                CliVisuals.printInfo(LOGOUT_CANCELLED_MESSAGE);
                return;
            }

            CliVisuals.printError(INVALID_CONFIRMATION);
        }
    }

    public boolean isLogoutConfirmed() {
        return logoutConfirmed;
    }
}
