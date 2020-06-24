/*
 * TryParseLongTest.java
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

import static com.github.robtimus.tryparse.TryParse.tryParseLong;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigInteger;
import java.util.OptionalLong;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
class TryParseLongTest {

    @Test
    void testTryParseLong() {
        checkNegativeNumber(-128);
        for (long i = -32; i < 0; i++) {
            checkNegativeNumber(i);
        }
        for (long i = Long.MIN_VALUE; i <= Long.MIN_VALUE + 5; i++) {
            checkNegativeNumber(i);
        }

        checkNonNegativeNumber(128);
        for (long i = 0; i <= 31; i++) {
            checkNonNegativeNumber(i);
        }
        for (long i = Long.MAX_VALUE; i > Long.MAX_VALUE - 5; i--) {
            checkNonNegativeNumber(i);
        }

        final BigInteger minValue = BigInteger.valueOf(Long.MIN_VALUE);
        final BigInteger minLimit = minValue.subtract(BigInteger.valueOf(5));
        final BigInteger maxValue = BigInteger.valueOf(Long.MAX_VALUE);
        final BigInteger maxLimit = maxValue.add(BigInteger.valueOf(5));

        for (BigInteger i = minValue.subtract(BigInteger.ONE); i.compareTo(minLimit) >= 0; i = i.subtract(BigInteger.ONE)) {
            checkNegativeOverflow(i);
        }
        for (BigInteger i = maxValue.add(BigInteger.ONE); i.compareTo(maxLimit) <= 0; i = i.add(BigInteger.ONE)) {
            checkPositiveOverflow(i);
        }
        int m = 0;
        for (BigInteger i = minValue.multiply(BigInteger.TEN); m < 5; i = i.multiply(BigInteger.TEN), m++) {
            checkNegativeOverflow(i);
        }
        m = 0;
        for (BigInteger i = maxValue.multiply(BigInteger.TEN); m < 5; i = i.multiply(BigInteger.TEN), m++) {
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

    private void checkNegativeNumber(long i) {
        for (int radix = Character.MIN_RADIX; radix <= Character.MAX_RADIX; radix++) {
            checkWithAndWithoutSubsequences(i, Long.toString(i, radix), radix);
            checkWithAndWithoutSubsequences(i, Long.toString(i, radix).replace("-", "-0000"), radix);
            checkFailureWithAndWithoutSubsequences(Long.toString(i, radix).replace("-", "--"), radix);
            checkFailureWithAndWithoutSubsequences(Long.toString(i, radix).replace("-", "-+"), radix);
            checkFailureWithAndWithoutSubsequences(Long.toString(i, radix).replace("-", "+-"), radix);
            checkFailureWithAndWithoutSubsequences(Long.toString(i, radix).replace("-", "++"), radix);
        }
        checkWithAndWithoutSubsequences(i, Long.toString(i));
        checkWithAndWithoutSubsequences(i, Long.toString(i).replace("-", "-0000"));
        checkFailureWithAndWithoutSubsequences(Long.toString(i).replace("-", "--"));
        checkFailureWithAndWithoutSubsequences(Long.toString(i).replace("-", "-+"));
        checkFailureWithAndWithoutSubsequences(Long.toString(i).replace("-", "+-"));
        checkFailureWithAndWithoutSubsequences(Long.toString(i).replace("-", "++"));
    }

    private void checkNonNegativeNumber(long i) {
        for (int radix = Character.MIN_RADIX; radix <= Character.MAX_RADIX; radix++) {
            checkWithAndWithoutSubsequences(i, Long.toString(i, radix), radix);
            checkWithAndWithoutSubsequences(i, "+" + Long.toString(i, radix), radix);
            checkWithAndWithoutSubsequences(i, "0000" + Long.toString(i, radix), radix);
            checkWithAndWithoutSubsequences(i, "+0000" + Long.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("--" + Long.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("-+" + Long.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("+-" + Long.toString(i, radix), radix);
            checkFailureWithAndWithoutSubsequences("++" + Long.toString(i, radix), radix);
        }
        checkWithAndWithoutSubsequences(i, Long.toString(i));
        checkWithAndWithoutSubsequences(i, "+" + Long.toString(i));
        checkWithAndWithoutSubsequences(i, "0000" + Long.toString(i));
        checkWithAndWithoutSubsequences(i, "+0000" + Long.toString(i));
        checkFailureWithAndWithoutSubsequences("--" + Long.toString(i));
        checkFailureWithAndWithoutSubsequences("-+" + Long.toString(i));
        checkFailureWithAndWithoutSubsequences("+-" + Long.toString(i));
        checkFailureWithAndWithoutSubsequences("++" + Long.toString(i));
    }

    private void checkNegativeOverflow(BigInteger i) {
        for (int radix = Character.MIN_RADIX; radix <= Character.MAX_RADIX; radix++) {
            checkFailureWithAndWithoutSubsequences(i.toString(radix), radix);
            checkFailureWithAndWithoutSubsequences(i.toString(radix).replace("-", "-0000"), radix);
            checkFailureWithAndWithoutSubsequences(i.toString(radix).replace("-", "--"), radix);
            checkFailureWithAndWithoutSubsequences(i.toString(radix).replace("-", "-+"), radix);
            checkFailureWithAndWithoutSubsequences(i.toString(radix).replace("-", "+-"), radix);
            checkFailureWithAndWithoutSubsequences(i.toString(radix).replace("-", "++"), radix);
        }
        checkFailureWithAndWithoutSubsequences(i.toString());
        checkFailureWithAndWithoutSubsequences(i.toString().replace("-", "-0000"));
        checkFailureWithAndWithoutSubsequences(i.toString().replace("-", "--"));
        checkFailureWithAndWithoutSubsequences(i.toString().replace("-", "-+"));
        checkFailureWithAndWithoutSubsequences(i.toString().replace("-", "+-"));
        checkFailureWithAndWithoutSubsequences(i.toString().replace("-", "++"));
    }

    private void checkPositiveOverflow(BigInteger i) {
        for (int radix = Character.MIN_RADIX; radix <= Character.MAX_RADIX; radix++) {
            checkFailureWithAndWithoutSubsequences(i.toString(radix), radix);
            checkFailureWithAndWithoutSubsequences("+" + i.toString(radix), radix);
            checkFailureWithAndWithoutSubsequences("0000" + i.toString(radix), radix);
            checkFailureWithAndWithoutSubsequences("+0000" + i.toString(radix), radix);
            checkFailureWithAndWithoutSubsequences("--" + i.toString(radix), radix);
            checkFailureWithAndWithoutSubsequences("++" + i.toString(radix), radix);
            checkFailureWithAndWithoutSubsequences("-+" + i.toString(radix), radix);
            checkFailureWithAndWithoutSubsequences("++" + i.toString(radix), radix);
        }
        checkFailureWithAndWithoutSubsequences(i.toString());
        checkFailureWithAndWithoutSubsequences("+" + i.toString());
        checkFailureWithAndWithoutSubsequences("0000" + i.toString());
        checkFailureWithAndWithoutSubsequences("+0000" + i.toString());
        checkFailureWithAndWithoutSubsequences("--" + i.toString());
        checkFailureWithAndWithoutSubsequences("++" + i.toString());
        checkFailureWithAndWithoutSubsequences("-+" + i.toString());
        checkFailureWithAndWithoutSubsequences("++" + i.toString());
    }

    private void checkWithAndWithoutSubsequences(long i, String input) {
        final String prefix = "prefix";
        final String postfix = "postfix";
        String s = prefix + input + postfix;

        check(i, input);
        check(i, s, prefix.length(), s.length() - postfix.length());
    }

    private void checkWithAndWithoutSubsequences(long i, String input, int radix) {
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

    private void check(long expected, String input) {
        Long.parseLong(input);
        assertEquals(OptionalLong.of(expected), tryParseLong(input));
    }

    private void check(long expected, String input, int radix) {
        Long.parseLong(input, radix);
        assertEquals(OptionalLong.of(expected), tryParseLong(input, radix));
    }

    private void check(long expected, String input, int start, int end) {
        // Long.parseLong with indexing is not yet available, use substring
        Long.parseLong(input.substring(start, end));
        assertEquals(OptionalLong.of(expected), tryParseLong(input, start, end));
    }

    private void check(long expected, String input, int start, int end, int radix) {
        // Long.parseLong with indexing is not yet available, use substring
        Long.parseLong(input.substring(start, end), radix);
        assertEquals(OptionalLong.of(expected), tryParseLong(input, start, end, radix));
    }

    private void checkFailure(String input) {
        assertThrows(NumberFormatException.class, () -> Long.parseLong(input));
        assertEquals(OptionalLong.empty(), tryParseLong(input));
    }

    private void checkFailure(String input, int radix) {
        assertThrows(NumberFormatException.class, () -> Long.parseLong(input, radix));
        assertEquals(OptionalLong.empty(), tryParseLong(input, radix));
    }

    private void checkFailure(String input, int start, int end) {
        // Long.parseLong with indexing is not yet available, use substring
        assertThrows(NumberFormatException.class, () -> Long.parseLong(input.substring(start, end)));
        assertEquals(OptionalLong.empty(), tryParseLong(input, start, end));
    }

    private void checkFailure(String input, int start, int end, int radix) {
        // Long.parseLong with indexing is not yet available, use substring
        assertThrows(NumberFormatException.class, () -> Long.parseLong(input.substring(start, end), radix));
        assertEquals(OptionalLong.empty(), tryParseLong(input, start, end, radix));
    }

    private void checkIllegalArgumentException(String input, int start, int end, int radix) {
        // Long.parseLong with indexing is not yet available, use substring
        // Long.parseLong throws NumberFormatException for invalid radixes instead of IllegalArgumentException
        assertThrows(NumberFormatException.class, () -> Long.parseLong(input.substring(start, end), radix));
        assertThrows(IllegalArgumentException.class, () -> tryParseLong(input, start, end, radix));
    }

    private void checkIndexOutOfBoundsException(String input, int start, int end, int radix) {
        // Long.parseLong with indexing is not yet available, use substring
        assertThrows(IndexOutOfBoundsException.class, () -> Long.parseLong(input.substring(start, end), radix));
        assertThrows(IndexOutOfBoundsException.class, () -> tryParseLong(input, start, end, radix));
    }

    private void checkNull(int radix) {
        assertEquals(OptionalLong.empty(), tryParseLong(null, radix));
    }

    private void checkNull(int start, int end, int radix) {
        assertEquals(OptionalLong.empty(), tryParseLong(null, start, end, radix));
    }
}
