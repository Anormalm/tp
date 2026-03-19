package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Exceptions;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.Key;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

public class KeygenCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: keygen w/WALLET_NAME
            Example: keygen w/main_wallet
            
            Generates and displays key pair for new wallet, or regenerates for existing wallet
            Displays the process of creating a key pair
            """;
    private static final String INVALID_WALLET_NUMBER_ERROR = "Error: Invalid number of args";
    private static final String INVALID_FORMAT_ERROR = "Error: Invalid keygen format. Use: keygen w/WALLET_NAME";
    private static final String NAME_ERROR = "Error: wallet name cannot be empty.";
    private static final String WALLET_NOT_FOUND_ERROR = "Error: Wallet not found";
    private static final String KEY_PAIR_GENERATION_SUCCESSFUL = "Key pair successfully generated";

    private final String arguments;
    private final WalletManager walletManager;

    public KeygenCommand(String arguments, WalletManager walletManager) {
        super(HELP_DESCRIPTION);
        this.arguments = arguments;
        this.walletManager = walletManager;
    }

    @Override
    public void execute(String description, Blockchain blockchain) throws Exceptions {
        String walletName = parseArguments(arguments);
        Wallet wallet = walletManager.findWallet(walletName)
                .orElseThrow(() -> new Exceptions(WALLET_NOT_FOUND_ERROR));
        wallet.setKeys(Key.generateKeyPair());
        System.out.println(KEY_PAIR_GENERATION_SUCCESSFUL);
    }

    private String parseArguments(String args) throws Exceptions {
        if (args == null || args.isBlank()) {
            throw new Exceptions(INVALID_FORMAT_ERROR);
        }

        String[] parts = args.trim().split("\\s+");
        if (parts.length != 1) {
            throw new Exceptions(INVALID_WALLET_NUMBER_ERROR);
        } else if (parts[0].startsWith("w/")) {
            String walletName = parts[0].substring(2).trim();
            if (walletName.isEmpty()) {
                throw new Exceptions(NAME_ERROR);
            }
            return walletName;
        } else {
            throw new Exceptions(INVALID_FORMAT_ERROR);
        }
    }
}
