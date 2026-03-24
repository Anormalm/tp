package seedu.crypto1010.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import seedu.crypto1010.exceptions.Exceptions;

public class Wallet {
    private static final String NO_ADDRESS_ERROR = "Generate keys first";

    private final String name;
    private final List<String> transactionHistory;
    private String address;
    private Key publicKey;
    private Key privateKey;

    public Wallet(String name) {
        this.name = Objects.requireNonNull(name).trim();
        this.address = null; //set after keygen
        this.transactionHistory = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getAddress() throws Exceptions {
        if (address == null) {
            throw new Exceptions(NO_ADDRESS_ERROR);
        }
        return address;
    }

    public void addTransaction(String transactionEntry) {
        transactionHistory.add(Objects.requireNonNull(transactionEntry).trim());
    }

    public List<String> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    public void setKeys(Key[] keys) {
        this.publicKey = keys[0];
        this.privateKey = keys[1];
        address = publicKey.getWalletAddress();
    }

    private String bytesToHex(byte[] hashBytes) {
        StringBuilder hex = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String value = Integer.toHexString(0xff & hashByte);
            if (value.length() == 1) {
                hex.append('0');
            }
            hex.append(value);
        }
        return hex.toString();
    }
}
