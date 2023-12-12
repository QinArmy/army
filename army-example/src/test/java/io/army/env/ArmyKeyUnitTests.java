package io.army.env;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.List;

/**
 * <p>
 * This class is unit test class of {@link ArmyKey}
*/
public class ArmyKeyUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(ArmyKeyUnitTests.class);


    /**
     * @see ArmyKey#keyList()
     */
    @Test
    public void armyKeyList() {

        final List<ArmyKey<?>> list;
        list = ArmyKey.keyList();
        final int size = list.size();
        final StringBuilder builder = new StringBuilder(size * 30);
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append("\n");
            }
            builder.append(i + 1)
                    .append(" : ")
                    .append(list.get(i));
        }
        LOG.debug("{}", builder);
    }

}
