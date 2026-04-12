package seedu.crypto1010.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import seedu.crypto1010.exceptions.Crypto1010Exception;

public class Wallet {
    private static final String NO_ADDRESS_ERROR = "Error: Generate keys first";
    private static final String INVALID_KEYS_ERROR = "Error: Keys must contain public and private keys";

    private final String name;
    private final String currencyCode;
    private final List<String> transactionHistory;
    private String address;
    private KeyPair keyPair;

    public Wallet(String name) {
        this(name, CurrencyCode.GENERIC);
    }

    public Wallet(String name, String currencyCode) {
        this.name = Objects.requireNonNull(name).trim();
        this.currencyCode = CurrencyCode.normalizeOrDefault(currencyCode);
        this.address = null; //set after keygen
        this.transactionHistory = new ArrayList<>();
        assert !this.name.isBlank() : "wallet name must not be blank";
        assert this.currencyCode != null : "currency code must not be null";
    }

    public String getName() {
        return name;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getAddress() throws Crypto1010Exception {
        if (address == null) {
            throw new Crypto1010Exception(NO_ADDRESS_ERROR);
        }
        return address;
    }

    public boolean hasKeyPair() {
        return keyPair != null;
    }

    public void addTransaction(String transactionEntry) {
        transactionHistory.add(Objects.requireNonNull(transactionEntry).trim());
        assert !transactionHistory.get(transactionHistory.size() - 1).isBlank()
                : "transaction entry must not be blank";
    }

    public List<String> getTransactionHistory() {
        assert transactionHistory != null : "transaction history must be initialized";
        return Collections.unmodifiableList(transactionHistory);
    }

    public void setKeys(KeyPair keys) {
        String generatedAddress = keys.getWalletAddress();
        if (generatedAddress == null || generatedAddress.isBlank()) {
            throw new IllegalArgumentException(INVALID_KEYS_ERROR);
        }
        this.keyPair = keys;
        this.address = generatedAddress;
    }
}
