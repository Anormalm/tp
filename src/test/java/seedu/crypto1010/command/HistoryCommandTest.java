package seedu.crypto1010.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class HistoryCommandTest {
    @Test
    void execute_walletWithoutHistory_printsEmptyMessage() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice");
        HistoryCommand command = new HistoryCommand("w/alice", walletManager);

        String output = runCommand(command, blockchain);

        assertEquals("No transaction history found for alice." + System.lineSeparator(), output);
    }

    @Test
    void execute_walletWithHistory_printsNumberedEntries() throws Crypto1010Exception {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        Wallet wallet = walletManager.createWallet("alice");
        wallet.addTransaction("to/0xabc amt/1 speed/standard fee/0.001");
        wallet.addTransaction("to/0xdef amt/2 speed/manual fee/0.1 note/rent");
        HistoryCommand command = new HistoryCommand("w/alice", walletManager);

        String output = runCommand(command, blockchain);

        String normOutput = output.replaceAll("\r\n", "\n").replaceAll("[ \t]+$", "").trim();
        assertTrue(normOutput.contains("Transaction History for alice"));
        assertTrue(normOutput.contains("No. | Transaction"));
        assertTrue(normOutput.contains("1   | to/0xabc amt/1 speed/standard fee/0.001"));
        assertTrue(normOutput.contains("2   | to/0xdef amt/2 speed/manual fee/0.1 note/rent"));
    }

    @Test
    void execute_walletNotFound_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        HistoryCommand command = new HistoryCommand("w/ghost", walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: Wallet not found.", exception.getMessage());
    }

    @Test
    void execute_invalidFormat_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        HistoryCommand command = new HistoryCommand("alice", walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: Invalid history format. Use: history w/WALLET_NAME", exception.getMessage());
    }

    @Test
    void execute_walletNameWithSpaces_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        WalletManager walletManager = new WalletManager();
        HistoryCommand command = new HistoryCommand("w/alice bob", walletManager);

        Crypto1010Exception exception = assertThrows(Crypto1010Exception.class, () -> command.execute(blockchain));
        assertEquals("Error: wallet name must be one word without spaces. Use: history w/WALLET_NAME",
                exception.getMessage());
    }

    @Test
    void constructor_nullWalletManager_throwsException() {
        assertThrows(NullPointerException.class, () -> new HistoryCommand("w/alice", null));
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
