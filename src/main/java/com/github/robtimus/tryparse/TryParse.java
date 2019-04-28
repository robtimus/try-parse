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

import java.util.Optional;
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
        throw new Error("cannot create instances of " + getClass().getName()); //$NON-NLS-1$
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
                && matchesChar(cs, index++, 'e', ignoreCase)) {

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
                && matchesChar(cs, index++, 'e', ignoreCase)) {

            return Optional.of(false);
        }
        return Optional.empty();
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
        return c == toMatch || (ignoreCase && Character.toLowerCase(c) == toMatch);
    }
}
