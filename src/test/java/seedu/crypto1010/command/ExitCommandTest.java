package seedu.crypto1010.command;

import org.junit.jupiter.api.Test;
import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExitCommandTest {

    @Test
    public void execute_printsExitMessage() throws Crypto1010Exception {
        ExitCommand exitCommand = new ExitCommand();
        Blockchain blockchain = Blockchain.createDefault();
        Scanner scanner = new Scanner(System.in); // Scanner is not used here

        // Capture system output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            exitCommand.execute(blockchain, scanner);
        } finally {
            System.setOut(originalOut);
        }

        String output = outputStream.toString();
        assertTrue(output.contains("Exiting Crypto1010..."));
    }
}
