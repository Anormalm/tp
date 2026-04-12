package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Block;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.ui.CliVisuals;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ViewChainCommand extends Command {
    private static final String HELP_DESCRIPTION = """
            Format: viewchain

            Displays a compact blockchain overview
            Includes total blocks, total transactions, and a compact block list
            """;
    private static final int HASH_PREVIEW_LENGTH = 12;

    public ViewChainCommand() {
        super(HELP_DESCRIPTION);
    }

    @Override
    public void execute(Blockchain blockchain, Scanner in) throws Crypto1010Exception {
        List<Block> blocks = blockchain.getBlocks();
        int totalTransactions = blocks.stream()
                .mapToInt(block -> block.getTransactions().size())
                .sum();

        CliVisuals.printKeyValuePanel("Blockchain Overview", List.of(
                List.of("Total blocks", String.valueOf(blocks.size())),
                List.of("Total transactions", String.valueOf(totalTransactions))));

        List<List<String>> rows = new ArrayList<>();
        for (Block block : blocks) {
            rows.add(List.of(
                    String.valueOf(block.getIndex()),
                    String.valueOf(block.getTransactions().size()),
                    block.getTimestamp(),
                    compactHash(block.getCurrentHash())));
        }
        CliVisuals.printTable("Blocks", List.of("Index", "Tx Count", "Timestamp", "Hash Preview"), rows);
    }

    private String compactHash(String hash) {
        if (hash == null || hash.length() <= HASH_PREVIEW_LENGTH) {
            return hash;
        }
        return hash.substring(0, HASH_PREVIEW_LENGTH) + "...";
    }
}
