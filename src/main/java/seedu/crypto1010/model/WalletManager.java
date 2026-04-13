package seedu.crypto1010.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.crypto1010.exceptions.Crypto1010Exception;

public class WalletManager {
    private static final char RESERVED_NAME_DELIMITER = '|';
    private static final int MAX_WALLET_NAME_LENGTH = 32;
    private final List<Wallet> wallets;

    public WalletManager() {
        this.wallets = new ArrayList<>();
    }

    public Wallet createWallet(String walletName) {
        return createWallet(walletName, CurrencyCode.GENERIC);
    }

    public Wallet createWallet(String walletName, String currencyCode) {
        Objects.requireNonNull(walletName, "walletName must not be null");
        String normalizedName = walletName.trim();
        String normalizedCurrency = CurrencyCode.normalizeOrDefault(currencyCode);
        if (normalizedName.isEmpty()) {
            throw new IllegalArgumentException("walletName must not be blank");
        }
        if (normalizedName.length() > MAX_WALLET_NAME_LENGTH) {
            throw new IllegalArgumentException("walletName exceeds max length: " + MAX_WALLET_NAME_LENGTH);
        }
        if (containsReservedNameDelimiter(normalizedName)) {
            throw new IllegalArgumentException("walletName contains reserved character: " + RESERVED_NAME_DELIMITER);
        }
        if (hasWallet(normalizedName)) {
            throw new IllegalArgumentException("wallet already exists: " + normalizedName);
        }
        if (!CurrencyCode.isGeneric(normalizedCurrency) && hasWalletForCurrency(normalizedCurrency)) {
            throw new IllegalArgumentException("wallet currency already exists: " + normalizedCurrency);
        }
        Wallet wallet = new Wallet(normalizedName, normalizedCurrency);
        wallets.add(wallet);
        return wallet;
    }

    private boolean containsReservedNameDelimiter(String walletName) {
        return walletName.indexOf(RESERVED_NAME_DELIMITER) >= 0;
    }

    public boolean hasWallet(String walletName) {
        String normalizedName = walletName.trim();
        return wallets.stream()
                .anyMatch(wallet -> wallet.getName().equalsIgnoreCase(normalizedName));
    }

    public Optional<Wallet> findWallet(String walletName) {
        String normalizedName = walletName.trim();
        return wallets.stream()
                .filter(wallet -> wallet.getName().equalsIgnoreCase(normalizedName))
                .findFirst();
    }

    public Optional<Wallet> findWalletByAddress(String address) {
        String normalizedAddress = address.trim();
        return wallets.stream()
                .filter(wallet -> {
                    try {
                        return wallet.getAddress().equalsIgnoreCase(normalizedAddress);
                    } catch (Crypto1010Exception e) {
                        return false;
                    }
                })
                .findFirst();
    }

    public boolean hasWalletForCurrency(String currencyCode) {
        String normalizedCurrency = CurrencyCode.normalizeOrDefault(currencyCode);
        return wallets.stream()
                .anyMatch(wallet -> wallet.getCurrencyCode().equalsIgnoreCase(normalizedCurrency));
    }

    public List<Wallet> findWalletsByCurrency(String currencyCode) {
        String normalizedCurrency = CurrencyCode.normalizeOrDefault(currencyCode);
        return wallets.stream()
                .filter(wallet -> wallet.getCurrencyCode().equalsIgnoreCase(normalizedCurrency))
                .toList();
    }

    public List<Wallet> getWallets() {
        return Collections.unmodifiableList(wallets);
    }
}
