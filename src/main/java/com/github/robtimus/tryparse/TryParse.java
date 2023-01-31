/*
 * TryParse.java
 * Copyright 2019 Rob Spoor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.robtimus.tryparse;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalQuery;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * Provides functionality to parse to {@link Optional} and its primitive versions.
 *
 * @author Rob Spoor
 */
public final class TryParse {

    private static final int DEFAULT_RADIX = 10;

    private TryParse() {
    }

    // int

    /**
     * Tries to parse the given {@link CharSequence}.
     * This method is a special case of {@link #tryParseInt(CharSequence, int)} using radix 10.
     *
     * @param cs The {@code CharSequence} to parse.
     * @return An {@code OptionalInt} with the result of parsing the given portion of the given {@code CharSequence}, or {@link OptionalInt#empty()}
     *         if parsing failed.
     */
    @NativeParsing
    public static OptionalInt tryParseInt(CharSequence cs) {
        return tryParseInt(cs, DEFAULT_RADIX);
    }

    /**
     * Tries to parse the given {@link CharSequence}.
     * This method is like {@code Integer.parseInt} but instead of throwing a {@link NumberFormatException} it will return an empty
     * {@link OptionalInt}.
     *
     * @param cs The {@code CharSequence} to parse.
     * @param radix The radix to use, between {@link Character#MIN_RADIX} and {@link Character#MAX_RADIX} inclusive.
     * @return An {@code OptionalInt} with the result of parsing the given portion of the given {@code CharSequence}, or {@link OptionalInt#empty()}
     *         if parsing failed.
     * @throws IllegalArgumentException If the given radix is invalid.
     */
    @NativeParsing
    public static OptionalInt tryParseInt(CharSequence cs, int radix) {
        return cs == null ? OptionalInt.empty() : tryParseInt(cs, 0, cs.length(), radix);
    }

    /**
     * Tries to parse the portion of the given {@link CharSequence} from the given start and end indexes.
     * This method is a special case of {@link #tryParseInt(CharSequence, int, int, int)} using radix 10.
     *
     * @param cs The {@code CharSequence} to parse.
     * @param start The index in the {@code CharSequence} to start parsing, inclusive.
     * @param end The index in the {@code CharSequence} to end parsing, exclusive.
     * @return An {@code OptionalInt} with the result of parsing the given portion of the given {@code CharSequence},
     *         or {@link OptionalInt#empty()} if parsing failed.
     * @throws IndexOutOfBoundsException If the given start index is negative or larger than the given end index,
     *                                       or if the given end index is larger than the given {@code CharSequence}'s length.
     */
    @NativeParsing
    public static OptionalInt tryParseInt(CharSequence cs, int start, int end) {
        return tryParseInt(cs, start, end, DEFAULT_RADIX);
    }

    /**
     * Tries to parse the portion of the given {@link CharSequence} from the given start and end indexes.
     * This method is like {@code Integer.parseInt} but instead of throwing a {@link NumberFormatException} it will return an empty
     * {@link OptionalInt}.
     *
     * @param cs The {@code CharSequence} to parse.
     * @param start The index in the {@code CharSequence} to start parsing, inclusive.
     * @param end The index in the {@code CharSequence} to end parsing, exclusive.
     * @param radix The radix to use, between {@link Character#MIN_RADIX} and {@link Character#MAX_RADIX} inclusive.
     * @return An {@code OptionalInt} with the result of parsing the given portion of the given {@code CharSequence},
     *         or {@link OptionalInt#empty()} if parsing failed.
     * @throws IndexOutOfBoundsException If the given start index is negative or larger than the given end index,
     *                                       or if the given end index is larger than the given {@code CharSequence}'s length.
     * @throws IllegalArgumentException If the given radix is invalid.
     */
    @NativeParsing
    public static OptionalInt tryParseInt(CharSequence cs, int start, int end, int radix) {
        if (cs == null) {
            return OptionalInt.empty();
        }

        validateCharSequence(cs, start, end);
        validateRadix(radix);

        if (start == end) {
            return OptionalInt.empty();
        }

        final char first = cs.charAt(start);

        // To use one block of code for both positive and negative numbers, use negative numbers.
        // That will fit both Integer.MIN_VALUE and -Integer.MAX_VALUE

        final boolean negative = first == '-';
        final int limit = negative ? Integer.MIN_VALUE : -Integer.MAX_VALUE;
        int index = start;

        if (first == '-' || first == '+') {
            index++;
        }

        if (index == end) {
            return OptionalInt.empty();
        }

        final int multiplyBound = limit / radix;
        int result = 0;

        // Invariants:
        // P0: 0 >= result >= limit
        // P1: 0 >= multiplyBound * radix >= limit && (multiplyBound - 1) * radix < limit

        // The loop should continuously calculate result = result * radix - next digit, and abort with OptionalInt.empty() if:
        // a) an invalid digit is encountered
        // b) result < multiplyBound === result <= multiplyBound - 1, because then result * radix < limit
        // c) result < limit + digit === result - digit < limit
        while (index < end) {
            int digit = Character.digit(cs.charAt(index), radix);
            if (digit == -1) {
                return OptionalInt.empty();
            }
            if (result < multiplyBound) {
                return OptionalInt.empty();
            }
            result *= radix;
            if (result < limit + digit) {
                return OptionalInt.empty();
            }
            result -= digit;
            index++;
        }
        return OptionalInt.of(negative ? result : -result);
    }

