package seedu.crypto1010.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.WalletManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AccountScopedStorageTest {
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        System.setProperty("crypto1010.dataDir", tempDir.toString());
    }

    @AfterEach
    void tearDown() {
        System.clearProperty("crypto1010.dataDir");
    }

    @Test
    void walletStorage_accountScoped_keepsWalletsSeparated() throws IOException {
        WalletStorage aliceStorage = new WalletStorage(AccountScopedStorageTest.class, "alice");
        WalletStorage bobStorage = new WalletStorage(AccountScopedStorageTest.class, "bob");

        WalletManager aliceManager = new WalletManager();
        aliceManager.createWallet("alice-wallet");
        WalletManager bobManager = new WalletManager();
        bobManager.createWallet("bob-wallet");

        aliceStorage.save(aliceManager);
        bobStorage.save(bobManager);

        assertEquals("alice-wallet", aliceStorage.load().getWallets().get(0).getName());
        assertEquals("bob-wallet", bobStorage.load().getWallets().get(0).getName());
    }

    @Test
    void blockchainStorage_accountScoped_keepsChainsSeparated() throws IOException {
        BlockchainStorage aliceStorage = new BlockchainStorage(AccountScopedStorageTest.class, "alice");
        BlockchainStorage bobStorage = new BlockchainStorage(AccountScopedStorageTest.class, "bob");

        Blockchain aliceChain = Blockchain.createDefault();
        aliceChain.addTransactions(List.of("network -> alicewallet : 1"));
        Blockchain bobChain = Blockchain.createDefault();

        aliceStorage.save(aliceChain);
        bobStorage.save(bobChain);

        Blockchain loadedAliceChain = aliceStorage.load();
        Blockchain loadedBobChain = bobStorage.load();

        assertEquals(3, loadedAliceChain.size());
        assertEquals(2, loadedBobChain.size());
        assertTrue(loadedAliceChain.validate().isValid());
        assertTrue(loadedBobChain.validate().isValid());
    }
}
