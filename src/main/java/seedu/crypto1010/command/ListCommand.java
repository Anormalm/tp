package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Exceptions;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

import java.util.List;
import java.util.Objects;

public class ListCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: list
            
            Lists all the available wallets
            """;
  
    private static final String NO_WALLETS_MESSAGE = "No wallets found.";
    private static final String INVALID_FORMAT_ERROR = "Error: Invalid list format. Use: list";
    private static final String GENERATE_KEYS_FIRST = "Generate keys first";

    private final WalletManager walletManager;
  
    public ListCommand(WalletManager walletManager) {
        super(HELP_DESCRIPTION);
        this.walletManager = Objects.requireNonNull(walletManager);
    }

    @Override
    public void execute(String description, Blockchain blockchain) throws Exceptions {
        if (description != null && !description.isBlank()) {
            throw new Exceptions(INVALID_FORMAT_ERROR);
        }

        List<Wallet> wallets = walletManager.getWallets();
        if (wallets.isEmpty()) {
            System.out.println(NO_WALLETS_MESSAGE);
            return;
        }

        System.out.println("Wallets:");
        for (int i = 0; i < wallets.size(); i++) {
            Wallet wallet = wallets.get(i);
            System.out.print((i + 1) + ". " + wallet.getName() + " | Address: ");
            try {
                System.out.println(wallet.getAddress());
            } catch (Exceptions e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
