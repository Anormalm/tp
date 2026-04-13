package seedu.crypto1010.service;

import seedu.crypto1010.auth.AccountCredentials;
import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.CurrencyCode;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;
import seedu.crypto1010.storage.AccountStorage;
import seedu.crypto1010.storage.BlockchainStorage;
import seedu.crypto1010.storage.WalletStorage;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Coordinates cross-account transfers and persists both sides of the transfer.
 */
public class CrossAccountTransferService {
    static final String RECIPIENT_NOT_FOUND_ERROR = "Error: Recipient account not found.";
    static final String SAME_ACCOUNT_ERROR = "Error: Cannot send to your own account.";
    static final String SENDER_WALLET_NOT_FOUND_ERROR = "Error: No wallet found for currency '%s'.";
    static final String DUPLICATE_CURRENCY_WALLET_ERROR =
            "Error: Multiple wallets found for currency '%s'. Use exactly one wallet per currency.";
    static final String ACCOUNT_DATA_LOAD_ERROR = "Error: Failed to load account data.";
    static final String SENDER_DATA_SAVE_ERROR = "Error: Failed to save sender account data.";
    static final String RECIPIENT_DATA_LOAD_ERROR = "Error: Failed to load recipient account data.";
    static final String RECIPIENT_DATA_SAVE_ERROR = "Error: Failed to save recipient account data.";

    private static final String EXTERNAL_ACCOUNT_PREFIX = "external:";

    private final WalletManager currentWalletManager;
    private final String currentAccountName;
    private final Class<?> storageAnchor;

    public CrossAccountTransferService(WalletManager currentWalletManager, String currentAccountName,
                                       Class<?> storageAnchor) {
        this.currentWalletManager = Objects.requireNonNull(currentWalletManager);
        this.currentAccountName = normalizeAccountName(currentAccountName);
        this.storageAnchor = Objects.requireNonNull(storageAnchor);
    }

