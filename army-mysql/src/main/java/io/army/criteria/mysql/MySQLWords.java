package io.army.criteria.mysql;

import io.army.criteria.SQLWords;

public enum MySQLWords implements SQLWords.Modifier {

    /*################################## blow SELECT modifiers ##################################*/
    ALL(" ALL", 0),
    DISTINCT(" DISTINCT", 0),
    DISTINCTROW(" DISTINCTROW", 0),

    HIGH_PRIORITY(" HIGH_PRIORITY", 2),

    STRAIGHT_JOIN(" STRAIGHT_JOIN", 3),

    SQL_SMALL_RESULT(" SQL_SMALL_RESULT", 4),
    SQL_BIG_RESULT(" SQL_BIG_RESULT", 5),
    SQL_BUFFER_RESULT(" SQL_BUFFER_RESULT", 6),

    SQL_NO_CACHE(" SQL_NO_CACHE", 7),
    SQL_CALC_FOUND_ROWS(" SQL_CALC_FOUND_ROWS", 8),

    /*################################## blow INSERT/UPDATE/DELETE modifiers ##################################*/
    LOW_PRIORITY(" LOW_PRIORITY", 0),
    DELAYED(" DELAYED", 1),

    QUICK(" QUICK", 1),
    IGNORE(" IGNORE", 3);


    public final String words;

    public final byte number;

    MySQLWords(String words, int number) {
        this.words = words;
        this.number = (byte) number;
    }

    @Override
    public final String render() {
        return this.words;
    }

    @Override
    public final int level() {
        return this.number;
    }

    @Override
    public final String toString() {
        return String.format("%s.%s", MySQLWords.class.getName(), this.name());
    }


}
