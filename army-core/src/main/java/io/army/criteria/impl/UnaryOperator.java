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
    INVERT(" ~"),
    AT(" @");

    final String spaceOperator;

    UnaryOperator(String spaceOperator) {
        this.spaceOperator = spaceOperator;
    }

    @Override
    public final String render() {
        return this.spaceOperator;
    }

    @Override
    public final String toString() {
        return CriteriaUtils.sqlWordsToString(this);
    }


}
