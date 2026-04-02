package seedu.crypto1010.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class WalletTest {

    @Test
    void setKeys_nullArray_throwsIllegalArgumentException() {
        Wallet wallet = new Wallet("alice");

        assertThrows(IllegalArgumentException.class, () -> wallet.setKeys(null));
    }

    @Test
    void setKeys_arrayTooShort_throwsIllegalArgumentException() {
        Wallet wallet = new Wallet("alice");

        assertThrows(IllegalArgumentException.class, () -> wallet.setKeys(new Key[] {Key.generateKeyPair()[0]}));
    }

    @Test
    void setKeys_containsNullEntries_throwsIllegalArgumentException() {
        Wallet wallet = new Wallet("alice");

        assertThrows(IllegalArgumentException.class, () -> wallet.setKeys(new Key[] {null, Key.generateKeyPair()[1]}));
        assertThrows(IllegalArgumentException.class, () -> wallet.setKeys(new Key[] {Key.generateKeyPair()[0], null}));
    }
}
