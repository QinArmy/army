package io.army.criteria.mysql;

import io.army.criteria.SQLWords;

public enum MySQLTimeUnit implements SQLWords {

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

    private final String spaceWords;

    MySQLTimeUnit(String spaceWords) {
        this.spaceWords = spaceWords;
    }

    @Override
    public final String spaceRender() {
        return this.spaceWords;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", MySQLTimeUnit.class.getName(), this.name());
    }

    public final boolean isTimePart() {
        final boolean match;
        switch (this) {
            case HOUR:
            case MINUTE:
            case SECOND:
            case DAY_HOUR:
            case DAY_MINUTE:
            case DAY_SECOND:
            case DAY_MICROSECOND:
            case HOUR_MINUTE:
            case HOUR_SECOND:
            case HOUR_MICROSECOND:
            case MINUTE_SECOND:
            case MINUTE_MICROSECOND:
            case SECOND_MICROSECOND:
            case MICROSECOND:
                match = true;
                break;
            default:
                match = false;

        }

        return match;
    }


}
