package io.army.criteria.mysql;

import io.army.criteria.SQLWords;

public enum MySQLModifier implements SQLWords {


    HIGH_PRIORITY(" HIGH PRIORITY"),

    STRAIGHT_JOIN(" STRAIGHT JOIN"),
    SQL_SMALL_RESULT(" SQL SMALL RESULT"),
    SQL_BIG_RESULT(" SQL BIG RESULT"),
    SQL_CACHE(" SQL CACHE"),

    SQL_NO_CACHE(" SQL NO CACHE"),
    SQL_CALC_FOUND_ROWS(" SQL CALC FOUND ROWS"),

    LOW_PRIORITY(" LOW_PRIORITY"),                                  // update statement
    IGNORE(" IGNORE");                                             // update statement


    private final String modifier;

    MySQLModifier(String modifier) {
        this.modifier = modifier;
    }

    @Override
    public final String render() {
        return this.modifier;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", MySQLModifier.class.getName(), this.name());
    }


}
