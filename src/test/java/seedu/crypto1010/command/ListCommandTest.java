package seedu.crypto1010.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import seedu.crypto1010.exceptions.Exceptions;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.Key;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;

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
    void execute_existingWallets_printsWalletNames() throws Exceptions {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice");
        walletManager.createWallet("bob");
        ListCommand command = new ListCommand(walletManager);

        String output = runCommand(command, blockchain);

        String expected = String.join(System.lineSeparator(),
                "Wallets:",
                "1. alice | Address: Generate keys first",
                "2. bob | Address: Generate keys first") + System.lineSeparator();
        assertEquals(expected, output);
    }

    @Test
    void execute_walletWithGeneratedKeys_printsAddress() throws Exceptions {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        Wallet alice = walletManager.createWallet("alice");
        alice.setKeys(new Key[]{
            new Key(BigInteger.valueOf(3), BigInteger.valueOf(7), true),
            new Key(BigInteger.valueOf(3), BigInteger.valueOf(11), false)});
        ListCommand command = new ListCommand(walletManager);

        String output = runCommand(command, blockchain);

        String expected = String.join(System.lineSeparator(),
                "Wallets:",
                "1. alice | Address: " + alice.getAddress()) + System.lineSeparator();
        assertEquals(expected, output);
    }

    @Test
    void execute_withUnexpectedArguments_throwsFormatError() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        ListCommand command = new ListCommand(walletManager);

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute("extra", blockchain));
        assertEquals("Error: Invalid list format. Use: list", exception.getMessage());
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
