package org.qinarmy.army.criteria.impl;

/**
 * created  on 2018/11/25.
 */
interface SqlOperator {


    SqlOperator negated();

    String rendered();

    Position position();


    enum Position {
        LEFT,
        CENTER,
        RIGHT,
        TOW,
    }

}
