package seedu.crypto1010.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Block;
import seedu.crypto1010.model.Blockchain;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.Test;

class ViewChainCommandTest {
    // Helper to normalize output for robust comparison
    private String normalizeOutput(String s) {
        return s.replaceAll("\r\n", "\n").replaceAll("[ \t]+$", "").trim();
    }

    @Test
    void execute_emptyBlockchain_printsZeroTransactions() {
        Blockchain blockchain = new Blockchain(List.of(
                new Block(0, blockchainTime(), "0000000000000000", List.of("Genesis Block"))));
        ViewChainCommand command = new ViewChainCommand();

        String output = runCommand(command, blockchain);

        String norm = normalizeOutput(output);
        assertTrue(norm.contains("Total blocks : 1"));
        assertTrue(norm.contains("Total transactions : 1"));
    }

    private java.time.LocalDateTime blockchainTime() {
        return java.time.LocalDateTime.of(2026, 2, 12, 14, 30, 21);
    }

    private String compactHash(String hash) {
        return hash.substring(0, 12) + "...";
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
