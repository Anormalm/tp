package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Block;
import seedu.duke.model.Blockchain;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class BalanceCommandTest {
    @Test
    void execute_existingWallet_printsBalanceToEightDecimalPlaces() {
        Blockchain blockchain = Blockchain.createDefault();
        BalanceCommand command = new BalanceCommand("w/bob");

        String output = runCommand(command, blockchain);

        assertEquals("Balance of bob: 5.00000000" + System.lineSeparator(), output);
    }

    @Test
    void execute_decimalBalance_roundsToEightDecimalPlaces() {
        Blockchain blockchain = new Blockchain(List.of(
                new Block(
                        0,
                        LocalDateTime.of(2026, 2, 12, 14, 30, 21),
                        "0000000000000000",
                        List.of("Genesis Block")),
                new Block(
                        1,
                        LocalDateTime.of(2026, 2, 12, 14, 35, 2),
                        "prev-hash",
                        List.of("miner -> alice : 1.234567895"))));
        BalanceCommand command = new BalanceCommand("w/alice");

        String output = runCommand(command, blockchain);

        assertEquals("Balance of alice: 1.23456790" + System.lineSeparator(), output);
    }

    @Test
    void execute_selfTransfer_keepsNetZeroBalance() {
        Blockchain blockchain = new Blockchain(List.of(
                new Block(
                        0,
                        LocalDateTime.of(2026, 2, 12, 14, 30, 21),
                        "0000000000000000",
                        List.of("Genesis Block")),
                new Block(
                        1,
                        LocalDateTime.of(2026, 2, 12, 14, 35, 2),
                        "prev-hash",
                        List.of("alice -> alice : 5"))));
        BalanceCommand command = new BalanceCommand("w/alice");

        String output = runCommand(command, blockchain);

        assertEquals("Balance of alice: 0.00000000" + System.lineSeparator(), output);
    }

    @Test
    void execute_blankWalletNameAfterPrefix_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        BalanceCommand command = new BalanceCommand("w/   ");

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertEquals("Error: wallet name cannot be empty.", exception.getMessage());
    }

    @Test
    void execute_missingWalletPrefix_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        BalanceCommand command = new BalanceCommand("alice");

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertEquals("Error: Invalid balance format. Use: balance w/WALLET_NAME", exception.getMessage());
    }

    @Test
    void execute_walletNameWithSpaces_throwsException() {
        Blockchain blockchain = Blockchain.createDefault();
        BalanceCommand command = new BalanceCommand("w/alice bob");

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertEquals("Error: wallet name must be one word without spaces.", exception.getMessage());
    }

    private String runCommand(Command command, Blockchain blockchain) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        try {
            command.execute(blockchain);
        } catch (Exceptions e) {
            throw new RuntimeException(e);
        } finally {
            System.setOut(originalOut);
        }
        return outputStream.toString();
    }
}
