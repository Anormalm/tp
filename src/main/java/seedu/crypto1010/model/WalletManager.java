package seedu.crypto1010.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import seedu.crypto1010.exceptions.Exceptions;

public class WalletManager {
    private final List<Wallet> wallets;

    public WalletManager() {
        this.wallets = new ArrayList<>();
    }

    public Wallet createWallet(String walletName) {
        Wallet wallet = new Wallet(walletName);
        wallets.add(wallet);
        return wallet;
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
                    } catch (Exceptions e) {
                        return false;
                    }
                })
                .findFirst();
    }

    public List<Wallet> getWallets() {
        return Collections.unmodifiableList(wallets);
    }
}