    /**
     * Tries to parse the given {@link CharSequence}.
     * This method is a special case of {@link #tryParseUnsignedInt(CharSequence, int)} using radix 10.
     *
     * @param cs The {@code CharSequence} to parse.
     * @return An {@code OptionalInt} with the result of parsing the given portion of the given {@code CharSequence}, or {@link OptionalInt#empty()}
     *         if parsing failed.
     */
    @NativeParsing
    public static OptionalInt tryParseUnsignedInt(CharSequence cs) {
        return tryParseUnsignedInt(cs, DEFAULT_RADIX);
    }

    /**
     * Tries to parse the given {@link CharSequence}.
     * This method is like {@code Integer.parseUnsignedInt} but instead of throwing a {@link NumberFormatException} it will return an empty
     * {@link OptionalInt}.
     *
     * @param cs The {@code CharSequence} to parse.
     * @param radix The radix to use, between {@link Character#MIN_RADIX} and {@link Character#MAX_RADIX} inclusive.
     * @return An {@code OptionalInt} with the result of parsing the given portion of the given {@code CharSequence}, or {@link OptionalInt#empty()}
     *         if parsing failed.
     * @throws IllegalArgumentException If the given radix is invalid.
     */
    @NativeParsing
    public static OptionalInt tryParseUnsignedInt(CharSequence cs, int radix) {
        return cs == null ? OptionalInt.empty() : tryParseUnsignedInt(cs, 0, cs.length(), radix);
    }

    /**
     * Tries to parse the portion of the given {@link CharSequence} from the given start and end indexes.
     * This method is a special case of {@link #tryParseUnsignedInt(CharSequence, int, int, int)} using radix 10.
     *
     * @param cs The {@code CharSequence} to parse.
     * @param start The index in the {@code CharSequence} to start parsing, inclusive.
     * @param end The index in the {@code CharSequence} to end parsing, exclusive.
     * @return An {@code OptionalInt} with the result of parsing the given portion of the given {@code CharSequence},
     *         or {@link OptionalInt#empty()} if parsing failed.
     * @throws IndexOutOfBoundsException If the given start index is negative or larger than the given end index,
     *                                       or if the given end index is larger than the given {@code CharSequence}'s length.
     */
    @NativeParsing
    public static OptionalInt tryParseUnsignedInt(CharSequence cs, int start, int end) {
        return tryParseUnsignedInt(cs, start, end, DEFAULT_RADIX);
    }

    /**
     * Tries to parse the portion of the given {@link CharSequence} from the given start and end indexes.
     * This method is like {@code Integer.parseUnsignedInt} but instead of throwing a {@link NumberFormatException} it will return an empty
     * {@link OptionalInt}.
     *
     * @param cs The {@code CharSequence} to parse.
     * @param start The index in the {@code CharSequence} to start parsing, inclusive.
     * @param end The index in the {@code CharSequence} to end parsing, exclusive.
     * @param radix The radix to use, between {@link Character#MIN_RADIX} and {@link Character#MAX_RADIX} inclusive.
     * @return An {@code OptionalInt} with the result of parsing the given portion of the given {@code CharSequence},
     *         or {@link OptionalInt#empty()} if parsing failed.
     * @throws IndexOutOfBoundsException If the given start index is negative or larger than the given end index,
     *                                       or if the given end index is larger than the given {@code CharSequence}'s length.
     * @throws IllegalArgumentException If the given radix is invalid.
     */
    @NativeParsing
    public static OptionalInt tryParseUnsignedInt(CharSequence cs, int start, int end, int radix) {
        if (cs == null) {
            return OptionalInt.empty();
        }

        validateCharSequence(cs, start, end);
        validateRadix(radix);

        if (start == end) {
            return OptionalInt.empty();
        }

        int index = cs.charAt(start) == '+' ? start + 1 : start;

        if (index == end) {
            return OptionalInt.empty();
        }

        final long limit = 0xFFFF_FFFFL;
        final long multiplyBound = limit / radix;
        long result = 0;

        // Invariants:
        // P0: 0 <= result <= limit
        // P1: 0 <= multiplyBound * radix <= limit && (multiplyBound + 1) * radix > limit

        // The loop should continuously calculate result = result * radix + next digit, and abort with OptionalLong.empty() if:
        // a) an invalid digit is encountered
        // b) result > multiplyBound === result >= multiplyBound + 1, because then result * radix > MAX_UNSIGNED_LONG_VALUE
        // c) result > limit - digit === result + digit > limit
        while (index < end) {
            int digit = Character.digit(cs.charAt(index), radix);
            if (digit == -1) {
                return OptionalInt.empty();
            }
            if (result > multiplyBound) {
                return OptionalInt.empty();
            }
            result *= radix;
            if (result > limit - digit) {
                return OptionalInt.empty();
            }
            result += digit;
            index++;
        }
        return OptionalInt.of((int) result);
    }

    // long

    /**
     * Tries to parse the given {@link CharSequence}.
     * This method is a special case of {@link #tryParseLong(CharSequence, int)} using radix 10.
     *
     * @param cs The {@code CharSequence} to parse.
     * @return An {@code OptionalLong} with the result of parsing the given portion of the given {@code CharSequence}, or {@link OptionalLong#empty()}
     *         if parsing failed.
     */
    @NativeParsing
    public static OptionalLong tryParseLong(CharSequence cs) {
        return tryParseLong(cs, DEFAULT_RADIX);
    }

