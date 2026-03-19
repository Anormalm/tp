package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Exceptions;
import seedu.crypto1010.model.Blockchain;

public abstract class Command {
    protected String helpDescription;

    Command(String helpDescription) {
        this.helpDescription = helpDescription;
    }

    public abstract void execute(String description, Blockchain blockchain) throws Exceptions;

    public void execute(Blockchain blockchain) throws Exceptions {
        execute("", blockchain);
    }

    public void displayHelpDescription() {
        System.out.println(helpDescription);
    }
}
