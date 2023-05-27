package io.army.mapping.postgre;

import io.army.lang.Nullable;
import io.army.util._TimeUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Function;

/**
 * <p>
 * This class is unit test class of {@link PostgreRangeType}
 * </p>
 */
public class PostgreRangeTypeUniteTests {

    /**
     * @see PostgreRangeType#textToNonEmptyRange(String, int, int, RangeFunction, Function)
     */
    @Test
    public void textToRange() {
        String text;
        Int4Range range;

        text = "(1,3)";
        range = PostgreRangeType.textToNonEmptyRange(text, 0, text.length(), Int4Range::create, PostgreInt4RangeType::parseInt);
        Assert.assertEquals(range, Int4Range.create(1, false, 3, false));

        text = "[-1,34)";
        range = PostgreRangeType.textToNonEmptyRange(text, 0, text.length(), Int4Range::create, PostgreInt4RangeType::parseInt);
        Assert.assertEquals(range, Int4Range.create(-1, true, 34, false));

        text = "[ -1 , 34 )";
        range = PostgreRangeType.textToNonEmptyRange(text, 0, text.length(), Int4Range::create, PostgreInt4RangeType::parseInt);
        Assert.assertEquals(range, Int4Range.create(-1, true, 34, false));

        text = "[  , 34 )";
        range = PostgreRangeType.textToNonEmptyRange(text, 0, text.length(), Int4Range::create, PostgreInt4RangeType::parseInt);
        Assert.assertEquals(range, Int4Range.create(null, true, 34, false));

        text = "[  -1,  )";
        range = PostgreRangeType.textToNonEmptyRange(text, 0, text.length(), Int4Range::create, PostgreInt4RangeType::parseInt);
        Assert.assertEquals(range, Int4Range.create(-1, true, null, false));

        text = "[  ,  )";
        range = PostgreRangeType.textToNonEmptyRange(text, 0, text.length(), Int4Range::create, PostgreInt4RangeType::parseInt);
        Assert.assertEquals(range, Int4Range.create(null, false, null, false));


        final LocalDateTime lowerBound, upperBound;
        lowerBound = LocalDateTime.parse("2023-05-25 15:29:51.118251", _TimeUtils.DATETIME_FORMATTER_6);
        upperBound = LocalDateTime.parse("2023-05-27 11:43:56.89738", _TimeUtils.DATETIME_FORMATTER_6);

        LocalDateTimeRange timeRange;
        text = "[\"2023-05-25 15:29:51.118251\",\"2023-05-27 11:43:56.89738\")";
        timeRange = PostgreRangeType.textToNonEmptyRange(text, 0, text.length(), LocalDateTimeRange::create, PostgreTsRangeType::parseDateTime);
        Assert.assertEquals(timeRange, LocalDateTimeRange.create(lowerBound, true, upperBound, false));

        text = "(\"2023-05-25 15:29:51.118251\",\"2023-05-27 11:43:56.89738\"]";
        timeRange = PostgreRangeType.textToNonEmptyRange(text, 0, text.length(), LocalDateTimeRange::create, PostgreTsRangeType::parseDateTime);
        Assert.assertEquals(timeRange, LocalDateTimeRange.create(lowerBound, false, upperBound, true));

        text = "[  \"2023-05-25 15:29:51.118251\"  ,  \"2023-05-27 11:43:56.89738\"   )";
        timeRange = PostgreRangeType.textToNonEmptyRange(text, 0, text.length(), LocalDateTimeRange::create, PostgreTsRangeType::parseDateTime);
        Assert.assertEquals(timeRange, LocalDateTimeRange.create(lowerBound, true, upperBound, false));

        text = "[  infinity, \"2023-05-27 11:43:56.89738\" )";
        timeRange = PostgreRangeType.textToNonEmptyRange(text, 0, text.length(), LocalDateTimeRange::create, PostgreTsRangeType::parseDateTime);
        Assert.assertEquals(timeRange, LocalDateTimeRange.create(null, false, upperBound, false));

        text = "[  \"2023-05-25 15:29:51.118251\", infinity )";
        timeRange = PostgreRangeType.textToNonEmptyRange(text, 0, text.length(), LocalDateTimeRange::create, PostgreTsRangeType::parseDateTime);
        Assert.assertEquals(timeRange, LocalDateTimeRange.create(lowerBound, true, null, false));

        text = "[  infinity,  )";
        timeRange = PostgreRangeType.textToNonEmptyRange(text, 0, text.length(), LocalDateTimeRange::create, PostgreTsRangeType::parseDateTime);
        Assert.assertEquals(timeRange, LocalDateTimeRange.create(null, false, null, false));

        text = "[ infinity , infinity )";
        timeRange = PostgreRangeType.textToNonEmptyRange(text, 0, text.length(), LocalDateTimeRange::create, PostgreTsRangeType::parseDateTime);
        Assert.assertEquals(timeRange, LocalDateTimeRange.create(null, false, null, false));

        text = "[ infinity,infinity )";
        timeRange = PostgreRangeType.textToNonEmptyRange(text, 0, text.length(), LocalDateTimeRange::create, PostgreTsRangeType::parseDateTime);
        Assert.assertEquals(timeRange, LocalDateTimeRange.create(null, false, null, false));
    }

    @Test
    public void test() {
        System.out.println(LocalDateTime.parse("2023-05-25 15:29:51.118251", _TimeUtils.DATETIME_FORMATTER_6));
    }


    public static final class LocalDateTimeRange {

        private static LocalDateTimeRange create(@Nullable LocalDateTime lower, boolean includeLower,
                                                 @Nullable LocalDateTime upper, boolean includeUpper) {
            return new LocalDateTimeRange(lower, includeLower, upper, includeUpper);
        }

        private static final LocalDateTimeRange EMPTY = new LocalDateTimeRange(null, false, null, false);

        public static LocalDateTimeRange emptyRange() {
            return EMPTY;
        }

        private final LocalDateTime lower;

        private final boolean includeLower;

        private final LocalDateTime upper;

        private final boolean includeUpper;

        private LocalDateTimeRange(@Nullable LocalDateTime lower, boolean includeLower, @Nullable LocalDateTime upper,
                                   boolean includeUpper) {
            this.lower = lower;
            this.includeLower = lower != null && includeLower;
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

        private static Int4Range create(@Nullable Integer lower, boolean includeLower,
                                        @Nullable Integer upper, boolean includeUpper) {
            return new Int4Range(lower, includeLower, upper, includeUpper);
        }

        private static final Int4Range EMPTY = new Int4Range(null, false, null, false);

        public static Int4Range emptyRange() {
            return EMPTY;
        }

        private final Integer lower;

        private final boolean includeLower;

        private final Integer upper;

        private final boolean includeUpper;

        private Int4Range(@Nullable Integer lower, boolean includeLower, @Nullable Integer upper,
                          boolean includeUpper) {
            this.lower = lower;
            this.includeLower = lower != null && includeLower;
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
