package com.xenomi.acs.utils;

/**
 * Utility class for validating numeric identifiers using the
 * <b>Luhn algorithm</b> (also known as the Mod 10 algorithm).
 * <p>
 * This algorithm is widely used to validate payment card numbers
 * (credit/debit cards), IMEI numbers, and other identifiers where
 * a simple checksum is required to detect accidental errors.
 * </p>
 *
 * <h2>How it works</h2>
 * <ol>
 *   <li>Starting from the rightmost digit, move left.</li>
 *   <li>Double every second digit.</li>
 *   <li>If doubling results in a number greater than 9, subtract 9.</li>
 *   <li>Sum all digits.</li>
 *   <li>If the total sum is divisible by 10, the number is valid.</li>
 * </ol>
 *
 * <p>
 * This class is stateless, thread-safe, and designed for reuse
 * in production systems handling sensitive financial data.
 * </p>
 */
public final class LunhValidation {

    /**
     * Private constructor to prevent instantiation.
     */
    private LunhValidation() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Validates a numeric string using the Luhn algorithm.
     *
     * <p>
     * The input must contain only digits and typically be between
     * 13 and 19 characters long when used for payment card numbers.
     * Length validation is intentionally left to the caller to keep
     * this method generic and reusable.
     * </p>
     *
     * @param number the numeric string to validate (e.g. {@code "4111111111111111"})
     * @return {@code true} if the number satisfies the Luhn checksum;
     *         {@code false} otherwise
     * @throws IllegalArgumentException if {@code number} is {@code null}
     *                                  or contains non-digit characters
     */
    public static boolean isValid(String number) {
        if (number == null) {
            throw new IllegalArgumentException("Number must not be null");
        }

        if (!number.matches("\\d+")) {
            throw new IllegalArgumentException("Number must contain only digits");
        }

        int sum = 0;
        boolean alternate = false;

        for (int i = number.length() - 1; i >= 0; i--) {
            int n = number.charAt(i) - '0';

            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }

            sum += n;
            alternate = !alternate;
        }

        return sum % 10 == 0;
    }
}
