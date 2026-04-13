package seedu.crypto1010.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WalletStorageTest {
    @TempDir
    Path tempDir;

    private Path dataDir;
    private Path walletFile;

    @BeforeEach
    void setUp() {
        System.setProperty("crypto1010.dataDir", tempDir.toString());
        dataDir = tempDir;
        walletFile = dataDir.resolve("wallets.txt");
    }

    @AfterEach
    void tearDown() {
        System.clearProperty("crypto1010.dataDir");
    }

    @Test
    void saveThenLoad_persistsWalletsAndHistory() throws IOException {
        WalletManager manager = new WalletManager();
        try {
            Wallet alice = manager.createWallet("alice");
            Wallet bob = manager.createWallet("bob");
            alice.addTransaction("to/0xabc amt/1 speed/standard fee/0.001");
            bob.addTransaction("to/0xdef amt/2 speed/manual fee/0.1 note/rent");
        } catch (Crypto1010Exception e) {
            throw new IOException(e.getMessage());
        }

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
    void saveThenLoad_persistsWalletCurrency() throws IOException {
        WalletManager manager = new WalletManager();
        try {
            manager.createWallet("alice", "btc");
        } catch (Crypto1010Exception e) {
            throw new RuntimeException(e);
        }

        WalletStorage storage = new WalletStorage(WalletStorageTest.class);
        storage.save(manager);
        WalletManager loaded = storage.load();

        assertEquals(1, loaded.getWallets().size());
        assertEquals("btc", loaded.getWallets().get(0).getCurrencyCode());
    }

    @Test
    void load_missingFile_returnsEmptyWalletManager() throws IOException {
        WalletStorage storage = new WalletStorage(WalletStorageTest.class);
        Files.deleteIfExists(walletFile);

        WalletManager loaded = storage.load();

        assertTrue(loaded.getWallets().isEmpty());
    }

    @Test
    void load_duplicateWalletEntries_throwsIOException() throws IOException {
        Files.createDirectories(dataDir);
        Files.writeString(walletFile, """
                W|alice
                E
                W|alice
                E
                """, StandardCharsets.UTF_8);
        WalletStorage storage = new WalletStorage(WalletStorageTest.class);

        IOException exception = assertThrows(IOException.class, storage::load);
        assertTrue(exception.getMessage().startsWith("Invalid wallet data: wallet already exists: alice"));
    }
}
