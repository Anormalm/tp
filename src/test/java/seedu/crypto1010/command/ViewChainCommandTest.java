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
    @Test
    void execute_validFormat_printsBlockchainOverview() {
        Blockchain blockchain = Blockchain.createDefault();
        ViewChainCommand command = new ViewChainCommand();

        String output = runCommand(command, blockchain);

        Block genesis = blockchain.getBlock(0);
        Block secondBlock = blockchain.getBlock(1);
        assertTrue(output.contains("Blockchain Overview:"));
        assertTrue(output.contains("Total blocks: 2"));
        assertTrue(output.contains("Total transactions: 4"));
        assertTrue(output.contains("Blocks:"));
        assertTrue(output.contains("0 | tx=1 | time=2026-02-12 14:30:21 | hash="
                + compactHash(genesis.getCurrentHash())));
        assertTrue(output.contains("1 | tx=3 | time=2026-02-12 14:35:02 | hash="
                + compactHash(secondBlock.getCurrentHash())));
    }

    @Test
    void execute_emptyBlockchain_printsZeroTransactions() {
        Blockchain blockchain = new Blockchain(List.of(
                new Block(0, blockchainTime(), "0000000000000000", List.of("Genesis Block"))));
        ViewChainCommand command = new ViewChainCommand();

        String output = runCommand(command, blockchain);

        assertTrue(output.contains("Total blocks: 1"));
        assertTrue(output.contains("Total transactions: 1"));
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
