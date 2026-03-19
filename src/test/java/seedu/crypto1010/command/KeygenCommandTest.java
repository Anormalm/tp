package seedu.crypto1010.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import seedu.crypto1010.exceptions.Exceptions;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.WalletManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class KeygenCommandTest {
    @Test
    void execute_caseInsensitiveWalletName_printsSuccess() throws Exceptions {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("bob");
        KeygenCommand command = new KeygenCommand("w/BOB", walletManager);

        String output = runCommand(command, blockchain);

        assertTrue(output.contains("Key pair successfully generated"));
    }

    @Test
    void execute_invalidFormat_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        KeygenCommand command = new KeygenCommand("bob", walletManager);

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertEquals("Error: Invalid keygen format. Use: keygen w/WALLET_NAME", exception.getMessage());
    }

    @Test
    void execute_emptyWalletName_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        KeygenCommand command = new KeygenCommand("w/", walletManager);

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertEquals("Error: wallet name cannot be empty.", exception.getMessage());
    }

    private String runCommand(Command command, Blockchain blockchain) throws Exceptions {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        try {
            command.execute(blockchain);
        } finally {
            System.setOut(originalOut);
        }
        return outputStream.toString();
    }
}
