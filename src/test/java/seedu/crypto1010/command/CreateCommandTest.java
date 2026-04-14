package seedu.crypto1010.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.WalletManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

class CreateCommandTest {
    private static final Scanner DUMMY_SCANNER = new Scanner(System.in);

    private String normalizeOutput(String s) {
        return s.replaceAll("\r\n", "\n").replaceAll("[ \t]+$", "").trim();
    }

    private String runCommand(Command command, Blockchain blockchain) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        try {
            command.execute(blockchain, DUMMY_SCANNER);
        } catch (Crypto1010Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.setOut(originalOut);
        }
        return outputStream.toString();
    }

    @Test
    void execute_validName_createsWallet() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("w/alice", walletManager);

        String output = normalizeOutput(runCommand(command, blockchain));

        assertTrue(output.contains("Wallet Created"));
        assertTrue(output.contains("Wallet : alice"));
        assertEquals(1, walletManager.getWallets().size());
        assertEquals("alice", walletManager.getWallets().get(0).getName());
    }

    @Test
    void execute_validNameWithCurrency_createsWalletWithCurrency() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("w/alice curr/btc", walletManager);

        String output = normalizeOutput(runCommand(command, blockchain));

        assertTrue(output.contains("Wallet Created"));
        assertTrue(output.contains("Wallet : alice"));
        assertTrue(output.contains("Currency : btc"));
        assertEquals("btc", walletManager.getWallets().get(0).getCurrencyCode());
    }

    @Test
    void execute_duplicateName_throwsException() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice");
        CreateCommand command = new CreateCommand("w/alice", walletManager);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain, DUMMY_SCANNER));
        assertTrue(exception.getMessage().contains("already exists"));
        assertEquals(1, walletManager.getWallets().size());
    }

    @Test
    void execute_reservedName_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("w/network", walletManager);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain, DUMMY_SCANNER));
        assertTrue(exception.getMessage().contains("reserved"));
        assertEquals(0, walletManager.getWallets().size());
    }

    @Test
    void execute_blankName_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("w/   ", walletManager);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain, DUMMY_SCANNER));
        assertTrue(exception.getMessage().contains("cannot be empty"));
        assertEquals(0, walletManager.getWallets().size());
    }

    @Test
    void execute_nullArguments_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand(null, walletManager);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain, DUMMY_SCANNER));
        assertTrue(exception.getMessage().contains("cannot be empty"));
        assertEquals(0, walletManager.getWallets().size());
    }

    @Test
    void execute_blankArguments_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("", walletManager);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain, DUMMY_SCANNER));
        assertTrue(exception.getMessage().contains("cannot be empty"));
        assertEquals(0, walletManager.getWallets().size());
    }

    @Test
    void execute_missingNamePrefix_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("alice", walletManager);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain, DUMMY_SCANNER));
        assertTrue(exception.getMessage().contains("Invalid create format"));
        assertEquals(0, walletManager.getWallets().size());
    }

    @Test
    void execute_nameWithSpaces_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("w/alice bob", walletManager);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain, DUMMY_SCANNER));
        assertTrue(exception.getMessage().contains("one word without spaces"));
        assertEquals(0, walletManager.getWallets().size());
    }

    @Test
    void execute_nameWithReservedDelimiter_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("w/alice|btc", walletManager);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain, DUMMY_SCANNER));
        assertTrue(exception.getMessage().contains("'|'"));
        assertEquals(0, walletManager.getWallets().size());
    }

    @Test
    void execute_duplicateCurrency_throwsException() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice", "btc");
        CreateCommand command = new CreateCommand("w/bob curr/btc", walletManager);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain, DUMMY_SCANNER));
        assertTrue(exception.getMessage().contains("currency already exists"));
    }

    @Test
    void execute_invalidCurrency_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("w/alice curr/sol", walletManager);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain, DUMMY_SCANNER));
        assertTrue(exception.getMessage().contains("CURRENCY must be"));
        assertEquals(0, walletManager.getWallets().size());
    }

    @Test
    void execute_genericCurrency_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("w/alice curr/generic", walletManager);

        Crypto1010Exception exception = assertThrows(
                Crypto1010Exception.class,
                () -> command.execute(blockchain, DUMMY_SCANNER));
        assertTrue(exception.getMessage().contains("Omit curr/"));
        assertEquals(0, walletManager.getWallets().size());
    }

    @Test
    void constructor_nullWalletManager_throwsException() {
        assertThrows(NullPointerException.class, () -> new CreateCommand("w/alice", null));
    }
}
