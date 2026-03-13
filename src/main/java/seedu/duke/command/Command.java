package seedu.duke.command;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Blockchain;

public abstract class Command {
    public abstract void execute(Blockchain blockchain) throws Exceptions;
}
