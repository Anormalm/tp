package seedu.duke.command;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Blockchain;

public abstract class Command {
    protected String helpDescription;
    public abstract void execute(String description, Blockchain blockchain) throws Exceptions;

    Command(String helpDescription) {
        this.helpDescription = helpDescription;
    }

    public void displayHelpDescription() {
        System.out.println(helpDescription);
    }
}