    /**
     * Tries to parse the given {@link CharSequence}.
     * This method is like {@code Long.parseLong} but instead of throwing a {@link NumberFormatException} it will return an empty
     * {@link OptionalLong}.
     *
     * @param cs The {@code CharSequence} to parse.
     * @param radix The radix to use, between {@link Character#MIN_RADIX} and {@link Character#MAX_RADIX} inclusive.
     * @return An {@code OptionalLong} with the result of parsing the given portion of the given {@code CharSequence}, or {@link OptionalLong#empty()}
     *         if parsing failed.
     * @throws IllegalArgumentException If the given radix is invalid.
     */
    @NativeParsing
    public static OptionalLong tryParseLong(CharSequence cs, int radix) {
        return cs == null ? OptionalLong.empty() : tryParseLong(cs, 0, cs.length(), radix);
    }

    /**
     * Tries to parse the portion of the given {@link CharSequence} from the given start and end indexes.
     * This method is a special case of {@link #tryParseLong(CharSequence, int, int, int)} using radix 10.
     *
     * @param cs The {@code CharSequence} to parse.
     * @param start The index in the {@code CharSequence} to start parsing, inclusive.
     * @param end The index in the {@code CharSequence} to end parsing, exclusive.
     * @return An {@code OptionalLong} with the result of parsing the given portion of the given {@code CharSequence},
     *         or {@link OptionalLong#empty()} if parsing failed.
     * @throws IndexOutOfBoundsException If the given start index is negative or larger than the given end index,
     *                                       or if the given end index is larger than the given {@code CharSequence}'s length.
     */
    @NativeParsing
    public static OptionalLong tryParseLong(CharSequence cs, int start, int end) {
        return tryParseLong(cs, start, end, DEFAULT_RADIX);
    }

    /**
     * Tries to parse the portion of the given {@link CharSequence} from the given start and end indexes.
     * This method is like {@code Long.parseLong} but instead of throwing a {@link NumberFormatException} it will return an empty
     * {@link OptionalLong}.
     *
     * @param cs The {@code CharSequence} to parse.
     * @param start The index in the {@code CharSequence} to start parsing, inclusive.
     * @param end The index in the {@code CharSequence} to end parsing, exclusive.
     * @param radix The radix to use, between {@link Character#MIN_RADIX} and {@link Character#MAX_RADIX} inclusive.
     * @return An {@code OptionalLong} with the result of parsing the given portion of the given {@code CharSequence},
     *         or {@link OptionalLong#empty()} if parsing failed.
     * @throws IndexOutOfBoundsException If the given start index is negative or larger than the given end index,
     *                                       or if the given end index is larger than the given {@code CharSequence}'s length.
     * @throws IllegalArgumentException If the given radix is invalid.
     */
    @NativeParsing
    public static OptionalLong tryParseLong(CharSequence cs, int start, int end, int radix) {
        if (cs == null) {
            return OptionalLong.empty();
        }

        validateCharSequence(cs, start, end);
        validateRadix(radix);

        if (start == end) {
            return OptionalLong.empty();
        }

        final char first = cs.charAt(start);

        // To use one block of code for both positive and negative numbers, use negative numbers.
        // That will fit both Long.MIN_VALUE and -Long.MAX_VALUE

        final boolean negative = first == '-';
        final long limit = negative ? Long.MIN_VALUE : -Long.MAX_VALUE;
        int index = start;

        if (first == '-' || first == '+') {
            index++;
        }

        if (index == end) {
            return OptionalLong.empty();
        }

        final long multiplyBound = limit / radix;
        long result = 0;

        // Invariants:
        // P0: 0 >= result >= limit
        // P1: 0 >= multiplyBound * radix >= limit && (multiplyBound - 1) * radix < limit

        // The loop should continuously calculate result = result * radix - next digit, and abort with OptionalLong.empty() if:
        // a) an invalid digit is encountered
        // b) result < multiplyBound === result <= multiplyBound - 1, because then result * radix < limit
        // c) result < limit + digit === result - digit < limit
        while (index < end) {
            int digit = Character.digit(cs.charAt(index), radix);
            if (digit == -1) {
                return OptionalLong.empty();
            }
            if (result < multiplyBound) {
                return OptionalLong.empty();
            }
            result *= radix;
            if (result < limit + digit) {
                return OptionalLong.empty();
            }
            result -= digit;
            index++;
        }
        return OptionalLong.of(negative ? result : -result);
    }

    /**
     * Tries to parse the given {@link CharSequence}.
     * This method is a special case of {@link #tryParseUnsignedLong(CharSequence, int)} using radix 10.
     *
     * @param cs The {@code CharSequence} to parse.
     * @return An {@code OptionalLong} with the result of parsing the given portion of the given {@code CharSequence}, or {@link OptionalLong#empty()}
     *         if parsing failed.
     */
    @NativeParsing
    public static OptionalLong tryParseUnsignedLong(CharSequence cs) {
        return tryParseUnsignedLong(cs, DEFAULT_RADIX);
    }

    /**
     * Tries to parse the given {@link CharSequence}.
     * This method is like {@code Long.parseUnsignedLong} but instead of throwing a {@link NumberFormatException} it will return an empty
     * {@link OptionalLong}.
     *
     * @param cs The {@code CharSequence} to parse.
     * @param radix The radix to use, between {@link Character#MIN_RADIX} and {@link Character#MAX_RADIX} inclusive.
     * @return An {@code OptionalLong} with the result of parsing the given portion of the given {@code CharSequence}, or {@link OptionalLong#empty()}
     *         if parsing failed.
     * @throws IllegalArgumentException If the given radix is invalid.
     */
    @NativeParsing
    public static OptionalLong tryParseUnsignedLong(CharSequence cs, int radix) {
        return cs == null ? OptionalLong.empty() : tryParseUnsignedLong(cs, 0, cs.length(), radix);
    }

