package seedu.crypto1010;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import seedu.crypto1010.command.BalanceCommand;
import seedu.crypto1010.command.Command;
import seedu.crypto1010.model.WalletManager;

import org.junit.jupiter.api.Test;

class ParserTest {
    @Test
    void parse_balanceCommandWithName_returnsBalanceCommand() {
        Parser parser = new Parser(new WalletManager());

        Command command = parser.parse("balance w/alice");

        assertInstanceOf(BalanceCommand.class, command);
    }
}
