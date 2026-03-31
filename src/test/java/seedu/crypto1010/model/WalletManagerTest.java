package seedu.crypto1010.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class WalletManagerTest {
    @Test
    void createWallet_duplicateName_throwsIllegalArgumentException() {
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice");

        assertThrows(IllegalArgumentException.class, () -> walletManager.createWallet("Alice"));
    }
}
