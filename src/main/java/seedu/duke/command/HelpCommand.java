package seedu.duke.command;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Blockchain;

public class HelpCommand extends Command {
    @Override
    public void execute(Blockchain blockchain) throws Exceptions {
        System.out.println("help command executed");
    }
}
