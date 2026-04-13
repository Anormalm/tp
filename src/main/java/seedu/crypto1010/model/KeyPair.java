package seedu.crypto1010.model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;

import seedu.crypto1010.exceptions.Crypto1010Exception;

/**
 * Generates secp256k1 key pairs and derives wallet addresses for supported currencies.
 */
public class KeyPair {
    private static final String GENERATE_START = "Generating secp256k1 keypair...";
    private static final String PRIVATE_KEY_DISPLAY = "Private key: ";
    private static final String PUBLIC_KEY_X_DISPLAY = "Public key X: ";
    private static final String PUBLIC_KEY_Y_DISPLAY = "Public key Y: ";
    private static final String ETH_DERIVE_DISPLAY = "Deriving Ethereum address via Keccak-256...";
    private static final String BTC_DERIVE_DISPLAY = "Deriving Bitcoin address via SHA-256 + RIPEMD-160...";
    private static final String ADDRESS_DISPLAY = "Address: ";
    private static final String COMPLETE_DISPLAY = "Keypair generation complete.";
    private static final String KEY_GENERATION_FAIL_ERROR = "Error: key pair generation failed";
    private static final String HASH_ALGORITHM_ERROR = "Error: SHA-256 not available";
    private static final String ON_CURVE_DISPLAY = "Verifying public key is on curve...";
    private static final String ON_CURVE_SUCCESS = "Public key verified on secp256k1 curve.";

    private static final int MAX_UNTRUNCATED_LENGTH = 20;
    private static final int TRUNCATED_PART_LENGTH = 10;

