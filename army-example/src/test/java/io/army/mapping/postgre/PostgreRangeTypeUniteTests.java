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

package io.army.mapping.postgre;

import io.army.dialect.impl._Constant;
import io.army.util._TimeUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Function;

/**
 * <p>
 * This class is unit test class of {@link PostgreSingleRangeType}
*/
public class PostgreRangeTypeUniteTests {

    /**
     * @see PostgreRangeType#parseNonEmptyRange(String, int, int, RangeFunction, Function)
     */
    @Test
    public void textToRange() {
        String text;
        Int4Range range;

        text = "(1,3)";
        range = PostgreRangeType.parseNonEmptyRange(text, 0, text.length(), Int4Range::create, Integer::parseInt);
        Assert.assertEquals(range, Int4Range.create(false, 1, 3, false));

        text = "[-1,34)";
        range = PostgreRangeType.parseNonEmptyRange(text, 0, text.length(), Int4Range::create, Integer::parseInt);
        Assert.assertEquals(range, Int4Range.create(true, -1, 34, false));

        text = "[ -1 , 34 )";
        range = PostgreRangeType.parseNonEmptyRange(text, 0, text.length(), Int4Range::create, Integer::parseInt);
        Assert.assertEquals(range, Int4Range.create(true, -1, 34, false));

        text = "[  , 34 )";
        range = PostgreRangeType.parseNonEmptyRange(text, 0, text.length(), Int4Range::create, Integer::parseInt);
        Assert.assertEquals(range, Int4Range.create(true, null, 34, false));

        text = "[  -1,  )";
        range = PostgreRangeType.parseNonEmptyRange(text, 0, text.length(), Int4Range::create, Integer::parseInt);
        Assert.assertEquals(range, Int4Range.create(true, -1, null, false));

        text = "[  ,  )";
        range = PostgreRangeType.parseNonEmptyRange(text, 0, text.length(), Int4Range::create, Integer::parseInt);
        Assert.assertEquals(range, Int4Range.create(false, null, null, false));


        final LocalDateTime lowerBound, upperBound;
        lowerBound = LocalDateTime.parse("2023-05-25 15:29:51.118251", _TimeUtils.DATETIME_FORMATTER_6);
        upperBound = LocalDateTime.parse("2023-05-27 11:43:56.89738", _TimeUtils.DATETIME_FORMATTER_6);

        LocalDateTimeRange timeRange;
        text = "[\"2023-05-25 15:29:51.118251\",\"2023-05-27 11:43:56.89738\")";
        timeRange = PostgreRangeType.parseNonEmptyRange(text, 0, text.length(), LocalDateTimeRange::create, this::parseDateTime);
        Assert.assertEquals(timeRange, LocalDateTimeRange.create(true, lowerBound, upperBound, false));

        text = "(\"2023-05-25 15:29:51.118251\",\"2023-05-27 11:43:56.89738\"]";
        timeRange = PostgreRangeType.parseNonEmptyRange(text, 0, text.length(), LocalDateTimeRange::create, this::parseDateTime);
        Assert.assertEquals(timeRange, LocalDateTimeRange.create(false, lowerBound, upperBound, true));

        text = "[  \"2023-05-25 15:29:51.118251\"  ,  \"2023-05-27 11:43:56.89738\"   )";
        timeRange = PostgreRangeType.parseNonEmptyRange(text, 0, text.length(), LocalDateTimeRange::create, this::parseDateTime);
        Assert.assertEquals(timeRange, LocalDateTimeRange.create(true, lowerBound, upperBound, false));

        text = "[  infinity, \"2023-05-27 11:43:56.89738\" )";
        timeRange = PostgreRangeType.parseNonEmptyRange(text, 0, text.length(), LocalDateTimeRange::create, this::parseDateTime);
        Assert.assertEquals(timeRange, LocalDateTimeRange.create(false, null, upperBound, false));

        text = "[  \"2023-05-25 15:29:51.118251\", infinity )";
        timeRange = PostgreRangeType.parseNonEmptyRange(text, 0, text.length(), LocalDateTimeRange::create, this::parseDateTime);
        Assert.assertEquals(timeRange, LocalDateTimeRange.create(true, lowerBound, null, false));

        text = "[  infinity,  )";
        timeRange = PostgreRangeType.parseNonEmptyRange(text, 0, text.length(), LocalDateTimeRange::create, this::parseDateTime);
        Assert.assertEquals(timeRange, LocalDateTimeRange.create(false, null, null, false));

        text = "[ infinity , infinity )";
        timeRange = PostgreRangeType.parseNonEmptyRange(text, 0, text.length(), LocalDateTimeRange::create, this::parseDateTime);
        Assert.assertEquals(timeRange, LocalDateTimeRange.create(false, null, null, false));

        text = "[ infinity,infinity )";
        timeRange = PostgreRangeType.parseNonEmptyRange(text, 0, text.length(), LocalDateTimeRange::create, this::parseDateTime);
        Assert.assertEquals(timeRange, LocalDateTimeRange.create(false, null, null, false));
    }

    /**
     * @see PostgreRangeType#createMockFunction(Class, Class)
     */
    @Test
    public void mockFunction() {
        PostgreRangeType.createMockFunction(Int4Range.class, Integer.class);
        PostgreRangeType.createMockFunction(LocalDateTimeRange.class, LocalDateTime.class);
    }

