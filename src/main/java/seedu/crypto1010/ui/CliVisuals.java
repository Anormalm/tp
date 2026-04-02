package seedu.crypto1010.ui;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import seedu.crypto1010.command.CommandWord;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public final class CliVisuals {
    private static final int PANEL_WIDTH = 92;
    private static final int MAX_RESULT_LINES = 16;
    private static ThemePreset themePreset = ThemePreset.NEON;
    private static boolean plainMode;

    private CliVisuals() {
    }

    public static void install() {
        AnsiConsole.systemInstall();
    }

    public static void uninstall() {
        AnsiConsole.systemUninstall();
    }

    public static void printAppBanner() {
        String[] lines = {
            "  ______                 __         ___    ___    __    ___  ",
            " / ____/______  ______  / /_____   <  /   / _ |  / /   / _ \\ ",
            "/ /   / ___/ / / / __ \\/ __/ __ \\  / /   / __ | / /__ / // / ",
            "/ /___/ /  / /_/ / /_/ / /_/ /_/ / / /   /_/ |_|/____/____/ ",
            "\\____/_/   \\__, / .___/\\__/\\____/ /_/    \\___/              ",
            "          /____/_/                                           "
        };
        printLines(lines, Role.TITLE);
        printHint("Theme: " + getThemeName() + (plainMode ? " | Plain mode ON" : " | Plain mode OFF"));
    }

    public static void printWelcomePanel(String username) {
        List<String> lines = List.of(
                "Welcome to Crypto1010",
                "Logged in as: " + username,
                "",
                "Shortcuts: /ls /vc /q /kb",
                "Controls : :help :theme :mode :timeline :search :plain :confirm"
        );
        printCard("SESSION READY", CardType.SUCCESS, lines);
    }

    public static void printStatusDashboard(String username, WalletManager walletManager, Blockchain blockchain) {
        int walletCount = walletManager.getWallets().size();
        int blockCount = blockchain.size();
        int transactionCount = blockchain.getBlocks().stream().mapToInt(block -> block.getTransactions().size()).sum();
        String walletPeek = walletManager.getWallets().stream()
                .map(Wallet::getName)
                .sorted(String::compareToIgnoreCase)
                .limit(4)
                .collect(Collectors.joining(", "));
        if (walletPeek.isBlank()) {
            walletPeek = "(none)";
        }

        List<String> lines = new ArrayList<>();
        lines.add("Account      : " + username);
        lines.add("Wallets      : " + walletCount);
        lines.add("Blocks       : " + blockCount);
        lines.add("Transactions : " + transactionCount);
        lines.add("Wallet Peek  : " + walletPeek);
        lines.add("Commands     : " + commandPreview());
        printCard("LIVE DASHBOARD", CardType.INFO, lines);
    }

    public static void printCommandExplorer() {
        List<String> commands = Arrays.stream(CommandWord.values())
                .map(command -> command.getCommand() + " - " + command.getDescription())
                .sorted(Comparator.naturalOrder())
                .toList();
        printCard("COMMAND EXPLORER", CardType.HIGHLIGHT, commands);
    }

    public static void printUiControlGuide() {
        List<String> lines = List.of(
                "Slash aliases : /ls /vc /q /kb /bal /snd /xs",
                "UI controls   : :help :theme <name> :plain <on|off|toggle>",
                "Flow controls : :mode <normal|compose|search|history> :confirm <on|off|toggle>",
                "Diagnostics   : :status :timeline :search <text>",
                "Workspace     : :home :clear :exit",
                "Keyboard      : TAB autocomplete, Up/Down history, Ctrl+D exit"
        );
        printCard("INTERACTION MAP", CardType.INFO, lines);
    }

    public static void printAuthMenu(boolean hasAccounts) {
        List<String> lines = new ArrayList<>();
        if (!hasAccounts) {
            lines.add("No registered accounts found. Register to get started.");
            lines.add("");
        }
        lines.add("1) login");
        lines.add("2) register");
        lines.add("3) exit");
        printCard("ACCOUNT ACCESS", CardType.INFO, lines);
    }

    public static String buildPrompt(String accountUsername, String modeName) {
        String account = accountUsername == null || accountUsername.isBlank() ? "guest" : accountUsername;
        String mode = modeName.toLowerCase(Locale.ROOT);
        return switch (themePreset) {
        case NEON -> color("[" + mode + "] ", Role.MUTED)
                + color(account, Role.HIGHLIGHT)
                + color("@crypto1010", Role.MUTED)
                + color(" > ", Role.PRIMARY);
        case CLASSIC -> color("{" + mode + "} ", Role.TITLE)
                + color(account, Role.PRIMARY)
                + color("::crypto1010", Role.MUTED)
                + color(" $ ", Role.SUCCESS);
        case MINIMAL -> color(mode + " ", Role.MUTED)
                + color(account, Role.PRIMARY)
                + color(" > ", Role.HIGHLIGHT);
        case HIGH_CONTRAST -> color("<" + mode.toUpperCase(Locale.ROOT) + "> ", Role.TITLE)
                + color(account.toUpperCase(Locale.ROOT), Role.HIGHLIGHT)
                + color(" # ", Role.ERROR);
        };
    }

    public static String buildRightStatus(
            String modeName,
            WalletManager walletManager,
            Blockchain blockchain,
            String lastSaveAt,
            boolean confirmationsEnabled) {
        int walletCount = walletManager.getWallets().size();
        int blockCount = blockchain.size();
        int transactionCount = blockchain.getBlocks().stream().mapToInt(block -> block.getTransactions().size()).sum();
        String status = "mode:" + modeName.toLowerCase(Locale.ROOT)
                + " | wallets:" + walletCount
                + " | blocks:" + blockCount
                + " | tx:" + transactionCount
                + " | confirm:" + (confirmationsEnabled ? "on" : "off")
                + " | save:" + lastSaveAt
                + " | theme:" + getThemeName();
        if (themePreset == ThemePreset.MINIMAL) {
            status = "m=" + modeName.toLowerCase(Locale.ROOT)
                    + " w=" + walletCount
                    + " b=" + blockCount
                    + " tx=" + transactionCount
                    + " c=" + (confirmationsEnabled ? "on" : "off")
                    + " save=" + lastSaveAt;
        }
        return color(status, Role.MUTED);
    }

    public static void printCommandResultCard(String commandText, boolean success, long durationMs,
                                              List<String> outputLines) {
        List<String> lines = new ArrayList<>();
        lines.add("Command : " + (commandText == null ? "(unknown)" : commandText));
        lines.add("Status  : " + (success ? "SUCCESS" : "FAILED"));
        lines.add("Latency : " + durationMs + " ms");
        lines.add("");
        if (outputLines == null || outputLines.isEmpty()) {
            lines.add("(No output)");
        } else {
            lines.addAll(limitLines(outputLines, MAX_RESULT_LINES));
        }
        printCard("EXECUTION RESULT", success ? CardType.SUCCESS : CardType.ERROR, lines);
    }

    public static void printTimeline(Deque<String> timelineEntries) {
        if (timelineEntries.isEmpty()) {
            printHint("Timeline is empty.");
            return;
        }
        printCard("RECENT ACTIONS", CardType.HIGHLIGHT, new ArrayList<>(timelineEntries));
    }

    public static void printSearchResults(String query, List<String> matches) {
        if (matches.isEmpty()) {
            printHint("No history results for `" + query + "`.");
            return;
        }
        List<List<String>> rows = new ArrayList<>();
        for (int i = 0; i < matches.size(); i++) {
            rows.add(List.of(String.valueOf(i + 1), matches.get(i)));
        }
        printTable("HISTORY SEARCH: " + query, List.of("#", "Command"), rows);
    }

    public static void printTable(String title, List<String> headers, List<List<String>> rows) {
        if (headers.isEmpty()) {
            return;
        }
        int[] widths = new int[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            widths[i] = headers.get(i).length();
        }
        for (List<String> row : rows) {
            for (int i = 0; i < Math.min(widths.length, row.size()); i++) {
                widths[i] = Math.max(widths[i], row.get(i).length());
            }
        }
        List<String> lines = new ArrayList<>();
        lines.add(joinRow(headers, widths));
        lines.add(separator(widths));
        for (List<String> row : rows) {
            lines.add(joinRow(row, widths));
        }
        printCard(title, CardType.INFO, lines);
    }

    public static void clearScreen() {
        System.out.print("\u001B[H\u001B[2J");
        System.out.flush();
    }

    public static void printInfo(String message) {
        System.out.println(color(message, Role.PRIMARY));
    }

    public static void printSuccess(String message) {
        System.out.println(color(message, Role.SUCCESS));
    }

    public static void printWarning(String message) {
        System.out.println(color(message, Role.WARNING));
    }

    public static void printError(String message) {
        System.out.println(color(message, Role.ERROR));
    }

    public static void printHint(String message) {
        System.out.println(color(message, Role.MUTED));
    }

    public static void printSectionBreak() {
        System.out.println(color("~".repeat(PANEL_WIDTH), Role.MUTED));
    }

    public static boolean setTheme(String themeName) {
        for (ThemePreset preset : ThemePreset.values()) {
            if (preset.name.equalsIgnoreCase(themeName)) {
                themePreset = preset;
                return true;
            }
        }
        return false;
    }

    public static String themeNames() {
        return Arrays.stream(ThemePreset.values())
                .map(theme -> theme.name)
                .collect(Collectors.joining(", "));
    }

    public static String getThemeName() {
        return themePreset.name;
    }

    public static void setPlainMode(boolean enabled) {
        plainMode = enabled;
    }

    public static boolean isPlainMode() {
        return plainMode;
    }

    private static void printCard(String title, CardType cardType, List<String> lines) {
        Role role = switch (cardType) {
        case INFO -> Role.PRIMARY;
        case SUCCESS -> Role.SUCCESS;
        case WARNING -> Role.WARNING;
        case ERROR -> Role.ERROR;
        case HIGHLIGHT -> Role.HIGHLIGHT;
        };
        printPanel(title, lines, role);
    }

    private static void printLines(String[] lines, Role role) {
        for (String line : lines) {
            System.out.println(color(line, role));
        }
    }

    private static void printPanel(String title, List<String> lines, Role role) {
        if (themePreset == ThemePreset.MINIMAL) {
            System.out.println(color("[" + title + "]", role));
            for (String line : lines) {
                for (String wrapped : wrapLine(line, PANEL_WIDTH - 4)) {
                    System.out.println(color("  - " + wrapped, role));
                }
            }
            return;
        }

        PanelFrame frame = frameForTheme();
        String top = frame.corner() + String.valueOf(frame.horizontal()).repeat(PANEL_WIDTH - 2) + frame.corner();
        String divider = frame.vertical()
                + String.valueOf(frame.horizontal()).repeat(PANEL_WIDTH - 2)
                + frame.vertical();
        System.out.println(color(top, role));
        System.out.println(color(
                frame.vertical() + " " + padRight(title, PANEL_WIDTH - 4) + " " + frame.vertical(),
                role
        ));
        System.out.println(color(divider, role));
        for (String line : lines) {
            for (String wrapped : wrapLine(line, PANEL_WIDTH - 4)) {
                String row = frame.vertical() + " " + padRight(wrapped, PANEL_WIDTH - 4) + " " + frame.vertical();
                System.out.println(color(row, role));
            }
        }
        System.out.println(color(top, role));
    }

    private static List<String> wrapLine(String line, int width) {
        String safe = line == null ? "" : line;
        if (safe.length() <= width) {
            return List.of(safe);
        }
        List<String> wrapped = new ArrayList<>();
        String remaining = safe;
        while (remaining.length() > width) {
            int breakIndex = remaining.lastIndexOf(' ', width);
            if (breakIndex <= 0) {
                breakIndex = width;
            }
            wrapped.add(remaining.substring(0, breakIndex).stripTrailing());
            remaining = remaining.substring(breakIndex).stripLeading();
        }
        if (!remaining.isEmpty()) {
            wrapped.add(remaining);
        }
        return wrapped;
    }

    private static String padRight(String value, int width) {
        if (value.length() >= width) {
            return value.substring(0, width);
        }
        return value + " ".repeat(width - value.length());
    }

    private static List<String> limitLines(List<String> lines, int maxLines) {
        List<String> normalized = lines.stream().filter(Objects::nonNull).toList();
        if (normalized.size() <= maxLines) {
            return normalized;
        }
        List<String> out = new ArrayList<>(normalized.subList(0, maxLines));
        out.add("... (" + (normalized.size() - maxLines) + " more line(s))");
        return out;
    }

    private static String joinRow(List<String> row, int[] widths) {
        List<String> cells = new ArrayList<>();
        for (int i = 0; i < widths.length; i++) {
            String value = i < row.size() ? row.get(i) : "";
            cells.add(padRight(value, widths[i]));
        }
        return String.join(" | ", cells);
    }

    private static String separator(int[] widths) {
        List<String> segments = new ArrayList<>();
        for (int width : widths) {
            segments.add("-".repeat(width));
        }
        return String.join("-+-", segments);
    }

    private static String color(String text, Role role) {
        if (plainMode) {
            return text;
        }
        return switch (themePreset) {
        case NEON -> neonColor(role, text);
        case CLASSIC -> classicColor(role, text);
        case MINIMAL -> minimalColor(role, text);
        case HIGH_CONTRAST -> highContrastColor(role, text);
        };
    }

    private static String neonColor(Role role, String text) {
        return switch (role) {
        case TITLE -> Ansi.ansi().fgBrightMagenta().a(text).reset().toString();
        case PRIMARY -> Ansi.ansi().fgBrightCyan().a(text).reset().toString();
        case SUCCESS -> Ansi.ansi().fgBrightGreen().a(text).reset().toString();
        case WARNING -> Ansi.ansi().fgBrightYellow().a(text).reset().toString();
        case ERROR -> Ansi.ansi().fgBrightRed().a(text).reset().toString();
        case HIGHLIGHT -> Ansi.ansi().fgBrightBlue().a(text).reset().toString();
        case MUTED -> Ansi.ansi().fgBrightBlack().a(text).reset().toString();
        };
    }

    private static String classicColor(Role role, String text) {
        return switch (role) {
        case TITLE -> Ansi.ansi().bold().fgYellow().a(text).reset().toString();
        case PRIMARY -> Ansi.ansi().fgGreen().a(text).reset().toString();
        case SUCCESS -> Ansi.ansi().bold().fgGreen().a(text).reset().toString();
        case WARNING -> Ansi.ansi().fgYellow().a(text).reset().toString();
        case ERROR -> Ansi.ansi().bold().fgRed().a(text).reset().toString();
        case HIGHLIGHT -> Ansi.ansi().fgCyan().a(text).reset().toString();
        case MUTED -> Ansi.ansi().fgBrightBlack().a(text).reset().toString();
        };
    }

    private static String minimalColor(Role role, String text) {
        return switch (role) {
        case TITLE -> Ansi.ansi().fg(Ansi.Color.WHITE).a(text).reset().toString();
        case PRIMARY -> Ansi.ansi().fgBrightBlack().a(text).reset().toString();
        case SUCCESS -> Ansi.ansi().fgGreen().a(text).reset().toString();
        case WARNING -> Ansi.ansi().fgYellow().a(text).reset().toString();
        case ERROR -> Ansi.ansi().fgRed().a(text).reset().toString();
        case HIGHLIGHT -> Ansi.ansi().fgBlue().a(text).reset().toString();
        case MUTED -> Ansi.ansi().fgBrightBlack().a(text).reset().toString();
        };
    }

    private static String highContrastColor(Role role, String text) {
        return switch (role) {
        case TITLE -> Ansi.ansi().bg(Ansi.Color.YELLOW).fgBlack().bold().a(text).reset().toString();
        case PRIMARY -> Ansi.ansi().bg(Ansi.Color.BLUE).fg(Ansi.Color.WHITE).a(text).reset().toString();
        case SUCCESS -> Ansi.ansi().bg(Ansi.Color.GREEN).fgBlack().a(text).reset().toString();
        case WARNING -> Ansi.ansi().bg(Ansi.Color.MAGENTA).fg(Ansi.Color.WHITE).a(text).reset().toString();
        case ERROR -> Ansi.ansi().bg(Ansi.Color.RED).fg(Ansi.Color.WHITE).bold().a(text).reset().toString();
        case HIGHLIGHT -> Ansi.ansi().bg(Ansi.Color.CYAN).fgBlack().a(text).reset().toString();
        case MUTED -> Ansi.ansi().fgBrightBlack().a(text).reset().toString();
        };
    }

    private static PanelFrame frameForTheme() {
        return switch (themePreset) {
        case NEON -> new PanelFrame('+', '-', '|');
        case CLASSIC -> new PanelFrame('*', '=', '!');
        case MINIMAL -> new PanelFrame('-', '-', '-');
        case HIGH_CONTRAST -> new PanelFrame('#', '#', '#');
        };
    }

    private static String commandPreview() {
        return Arrays.stream(CommandWord.values())
                .map(CommandWord::getCommand)
                .limit(6)
                .collect(Collectors.joining(" | "));
    }

    private enum Role {
        TITLE,
        PRIMARY,
        SUCCESS,
        WARNING,
        ERROR,
        HIGHLIGHT,
        MUTED
    }

    public enum CardType {
        INFO,
        SUCCESS,
        WARNING,
        ERROR,
        HIGHLIGHT
    }

    private enum ThemePreset {
        NEON("neon"),
        CLASSIC("classic"),
        MINIMAL("minimal"),
        HIGH_CONTRAST("high-contrast");

        private final String name;

        ThemePreset(String name) {
            this.name = name;
        }
    }

    private record PanelFrame(char corner, char horizontal, char vertical) {
    }
}
