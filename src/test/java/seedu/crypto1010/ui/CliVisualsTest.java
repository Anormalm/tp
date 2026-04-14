package seedu.crypto1010.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

class CliVisualsTest {

    @Test
    void printInfo_controlCharactersAreSanitized() {
        String output = captureOutput(() -> CliVisuals.printInfo("ok\u001B[31mred\nx"));

        assertTrue(output.contains("ok?[31mred?x"));
        assertFalse(output.contains("\u001B"));
    }

    @Test
    void printKeyValuePanel_sanitizesUserProvidedValues() {
        String output = captureOutput(() -> CliVisuals.printKeyValuePanel("Title\u0007", List.of(
                List.of("Wallet", "ali\u001Bce"),
                List.of("Note", "line1\rline2"))));

        assertTrue(output.contains("Title?"));
        assertTrue(output.contains("ali?ce"));
        assertTrue(output.contains("line1?line2"));
        assertFalse(output.contains("\u001B"));
    }

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return outputStream.toString();
    }
}

