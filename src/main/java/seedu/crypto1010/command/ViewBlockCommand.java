package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Block;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.ui.CliVisuals;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Displays the full contents of a single block.
 */
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
        CliVisuals.printKeyValuePanel("Block Details", List.of(
                List.of("Block Index", String.valueOf(block.getIndex())),
                List.of("Timestamp", block.getTimestamp()),
                List.of("Previous Hash", block.getPreviousHash()),
                List.of("Current Hash", block.getCurrentHash())));

        List<List<String>> rows = new ArrayList<>();
        int txNo = 1;
        for (String transaction : block.getTransactions()) {
            rows.add(List.of(String.valueOf(txNo++), transaction));
        }
        CliVisuals.printTable("Transactions", List.of("No.", "Transaction"), rows);
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
