/*
 * UnsignedMultiplyLongBoundsTest.java
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class UnsignedMultiplyLongBoundsTest {

    private static final BigInteger MAX_UNSIGNED_LONG_VALUE = BigInteger.valueOf(2).pow(64).subtract(BigInteger.ONE);

    @Test
    public void testGet() {
        for (int radix = Character.MIN_RADIX; radix <= Character.MAX_RADIX; radix++) {
            long expected = MAX_UNSIGNED_LONG_VALUE.divide(BigInteger.valueOf(radix)).longValueExact();
            assertEquals(expected, UnsignedMultiplyLongBounds.get(radix));
        }
    }

    @Test
    public void testGetRadixToHigh() {
        assertThrows(IndexOutOfBoundsException.class, () -> UnsignedMultiplyLongBounds.get(Character.MAX_RADIX + 1));
    }
}
