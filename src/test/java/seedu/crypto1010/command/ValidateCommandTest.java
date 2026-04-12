package seedu.crypto1010.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Block;
import seedu.crypto1010.model.Blockchain;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class  ValidateCommandTest {
    private String normalizeOutput(String s) {
        return s.replaceAll("\r\n", "\n").replaceAll("[ \t]+$", "").trim();
    }

    @Test
    void execute_validBlockchain_printsSuccessMessage() {
        Blockchain blockchain = Blockchain.createDefault();
        ValidateCommand command = new ValidateCommand();

        String output = runCommand(command, blockchain);

        String normalized = normalizeOutput(output);
        assertTrue(normalized.contains("Blockchain Validation"));
        assertTrue(normalized.contains("Status : Valid"));
        assertTrue(normalized.contains("Details : All blocks verified successfully."));
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

        String normalized = normalizeOutput(output);
        assertTrue(normalized.contains("Blockchain Validation"));
        assertTrue(normalized.contains("Status : Invalid"));
        assertTrue(normalized.contains("Reason : Hash mismatch at Block 1."));
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

        String normalized = normalizeOutput(output);
        assertTrue(normalized.contains("Blockchain Validation"));
        assertTrue(normalized.contains("Status : Invalid"));
        assertTrue(normalized.contains("Reason : Invalid previous hash linkage at Block 1."));
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

        String normalized = normalizeOutput(output);
        assertTrue(normalized.contains("Blockchain Validation"));
        assertTrue(normalized.contains("Status : Invalid"));
        assertTrue(normalized.contains("Invalid transaction data"));
        assertTrue(normalized.contains("blank"));
        assertTrue(normalized.contains("transaction"));
    }

    @Test
    void execute_negativeAmountTransaction_printsTransactionReason() {
        Block genesis = new Block(
                0,
                LocalDateTime.of(2026, 2, 12, 14, 30, 21),
                "0000000000000000",
                List.of("Genesis Block"));
        Block secondBlock = new Block(
                1,
                LocalDateTime.of(2026, 2, 12, 14, 35, 2),
                genesis.getCurrentHash(),
                List.of("alice -> bob : -10"));
        Blockchain blockchain = new Blockchain(List.of(genesis, secondBlock));
        ValidateCommand command = new ValidateCommand();

        String output = runCommand(command, blockchain);

        String normalized = normalizeOutput(output);
        assertTrue(normalized.contains("Blockchain Validation"));
        assertTrue(normalized.contains("Status : Invalid"));
        assertTrue(normalized.contains("Reason : Invalid transaction amount at Block 1, Transaction 0: -10"));
    }

    @Test
    void execute_insufficientBalanceTransaction_printsBalanceReason() {
        Block genesis = new Block(
                0,
                LocalDateTime.of(2026, 2, 12, 14, 30, 21),
                "0000000000000000",
                List.of("Genesis Block"));
        Block secondBlock = new Block(
                1,
                LocalDateTime.of(2026, 2, 12, 14, 35, 2),
                genesis.getCurrentHash(),
                List.of("alice -> bob : 10"));
        Blockchain blockchain = new Blockchain(List.of(genesis, secondBlock));
        ValidateCommand command = new ValidateCommand();

        String output = runCommand(command, blockchain);

        String normalized = normalizeOutput(output);
        assertTrue(normalized.contains("Blockchain Validation"));
        assertTrue(normalized.contains("Status : Invalid"));
        assertTrue(normalized.contains("Insufficient balance at Block 1"));
        assertTrue(normalized.contains("sender"));
        assertTrue(normalized.contains("alice"));
        assertTrue(normalized.contains("needs 10"));
    }

    @Test
    void execute_nonSequentialBlockIndex_printsIndexReason() {
        Block genesis = new Block(
                0,
                LocalDateTime.of(2026, 2, 12, 14, 30, 21),
                "0000000000000000",
                List.of("Genesis Block"));
        Block secondBlock = new Block(
                2,
                LocalDateTime.of(2026, 2, 12, 14, 35, 2),
                genesis.getCurrentHash(),
                List.of("alice -> bob : 10"));
        Blockchain blockchain = new Blockchain(List.of(genesis, secondBlock));
        ValidateCommand command = new ValidateCommand();

        String output = runCommand(command, blockchain);

        String normalized = normalizeOutput(output);
        assertTrue(normalized.contains("Blockchain Validation"));
        assertTrue(normalized.contains("Status : Invalid"));
        assertTrue(normalized.contains("Reason : Invalid block index at Block 1."));
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
