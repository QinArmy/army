package io.army.criteria.impl;


import io.army.criteria.SQLWords;
import io.army.dialect._Constant;

/**
 * representing Unary SQL Operator
 */
enum UnaryOperator implements SQLWords {

    EXISTS(_Constant.SPACE_EXISTS),
    NOT_EXISTS(" NOT EXISTS"),
    NEGATE(" -"),
    POSITIVE(" +"),
    BITWISE_NOT(" ~"),

    /**
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">Absolute value operator</a>
     */
    AT(" @"),   // postgre only

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
    DOUBLE_VERTICAL_SLASH(" ||/");// postgre only


    final String spaceOperator;

    UnaryOperator(String spaceOperator) {
        this.spaceOperator = spaceOperator;
    }

    @Override
    public final String spaceRender() {
        return this.spaceOperator;
    }

    @Override
    public final String toString() {
        return CriteriaUtils.sqlWordsToString(this);
    }


}
