package seedu.crypto1010.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Blockchain {
    private static final String GENESIS_PREVIOUS_HASH = "0000000000000000";
    private static final Pattern TRANSACTION_PATTERN =
            Pattern.compile("^(.+?)\\s*->\\s*(.+?)\\s*:\\s*([+-]?\\d+(?:\\.\\d+)?)$");
    private static final String GENESIS_TRANSACTION = "Genesis Block";
    private static final Set<String> EXEMPT_BALANCE_ACCOUNTS =
            Set.of("network", "network-fee", "system", "coinbase", "genesis");
    private static final Logger LOGGER = Logger.getLogger(Blockchain.class.getName());

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
                List.of("network -> alice : 10", "alice -> bob : 10", "bob -> carol : 5")));
        return new Blockchain(defaultBlocks);
    }

    public int size() {
        return blocks.size();
    }

    public List<Block> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    public Block getBlock(int index) {
        return blocks.get(index);
    }

    public ValidationResult validate() {
        assert blocks != null : "Block list must not be null.";
        if (blocks.isEmpty()) {
            return invalidWithLog("Blockchain must contain at least one block.");
        }

        LOGGER.fine(() -> "Starting blockchain validation for " + blocks.size() + " blocks.");
        Map<String, BigDecimal> balances = new HashMap<>();
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            assert block != null : "Block must not be null.";
            if (block.getIndex() != i) {
                return invalidWithLog("Invalid block index at Block " + i + ".");
            }

            String computedHash = block.computeCurrentHash();
            assert computedHash != null : "Computed hash must not be null.";
            if (!computedHash.equals(block.getCurrentHash())) {
                return invalidWithLog("Hash mismatch at Block " + i + ".");
            }

            if (!block.hasValidTransactionData()) {
                return invalidWithLog("Invalid transaction data at Block " + i + ": contains blank transaction.");
            }

            if (i == 0) {
                if (!GENESIS_PREVIOUS_HASH.equals(block.getPreviousHash())) {
                    return invalidWithLog("Invalid previous hash linkage at Block " + i + ".");
                }
                if (!isValidGenesisBlock(block)) {
                    return invalidWithLog("Invalid genesis block transaction data at Block 0.");
                }
                continue;
            }

            Block previousBlock = blocks.get(i - 1);
            assert previousBlock != null : "Previous block must not be null.";
            if (!previousBlock.getCurrentHash().equals(block.getPreviousHash())) {
                return invalidWithLog("Invalid previous hash linkage at Block " + i + ".");
            }

            ValidationResult transferValidation = validateTransferTransactions(block, i, balances);
            if (!transferValidation.isValid()) {
                return transferValidation;
            }
        }

        LOGGER.fine("Blockchain validation completed successfully.");
        return ValidationResult.valid();
    }

    public void addTransactions(List<String> transactions) {
        Objects.requireNonNull(transactions);
        if (transactions.isEmpty()) {
            throw new IllegalArgumentException("Transactions cannot be empty.");
        }

        Block previousBlock = blocks.get(blocks.size() - 1);
        Block newBlock = new Block(
                previousBlock.getIndex() + 1,
                LocalDateTime.now(),
                previousBlock.getCurrentHash(),
                transactions);
        blocks.add(newBlock);
    }

    public double getBalance(String walletName) {
        return getPreciseBalance(walletName).doubleValue();
    }

    public BigDecimal getPreciseBalance(String walletName) {
        String normalizedWalletName = walletName == null ? "" : walletName.trim();
        BigDecimal balance = BigDecimal.ZERO;
        for (Block block : blocks) {
            for (String transaction : block.getTransactions()) {
                balance = balance.add(parseTransactionAmount(normalizedWalletName, transaction));
            }
        }
        return balance;
    }

    private BigDecimal parseTransactionAmount(String walletName, String transaction) {
        Matcher matcher = TRANSACTION_PATTERN.matcher(transaction);
        if (!matcher.matches()) {
            return BigDecimal.ZERO;
        }

        String sender = matcher.group(1).trim();
        String receiver = matcher.group(2).trim();
        BigDecimal amount = new BigDecimal(matcher.group(3));
        BigDecimal delta = BigDecimal.ZERO;
        if (sender.equalsIgnoreCase(walletName)) {
            delta = delta.subtract(amount);
        }
        if (receiver.equalsIgnoreCase(walletName)) {
            delta = delta.add(amount);
        }
        return delta;
    }

    private boolean isValidGenesisBlock(Block block) {
        List<String> transactions = block.getTransactions();
        return transactions.size() == 1 && GENESIS_TRANSACTION.equals(transactions.get(0).trim());
    }

    private ValidationResult validateTransferTransactions(Block block, int blockIndex,
                                                          Map<String, BigDecimal> balances) {
        List<String> transactions = block.getTransactions();
        assert transactions != null : "Block transactions must not be null.";
        for (int transactionIndex = 0; transactionIndex < transactions.size(); transactionIndex++) {
            String transaction = transactions.get(transactionIndex);
            Matcher matcher = TRANSACTION_PATTERN.matcher(transaction);
            if (!matcher.matches()) {
                return invalidWithLog("Invalid transaction format at Block " + blockIndex
                        + ", Transaction " + transactionIndex + ": " + transaction);
            }

            String sender = matcher.group(1).trim();
            String receiver = matcher.group(2).trim();
            BigDecimal amount = new BigDecimal(matcher.group(3));
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return invalidWithLog("Invalid transaction amount at Block " + blockIndex
                        + ", Transaction " + transactionIndex + ": " + amount.stripTrailingZeros().toPlainString());
            }
            assert amount.compareTo(BigDecimal.ZERO) > 0 : "Validated amount should be positive.";

            String normalizedSender = sender.toLowerCase();
            String normalizedReceiver = receiver.toLowerCase();
            if (!isExemptAccount(normalizedSender)) {
                BigDecimal senderBalance = balances.getOrDefault(normalizedSender, BigDecimal.ZERO);
                if (senderBalance.compareTo(amount) < 0) {
                    return invalidWithLog("Insufficient balance at Block " + blockIndex
                            + ", Transaction " + transactionIndex + ": sender '" + sender
                            + "' has " + senderBalance.stripTrailingZeros().toPlainString()
                            + ", needs " + amount.stripTrailingZeros().toPlainString() + ".");
                }
                balances.put(normalizedSender, senderBalance.subtract(amount));
            }
            if (!isExemptAccount(normalizedReceiver)) {
                BigDecimal receiverBalance = balances.getOrDefault(normalizedReceiver, BigDecimal.ZERO);
                balances.put(normalizedReceiver, receiverBalance.add(amount));
            }
        }
        return ValidationResult.valid();
    }

    private boolean isExemptAccount(String normalizedAccountName) {
        return EXEMPT_BALANCE_ACCOUNTS.contains(normalizedAccountName);
    }

    private ValidationResult invalidWithLog(String reason) {
        LOGGER.warning(reason);
        return ValidationResult.invalid(reason);
    }
}
