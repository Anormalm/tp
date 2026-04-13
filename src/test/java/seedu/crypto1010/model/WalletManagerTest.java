package seedu.crypto1010.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class WalletManagerTest {
    @Test
    void createWallet_duplicateName_throwsIllegalArgumentException() {
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice");

        assertThrows(IllegalArgumentException.class, () -> walletManager.createWallet("Alice"));
    }

    @Test
    void createWallet_duplicateSpecificCurrency_throwsIllegalArgumentException() {
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice", "btc");

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> walletManager.createWallet("bob", "btc"));
        assertEquals("wallet currency already exists: btc", exception.getMessage());
    }

    @Test
    void createWallet_nameContainsReservedDelimiter_throwsIllegalArgumentException() {
        WalletManager walletManager = new WalletManager();

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> walletManager.createWallet("ali|ce"));
        assertEquals("walletName contains reserved character: |", exception.getMessage());
    }

    @Test
    void createWallet_nameTooLong_throwsIllegalArgumentException() {
        WalletManager walletManager = new WalletManager();

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> walletManager.createWallet("abcdefghijklmnopqrstuvwxyz1234567"));
        assertEquals("walletName exceeds max length: 32", exception.getMessage());
    }
}