    /**
     * Tries to parse the portion of the given {@link CharSequence} from the given start and end indexes.
     * This method is a special case of {@link #tryParseUnsignedLong(CharSequence, int, int, int)} using radix 10.
     *
     * @param cs The {@code CharSequence} to parse.
     * @param start The index in the {@code CharSequence} to start parsing, inclusive.
     * @param end The index in the {@code CharSequence} to end parsing, exclusive.
     * @return An {@code OptionalLong} with the result of parsing the given portion of the given {@code CharSequence},
     *         or {@link OptionalLong#empty()} if parsing failed.
     * @throws IndexOutOfBoundsException If the given start index is negative or larger than the given end index,
     *                                       or if the given end index is larger than the given {@code CharSequence}'s length.
     */
    @NativeParsing
    public static OptionalLong tryParseUnsignedLong(CharSequence cs, int start, int end) {
        return tryParseUnsignedLong(cs, start, end, DEFAULT_RADIX);
    }

    /**
     * Tries to parse the portion of the given {@link CharSequence} from the given start and end indexes.
     * This method is like {@code Long.parseUnsignedLong} but instead of throwing a {@link NumberFormatException} it will return an empty
     * {@link OptionalLong}.
     *
     * @param cs The {@code CharSequence} to parse.
     * @param start The index in the {@code CharSequence} to start parsing, inclusive.
     * @param end The index in the {@code CharSequence} to end parsing, exclusive.
     * @param radix The radix to use, between {@link Character#MIN_RADIX} and {@link Character#MAX_RADIX} inclusive.
     * @return An {@code OptionalLong} with the result of parsing the given portion of the given {@code CharSequence},
     *         or {@link OptionalLong#empty()} if parsing failed.
     * @throws IndexOutOfBoundsException If the given start index is negative or larger than the given end index,
     *                                       or if the given end index is larger than the given {@code CharSequence}'s length.
     * @throws IllegalArgumentException If the given radix is invalid.
     */
    @NativeParsing
    public static OptionalLong tryParseUnsignedLong(CharSequence cs, int start, int end, int radix) {
        if (cs == null) {
            return OptionalLong.empty();
        }

        validateCharSequence(cs, start, end);
        validateRadix(radix);

        if (start == end) {
            return OptionalLong.empty();
        }

        int index = cs.charAt(start) == '+' ? start + 1 : start;

        if (index == end) {
            return OptionalLong.empty();
        }

        final long limit = 0xFFFF_FFFF_FFFF_FFFFL;
        final long multiplyBound = UnsignedMultiplyLongBounds.get(radix);
        long result = 0;

        // Invariants (treating limit as unsigned):
        // P0: 0 <= result <= limit
        // P1: 0 <= multiplyBound * radix <= limit && (multiplyBound + 1) * radix > limit

        // The loop should continuously calculate result = result * radix + next digit, and abort with OptionalLong.empty() if:
        // a) an invalid digit is encountered
        // b) result > multiplyBound === result >= multiplyBound + 1, because then result * radix > MAX_UNSIGNED_LONG_VALUE
        // c) result > limit - digit === result + digit > limit
        while (index < end) {
            int digit = Character.digit(cs.charAt(index), radix);
            if (digit == -1) {
                return OptionalLong.empty();
            }
            if (Long.compareUnsigned(result, multiplyBound) > 0) {
                return OptionalLong.empty();
            }
            result *= radix;
            if (Long.compareUnsigned(result, limit - digit) > 0) {
                return OptionalLong.empty();
            }
            result += digit;
            index++;
        }
        return OptionalLong.of(result);
    }

    // boolean

    /**
     * Tries to parse the portion of the given {@link CharSequence} from the given start and end indexes.
     * Unlike {@link Boolean#parseBoolean(String)}, this method does not return {@code false} for any value that is not {@code "true"}.
     * Instead, it returns an empty {@link Optional} for any value that is not {@code "true"} or {@code "false"} case insensitively.
     *
     * @param cs The {@code CharSequence} to parse.
     * @return An {@code Optional} with the result of parsing the given portion of the given {@code CharSequence},
     *         or {@link Optional#empty()} if parsing failed.
     * @throws IndexOutOfBoundsException If the given start index is negative or larger than the given end index,
     *                                       or if the given end index is larger than the given {@code CharSequence}'s length.
     */
    @NativeParsing
    public static Optional<Boolean> tryParseBoolean(CharSequence cs) {
        return tryParseBoolean(cs, true);
    }

    /**
     * Tries to parse the portion of the given {@link CharSequence} from the given start and end indexes.
     * Unlike {@link Boolean#parseBoolean(String)}, this method does not return {@code false} for any value that is not {@code "true"}.
     * Instead, it returns an empty {@link Optional} for any value that is not {@code "true"} or {@code "false"}.
     *
     * @param cs The {@code CharSequence} to parse.
     * @param ignoreCase If {@code true}, ignore case when comparing characters
     * @return An {@code Optional} with the result of parsing the given portion of the given {@code CharSequence},
     *         or {@link Optional#empty()} if parsing failed.
     */
    @NativeParsing
    public static Optional<Boolean> tryParseBoolean(CharSequence cs, boolean ignoreCase) {
        return cs == null ? Optional.empty() : tryParseBoolean(cs, 0, cs.length(), ignoreCase);
    }

