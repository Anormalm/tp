package seedu.crypto1010;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Crypto1010Test {
    @TempDir
    Path tempDir;

    @Test
    void main_logoutConfirmed_returnsToAccountAccessAndAllowsSwitchingAccounts() {
        String originalDataDir = System.getProperty("crypto1010.dataDir");
        java.io.InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String input = String.join(System.lineSeparator(),
                "register",
                "alice",
                "password1",
                "password1",
                "logout",
                "n",
                "logout",
                "y",
                "register",
                "bob",
                "password2",
                "password2",
                "exit")
                + System.lineSeparator();

        System.setProperty("crypto1010.dataDir", tempDir.toString());
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(outputStream));

        try {
            Crypto1010.main(new String[0]);
        } finally {
            restoreDataDirProperty(originalDataDir);
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String output = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Registration successful. Logged in as alice."));
        assertTrue(output.contains("Logout cancelled."));
        assertTrue(output.contains("Logged out from alice."));
        assertTrue(output.contains("Registration successful. Logged in as bob."));
        assertTrue(output.contains("Logged in as: bob"));
    }

    @Test
    void main_exitInsideTutorial_terminatesProgram() {
        String originalDataDir = System.getProperty("crypto1010.dataDir");
        java.io.InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String input = String.join(System.lineSeparator(),
                "register",
                "alice",
                "password1",
                "password1",
                "tutorial start",
                "exit",
                "list")
                + System.lineSeparator();

        System.setProperty("crypto1010.dataDir", tempDir.toString());
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(outputStream));

        try {
            Crypto1010.main(new String[0]);
        } finally {
            restoreDataDirProperty(originalDataDir);
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String output = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Welcome!"));
        assertTrue(output.contains("Exiting Crypto1010..."));
        assertFalse(output.contains("No wallets found."));
    }

    @Test
    void main_registerBlankUsername_repromptsImmediatelyBeforePassword() {
        String originalDataDir = System.getProperty("crypto1010.dataDir");
        java.io.InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        String input = String.join(System.lineSeparator(),
                "register",
                "",
                "alice",
                "password1",
                "password1",
                "exit")
                + System.lineSeparator();

        System.setProperty("crypto1010.dataDir", tempDir.toString());
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(outputStream));

        try {
            Crypto1010.main(new String[0]);
        } finally {
            restoreDataDirProperty(originalDataDir);
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String output = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Error: Username must be 3-20 characters with no spaces, using letters, "
                + "numbers, '_' or '-'."));
        assertTrue(output.contains("Registration successful. Logged in as alice."));
    }

    private void restoreDataDirProperty(String originalDataDir) {
        if (originalDataDir == null) {
            System.clearProperty("crypto1010.dataDir");
            return;
        }
        System.setProperty("crypto1010.dataDir", originalDataDir);
    }
}
