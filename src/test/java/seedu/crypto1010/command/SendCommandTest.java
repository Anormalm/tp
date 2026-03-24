package seedu.crypto1010.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.exceptions.Exceptions;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.Key;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class SendCommandTest {
    private static final String ETH_ADDRESS = "0x1111111111111111111111111111111111111111";

    @Test
    void execute_validSendWithDefaultSpeed_recordsTransactionAndHistory() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("bob"); // bob has balance 10 from default blockchain
        SendCommand command = new SendCommand("w/bob to/" + ETH_ADDRESS + " amt/4", walletManager);

        String output = runCommand(command, blockchain);

        String expected = "Transaction sent successfully." + System.lineSeparator()
                + "Wallet: bob" + System.lineSeparator()
                + "To: " + ETH_ADDRESS + System.lineSeparator()
                + "Amount: 4" + System.lineSeparator()
                + "Speed: standard" + System.lineSeparator()
                + "Fee: 0.0010" + System.lineSeparator();
        assertEquals(expected, output);

        assertEquals(3, blockchain.size());
        assertEquals(new BigDecimal("0.999"), blockchain.getPreciseBalance("bob"));
        Wallet wallet = walletManager.findWallet("bob").orElse(null);
        assertNotNull(wallet);
        assertEquals(1, wallet.getTransactionHistory().size());
        assertTrue(wallet.getTransactionHistory().get(0).contains("speed/standard"));
    }

    @Test
    void execute_insufficientBalance_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice"); // alice has balance -10
        SendCommand command = new SendCommand("w/alice to/" + ETH_ADDRESS + " amt/1", walletManager);

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertEquals("Error: Insufficient balance.", exception.getMessage());
    }

    @Test
    void execute_walletNotFound_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        SendCommand command = new SendCommand("w/nonexistent to/" + ETH_ADDRESS + " amt/1", walletManager);

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertEquals("Error: Wallet not found.", exception.getMessage());
    }

    @Test
    void execute_invalidAmount_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("bob");
        SendCommand command = new SendCommand("w/bob to/" + ETH_ADDRESS + " amt/-5", walletManager);

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertEquals("Error: Amount must be a positive number. Use: send w/WALLET_NAME"
            + " to/RECIPIENT_ADDRESS amt/AMOUNT [speed/SPEED] [fee/FEE] [note/MEMO]",
            exception.getMessage());
    }

    @Test
    void execute_invalidFormat_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        SendCommand command = new SendCommand("invalid", walletManager);

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertTrue(exception.getMessage().startsWith("Error: Invalid send format."));
    }

    @Test
    void execute_manualFeeOverride_usesProvidedFee() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("bob");
        SendCommand command = new SendCommand(
                "w/bob to/" + ETH_ADDRESS + " amt/4 speed/fast fee/0.5 note/priority transfer",
                walletManager);

        String output = runCommand(command, blockchain);

        assertTrue(output.contains("Speed: manual"));
        assertTrue(output.contains("Fee: 0.5"));
        assertEquals(new BigDecimal("0.5"), blockchain.getPreciseBalance("network-fee"));
        Wallet wallet = walletManager.findWallet("bob").orElse(null);
        assertNotNull(wallet);
        assertTrue(wallet.getTransactionHistory().get(0).contains("note/priority transfer"));
    }

    @Test
    void execute_invalidAddress_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("bob");
        SendCommand command = new SendCommand("w/bob to/not-an-address amt/1", walletManager);

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertEquals("Error: Invalid recipient address. Use: send w/WALLET_NAME"
            + " to/RECIPIENT_ADDRESS amt/AMOUNT [speed/SPEED] [fee/FEE] [note/MEMO]",
            exception.getMessage());
    }

    @Test
    void execute_unsupportedSpeed_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("bob");
        SendCommand command = new SendCommand("w/bob to/" + ETH_ADDRESS + " amt/1 speed/urgent", walletManager);

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertEquals("Error: Unsupported speed. Use speed/slow, speed/standard, or speed/fast."
            + " Use: send w/WALLET_NAME to/RECIPIENT_ADDRESS amt/AMOUNT [speed/SPEED]"
            + " [fee/FEE] [note/MEMO]", exception.getMessage());
    }

    @Test
    void execute_negativeManualFee_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("bob");
        SendCommand command = new SendCommand("w/bob to/" + ETH_ADDRESS + " amt/1 fee/-0.1", walletManager);

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertEquals("Error: Fee must be a non-negative number. Use: send w/WALLET_NAME"
            + " to/RECIPIENT_ADDRESS amt/AMOUNT [speed/SPEED] [fee/FEE] [note/MEMO]",
            exception.getMessage());
    }

    @Test
    void execute_sendToLocalWalletAddress_creditsReceiverBalance() throws Exceptions {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        Wallet sender = walletManager.createWallet("bob");
        Wallet receiver = walletManager.createWallet("carol");

        Key[] keys = Key.generateKeyPair();
        receiver.setKeys(keys);

        SendCommand command = new SendCommand("w/bob to/" + receiver.getAddress() + " amt/2 fee/0"
                , walletManager);

        command.execute(blockchain);

        assertEquals(new BigDecimal("3"), blockchain.getPreciseBalance("bob"));
        assertEquals(new BigDecimal("7"), blockchain.getPreciseBalance("carol"));
        assertEquals(new BigDecimal("0"), blockchain.getPreciseBalance(receiver.getAddress()));
        assertEquals(1, sender.getTransactionHistory().size());
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


