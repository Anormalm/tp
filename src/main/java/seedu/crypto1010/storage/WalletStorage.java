package seedu.crypto1010.storage;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.KeyPair;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Loads and saves wallets together with their local transaction history.
 */
public class WalletStorage {
    private static final String DATA_DIR = "data";
    private static final String FILE_NAME = "wallets.txt";
    private static final String WALLET_PREFIX = "W|";
    private static final String TRANSACTION_PREFIX = "T|";
    private static final String END_MARKER = "E";
    private static final String FIELD_SEPARATOR = "|";
    private static final long MAX_WALLET_FILE_SIZE_BYTES = 1024L * 1024L;
    private static final int MAX_WALLET_COUNT = 1_000;
    private static final int MAX_TRANSACTIONS_PER_WALLET = 5_000;
    private static final int MAX_TRANSACTION_ENTRY_LENGTH = 512;

    private final Path dataFilePath;

    public WalletStorage(Class<?> appClass) {
        this.dataFilePath = StorageUtils.resolveDataFilePath(appClass, DATA_DIR, FILE_NAME);
    }

    public WalletStorage(Class<?> appClass, String accountName) {
        this.dataFilePath = StorageUtils.resolveAccountDataFilePath(appClass, DATA_DIR, accountName, FILE_NAME);
    }

    /**
     * Loads the line-based wallet format, where wallet headers are followed by their transaction lines.
     */
    public WalletManager load() throws IOException {
        WalletManager walletManager = new WalletManager();
        if (!Files.exists(dataFilePath)) {
            return walletManager;
        }
        enforceFileSizeLimit(dataFilePath, MAX_WALLET_FILE_SIZE_BYTES,
                "Invalid wallet data: wallet file is too large.");

        List<String> lines = Files.readAllLines(dataFilePath, StandardCharsets.UTF_8);
        Wallet currentWallet = null;
        int walletCount = 0;
        int transactionsForCurrentWallet = 0;

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            if (line.startsWith(WALLET_PREFIX)) {
                // A wallet header starts a new wallet context for the following transaction lines.
                String walletData = line.substring(WALLET_PREFIX.length());
                String[] walletFields = walletData.split("\\|", 6);
                String walletName = unescape(walletFields[0]);
                String currencyCode = walletFields.length >= 2 ? unescape(walletFields[1]) : null;
                try {
                    currentWallet = walletManager.createWallet(walletName, currencyCode);
                } catch (Crypto1010Exception e) {
                    throw new IOException("Invalid wallet data: " + e.getMessage(), e);
                }

                if (walletFields.length == 6) {
                    try {
                        String address = unescape(walletFields[2]);
                        BigInteger publicKeyX = new BigInteger(walletFields[3], 16);
                        BigInteger publicKeyY = new BigInteger(walletFields[4], 16);
                        BigInteger privateKey = new BigInteger(walletFields[5], 16);
                        currentWallet.restoreKeyPair(
                                KeyPair.restore(privateKey, publicKeyX, publicKeyY,
                                                address, currentWallet.getCurrencyCode()));
                    } catch (Exception e) {
                        System.out.println("[Warning] Key data for wallet '" + currentWallet.getName()
                                                   + "' is corrupted and could not be restored. "
                                                   + "Please run keygen on this wallet to generate new keys.");
                    }
                }

                walletCount++;
                if (walletCount > MAX_WALLET_COUNT) {
                    throw new IOException("Invalid wallet data: too many wallets.");
                }
                transactionsForCurrentWallet = 0;
                continue;
            }
            if (line.startsWith(TRANSACTION_PREFIX)) {
                if (currentWallet == null) {
                    throw new IOException("Invalid wallet data: transaction without wallet context.");
                }
                String transaction = unescape(line.substring(TRANSACTION_PREFIX.length()));
                if (transaction.length() > MAX_TRANSACTION_ENTRY_LENGTH) {
                    throw new IOException("Invalid wallet data: transaction entry is too long.");
                }
                currentWallet.addTransaction(transaction);
                transactionsForCurrentWallet++;
                if (transactionsForCurrentWallet > MAX_TRANSACTIONS_PER_WALLET) {
                    throw new IOException("Invalid wallet data: too many transactions in wallet.");
                }
                continue;
            }
            if (END_MARKER.equals(line)) {
                currentWallet = null;
                continue;
            }
            throw new IOException("Invalid wallet data: unrecognized line format.");
        }

        return walletManager;
    }

    /**
     * Saves wallets using a compact line-based format that is easy to inspect and append to.
     */
    public void save(WalletManager walletManager) throws IOException {
        StringBuilder content = new StringBuilder();
        for (Wallet wallet : walletManager.getWallets()) {
            content.append(WALLET_PREFIX)
                    .append(escape(wallet.getName()))
                    .append(FIELD_SEPARATOR)
                    .append(escape(wallet.getCurrencyCode()));

            if (wallet.hasKeyPair()) {
                try {
                    content.append(FIELD_SEPARATOR)
                            .append(escape(wallet.getAddress()))
                            .append(FIELD_SEPARATOR)
                            .append(wallet.getKeyPair().getPublicKeyX().toString(16))
                            .append(FIELD_SEPARATOR)
                            .append(wallet.getKeyPair().getPublicKeyY().toString(16))
                            .append(FIELD_SEPARATOR)
                            .append(wallet.getKeyPair().getPrivateKey().toString(16));
                } catch (Crypto1010Exception e) {
                    // address not set - skip key fields
                }
            }

            content.append(System.lineSeparator());
            for (String transaction : wallet.getTransactionHistory()) {
                content.append(TRANSACTION_PREFIX).append(escape(transaction)).append(System.lineSeparator());
            }
            content.append(END_MARKER).append(System.lineSeparator());
        }

        Files.createDirectories(dataFilePath.getParent());
        Files.writeString(dataFilePath, content.toString(), StandardCharsets.UTF_8);
    }

    /**
     * Escapes control characters so wallet names and history entries stay on one storage line each.
     */
    private String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Reconstructs the original string values from the escaped storage representation.
     */
    private String unescape(String value) {
        StringBuilder result = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (!escaping) {
                if (current == '\\') {
                    escaping = true;
                } else {
                    result.append(current);
                }
                continue;
            }

            switch (current) {
            case 'n':
                result.append('\n');
                break;
            case 'r':
                result.append('\r');
                break;
            case 't':
                result.append('\t');
                break;
            case '\\':
                result.append('\\');
                break;
            default:
                result.append(current);
                break;
            }
            escaping = false;
        }

        if (escaping) {
            result.append('\\');
        }
        return result.toString();
    }

    private void enforceFileSizeLimit(Path filePath, long maxBytes, String errorMessage) throws IOException {
        if (Files.size(filePath) > maxBytes) {
            throw new IOException(errorMessage);
        }
    }
}
