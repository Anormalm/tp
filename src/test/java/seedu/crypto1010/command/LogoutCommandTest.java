package seedu.crypto1010.command;

import org.junit.jupiter.api.Test;
import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogoutCommandTest {

    @Test
    public void execute_confirmationYes_confirmsLogout() throws Crypto1010Exception {
        LogoutCommand logoutCommand = new LogoutCommand();
        Blockchain blockchain = Blockchain.createDefault();
        Scanner scanner = new Scanner("y\n");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            logoutCommand.execute(blockchain, scanner);
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();
        assertTrue(logoutCommand.isLogoutConfirmed());
        assertTrue(output.contains("Confirm logout? (y/n)"));
        assertTrue(output.contains("Logging out..."));
    }

    @Test
    public void execute_confirmationNo_cancelsLogout() throws Crypto1010Exception {
        LogoutCommand logoutCommand = new LogoutCommand();
        Blockchain blockchain = Blockchain.createDefault();
        Scanner scanner = new Scanner("n\n");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            logoutCommand.execute(blockchain, scanner);
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();
        assertFalse(logoutCommand.isLogoutConfirmed());
        assertTrue(output.contains("Logout cancelled."));
    }

    @Test
    public void execute_invalidConfirmation_repromptsUntilValidInput() throws Crypto1010Exception {
        LogoutCommand logoutCommand = new LogoutCommand();
        Blockchain blockchain = Blockchain.createDefault();
        Scanner scanner = new Scanner("maybe\ny\n");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            logoutCommand.execute(blockchain, scanner);
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();
        assertTrue(logoutCommand.isLogoutConfirmed());
        assertTrue(output.contains("Please enter 'y' to confirm logout or 'n' to stay logged in."));
    }
}
