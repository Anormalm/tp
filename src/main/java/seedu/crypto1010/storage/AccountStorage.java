package seedu.crypto1010.storage;

import seedu.crypto1010.auth.AccountCredentials;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class AccountStorage {
    private static final String DATA_DIR = "data";
    private static final String ACCOUNTS_DIR = "accounts";
    private static final String FILE_NAME = "credentials.txt";
    private static final String KEY_FILE_NAME = "credentials.key";
    private static final String SIGNATURE_PREFIX = "S|";
    private static final String RECORD_PREFIX = "U|";
    private static final String MAC_ALGORITHM = "HmacSHA256";
    private static final long MAX_CREDENTIALS_FILE_SIZE_BYTES = 256L * 1024L;
    private static final int MAX_ACCOUNT_COUNT = 10_000;

    private final Path dataFilePath;
    private final Path keyFilePath;

    public AccountStorage(Class<?> appClass) {
        Path accountsDirectory = StorageUtils.resolveDataDirectory(appClass, DATA_DIR).resolve(ACCOUNTS_DIR);
        this.dataFilePath = accountsDirectory.resolve(FILE_NAME);
        this.keyFilePath = accountsDirectory.resolve(KEY_FILE_NAME);
    }

    public List<AccountCredentials> load() throws IOException {
        if (!Files.exists(dataFilePath)) {
            return List.of();
        }
        enforceFileSizeLimit(dataFilePath, MAX_CREDENTIALS_FILE_SIZE_BYTES,
                "Invalid account data: credential file is too large.");

        List<String> lines = Files.readAllLines(dataFilePath, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            return List.of();
        }

        int startIndex = 0;
        String expectedSignatureHex = null;
        String firstLine = lines.get(0);
        if (firstLine.startsWith(SIGNATURE_PREFIX)) {
            expectedSignatureHex = firstLine.substring(SIGNATURE_PREFIX.length());
            startIndex = 1;
        }

        List<String> recordLines = new ArrayList<>();
        List<AccountCredentials> accounts = new ArrayList<>();
        Set<String> seenUsernames = new LinkedHashSet<>();

        for (int i = startIndex; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            if (!line.startsWith(RECORD_PREFIX)) {
                throw new IOException("Invalid account data: unrecognized line format.");
            }
            recordLines.add(line);

            String[] parts = line.substring(RECORD_PREFIX.length()).split("\\|", 3);
            if (parts.length != 3 || hasBlankField(parts)) {
                throw new IOException("Invalid account data: malformed credential line.");
            }

            String username = parts[0].trim().toLowerCase();
            if (!seenUsernames.add(username)) {
                throw new IOException("Invalid account data: duplicate username '" + username + "'.");
            }
            accounts.add(new AccountCredentials(username, parts[1], parts[2]));
            if (accounts.size() > MAX_ACCOUNT_COUNT) {
                throw new IOException("Invalid account data: too many accounts.");
            }
        }

        if (expectedSignatureHex != null) {
            verifySignature(expectedSignatureHex, canonicalizeRecordContent(recordLines));
        }

        return accounts;
    }

    public void save(Collection<AccountCredentials> accounts) throws IOException {
        Files.createDirectories(dataFilePath.getParent());

        List<String> recordLines = new ArrayList<>();
        for (AccountCredentials credentials : accounts) {
            recordLines.add(new StringBuilder()
                    .append(RECORD_PREFIX)
                    .append(credentials.username())
                    .append("|")
                    .append(credentials.saltHex())
                    .append("|")
                    .append(credentials.passwordHashHex())
                    .toString());
        }

        String canonicalRecordContent = canonicalizeRecordContent(recordLines);
        String signatureHex = sign(canonicalRecordContent);

        StringBuilder content = new StringBuilder()
                .append(SIGNATURE_PREFIX)
                .append(signatureHex)
                .append(System.lineSeparator());
        for (String recordLine : recordLines) {
            content.append(recordLine).append(System.lineSeparator());
        }

        Files.writeString(dataFilePath, content.toString(), StandardCharsets.UTF_8);
    }

    private void verifySignature(String expectedSignatureHex, String canonicalRecordContent) throws IOException {
        String actualSignatureHex = sign(canonicalRecordContent);
        byte[] expected = expectedSignatureHex.getBytes(StandardCharsets.UTF_8);
        byte[] actual = actualSignatureHex.getBytes(StandardCharsets.UTF_8);
        if (!java.security.MessageDigest.isEqual(expected, actual)) {
            throw new IOException("Invalid account data: credential file signature mismatch.");
        }
    }

    private String sign(String canonicalRecordContent) throws IOException {
        try {
            Mac mac = Mac.getInstance(MAC_ALGORITHM);
            mac.init(new SecretKeySpec(loadOrCreateMacKey(), MAC_ALGORITHM));
            byte[] signature = mac.doFinal(canonicalRecordContent.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(signature);
        } catch (java.security.GeneralSecurityException e) {
            throw new IOException("Failed to sign account credentials.", e);
        }
    }

    private byte[] loadOrCreateMacKey() throws IOException {
        if (Files.exists(keyFilePath)) {
            String keyHex = Files.readString(keyFilePath, StandardCharsets.UTF_8).trim();
            if (keyHex.isEmpty()) {
                throw new IOException("Invalid account key data.");
            }
            try {
                return HexFormat.of().parseHex(keyHex);
            } catch (IllegalArgumentException e) {
                throw new IOException("Invalid account key data.", e);
            }
        }

        byte[] key = new byte[32];
        new java.security.SecureRandom().nextBytes(key);
        Files.writeString(keyFilePath, HexFormat.of().formatHex(key), StandardCharsets.UTF_8);
        return key;
    }

    private String canonicalizeRecordContent(List<String> recordLines) {
        if (recordLines.isEmpty()) {
            return "";
        }
        return String.join("\n", recordLines) + "\n";
    }

    private void enforceFileSizeLimit(Path filePath, long maxBytes, String errorMessage) throws IOException {
        if (Files.size(filePath) > maxBytes) {
            throw new IOException(errorMessage);
        }
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
