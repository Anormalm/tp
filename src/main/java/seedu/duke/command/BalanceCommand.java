package seedu.duke.command;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Blockchain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BalanceCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: balance w/WALLET_NAME
            Example: balance w/Bob's Wallet
            
            Displays the balance of wallet up to 8 decimal points
            """;
    private static final String NAME_ERROR = "Error: wallet name cannot be empty.";
    private static final String INVALID_FORMAT_ERROR = "Error: Invalid create format. Use: balance n/WALLET_NAME";

    private final String walletName;

    public BalanceCommand(String walletName) {
        super(HELP_DESCRIPTION);
        this.walletName = walletName;
    }

    @Override
    public void execute(String description, Blockchain blockchain) throws Exceptions {
        if (walletName == null || walletName.isBlank()) {
            System.out.println(NAME_ERROR);
            return;
        }
        String trimmedWalletName = walletName.trim();
        BigDecimal balance = blockchain.getPreciseBalance(trimmedWalletName);

        System.out.println("Balance of " + trimmedWalletName + ": " + formatBalance(balance));
    }

    private String formatBalance(BigDecimal balance) {
        return balance.setScale(8, RoundingMode.HALF_UP).toPlainString();
    }

}
