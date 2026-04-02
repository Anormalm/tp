package seedu.crypto1010.command;

import org.junit.jupiter.api.Test;
import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TutorialCommandTest {

    @Test
    public void execute_startTutorial_printsWelcomeAndFirstInstruction() throws Crypto1010Exception {
        String simulatedInput = "create w/alice\n" + "tutorial exit\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));
        TutorialCommand tutorialCommand = new TutorialCommand("start");
        Blockchain blockchain = Blockchain.createDefault();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            tutorialCommand.execute(blockchain, scanner);
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();

        assertTrue(output.contains("Welcome to the tutorial!"));
        assertTrue(output.contains("Enter the following command:"));
        assertTrue(output.contains("create w/alice"));
        assertTrue(output.contains("Exiting tutorial..."));
    }

    @Test
    public void execute_invalidFormat_throwsCrypto1010Exception() {
        TutorialCommand tutorialCommand = new TutorialCommand("invalid");
        Blockchain blockchain = Blockchain.createDefault();
        Scanner scanner = new Scanner(System.in);

        Crypto1010Exception thrown = assertThrows(
                Crypto1010Exception.class,
                () -> tutorialCommand.execute(blockchain, scanner)
        );

        assertEquals("Error: Invalid tutorial format. Use tutorial start", thrown.getMessage());
    }

    @Test
    public void execute_exitImmediately_printsExitMessage() throws Crypto1010Exception {
        String simulatedInput = "tutorial exit\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));
        TutorialCommand tutorialCommand = new TutorialCommand("start");
        Blockchain blockchain = Blockchain.createDefault();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            tutorialCommand.execute(blockchain, scanner);
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();
        assertTrue(output.contains("Exiting tutorial..."));
    }
}
