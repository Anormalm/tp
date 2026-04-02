package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.ValidationResult;

import java.util.Scanner;

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
            System.out.println("Blockchain is valid. All blocks verified successfully.");
        } else {
            System.out.println("Blockchain is invalid. Reason: " + result.getReason());
        }
    }
}
