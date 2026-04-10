package seedu.crypto1010.command;

import seedu.crypto1010.Parser;
import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.WalletManager;

import java.util.Scanner;

public class HelpCommand extends Command {
    private static final String COMMAND_PREFIX = "c/";
    private static final int COMMAND_LIST_COLUMN_WIDTH = 12;
    private static final String HELP_DESCRIPTION = """
            Format: help [c/COMMAND]
            Example: help c/list
            
            COMMAND is optional
            If no valid COMMAND is given: lists all the available commands
            If a valid COMMAND is given: displays details regarding that command
            """;
    private static final String HELP_MESSAGE =
            "For more details about each command type 'help c/COMMAND', eg. 'help c/list'";
    private static final String INVALID_FORMAT_ERROR = "Error: Invalid help format. Use: help [c/COMMAND]";

    private final String arguments;

    public HelpCommand(String arguments) {
        super(HELP_DESCRIPTION);
        this.arguments = arguments;
    }

    @Override
    public void execute(Blockchain blockchain, Scanner in) throws Crypto1010Exception {
        WalletManager walletManager = new WalletManager();
        Parser parser = new Parser(walletManager);

        try {
            if (arguments.isEmpty()) {
                System.out.println();
                System.out.println("Available Commands");
                System.out.println("=".repeat(40));
                System.out.printf("%-14s | %s%n", "Command", "Description");
                System.out.println("-".repeat(40));
                for (CommandWord c : CommandWord.values()) {
                    assert c.getCommand() != null : "command word should have a command";
                    assert c.getDescription() != null : "command word should have a description";
                    System.out.printf("%-14s | %s%n", c.getCommand(), c.getDescription());
                }
                System.out.println("=".repeat(40));
                System.out.println(HELP_MESSAGE);
            } else {
                if (!arguments.startsWith(COMMAND_PREFIX)) {
                    throw new Crypto1010Exception(INVALID_FORMAT_ERROR);
                }

                String commandName = arguments.substring(COMMAND_PREFIX.length()).trim();
                if (commandName.isEmpty() || commandName.chars().anyMatch(Character::isWhitespace)) {
                    throw new Crypto1010Exception(INVALID_FORMAT_ERROR);
                }

                Command c = parser.parse(commandName);
                c.displayHelpDescription();
            }
        } catch (IllegalArgumentException e) {
            throw new Crypto1010Exception(INVALID_FORMAT_ERROR);
        }
    }
}