    /**
     * @see PostgreRangeType#createRangeFunction(Class, Class, String)
     */
    @Test
    public void rangeFunction() {
        String funcRef;
        funcRef = Int4Range.class.getName() + _Constant.DOUBLE_COLON + "create";
        PostgreRangeType.createRangeFunction(Int4Range.class, Integer.class, funcRef);

        funcRef = LocalDateTimeRange.class.getName() + _Constant.DOUBLE_COLON + "create";
        PostgreRangeType.createRangeFunction(LocalDateTimeRange.class, LocalDateTime.class, funcRef);
    }

    /**
     * @see PostgreRangeType#emptyRange(Class)
     */
    @Test
    public void emptyRange() {
        Int4Range intEmpty;
        intEmpty = PostgreRangeType.emptyRange(Int4Range.class);
        Assert.assertEquals(intEmpty, Int4Range.EMPTY);

        LocalDateTimeRange dateTimeEmpty;
        dateTimeEmpty = PostgreRangeType.emptyRange(LocalDateTimeRange.class);
        Assert.assertEquals(dateTimeEmpty, LocalDateTimeRange.EMPTY);

    }


    @Nullable
    private LocalDateTime parseDateTime(final String text) {
        if (PostgreRangeType.INFINITY.equals(text)) {
            return null;
        }
        return LocalDateTime.parse(text, _TimeUtils.DATETIME_FORMATTER_6);
    }


    public static final class LocalDateTimeRange {

        public static LocalDateTimeRange create(boolean includeLower, @Nullable LocalDateTime lower,
                                                @Nullable LocalDateTime upper, boolean includeUpper) {
            return new LocalDateTimeRange(includeLower, lower, upper, includeUpper);
        }

        private static final LocalDateTimeRange EMPTY = new LocalDateTimeRange(false, null, null, false);

        public static LocalDateTimeRange emptyRange() {
            return EMPTY;
        }

        private final LocalDateTime lower;

        private final boolean includeLower;

        private final LocalDateTime upper;

        private final boolean includeUpper;

        private LocalDateTimeRange(boolean includeLower, @Nullable LocalDateTime lower, @Nullable LocalDateTime upper,
                                   boolean includeUpper) {
            this.includeLower = lower != null && includeLower;
            this.lower = lower;
            this.upper = upper;
            this.includeUpper = upper != null && includeUpper;
        }

        public boolean isEmpty() {
            return this == EMPTY;
        }


        public boolean isIncludeLowerBound() {
            if (this == EMPTY) {
                throw new IllegalStateException();
            }
            return this.includeLower;
        }


        public boolean isIncludeUpperBound() {
            if (this == EMPTY) {
                throw new IllegalStateException();
            }
            return this.includeUpper;
        }

        @Nullable
        public LocalDateTime getLowerBound() {
            if (this == EMPTY) {
                throw new IllegalStateException();
            }
            return this.lower;
        }

        @Nullable
        public LocalDateTime getUpperBound() {
            if (this == EMPTY) {
                throw new IllegalStateException();
            }
            return this.upper;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.lower, this.includeLower, this.upper, this.includeUpper);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof LocalDateTimeRange) {
                final LocalDateTimeRange o = (LocalDateTimeRange) obj;
                match = Objects.equals(o.lower, this.lower)
                        && o.includeLower == this.includeLower
                        && Objects.equals(o.upper, this.upper)
                        && o.includeUpper == this.includeUpper;
            } else {
                match = false;
            }
            return match;
        }


    }//LocalDateTimeRange


    public static final class Int4Range {

        public static Int4Range create(boolean includeLower, @Nullable Integer lower,
                                       @Nullable Integer upper, boolean includeUpper) {
            return new Int4Range(includeLower, lower, upper, includeUpper);
        }

        private static final Int4Range EMPTY = new Int4Range(false, null, null, false);

        public static Int4Range emptyRange() {
            return EMPTY;
        }

        private final Integer lower;

        private final boolean includeLower;

        private final Integer upper;

        private final boolean includeUpper;

        private Int4Range(boolean includeLower, @Nullable Integer lower, @Nullable Integer upper,
                          boolean includeUpper) {
            this.includeLower = lower != null && includeLower;
            this.lower = lower;
            this.upper = upper;
            this.includeUpper = upper != null && includeUpper;
        }

        public boolean isEmpty() {
            return this == EMPTY;
        }


        public boolean isIncludeLowerBound() {
            if (this == EMPTY) {
                throw new IllegalStateException();
            }
            return this.includeLower;
        }


        public boolean isIncludeUpperBound() {
            if (this == EMPTY) {
                throw new IllegalStateException();
            }
            return this.includeUpper;
        }

        @Nullable
        public Integer getLowerBound() {
            if (this == EMPTY) {
                throw new IllegalStateException();
            }
            return this.lower;
        }

        @Nullable
        public Integer getUpperBound() {
            if (this == EMPTY) {
                throw new IllegalStateException();
            }
            return this.upper;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.lower, this.includeLower, this.upper, this.includeUpper);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof Int4Range) {
                final Int4Range o = (Int4Range) obj;
                match = Objects.equals(o.lower, this.lower)
                        && o.includeLower == this.includeLower
                        && Objects.equals(o.upper, this.upper)
                        && o.includeUpper == this.includeUpper;
            } else {
                match = false;
            }
            return match;
        }


    }//Int4Range

}
