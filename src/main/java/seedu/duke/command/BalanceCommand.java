package seedu.duke.command;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Blockchain;

public class BalanceCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: balance NAME
            Displays the balance of wallet up to 8 decimal points
            """;

    public BalanceCommand() {
        super(HELP_DESCRIPTION);
    }

    @Override
    public void execute(String description, Blockchain blockchain) throws Exceptions {
        System.out.println("balance command executed");
    }
}
