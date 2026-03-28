package seedu.crypto1010;

import seedu.crypto1010.command.Command;
import seedu.crypto1010.command.ExitCommand;
import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.WalletManager;
import seedu.crypto1010.storage.BlockchainStorage;
import seedu.crypto1010.storage.WalletStorage;

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
        BlockchainStorage blockchainStorage = new BlockchainStorage(Crypto1010.class);
        WalletStorage walletStorage = new WalletStorage(Crypto1010.class);
        Blockchain blockchain = loadBlockchain(blockchainStorage);
        WalletManager walletManager = loadWalletManager(walletStorage);
        Parser parser = new Parser(walletManager);

        while (true) {
            String message;
            try {
                message = in.nextLine().strip();
            } catch (NoSuchElementException e) {
                saveData(blockchainStorage, walletStorage, blockchain, walletManager);
                break;
            }
            try {
                Command c = parser.parse(message);
                String[] components = message.split("\\s+", 2);
                String description = components.length > 1 ? components[1] : "";
                if (c instanceof ExitCommand) {
                    c.execute(description, blockchain);
                    saveData(blockchainStorage, walletStorage, blockchain, walletManager);
                    break;
                }
                c.execute(description, blockchain);
                saveData(blockchainStorage, walletStorage, blockchain, walletManager);
            } catch (Crypto1010Exception e) {
                System.out.println(e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Invalid command. Use: help c/COMMAND");
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
