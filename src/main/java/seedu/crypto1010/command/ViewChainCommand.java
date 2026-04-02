package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Block;
import seedu.crypto1010.model.Blockchain;

import java.util.List;

public class ViewChainCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: viewchain

            Displays a compact blockchain overview
            Includes total blocks, total transactions, and a compact block list
            """;
    private static final String INVALID_FORMAT_ERROR = "Error: Invalid viewchain format. Use: viewchain";
    private static final int HASH_PREVIEW_LENGTH = 12;

    public ViewChainCommand() {
        super(HELP_DESCRIPTION);
    }

    @Override
    public void execute(String description, Blockchain blockchain) throws Crypto1010Exception {
        if (description != null && !description.isBlank()) {
            throw new Crypto1010Exception(INVALID_FORMAT_ERROR);
        }

        List<Block> blocks = blockchain.getBlocks();
        int totalTransactions = blocks.stream()
                .mapToInt(block -> block.getTransactions().size())
                .sum();

        System.out.println("Blockchain Overview:");
        System.out.println("Total blocks: " + blocks.size());
        System.out.println("Total transactions: " + totalTransactions);
        System.out.println("Blocks:");
        for (Block block : blocks) {
            System.out.println(block.getIndex()
                    + " | tx=" + block.getTransactions().size()
                    + " | time=" + block.getTimestamp()
                    + " | hash=" + compactHash(block.getCurrentHash()));
        }
    }

    private String compactHash(String hash) {
        if (hash == null || hash.length() <= HASH_PREVIEW_LENGTH) {
            return hash;
        }
        return hash.substring(0, HASH_PREVIEW_LENGTH) + "...";
    }
}
