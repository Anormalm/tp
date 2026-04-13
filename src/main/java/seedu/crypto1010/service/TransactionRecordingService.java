package seedu.crypto1010.service;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.Wallet;
import seedu.crypto1010.model.WalletManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransactionRecordingService {
    static final String WALLET_NOT_FOUND_ERROR = "Error: Wallet not found.";
    static final String INSUFFICIENT_BALANCE_ERROR = "Error: Insufficient balance.";
    private static final String NETWORK_FEE_ACCOUNT = "network-fee";

    private final WalletManager walletManager;

    public TransactionRecordingService(WalletManager walletManager) {
        this.walletManager = Objects.requireNonNull(walletManager);
    }

    public void recordTransfer(TransferRequest request, Blockchain blockchain) throws Crypto1010Exception {
        Objects.requireNonNull(request);
        Objects.requireNonNull(blockchain);

        Wallet senderWallet = walletManager.findWallet(request.getSenderWalletName())
                .orElseThrow(() -> new Crypto1010Exception(WALLET_NOT_FOUND_ERROR));

        BigDecimal totalCost = request.getAmount().add(request.getFee());
        BigDecimal balance = blockchain.getPreciseBalance(senderWallet.getName());
        if (balance.compareTo(totalCost) < 0) {
            throw new Crypto1010Exception(INSUFFICIENT_BALANCE_ERROR);
        }

        String receiverAccount = walletManager.findWalletByAddress(request.getRecipientAddress())
                .map(Wallet::getName)
                .orElse(request.getRecipientAddress());

        blockchain.addTransactions(buildBlockchainTransactions(
                senderWallet.getName(),
                receiverAccount,
                request.getAmount(),
                request.getFee()));
        senderWallet.addTransaction(buildHistoryEntry(request));
    }

    private List<String> buildBlockchainTransactions(String senderWalletName, String receiverAccount,
                                                     BigDecimal amount, BigDecimal fee) {
        List<String> transactions = new ArrayList<>();
        transactions.add(formatTransaction(senderWalletName, receiverAccount, amount));
        if (fee.compareTo(BigDecimal.ZERO) > 0) {
            transactions.add(formatTransaction(senderWalletName, NETWORK_FEE_ACCOUNT, fee));
        }
        return transactions;
    }

    private String buildHistoryEntry(TransferRequest request) {
        StringBuilder history = new StringBuilder();
        history.append("to/").append(request.getRecipientAddress())
                .append(" amt/").append(request.getAmount().toPlainString())
                .append(" speed/").append(request.getSpeedLabel())
                .append(" fee/").append(request.getFee().stripTrailingZeros().toPlainString());
        if (request.getNote() != null) {
            history.append(" note/").append(request.getNote());
        }
        return history.toString();
    }

    private String formatTransaction(String sender, String receiver, BigDecimal amount) {
        return sender + " -> " + receiver + " : " + amount.stripTrailingZeros().toPlainString();
    }
}
