package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;
import seedu.crypto1010.ui.CliVisuals;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class HistoryCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: history w/WALLET_NAME
            Example: history w/BobWallet

            Displays the recorded send history of the wallet
            Shows outgoing transactions in chronological order
            """;
    private static final String NAME_ERROR = "Error: wallet name cannot be empty.";
    private static final String NAME_WHITESPACE_ERROR = "Error: wallet name must be one word without spaces.";
    private static final String INVALID_FORMAT_ERROR = "Error: Invalid history format. Use: history w/WALLET_NAME";
    private static final String WALLET_NOT_FOUND_ERROR = "Error: Wallet not found.";
    private static final String HISTORY_FORMAT = "Use: history w/WALLET_NAME";

    private final String arguments;
    private final WalletManager walletManager;

    public HistoryCommand(String arguments, WalletManager walletManager) {
        super(HELP_DESCRIPTION);
        this.arguments = arguments;
        this.walletManager = Objects.requireNonNull(walletManager);
    }

    @Override
    public void execute(Blockchain blockchain, Scanner in) throws Crypto1010Exception {
        String walletName = parseArguments(arguments);
        Wallet wallet = walletManager.findWallet(walletName)
                .orElseThrow(() -> new Crypto1010Exception(WALLET_NOT_FOUND_ERROR));

        List<String> transactionHistory = wallet.getTransactionHistory();
        if (transactionHistory.isEmpty()) {
            System.out.println("No transaction history found for " + wallet.getName() + ".");
            return;
        }

        List<List<String>> rows = new ArrayList<>();
        for (int i = 0; i < transactionHistory.size(); i++) {
            rows.add(List.of(String.valueOf(i + 1), transactionHistory.get(i)));
        }
        CliVisuals.printTable("Transaction History for " + wallet.getName(), List.of("No.", "Transaction"), rows);
    }

    private String parseArguments(String args) throws Crypto1010Exception {
        return CommandParserUtil.parseRequiredWalletNameArgument(
                args,
                INVALID_FORMAT_ERROR,
                NAME_ERROR,
                NAME_WHITESPACE_ERROR,
                HISTORY_FORMAT);
    }
}
