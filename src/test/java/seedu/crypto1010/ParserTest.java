package seedu.crypto1010;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import seedu.crypto1010.command.BalanceCommand;
import seedu.crypto1010.command.Command;
import seedu.crypto1010.command.CrossSendCommand;
import seedu.crypto1010.command.HistoryCommand;
import seedu.crypto1010.command.ViewChainCommand;
import seedu.crypto1010.model.WalletManager;

import org.junit.jupiter.api.Test;

class ParserTest {
    @Test
    void parse_balanceCommandWithName_returnsBalanceCommand() {
        Parser parser = new Parser(new WalletManager());

        Command command = parser.parse("balance w/alice");

        assertInstanceOf(BalanceCommand.class, command);
    }

    @Test
    void parse_historyCommandWithName_returnsHistoryCommand() {
        Parser parser = new Parser(new WalletManager());

        Command command = parser.parse("history w/alice");

        assertInstanceOf(HistoryCommand.class, command);
    }

    @Test
    void parse_crossSendCommand_returnsCrossSendCommand() {
        Parser parser = new Parser(new WalletManager(), "sender", ParserTest.class);

        Command command = parser.parse("crossSend acc/receiver amt/1 curr/btc");

        assertInstanceOf(CrossSendCommand.class, command);
    }

    @Test
    void parse_viewChainCommand_returnsViewChainCommand() {
        Parser parser = new Parser(new WalletManager());

        Command command = parser.parse("viewchain");

        assertInstanceOf(ViewChainCommand.class, command);
    }
}
