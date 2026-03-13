package seedu.duke.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    private static final String GENESIS_PREVIOUS_HASH = "0000000000000000";

    private final List<Block> blocks;

    public Blockchain(List<Block> blocks) {
        this.blocks = new ArrayList<>(blocks);
    }

    public static Blockchain createDefault() {
        List<Block> defaultBlocks = new ArrayList<>();
        Block genesis = new Block(
                0,
                LocalDateTime.of(2026, 2, 12, 14, 30, 21),
                GENESIS_PREVIOUS_HASH,
                List.of("Genesis Block"));
        defaultBlocks.add(genesis);
        defaultBlocks.add(new Block(
                1,
                LocalDateTime.of(2026, 2, 12, 14, 35, 2),
                genesis.getCurrentHash(),
                List.of("alice -> bob : 10", "bob -> carol : 5")));
        return new Blockchain(defaultBlocks);
    }

    public int size() {
        return blocks.size();
    }

    public Block getBlock(int index) {
        return blocks.get(index);
    }

    public ValidationResult validate() {
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);

            String computedHash = block.computeCurrentHash();
            if (!computedHash.equals(block.getCurrentHash())) {
                return ValidationResult.invalid("Hash mismatch at Block " + i + ".");
            }

            if (!block.hasValidTransactionData()) {
                return ValidationResult.invalid("Invalid transaction data at Block " + i + ".");
            }

            if (i == 0) {
                if (!GENESIS_PREVIOUS_HASH.equals(block.getPreviousHash())) {
                    return ValidationResult.invalid("Invalid previous hash linkage at Block " + i + ".");
                }
                continue;
            }

            Block previousBlock = blocks.get(i - 1);
            if (!previousBlock.getCurrentHash().equals(block.getPreviousHash())) {
                return ValidationResult.invalid("Invalid previous hash linkage at Block " + i + ".");
            }
        }

        return ValidationResult.valid();
    }

    public double getBalance(String walletName) {
        double balance = 0.0;
        for (Block block : blocks) {
            for (String transaction : block.getTransactions()) {
                balance += parseTransactionAmount(walletName, transaction);
            }
        }
        return balance;
    }

    private double parseTransactionAmount(String walletName, String transaction) {
        // Expected format: "sender -> receiver : amount"
        String[] parts = transaction.split(" -> ");
        if (parts.length != 2) {
            return 0.0;
        }
        String sender = parts[0].trim();
        String rest = parts[1];
        String[] receiverAmount = rest.split(" : ");
        if (receiverAmount.length != 2) {
            return 0.0;
        }
        String receiver = receiverAmount[0].trim();
        String amountStr = receiverAmount[1].trim();

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }

        if (walletName.equalsIgnoreCase(receiver)) {
            return amount;
        } else if (walletName.equalsIgnoreCase(sender)) {
            return -amount;
        }
        return 0.0;
    }
}
