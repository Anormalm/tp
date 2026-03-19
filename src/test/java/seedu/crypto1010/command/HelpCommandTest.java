package seedu.crypto1010.command;

import org.junit.jupiter.api.Test;
import seedu.crypto1010.model.Blockchain;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelpCommandTest {

    @Test
    public void execute_noCommand_showsGeneralHelpMessage() {
        HelpCommand helpCommand = new HelpCommand();
        Blockchain blockchain = Blockchain.createDefault();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            helpCommand.execute("help", blockchain);
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();
        assertTrue(output.contains(
                "For more details about each command type 'help c/COMMAND', eg. 'help c/list'"
        ));
    }

    @Test
    public void execute_invalidCommand_showsErrorMessage() {
        HelpCommand helpCommand = new HelpCommand();
        Blockchain blockchain = Blockchain.createDefault();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            helpCommand.execute("help c/invalidcommand", blockchain);
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();
        assertTrue(output.contains(
                "Please input a valid command, use 'help' to see the list of commands"
        ));
    }
}
