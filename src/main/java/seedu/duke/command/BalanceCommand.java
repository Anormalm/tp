package seedu.duke.command;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Block;
import seedu.duke.model.Blockchain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BalanceCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: balance NAME
            Displays the balance of wallet up to 8 decimal points
            """;
    private static final String NAME_ERROR = "Error: wallet name cannot be empty.";
    private static final Pattern TRANSACTION_PATTERN =
            Pattern.compile("^(.+?)\\s*->\\s*(.+?)\\s*:\\s*([+-]?\\d+(?:\\.\\d+)?)$");

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
        BigDecimal balance = BigDecimal.ZERO;
        for (int i = 0; i < blockchain.size(); i++) {
            Block block = blockchain.getBlock(i);
            for (String transaction : block.getTransactions()) {
                balance = balance.add(getBalanceDelta(trimmedWalletName, transaction));
            }
        }

        System.out.println("Balance of " + trimmedWalletName + ": " + formatBalance(balance));
    }

    private BigDecimal getBalanceDelta(String trimmedWalletName, String transaction) {
        Matcher matcher = TRANSACTION_PATTERN.matcher(transaction);
        if (!matcher.matches()) {
            return BigDecimal.ZERO;
        }

        String sender = matcher.group(1).trim();
        String receiver = matcher.group(2).trim();
        BigDecimal amount = new BigDecimal(matcher.group(3));
        BigDecimal delta = BigDecimal.ZERO;
        if (sender.equalsIgnoreCase(trimmedWalletName)) {
            delta = delta.subtract(amount);
        }
        if (receiver.equalsIgnoreCase(trimmedWalletName)) {
            delta = delta.add(amount);
        }
        return delta;
    }

    private String formatBalance(BigDecimal balance) {
        return balance.setScale(8, RoundingMode.HALF_UP).toPlainString();
    }
}
