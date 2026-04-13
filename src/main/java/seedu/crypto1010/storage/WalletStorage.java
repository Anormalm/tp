package seedu.crypto1010.storage;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

import java.io.IOException;
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

        List<String> lines = Files.readAllLines(dataFilePath, StandardCharsets.UTF_8);
        Wallet currentWallet = null;

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            if (line.startsWith(WALLET_PREFIX)) {
                // A wallet header starts a new wallet context for the following transaction lines.
                String walletData = line.substring(WALLET_PREFIX.length());
                String[] walletFields = walletData.split("\\|", 2);
                String walletName = unescape(walletFields[0]);
                String currencyCode = walletFields.length == 2 ? unescape(walletFields[1]) : null;
                try {
                    currentWallet = walletManager.createWallet(walletName, currencyCode);
                } catch (Crypto1010Exception e) {
                    throw new IOException("Invalid wallet data: " + e.getMessage(), e);
                }
                continue;
            }
            if (line.startsWith(TRANSACTION_PREFIX)) {
                if (currentWallet == null) {
                    throw new IOException("Invalid wallet data: transaction without wallet context.");
                }
                currentWallet.addTransaction(unescape(line.substring(TRANSACTION_PREFIX.length())));
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
            content.append(WALLET_PREFIX).append(escape(wallet.getName()));
            if (!"generic".equals(wallet.getCurrencyCode())) {
                content.append(FIELD_SEPARATOR).append(escape(wallet.getCurrencyCode()));
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
}
