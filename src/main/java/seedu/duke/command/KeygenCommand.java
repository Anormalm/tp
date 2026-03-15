package seedu.duke.command;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Blockchain;

public class KeygenCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            format: keygen w/NAME
            Generates and displays key pair for new wallet, or regenerates for existing wallet
            Displays the process of creating a key pair
            """;

    public KeygenCommand() {
        super(HELP_DESCRIPTION);
    }

    @Override
    public void execute(String description, Blockchain blockchain) throws Exceptions {
        System.out.println("keygen command executed");
    }
}
