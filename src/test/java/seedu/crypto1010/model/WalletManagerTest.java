package seedu.crypto1010.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.crypto1010.exceptions.Crypto1010Exception;

class WalletManagerTest {
    @Test
    void createWallet_duplicateName_throwsCrypto1010Exception() throws Crypto1010Exception {
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice");

        Crypto1010Exception exception =
                assertThrows(Crypto1010Exception.class, () -> walletManager.createWallet("Alice"));
        assertTrue(exception.getMessage().contains("wallet already exists"));
    }

    @Test
    void createWallet_duplicateSpecificCurrency_throwsCrypto1010Exception() throws Crypto1010Exception {
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice", "btc");

        Crypto1010Exception exception =
                assertThrows(Crypto1010Exception.class, () -> walletManager.createWallet("bob", "btc"));
        assertTrue(exception.getMessage().contains("currency already exists"));
    }

    @Test
    void createWallet_nameContainsReservedDelimiter_throwsCrypto1010Exception() {
        WalletManager walletManager = new WalletManager();

        Crypto1010Exception exception =
                assertThrows(Crypto1010Exception.class, () -> walletManager.createWallet("ali|ce"));
        assertTrue(exception.getMessage().contains("reserved character"));
    }

    @Test
    void createWallet_nameTooLong_throwsCrypto1010Exception() {
        WalletManager walletManager = new WalletManager();

        Crypto1010Exception exception =
                assertThrows(Crypto1010Exception.class,
                             () -> walletManager.createWallet("abcdefghijklmnopqrstuvwxyz1234567"));
        assertEquals("walletName exceeds max length: 32", exception.getMessage());
    }

    @Test
    void createWallet_reservedName_throwsCrypto1010Exception() {
        WalletManager walletManager = new WalletManager();

        Crypto1010Exception exception =
                assertThrows(Crypto1010Exception.class, () -> walletManager.createWallet("network"));
        assertTrue(exception.getMessage().contains("reserved"));
    }

    @Test
    void createWallet_blankName_throwsCrypto1010Exception() {
        WalletManager walletManager = new WalletManager();

        Crypto1010Exception exception =
                assertThrows(Crypto1010Exception.class, () -> walletManager.createWallet("   "));
        assertTrue(exception.getMessage().contains("must not be blank"));
    }

    @Test
    void createWallet_validName_createsWallet() throws Crypto1010Exception {
        WalletManager walletManager = new WalletManager();

        Wallet wallet = walletManager.createWallet("alice");

        assertEquals("alice", wallet.getName());
        assertEquals(CurrencyCode.GENERIC, wallet.getCurrencyCode());
        assertEquals(1, walletManager.getWallets().size());
    }

    @Test
    void createWallet_validNameWithCurrency_createsWalletWithCurrency() throws Crypto1010Exception {
        WalletManager walletManager = new WalletManager();

        Wallet wallet = walletManager.createWallet("alice", "eth");

        assertEquals("eth", wallet.getCurrencyCode());
    }
}
