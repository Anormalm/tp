package seedu.crypto1010.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.crypto1010.exceptions.Crypto1010Exception;

class WalletManagerTest {
    @Test
    void createWallet_duplicateName_throwsCrypto1010Exception() throws Crypto1010Exception {
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice");

        assertThrows(Crypto1010Exception.class, () -> walletManager.createWallet("Alice"));
    }

    @Test
    void createWallet_duplicateSpecificCurrency_throwsCrypto1010Exception() throws Crypto1010Exception {
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice", "btc");

        Crypto1010Exception exception =
                assertThrows(Crypto1010Exception.class, () -> walletManager.createWallet("bob", "btc"));
        assertEquals("Error: a wallet for that currency already exists in this account."
                + "Use: create w/WALLET_NAME [curr/CURRENCY]", exception.getMessage());
    }

    @Test
    void createWallet_nameContainsReservedDelimiter_throwsCrypto1010Exception() {
        WalletManager walletManager = new WalletManager();

        Crypto1010Exception exception =
                assertThrows(Crypto1010Exception.class, () -> walletManager.createWallet("ali|ce"));
        assertEquals("walletName contains reserved character: |", exception.getMessage());
    }

    @Test
    void createWallet_nameTooLong_throwsCrypto1010Exception() {
        WalletManager walletManager = new WalletManager();

        Crypto1010Exception exception =
                assertThrows(Crypto1010Exception.class,
                        () -> walletManager.createWallet("abcdefghijklmnopqrstuvwxyz1234567"));
        assertEquals("walletName exceeds max length: 32", exception.getMessage());
    }
}
