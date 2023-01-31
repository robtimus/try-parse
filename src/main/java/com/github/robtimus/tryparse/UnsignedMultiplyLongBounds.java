/*
 * UnsignedMultiplyLongBounds.java
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

final class UnsignedMultiplyLongBounds {

    private static final long[] BOUNDS = new long[Character.MAX_RADIX + 1];

    static {
        // calculated using https://www.wolframalpha.com/ using "(2^64-1) / x" and validated in unit tests
        BOUNDS[2] = 9223372036854775807L;
        BOUNDS[3] = 6148914691236517205L;
        BOUNDS[4] = 4611686018427387903L;
        BOUNDS[5] = 3689348814741910323L;
        BOUNDS[6] = 3074457345618258602L;
        BOUNDS[7] = 2635249153387078802L;
        BOUNDS[8] = 2305843009213693951L;
        BOUNDS[9] = 2049638230412172401L;
        BOUNDS[10] = 1844674407370955161L;
        BOUNDS[11] = 1676976733973595601L;
        BOUNDS[12] = 1537228672809129301L;
        BOUNDS[13] = 1418980313362273201L;
        BOUNDS[14] = 1317624576693539401L;
        BOUNDS[15] = 1229782938247303441L;
        BOUNDS[16] = 1152921504606846975L;
        BOUNDS[17] = 1085102592571150095L;
        BOUNDS[18] = 1024819115206086200L;
        BOUNDS[19] = 970881267037344821L;
        BOUNDS[20] = 922337203685477580L;
        BOUNDS[21] = 878416384462359600L;
        BOUNDS[22] = 838488366986797800L;
        BOUNDS[23] = 802032351030850070L;
        BOUNDS[24] = 768614336404564650L;
        BOUNDS[25] = 737869762948382064L;
        BOUNDS[26] = 709490156681136600L;
        BOUNDS[27] = 683212743470724133L;
        BOUNDS[28] = 658812288346769700L;
        BOUNDS[29] = 636094623231363848L;
        BOUNDS[30] = 614891469123651720L;
        BOUNDS[31] = 595056260442243600L;
        BOUNDS[32] = 576460752303423487L;
        BOUNDS[33] = 558992244657865200L;
        BOUNDS[34] = 542551296285575047L;
        BOUNDS[35] = 527049830677415760L;
        BOUNDS[36] = 512409557603043100L;
    }

    private UnsignedMultiplyLongBounds() {
    }

    static long get(int radix) {
        return BOUNDS[radix];
    }
}
