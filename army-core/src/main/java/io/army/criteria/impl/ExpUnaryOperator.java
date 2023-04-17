package io.army.criteria.impl;


/**
 * representing Unary SQL Operator
 *
 * @see BooleanUnaryOperator
 */
enum ExpUnaryOperator implements Operator.SqlUnaryOperator {

    NEGATE(" -"),
    POSITIVE(" +"),
    BITWISE_NOT(" ~"),

    // postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">|/ double precision → double precision<br/>
     * Square root
     * </a>
     */
    VERTICAL_SLASH(" |/"), // postgre only

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">||/ double precision → double precision<br/>
     * Cube root
     * </a>
     */
    DOUBLE_VERTICAL_SLASH(" ||/"),
    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">Absolute value operator</a>
     */
    AT(" @");// postgre only


    final String spaceOperator;

    ExpUnaryOperator(String spaceOperator) {
        this.spaceOperator = spaceOperator;
    }


    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }


}
