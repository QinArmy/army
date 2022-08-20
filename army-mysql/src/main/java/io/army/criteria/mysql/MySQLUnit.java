package io.army.criteria.mysql;

import io.army.criteria.SQLWords;

public enum MySQLUnit implements SQLWords {

    MICROSECOND(" MICROSECOND"),
    SECOND(" SECOND"),
    MINUTE(" MINUTE"),
    HOUR(" HOUR"),

    DAY(" DAY"),
    WEEK(" WEEK"),
    MONTH(" MONTH"),
    QUARTER(" QUARTER"),

    YEAR(" YEAR"),
    SECOND_MICROSECOND(" SECOND_MICROSECOND"),
    MINUTE_MICROSECOND(" MINUTE_MICROSECOND"),
    MINUTE_SECOND(" MINUTE_SECOND"),

    HOUR_MICROSECOND(" HOUR_MICROSECOND"),
    HOUR_SECOND(" HOUR_SECOND"),
    HOUR_MINUTE(" HOUR_MINUTE"),
    DAY_MICROSECOND(" DAY_MICROSECOND"),

    DAY_SECOND(" DAY_SECOND"),
    DAY_MINUTE(" DAY_MINUTE"),
    DAY_HOUR(" DAY_HOUR"),
    YEAR_MONTH(" YEAR_MONTH");

    private final String words;

    MySQLUnit(String words) {
        this.words = words;
    }

    @Override
    public final String render() {
        return this.words;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", MySQLUnit.class.getName(), this.name());
    }


}
