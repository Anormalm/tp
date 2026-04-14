package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Crypto1010Exception;

import java.math.BigDecimal;

/**
 * Provides shared parsing helpers used by multiple command classes.
 */
public final class CommandParserUtil {

    private static final String WALLET_PREFIX = "w/";
    private static final int MAX_SIGNIFICANT_DIGITS = 50;
    private static final int MAX_ABSOLUTE_SCALE = 32;
    private static final int MAX_WALLET_NAME_LENGTH = 32;

    private CommandParserUtil() {
    }

    public static String parseRequiredWalletNameArgument(String args,
                                                         String invalidFormatError,
                                                         String emptyNameError,
                                                         String whitespaceError,
                                                         String commandFormat) throws Crypto1010Exception {
        if (args == null || args.isBlank()) {
            throw new Crypto1010Exception(emptyNameError + " " + commandFormat);
        }

        String trimmedArgs = args.trim();
        if (!trimmedArgs.startsWith(WALLET_PREFIX)) {
            throw new Crypto1010Exception(invalidFormatError);
        }

        String walletName = trimmedArgs.substring(WALLET_PREFIX.length()).trim();
        return validateWalletName(walletName, emptyNameError, whitespaceError, commandFormat);
    }

    public static String validateWalletName(String walletName,
                                            String emptyNameError,
                                            String whitespaceError,
                                            String commandFormat) throws Crypto1010Exception {
        if (walletName == null || walletName.isEmpty()) {
            throw new Crypto1010Exception(emptyNameError + " " + commandFormat);
        }
        if (walletName.length() > MAX_WALLET_NAME_LENGTH) {
            throw new Crypto1010Exception(
                    "Error: wallet name must be at most " + MAX_WALLET_NAME_LENGTH + " characters. " + commandFormat);
        }
        if (walletName.chars().anyMatch(Character::isWhitespace)) {
            throw new Crypto1010Exception(whitespaceError + " " + commandFormat);
        }
        return walletName;
    }

    public static BigDecimal parsePositiveDecimal(String amountText,
                                                  String invalidAmountError,
                                                  String commandFormat) throws Crypto1010Exception {
        try {
            BigDecimal amount = new BigDecimal(amountText);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new Crypto1010Exception(invalidAmountError + " " + commandFormat);
            }
            if (amount.precision() > MAX_SIGNIFICANT_DIGITS || Math.abs(amount.scale()) > MAX_ABSOLUTE_SCALE) {
                throw new Crypto1010Exception(invalidAmountError + " " + commandFormat);
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new Crypto1010Exception(invalidAmountError + " " + commandFormat);
        }
    }
}
