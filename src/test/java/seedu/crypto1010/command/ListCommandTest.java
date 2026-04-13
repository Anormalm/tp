package seedu.crypto1010.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
// import seedu.crypto1010.model.KeyPair;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
// import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.Test;

class ListCommandTest {
    @Test
    void execute_noWallets_printsEmptyMessage() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        ListCommand command = new ListCommand(walletManager);

        String output = runCommand(command, blockchain);

        assertEquals("No wallets found." + System.lineSeparator(), output);
    }

    @Test
    void execute_existingWallets_printsWalletNames() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice");
        walletManager.createWallet("bob");
        ListCommand command = new ListCommand(walletManager);

        String output = runCommand(command, blockchain);

        String normOutput = output.replaceAll("\r\n", "\n").replaceAll("[ \t]+$", "").trim();
        assertTrue(normOutput.contains("Wallets"));
        assertTrue(normOutput.contains("No. | Wallet Name | Currency | Address"));
        assertTrue(normOutput.contains("1   | alice"));
        assertTrue(normOutput.contains("2   | bob"));
        assertTrue(normOutput.contains("Generate keys first"));
    }

    //    @Test
    //    void execute_walletWithGeneratedKeys_printsAddress() throws Crypto1010Exception {
    //        Blockchain blockchain = Blockchain.createDefault();
    //        WalletManager walletManager = new WalletManager();
    //        Wallet alice = walletManager.createWallet("alice");
    //        alice.setKeys(new Key[]{
    //            new Key(BigInteger.valueOf(3), BigInteger.valueOf(7), true),
    //            new Key(BigInteger.valueOf(3), BigInteger.valueOf(11), false)});
    //        ListCommand command = new ListCommand(walletManager);
    //
    //        String output = runCommand(command, blockchain);
    //
    //        String normOutput = output.replaceAll("\r\n", "\n").replaceAll("[ \t]+$", "").trim();
    //        assertTrue(normOutput.contains("Wallets"));
    //        assertTrue(normOutput.contains("1   | alice"));
    //        assertTrue(normOutput.contains(alice.getAddress()));
    //    }

    @Test
    void execute_walletWithSpecificCurrency_printsCurrency() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice", "btc");
        ListCommand command = new ListCommand(walletManager);

        String output = runCommand(command, blockchain);

        String normOutput = output.replaceAll("\r\n", "\n").replaceAll("[ \t]+$", "").trim();
        assertTrue(normOutput.contains("Wallets"));
        assertTrue(normOutput.contains("1   | alice"));
        assertTrue(normOutput.contains("btc"));
    }

    @Test
    void execute_corruptWalletData_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        injectCorruptWalletEntry(walletManager);
        ListCommand command = new ListCommand(walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));

        assertEquals("Error: Wallet data is corrupted.", exception.getMessage());
    }

    @Test
    void constructor_nullWalletManager_throwsException() {
        assertThrows(NullPointerException.class, () -> new ListCommand(null));
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

    @SuppressWarnings("unchecked")
    private void injectCorruptWalletEntry(WalletManager walletManager) {
        try {
            Field walletsField = WalletManager.class.getDeclaredField("wallets");
            walletsField.setAccessible(true);
            List<Wallet> wallets = (List<Wallet>) walletsField.get(walletManager);
            wallets.add(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to inject corrupt wallet entry for test setup", e);
        }
    }
}
