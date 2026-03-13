package seedu.duke.command;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Blockchain;
import seedu.duke.model.ValidationResult;

public class ValidateCommand extends Command {
    @Override
    public void execute(Blockchain blockchain) throws Exceptions {
        ValidationResult result = blockchain.validate();
        if (result.isValid()) {
            System.out.println("Blockchain is valid. All blocks verified successfully.");
        } else {
            System.out.println("Blockchain is invalid. Reason: " + result.getReason());
        }
    }
}