    /**
     * Transfers funds to another account while keeping sender and recipient storage updates consistent.
     */
    public CrossAccountTransferResult transfer(String recipientAccountName, BigDecimal amount, String currencyCode,
                                               Blockchain senderBlockchain) throws Crypto1010Exception {
        Objects.requireNonNull(amount);
        Objects.requireNonNull(senderBlockchain);

        if (currentAccountName.isBlank()) {
            throw new Crypto1010Exception(ACCOUNT_DATA_LOAD_ERROR);
        }

        String normalizedRecipientAccountName = normalizeAccountName(recipientAccountName);
        String normalizedCurrencyCode = CurrencyCode.normalize(currencyCode);

        if (normalizedRecipientAccountName.equals(currentAccountName)) {
            throw new Crypto1010Exception(SAME_ACCOUNT_ERROR);
        }
        if (!accountExists(normalizedRecipientAccountName)) {
            throw new Crypto1010Exception(RECIPIENT_NOT_FOUND_ERROR);
        }

        Wallet senderWallet = resolveSingleCurrencyWallet(currentWalletManager, normalizedCurrencyCode);
        BigDecimal senderBalance = senderBlockchain.getPreciseBalance(senderWallet.getName());
        if (senderBalance.compareTo(amount) < 0) {
            throw new Crypto1010Exception(TransactionRecordingService.INSUFFICIENT_BALANCE_ERROR);
        }

        WalletStorage recipientWalletStorage = new WalletStorage(storageAnchor, normalizedRecipientAccountName);
        BlockchainStorage recipientBlockchainStorage =
                new BlockchainStorage(storageAnchor, normalizedRecipientAccountName);
        WalletStorage senderWalletStorage = new WalletStorage(storageAnchor, currentAccountName);
        BlockchainStorage senderBlockchainStorage = new BlockchainStorage(storageAnchor, currentAccountName);

        WalletManager recipientWalletManager = loadRecipientWalletManager(recipientWalletStorage);
        Blockchain recipientBlockchain = loadRecipientBlockchain(recipientBlockchainStorage);
        // Keep snapshots so partially persisted cross-account transfers can be rolled back.
        WalletManager recipientWalletManagerBefore = copyWalletManager(recipientWalletManager);
        Blockchain recipientBlockchainBefore = copyBlockchain(recipientBlockchain);

        WalletManager senderWalletManagerForSave = copyWalletManager(currentWalletManager);
        Blockchain senderBlockchainForSave = copyBlockchain(senderBlockchain);
        Wallet senderWalletForSave = resolveSingleCurrencyWallet(senderWalletManagerForSave, normalizedCurrencyCode);

        RecipientWalletResolution recipientResolution =
                resolveOrCreateRecipientWallet(recipientWalletManager, normalizedCurrencyCode);

        // Persist against cloned sender state first so live in-memory state changes only after durable saves succeed.
        recipientBlockchain.addTransactions(List.of(formatRecipientTransaction(
                recipientResolution.wallet().getName(),
                normalizedCurrencyCode,
                amount)));
        senderBlockchainForSave.addTransactions(List.of(formatSenderTransaction(
                senderWalletForSave.getName(),
                normalizedRecipientAccountName,
                normalizedCurrencyCode,
                amount)));
        senderWalletForSave.addTransaction(buildHistoryEntry(
                normalizedRecipientAccountName,
                amount,
                normalizedCurrencyCode));

        persistTransfer(
                senderWalletStorage,
                senderBlockchainStorage,
                senderWalletManagerForSave,
                senderBlockchainForSave,
                copyWalletManager(currentWalletManager),
                copyBlockchain(senderBlockchain),
                recipientWalletStorage,
                recipientBlockchainStorage,
                recipientWalletManager,
                recipientBlockchain,
                recipientWalletManagerBefore,
                recipientBlockchainBefore);

        senderBlockchain.addTransactions(List.of(formatSenderTransaction(
                senderWallet.getName(),
                normalizedRecipientAccountName,
                normalizedCurrencyCode,
                amount)));
        senderWallet.addTransaction(buildHistoryEntry(normalizedRecipientAccountName, amount, normalizedCurrencyCode));

        return new CrossAccountTransferResult(senderWallet.getName(), recipientResolution.wallet().getName(),
                recipientResolution.wasCreated());
    }

    /**
     * Checks account storage rather than the current wallet manager because the recipient is another account.
     */
    private boolean accountExists(String recipientAccountName) throws Crypto1010Exception {
        AccountStorage accountStorage = new AccountStorage(storageAnchor);
        try {
            List<AccountCredentials> accountCredentials = accountStorage.load();
            return accountCredentials.stream()
                    .map(AccountCredentials::username)
                    .anyMatch(username -> username.equalsIgnoreCase(recipientAccountName));
        } catch (IOException e) {
            throw new Crypto1010Exception(ACCOUNT_DATA_LOAD_ERROR);
        }
    }

    private Wallet resolveSingleCurrencyWallet(WalletManager walletManager, String currencyCode)
            throws Crypto1010Exception {
        List<Wallet> matchingWallets = walletManager.findWalletsByCurrency(currencyCode);
        if (matchingWallets.isEmpty()) {
            throw new Crypto1010Exception(String.format(Locale.ROOT, SENDER_WALLET_NOT_FOUND_ERROR, currencyCode));
        }
        if (matchingWallets.size() > 1) {
            throw new Crypto1010Exception(String.format(Locale.ROOT, DUPLICATE_CURRENCY_WALLET_ERROR, currencyCode));
        }
        return matchingWallets.get(0);
    }

    private WalletManager loadRecipientWalletManager(WalletStorage recipientWalletStorage) throws Crypto1010Exception {
        try {
            return recipientWalletStorage.load();
        } catch (IOException e) {
            throw new Crypto1010Exception(RECIPIENT_DATA_LOAD_ERROR);
        }
    }

