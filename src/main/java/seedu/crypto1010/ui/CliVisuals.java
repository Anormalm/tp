package seedu.crypto1010.ui;

import java.util.ArrayList;
import java.util.List;

public final class CliVisuals {
    private static final int PANEL_WIDTH = 72;
    private static final int LEGACY_WIDTH = 60;
    private static final boolean COLORS_ENABLED = System.console() != null && System.getenv("NO_COLOR") == null;
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_MAGENTA = "\u001B[35m";

    private CliVisuals() {
    }

    public static void printPanel(String title, List<String> lines) {
        String border = "+" + "-".repeat(PANEL_WIDTH - 2) + "+";
        System.out.println(accent(border));
        System.out.println(accent("| ") + titleText(fit(title, PANEL_WIDTH - 4)) + accent(" |"));
        System.out.println(accent(border));
        for (String line : safeLines(lines)) {
            for (String wrapped : wrapLine(line, PANEL_WIDTH - 4)) {
                System.out.println(accent("| ") + fit(wrapped, PANEL_WIDTH - 4) + accent(" |"));
            }
        }
        System.out.println(accent(border));
    }

    public static void printInfo(String message) {
        System.out.println(color(message, ANSI_GREEN));
    }

    public static void printWarning(String message) {
        System.out.println(color(message, ANSI_YELLOW));
    }

    public static void printError(String message) {
        System.out.println(color(message, ANSI_RED));
    }

    public static void printLegacySection(String title, List<String> lines) {
        String divider = "=".repeat(LEGACY_WIDTH);
        System.out.println(accent(divider));
        System.out.println(titleText(title));
        for (String line : safeLines(lines)) {
            System.out.println(line == null ? "" : line);
        }
        System.out.println(accent(divider));
    }

    public static void printLogo(List<String> logoLines, String slogan) {
        if (logoLines != null) {
            for (String line : logoLines) {
                if (line != null) {
                    System.out.println(color(line, ANSI_MAGENTA));
                }
            }
        }
        if (slogan != null && !slogan.isBlank()) {
            System.out.println(color(slogan, ANSI_CYAN));
        }
    }

    public static void printTable(String title, List<String> headers, List<List<String>> rows) {
        int[] widths = new int[headers.size()];
        for (int i = 0; i < headers.size(); i++) {
            widths[i] = safeCell(headers.get(i)).length();
        }
        for (List<String> row : rows) {
            for (int i = 0; i < Math.min(row.size(), widths.length); i++) {
                widths[i] = Math.max(widths[i], safeCell(row.get(i)).length());
            }
        }

        List<String> lines = new ArrayList<>();
        lines.add(joinCells(headers, widths));
        lines.add(separator(widths));
        for (List<String> row : rows) {
            lines.add(joinCells(row, widths));
        }
        printPanel(title, lines);
    }

    public static void printKeyValuePanel(String title, List<List<String>> rows) {
        List<String> lines = new ArrayList<>();
        for (List<String> row : rows) {
            if (row != null && row.size() >= 2) {
                lines.add(safeCell(row.get(0)) + " : " + safeCell(row.get(1)));
            }
        }
        printPanel(title, lines);
    }

    private static List<String> safeLines(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return List.of("");
        }
        return lines;
    }

    private static String safeCell(String value) {
        return value == null ? "" : value;
    }

    private static String joinCells(List<String> row, int[] widths) {
        List<String> cells = new ArrayList<>();
        for (int i = 0; i < widths.length; i++) {
            String value = i < row.size() ? safeCell(row.get(i)) : "";
            cells.add(fit(value, widths[i]));
        }
        return String.join(" | ", cells);
    }

    private static String separator(int[] widths) {
        List<String> parts = new ArrayList<>();
        for (int width : widths) {
            parts.add("-".repeat(width));
        }
        return String.join("-+-", parts);
    }

    private static List<String> wrapLine(String line, int width) {
        String value = line == null ? "" : line;
        if (value.length() <= width) {
            return List.of(value);
        }
        List<String> out = new ArrayList<>();
        String remaining = value;
        while (remaining.length() > width) {
            int breakAt = remaining.lastIndexOf(' ', width);
            if (breakAt <= 0) {
                breakAt = width;
            }
            out.add(remaining.substring(0, breakAt).stripTrailing());
            remaining = remaining.substring(breakAt).stripLeading();
        }
        if (!remaining.isEmpty()) {
            out.add(remaining);
        }
        return out;
    }

    private static String fit(String value, int width) {
        if (value.length() >= width) {
            return value.substring(0, width);
        }
        return value + " ".repeat(width - value.length());
    }

    private static String accent(String text) {
        return color(text, ANSI_CYAN);
    }

    private static String titleText(String text) {
        return color(text, ANSI_MAGENTA);
    }

    private static String color(String text, String ansiColor) {
        if (!COLORS_ENABLED) {
            return text;
        }
        return ansiColor + text + ANSI_RESET;
    }
}
