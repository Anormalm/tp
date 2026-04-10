package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.CurrencyCode;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ListCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: list
            
            Lists all the available wallets
            """;
  
    private static final String NO_WALLETS_MESSAGE = "No wallets found.";
    private static final String INVALID_WALLET_DATA_ERROR = "Error: Wallet data is corrupted.";

    private final WalletManager walletManager;
  
    public ListCommand(WalletManager walletManager) {
        super(HELP_DESCRIPTION);
        this.walletManager = Objects.requireNonNull(walletManager);
    }

    @Override
    public void execute(Blockchain blockchain, Scanner in) throws Crypto1010Exception {

        List<Wallet> wallets = walletManager.getWallets();
        if (wallets.isEmpty()) {
            System.out.println(NO_WALLETS_MESSAGE);
            return;
        }

        // Table headers
        String headerFormat = "%-4s %-20s %-12s %-44s";
        String rowFormat =   "%-4d %-20s %-12s %-44s";
        System.out.println();
        System.out.println("Wallets:");
        System.out.println("=".repeat(84));
        System.out.printf(headerFormat + "%n", "No.", "Wallet Name", "Currency", "Address");
        System.out.println("-".repeat(84));
        for (int i = 0; i < wallets.size(); i++) {
            Wallet wallet = wallets.get(i);
            String walletName = validateWalletName(wallet);
            String currency = CurrencyCode.isGeneric(wallet.getCurrencyCode()) ? "-" : wallet.getCurrencyCode();
            String address;
            try {
                address = wallet.getAddress();
            } catch (Crypto1010Exception e) {
                address = e.getMessage();
            }
            System.out.printf(rowFormat + "%n", i + 1, walletName, currency, address);
        }
        System.out.println("=".repeat(84));
    }

    private String validateWalletName(Wallet wallet) throws Crypto1010Exception {
        if (wallet == null || wallet.getName() == null || wallet.getName().isBlank()) {
            throw new Crypto1010Exception(INVALID_WALLET_DATA_ERROR);
        }
        return wallet.getName();
    }
}
