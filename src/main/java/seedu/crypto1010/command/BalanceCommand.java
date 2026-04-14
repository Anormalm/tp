package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.WalletManager;
import seedu.crypto1010.ui.CliVisuals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Scanner;

/**
 * Displays the current balance of one wallet.
 */
public class BalanceCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: balance w/WALLET_NAME
            Example: balance w/BobWallet
            
            Displays the balance of wallet up to 8 decimal points
            """;
    private static final String NAME_ERROR = "Error: wallet name cannot be empty.";
    private static final String NAME_WHITESPACE_ERROR = "Error: wallet name must be one word without spaces.";
    private static final String INVALID_FORMAT_ERROR = "Error: Invalid balance format. Use: balance w/WALLET_NAME";
    private static final String WALLET_NOT_FOUND_ERROR = "Error: Wallet not found.";
    private static final String BALANCE_FORMAT = "Use: balance w/WALLET_NAME";

    private final String arguments;
    private final WalletManager walletManager;

    public BalanceCommand(String arguments, WalletManager walletManager) {
        super(HELP_DESCRIPTION);
        this.arguments = arguments;
        this.walletManager = walletManager;
    }

    @Override
    public void execute(Blockchain blockchain, Scanner in) throws Crypto1010Exception {
        String walletName = parseArguments(arguments);
        String trimmedWalletName = walletName.trim();
        if (!walletManager.hasWallet(trimmedWalletName)) {
            throw new Crypto1010Exception(WALLET_NOT_FOUND_ERROR);
        }
        BigDecimal balance = blockchain.getPreciseBalance(trimmedWalletName);

        CliVisuals.printKeyValuePanel("Wallet Balance", List.of(
                List.of("Wallet", trimmedWalletName),
                List.of("Balance", formatBalance(balance))));
    }

    private String parseArguments(String args) throws Crypto1010Exception {
        return CommandParserUtil.parseRequiredWalletNameArgument(
                args,
                INVALID_FORMAT_ERROR,
                NAME_ERROR,
                NAME_WHITESPACE_ERROR,
                BALANCE_FORMAT);
    }

    private String formatBalance(BigDecimal balance) {
        BigDecimal rounded = balance.setScale(8, RoundingMode.HALF_UP);
        // If rounded is zero but actual balance is non-zero, show scientific notation
        if (rounded.compareTo(BigDecimal.ZERO) == 0 && balance.compareTo(BigDecimal.ZERO) != 0) {
            // Use scientific notation with up to 8 decimal places in mantissa
            return String.format("%.8e", balance);
        }
        return rounded.toPlainString();
    }

}
