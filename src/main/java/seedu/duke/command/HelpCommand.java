package seedu.duke.command;

import seedu.duke.Parser;
import seedu.duke.model.Blockchain;
import seedu.duke.model.WalletManager;

import java.util.Scanner;

public class HelpCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: help [c/COMMAND]
            Example: help c/list
            
            COMMAND is optional
            If no valid COMMAND is given: lists all the available commands
            If a valid COMMAND is given: displays details regarding that command
            """;

    private static final String HELP_MESSAGE =
            "For more details about each command type 'help c/COMMAND', eg. 'help c/list'";

    public HelpCommand() {
        super(HELP_DESCRIPTION);
    }

    @Override
    public void execute(String description, Blockchain blockchain) {
        WalletManager walletManager = new WalletManager();
        Parser parser = new Parser(walletManager);
        String[] components = description.split("c/");
        try {
            if (components.length < 2) {
                for (CommandWord c : CommandWord.values()) {
                    assert c.getCommand() != null : "command word should have a command";
                    assert c.getDescription() != null : "command word should have a description";

                    System.out.print("  ");
                    System.out.print(c.getCommand());
                    for (int i = 0; i < 12 - c.getCommand().length(); i++) {
                        System.out.print(" ");
                    }
                    System.out.println(c.getDescription());
                }
                System.out.println(HELP_MESSAGE);
            } else {
                Command c = parser.parse(components[1]);
                c.displayHelpDescription();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Please input a valid command, use 'help' to see the list of commands");
        }
    }

    private void tutorial() {
        Scanner in = new Scanner(System.in);
        String[] instructions = {
                "create w/alice",
                "create w/bob",
                "keygen w/alice",
                "keygen w/bob",
                "list",
        };
        int index = 0;

        while (true) {
            System.out.println("Enter the following command:");
            System.out.println(instructions[index]);
            String input = in.nextLine().strip();
            if (input.equals(instructions[index])) {
                // Do the executing
                index++;
            } else if (input.equals("exit()")) {
                return;
            } else {
                System.out.println("That was not the given instruction");
                System.out.println("If you wish to exit type: exit()");
            }
        }
    }
}
