package seedu.crypto1010.service;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Captures the normalized fields needed to record a transfer.
 */
public class TransferRequest {
    private final String senderWalletName;
    private final String recipientAddress;
    private final BigDecimal amount;
    private final String speedLabel;
    private final BigDecimal fee;
    private final String note;

    public TransferRequest(String senderWalletName, String recipientAddress, BigDecimal amount,
                           String speedLabel, BigDecimal fee, String note) {
        this.senderWalletName = normalizeRequired(senderWalletName, "senderWalletName");
        this.recipientAddress = normalizeRequired(recipientAddress, "recipientAddress");
        this.speedLabel = normalizeRequired(speedLabel, "speedLabel");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.fee = Objects.requireNonNull(fee, "fee must not be null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (fee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("fee must be non-negative");
        }
        this.note = normalizeOptional(note);
    }

    public String getSenderWalletName() {
        return senderWalletName;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getSpeedLabel() {
        return speedLabel;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public String getNote() {
        return note;
    }

    private String normalizeRequired(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return trimmedValue;
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();
        return trimmedValue.isEmpty() ? null : trimmedValue;
    }
}
