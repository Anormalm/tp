package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.Key;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

import java.util.Scanner;

public class KeygenCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: keygen w/WALLET_NAME
            Example: keygen w/mainwallet
            
            Generates and displays key pair for new wallet, or regenerates for existing wallet
            Displays the process of creating a key pair
            """;

    private static final String WALLET_NOT_FOUND_ERROR = "Error: Wallet not found";
    private static final String KEY_PAIR_GENERATION_SUCCESSFUL = "Key pair successfully generated";
    private static final String NAME_ERROR = "Error: wallet name cannot be empty.";
    private static final String NAME_WHITESPACE_ERROR = "Error: wallet name must be one word without spaces.";
    private static final String INVALID_FORMAT_ERROR = "Error: Invalid keygen format. Use: keygen w/WALLET_NAME";
    private static final String KEYGEN_FORMAT = "Use: keygen w/WALLET_NAME";

    private final String arguments;
    private final WalletManager walletManager;

    public KeygenCommand(String arguments, WalletManager walletManager) {
        super(HELP_DESCRIPTION);
        this.arguments = arguments;
        this.walletManager = walletManager;
    }

    @Override
    public void execute(Blockchain blockchain, Scanner in) throws Crypto1010Exception {
        String walletName = parseArguments(arguments);
        Wallet wallet = walletManager.findWallet(walletName)
                .orElseThrow(() -> new Crypto1010Exception(WALLET_NOT_FOUND_ERROR));
        wallet.setKeys(Key.generateKeyPair());
        System.out.println(KEY_PAIR_GENERATION_SUCCESSFUL);
    }

    private String parseArguments(String args) throws Crypto1010Exception {
        if (args == null || args.isBlank()) {
            throw new Crypto1010Exception(NAME_ERROR + " " + KEYGEN_FORMAT);
        }

        String trimmedArgs = args.trim();
        if (!trimmedArgs.startsWith("w/")) {
            throw new Crypto1010Exception(INVALID_FORMAT_ERROR);
        }

        String walletName = trimmedArgs.substring(2).trim();
        if (walletName.isEmpty()) {
            throw new Crypto1010Exception(NAME_ERROR + " " + KEYGEN_FORMAT);
        }
        if (walletName.chars().anyMatch(Character::isWhitespace)) {
            throw new Crypto1010Exception(NAME_WHITESPACE_ERROR + " " + KEYGEN_FORMAT);
        }

        return walletName;
    }
}
