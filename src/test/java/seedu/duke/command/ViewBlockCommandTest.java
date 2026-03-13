package seedu.duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Blockchain;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class ViewBlockCommandTest {
    @Test
    void execute_validIndex_printsBlockDetails() {
        Blockchain blockchain = Blockchain.createDefault();
        ViewBlockCommand command = new ViewBlockCommand("1");

        String output = runCommand(command, blockchain);

        assertEquals(
                "Block Index   : 1" + System.lineSeparator()
                        + "Timestamp     : 2026-02-12 14:35:02" + System.lineSeparator()
                        + "Previous Hash : " + blockchain.getBlock(1).getPreviousHash() + System.lineSeparator()
                        + "Current Hash  : " + blockchain.getBlock(1).getCurrentHash() + System.lineSeparator()
                        + "Transactions:" + System.lineSeparator()
                        + "alice -> bob : 10" + System.lineSeparator()
                        + "bob -> carol : 5" + System.lineSeparator(),
                output);
    }

    @Test
    void execute_negativeIndex_printsParseError() {
        Blockchain blockchain = Blockchain.createDefault();
        ViewBlockCommand command = new ViewBlockCommand("-1");

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertEquals("Error: INDEX must be a non-negative integer.", exception.getMessage());
    }

    @Test
    void execute_nonNumericIndex_printsParseError() {
        Blockchain blockchain = Blockchain.createDefault();
        ViewBlockCommand command = new ViewBlockCommand("abc");

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertEquals("Error: INDEX must be a non-negative integer.", exception.getMessage());
    }

    @Test
    void execute_outOfRangeIndex_printsRangeError() {
        Blockchain blockchain = Blockchain.createDefault();
        ViewBlockCommand command = new ViewBlockCommand("5");

        Exceptions exception = assertThrows(Exceptions.class, () -> command.execute(blockchain));
        assertEquals("Error: Block index out of range.", exception.getMessage());
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
