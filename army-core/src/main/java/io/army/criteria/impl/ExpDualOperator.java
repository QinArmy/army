package io.army.criteria.impl;

enum ExpDualOperator implements Operator.DualOperator {

    PLUS(" +"),
    MINUS(" -"),
    MOD(" %"),
    TIMES(" *"),
    DIVIDE(" /"),

    BITWISE_OR(" |"),
    BITWISE_AND(" &"),
    BITWISE_XOR(" ^"),
    LEFT_SHIFT(" <<"),
    RIGHT_SHIFT(" >>"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">numeric ^ numeric → numeric <br/>
     * double precision ^ double precision → double precision <br/>
     * Exponentiation</a>
     */
    CARET(" ^"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">bytea || bytea → bytea</a>
     */
    DOUBLE_VERTICAL(" ||"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-ZONECONVERT-TABLE"> AT TIME ZONE Variants</a>
     */
    AT_TIME_ZONE(" AT TIME ZONE");


    final String spaceOperator;


    ExpDualOperator(String spaceOperator) {
        this.spaceOperator = spaceOperator;
    }


    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }


}


