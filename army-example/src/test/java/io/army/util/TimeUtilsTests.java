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

package io.army.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;

/**
 * <p>
 * This class is test class of {@link _TimeUtils}
*/
public class TimeUtilsTests {

    private static final Logger LOG = LoggerFactory.getLogger(TimeUtilsTests.class);


    /**
     * @see _TimeUtils#truncatedIfNeed(int, Temporal)
     */
    @Test
    public void truncatedIfNeed() {
        final LocalDateTime dateTime = LocalDateTime.parse("2023-06-29T01:42:03.999999");
        LocalDateTime truncated;
        final StringBuilder builder = new StringBuilder();
        builder.append("now     : ")
                .append(dateTime.format(_TimeUtils.DATETIME_FORMATTER_6));
        String text;
        for (int i = 0; i < 7; i++) {
            truncated = _TimeUtils.truncatedIfNeed(i, dateTime);
            text = truncated.format(_TimeUtils.DATETIME_FORMATTER_6);
            builder.append("\nscale ")
                    .append(i)
                    .append(" : ")
                    .append(text);


            switch (i) {
                case 0:
                    Assert.assertEquals(text, "2023-06-29 01:42:03");
                    builder.append("       ");
                    break;
                case 1:
                    Assert.assertEquals(text, "2023-06-29 01:42:03.9");
                    builder.append("     ");
                    break;
                case 2:
                    Assert.assertEquals(text, "2023-06-29 01:42:03.99");
                    builder.append("    ");
                    break;
                case 3:
                    Assert.assertEquals(text, "2023-06-29 01:42:03.999");
                    builder.append("   ");
                    break;
                case 4:
                    Assert.assertEquals(text, "2023-06-29 01:42:03.9999");
                    builder.append("  ");
                    break;
                case 5:
                    Assert.assertEquals(text, "2023-06-29 01:42:03.99999");
                    builder.append(" ");
                    break;
                case 6:
                    Assert.assertEquals(text, "2023-06-29 01:42:03.999999");
                    break;
                default://no-op
            }
            builder.append(" ; truncated : ")
                    .append(truncated);

        }

        LOG.debug(builder.toString());

    }

}
