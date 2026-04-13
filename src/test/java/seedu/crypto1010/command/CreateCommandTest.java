package seedu.crypto1010.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.WalletManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class CreateCommandTest {
    private String normalizeOutput(String s) {
        return s.replaceAll("\r\n", "\n").replaceAll("[ \t]+$", "").trim();
    }

    @Test
    void execute_validName_createsWallet() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("w/alice", walletManager);

        String output = runCommand(command, blockchain);

        String normalized = normalizeOutput(output);
        assertTrue(normalized.contains("Wallet Created"));
        assertTrue(normalized.contains("Wallet : alice"));
        assertEquals(1, walletManager.getWallets().size());
        assertEquals("alice", walletManager.getWallets().get(0).getName());
    }

    @Test
    void execute_validNameWithCurrency_createsWalletWithCurrency() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("w/alice curr/btc", walletManager);

        String output = runCommand(command, blockchain);

        String normalized = normalizeOutput(output);
        assertTrue(normalized.contains("Wallet Created"));
        assertTrue(normalized.contains("Wallet : alice"));
        assertTrue(normalized.contains("Currency : btc"));
        assertEquals("btc", walletManager.getWallets().get(0).getCurrencyCode());
    }

    @Test
    void execute_blankName_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("w/   ", walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: wallet name cannot be empty. Use: create w/WALLET_NAME [curr/CURRENCY]",
                exception.getMessage());
        assertEquals(0, walletManager.getWallets().size());
    }

    @Test
    void execute_nullArguments_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand(null, walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: wallet name cannot be empty. Use: create w/WALLET_NAME [curr/CURRENCY]",
                exception.getMessage());
        assertEquals(0, walletManager.getWallets().size());
    }

    @Test
    void execute_duplicateName_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice");
        CreateCommand command = new CreateCommand("w/alice", walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: wallet name already exists.", exception.getMessage());
        assertEquals(1, walletManager.getWallets().size());
    }

    @Test
    void execute_missingNamePrefix_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("alice", walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: Invalid create format. Use: create w/WALLET_NAME [curr/CURRENCY]",
                exception.getMessage());
        assertEquals(0, walletManager.getWallets().size());
    }

    @Test
    void execute_nameWithSpaces_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("w/alice bob", walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: wallet name must be one word without spaces. Use: create w/WALLET_NAME [curr/CURRENCY]",
            exception.getMessage());
        assertEquals(0, walletManager.getWallets().size());
    }

    @Test
    void execute_nameWithReservedDelimiter_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("w/alice|btc", walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: wallet name cannot contain '|'. Use: create w/WALLET_NAME [curr/CURRENCY]",
                exception.getMessage());
        assertEquals(0, walletManager.getWallets().size());
    }

    @Test
    void execute_nameTooLong_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("w/abcdefghijklmnopqrstuvwxyz1234567", walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: wallet name must be at most 32 characters. Use: create w/WALLET_NAME [curr/CURRENCY]",
                exception.getMessage());
        assertEquals(0, walletManager.getWallets().size());
    }

    @Test
    void execute_blankArguments_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("", walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: wallet name cannot be empty. Use: create w/WALLET_NAME [curr/CURRENCY]",
                exception.getMessage());
        assertEquals(0, walletManager.getWallets().size());
    }

    @Test
    void execute_duplicateCurrency_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice", "btc");
        CreateCommand command = new CreateCommand("w/bob curr/btc", walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: a wallet for that currency already exists in this account. "
                + "Use: create w/WALLET_NAME [curr/CURRENCY]", exception.getMessage());
    }

    @Test
    void execute_invalidCurrency_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("w/alice curr/b", walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: CURRENCY must be 2-10 alphanumeric characters. "
                + "Use: create w/WALLET_NAME [curr/CURRENCY]", exception.getMessage());
    }

    @Test
    void execute_genericCurrency_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("w/alice curr/generic", walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: CURRENCY must be specific. Omit curr/ to create a generic wallet. "
                + "Use: create w/WALLET_NAME [curr/CURRENCY]", exception.getMessage());
    }

    @Test
    void constructor_nullWalletManager_throwsException() {
        assertThrows(NullPointerException.class, () -> new CreateCommand("w/alice", null));
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