    /**
     * Tries to parse the portion of the given {@link CharSequence} from the given start and end indexes.
     * Unlike {@link Boolean#parseBoolean(String)}, this method does not return {@code false} for any value that is not {@code "true"}.
     * Instead, it returns an empty {@link Optional} for any value that is not {@code "true"} or {@code "false"} case insensitively.
     *
     * @param cs The {@code CharSequence} to parse.
     * @param start The index in the {@code CharSequence} to start parsing, inclusive.
     * @param end The index in the {@code CharSequence} to end parsing, exclusive.
     * @return An {@code Optional} with the result of parsing the given portion of the given {@code CharSequence},
     *         or {@link Optional#empty()} if parsing failed.
     * @throws IndexOutOfBoundsException If the given start index is negative or larger than the given end index,
     *                                       or if the given end index is larger than the given {@code CharSequence}'s length.
     */
    @NativeParsing
    public static Optional<Boolean> tryParseBoolean(CharSequence cs, int start, int end) {
        return tryParseBoolean(cs, start, end, true);
    }

    /**
     * Tries to parse the portion of the given {@link CharSequence} from the given start and end indexes.
     * Unlike {@link Boolean#parseBoolean(String)}, this method does not return {@code false} for any value that is not {@code "true"}.
     * Instead, it returns an empty {@link Optional} for any value that is not {@code "true"} or {@code "false"}.
     *
     * @param cs The {@code CharSequence} to parse.
     * @param start The index in the {@code CharSequence} to start parsing, inclusive.
     * @param end The index in the {@code CharSequence} to end parsing, exclusive.
     * @param ignoreCase If {@code true}, ignore case when comparing characters
     * @return An {@code Optional} with the result of parsing the given portion of the given {@code CharSequence},
     *         or {@link Optional#empty()} if parsing failed.
     * @throws IndexOutOfBoundsException If the given start index is negative or larger than the given end index,
     *                                       or if the given end index is larger than the given {@code CharSequence}'s length.
     */
    @NativeParsing
    public static Optional<Boolean> tryParseBoolean(CharSequence cs, int start, int end, boolean ignoreCase) {
        if (cs == null) {
            return Optional.empty();
        }

        validateCharSequence(cs, start, end);

        if (start == end) {
            return Optional.empty();
        }

        switch (end - start) {
            case 4:
                return tryParseTrue(cs, start, ignoreCase);
            case 5:
                return tryParseFalse(cs, start, ignoreCase);
            default:
                return Optional.empty();
        }
    }

    private static Optional<Boolean> tryParseTrue(CharSequence cs, int start, boolean ignoreCase) {
        int index = start;
        if (matchesChar(cs, index++, 't', ignoreCase)
                && matchesChar(cs, index++, 'r', ignoreCase)
                && matchesChar(cs, index++, 'u', ignoreCase)
                && matchesChar(cs, index, 'e', ignoreCase)) {

            return Optional.of(true);
        }
        return Optional.empty();
    }

    private static Optional<Boolean> tryParseFalse(CharSequence cs, int start, boolean ignoreCase) {
        int index = start;
        if (matchesChar(cs, index++, 'f', ignoreCase)
                && matchesChar(cs, index++, 'a', ignoreCase)
                && matchesChar(cs, index++, 'l', ignoreCase)
                && matchesChar(cs, index++, 's', ignoreCase)
                && matchesChar(cs, index, 'e', ignoreCase)) {

            return Optional.of(false);
        }
        return Optional.empty();
    }

    // double