    private Blockchain loadRecipientBlockchain(BlockchainStorage recipientBlockchainStorage)
            throws Crypto1010Exception {
        try {
            return recipientBlockchainStorage.load();
        } catch (IOException e) {
            throw new Crypto1010Exception(RECIPIENT_DATA_LOAD_ERROR);
        }
    }

    /**
     * Finds the recipient wallet for the requested currency or creates one when none exists yet.
     */
    private RecipientWalletResolution resolveOrCreateRecipientWallet(WalletManager recipientWalletManager,
                                                                     String currencyCode)
            throws Crypto1010Exception {
        List<Wallet> matchingWallets = recipientWalletManager.findWalletsByCurrency(currencyCode);
        if (matchingWallets.size() > 1) {
            throw new Crypto1010Exception(String.format(Locale.ROOT, DUPLICATE_CURRENCY_WALLET_ERROR, currencyCode));
        }
        if (!matchingWallets.isEmpty()) {
            return new RecipientWalletResolution(matchingWallets.get(0), false);
        }

        String walletName = generateRecipientWalletName(recipientWalletManager, currencyCode);
        Wallet createdWallet = recipientWalletManager.createWallet(walletName, currencyCode);
        return new RecipientWalletResolution(createdWallet, true);
    }

    /**
     * Generates a readable wallet name that remains unique within the recipient account.
     */
    private String generateRecipientWalletName(WalletManager walletManager, String currencyCode) {
        if (!walletManager.hasWallet(currencyCode)) {
            return currencyCode;
        }

        String baseName = currencyCode + "-wallet";
        if (!walletManager.hasWallet(baseName)) {
            return baseName;
        }

        int suffix = 2;
        while (walletManager.hasWallet(baseName + "-" + suffix)) {
            suffix++;
        }
        return baseName + "-" + suffix;
    }

    private String formatSenderTransaction(String senderWalletName, String recipientAccountName,
                                           String currencyCode, BigDecimal amount) {
        return senderWalletName + " -> " + EXTERNAL_ACCOUNT_PREFIX + recipientAccountName + ":" + currencyCode
                + " : " + amount.stripTrailingZeros().toPlainString();
    }

    private String formatRecipientTransaction(String recipientWalletName, String currencyCode, BigDecimal amount) {
        return EXTERNAL_ACCOUNT_PREFIX + currentAccountName + ":" + currencyCode + " -> " + recipientWalletName
                + " : " + amount.stripTrailingZeros().toPlainString();
    }

    private void saveRecipientWalletManager(WalletStorage recipientWalletStorage, WalletManager recipientWalletManager)
            throws Crypto1010Exception {
        try {
            recipientWalletStorage.save(recipientWalletManager);
        } catch (IOException e) {
            throw new Crypto1010Exception(RECIPIENT_DATA_SAVE_ERROR);
        }
    }

    private void saveRecipientBlockchain(BlockchainStorage recipientBlockchainStorage, Blockchain recipientBlockchain)
            throws Crypto1010Exception {
        try {
            recipientBlockchainStorage.save(recipientBlockchain);
        } catch (IOException e) {
            throw new Crypto1010Exception(RECIPIENT_DATA_SAVE_ERROR);
        }
    }

    private void persistTransfer(
            WalletStorage senderWalletStorage,
            BlockchainStorage senderBlockchainStorage,
            WalletManager senderWalletManagerAfter,
            Blockchain senderBlockchainAfter,
            WalletManager senderWalletManagerBefore,
            Blockchain senderBlockchainBefore,
            WalletStorage recipientWalletStorage,
            BlockchainStorage recipientBlockchainStorage,
            WalletManager recipientWalletManagerAfter,
            Blockchain recipientBlockchainAfter,
            WalletManager recipientWalletManagerBefore,
            Blockchain recipientBlockchainBefore) throws Crypto1010Exception {
        try {
            saveRecipientWalletManager(recipientWalletStorage, recipientWalletManagerAfter);
            saveRecipientBlockchain(recipientBlockchainStorage, recipientBlockchainAfter);
            saveSenderWalletManager(senderWalletStorage, senderWalletManagerAfter);
            saveSenderBlockchain(senderBlockchainStorage, senderBlockchainAfter);
        } catch (Crypto1010Exception saveFailure) {
            rollbackPersistedTransfer(
                    senderWalletStorage,
                    senderBlockchainStorage,
                    senderWalletManagerBefore,
                    senderBlockchainBefore,
                    recipientWalletStorage,
                    recipientBlockchainStorage,
                    recipientWalletManagerBefore,
                    recipientBlockchainBefore);
            throw saveFailure;
        }
    }

