package seedu.crypto1010.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class WalletStorageTest {
    private static final Path DATA_DIR = Path.of(System.getProperty("user.dir"), "data");
    private static final Path WALLET_FILE = DATA_DIR.resolve("wallets.txt");

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(WALLET_FILE);
    }

    @Test
    void saveThenLoad_persistsWalletsAndHistory() throws IOException {
        WalletManager manager = new WalletManager();
        Wallet alice = manager.createWallet("alice");
        Wallet bob = manager.createWallet("bob");
        alice.addTransaction("to/0xabc amt/1 speed/standard fee/0.001");
        bob.addTransaction("to/0xdef amt/2 speed/manual fee/0.1 note/rent");

        WalletStorage storage = new WalletStorage(WalletStorageTest.class);
        storage.save(manager);
        WalletManager loaded = storage.load();

        assertEquals(2, loaded.getWallets().size());
        assertEquals("alice", loaded.getWallets().get(0).getName());
        assertEquals("bob", loaded.getWallets().get(1).getName());
        assertEquals(1, loaded.getWallets().get(0).getTransactionHistory().size());
        assertEquals(1, loaded.getWallets().get(1).getTransactionHistory().size());
        assertEquals("to/0xabc amt/1 speed/standard fee/0.001",
                loaded.getWallets().get(0).getTransactionHistory().get(0));
        assertEquals("to/0xdef amt/2 speed/manual fee/0.1 note/rent",
                loaded.getWallets().get(1).getTransactionHistory().get(0));
    }

    @Test
    void load_missingFile_returnsEmptyWalletManager() throws IOException {
        WalletStorage storage = new WalletStorage(WalletStorageTest.class);
        Files.deleteIfExists(WALLET_FILE);

        WalletManager loaded = storage.load();

        assertTrue(loaded.getWallets().isEmpty());
    }
}
