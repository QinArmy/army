package io.army.criteria.impl;


import io.army.criteria.RowExpression;

/**
 * <p>
 * Package interface and must be package interface.
 * </p>
 *
 * @since 1.0
 */
interface ArmyRowExpression extends ArmySQLExpression, RowExpression, FunctionArg.SingleFunctionArg {

    int columnSize();


}
