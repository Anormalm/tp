package seedu.duke.command;

import seedu.duke.exceptions.Exceptions;
import seedu.duke.model.Block;
import seedu.duke.model.Blockchain;

public class ViewBlockCommand extends Command {
    private static final String INDEX_PARSE_ERROR = "Error: INDEX must be a non-negative integer.";
    private static final String INDEX_RANGE_ERROR = "Error: Block index out of range.";

    private final String indexText;

    public ViewBlockCommand(String indexText) {
        this.indexText = indexText;
    }

    @Override
    public void execute(Blockchain blockchain) throws Exceptions {
        Integer index = parseIndex(indexText);
        if (index == null) {
            throw new Exceptions(INDEX_PARSE_ERROR);
        }
        if (index >= blockchain.size()) {
            throw new Exceptions(INDEX_RANGE_ERROR);
        }

        Block block = blockchain.getBlock(index);
        System.out.println("Block Index   : " + block.getIndex());
        System.out.println("Timestamp     : " + block.getTimestamp());
        System.out.println("Previous Hash : " + block.getPreviousHash());
        System.out.println("Current Hash  : " + block.getCurrentHash());
        System.out.println("Transactions:");
        for (String transaction : block.getTransactions()) {
            System.out.println(transaction);
        }
    }

    private Integer parseIndex(String rawIndex) {
        if (rawIndex == null || rawIndex.isBlank() || rawIndex.contains(" ")) {
            return null;
        }
        try {
            int parsedIndex = Integer.parseInt(rawIndex);
            return parsedIndex >= 0 ? parsedIndex : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
