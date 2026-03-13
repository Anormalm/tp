package seedu.duke.command;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Blockchain;

public class KeygenCommand extends Command {
    @Override
    public void execute(Blockchain blockchain) throws Exceptions {
        System.out.println("keygen command executed");
    }
}