    private static final BigInteger fieldPrime = new BigInteger(
            "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16);
    private static final BigInteger curveOrder = new BigInteger(
            "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);
    private static final BigInteger generatorX = new BigInteger(
            "79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798", 16);
    private static final BigInteger generatorY = new BigInteger(
            "483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8", 16);
    private static final BigInteger coefficient = BigInteger.valueOf(7);

    private static final ECPoint GENERATOR_POINT = new ECPoint(generatorX, generatorY);
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final byte BTC_VERSION_BYTE = 0x00;

    private final BigInteger privateKey;
    private final BigInteger publicKeyX;
    private final BigInteger publicKeyY;
    private final String walletAddress;
    private final String currencyCode;

    private KeyPair(BigInteger privateKey, BigInteger publicKeyX, BigInteger publicKeyY,
                    String walletAddress, String currencyCode) {
        this.privateKey = privateKey;
        this.publicKeyX = publicKeyX;
        this.publicKeyY = publicKeyY;
        this.walletAddress = walletAddress;
        this.currencyCode = currencyCode;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public BigInteger getPublicKeyX() {
        return publicKeyX;
    }

    public BigInteger getPublicKeyY() {
        return publicKeyY;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Picks the address derivation strategy that matches the wallet currency.
     */
    public static KeyPair generate(String currencyCode) throws Crypto1010Exception {
        String normalized = CurrencyCode.normalizeOrDefault(currencyCode);
        if (normalized.equals("btc")) {
            return generateBtc();
        } else if (normalized.equals("eth")) {
            return generateEth();
        } else {
            return generateGeneric(); // full keypair, ETH-style address, stored as "generic"
        }
    }

    public static KeyPair generateEth() throws Crypto1010Exception {
        System.out.println(GENERATE_START);

        BigInteger privateKey = generatePrivateKey();
        System.out.println(PRIVATE_KEY_DISPLAY + truncate(privateKey.toString(16)));

        ECPoint publicKeyPoint = ECCurve.scalarMultiply(privateKey, GENERATOR_POINT);

        System.out.println(ON_CURVE_DISPLAY);
        if (!ECCurve.isOnCurve(publicKeyPoint)) {
            throw new Crypto1010Exception(KEY_GENERATION_FAIL_ERROR);
        }
        System.out.println(ON_CURVE_SUCCESS);

        System.out.println(PUBLIC_KEY_X_DISPLAY + truncate(publicKeyPoint.xCoord.toString(16)));
        System.out.println(PUBLIC_KEY_Y_DISPLAY + truncate(publicKeyPoint.yCoord.toString(16)));

        System.out.println(ETH_DERIVE_DISPLAY);
        String address = deriveEthAddress(publicKeyPoint);
        System.out.println(ADDRESS_DISPLAY + address);
        System.out.println(COMPLETE_DISPLAY);

        return new KeyPair(privateKey, publicKeyPoint.xCoord, publicKeyPoint.yCoord,
                           address, CurrencyCode.normalize("eth"));
    }

    public static KeyPair generateBtc() throws Crypto1010Exception {
        System.out.println(GENERATE_START);

        BigInteger privateKey = generatePrivateKey();
        System.out.println(PRIVATE_KEY_DISPLAY + truncate(privateKey.toString(16)));

        ECPoint publicKeyPoint = ECCurve.scalarMultiply(privateKey, GENERATOR_POINT);

        System.out.println(ON_CURVE_DISPLAY);
        if (!ECCurve.isOnCurve(publicKeyPoint)) {
            throw new Crypto1010Exception(KEY_GENERATION_FAIL_ERROR);
        }
        System.out.println(ON_CURVE_SUCCESS);

        System.out.println(PUBLIC_KEY_X_DISPLAY + truncate(publicKeyPoint.xCoord.toString(16)));
        System.out.println(PUBLIC_KEY_Y_DISPLAY + truncate(publicKeyPoint.yCoord.toString(16)));

        System.out.println(BTC_DERIVE_DISPLAY);
        String address = deriveBtcAddress(publicKeyPoint);
        System.out.println(ADDRESS_DISPLAY + address);
        System.out.println(COMPLETE_DISPLAY);

        return new KeyPair(privateKey, publicKeyPoint.xCoord, publicKeyPoint.yCoord,
                           address, CurrencyCode.normalize("btc"));
    }

    public static KeyPair generateGeneric() throws Crypto1010Exception {
        System.out.println(GENERATE_START);

        BigInteger privateKey = generatePrivateKey();
        System.out.println(PRIVATE_KEY_DISPLAY + truncate(privateKey.toString(16)));

        ECPoint publicKeyPoint = ECCurve.scalarMultiply(privateKey, GENERATOR_POINT);

        System.out.println(ON_CURVE_DISPLAY);
        if (!ECCurve.isOnCurve(publicKeyPoint)) {
            throw new Crypto1010Exception(KEY_GENERATION_FAIL_ERROR);
        }
        System.out.println(ON_CURVE_SUCCESS);

        System.out.println(PUBLIC_KEY_X_DISPLAY + truncate(publicKeyPoint.xCoord.toString(16)));
        System.out.println(PUBLIC_KEY_Y_DISPLAY + truncate(publicKeyPoint.yCoord.toString(16)));

        System.out.println(ETH_DERIVE_DISPLAY);
        String address = deriveEthAddress(publicKeyPoint);
        System.out.println(ADDRESS_DISPLAY + address);
        System.out.println(COMPLETE_DISPLAY);

        return new KeyPair(privateKey, publicKeyPoint.xCoord, publicKeyPoint.yCoord,
                           address, CurrencyCode.GENERIC);
    }

    /**
     * Ethereum addresses use the last 20 bytes of the Keccak-256 hash of the uncompressed public key.
     */
    private static String deriveEthAddress(ECPoint publicKeyPoint) {
        byte[] publicKeyBytes = new byte[64];
        byte[] xBytes = toBytes32(publicKeyPoint.xCoord);
        byte[] yBytes = toBytes32(publicKeyPoint.yCoord);
        System.arraycopy(xBytes, 0, publicKeyBytes, 0, 32);
        System.arraycopy(yBytes, 0, publicKeyBytes, 32, 32);

        KeccakDigest digest = new KeccakDigest(256);
        digest.update(publicKeyBytes, 0, publicKeyBytes.length);
        byte[] hash = new byte[32];
        digest.doFinal(hash, 0);

        StringBuilder address = new StringBuilder("0x");
        for (int i = 12; i < 32; i++) {
            address.append(String.format("%02x", hash[i]));
        }
        return address.toString();
    }

    /**
     * Builds a legacy Base58Check Bitcoin address from the compressed public key.
     */
    private static String deriveBtcAddress(ECPoint publicKeyPoint) throws Crypto1010Exception {
        byte[] compressedPublicKey = new byte[33];
        compressedPublicKey[0] = publicKeyPoint.yCoord.testBit(0) ? (byte) 0x03 : (byte) 0x02;
        byte[] xBytes = toBytes32(publicKeyPoint.xCoord);
        System.arraycopy(xBytes, 0, compressedPublicKey, 1, 32);

        // SHA-256
        byte[] sha256Hash;
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256Hash = sha256.digest(compressedPublicKey);
        } catch (NoSuchAlgorithmException e) {
            throw new Crypto1010Exception(HASH_ALGORITHM_ERROR);
        }

        // RIPEMD-160
        RIPEMD160Digest ripemd160 = new RIPEMD160Digest();
        ripemd160.update(sha256Hash, 0, sha256Hash.length);
        byte[] ripemd160Hash = new byte[20];
        ripemd160.doFinal(ripemd160Hash, 0);

        // version byte
        byte[] versioned = new byte[21];
        versioned[0] = BTC_VERSION_BYTE;
        System.arraycopy(ripemd160Hash, 0, versioned, 1, 20);

        // 4-byte checksum - double SHA-256
        byte[] checksum = computeChecksum(versioned);

        // append checksum
        byte[] withChecksum = new byte[25];
        System.arraycopy(versioned, 0, withChecksum, 0, 21);
        System.arraycopy(checksum, 0, withChecksum, 21, 4);

        return Base58.encode(withChecksum);
    }

    private static byte[] computeChecksum(byte[] input) throws Crypto1010Exception {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] first = sha256.digest(input);
            byte[] second = sha256.digest(first);
            byte[] checksum = new byte[4];
            System.arraycopy(second, 0, checksum, 0, 4);
            return checksum;
        } catch (NoSuchAlgorithmException e) {
            throw new Crypto1010Exception(HASH_ALGORITHM_ERROR);
        }
    }

    /**
     * Normalizes an unsigned big integer into the fixed 32-byte width expected by secp256k1 operations.
     */
    private static byte[] toBytes32(BigInteger value) {
        byte[] bytes = value.toByteArray();
        byte[] result = new byte[32];
        if (bytes.length <= 32) {
            System.arraycopy(bytes, 0, result, 32 - bytes.length, bytes.length);
        } else {
            System.arraycopy(bytes, bytes.length - 32, result, 0, 32);
        }
        return result;
    }

    /**
     * Generates a private key in the valid secp256k1 range [1, curveOrder).
     */
    private static BigInteger generatePrivateKey() {
        BigInteger privateKey;
        do {
            privateKey = new BigInteger(256, RANDOM);
        } while (privateKey.compareTo(BigInteger.ONE) < 0
                || privateKey.compareTo(curveOrder) >= 0);
        return privateKey;
    }

    public static String truncate(String hex) {
        if (hex.length() <= MAX_UNTRUNCATED_LENGTH) {
            return hex;
        }
        return hex.substring(0, TRUNCATED_PART_LENGTH) + "..."
                + hex.substring(hex.length() - TRUNCATED_PART_LENGTH);
    }

    /**
     * Point at infinity acts as the additive identity during elliptic-curve arithmetic.
     */
    private static class ECPoint {
        final BigInteger xCoord;
        final BigInteger yCoord;
        final boolean isInfinity;

        ECPoint(BigInteger xCoord, BigInteger yCoord) {
            this.xCoord = xCoord;
            this.yCoord = yCoord;
            this.isInfinity = false;
        }

        ECPoint() {
            this.xCoord = null;
            this.yCoord = null;
            this.isInfinity = true;
        }
    }

    /**
     * Minimal secp256k1 arithmetic used to derive public keys from private keys.
     */
    private static class ECCurve {
        private static BigInteger floorMod(BigInteger a) {
            return a.mod(fieldPrime).add(fieldPrime).mod(fieldPrime);
        }

        static ECPoint pointDouble(ECPoint point) {
            if (point.isInfinity) {
                return point;
            }

            // lambda = 3x^2 * (2y)^-1 mod p
            BigInteger lambda = BigInteger.valueOf(3)
                    .multiply(point.xCoord.pow(2))
                    .mod(fieldPrime)
                    .multiply(floorMod(BigInteger.TWO.multiply(point.yCoord)).modInverse(fieldPrime))
                    .mod(fieldPrime);

            // x' = lambda^2 - 2x mod p
            BigInteger resultX = floorMod(lambda.pow(2).subtract(BigInteger.TWO.multiply(point.xCoord)));
            // y' = lambda(x - x') - y mod p
            BigInteger resultY = floorMod(lambda.multiply(point.xCoord.subtract(resultX)).subtract(point.yCoord));

            return new ECPoint(resultX, resultY);
        }

        static ECPoint pointAdd(ECPoint pointOne, ECPoint pointTwo) {
            if (pointOne.isInfinity) {
                return pointTwo;
            }
            if (pointTwo.isInfinity) {
                return pointOne;
            }
            if (pointOne.xCoord.equals(pointTwo.xCoord) && !pointOne.yCoord.equals(pointTwo.yCoord)) {
                return new ECPoint();
            }
            if (pointOne.xCoord.equals(pointTwo.xCoord) && pointOne.yCoord.equals(pointTwo.yCoord)) {
                return pointDouble(pointOne);
            }

            // lambda = (y2 - y1) / (x2 - x1) mod p
            BigInteger lambda = floorMod(pointTwo.yCoord.subtract(pointOne.yCoord))
                    .multiply(floorMod(pointTwo.xCoord.subtract(pointOne.xCoord)).modInverse(fieldPrime))
                    .mod(fieldPrime);

            // x' = lambda^2 - x1 - x2 mod p
            BigInteger resultX = floorMod(lambda.pow(2).subtract(pointOne.xCoord).subtract(pointTwo.xCoord));
            // y' = lambda(x1 - x') - y1 mod p
            BigInteger resultY = floorMod(lambda.multiply(pointOne.xCoord.subtract(resultX)).subtract(pointOne.yCoord));

            return new ECPoint(resultX, resultY);
        }

        static ECPoint scalarMultiply(BigInteger scalar, ECPoint point) {
            ECPoint result = new ECPoint();
            ECPoint addend = point;

            while (scalar.signum() > 0) {
                if (scalar.testBit(0)) {
                    result = pointAdd(result, addend);
                }
                addend = pointDouble(addend);
                scalar = scalar.shiftRight(1);
            }
            return result;
        }

        static boolean isOnCurve(ECPoint point) {
            if (point.isInfinity) {
                return true;
            }
            BigInteger left = point.yCoord.modPow(BigInteger.TWO, fieldPrime);
            BigInteger right = point.xCoord.modPow(BigInteger.valueOf(3), fieldPrime)
                    .add(coefficient).mod(fieldPrime);
            return left.equals(right);
        }
    }

    /**
     * Encodes the final Bitcoin payload using the Base58 alphabet that avoids ambiguous characters.
     */
    private static class Base58 {
        private static final String ALPHABET =
                "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        private static final BigInteger BASE = BigInteger.valueOf(58);

        static String encode(byte[] input) {
            int leadingZeros = 0;
            for (byte b : input) {
                if (b == 0) {
                    leadingZeros++;
                } else {
                    break;
                }
            }

            BigInteger value = new BigInteger(1, input);

            StringBuilder result = new StringBuilder();
            while (value.compareTo(BigInteger.ZERO) > 0) {
                BigInteger[] divRem = value.divideAndRemainder(BASE);
                value = divRem[0];
                result.append(ALPHABET.charAt(divRem[1].intValue()));
            }

            for (int i = 0; i < leadingZeros; i++) {
                result.append(ALPHABET.charAt(0));
            }

            return result.reverse().toString();
        }
    }
}
