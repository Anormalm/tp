package seedu.crypto1010;

import seedu.crypto1010.auth.AuthenticationException;
import seedu.crypto1010.auth.AuthenticationService;
import seedu.crypto1010.command.Command;
import seedu.crypto1010.command.ExitCommand;
import seedu.crypto1010.command.LogoutCommand;
import seedu.crypto1010.command.TutorialCommand;
import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.WalletManager;
import seedu.crypto1010.storage.AccountStorage;
import seedu.crypto1010.storage.BlockchainStorage;
import seedu.crypto1010.storage.WalletStorage;
import seedu.crypto1010.ui.CliVisuals;
import seedu.crypto1010.ui.InteractiveShell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Crypto1010 {
    private static final Logger LOGGER = Logger.getLogger(Crypto1010.class.getName());
    private static final String ACCOUNT_ACCESS_HEADER = "Crypto1010 Account Access";
    private static final String ACCOUNT_SELECTION_ERROR =
            "Error: Invalid selection. Choose login, register, or exit.";

    /**
     * Main entry-point for the java.crypto1010.Crypto1010 application.
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        InteractiveShell shell = InteractiveShell.create(in);
        AuthenticationService authenticationService = loadAuthenticationService();
        while (true) {
            String accountUsername = authenticateUser(shell, authenticationService);
            if (accountUsername == null) {
                return;
            }

            SessionOutcome sessionOutcome = runAuthenticatedSession(in, accountUsername);
            if (sessionOutcome == SessionOutcome.EXIT) {
                return;
            }
        }
    }

    private static SessionOutcome runAuthenticatedSession(Scanner in, String accountUsername) {
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
                return SessionOutcome.EXIT;
            }
            try {
                Command c;
                try {
                    c = parser.parse(message);
                } catch (IllegalArgumentException e) {
                    LOGGER.log(Level.FINE, "Command parse failed for input: " + message, e);
                    System.out.println("Error: Invalid command. Use: help");
                    continue;
                }
                long startNs = System.nanoTime();
                if (c instanceof ExitCommand) {
                    c.execute(blockchain, in);
                    long durationMs = (System.nanoTime() - startNs) / 1_000_000;
                    LOGGER.fine(() -> "Command executed successfully: exit (" + durationMs + " ms)");
                    saveData(
                            blockchainStorage,
                            walletStorage,
                            blockchain,
                            walletManager,
                            allowBlockchainSave,
                            allowWalletSave);
                    return SessionOutcome.EXIT;
                }

                c.execute(blockchain, in);
                if (c instanceof TutorialCommand tutorialCommand && tutorialCommand.isExitRequested()) {
                    saveData(
                            blockchainStorage,
                            walletStorage,
                            blockchain,
                            walletManager,
                            allowBlockchainSave,
                            allowWalletSave);
                    return SessionOutcome.EXIT;
                }
                long durationMs = (System.nanoTime() - startNs) / 1_000_000;
                String commandName = c.getClass().getSimpleName();
                LOGGER.fine(() -> "Command executed successfully: " + commandName + " (" + durationMs + " ms)");
                saveData(
                        blockchainStorage,
                        walletStorage,
                        blockchain,
                        walletManager,
                        allowBlockchainSave,
                        allowWalletSave);

                if (c instanceof LogoutCommand logoutCommand && logoutCommand.isLogoutConfirmed()) {
                    System.out.println("Logged out from " + accountUsername + ".");
                    return SessionOutcome.LOGOUT;
                }
            } catch (Crypto1010Exception e) {
                LOGGER.log(Level.FINE, "Command execution failed.", e);
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

    private static String authenticateUser(InteractiveShell shell, AuthenticationService authenticationService) {
        while (true) {
            printAuthenticationMenu(authenticationService);
            String choice = shell.readPlain("Choice:");
            if (choice == null) {
                return null;
            }

            switch (choice.toLowerCase()) {
            case "1":
            case "login":
                String loggedInUsername = handleLogin(shell, authenticationService);
                if (loggedInUsername != null) {
                    return loggedInUsername;
                }
                break;
            case "2":
            case "register":
                String registeredUsername = handleRegistration(shell, authenticationService);
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
        List<String> lines = new ArrayList<>();
        if (!authenticationService.hasRegisteredAccounts()) {
            lines.add("No registered accounts found. Register to get started.");
        }
        lines.add("1. login");
        lines.add("2. register");
        lines.add("3. exit");
        CliVisuals.printLegacySection(ACCOUNT_ACCESS_HEADER, lines);
    }

    private static String handleLogin(InteractiveShell shell, AuthenticationService authenticationService) {
        if (!authenticationService.hasRegisteredAccounts()) {
            System.out.println("Error: No accounts registered yet. Choose register first.");
            return null;
        }

        String username = shell.readPlain("Username:");
        String password = shell.readSecret("Password:");
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

    private static String handleRegistration(InteractiveShell shell, AuthenticationService authenticationService) {
        String username;
        while (true) {
            username = shell.readPlain("Choose username:");
            if (username == null) {
                return null;
            }
            try {
                username = authenticationService.validateNewUsername(username);
                break;
            } catch (AuthenticationException e) {
                System.out.println(e.getMessage());
            }
        }

        String password = shell.readSecret("Choose password:");
        String passwordConfirmation = shell.readSecret("Confirm password:");
        if (password == null || passwordConfirmation == null) {
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

    private static void printWelcome(String accountUsername) {
        CliVisuals.printLegacySection("Welcome to Crypto1010", List.of(
                "Logged in as: " + accountUsername,
                "Manage wallets, send transactions, and inspect your blockchain quickly.",
                "Try: create w/MainWallet | list | help"));
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

    private enum SessionOutcome {
        LOGOUT,
        EXIT
    }

    private record LoadResult<T>(T data, boolean loadedSuccessfully) {
    }
}
