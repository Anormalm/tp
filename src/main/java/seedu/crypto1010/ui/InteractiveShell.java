package seedu.crypto1010.ui;

import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.Highlighter;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Parser;
import org.jline.reader.Reference;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultHighlighter;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import seedu.crypto1010.command.CommandWord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class InteractiveShell implements AutoCloseable {
    private static final String HISTORY_FILE_VARIABLE = "crypto1010.history.file";
    private static final int MAX_SUGGESTION_DISTANCE = 3;
    private static final int DEFAULT_SEARCH_LIMIT = 10;
    private static final String[] STATIC_PREFIXES = {
        "w/", "to/", "amt/", "speed/", "fee/", "note/", "curr/", "acc/", "c/"
    };
    private static final String[] STATIC_SPEEDS = {"slow", "standard", "fast"};
    private static final String[] STATIC_META_COMMANDS = {
        ":help", ":theme", ":themes", ":plain", ":mode", ":timeline", ":search", ":clear",
        ":status", ":home", ":confirm", ":exit"
    };
    private static final String[] STATIC_SLASH_ALIASES = {
        "/h", "/help", "/ls", "/list", "/vc", "/viewchain", "/q", "/quit", "/kb", "/keygen"
    };
    private static final Set<String> COMMAND_WORDS = Arrays.stream(CommandWord.values())
            .map(commandWord -> commandWord.getCommand().toLowerCase(Locale.ROOT))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    private static final Set<String> PREFIXES = Set.of(STATIC_PREFIXES);
    private static final Set<String> SPEEDS = Set.of(STATIC_SPEEDS);
    private static final Set<String> META_COMMANDS = Set.of(STATIC_META_COMMANDS);
    private static final Set<String> SLASH_ALIASES = Set.of(STATIC_SLASH_ALIASES);

    private final Terminal terminal;
    private final LineReader lineReader;
    private final List<String> dynamicWalletNames;
    private final List<String> dynamicAccountNames;

    private InteractiveShell(Terminal terminal, LineReader lineReader,
                             List<String> dynamicWalletNames, List<String> dynamicAccountNames) {
        this.terminal = terminal;
        this.lineReader = lineReader;
        this.dynamicWalletNames = dynamicWalletNames;
        this.dynamicAccountNames = dynamicAccountNames;
    }

    public static InteractiveShell create() {
        try {
            Terminal terminal = TerminalBuilder.builder()
                    .system(true)
                    .jna(true)
                    .jansi(true)
                    .build();
            Parser parser = new DefaultParser();
            Highlighter highlighter = new CommandSyntaxHighlighter();
            History history = new DefaultHistory();
            List<String> wallets = new ArrayList<>();
            List<String> accounts = new ArrayList<>();
            Completer completer = buildCompleter(wallets, accounts);

            LineReader lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .parser(parser)
                    .highlighter(highlighter)
                    .history(history)
                    .completer(completer)
                    .variable(HISTORY_FILE_VARIABLE, ".crypto1010-history")
                    .build();

            lineReader.option(LineReader.Option.AUTO_LIST, true);
            lineReader.option(LineReader.Option.AUTO_MENU, true);
            lineReader.option(LineReader.Option.MENU_COMPLETE, true);
            lineReader.option(LineReader.Option.COMPLETE_IN_WORD, true);
            lineReader.option(LineReader.Option.AUTO_FRESH_LINE, true);
            lineReader.option(LineReader.Option.INSERT_TAB, false);
            lineReader.option(LineReader.Option.DISABLE_EVENT_EXPANSION, true);
            bindCompletionKeys(lineReader);

            return new InteractiveShell(terminal, lineReader, wallets, accounts);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to initialize interactive shell.", e);
        }
    }

    public String readLine(String accountName, InputMode mode, String rightStatus) {
        try {
            String prompt = CliVisuals.buildPrompt(accountName == null ? "guest" : accountName, mode.label);
            String raw = lineReader.readLine(prompt, rightStatus == null ? "" : rightStatus, (Character) null, null);
            return raw == null ? null : raw.strip();
        } catch (UserInterruptException e) {
            return "";
        } catch (EndOfFileException e) {
            return null;
        }
    }

    public String readSecret(String label) {
        try {
            String raw = lineReader.readLine(label + " ", '*');
            return raw == null ? null : raw.strip();
        } catch (UserInterruptException e) {
            return "";
        } catch (EndOfFileException e) {
            return null;
        }
    }

    public String readPlain(String label) {
        try {
            String raw = lineReader.readLine(label + " ");
            return raw == null ? null : raw.strip();
        } catch (UserInterruptException e) {
            return "";
        } catch (EndOfFileException e) {
            return null;
        }
    }

    public void printCommandPalette() {
        CliVisuals.printHint("Command palette: help, list, viewchain, send, crossSend, validate");
        CliVisuals.printHint("Autocomplete: TAB. Aliases: /ls /vc /q /kb. UI controls: :help");
    }

    public String suggestCommand(String input) {
        String raw = input == null ? "" : input.trim();
        if (raw.isEmpty()) {
            return null;
        }
        String typed = raw.split("\\s+", 2)[0].toLowerCase(Locale.ROOT);
        String best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (String command : COMMAND_WORDS) {
            int distance = levenshteinDistance(typed, command);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = command;
            }
        }
        return bestDistance <= MAX_SUGGESTION_DISTANCE ? best : null;
    }

    public void updateDynamicCandidates(List<String> walletNames, List<String> accountNames) {
        replaceWithNormalized(dynamicWalletNames, walletNames);
        replaceWithNormalized(dynamicAccountNames, accountNames);
    }

    public List<String> searchHistory(String query, int limit) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        int normalizedLimit = Math.max(1, limit);
        List<String> lines = new ArrayList<>();
        for (History.Entry entry : lineReader.getHistory()) {
            if (entry.line() != null && !entry.line().isBlank()) {
                lines.add(entry.line().strip());
            }
        }

        List<String> matches = new ArrayList<>();
        for (int i = lines.size() - 1; i >= 0; i--) {
            String line = lines.get(i);
            if (normalizedQuery.isBlank() || line.toLowerCase(Locale.ROOT).contains(normalizedQuery)) {
                matches.add(line);
            }
            if (matches.size() >= normalizedLimit) {
                break;
            }
        }
        return matches;
    }

    public List<String> searchHistory(String query) {
        return searchHistory(query, DEFAULT_SEARCH_LIMIT);
    }

    public InputMode parseMode(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        for (InputMode mode : InputMode.values()) {
            if (mode.label.equalsIgnoreCase(token.trim())) {
                return mode;
            }
        }
        return null;
    }

    @Override
    public void close() {
        try {
            lineReader.getHistory().save();
        } catch (IOException e) {
            // ignore
        }
        try {
            terminal.flush();
            terminal.close();
        } catch (IOException e) {
            // ignore
        }
    }

    private static void bindCompletionKeys(LineReader lineReader) {
        bindKeymapCompletion(lineReader, LineReader.MAIN);
        bindKeymapCompletion(lineReader, LineReader.EMACS);
        bindKeymapCompletion(lineReader, LineReader.VIINS);
    }

    private static void bindKeymapCompletion(LineReader lineReader, String keymapName) {
        KeyMap<Binding> keymap = lineReader.getKeyMaps().get(keymapName);
        if (keymap == null) {
            return;
        }
        keymap.bind(new Reference(LineReader.MENU_COMPLETE), "\t", "^I", "\u0000");
    }

    private static Completer buildCompleter(List<String> dynamicWalletNames, List<String> dynamicAccountNames) {
        List<String> commandWords = Arrays.stream(CommandWord.values())
                .map(CommandWord::getCommand)
                .sorted(String::compareToIgnoreCase)
                .toList();
        List<String> prefixes = List.of(STATIC_PREFIXES);
        List<String> speeds = List.of(STATIC_SPEEDS);
        List<String> metaCommands = List.of(STATIC_META_COMMANDS);
        List<String> slashAliases = List.of(STATIC_SLASH_ALIASES);

        return (reader, line, candidates) -> {
            String word = line.word() == null ? "" : line.word().trim();
            String normalizedWord = word.toLowerCase(Locale.ROOT);
            boolean firstWord = line.wordIndex() == 0;
            if (firstWord) {
                addCandidates(candidates, commandWords, "commands", normalizedWord, false);
                addCandidates(candidates, metaCommands, "ui", normalizedWord, false);
                addCandidates(candidates, slashAliases, "aliases", normalizedWord, false);
                return;
            }

            addCandidates(candidates, prefixes, "prefixes", normalizedWord, false);
            addCandidates(candidates, speeds, "speed", normalizedWord, false);
            addCandidates(candidates, dynamicWalletNames, "wallets", normalizedWord, true);
            addCandidates(candidates, dynamicAccountNames, "accounts", normalizedWord, true);
            if (candidates.isEmpty()) {
                candidates.addAll(defaultCandidates(commandWords, prefixes, speeds, metaCommands, slashAliases,
                        dynamicWalletNames, dynamicAccountNames));
            }
        };
    }

    private static void addCandidates(List<Candidate> candidates, List<String> values, String group,
                                      String normalizedWord, boolean prefixedValue) {
        String prefix = normalizePrefix(group);
        for (String value : values) {
            String candidate = prefixedValue ? prefix + value : value;
            if (matches(candidate, normalizedWord, prefixedValue, prefix)) {
                candidates.add(new Candidate(candidate, candidate, group, null, null, null, true));
            }
        }
    }

    private static boolean matches(String candidateValue, String normalizedWord, boolean prefixedValue,
                                   String normalizedPrefix) {
        if (normalizedWord.isBlank()) {
            return true;
        }
        String normalizedCandidate = candidateValue.toLowerCase(Locale.ROOT);
        if (normalizedCandidate.startsWith(normalizedWord)) {
            return true;
        }
        if (prefixedValue && normalizedWord.startsWith(normalizedPrefix)) {
            String candidateRest = normalizedCandidate.substring(normalizedPrefix.length());
            String typedRest = normalizedWord.substring(normalizedPrefix.length());
            return candidateRest.startsWith(typedRest);
        }
        return false;
    }

    private static String normalizePrefix(String group) {
        return switch (group) {
        case "wallets" -> "w/";
        case "accounts" -> "acc/";
        default -> "";
        };
    }

    private static List<Candidate> defaultCandidates(List<String> commandWords, List<String> prefixes,
                                                     List<String> speeds, List<String> metaCommands,
                                                     List<String> slashAliases, List<String> dynamicWalletNames,
                                                     List<String> dynamicAccountNames) {
        List<Candidate> out = new ArrayList<>();
        for (String command : commandWords) {
            out.add(new Candidate(command, command, "commands", null, null, null, true));
        }
        for (String prefix : prefixes) {
            out.add(new Candidate(prefix, prefix, "prefixes", null, null, null, true));
        }
        for (String speed : speeds) {
            out.add(new Candidate(speed, speed, "speed", null, null, null, true));
        }
        for (String meta : metaCommands) {
            out.add(new Candidate(meta, meta, "ui", null, null, null, true));
        }
        for (String alias : slashAliases) {
            out.add(new Candidate(alias, alias, "aliases", null, null, null, true));
        }
        for (String wallet : dynamicWalletNames) {
            String value = "w/" + wallet;
            out.add(new Candidate(value, value, "wallets", null, null, null, true));
        }
        for (String account : dynamicAccountNames) {
            String value = "acc/" + account;
            out.add(new Candidate(value, value, "accounts", null, null, null, true));
        }
        return out;
    }

    private void replaceWithNormalized(List<String> target, List<String> source) {
        target.clear();
        if (source == null || source.isEmpty()) {
            return;
        }
        Set<String> normalized = source.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        target.addAll(normalized.stream().sorted(Comparator.naturalOrder()).toList());
    }

    private int levenshteinDistance(String left, String right) {
        int[][] dp = new int[left.length() + 1][right.length() + 1];
        for (int i = 0; i <= left.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= right.length(); j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= left.length(); i++) {
            for (int j = 1; j <= right.length(); j++) {
                int substitutionCost = left.charAt(i - 1) == right.charAt(j - 1) ? 0 : 1;
                int replace = dp[i - 1][j - 1] + substitutionCost;
                int delete = dp[i - 1][j] + 1;
                int insert = dp[i][j - 1] + 1;
                dp[i][j] = Math.min(replace, Math.min(delete, insert));
            }
        }
        return dp[left.length()][right.length()];
    }

    private static final class CommandSyntaxHighlighter extends DefaultHighlighter {
        private static final AttributedStyle COMMAND_STYLE = AttributedStyle.DEFAULT.bold()
                .foreground(AttributedStyle.BRIGHT | AttributedStyle.CYAN);
        private static final AttributedStyle PREFIX_STYLE = AttributedStyle.DEFAULT.bold()
                .foreground(AttributedStyle.BRIGHT | AttributedStyle.BLUE);
        private static final AttributedStyle SPEED_STYLE = AttributedStyle.DEFAULT.bold()
                .foreground(AttributedStyle.BRIGHT | AttributedStyle.GREEN);
        private static final AttributedStyle META_STYLE = AttributedStyle.DEFAULT.bold()
                .foreground(AttributedStyle.BRIGHT | AttributedStyle.MAGENTA);
        private static final AttributedStyle ALIAS_STYLE = AttributedStyle.DEFAULT.bold()
                .foreground(AttributedStyle.BRIGHT | AttributedStyle.YELLOW);

        @Override
        public AttributedString highlight(LineReader reader, String buffer) {
            if (buffer == null || buffer.isEmpty()) {
                return AttributedString.EMPTY;
            }
            if (CliVisuals.isPlainMode()) {
                return new AttributedString(buffer);
            }

            AttributedStringBuilder builder = new AttributedStringBuilder();
            String[] fragments = buffer.split("(?<=\\s)|(?=\\s)");
            int tokenIndex = 0;
            for (String fragment : fragments) {
                if (fragment.isBlank()) {
                    builder.append(fragment);
                    continue;
                }
                builder.style(resolveStyle(fragment, tokenIndex == 0)).append(fragment);
                tokenIndex++;
            }
            return builder.toAttributedString();
        }

        private AttributedStyle resolveStyle(String token, boolean firstToken) {
            String normalized = token.toLowerCase(Locale.ROOT);
            if (META_COMMANDS.contains(normalized)) {
                return META_STYLE;
            }
            if (SLASH_ALIASES.contains(normalized)) {
                return ALIAS_STYLE;
            }
            if (firstToken && COMMAND_WORDS.contains(normalized)) {
                return COMMAND_STYLE;
            }
            if (SPEEDS.contains(normalized)) {
                return SPEED_STYLE;
            }
            if (hasKnownPrefix(normalized)) {
                return PREFIX_STYLE;
            }
            return AttributedStyle.DEFAULT;
        }

        private boolean hasKnownPrefix(String token) {
            int index = token.indexOf('/');
            if (index <= 0) {
                return false;
            }
            String prefix = token.substring(0, index + 1);
            return PREFIXES.contains(prefix);
        }
    }

    public enum InputMode {
        NORMAL("normal"),
        COMPOSE("compose"),
        SEARCH("search"),
        HISTORY("history");

        private final String label;

        InputMode(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }
}
