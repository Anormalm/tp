package seedu.crypto1010.storage;

import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class WalletStorage {
    private static final String DATA_DIR = "data";
    private static final String FILE_NAME = "wallets.txt";
    private static final String WALLET_PREFIX = "W|";
    private static final String TRANSACTION_PREFIX = "T|";
    private static final String END_MARKER = "E";

    private final Path dataFilePath;

    public WalletStorage(Class<?> appClass) {
        this.dataFilePath = StorageUtils.resolveDataFilePath(appClass, DATA_DIR, FILE_NAME);
    }

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
                String walletName = unescape(line.substring(WALLET_PREFIX.length()));
                currentWallet = walletManager.createWallet(walletName);
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

    public void save(WalletManager walletManager) throws IOException {
        StringBuilder content = new StringBuilder();
        for (Wallet wallet : walletManager.getWallets()) {
            content.append(WALLET_PREFIX).append(escape(wallet.getName())).append(System.lineSeparator());
            for (String transaction : wallet.getTransactionHistory()) {
                content.append(TRANSACTION_PREFIX).append(escape(transaction)).append(System.lineSeparator());
            }
            content.append(END_MARKER).append(System.lineSeparator());
        }

        Files.createDirectories(dataFilePath.getParent());
        Files.writeString(dataFilePath, content.toString(), StandardCharsets.UTF_8);
    }

    private String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

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
