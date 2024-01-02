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
