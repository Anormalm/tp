package seedu.crypto1010.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import seedu.crypto1010.exceptions.Crypto1010Exception;

import org.junit.jupiter.api.Test;

class WalletTest {

    @Test
    void constructor_setsNameAndGenericCurrency() {
        Wallet wallet = new Wallet("alice");

        assertEquals("alice", wallet.getName());
        assertEquals(CurrencyCode.GENERIC, wallet.getCurrencyCode());
    }

    @Test
    void constructor_withCurrency_setsCurrencyCode() {
        Wallet wallet = new Wallet("alice", "eth");

        assertEquals("eth", wallet.getCurrencyCode());
    }

    @Test
    void getAddress_noKeyPair_throwsException() {
        Wallet wallet = new Wallet("alice");

        assertThrows(Crypto1010Exception.class, wallet::getAddress);
    }

    @Test
    void hasKeyPair_noKeyPair_returnsFalse() {
        Wallet wallet = new Wallet("alice");

        assertFalse(wallet.hasKeyPair());
    }

    @Test
    void setKeys_validKeyPair_setsAddressAndKeyPair() throws Crypto1010Exception {
        Wallet wallet = new Wallet("alice");
        KeyPair keyPair = KeyPair.generate("eth");

        wallet.setKeys(keyPair);

        assertTrue(wallet.hasKeyPair());
        assertEquals(keyPair.getWalletAddress(), wallet.getAddress());
    }

    @Test
    void setKeys_calledTwice_throwsException() throws Crypto1010Exception {
        Wallet wallet = new Wallet("alice");
        KeyPair keyPair = KeyPair.generate("eth");
        wallet.setKeys(keyPair);

        assertThrows(Crypto1010Exception.class, () -> wallet.setKeys(KeyPair.generate("eth")));
    }

    @Test
    void addTransaction_addsToHistory() {
        Wallet wallet = new Wallet("alice");

        wallet.addTransaction("to/0x123 amt/1 fee/0");

        assertEquals(1, wallet.getTransactionHistory().size());
        assertEquals("to/0x123 amt/1 fee/0", wallet.getTransactionHistory().get(0));
    }

    @Test
    void restoreKeyPair_setsKeyPairAndAddress() throws Crypto1010Exception {
        Wallet wallet = new Wallet("alice");
        KeyPair keyPair = KeyPair.generate("eth");

        wallet.restoreKeyPair(keyPair);

        assertTrue(wallet.hasKeyPair());
        assertEquals(keyPair.getWalletAddress(), wallet.getAddress());
    }

    @Test
    void restoreKeyPair_afterSetKeys_overwritesKeyPair() throws Crypto1010Exception {
        Wallet wallet = new Wallet("alice");
        KeyPair first = KeyPair.generate("eth");
        wallet.setKeys(first);
        KeyPair second = KeyPair.generate("eth");

        // restoreKeyPair bypasses the lock - simulates storage restoration
        wallet.restoreKeyPair(second);

        assertEquals(second.getWalletAddress(), wallet.getKeyPair().getWalletAddress());
    }
}
