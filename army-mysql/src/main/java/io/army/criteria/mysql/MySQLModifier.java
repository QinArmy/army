package io.army.criteria.mysql;

import io.army.criteria.SQLWords;

public enum MySQLModifier implements SQLWords {

    /*################################## blow SELECT modifiers ##################################*/
    ALL,
    DISTINCT,
    DISTINCTROW,

    HIGH_PRIORITY,

    STRAIGHT_JOIN,

    SQL_SMALL_RESULT,
    SQL_BIG_RESULT,
    SQL_BUFFER_RESULT,

    SQL_NO_CACHE,
    SQL_CALC_FOUND_ROWS,

    /*################################## blow INSERT/UPDATE/DELETE modifiers ##################################*/
    LOW_PRIORITY,
    DELAYED,

    QUICK,
    IGNORE,

    CONCURRENT,
    LOCAL;


    @Override
    public final String render() {
        return this.name();
    }

    @Override
    public final String toString() {
        return String.format("%s.%s", MySQLModifier.class.getName(), this.name());
    }


}