    /**
     * Tries to parse the given {@link String}.
     * This method delegates to {@link Double#parseDouble(String)}, catching any {@link NumberFormatException}.
     *
     * @param s The {@code String} to parse.
     * @return An {@code OptionalDouble} with the result of parsing the given {@code String},
     *         or {@link OptionalDouble#empty()} if the {@code String} is {@code null} or if parsing failed.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static OptionalDouble tryParseDouble(String s) {
        if (s == null) {
            return OptionalDouble.empty();
        }
        try {
            return OptionalDouble.of(Double.parseDouble(s));
        } catch (@SuppressWarnings("unused") NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    // URI

    /**
     * Tries to parse a {@code String} into a {@link URI}.
     * This method delegates to {@link URI#URI(String)}, catching any {@link URISyntaxException}.
     *
     * @param str The {@code String} to parse.
     * @return An {@code Optional} with the result of parsing the given {@code String},
     *         or {@link Optional#empty()} if the {@code String} is {@code null} or if parsing failed.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static Optional<URI> tryParseURI(String str) {
        if (str == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(new URI(str));
        } catch (@SuppressWarnings("unused") URISyntaxException e) {
            return Optional.empty();
        }
    }

    // URL

    /**
     * Tries to parse a {@code String} into a {@link URL}.
     * This method delegates to {@link URL#URL(String)}, catching any {@link MalformedURLException}.
     *
     * @param spec The {@code String} to parse.
     * @return An {@code Optional} with the result of parsing the given {@code String},
     *         or {@link Optional#empty()} if the {@code String} is {@code null} or if parsing failed.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static Optional<URL> tryParseURL(String spec) {
        if (spec == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(new URL(spec));
        } catch (@SuppressWarnings("unused") MalformedURLException e) {
            return Optional.empty();
        }
    }

    // Instant

    /**
     * Tries to parse a {@link CharSequence} into an {@link Instant}.
     * This method delegates to {@link Instant#parse(CharSequence)}, catching any {@link DateTimeParseException}.
     *
     * @param text The {@code CharSequence} to parse.
     * @return An {@code Optional} with the result of parsing the given {@code CharSequence},
     *         or {@link Optional#empty()} if the {@code CharSequence} is {@code null} or if parsing failed.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static Optional<Instant> tryParseInstant(CharSequence text) {
        if (text == null) {
            return Optional.empty();
        }
        // DateTimeFormatter.parse with a ParsePosition still throws a DateTimeParseException, so use the try-catch method instead
        try {
            return Optional.of(Instant.parse(text));
        } catch (@SuppressWarnings("unused") DateTimeParseException e) {
            return Optional.empty();
        }
    }

    // LocalDate

    /**
     * Tries to parse a {@link CharSequence} into a {@link LocalDate}.
     * This method delegates to {@link LocalDate#parse(CharSequence)}, catching any {@link DateTimeParseException}.
     *
     * @param text The {@code CharSequence} to parse.
     * @return An {@code Optional} with the result of parsing the given {@code CharSequence},
     *         or {@link Optional#empty()} if the {@code CharSequence} is {@code null} or if parsing failed.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static Optional<LocalDate> tryParseLocalDate(CharSequence text) {
        if (text == null) {
            return Optional.empty();
        }
        // DateTimeFormatter.parse with a ParsePosition still throws a DateTimeParseException, so use the try-catch method instead
        try {
            return Optional.of(LocalDate.parse(text));
        } catch (@SuppressWarnings("unused") DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Tries to parse a {@link CharSequence} into a {@link LocalDate}.
     * This method delegates to {@link LocalDate#parse(CharSequence, DateTimeFormatter)}, catching any {@link DateTimeParseException}.
     *
     * @param text The {@code CharSequence} to parse.
     * @param formatter The {@code DateTimeFormatter} to use for parsing.
     * @return An {@code Optional} with the result of parsing the given {@code CharSequence},
     *         or {@link Optional#empty()} if the {@code CharSequence} is {@code null} or if parsing failed.
     * @throws NullPointerException If the given {@code DateTimeFormatter} is {@code null}.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static Optional<LocalDate> tryParseLocalDate(CharSequence text, DateTimeFormatter formatter) {
        if (text == null) {
            return Optional.empty();
        }
        // DateTimeFormatter.parse with a ParsePosition still throws a DateTimeParseException, so use the try-catch method instead
        try {
            return Optional.of(LocalDate.parse(text, formatter));
        } catch (@SuppressWarnings("unused") DateTimeParseException e) {
            return Optional.empty();
        }
    }

    // LocalDateTime

    /**
     * Tries to parse a {@link CharSequence} into a {@link LocalDateTime}.
     * This method delegates to {@link LocalDateTime#parse(CharSequence)}, catching any {@link DateTimeParseException}.
     *
     * @param text The {@code CharSequence} to parse.
     * @return An {@code Optional} with the result of parsing the given {@code CharSequence},
     *         or {@link Optional#empty()} if the {@code CharSequence} is {@code null} or if parsing failed.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static Optional<LocalDateTime> tryParseLocalDateTime(CharSequence text) {
        if (text == null) {
            return Optional.empty();
        }
        // DateTimeFormatter.parse with a ParsePosition still throws a DateTimeParseException, so use the try-catch method instead
        try {
            return Optional.of(LocalDateTime.parse(text));
        } catch (@SuppressWarnings("unused") DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Tries to parse a {@link CharSequence} into a {@link LocalDateTime}.
     * This method delegates to {@link LocalDateTime#parse(CharSequence, DateTimeFormatter)}, catching any {@link DateTimeParseException}.
     *
     * @param text The {@code CharSequence} to parse.
     * @param formatter The {@code DateTimeFormatter} to use for parsing.
     * @return An {@code Optional} with the result of parsing the given {@code CharSequence},
     *         or {@link Optional#empty()} if the {@code CharSequence} is {@code null} or if parsing failed.
     * @throws NullPointerException If the given {@code DateTimeFormatter} is {@code null}.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static Optional<LocalDateTime> tryParseLocalDateTime(CharSequence text, DateTimeFormatter formatter) {
        if (text == null) {
            return Optional.empty();
        }
        // DateTimeFormatter.parse with a ParsePosition still throws a DateTimeParseException, so use the try-catch method instead
        try {
            return Optional.of(LocalDateTime.parse(text, formatter));
        } catch (@SuppressWarnings("unused") DateTimeParseException e) {
            return Optional.empty();
        }
    }

    // LocalTime

    /**
     * Tries to parse a {@link CharSequence} into a {@link LocalTime}.
     * This method delegates to {@link LocalTime#parse(CharSequence)}, catching any {@link DateTimeParseException}.
     *
     * @param text The {@code CharSequence} to parse.
     * @return An {@code Optional} with the result of parsing the given {@code CharSequence},
     *         or {@link Optional#empty()} if the {@code CharSequence} is {@code null} or if parsing failed.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static Optional<LocalTime> tryParseLocalTime(CharSequence text) {
        if (text == null) {
            return Optional.empty();
        }
        // DateTimeFormatter.parse with a ParsePosition still throws a DateTimeParseException, so use the try-catch method instead
        try {
            return Optional.of(LocalTime.parse(text));
        } catch (@SuppressWarnings("unused") DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Tries to parse a {@link CharSequence} into a {@link LocalTime}.
     * This method delegates to {@link LocalTime#parse(CharSequence, DateTimeFormatter)}, catching any {@link DateTimeParseException}.
     *
     * @param text The {@code CharSequence} to parse.
     * @param formatter The {@code DateTimeFormatter} to use for parsing.
     * @return An {@code Optional} with the result of parsing the given {@code CharSequence},
     *         or {@link Optional#empty()} if the {@code CharSequence} is {@code null} or if parsing failed.
     * @throws NullPointerException If the given {@code DateTimeFormatter} is {@code null}.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static Optional<LocalTime> tryParseLocalTime(CharSequence text, DateTimeFormatter formatter) {
        if (text == null) {
            return Optional.empty();
        }
        // DateTimeFormatter.parse with a ParsePosition still throws a DateTimeParseException, so use the try-catch method instead
        try {
            return Optional.of(LocalTime.parse(text, formatter));
        } catch (@SuppressWarnings("unused") DateTimeParseException e) {
            return Optional.empty();
        }
    }

    // OffsetDateTime

    /**
     * Tries to parse a {@link CharSequence} into an {@link OffsetDateTime}.
     * This method delegates to {@link OffsetDateTime#parse(CharSequence)}, catching any {@link DateTimeParseException}.
     *
     * @param text The {@code CharSequence} to parse.
     * @return An {@code Optional} with the result of parsing the given {@code CharSequence},
     *         or {@link Optional#empty()} if the {@code CharSequence} is {@code null} or if parsing failed.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static Optional<OffsetDateTime> tryParseOffsetDateTime(CharSequence text) {
        if (text == null) {
            return Optional.empty();
        }
        // DateTimeFormatter.parse with a ParsePosition still throws a DateTimeParseException, so use the try-catch method instead
        try {
            return Optional.of(OffsetDateTime.parse(text));
        } catch (@SuppressWarnings("unused") DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Tries to parse a {@link CharSequence} into an {@link OffsetDateTime}.
     * This method delegates to {@link OffsetDateTime#parse(CharSequence, DateTimeFormatter)}, catching any {@link DateTimeParseException}.
     *
     * @param text The {@code CharSequence} to parse.
     * @param formatter The {@code DateTimeFormatter} to use for parsing.
     * @return An {@code Optional} with the result of parsing the given {@code CharSequence},
     *         or {@link Optional#empty()} if the {@code CharSequence} is {@code null} or if parsing failed.
     * @throws NullPointerException If the given {@code DateTimeFormatter} is {@code null}.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static Optional<OffsetDateTime> tryParseOffsetDateTime(CharSequence text, DateTimeFormatter formatter) {
        if (text == null) {
            return Optional.empty();
        }
        // DateTimeFormatter.parse with a ParsePosition still throws a DateTimeParseException, so use the try-catch method instead
        try {
            return Optional.of(OffsetDateTime.parse(text, formatter));
        } catch (@SuppressWarnings("unused") DateTimeParseException e) {
            return Optional.empty();
        }
    }

    // OffsetTime

    /**
     * Tries to parse a {@link CharSequence} into an {@link OffsetTime}.
     * This method delegates to {@link OffsetTime#parse(CharSequence)}, catching any {@link DateTimeParseException}.
     *
     * @param text The {@code CharSequence} to parse.
     * @return An {@code Optional} with the result of parsing the given {@code CharSequence},
     *         or {@link Optional#empty()} if the {@code CharSequence} is {@code null} or if parsing failed.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static Optional<OffsetTime> tryParseOffsetTime(CharSequence text) {
        if (text == null) {
            return Optional.empty();
        }
        // DateTimeFormatter.parse with a ParsePosition still throws a DateTimeParseException, so use the try-catch method instead
        try {
            return Optional.of(OffsetTime.parse(text));
        } catch (@SuppressWarnings("unused") DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Tries to parse a {@link CharSequence} into an {@link OffsetTime}.
     * This method delegates to {@link OffsetTime#parse(CharSequence, DateTimeFormatter)}, catching any {@link DateTimeParseException}.
     *
     * @param text The {@code CharSequence} to parse.
     * @param formatter The {@code DateTimeFormatter} to use for parsing.
     * @return An {@code Optional} with the result of parsing the given {@code CharSequence},
     *         or {@link Optional#empty()} if the {@code CharSequence} is {@code null} or if parsing failed.
     * @throws NullPointerException If the given {@code DateTimeFormatter} is {@code null}.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static Optional<OffsetTime> tryParseOffsetTime(CharSequence text, DateTimeFormatter formatter) {
        if (text == null) {
            return Optional.empty();
        }
        // DateTimeFormatter.parse with a ParsePosition still throws a DateTimeParseException, so use the try-catch method instead
        try {
            return Optional.of(OffsetTime.parse(text, formatter));
        } catch (@SuppressWarnings("unused") DateTimeParseException e) {
            return Optional.empty();
        }
    }

    // ZonedDateTime

    /**
     * Tries to parse a {@link CharSequence} into a {@link ZonedDateTime}.
     * This method delegates to {@link ZonedDateTime#parse(CharSequence)}, catching any {@link DateTimeParseException}.
     *
     * @param text The {@code CharSequence} to parse.
     * @return An {@code Optional} with the result of parsing the given {@code CharSequence},
     *         or {@link Optional#empty()} if the {@code CharSequence} is {@code null} or if parsing failed.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static Optional<ZonedDateTime> tryParseZonedDateTime(CharSequence text) {
        if (text == null) {
            return Optional.empty();
        }
        // DateTimeFormatter.parse with a ParsePosition still throws a DateTimeParseException, so use the try-catch method instead
        try {
            return Optional.of(ZonedDateTime.parse(text));
        } catch (@SuppressWarnings("unused") DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Tries to parse a {@link CharSequence} into a {@link ZonedDateTime}.
     * This method delegates to {@link ZonedDateTime#parse(CharSequence, DateTimeFormatter)}, catching any {@link DateTimeParseException}.
     *
     * @param text The {@code CharSequence} to parse.
     * @param formatter The {@code DateTimeFormatter} to use for parsing.
     * @return An {@code Optional} with the result of parsing the given {@code CharSequence},
     *         or {@link Optional#empty()} if the {@code CharSequence} is {@code null} or if parsing failed.
     * @throws NullPointerException If the given {@code DateTimeFormatter} is {@code null}.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static Optional<ZonedDateTime> tryParseZonedDateTime(CharSequence text, DateTimeFormatter formatter) {
        if (text == null) {
            return Optional.empty();
        }
        // DateTimeFormatter.parse with a ParsePosition still throws a DateTimeParseException, so use the try-catch method instead
        try {
            return Optional.of(ZonedDateTime.parse(text, formatter));
        } catch (@SuppressWarnings("unused") DateTimeParseException e) {
            return Optional.empty();
        }
    }

    // DateTimeFormatter

    /**
     * Tries to parse a {@link CharSequence} using a {@link DateTimeFormatter} and {@link TemporalQuery}.
     * This method delegates to {@link DateTimeFormatter#parse(CharSequence, TemporalQuery)}, catching any {@link DateTimeParseException}.
     *
     * @param <R> The type to parse to.
     * @param text The {@code CharSequence} to parse.
     * @param formatter The {@code DateTimeFormatter} to use for parsing.
     * @param query The query defining the type to parse to.
     * @return An {@code Optional} with the result of parsing the given {@code CharSequence},
     *         or {@link Optional#empty()} if the {@code CharSequence} is {@code null} or if parsing failed.
     * @throws NullPointerException If the given {@code DateTimeFormatter} or {@code TemporalQuery} is {@code null}.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static <R> Optional<R> tryParseTemporal(CharSequence text, DateTimeFormatter formatter, TemporalQuery<? extends R> query) {
        if (text == null) {
            return Optional.empty();
        }
        // DateTimeFormatter.parse with a ParsePosition still throws a DateTimeParseException, so use the try-catch method instead
        try {
            return Optional.of(formatter.parse(text, query));
        } catch (@SuppressWarnings("unused") DateTimeParseException e) {
            return Optional.empty();
        }
    }

    // generic

    /**
     * Tries to parse an object. This is a generic purpose method that will attempt to parse the given input using the given parser. If the parsing
     * throws any exception, this method will return {@link Optional#empty()} instead.
     *
     * @param <T> The type of object to parse.
     * @param <R> The type of the parse result.
     * @param input The object to parse.
     * @param parser The parser that will perform the parsing.
     * @return An {@code Optional} with the result of parsing the given object, or {@link Optional#empty()} if parsing failed with an exception,
     *         or if the parser returned {@code null}.
     * @throws NullPointerException If the given parser is {@code null},
     *                                  or if the given input is {@code null} and the parser does not accept {@code null} values.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static <T, R> Optional<R> tryParse(T input, ParseFunction<? super T, ? extends R, ? extends Exception> parser) {

        try {
            return Optional.ofNullable(parser.parse(input));
        } catch (@SuppressWarnings("unused") Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Tries to parse an object. This is a generic purpose method that will attempt to parse the given input using the given parser. If the parsing
     * throws an exception of the given expected exception type, this method will return {@link Optional#empty()} instead. Any other (unchecked)
     * exceptions will be thrown as-is.
     *
     * @param <T> The type of object to parse.
     * @param <R> The type of the parse result.
     * @param <X> The type of exception that occurs if parsing fails.
     * @param input The object to parse.
     * @param parser The parser that will perform the parsing.
     * @param expectedExceptionType The expected type of exception.
     * @return An {@code Optional} with the result of parsing the given object, or {@link Optional#empty()} if parsing failed with the given exception
     *         type, or if the parser returned {@code null}.
     * @throws NullPointerException If the given parser or expected exception type is {@code null},
     *                                  or if the given input is {@code null} and the parser does not accept {@code null} values.
     * @since 1.1
     */
    @ExceptionBasedParsing
    public static <T, R, X extends Exception> Optional<R> tryParse(T input, ParseFunction<? super T, ? extends R, ? extends X> parser,
            Class<X> expectedExceptionType) {

        try {
            return Optional.ofNullable(parser.parse(input));
        } catch (final Exception e) {
            if (expectedExceptionType.isInstance(e)) {
                return Optional.empty();
            }
            // parser.parse can only throw instances of expectedException as checked exception,
            // so any exception that's not an instance must be a RuntimeException
            throw (RuntimeException) e;
        }
    }

    // helper methods

    private static void validateCharSequence(CharSequence cs, int start, int end) {
        if (start < 0 || start > end || end > cs.length()) {
            throw new IndexOutOfBoundsException();
        }
    }

    @SuppressWarnings("nls")
    private static void validateRadix(int radix) {
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            throw new IllegalArgumentException("radix " + radix + " is not between Character.MIN_RADIX and Character.MAX_RADIX");
        }
    }

    private static boolean matchesChar(CharSequence cs, int index, char toMatch, boolean ignoreCase) {
        char c = cs.charAt(index);
        return c == toMatch
                || ignoreCase && Character.toLowerCase(c) == toMatch;
    }
}
