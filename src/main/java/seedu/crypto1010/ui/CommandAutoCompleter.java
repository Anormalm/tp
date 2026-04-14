package seedu.crypto1010.ui;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Supplies context-aware suggestions for the interactive shell.
 */
public class CommandAutoCompleter implements Completer {
    private static final List<String> SPEED_VALUES = List.of("speed/slow", "speed/standard", "speed/fast");
    private static final List<String> CURRENCY_VALUES = List.of(
            "curr/btc",
            "curr/eth");

    private volatile WalletManager walletManager;
    private final List<String> authWords;
    private final List<String> commandWords;
    private volatile boolean authMode = true;

    public CommandAutoCompleter(List<String> authWords, List<String> commandWords) {
        this.authWords = authWords;
        this.commandWords = commandWords;
    }

    public void setWalletManager(WalletManager walletManager) {
        this.walletManager = walletManager;
    }

    public void setAuthMode(boolean authMode) {
        this.authMode = authMode;
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        String currentWord = line.word() == null ? "" : line.word();
        int wordIndex = line.wordIndex();
        List<String> words = line.words();

        if (wordIndex <= 0 || words.isEmpty()) {
            addFilteredCandidates(candidates, authMode ? authWords : commandWords, currentWord);
            return;
        }

        if (authMode) {
            addFilteredCandidates(candidates, authWords, currentWord);
            return;
        }

        String command = words.get(0).toLowerCase(Locale.ROOT);
        Set<String> suggestions = new LinkedHashSet<>();
        suggestions.addAll(prefixSuggestionsForCommand(command));
        suggestions.addAll(valueSuggestionsForCurrentWord(currentWord));
        addFilteredCandidates(candidates, suggestions, currentWord);
    }

    private List<String> prefixSuggestionsForCommand(String command) {
        return switch (command) {
        case "create" -> List.of("w/", "curr/");
        case "balance", "history", "keygen" -> List.of("w/");
        case "send" -> List.of("w/", "to/", "amt/", "speed/", "fee/", "note/");
        case "crosssend" -> List.of("acc/", "amt/", "curr/");
        case "tutorial" -> List.of("start", "exit");
        case "help" -> commandWords;
        default -> List.of();
        };
    }

    private Set<String> valueSuggestionsForCurrentWord(String currentWord) {
        Set<String> suggestions = new LinkedHashSet<>();
        String lowered = currentWord.toLowerCase(Locale.ROOT);
        if (lowered.startsWith("w/")) {
            if (walletManager == null) {
                return suggestions;
            }
            String suffix = currentWord.substring(2);
            for (Wallet wallet : walletManager.getWallets()) {
                suggestions.add("w/" + wallet.getName());
                if (!suffix.isEmpty()
                        && wallet.getName()
                        .toLowerCase(Locale.ROOT)
                        .startsWith(suffix.toLowerCase(Locale.ROOT))) {
                    suggestions.add("w/" + wallet.getName());
                }
            }
        }
        if (lowered.startsWith("speed/")) {
            suggestions.addAll(SPEED_VALUES);
        }
        if (lowered.startsWith("curr/")) {
            suggestions.addAll(CURRENCY_VALUES);
        }
        return suggestions;
    }

    private void addFilteredCandidates(List<Candidate> candidates, Iterable<String> options, String prefix) {
        String loweredPrefix = prefix == null ? "" : prefix.toLowerCase(Locale.ROOT);
        for (String option : options) {
            if (option == null || option.isBlank()) {
                continue;
            }
            if (loweredPrefix.isEmpty() || option.toLowerCase(Locale.ROOT).startsWith(loweredPrefix)) {
                candidates.add(new Candidate(option));
            }
        }
    }
}
