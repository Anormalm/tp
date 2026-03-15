package seedu.duke.command;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Blockchain;

public abstract class Command {
    protected String helpDescription;
    public abstract void execute(String description, Blockchain blockchain) throws Exceptions;
    public void execute(Blockchain blockchain) throws Exceptions {
        execute("", blockchain);
    }

    Command(String helpDescription) {
        this.helpDescription = helpDescription;
    }

    public void displayHelpDescription() {
        System.out.println(helpDescription);
    }
}
