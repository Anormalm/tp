package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Exceptions;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.ValidationResult;

public class ValidateCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: validate
            
            Validates entire blockchain integrity
            """;

    public ValidateCommand() {
        super(HELP_DESCRIPTION);
    }

    @Override
    public void execute(String description, Blockchain blockchain) throws Exceptions {
        ValidationResult result = blockchain.validate();
        if (result.isValid()) {
            System.out.println("Blockchain is valid. All blocks verified successfully.");
        } else {
            System.out.println("Blockchain is invalid. Reason: " + result.getReason());
        }
    }
}
