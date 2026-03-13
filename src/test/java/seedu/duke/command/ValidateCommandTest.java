package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Block;
import seedu.duke.model.Blockchain;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class ValidateCommandTest {
    @Test
    void execute_validBlockchain_printsSuccessMessage() {
        Blockchain blockchain = Blockchain.createDefault();
        ValidateCommand command = new ValidateCommand();

        String output = runCommand(command, blockchain);

        assertEquals("Blockchain is valid. All blocks verified successfully." + System.lineSeparator(), output);
    }

    @Test
    void execute_hashMismatch_printsHashMismatchReason() {
        Block genesis = new Block(
                0,
                LocalDateTime.of(2026, 2, 12, 14, 30, 21),
                "0000000000000000",
                List.of("Genesis Block"));
        Block tamperedBlock = new Block(
                1,
                LocalDateTime.of(2026, 2, 12, 14, 35, 2),
                genesis.getCurrentHash(),
                List.of("alice -> bob : 10"),
                "not-a-real-hash");
        Blockchain blockchain = new Blockchain(List.of(genesis, tamperedBlock));
        ValidateCommand command = new ValidateCommand();

        String output = runCommand(command, blockchain);

        assertEquals(
                "Blockchain is invalid. Reason: Hash mismatch at Block 1." + System.lineSeparator(),
                output);
    }

    @Test
    void execute_invalidLinkage_printsLinkageReason() {
        Block genesis = new Block(
                0,
                LocalDateTime.of(2026, 2, 12, 14, 30, 21),
                "0000000000000000",
                List.of("Genesis Block"));
        Block secondBlock = new Block(
                1,
                LocalDateTime.of(2026, 2, 12, 14, 35, 2),
                "wrong-previous-hash",
                List.of("alice -> bob : 10"));
        Blockchain blockchain = new Blockchain(List.of(genesis, secondBlock));
        ValidateCommand command = new ValidateCommand();

        String output = runCommand(command, blockchain);

        assertEquals(
                "Blockchain is invalid. Reason: Invalid previous hash linkage at Block 1."
                        + System.lineSeparator(),
                output);
    }

    @Test
    void execute_invalidTransactions_printsTransactionReason() {
        Block genesis = new Block(
                0,
                LocalDateTime.of(2026, 2, 12, 14, 30, 21),
                "0000000000000000",
                List.of("Genesis Block"));
        Block secondBlock = new Block(
                1,
                LocalDateTime.of(2026, 2, 12, 14, 35, 2),
                genesis.getCurrentHash(),
                List.of(" "));
        Blockchain blockchain = new Blockchain(List.of(genesis, secondBlock));
        ValidateCommand command = new ValidateCommand();

        String output = runCommand(command, blockchain);

        assertEquals(
                "Blockchain is invalid. Reason: Invalid transaction data at Block 1."
                        + System.lineSeparator(),
                output);
    }

    private String runCommand(Command command, Blockchain blockchain) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        try {
            command.execute(blockchain);
        } catch (Exceptions e) {
            throw new RuntimeException(e);
        } finally {
            System.setOut(originalOut);
        }
        return outputStream.toString();
    }
}
