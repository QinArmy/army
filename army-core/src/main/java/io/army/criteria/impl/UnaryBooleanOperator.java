package io.army.criteria.impl;

import io.army.dialect.Database;
import io.army.dialect._Constant;

@Deprecated
enum UnaryBooleanOperator implements Operator.SqlUnaryBooleanOperator {


    EXISTS(_Constant.SPACE_EXISTS),
    NOT_EXISTS(" NOT EXISTS");

    final String spaceOperator;

    UnaryBooleanOperator(String spaceOperator) {
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
