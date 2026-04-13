// ...existing code...
package seedu.crypto1010.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Block;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.WalletManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class BalanceCommandTest {
    private String normalizeOutput(String s) {
        return s.replaceAll("\r\n", "\n").replaceAll("[ \t]+$", "").trim();
    }

    @Test
    void execute_existingWallet_printsBalanceToEightDecimalPlaces() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("bob");
        BalanceCommand command = new BalanceCommand("w/bob", walletManager);

        String output = runCommand(command, blockchain);

        String normalized = normalizeOutput(output);
        assertTrue(normalized.contains("Wallet Balance"));
        assertTrue(normalized.contains("Wallet : bob"));
        assertTrue(normalized.contains("Balance : 5.00000000"));
    }


    @Test
    void execute_decimalBalance_roundsToEightDecimalPlaces() {
        Blockchain blockchain = new Blockchain(List.of(
            new Block(
                0,
                LocalDateTime.of(2026, 2, 12, 14, 30, 21),
                "0000000000000000",
                List.of("Genesis Block")),
            new Block(
                1,
                LocalDateTime.of(2026, 2, 12, 14, 35, 2),
                "prev-hash",
                List.of("miner -> alice : 1.234567895"))));
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice");
        BalanceCommand command = new BalanceCommand("w/alice", walletManager);

        String output = runCommand(command, blockchain);

        String normalized = normalizeOutput(output);
        assertTrue(normalized.contains("Wallet Balance"));
        assertTrue(normalized.contains("Wallet : alice"));
        assertTrue(normalized.contains("Balance : 1.23456790"));
    }

    @Test
    void execute_selfTransfer_keepsNetZeroBalance() {
        Blockchain blockchain = new Blockchain(List.of(
            new Block(
                0,
                LocalDateTime.of(2026, 2, 12, 14, 30, 21),
                "0000000000000000",
                List.of("Genesis Block")),
            new Block(
                1,
                LocalDateTime.of(2026, 2, 12, 14, 35, 2),
                "prev-hash",
                List.of("alice -> alice : 5"))));
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice");
        BalanceCommand command = new BalanceCommand("w/alice", walletManager);

        String output = runCommand(command, blockchain);

        String normalized = normalizeOutput(output);
        assertTrue(normalized.contains("Wallet Balance"));
        assertTrue(normalized.contains("Wallet : alice"));
        assertTrue(normalized.contains("Balance : 0.00000000"));
    }

    @Test
    void execute_blankWalletNameAfterPrefix_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        BalanceCommand command = new BalanceCommand("w/   ", walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: wallet name cannot be empty. Use: balance w/WALLET_NAME", exception.getMessage());
    }

    @Test
    void execute_missingWalletPrefix_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        BalanceCommand command = new BalanceCommand("alice", walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: Invalid balance format. Use: balance w/WALLET_NAME", exception.getMessage());
    }

    @Test
    void execute_walletNameWithSpaces_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        BalanceCommand command = new BalanceCommand("w/alice bob", walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: wallet name must be one word without spaces. Use: balance w/WALLET_NAME",
                exception.getMessage());
    }

    @Test
    void execute_nonExistentWallet_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        BalanceCommand command = new BalanceCommand("w/ghost", walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: Wallet not found.", exception.getMessage());
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
