package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Crypto1010Exception;

public final class CommandParserUtil {

    private static final String WALLET_PREFIX = "w/";

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
        if (walletName.chars().anyMatch(Character::isWhitespace)) {
            throw new Crypto1010Exception(whitespaceError + " " + commandFormat);
        }
        return walletName;
    }
}
