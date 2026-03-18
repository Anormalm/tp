package seedu.duke.command;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Blockchain;
import seedu.duke.model.Wallet;
import seedu.duke.model.WalletManager;

public class CreateCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: create w/WALLET_NAME
            Example: create w/Bob's Wallet
            
            Creates a wallet with the associated NAME
            NAME must consist only of alphanumeric characters
            """;
  
    private static final String NAME_ERROR = "Error: wallet name cannot be empty.";
    private static final String DUPLICATE_ERROR = "Error: wallet name already exists.";
    private static final String INVALID_FORMAT_ERROR = "Error: Invalid create format. Use: create w/WALLET_NAME";

    private final String arguments;
    private final WalletManager walletManager;

    public CreateCommand(String arguments, WalletManager walletManager) {
        super(HELP_DESCRIPTION);
        this.arguments = arguments;
        this.walletManager = walletManager;
    }

    @Override
    public void execute(String description, Blockchain blockchain) throws Exceptions {
        String walletName = parseArguments(arguments);
        if (walletName == null || walletName.isBlank()) {
            throw new Exceptions(NAME_ERROR);
        }

        String trimmedWalletName = walletName.trim();
        if (walletManager.hasWallet(trimmedWalletName)) {
            throw new Exceptions(DUPLICATE_ERROR);
        }

        Wallet wallet = walletManager.createWallet(trimmedWalletName);
        System.out.println("Wallet created: " + wallet.getName());
    }

    private String parseArguments(String args) throws Exceptions {
        if (args == null || args.isBlank()) {
            throw new Exceptions(NAME_ERROR);
        }

        String trimmedArgs = args.trim();
        if (!trimmedArgs.startsWith("w/")) {
            throw new Exceptions(INVALID_FORMAT_ERROR);
        }

        return trimmedArgs.substring(2).trim();
    }
}
