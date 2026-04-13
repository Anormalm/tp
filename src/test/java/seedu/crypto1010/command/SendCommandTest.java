package seedu.crypto1010.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.WalletManager;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.KeyPair;

import java.util.List;
import java.math.BigDecimal;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class SendCommandTest {
    private static final String ETH_ADDRESS = "0x1111111111111111111111111111111111111111";
    private static final String BTC_ADDRESS = "1A1zP1eP5QGefi2DMPTfTL5SLmv7Divfna";

    private String normalizeOutput(String s) {
        return s.replaceAll("\r\n", "\n").replaceAll("[ \t]+$", "").trim();
    }

    private String runCommand(Command command, Blockchain blockchain) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            command.execute(blockchain);
        } catch (Crypto1010Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.setOut(original);
        }
        return out.toString();
    }

    @Test
    void execute_walletNotFound_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        SendCommand command = new SendCommand("w/nonexistent to/" + ETH_ADDRESS + " amt/1", walletManager);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain));
        assertEquals("Error: Wallet not found.", exception.getMessage());
    }

    @Test
    void execute_walletHasNoKeyPair_succeeds() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("bob");
        blockchain.addTransactions(List.of("network -> bob : 5"));
        SendCommand command = new SendCommand("w/bob to/" + ETH_ADDRESS + " amt/1", walletManager);

        String output = normalizeOutput(runCommand(command, blockchain));
        assertTrue(output.contains("Transaction Sent Successfully"));
    }

    @Test
    void execute_invalidFormat_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        SendCommand command = new SendCommand("invalid", walletManager);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain));
        assertTrue(exception.getMessage().startsWith("Error: Invalid send format."));
    }

    @Test
    void execute_invalidAddress_throwsException() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("bob");
        blockchain.addTransactions(List.of("network -> bob : 5"));
        SendCommand command = new SendCommand(
                "w/bob to/" + ETH_ADDRESS + " amt/4 speed/fast fee/0.5 note/priority transfer",
                walletManager);

        String output = runCommand(command, blockchain);

        String normalized = normalizeOutput(output);
        assertTrue(normalized.contains("Transaction Sent Successfully"));
        assertTrue(normalized.contains("Wallet : bob"));
        assertTrue(normalized.contains("To : " + ETH_ADDRESS));
        assertTrue(normalized.contains("Amount : 4"));
        assertTrue(normalized.contains("Speed : manual"));
        assertTrue(normalized.contains("Fee : 0.5"));
        assertTrue(normalized.contains("Note : priority transfer"));
        assertEquals(new BigDecimal("0.5"), blockchain.getPreciseBalance("network-fee"));
        Wallet wallet = walletManager.findWallet("bob").orElse(null);
        assertNotNull(wallet);
        assertTrue(wallet.getTransactionHistory().get(0).contains("note/priority transfer"));
    }

    @Test
    void execute_manualFeeOverrideWithUnsupportedSpeed_succeeds() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("bob");
        blockchain.addTransactions(List.of("network -> bob : 5"));
        SendCommand command = new SendCommand(
                "w/bob to/" + ETH_ADDRESS + " amt/1 speed/ultra fee/0.1",
                walletManager);

        String output = runCommand(command, blockchain);

        String normalized = normalizeOutput(output);
        assertTrue(normalized.contains("Transaction Sent Successfully"));
        assertTrue(normalized.contains("Wallet : bob"));
        assertTrue(normalized.contains("To : " + ETH_ADDRESS));
        assertTrue(normalized.contains("Amount : 1"));
        assertTrue(normalized.contains("Speed : manual"));
        assertTrue(normalized.contains("Fee : 0.1"));
        Wallet wallet = walletManager.findWallet("bob").orElse(null);
        assertNotNull(wallet);
        assertTrue(wallet.getTransactionHistory().get(0).contains("speed/manual"));
    }

    @Test
    void execute_noteContainingPrefixLikeText_preservesEntireNote() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("bob");
        blockchain.addTransactions(List.of("network -> bob : 5"));
        SendCommand command = new SendCommand(
                "w/bob to/" + ETH_ADDRESS + " amt/1 fee/0 note/repay w/alice tomorrow",
                walletManager);

        String output = runCommand(command, blockchain);

        String normalized = normalizeOutput(output);
        assertTrue(normalized.contains("Transaction Sent Successfully"));
        assertTrue(normalized.contains("Wallet : bob"));
        assertTrue(normalized.contains("To : " + ETH_ADDRESS));
        assertTrue(normalized.contains("Amount : 1"));
        assertTrue(normalized.contains("Speed : manual"));
        assertTrue(normalized.contains("Fee : 0"));
        assertTrue(normalized.contains("Note : repay w/alice tomorrow"));
        Wallet wallet = walletManager.findWallet("bob").orElse(null);
        assertNotNull(wallet);
        assertTrue(wallet.getTransactionHistory().get(0).contains("note/repay w/alice tomorrow"));
    }

    @Test
    void execute_validBitcoinAddress_succeeds() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("bob");
        blockchain.addTransactions(List.of("network -> bob : 5"));
        SendCommand command = new SendCommand("w/bob to/" + BTC_ADDRESS + " amt/1 fee/0", walletManager);

        String output = normalizeOutput(runCommand(command, blockchain));
        assertTrue(output.contains("Transaction Sent Successfully"));
        assertTrue(output.contains("To : " + BTC_ADDRESS));
    }

    @Test
    void execute_insufficientBalance_throwsException() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        Wallet wallet = walletManager.createWallet("alice");
        wallet.setKeys(KeyPair.generate(wallet.getCurrencyCode()));
        blockchain.addTransactions(List.of("network -> alice : 0.5"));
        SendCommand command = new SendCommand("w/alice to/" + ETH_ADDRESS + " amt/1", walletManager);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain));
        assertEquals("Error: Insufficient balance.", exception.getMessage());
        assertTrue(wallet.getTransactionHistory().isEmpty());
    }

    @Test
    void execute_unsupportedSpeed_throwsException() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("bob");
        SendCommand command = new SendCommand(
                "w/bob to/" + ETH_ADDRESS + " amt/1 speed/urgent", walletManager);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain));
        assertTrue(exception.getMessage().contains("Unsupported speed"));
    }

    @Test
    void execute_negativeManualFee_throwsException() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("bob");
        SendCommand command = new SendCommand(
                "w/bob to/" + ETH_ADDRESS + " amt/1 fee/-0.1", walletManager);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain));
        assertTrue(exception.getMessage().contains("Fee must be a non-negative number"));
    }

    @Test
    void execute_validEthSend_succeeds() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        Wallet wallet = walletManager.createWallet("bob");
        wallet.setKeys(KeyPair.generate(wallet.getCurrencyCode()));
        blockchain.addTransactions(List.of("network -> bob : 10"));
        SendCommand command = new SendCommand("w/bob to/" + ETH_ADDRESS + " amt/1", walletManager);

        String output = normalizeOutput(runCommand(command, blockchain));

        assertTrue(output.contains("Transaction Sent Successfully"));
        assertTrue(output.contains("To : " + ETH_ADDRESS));
        assertTrue(output.contains("Amount : 1"));
        assertEquals(new BigDecimal("8.999"), blockchain.getPreciseBalance("bob"));
    }

    @Test
    void execute_validBtcSend_succeeds() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        Wallet wallet = walletManager.createWallet("bob", "btc");
        wallet.setKeys(KeyPair.generate(wallet.getCurrencyCode()));
        blockchain.addTransactions(List.of("network -> bob : 10"));
        SendCommand command = new SendCommand(
                "w/bob to/" + BTC_ADDRESS + " amt/1 fee/0", walletManager);

        String output = normalizeOutput(runCommand(command, blockchain));

        assertTrue(output.contains("Transaction Sent Successfully"));
        assertTrue(output.contains("To : " + BTC_ADDRESS));
    }
}
