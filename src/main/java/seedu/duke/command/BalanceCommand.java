package seedu.duke.command;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Blockchain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BalanceCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: balance w/WALLET_NAME
            Example: balance w/BobWallet
            
            Displays the balance of wallet up to 8 decimal points
            """;
    private static final String NAME_ERROR = "Error: wallet name cannot be empty.";
    private static final String NAME_WHITESPACE_ERROR = "Error: wallet name must be one word without spaces.";
    private static final String INVALID_FORMAT_ERROR = "Error: Invalid balance format. Use: balance w/WALLET_NAME";

    private final String arguments;

    public BalanceCommand(String arguments) {
        super(HELP_DESCRIPTION);
        this.arguments = arguments;
    }

    @Override
    public void execute(String description, Blockchain blockchain) throws Exceptions {
        String walletName = parseArguments(arguments);
        String trimmedWalletName = walletName.trim();
        BigDecimal balance = blockchain.getPreciseBalance(trimmedWalletName);

        System.out.println("Balance of " + trimmedWalletName + ": " + formatBalance(balance));
    }

    private String parseArguments(String args) throws Exceptions {
        if (args == null || args.isBlank()) {
            throw new Exceptions(NAME_ERROR);
        }

        String trimmedArgs = args.trim();
        if (!trimmedArgs.startsWith("w/")) {
            throw new Exceptions(INVALID_FORMAT_ERROR);
        }

        String walletName = trimmedArgs.substring(2).trim();
        if (walletName.isEmpty()) {
            throw new Exceptions(NAME_ERROR);
        }
        if (walletName.chars().anyMatch(Character::isWhitespace)) {
            throw new Exceptions(NAME_WHITESPACE_ERROR);
        }

        return walletName;
    }

    private String formatBalance(BigDecimal balance) {
        return balance.setScale(8, RoundingMode.HALF_UP).toPlainString();
    }

}
