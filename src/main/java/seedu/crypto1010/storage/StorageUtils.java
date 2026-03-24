package seedu.crypto1010.storage;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class StorageUtils {
    private StorageUtils() {
    }

    public static Path resolveDataFilePath(Class<?> appClass, String dataDir, String fileName) {
        Path defaultPath = Path.of(System.getProperty("user.dir"), dataDir, fileName);
        try {
            Path codeSourcePath = Path.of(appClass.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (Files.isRegularFile(codeSourcePath)) {
                return codeSourcePath.getParent().resolve(dataDir).resolve(fileName);
            }
            return defaultPath;
        } catch (URISyntaxException | NullPointerException e) {
            return defaultPath;
        }
    }
}
