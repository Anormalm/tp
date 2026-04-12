package seedu.crypto1010.ui;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.Console;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class InteractiveShell {
    private final Scanner scanner;
    private final LineReader lineReader;

    private InteractiveShell(Scanner scanner, LineReader lineReader) {
        this.scanner = scanner;
        this.lineReader = lineReader;
    }

    public static InteractiveShell create(Scanner scanner) {
        return new InteractiveShell(scanner, createLineReader());
    }

    public String readPlain(String prompt) {
        if (lineReader != null) {
            try {
                String input = lineReader.readLine(prompt + " ");
                return input == null ? null : input.strip();
            } catch (UserInterruptException e) {
                return "";
            } catch (EndOfFileException e) {
                return null;
            } catch (RuntimeException e) {
                // Fall back to scanner input if terminal state changes unexpectedly.
            }
        }

        System.out.println(prompt);
        try {
            return scanner.nextLine().strip();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public String readSecret(String prompt) {
        if (lineReader != null) {
            try {
                String input = lineReader.readLine(prompt + " ", '*');
                return input == null ? null : input.strip();
            } catch (UserInterruptException e) {
                return "";
            } catch (EndOfFileException e) {
                return null;
            } catch (RuntimeException e) {
                // Fall back to plain prompt if masking support is unavailable.
            }
        }

        Console console = System.console();
        if (console != null) {
            char[] input = console.readPassword("%s ", prompt);
            return input == null ? null : new String(input).strip();
        }

        return readPlain(prompt);
    }

    private static LineReader createLineReader() {
        if (System.console() == null) {
            return null;
        }

        Logger.getLogger("org.jline.utils.Log").setLevel(Level.SEVERE);
        try {
            Terminal terminal = TerminalBuilder.builder()
                    .system(true)
                    .jna(true)
                    .jansi(true)
                    .build();
            return LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }
}
