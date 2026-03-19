package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Exceptions;
import seedu.crypto1010.model.Blockchain;

public class ExitCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            format: exit
            Exits the program
            """;

    public ExitCommand() {
        super(HELP_DESCRIPTION);
    }

    @Override
    public void execute(String description, Blockchain blockchain) throws Exceptions {
        System.out.println("exit command executed");
    }
}
