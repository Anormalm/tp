package seedu.crypto1010;

<<<<<<< HEAD:src/main/java/seedu/duke/Duke.java
import seedu.duke.command.Command;
import seedu.duke.command.ExitCommand;
import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Blockchain;
import seedu.duke.model.WalletManager;
import seedu.duke.storage.BlockchainStorage;
import seedu.duke.storage.WalletStorage;
=======
import seedu.crypto1010.command.Command;
import seedu.crypto1010.command.ExitCommand;
import seedu.crypto1010.exceptions.Exceptions;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.WalletManager;
import seedu.crypto1010.storage.BlockchainStorage;
>>>>>>> upstream/master:src/main/java/seedu/crypto1010/Crypto1010.java

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Crypto1010 {
    private static final String DIVIDER =
            "============================================================";
    private static final String WELCOME_BANNER = """
            Welcome to Crypto1010
            Manage wallets, send transactions, and inspect your blockchain quickly.
            Try: create w/MainWallet | list | help
            """;

    /**
     * Main entry-point for the java.crypto1010.Crypto1010 application.
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        printWelcome();
<<<<<<< HEAD:src/main/java/seedu/duke/Duke.java
        BlockchainStorage blockchainStorage = new BlockchainStorage(Duke.class);
        WalletStorage walletStorage = new WalletStorage(Duke.class);
        Blockchain blockchain = loadBlockchain(blockchainStorage);
        WalletManager walletManager = loadWalletManager(walletStorage);
=======
        BlockchainStorage storage = new BlockchainStorage(Crypto1010.class);
        Blockchain blockchain = loadBlockchain(storage);
        WalletManager walletManager = new WalletManager();
>>>>>>> upstream/master:src/main/java/seedu/crypto1010/Crypto1010.java
        Parser parser = new Parser(walletManager);

        while (true) {
            String message = in.nextLine().strip();
            try {
                Command c = parser.parse(message);
                String[] components = message.split("\\s+", 2);
                String description = components.length > 1 ? components[1] : "";
                if (c instanceof ExitCommand) {
                    saveData(blockchainStorage, walletStorage, blockchain, walletManager);
                    break;
                }
                c.execute(description, blockchain);
                saveData(blockchainStorage, walletStorage, blockchain, walletManager);
            } catch (Exceptions e) {
                System.out.println(e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid Command");
            } catch (NoSuchElementException e) {
                System.out.println("No Input");
            }
        }
    }

    private static void printWelcome() {
        System.out.println(DIVIDER);
        System.out.print(WELCOME_BANNER);
        System.out.println(DIVIDER);
    }

    private static Blockchain loadBlockchain(BlockchainStorage storage) {
        try {
            return storage.load();
        } catch (IOException e) {
            System.out.println("Failed to load blockchain data. Starting with default blockchain.");
            return Blockchain.createDefault();
        }
    }

    private static WalletManager loadWalletManager(WalletStorage storage) {
        try {
            return storage.load();
        } catch (IOException e) {
            System.out.println("Failed to load wallet data. Starting with empty wallet list.");
            return new WalletManager();
        }
    }

    private static void saveData(
            BlockchainStorage blockchainStorage,
            WalletStorage walletStorage,
            Blockchain blockchain,
            WalletManager walletManager) {
        try {
            blockchainStorage.save(blockchain);
        } catch (IOException e) {
            System.out.println("Failed to save blockchain data.");
        }
        try {
            walletStorage.save(walletManager);
        } catch (IOException e) {
            System.out.println("Failed to save wallet data.");
        }
    }
}
