package seedu.crypto1010.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jline.reader.Candidate;
import org.jline.reader.ParsedLine;
import org.junit.jupiter.api.Test;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.WalletManager;

import java.util.ArrayList;
import java.util.List;

class CommandAutoCompleterTest {

    @Test
    void complete_authMode_showsOnlyAuthSuggestions() {
        CommandAutoCompleter completer = new CommandAutoCompleter(
                List.of("1", "2", "3", "login", "register", "exit"),
                List.of("create", "list", "send"));
        completer.setAuthMode(true);

        List<Candidate> candidates = complete(completer, List.of(), 0, "");

        assertContains(candidates, "login");
        assertContains(candidates, "register");
        assertNotContains(candidates, "create");
        assertNotContains(candidates, "send");
    }

    @Test
    void complete_commandMode_showsOnlyCommandSuggestions() {
        CommandAutoCompleter completer = new CommandAutoCompleter(
                List.of("1", "2", "3", "login", "register", "exit"),
                List.of("create", "list", "send"));
        completer.setAuthMode(false);

        List<Candidate> candidates = complete(completer, List.of(), 0, "");

        assertContains(candidates, "create");
        assertContains(candidates, "send");
        assertNotContains(candidates, "login");
        assertNotContains(candidates, "register");
    }

    @Test
    void complete_commandModeWalletPrefix_suggestsExistingWalletNames() throws Crypto1010Exception {
        WalletManager walletManager = new WalletManager();
        walletManager.createWallet("alice");
        walletManager.createWallet("bob");

        CommandAutoCompleter completer = new CommandAutoCompleter(
                List.of("1", "2", "3", "login", "register", "exit"),
                List.of("send"));
        completer.setAuthMode(false);
        completer.setWalletManager(walletManager);

        List<Candidate> candidates = complete(completer, List.of("send", "w/a"), 1, "w/a");

        assertContains(candidates, "w/alice");
        assertNotContains(candidates, "w/bob");
    }

    @Test
    void complete_commandModeSpeedPrefix_suggestsSpeedValues() {
        CommandAutoCompleter completer = new CommandAutoCompleter(
                List.of("1", "2", "3", "login", "register", "exit"),
                List.of("send"));
        completer.setAuthMode(false);

        List<Candidate> candidates = complete(completer, List.of("send", "speed/"), 1, "speed/");

        assertContains(candidates, "speed/slow");
        assertContains(candidates, "speed/standard");
        assertContains(candidates, "speed/fast");
    }

    private List<Candidate> complete(CommandAutoCompleter completer, List<String> words, int wordIndex, String word) {
        List<Candidate> candidates = new ArrayList<>();
        completer.complete(null, new StubParsedLine(words, wordIndex, word), candidates);
        return candidates;
    }

    private void assertContains(List<Candidate> candidates, String expected) {
        assertTrue(candidates.stream().anyMatch(candidate -> expected.equals(candidate.value())));
    }

    private void assertNotContains(List<Candidate> candidates, String unexpected) {
        assertFalse(candidates.stream().anyMatch(candidate -> unexpected.equals(candidate.value())));
    }

    private record StubParsedLine(List<String> words, int wordIndex, String word) implements ParsedLine {
        @Override
        public String line() {
            return String.join(" ", words);
        }

        @Override
        public int cursor() {
            return line().length();
        }

        @Override
        public int wordCursor() {
            return word.length();
        }
    }
}

