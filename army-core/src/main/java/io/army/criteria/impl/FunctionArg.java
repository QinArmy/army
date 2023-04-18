package io.army.criteria.impl;


/**
 * <p>
 * Package interface . This interface representing legal sql function argument.
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link SingleFunctionArg}</li>
 *         <li>{@link NonOperationExpression.MultiValueExpression}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
interface FunctionArg extends ArmyExpression {

    /**
     * <p>
     * This interface representing legal sql function argument.
     * This interface is base interface of below:
     *     <ul>
     *         <li>{@link OperationExpression}</li>
     *         <li>{@link SQLs#DEFAULT}</li>
     *         <li>{@link SQLs#_ASTERISK_EXP}</li>
     *     </ul>
     * </p>
     *
     * @since 1.0
     */
    interface SingleFunctionArg extends FunctionArg {

    }

}