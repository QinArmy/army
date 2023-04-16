package io.army.criteria.impl;

/**
 * Package interface
 */
interface Operator {

    String name();

    @Override
    String toString();

    int precedence();


    interface DualOperator extends Operator {

    }

}