    /**
     * Restores both accounts to their pre-transfer snapshots if any save step fails.
     */
    private void rollbackPersistedTransfer(
            WalletStorage senderWalletStorage,
            BlockchainStorage senderBlockchainStorage,
            WalletManager senderWalletManagerBefore,
            Blockchain senderBlockchainBefore,
            WalletStorage recipientWalletStorage,
            BlockchainStorage recipientBlockchainStorage,
            WalletManager recipientWalletManagerBefore,
            Blockchain recipientBlockchainBefore) throws Crypto1010Exception {
        try {
            recipientWalletStorage.save(recipientWalletManagerBefore);
            recipientBlockchainStorage.save(recipientBlockchainBefore);
            senderWalletStorage.save(senderWalletManagerBefore);
            senderBlockchainStorage.save(senderBlockchainBefore);
        } catch (IOException rollbackFailure) {
            throw new Crypto1010Exception("Error: Transfer save failed and rollback did not complete.");
        }
    }

    private void saveSenderWalletManager(WalletStorage senderWalletStorage, WalletManager senderWalletManager)
            throws Crypto1010Exception {
        try {
            senderWalletStorage.save(senderWalletManager);
        } catch (IOException e) {
            throw new Crypto1010Exception(SENDER_DATA_SAVE_ERROR);
        }
    }

    private void saveSenderBlockchain(BlockchainStorage senderBlockchainStorage, Blockchain senderBlockchain)
            throws Crypto1010Exception {
        try {
            senderBlockchainStorage.save(senderBlockchain);
        } catch (IOException e) {
            throw new Crypto1010Exception(SENDER_DATA_SAVE_ERROR);
        }
    }

    private String buildHistoryEntry(String recipientAccountName, BigDecimal amount, String currencyCode) {
        return "crossSend acc/" + recipientAccountName
                + " amt/" + amount.stripTrailingZeros().toPlainString()
                + " curr/" + currencyCode;
    }

    private String normalizeAccountName(String accountName) {
        return accountName == null ? "" : accountName.trim().toLowerCase(Locale.ROOT);
    }

    private WalletManager copyWalletManager(WalletManager original) throws Crypto1010Exception {
        WalletManager copy = new WalletManager();
        for (Wallet wallet : original.getWallets()) {
            Wallet walletCopy = copy.createWallet(wallet.getName(), wallet.getCurrencyCode());
            for (String history : wallet.getTransactionHistory()) {
                walletCopy.addTransaction(history);
            }
        }
        return copy;
    }

    /**
     * Copies blocks so persisted changes can be prepared without mutating the live sender chain early.
     */
    private Blockchain copyBlockchain(Blockchain original) {
        List<seedu.crypto1010.model.Block> copiedBlocks = new ArrayList<>();
        for (seedu.crypto1010.model.Block block : original.getBlocks()) {
            copiedBlocks.add(new seedu.crypto1010.model.Block(
                    block.getIndex(),
                    LocalDateTime.from(block.getTimestampValue()),
                    block.getPreviousHash(),
                    new ArrayList<>(block.getTransactions()),
                    block.getCurrentHash()));
        }
        return new Blockchain(copiedBlocks);
    }

    private record RecipientWalletResolution(Wallet wallet, boolean wasCreated) {
    }

    public record CrossAccountTransferResult(String senderWalletName, String recipientWalletName,
                                             boolean recipientWalletCreated) {
    }
}
