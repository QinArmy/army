package io.army.criteria.mysql;

import io.army.criteria.SQLWords;

public enum MySQLWords implements SQLWords {

    /*################################## blow SELECT modifiers ##################################*/
    ALL(" ALL"),
    DISTINCT(" DISTINCT"),
    DISTINCTROW(" DISTINCTROW"),

    HIGH_PRIORITY(" HIGH_PRIORITY"),

    STRAIGHT_JOIN(" STRAIGHT_JOIN"),

    SQL_SMALL_RESULT(" SQL_SMALL_RESULT"),
    SQL_BIG_RESULT(" SQL_BIG_RESULT"),
    SQL_BUFFER_RESULT(" SQL_BUFFER_RESULT"),

    SQL_NO_CACHE(" SQL_NO_CACHE"),
    SQL_CALC_FOUND_ROWS(" SQL_CALC_FOUND_ROWS"),

    /*################################## blow INSERT/UPDATE/DELETE modifiers ##################################*/
    LOW_PRIORITY(" LOW_PRIORITY"),
    DELAYED(" DELAYED"),

    QUICK(" QUICK"),
    IGNORE(" IGNORE"),

    CONCURRENT(" CONCURRENT"),
    LOCAL(" LOCAL");


    public final String words;

    MySQLWords(String words) {
        this.words = words;
    }

    @Override
    public final String render() {
        return this.words;
    }

    @Override
    public final String toString() {
        return String.format("%s.%s", MySQLWords.class.getName(), this.name());
    }


}
