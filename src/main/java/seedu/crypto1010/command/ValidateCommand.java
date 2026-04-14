package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.ValidationResult;
import seedu.crypto1010.ui.CliVisuals;

import java.util.List;
import java.util.Scanner;

/**
 * Validates the integrity of the current blockchain.
 */
public class ValidateCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: validate
            
            Validates entire blockchain integrity
            """;

    public ValidateCommand() {
        super(HELP_DESCRIPTION);
    }

    @Override
    public void execute(Blockchain blockchain, Scanner in) throws Crypto1010Exception {
        ValidationResult result = blockchain.validate();
        if (result.isValid()) {
            CliVisuals.printKeyValuePanel("Blockchain Validation", List.of(
                    List.of("Status", "Valid"),
                    List.of("Details", "All blocks verified successfully.")));
        } else {
            CliVisuals.printKeyValuePanel("Blockchain Validation", List.of(
                    List.of("Status", "Invalid"),
                    List.of("Reason", result.getReason())));
        }
    }
}
