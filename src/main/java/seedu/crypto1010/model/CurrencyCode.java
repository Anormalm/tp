package seedu.crypto1010.model;

import java.util.Locale;
import java.util.Set;

/**
 * Normalizes and validates wallet currency codes.
 */
public final class CurrencyCode {
    public static final String GENERIC = "generic";
    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("eth", "btc");

    private CurrencyCode() {
    }

    public static String normalize(String currencyCode) {
        return currencyCode == null ? "" : currencyCode.trim().toLowerCase(Locale.ROOT);
    }

    public static String normalizeOrDefault(String currencyCode) {
        String normalized = normalize(currencyCode);
        return normalized.isEmpty() ? GENERIC : normalized;
    }

    public static boolean isValidSpecificCurrency(String currencyCode) {
        return SUPPORTED_CURRENCIES.contains(normalize(currencyCode));
    }

    public static boolean isGeneric(String currencyCode) {
        return GENERIC.equals(normalizeOrDefault(currencyCode));
    }
}
