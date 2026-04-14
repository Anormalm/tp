package seedu.crypto1010.ui;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.Console;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wraps console input so the application can use JLine when available and fall back otherwise.
 */
public final class InteractiveShell {
    private final Scanner scanner;
    private final LineReader lineReader;

    private InteractiveShell(Scanner scanner, LineReader lineReader) {
        this.scanner = scanner;
        this.lineReader = lineReader;
    }

    public static InteractiveShell create(Scanner scanner) {
        return new InteractiveShell(scanner, createLineReader(null));
    }

    public static InteractiveShell create(Scanner scanner, List<String> suggestions) {
        return new InteractiveShell(scanner, createLineReader(new StringsCompleter(suggestions == null
                ? List.of()
                : suggestions)));
    }

    public static InteractiveShell create(Scanner scanner, Completer completer) {
        return new InteractiveShell(scanner, createLineReader(completer));
    }

    /**
     * Reads an unmasked line of input, preferring JLine but falling back to {@link Scanner} when needed.
     */
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

    /**
     * Reads a masked password when the terminal supports it, otherwise falls back to plain input.
     */
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

    /**
     * Reads a command line without printing an additional fallback prompt.
     */
    public String readCommand(String prompt) {
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

        try {
            return scanner.nextLine().strip();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /**
     * Builds a JLine reader only when the process is attached to a capable interactive console.
     */
    private static LineReader createLineReader(Completer completer) {
        if (System.console() == null) {
            return null;
        }

        Logger.getLogger("org.jline").setLevel(Level.SEVERE);
        Logger.getLogger("org.jline.utils.Log").setLevel(Level.SEVERE);
        try {
            Terminal terminal = TerminalBuilder.builder()
                    .system(true)
                    .build();
            if ("dumb".equalsIgnoreCase(terminal.getType())) {
                return null;
            }
            return LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(completer == null ? new StringsCompleter(new String[0]) : completer)
                    .build();
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }
}
