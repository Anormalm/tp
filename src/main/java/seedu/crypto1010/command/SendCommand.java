package seedu.crypto1010.command;

import seedu.crypto1010.exceptions.Crypto1010Exception;
import seedu.crypto1010.model.Blockchain;
import seedu.crypto1010.model.WalletManager;
import seedu.crypto1010.service.TransactionRecordingService;
import seedu.crypto1010.service.TransferRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendCommand extends Command {
    private static final Pattern PREFIX_PATTERN = Pattern.compile("(w/|to/|amt/|speed/|fee/|note/)");
    private static final Pattern ETH_ADDRESS_PATTERN = Pattern.compile("^0x[a-fA-F0-9]{40}$");
    private static final Pattern BTC_LEGACY_ADDRESS_PATTERN =
            Pattern.compile("^[13][A-HJ-NP-Za-km-z1-9]{25,34}$");
    private static final Pattern BTC_BECH32_ADDRESS_PATTERN =
            Pattern.compile("^(bc1[qpzry9x8gf2tvdw0s3jn54khce6mua7l]{11,71}"
                    + "|BC1[QPZRY9X8GF2TVDW0S3JN54KHCE6MUA7L]{11,71})$");
    private static final Pattern SOL_ADDRESS_PATTERN = Pattern.compile("^[1-9A-HJ-NP-Za-km-z]{32,44}$");

    private static final String DEFAULT_SPEED = "standard";
    private static final BigDecimal SLOW_FEE = new BigDecimal("0.0005");
    private static final BigDecimal STANDARD_FEE = new BigDecimal("0.0010");
    private static final BigDecimal FAST_FEE = new BigDecimal("0.0020");

    private static final String HELP_DESCRIPTION = """
            Format: send w/WALLET_NAME to/RECIPIENT_ADDRESS amt/AMOUNT [speed/SPEED] [fee/FEE] [note/MEMO]
            Examples: send w/alice to/0x8b3...e21 amt/0.05
                      send w/main to/bc1qxyz...9k amt/0.001 speed/slow fee/0.00001 note/rent

            SPEED FEE and MEMO are optional arguments
            Supported SPEED values: speed/slow, speed/standard, speed/fast
            If fee/FEE is provided, it overrides any speed/SPEED
            If neither speed/ nor fee/ is provided, speed/standard is used by default
            Sends cryptocurrency from one wallet to another address
            """;
    private static final String INVALID_FORMAT_ERROR = "Error: Invalid send format. Use: send w/WALLET_NAME"
        + " to/RECIPIENT_ADDRESS amt/AMOUNT [speed/SPEED] [fee/FEE] [note/MEMO]";
    private static final String SEND_FORMAT = "Use: send w/WALLET_NAME"
            + " to/RECIPIENT_ADDRESS amt/AMOUNT [speed/SPEED] [fee/FEE] [note/MEMO]";
    private static final String AMOUNT_INVALID_ERROR = "Error: Amount must be a positive number.";
    private static final String FEE_INVALID_ERROR = "Error: Fee must be a non-negative number.";
    private static final String SPEED_INVALID_ERROR = "Error: Unsupported speed. Use speed/slow, speed/standard,"
            + " or speed/fast.";
    private static final String INVALID_ADDRESS_ERROR = "Error: Invalid recipient address.";

    private final String arguments;
    private final TransactionRecordingService transactionRecordingService;

    public SendCommand(String arguments, WalletManager walletManager) {
        super(HELP_DESCRIPTION);
        this.arguments = arguments;
        this.transactionRecordingService = new TransactionRecordingService(Objects.requireNonNull(walletManager));
    }

    @Override
    public void execute(String description, Blockchain blockchain) throws Crypto1010Exception {
        ParsedArgs parsed = parseArguments(arguments);
        if (parsed == null) {
            throw new Crypto1010Exception(INVALID_FORMAT_ERROR);
        }

        BigDecimal amount = parseDecimal(parsed.amount);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Crypto1010Exception(AMOUNT_INVALID_ERROR + " " + SEND_FORMAT);
        }

        if (!isValidAddress(parsed.recipientAddress)) {
            throw new Crypto1010Exception(INVALID_ADDRESS_ERROR + " " + SEND_FORMAT);
        }

        String speed = parsed.speed == null ? DEFAULT_SPEED : parsed.speed.toLowerCase();
        if (!isSupportedSpeed(speed)) {
            throw new Crypto1010Exception(SPEED_INVALID_ERROR + " " + SEND_FORMAT);
        }

        BigDecimal fee = resolveFee(parsed.fee, speed);
        if (fee == null) {
            throw new Crypto1010Exception(FEE_INVALID_ERROR + " " + SEND_FORMAT);
        }

        String speedLabel = parsed.fee == null ? speed : "manual";
        TransferRequest transferRequest = new TransferRequest(
                parsed.walletName,
                parsed.recipientAddress,
                amount,
                speedLabel,
                fee,
                parsed.note);
        transactionRecordingService.recordTransfer(transferRequest, blockchain);

        System.out.println("Transaction sent successfully.");
        System.out.println("Wallet: " + transferRequest.getSenderWalletName());
        System.out.println("To: " + transferRequest.getRecipientAddress());
        System.out.println("Amount: " + transferRequest.getAmount().toPlainString());
        System.out.println("Speed: " + transferRequest.getSpeedLabel());
        System.out.println("Fee: " + transferRequest.getFee().toPlainString());
        if (transferRequest.getNote() != null) {
            System.out.println("Note: " + transferRequest.getNote());
        }
    }

    private ParsedArgs parseArguments(String args) {
        if (args == null || args.isBlank()) {
            return null;
        }

        String trimmedArgs = args.trim();
        Matcher matcher = PREFIX_PATTERN.matcher(trimmedArgs);
        List<PrefixMatch> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(new PrefixMatch(matcher.group(), matcher.start()));
        }

        if (matches.isEmpty() || matches.get(0).startIndex != 0) {
            return null;
        }

        ParsedArgs parsed = new ParsedArgs();
        boolean hasWallet = false;
        boolean hasRecipient = false;
        boolean hasAmount = false;
        boolean hasSpeed = false;
        boolean hasFee = false;
        boolean hasNote = false;

        for (int i = 0; i < matches.size(); i++) {
            PrefixMatch current = matches.get(i);
            int valueStart = current.startIndex + current.prefix.length();
            int valueEnd = i + 1 < matches.size() ? matches.get(i + 1).startIndex : trimmedArgs.length();
            String value = trimmedArgs.substring(valueStart, valueEnd).trim();
            if (value.isEmpty()) {
                return null;
            }

            switch (current.prefix) {
            case "w/":
                if (hasWallet || containsWhitespace(value)) {
                    return null;
                }
                parsed.walletName = value;
                hasWallet = true;
                break;
            case "to/":
                if (hasRecipient || containsWhitespace(value)) {
                    return null;
                }
                parsed.recipientAddress = value;
                hasRecipient = true;
                break;
            case "amt/":
                if (hasAmount || containsWhitespace(value)) {
                    return null;
                }
                parsed.amount = value;
                hasAmount = true;
                break;
            case "speed/":
                if (hasSpeed || containsWhitespace(value)) {
                    return null;
                }
                parsed.speed = value;
                hasSpeed = true;
                break;
            case "fee/":
                if (hasFee || containsWhitespace(value)) {
                    return null;
                }
                parsed.fee = value;
                hasFee = true;
                break;
            case "note/":
                if (hasNote) {
                    return null;
                }
                parsed.note = value;
                hasNote = true;
                break;
            default:
                return null;
            }
        }

        if (parsed.walletName == null || parsed.recipientAddress == null || parsed.amount == null) {
            return null;
        }

        return parsed;
    }

    private BigDecimal parseDecimal(String amountStr) {
        try {
            return new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal resolveFee(String feeArgument, String speed) {
        if (feeArgument != null) {
            BigDecimal manualFee = parseDecimal(feeArgument);
            if (manualFee == null || manualFee.compareTo(BigDecimal.ZERO) < 0) {
                return null;
            }
            return manualFee;
        }

        return switch (speed) {
        case "slow" -> SLOW_FEE;
        case "standard" -> STANDARD_FEE;
        case "fast" -> FAST_FEE;
        default -> null;
        };
    }

    private boolean isSupportedSpeed(String speed) {
        return "slow".equals(speed) || "standard".equals(speed) || "fast".equals(speed);
    }

    private boolean containsWhitespace(String value) {
        return value.chars().anyMatch(Character::isWhitespace);
    }

    private boolean isValidAddress(String address) {
        if (address == null || address.isBlank() || containsWhitespace(address)) {
            return false;
        }
        return ETH_ADDRESS_PATTERN.matcher(address).matches()
                || BTC_LEGACY_ADDRESS_PATTERN.matcher(address).matches()
                || BTC_BECH32_ADDRESS_PATTERN.matcher(address).matches()
                || SOL_ADDRESS_PATTERN.matcher(address).matches();
    }

    private static class PrefixMatch {
        private final String prefix;
        private final int startIndex;

        private PrefixMatch(String prefix, int startIndex) {
            this.prefix = prefix;
            this.startIndex = startIndex;
        }
    }

    private static class ParsedArgs {
        String walletName;
        String recipientAddress;
        String amount;
        String speed;
        String fee;
        String note;
    }
}
