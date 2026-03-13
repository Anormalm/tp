package seedu.duke.command;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Blockchain;
import seedu.duke.model.Wallet;
import seedu.duke.model.WalletManager;

import java.util.List;
import java.util.Objects;

public class ListCommand extends Command {
    private static final String NO_WALLETS_MESSAGE = "No wallets found.";

    private final WalletManager walletManager;

    public ListCommand(WalletManager walletManager) {
        this.walletManager = Objects.requireNonNull(walletManager);
    }

    @Override
    public void execute(Blockchain blockchain) throws Exceptions {
        List<Wallet> wallets = walletManager.getWallets();
        if (wallets.isEmpty()) {
            System.out.println(NO_WALLETS_MESSAGE);
            return;
        }

        System.out.println("Wallets:");
        for (int i = 0; i < wallets.size(); i++) {
            System.out.println((i + 1) + ". " + wallets.get(i).getName());
        }
    }
}
