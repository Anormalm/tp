package seedu.duke;

import seedu.duke.command.Command;
import seedu.duke.command.ExitCommand;
import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Blockchain;
import seedu.duke.model.WalletManager;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Duke {
    /**
     * Main entry-point for the java.duke.Duke application.
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        Parser parser = new Parser(walletManager);

        while (true) {
            String message = in.nextLine().strip();
            try {
                String[] components = message.split("\\s+", 2);
                Command c = parser.parse(components[0]);
                if (c instanceof ExitCommand) {
                    break;
                }
                c.execute(components[1], blockchain);
            } catch (Exceptions e) {
                System.out.println(e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid Command");
            } catch (NoSuchElementException e) {
                System.out.println("No Input");
            } catch (ArrayIndexOutOfBoundsException e) {
                String[] components = message.split("\\s+", 2);
                Command c = parser.parse(components[0]);
                c.execute("", blockchain);
            }
        }
    }
}
