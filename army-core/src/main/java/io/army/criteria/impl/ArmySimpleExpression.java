package io.army.criteria.impl;

/**
 * <p>
 * Package interface. This interface is base interface of below:
 *     <ul>
 *         <li>{@link OperationExpression.OperationSimpleExpression}</li>
 *         <li>{@link OperationExpression.BracketsExpression}</li>
 *         <li>{@link ArmyParamExpression}</li>
 *         <li>{@link ArmyLiteralExpression}</li>
 *         <li>{@link SQLs#NULL}</li>
 *         <li>{@link SQLs#TRUE}</li>
 *         <li>{@link SQLs#FALSE}</li>
 *         <li>{@link  OperationPredicate.OperationSimplePredicate}</li>
 *     </ul>
 ** @since 0.6.0
 */
interface ArmySimpleExpression extends ArmyExpression, ArmySimpleSQLExpression {


}
