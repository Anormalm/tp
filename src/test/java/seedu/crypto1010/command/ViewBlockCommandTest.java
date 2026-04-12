package seedu.crypto1010.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class ViewBlockCommandTest {
    // Helper to normalize output for robust comparison
    private String normalizeOutput(String s) {
        return s.replaceAll("\r\n", "\n").replaceAll("[ \t]+$", "").trim();
    }
    @Test
    void execute_validIndex_printsBlockDetails() {
        Blockchain blockchain = Blockchain.createDefault();
        ViewBlockCommand command = new ViewBlockCommand("1");

        String output = runCommand(command, blockchain);

        String normalized = normalizeOutput(output);
        assertTrue(normalized.contains("Block Details"));
        assertTrue(normalized.contains("Block Index : 1"));
        assertTrue(normalized.contains("Timestamp : 2026-02-12 14:35:02"));
        assertTrue(normalized.contains("Previous Hash :"));
        assertTrue(normalized.contains(blockchain.getBlock(1).getPreviousHash().substring(0, 16)));
        assertTrue(normalized.contains("Current Hash :"));
        assertTrue(normalized.contains(blockchain.getBlock(1).getCurrentHash().substring(0, 16)));
        assertTrue(normalized.contains("Transactions"));
        assertTrue(normalized.contains("No. | Transaction"));
        assertTrue(normalized.contains("1   | network -> alice : 10"));
        assertTrue(normalized.contains("2   | alice -> bob : 10"));
        assertTrue(normalized.contains("3   | bob -> carol : 5"));
    }

    @Test
    void execute_negativeIndex_printsParseError() {
        Blockchain blockchain = Blockchain.createDefault();
        ViewBlockCommand command = new ViewBlockCommand("-1");

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: INDEX must be a non-negative integer. Use: viewblock INDEX", exception.getMessage());
    }

    @Test
    void execute_nonNumericIndex_printsParseError() {
        Blockchain blockchain = Blockchain.createDefault();
        ViewBlockCommand command = new ViewBlockCommand("abc");

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: INDEX must be a non-negative integer. Use: viewblock INDEX", exception.getMessage());
    }

    @Test
    void execute_outOfRangeIndex_printsRangeError() {
        Blockchain blockchain = Blockchain.createDefault();
        ViewBlockCommand command = new ViewBlockCommand("5");

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: Block index out of range.", exception.getMessage());
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
}
