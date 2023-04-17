package io.army.criteria.impl;

enum ExpDualOperator implements Operator.DualOperator {

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">numeric ^ numeric → numeric <br/>
     * double precision ^ double precision → double precision <br/>
     * Exponentiation</a>
     */
    CARET(" ^", 90),

    BITWISE_XOR(" ^", 85),  // for MySQL , BITWISE_XOR > TIMES

    MOD(" %", 80),
    TIMES(" *", 80),
    DIVIDE(" /", 80),

    PLUS(" +", 70),
    MINUS(" -", 70),

    LEFT_SHIFT(" <<", 60),
    RIGHT_SHIFT(" >>", 60),

    BITWISE_AND(" &", 40),
    BITWISE_OR(" |", 30),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">bytea || bytea → bytea</a>
     */
    DOUBLE_VERTICAL(" ||", 20),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-ZONECONVERT-TABLE"> AT TIME ZONE Variants</a>
     */
    AT_TIME_ZONE(" AT TIME ZONE", 20);


    final String spaceOperator;

    final byte precedence;

    ExpDualOperator(String spaceOperator, int precedence) {
        assert precedence <= Byte.MAX_VALUE;
        this.spaceOperator = spaceOperator;
        this.precedence = (byte) precedence;
    }

    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }


}


