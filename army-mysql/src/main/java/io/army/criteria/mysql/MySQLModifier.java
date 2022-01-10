package io.army.criteria.mysql;

import io.army.criteria.SQLModifier;

public enum MySQLModifier implements SQLModifier {


    HIGH_PRIORITY,

    STRAIGHT_JOIN,
    SQL_SMALL_RESULT,
    SQL_BIG_RESULT,
    SQL_CACHE,

    SQL_NO_CACHE,
    SQL_CALC_FOUND_ROWS;


    @Override
    public final String render() {
        return this.name();
    }


}
