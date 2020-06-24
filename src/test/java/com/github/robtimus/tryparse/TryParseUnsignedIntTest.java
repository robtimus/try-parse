/*
 * TryParseUnsignedIntTest.java
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

import static com.github.robtimus.tryparse.TryParse.tryParseUnsignedInt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.OptionalInt;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class TryParseUnsignedIntTest {

    private static final long MAX_UNSIGNED_INT_VALUE = 0xFFFF_FFFFL;

    @Test
    void testTryParseUnsignedInt() {
        checkNegativeNumber(-128);
        for (int i = -32; i < 0; i++) {
            checkNegativeNumber(i);
        }
        for (int i = Integer.MIN_VALUE; i <= Integer.MIN_VALUE + 5; i++) {
            checkNegativeNumber(i);
        }

        checkNonNegativeNumber(128);
        for (int i = 0; i <= 31; i++) {
            checkNonNegativeNumber(i);
        }
        for (int i = Integer.MAX_VALUE; i > Integer.MAX_VALUE - 5; i--) {
            checkNonNegativeNumber(i);
        }

        for (long i = Integer.MAX_VALUE + 1L; i <= Integer.MAX_VALUE + 5L; i++) {
            checkPositiveNumber(i);
        }
        for (long i = MAX_UNSIGNED_INT_VALUE; i <= MAX_UNSIGNED_INT_VALUE - 5; i--) {
            checkPositiveNumber(i);
        }
        for (long i = MAX_UNSIGNED_INT_VALUE + 1L; i <= MAX_UNSIGNED_INT_VALUE + 5L; i++) {
            checkPositiveOverflow(i);
        }
        for (long i = MAX_UNSIGNED_INT_VALUE * 10L, m = 0; m < 5; i *= 10, m++) {
            checkPositiveOverflow(i);
        }

        checkFailureWithAndWithoutSubsequences("");
        checkFailureWithAndWithoutSubsequences("\u0000");
        checkFailureWithAndWithoutSubsequences("\u002f");
        checkFailureWithAndWithoutSubsequences("-");
        checkFailureWithAndWithoutSubsequences("+");
        checkFailureWithAndWithoutSubsequences("--");
        checkFailureWithAndWithoutSubsequences("-+");
        checkFailureWithAndWithoutSubsequences("+-");
        checkFailureWithAndWithoutSubsequences("++");
        checkFailureWithAndWithoutSubsequences("*100");

        checkFailure("1000000", 0, 0, 10);
        checkFailure("1000000", 7, 7, 10);

        checkIllegalArgumentException("1000000", 0, 2, Character.MAX_RADIX + 1);
        checkIllegalArgumentException("1000000", 0, 2, Character.MIN_RADIX - 1);

        checkIndexOutOfBoundsException("1000000", 10, 4, 10);
        checkIndexOutOfBoundsException("1000000", -1, 2, Character.MAX_RADIX + 1);
        checkIndexOutOfBoundsException("1000000", -1, 2, Character.MIN_RADIX - 1);
        checkIndexOutOfBoundsException("1000000", 10, 2, Character.MAX_RADIX + 1);
        checkIndexOutOfBoundsException("1000000", 10, 2, Character.MIN_RADIX - 1);
        checkIndexOutOfBoundsException("-1", 0, 3, 10);
        checkIndexOutOfBoundsException("-1", 2, 3, 10);
        checkIndexOutOfBoundsException("-1", -1, 2, 10);

        checkNull(10);
        checkNull(-1);
        checkNull(0, 1, 10);
        checkNull(-1, 0, 10);
        checkNull(0, 0, 10);
        checkNull(0, -1, 10);
        checkNull(-1, -1, -1);
    }

    private void checkNegativeNumber(int i) {
        for (int radix = Character.MIN_RADIX; radix <= Character.MAX_RADIX; radix++) {
            checkFailureWithAndWithoutSubsequences(Integer.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences(Integer.toString(i, radix).replace("-", "-0000"), radix);
            checkFailureWithAndWithoutSubsequences(Integer.toString(i, radix).replace("-", "--"), radix);
            checkFailureWithAndWithoutSubsequences(Integer.toString(i, radix).replace("-", "-+"), radix);
            checkFailureWithAndWithoutSubsequences(Integer.toString(i, radix).replace("-", "+-"), radix);
            checkFailureWithAndWithoutSubsequences(Integer.toString(i, radix).replace("-", "++"), radix);
        }
        checkFailureWithAndWithoutSubsequences(Integer.toString(i));
        checkFailureWithAndWithoutSubsequences(Integer.toString(i).replace("-", "-0000"));
        checkFailureWithAndWithoutSubsequences(Integer.toString(i).replace("-", "--"));
        checkFailureWithAndWithoutSubsequences(Integer.toString(i).replace("-", "-+"));
        checkFailureWithAndWithoutSubsequences(Integer.toString(i).replace("-", "+-"));
        checkFailureWithAndWithoutSubsequences(Integer.toString(i).replace("-", "++"));
    }

    private void checkNonNegativeNumber(int i) {
        for (int radix = Character.MIN_RADIX; radix <= Character.MAX_RADIX; radix++) {
            checkWithAndWithoutSubsequences(i, Integer.toString(i, radix), radix);
            checkWithAndWithoutSubsequences(i, "+" + Integer.toString(i, radix), radix);
            checkWithAndWithoutSubsequences(i, "0000" + Integer.toString(i, radix), radix);
            checkWithAndWithoutSubsequences(i, "+0000" + Integer.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("--" + Integer.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("-+" + Integer.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("+-" + Integer.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("++" + Integer.toString(i, radix), radix);
        }
        checkWithAndWithoutSubsequences(i, Integer.toString(i));
        checkWithAndWithoutSubsequences(i, "+" + Integer.toString(i));
        checkWithAndWithoutSubsequences(i, "0000" + Integer.toString(i));
        checkWithAndWithoutSubsequences(i, "+0000" + Integer.toString(i));
        checkFailureWithAndWithoutSubsequences("--" + Integer.toString(i));
        checkFailureWithAndWithoutSubsequences("-+" + Integer.toString(i));
        checkFailureWithAndWithoutSubsequences("+-" + Integer.toString(i));
        checkFailureWithAndWithoutSubsequences("++" + Integer.toString(i));
    }

    private void checkPositiveNumber(long i) {
        for (int radix = Character.MIN_RADIX; radix <= Character.MAX_RADIX; radix++) {
            checkWithAndWithoutSubsequences((int) i, Long.toString(i, radix), radix);
            checkWithAndWithoutSubsequences((int) i, "+" + Long.toString(i, radix), radix);
            checkWithAndWithoutSubsequences((int) i, "0000" + Long.toString(i, radix), radix);
            checkWithAndWithoutSubsequences((int) i, "+0000" + Long.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("--" + Long.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("++" + Long.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("-+" + Long.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("++" + Long.toString(i, radix), radix);
        }
        checkWithAndWithoutSubsequences((int) i, Long.toString(i));
        checkWithAndWithoutSubsequences((int) i, "+" + Long.toString(i));
        checkWithAndWithoutSubsequences((int) i, "0000" + Long.toString(i));
        checkWithAndWithoutSubsequences((int) i, "+0000" + Long.toString(i));
        checkFailureWithAndWithoutSubsequences("--" + Long.toString(i));
        checkFailureWithAndWithoutSubsequences("++" + Long.toString(i));
        checkFailureWithAndWithoutSubsequences("-+" + Long.toString(i));
        checkFailureWithAndWithoutSubsequences("++" + Long.toString(i));
    }

    private void checkPositiveOverflow(long i) {
        for (int radix = Character.MIN_RADIX; radix <= Character.MAX_RADIX; radix++) {
            checkFailureWithAndWithoutSubsequences(Long.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("+" + Long.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("0000" + Long.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("+0000" + Long.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("--" + Long.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("++" + Long.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("-+" + Long.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("++" + Long.toString(i, radix), radix);
        }
        checkFailureWithAndWithoutSubsequences(Long.toString(i));
        checkFailureWithAndWithoutSubsequences("+" + Long.toString(i));
        checkFailureWithAndWithoutSubsequences("0000" + Long.toString(i));
        checkFailureWithAndWithoutSubsequences("+0000" + Long.toString(i));
        checkFailureWithAndWithoutSubsequences("--" + Long.toString(i));
        checkFailureWithAndWithoutSubsequences("++" + Long.toString(i));
        checkFailureWithAndWithoutSubsequences("-+" + Long.toString(i));
        checkFailureWithAndWithoutSubsequences("++" + Long.toString(i));
    }

    private void checkWithAndWithoutSubsequences(int i, String input) {
        final String prefix = "prefix";
        final String postfix = "postfix";
        String s = prefix + input + postfix;

        check(i, input);
        check(i, s, prefix.length(), s.length() - postfix.length());
    }

    private void checkWithAndWithoutSubsequences(int i, String input, int radix) {
        final String prefix = "prefix";
        final String postfix = "postfix";
        String s = prefix + input + postfix;

        check(i, input, radix);
        check(i, s, prefix.length(), s.length() - postfix.length(), radix);
    }

    private void checkFailureWithAndWithoutSubsequences(String input) {
        final String prefix = "prefix";
        final String postfix = "postfix";
        String s = prefix + input + postfix;

        checkFailure(input);
        checkFailure(s, prefix.length(), s.length() - postfix.length());
    }

    private void checkFailureWithAndWithoutSubsequences(String input, int radix) {
        final String prefix = "prefix";
        final String postfix = "postfix";
        String s = prefix + input + postfix;

        checkFailure(input, radix);
        checkFailure(s, prefix.length(), s.length() - postfix.length(), radix);
    }

    private void check(int expected, String input) {
        Integer.parseUnsignedInt(input);
        assertEquals(OptionalInt.of(expected), tryParseUnsignedInt(input));
    }

    private void check(int expected, String input, int radix) {
        Integer.parseUnsignedInt(input, radix);
        assertEquals(OptionalInt.of(expected), tryParseUnsignedInt(input, radix));
    }

    private void check(int expected, String input, int start, int end) {
        // Integer.parseUnsignedInt with indexing is not yet available, use substring
        Integer.parseUnsignedInt(input.substring(start, end));
        assertEquals(OptionalInt.of(expected), tryParseUnsignedInt(input, start, end));
    }

    private void check(int expected, String input, int start, int end, int radix) {
        // Integer.parseUnsignedInt with indexing is not yet available, use substring
        Integer.parseUnsignedInt(input.substring(start, end), radix);
        assertEquals(OptionalInt.of(expected), tryParseUnsignedInt(input, start, end, radix));
    }

    private void checkFailure(String input) {
        assertThrows(NumberFormatException.class, () -> Integer.parseUnsignedInt(input));
        assertEquals(OptionalInt.empty(), tryParseUnsignedInt(input));
    }

    private void checkFailure(String input, int radix) {
        assertThrows(NumberFormatException.class, () -> Integer.parseUnsignedInt(input, radix));
        assertEquals(OptionalInt.empty(), tryParseUnsignedInt(input, radix));
    }

    private void checkFailure(String input, int start, int end) {
        // Integer.parseUnsignedInt with indexing is not yet available, use substring
        assertThrows(NumberFormatException.class, () -> Integer.parseUnsignedInt(input.substring(start, end)));
        assertEquals(OptionalInt.empty(), tryParseUnsignedInt(input, start, end));
    }

    private void checkFailure(String input, int start, int end, int radix) {
        // Integer.parseUnsignedInt with indexing is not yet available, use substring
        assertThrows(NumberFormatException.class, () -> Integer.parseUnsignedInt(input.substring(start, end), radix));
        assertEquals(OptionalInt.empty(), tryParseUnsignedInt(input, start, end, radix));
    }

    private void checkIllegalArgumentException(String input, int start, int end, int radix) {
        // Integer.parseUnsignedInt with indexing is not yet available, use substring
        // Integer.parseUnsignedInt throws NumberFormatException for invalid radixes instead of IllegalArgumentException
        assertThrows(NumberFormatException.class, () -> Integer.parseUnsignedInt(input.substring(start, end), radix));
        assertThrows(IllegalArgumentException.class, () -> tryParseUnsignedInt(input, start, end, radix));
    }

    private void checkIndexOutOfBoundsException(String input, int start, int end, int radix) {
        // Integer.parseUnsignedInt with indexing is not yet available, use substring
        assertThrows(IndexOutOfBoundsException.class, () -> Integer.parseUnsignedInt(input.substring(start, end), radix));
        assertThrows(IndexOutOfBoundsException.class, () -> tryParseUnsignedInt(input, start, end, radix));
    }

    private void checkNull(int radix) {
        assertEquals(OptionalInt.empty(), tryParseUnsignedInt(null, radix));
    }

    private void checkNull(int start, int end, int radix) {
        assertEquals(OptionalInt.empty(), tryParseUnsignedInt(null, start, end, radix));
    }
}
