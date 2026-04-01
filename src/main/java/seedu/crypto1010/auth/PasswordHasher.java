package seedu.crypto1010.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

final class PasswordHasher {
    private static final int SALT_LENGTH_BYTES = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordHasher() {
    }

    static String generateSaltHex() {
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        RANDOM.nextBytes(salt);
        return toHex(salt);
    }

    static String hash(String password, String saltHex) throws AuthenticationException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(fromHex(saltHex));
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return toHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", e);
        }
    }

    static boolean matches(String password, AccountCredentials credentials) throws AuthenticationException {
        String computedHash = hash(password, credentials.saltHex());
        byte[] expected = credentials.passwordHashHex().getBytes(StandardCharsets.UTF_8);
        byte[] actual = computedHash.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(expected, actual);
    }

    private static String toHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte currentByte : bytes) {
            String value = Integer.toHexString(0xff & currentByte);
            if (value.length() == 1) {
                hex.append('0');
            }
            hex.append(value);
        }
        return hex.toString();
    }

    private static byte[] fromHex(String hex) throws AuthenticationException {
        if (hex.length() % 2 != 0) {
            throw new AuthenticationException("Error: Stored account data is invalid.");
        }

        byte[] result = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            int high = Character.digit(hex.charAt(i), 16);
            int low = Character.digit(hex.charAt(i + 1), 16);
            if (high < 0 || low < 0) {
                throw new AuthenticationException("Error: Stored account data is invalid.");
            }
            result[i / 2] = (byte) ((high << 4) + low);
        }
        return result;
    }
}
