package seedu.crypto1010.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.util.List;

import org.junit.jupiter.api.Test;

class ViewBlockCommandTest {
    private String normalizeOutput(String s) {
        return s.replaceAll("\r\n", "\n").replaceAll("[ \t]+$", "").trim();
    }

    private String runCommand(Command command, Blockchain blockchain) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        try {
            command.execute(blockchain);
        } catch (Crypto1010Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.setOut(originalOut);
        }
        return outputStream.toString();
    }

    @Test
    void execute_negativeIndex_throwsParseError() {
        Blockchain blockchain = Blockchain.createDefault();
        ViewBlockCommand command = new ViewBlockCommand("-1");

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain));
        assertEquals("Error: INDEX must be a non-negative integer. Use: viewblock INDEX",
                     exception.getMessage());
    }

    @Test
    void execute_nonNumericIndex_throwsParseError() {
        Blockchain blockchain = Blockchain.createDefault();
        ViewBlockCommand command = new ViewBlockCommand("abc");

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain));
        assertEquals("Error: INDEX must be a non-negative integer. Use: viewblock INDEX",
                     exception.getMessage());
    }

    @Test
    void execute_outOfRangeIndex_throwsRangeError() {
        Blockchain blockchain = Blockchain.createDefault();
        ViewBlockCommand command = new ViewBlockCommand("5");

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain));
        assertEquals("Error: Block index out of range.", exception.getMessage());
    }

    @Test
    void execute_genesisBlock_printsBlockDetails() {
        Blockchain blockchain = Blockchain.createDefault();
        ViewBlockCommand command = new ViewBlockCommand("0");
        String output = normalizeOutput(runCommand(command, blockchain));

        assertTrue(output.contains("Block Details"));
        assertTrue(output.contains("Block Index : 0"));
        assertTrue(output.contains("Genesis Block"));
        assertTrue(output.contains(blockchain.getBlock(0).getCurrentHash()));
    }

    @Test
    void execute_validBlock_printsBlockDetails() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        blockchain.addTransactions(List.of("network -> alice : 100"));
        ViewBlockCommand command = new ViewBlockCommand("1");
        String output = normalizeOutput(runCommand(command, blockchain));

        assertTrue(output.contains("Block Details"));
        assertTrue(output.contains("Block Index : 1"));
        assertTrue(output.contains("network -> alice : 100"));
        assertTrue(output.contains(blockchain.getBlock(1).getCurrentHash()));
    }
}
