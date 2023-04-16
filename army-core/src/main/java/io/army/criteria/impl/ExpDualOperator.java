package io.army.criteria.impl;

enum ExpDualOperator implements Operator.DualOperator {

    PLUS(" +", 31),
    MINUS(" -", 31),
    MOD(" %", 32),
    TIMES(" *", 32),
    DIVIDE(" /", 32),

    BITWISE_OR(" |", 36),
    BITWISE_AND(" &", 37),
    BITWISE_XOR(" ^", 38),
    LEFT_SHIFT(" <<", 1),
    RIGHT_SHIFT(" >>", 1),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">numeric ^ numeric → numeric <br/>
     * double precision ^ double precision → double precision <br/>
     * Exponentiation</a>
     */
    CARET(" ^", 1),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">bytea || bytea → bytea</a>
     */
    DOUBLE_VERTICAL(" ||", 1),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-ZONECONVERT-TABLE"> AT TIME ZONE Variants</a>
     */
    AT_TIME_ZONE(" AT TIME ZONE", 1);


    final String spaceOperator;

    final byte precedence;

    ExpDualOperator(String spaceOperator, int precedence) {
        assert precedence <= Byte.MAX_VALUE;
        this.spaceOperator = spaceOperator;
        this.precedence = (byte) precedence;
    }


    @Override
    public final int precedence() {
        return this.precedence;
    }


    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }


}


