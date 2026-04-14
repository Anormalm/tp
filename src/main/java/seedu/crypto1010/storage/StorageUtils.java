package seedu.crypto1010.storage;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Resolves the directories used by the application's storage classes.
 */
public final class StorageUtils {
    private static final String DATA_DIRECTORY_PROPERTY = "crypto1010.dataDir";
    private static final String ACCOUNTS_DIR = "accounts";

    private StorageUtils() {
    }

    public static Path resolveDataDirectory(Class<?> appClass, String dataDir) {
        String overrideDirectory = System.getProperty(DATA_DIRECTORY_PROPERTY);
        if (overrideDirectory != null && !overrideDirectory.isBlank()) {
            return Path.of(overrideDirectory);
        }

        Path defaultPath = Path.of(System.getProperty("user.dir"), dataDir);
        try {
            Path codeSourcePath = Path.of(appClass.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (Files.isRegularFile(codeSourcePath)) {
                return codeSourcePath.getParent().resolve(dataDir);
            }
            return defaultPath;
        } catch (URISyntaxException | NullPointerException e) {
            return defaultPath;
        }
    }

    public static Path resolveDataFilePath(Class<?> appClass, String dataDir, String fileName) {
        return resolveDataDirectory(appClass, dataDir).resolve(fileName);
    }

    public static Path resolveAccountDataDirectory(Class<?> appClass, String dataDir, String accountName) {
        Objects.requireNonNull(accountName, "accountName must not be null");
        return resolveDataDirectory(appClass, dataDir).resolve(ACCOUNTS_DIR).resolve(accountName);
    }

    public static Path resolveAccountDataFilePath(
            Class<?> appClass,
            String dataDir,
            String accountName,
            String fileName) {
        return resolveAccountDataDirectory(appClass, dataDir, accountName).resolve(fileName);
    }
}
