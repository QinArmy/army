package io.army.criteria.impl;


import io.army.dialect.Database;

/**
 * representing Unary SQL Operator
 */
enum UnaryExpOperator implements Operator.SqlUnaryExpOperator {

    NEGATE(" -"),
    BITWISE_NOT(" ~");


    final String spaceOperator;

    UnaryExpOperator(String spaceOperator) {
        this.spaceOperator = spaceOperator;
    }


    @Override
    public final String spaceRender() {
        return this.spaceOperator;
    }

    @Override
    public final Database database() {
        // no bug,never here
        throw new UnsupportedOperationException();
    }

    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }


}
