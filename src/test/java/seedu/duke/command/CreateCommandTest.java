package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Blockchain;
import seedu.duke.model.WalletManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class CreateCommandTest {
    @Test
    void execute_validName_createsWallet() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("n/alice", walletManager);

        String output = runCommand(command, blockchain);

        assertEquals("Wallet created: alice" + System.lineSeparator(), output);
        assertEquals(1, walletManager.getWallets().size());
        assertEquals("alice", walletManager.getWallets().get(0).getName());
    }

    @Test
    void execute_blankName_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("n/   ", walletManager);

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertEquals("Error: wallet name cannot be empty.", exception.getMessage());
        assertEquals(0, walletManager.getWallets().size());
    }

    @Test
    void execute_duplicateName_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice");
        CreateCommand command = new CreateCommand("n/alice", walletManager);

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertEquals("Error: wallet name already exists.", exception.getMessage());
        assertEquals(1, walletManager.getWallets().size());
    }

    @Test
    void execute_missingNamePrefix_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        CreateCommand command = new CreateCommand("alice", walletManager);

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertEquals("Error: Invalid create format. Use: create n/WALLET_NAME", exception.getMessage());
        assertEquals(0, walletManager.getWallets().size());
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
