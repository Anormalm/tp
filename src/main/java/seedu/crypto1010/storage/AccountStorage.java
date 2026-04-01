package seedu.crypto1010.storage;

import seedu.crypto1010.auth.AccountCredentials;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AccountStorage {
    private static final String DATA_DIR = "data";
    private static final String ACCOUNTS_DIR = "accounts";
    private static final String FILE_NAME = "credentials.txt";
    private static final String RECORD_PREFIX = "U|";

    private final Path dataFilePath;

    public AccountStorage(Class<?> appClass) {
        this.dataFilePath = StorageUtils.resolveDataDirectory(appClass, DATA_DIR)
                .resolve(ACCOUNTS_DIR)
                .resolve(FILE_NAME);
    }

    public List<AccountCredentials> load() throws IOException {
        if (!Files.exists(dataFilePath)) {
            return List.of();
        }

        List<String> lines = Files.readAllLines(dataFilePath, StandardCharsets.UTF_8);
        List<AccountCredentials> accounts = new ArrayList<>();
        Set<String> seenUsernames = new LinkedHashSet<>();

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            if (!line.startsWith(RECORD_PREFIX)) {
                throw new IOException("Invalid account data: unrecognized line format.");
            }

            String[] parts = line.substring(RECORD_PREFIX.length()).split("\\|", 3);
            if (parts.length != 3 || hasBlankField(parts)) {
                throw new IOException("Invalid account data: malformed credential line.");
            }

            String username = parts[0].trim().toLowerCase();
            if (!seenUsernames.add(username)) {
                throw new IOException("Invalid account data: duplicate username '" + username + "'.");
            }
            accounts.add(new AccountCredentials(username, parts[1], parts[2]));
        }

        return accounts;
    }

    public void save(Collection<AccountCredentials> accounts) throws IOException {
        Files.createDirectories(dataFilePath.getParent());

        StringBuilder content = new StringBuilder();
        for (AccountCredentials credentials : accounts) {
            content.append(RECORD_PREFIX)
                    .append(credentials.username())
                    .append("|")
                    .append(credentials.saltHex())
                    .append("|")
                    .append(credentials.passwordHashHex())
                    .append(System.lineSeparator());
        }

        Files.writeString(dataFilePath, content.toString(), StandardCharsets.UTF_8);
    }

    private boolean hasBlankField(String[] parts) {
        for (String part : parts) {
            if (part == null || part.isBlank()) {
                return true;
            }
        }
        return false;
    }
}
