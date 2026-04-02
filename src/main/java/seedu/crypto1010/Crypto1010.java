package seedu.crypto1010;

import seedu.crypto1010.auth.AuthenticationException;
import seedu.crypto1010.auth.AuthenticationService;
import seedu.crypto1010.command.Command;
import seedu.crypto1010.command.ExitCommand;
import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.WalletManager;
import seedu.crypto1010.storage.AccountStorage;
import seedu.crypto1010.storage.BlockchainStorage;
import seedu.crypto1010.storage.WalletStorage;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Crypto1010 {
    private static final String DIVIDER =
            "============================================================";
    private static final String ACCOUNT_ACCESS_HEADER = "Crypto1010 Account Access";
    private static final String ACCOUNT_SELECTION_ERROR =
            "Error: Invalid selection. Choose login, register, or exit.";

    /**
     * Main entry-point for the java.crypto1010.Crypto1010 application.
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        AuthenticationService authenticationService = loadAuthenticationService();
        String accountUsername = authenticateUser(in, authenticationService);
        if (accountUsername == null) {
            return;
        }

        printWelcome(accountUsername);
        BlockchainStorage blockchainStorage = new BlockchainStorage(Crypto1010.class, accountUsername);
        WalletStorage walletStorage = new WalletStorage(Crypto1010.class, accountUsername);
        LoadResult<Blockchain> blockchainLoadResult = loadBlockchain(blockchainStorage);
        LoadResult<WalletManager> walletLoadResult = loadWalletManager(walletStorage);
        Blockchain blockchain = blockchainLoadResult.data();
        WalletManager walletManager = walletLoadResult.data();
        boolean allowBlockchainSave = blockchainLoadResult.loadedSuccessfully();
        boolean allowWalletSave = walletLoadResult.loadedSuccessfully();
        if (!allowBlockchainSave) {
            System.out.println("Blockchain save is disabled to avoid overwriting existing data after load failure.");
        }
        if (!allowWalletSave) {
            System.out.println("Wallet save is disabled to avoid overwriting existing data after load failure.");
        }
        Parser parser = new Parser(walletManager, accountUsername, Crypto1010.class);

        while (true) {
            String message;
            try {
                message = in.nextLine().strip();
            } catch (NoSuchElementException e) {
                saveData(
                        blockchainStorage,
                        walletStorage,
                        blockchain,
                        walletManager,
                        allowBlockchainSave,
                        allowWalletSave);
                break;
            }
            try {
                Command c;
                try {
                    c = parser.parse(message);
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: Invalid command. Use: help");
                    continue;
                }
                if (c instanceof ExitCommand) {
                    c.execute(blockchain, in);
                    saveData(
                            blockchainStorage,
                            walletStorage,
                            blockchain,
                            walletManager,
                            allowBlockchainSave,
                            allowWalletSave);
                    break;
                }
                c.execute(blockchain, in);
                saveData(
                        blockchainStorage,
                        walletStorage,
                        blockchain,
                        walletManager,
                        allowBlockchainSave,
                        allowWalletSave);
            } catch (Crypto1010Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static AuthenticationService loadAuthenticationService() {
        AuthenticationService authenticationService = new AuthenticationService(new AccountStorage(Crypto1010.class));
        try {
            authenticationService.load();
        } catch (IOException e) {
            System.out.println("Failed to load account data. Starting with no registered accounts.");
        }
        return authenticationService;
    }

    private static String authenticateUser(Scanner in, AuthenticationService authenticationService) {
        while (true) {
            printAuthenticationMenu(authenticationService);
            String choice = promptForTrimmedInput(in, "Choice:");
            if (choice == null) {
                return null;
            }

            switch (choice.toLowerCase()) {
            case "1":
            case "login":
                String loggedInUsername = handleLogin(in, authenticationService);
                if (loggedInUsername != null) {
                    return loggedInUsername;
                }
                break;
            case "2":
            case "register":
                String registeredUsername = handleRegistration(in, authenticationService);
                if (registeredUsername != null) {
                    return registeredUsername;
                }
                break;
            case "3":
            case "exit":
                System.out.println("Exiting Crypto1010.");
                return null;
            default:
                System.out.println(ACCOUNT_SELECTION_ERROR);
            }
        }
    }

    private static void printAuthenticationMenu(AuthenticationService authenticationService) {
        System.out.println(DIVIDER);
        System.out.println(ACCOUNT_ACCESS_HEADER);
        if (!authenticationService.hasRegisteredAccounts()) {
            System.out.println("No registered accounts found. Register to get started.");
        }
        System.out.println("1. login");
        System.out.println("2. register");
        System.out.println("3. exit");
        System.out.println(DIVIDER);
    }

    private static String handleLogin(Scanner in, AuthenticationService authenticationService) {
        if (!authenticationService.hasRegisteredAccounts()) {
            System.out.println("Error: No accounts registered yet. Choose register first.");
            return null;
        }

        String username = promptForTrimmedInput(in, "Username:");
        String password = promptForTrimmedInput(in, "Password:");
        if (username == null || password == null) {
            return null;
        }

        try {
            String authenticatedUsername = authenticationService.authenticate(username, password);
            System.out.println("Login successful. Logged in as " + authenticatedUsername + ".");
            return authenticatedUsername;
        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static String handleRegistration(Scanner in, AuthenticationService authenticationService) {
        String username = promptForTrimmedInput(in, "Choose username:");
        String password = promptForTrimmedInput(in, "Choose password:");
        String passwordConfirmation = promptForTrimmedInput(in, "Confirm password:");
        if (username == null || password == null || passwordConfirmation == null) {
            return null;
        }

        try {
            String registeredUsername = authenticationService.register(username, password, passwordConfirmation);
            System.out.println("Registration successful. Logged in as " + registeredUsername + ".");
            return registeredUsername;
        } catch (AuthenticationException | IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static String promptForTrimmedInput(Scanner in, String prompt) {
        System.out.println(prompt);
        try {
            return in.nextLine().strip();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private static void printWelcome(String accountUsername) {
        System.out.println(DIVIDER);
        System.out.println("Welcome to Crypto1010");
        System.out.println("Logged in as: " + accountUsername);
        System.out.println("Manage wallets, send transactions, and inspect your blockchain quickly.");
        System.out.println("Try: create w/MainWallet | list | help");
        System.out.println(DIVIDER);
    }

    private static LoadResult<Blockchain> loadBlockchain(BlockchainStorage storage) {
        try {
            return new LoadResult<>(storage.load(), true);
        } catch (IOException e) {
            System.out.println("Failed to load blockchain data. Starting with default blockchain.");
            return new LoadResult<>(Blockchain.createDefault(), false);
        }
    }

    private static LoadResult<WalletManager> loadWalletManager(WalletStorage storage) {
        try {
            return new LoadResult<>(storage.load(), true);
        } catch (IOException e) {
            System.out.println("Failed to load wallet data. Starting with empty wallet list.");
            return new LoadResult<>(new WalletManager(), false);
        }
    }

    private static void saveData(
            BlockchainStorage blockchainStorage,
            WalletStorage walletStorage,
            Blockchain blockchain,
            WalletManager walletManager,
            boolean allowBlockchainSave,
            boolean allowWalletSave) {
        if (allowBlockchainSave) {
            try {
                blockchainStorage.save(blockchain);
            } catch (IOException e) {
                System.out.println("Failed to save blockchain data.");
            }
        }
        if (allowWalletSave) {
            try {
                walletStorage.save(walletManager);
            } catch (IOException e) {
                System.out.println("Failed to save wallet data.");
            }
        }
    }

    private record LoadResult<T>(T data, boolean loadedSuccessfully) {
    }
}
