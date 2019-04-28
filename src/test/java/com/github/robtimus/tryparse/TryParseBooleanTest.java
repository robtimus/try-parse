/*
 * TryParseBooleanTest.java
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

import static com.github.robtimus.tryparse.TryParse.tryParseBoolean;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.Optional;
import org.junit.Test;

@SuppressWarnings({ "javadoc", "nls" })
public class TryParseBooleanTest {

    @Test
    public void testTryParseBoolean() {
        checkWithAndWithoutSubsequences(true, "true");
        checkWithAndWithoutSubsequences(true, "TRUE");
        checkWithAndWithoutSubsequences(false, "false");
        checkWithAndWithoutSubsequences(false, "FALSE");
        checkWithAndWithoutSubsequences(true, "true", false);
        checkWithAndWithoutSubsequences(false, "false", false);

        checkFailureWithAndWithoutSubsequences("tru");
        checkFailureWithAndWithoutSubsequences("falsee");

        checkFailureWithAndWithoutSubsequences("TRUE", false);
        checkFailureWithAndWithoutSubsequences("FALSE", false);

        checkFailureWithAndWithoutSubsequences("trux");
        checkFailureWithAndWithoutSubsequences("trxe");
        checkFailureWithAndWithoutSubsequences("txue");
        checkFailureWithAndWithoutSubsequences("xrue");

        checkFailureWithAndWithoutSubsequences("falsx");
        checkFailureWithAndWithoutSubsequences("falxe");
        checkFailureWithAndWithoutSubsequences("faxse");
        checkFailureWithAndWithoutSubsequences("fxlse");
        checkFailureWithAndWithoutSubsequences("xalse");

        checkFailure("true", 0, 0, true);
        checkFailure("true", 0, 3, true);
        checkFailure("true", 1, 4, true);
        checkFailure("true", 4, 4, true);
        checkFailure("false", 0, 0, true);
        checkFailure("false", 0, 4, true);
        checkFailure("false", 1, 5, true);
        checkFailure("false", 5, 5, true);

        checkIndexOutOfBoundsException("true", 10, 4, true);
        checkIndexOutOfBoundsException("true", -1, 2, true);
        checkIndexOutOfBoundsException("true", 10, 2, true);
        checkIndexOutOfBoundsException("true", 0, 5, true);
        checkIndexOutOfBoundsException("true", 4, 5, true);
        checkIndexOutOfBoundsException("true", -1, 4, true);
        checkIndexOutOfBoundsException("false", 10, 5, true);
        checkIndexOutOfBoundsException("false", -1, 2, true);
        checkIndexOutOfBoundsException("false", 10, 2, true);
        checkIndexOutOfBoundsException("false", 0, 6, true);
        checkIndexOutOfBoundsException("false", 5, 6, true);
        checkIndexOutOfBoundsException("false", -1, 5, true);

        checkNull(true);
        checkNull(false);
        checkNull(0, 1, true);
        checkNull(-1, 0, true);
        checkNull(0, 0, true);
        checkNull(0, -1, true);
        checkNull(-1, -1, false);
    }

    private void checkWithAndWithoutSubsequences(boolean b, String input) {
        final String prefix = "prefix";
        final String postfix = "postfix";
        String s = prefix + input + postfix;

        check(b, input);
        check(b, s, prefix.length(), s.length() - postfix.length());
    }

    private void checkWithAndWithoutSubsequences(boolean b, String input, boolean ignoreCase) {
        final String prefix = "prefix";
        final String postfix = "postfix";
        String s = prefix + input + postfix;

        check(b, input, ignoreCase);
        check(b, s, prefix.length(), s.length() - postfix.length(), ignoreCase);
    }

    private void checkFailureWithAndWithoutSubsequences(String input) {
        final String prefix = "prefix";
        final String postfix = "postfix";
        String s = prefix + input + postfix;

        checkFailure(input);
        checkFailure(s, prefix.length(), s.length() - postfix.length());
    }

    private void checkFailureWithAndWithoutSubsequences(String input, boolean ignoreCase) {
        final String prefix = "prefix";
        final String postfix = "postfix";
        String s = prefix + input + postfix;

        checkFailure(input, ignoreCase);
        checkFailure(s, prefix.length(), s.length() - postfix.length(), ignoreCase);
    }

    private void check(boolean expected, String input) {
        assertEquals(Optional.of(expected), tryParseBoolean(input));
    }

    private void check(boolean expected, String input, boolean ignoreCase) {
        assertEquals(Optional.of(expected), tryParseBoolean(input, ignoreCase));
    }

    private void check(boolean expected, String input, int start, int end) {
        assertEquals(Optional.of(expected), tryParseBoolean(input, start, end));
    }

    private void check(boolean expected, String input, int start, int end, boolean ignoreCase) {
        assertEquals(Optional.of(expected), tryParseBoolean(input, start, end, ignoreCase));
    }

    private void checkFailure(String input) {
        assertEquals(Optional.empty(), tryParseBoolean(input));
    }

    private void checkFailure(String input, boolean ignoreCase) {
        assertEquals(Optional.empty(), tryParseBoolean(input, ignoreCase));
    }

    private void checkFailure(String input, int start, int end) {
        assertEquals(Optional.empty(), tryParseBoolean(input, start, end));
    }

    private void checkFailure(String input, int start, int end, boolean ignoreCase) {
        assertEquals(Optional.empty(), tryParseBoolean(input, start, end, ignoreCase));
    }

    private void checkIndexOutOfBoundsException(String input, int start, int end, boolean ignoreCase) {
        try {
            tryParseBoolean(input, start, end, ignoreCase);
            fail("Expected IndexOutOfBoundsException");
        } catch (@SuppressWarnings("unused") IndexOutOfBoundsException e) {
            // expected
        }
    }

    private void checkNull(boolean ignoreCase) {
        assertEquals(Optional.empty(), tryParseBoolean(null, ignoreCase));
    }

    private void checkNull(int start, int end, boolean ignoreCase) {
        assertEquals(Optional.empty(), tryParseBoolean(null, start, end, ignoreCase));
    }
}
