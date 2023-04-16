package io.army.criteria.impl;

import io.army.dialect._Constant;

enum BooleanUnaryOperator implements Operator.UnaryOperator {


    EXISTS(_Constant.SPACE_EXISTS),
    NOT_EXISTS(" NOT EXISTS");

    final String spaceOperator;

    BooleanUnaryOperator(String spaceOperator) {
        this.spaceOperator = spaceOperator;
    }


    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }

}
