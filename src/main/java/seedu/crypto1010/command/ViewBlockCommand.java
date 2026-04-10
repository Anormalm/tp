package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Block;
import seedu.crypto1010.model.Blockchain;

import java.util.Scanner;

public class ViewBlockCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: viewblock INDEX
            Example: viewblock 0
            
            Displays the full details of a block
            The index must be a non-negative integer 0, 1, 2...
            Details include: Block Index, Timestamp, Previous Hash, Current Hash and List of Transactions
            """;

    private static final String INDEX_PARSE_ERROR = "Error: INDEX must be a non-negative integer."
            + " Use: viewblock INDEX";
    private static final String INDEX_RANGE_ERROR = "Error: Block index out of range.";

    private final String indexText;

    public ViewBlockCommand(String indexText) {
        super(HELP_DESCRIPTION);
        this.indexText = indexText;
    }

    @Override
    public void execute(Blockchain blockchain, Scanner in) throws Crypto1010Exception {
        Integer index = parseIndex(indexText);
        if (index == null) {
            throw new Crypto1010Exception(INDEX_PARSE_ERROR);
        }
        if (index >= blockchain.size()) {
            throw new Crypto1010Exception(INDEX_RANGE_ERROR);
        }

        Block block = blockchain.getBlock(index);
        System.out.println();
        System.out.println("Block Details:");
        System.out.println("=".repeat(80));
        System.out.printf("%-18s: %s%n", "Block Index", block.getIndex());
        System.out.printf("%-18s: %s%n", "Timestamp", block.getTimestamp());
        System.out.printf("%-18s: %s%n", "Previous Hash", block.getPreviousHash());
        System.out.printf("%-18s: %s%n", "Current Hash", block.getCurrentHash());
        System.out.println("-".repeat(80));
        System.out.println("Transactions:");
        System.out.printf("%-4s %-74s%n", "No.", "Transaction");
        System.out.println("-".repeat(80));
        int txNo = 1;
        for (String transaction : block.getTransactions()) {
            System.out.printf("%-4d %-74s%n", txNo++, transaction);
        }
        System.out.println("=".repeat(80));
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
