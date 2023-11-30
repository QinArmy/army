package io.army.mapping;

import io.army.sqltype.MySQLType;
import io.army.util._MappingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.BitSet;

/**
 * <p>
 * This class is test class of {@link  _MappingUtils}。
 * </p>
 */
public class MappingUtilsTests {

    private static final Logger LOG = LoggerFactory.getLogger(MappingUtilsTests.class);


    @Test
    public void bitwiseToString() {
        BitSet bitSet = BitSet.valueOf(new long[]{0xffff_ffffL, 2});
        String bitStr;
        bitStr = BitSetType.bitwiseToString(BitSetType.INSTANCE, MySQLType.BIT, bitSet, MappingType.PARAM_ERROR_HANDLER);
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
            paramValue = BitSetType.bitwiseToLong(BitSetType.INSTANCE, MySQLType.BIT, bitSet.toByteArray(), MappingType.PARAM_ERROR_HANDLER);
            Assert.assertEquals(paramValue, word);
        }

    }

}
