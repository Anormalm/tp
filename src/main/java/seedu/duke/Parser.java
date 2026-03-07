package seedu.duke;

import seedu.duke.command.*;

public class Parser {
    public CommandWord parseCommand(String commandWord) {
        return CommandWord.valueOf(commandWord.toUpperCase());
    }

    /**
     * Parses the given text input to find
     * which command is being called
     * then returns the class corresponding
     * with the command with the correct fields
     *
     * @param inputText the string from the user input
     * @return the class associated with the command
     * that was parsed from the input text
     */
    public Command parse(String inputText) {
        CommandWord commandWord = parseCommand(inputText);
        return switch (commandWord) {
        case LIST -> new ListCommand();
        case HELP -> new HelpCommand();
        case CREATE -> new CreateCommand();
        case BALANCE -> new BalanceCommand();
        case VALIDATE -> new ValidateCommand();
        case VIEWBLOCK -> new ViewBlockCommand();
        case EXIT -> new ExitCommand();
        case SEND -> new SendCommand();
        case KEYGEN -> new KeygenCommand();
        };
    }
}
