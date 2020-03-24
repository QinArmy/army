package io.army.criteria;


public enum MySQLModifier implements SQLModifier {
    ALL,
    DISTINCT,
    DISTINCTROW,
    LOW_PRIORITY,

    QUICK,
    IGNORE,
    HIGH_PRIORITY,
    SQL_SMALL_RESULT,

    SQL_BIG_RESULT,
    SQL_BUFFER_RESULT,
    SQL_CACHE,
    SQL_NO_CACHE,

    SQL_CALC_FOUND_ROWS;

    @Override
    public String render() {
        return name();
    }
}
