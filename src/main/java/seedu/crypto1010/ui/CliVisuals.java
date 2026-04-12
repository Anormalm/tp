package seedu.crypto1010.ui;

import java.util.ArrayList;
import java.util.List;

public final class CliVisuals {
    private static final int PANEL_WIDTH = 72;
    private static final int LEGACY_WIDTH = 60;

    private CliVisuals() {
    }

    public static void printPanel(String title, List<String> lines) {
        System.out.println("+" + "-".repeat(PANEL_WIDTH - 2) + "+");
        System.out.println("| " + fit(title, PANEL_WIDTH - 4) + " |");
        System.out.println("+" + "-".repeat(PANEL_WIDTH - 2) + "+");
        for (String line : safeLines(lines)) {
            for (String wrapped : wrapLine(line, PANEL_WIDTH - 4)) {
                System.out.println("| " + fit(wrapped, PANEL_WIDTH - 4) + " |");
            }
        }
        System.out.println("+" + "-".repeat(PANEL_WIDTH - 2) + "+");
    }

    public static void printInfo(String message) {
        System.out.println(message);
    }

    public static void printWarning(String message) {
        System.out.println(message);
    }

    public static void printError(String message) {
        System.out.println(message);
    }

    public static void printLegacySection(String title, List<String> lines) {
        String divider = "=".repeat(LEGACY_WIDTH);
        System.out.println(divider);
        System.out.println(title);
        for (String line : safeLines(lines)) {
            System.out.println(line == null ? "" : line);
        }
        System.out.println(divider);
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
}
