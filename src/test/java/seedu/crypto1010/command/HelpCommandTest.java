package seedu.crypto1010.command;

import org.junit.jupiter.api.Test;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.exceptions.Crypto1010Exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HelpCommandTest {

    @Test
    public void execute_noCommand_showsGeneralHelpMessage() throws Crypto1010Exception {
        HelpCommand helpCommand = new HelpCommand("");
        Blockchain blockchain = Blockchain.createDefault();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            helpCommand.execute(blockchain);
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();
        assertTrue(output.contains(
                "For more details about each command type 'help c/COMMAND', eg. 'help c/list'"
        ));
    }

    @Test
    public void execute_invalidCommand_throwsCrypto1010Exception() {
        HelpCommand helpCommand = new HelpCommand("c/invalidCommand");
        Blockchain blockchain = Blockchain.createDefault();

        Crypto1010Exception thrown = assertThrows(
                Crypto1010Exception.class,
                () -> helpCommand.execute(blockchain)
        );

        assertEquals("Error: Invalid help format. Use: help [c/COMMAND]", thrown.getMessage());
    }

    @Test
    public void execute_helpForSpecificCommand_showsCommandFormat() throws Crypto1010Exception {
        HelpCommand helpCommand = new HelpCommand("c/send");
        Blockchain blockchain = Blockchain.createDefault();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            helpCommand.execute(blockchain);
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();
        assertTrue(output.contains(
                "Format: send w/WALLET_NAME to/RECIPIENT_ADDRESS amt/AMOUNT [speed/SPEED] [fee/FEE] [note/MEMO]"
        ));
    }

    @Test
    public void execute_helpForHistoryCommand_showsCommandFormat() throws Crypto1010Exception {
        HelpCommand helpCommand = new HelpCommand("c/history");
        Blockchain blockchain = Blockchain.createDefault();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            helpCommand.execute(blockchain);
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();
        assertTrue(output.contains("Format: history w/WALLET_NAME"));
    }
}
