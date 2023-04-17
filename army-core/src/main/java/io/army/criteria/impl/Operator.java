package io.army.criteria.impl;

/**
 * Package interface
 */
interface Operator {

    String name();

    @Override
    String toString();

    interface SqlUnaryOperator extends Operator {

    }


    interface DualOperator extends Operator {

    }


}
