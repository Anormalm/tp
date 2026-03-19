package seedu.duke.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Wallet {
    private final String name;
    private final String address;
    private final List<String> transactionHistory;
    private Key publicKey;
    private Key privateKey;

    public Wallet(String name) {
        this.name = Objects.requireNonNull(name).trim();
        this.address = generateAddress(this.name);
        this.transactionHistory = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
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
    }

    private String generateAddress(String walletName) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(walletName.toLowerCase().getBytes(StandardCharsets.UTF_8));
            return "0x" + bytesToHex(hash).substring(0, 40);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", e);
        }
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
