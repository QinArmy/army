/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.mapping;

import io.army.sqltype.MySQLType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.BitSet;


public class MappingUtilsTests {


    private static final Logger LOG = LoggerFactory.getLogger(MappingUtilsTests.class);


    @Test
    public void bitwiseToString() {
        BitSet bitSet = BitSet.valueOf(new long[]{0xffff_ffffL, 2});
        String bitStr;
        bitStr = BitSetType.bitwiseToString(BitSetType.INSTANCE, MySQLType.BIT, bitSet, AbstractMappingType.PARAM_ERROR_HANDLER);
        Assert.assertEquals(bitStr, "100000000000000000000000000000000011111111111111111111111111111111");
    }

    @Test
    public void bitwiseToLong() {
        final long[] words;
        words = new long[]{0xffff_ffffL, -1L, 0, 45346534};

        BitSet bitSet;
        long paramValue;
        for (long word : words) {
            bitSet = BitSet.valueOf(new long[]{word});
            paramValue = BitSetType.bitwiseToLong(BitSetType.INSTANCE, MySQLType.BIT, bitSet.toByteArray(), AbstractMappingType.PARAM_ERROR_HANDLER);
            Assert.assertEquals(paramValue, word);
        }

    }

}
