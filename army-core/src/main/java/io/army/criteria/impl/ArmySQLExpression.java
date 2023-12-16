package io.army.criteria.impl;

import io.army.criteria.SQLExpression;
import io.army.criteria.impl.inner._SelfDescribed;

/**
 * <p>
 * Package interface and must be package interface. This interface is base interface of :
 * <ul>
 *     <li>{@link ArmyExpression}</li>
 *     <li>{@link ArmyRowExpression}</li>
 * </ul>
 * * @since 0.6.0
 */
interface ArmySQLExpression extends SQLExpression, _SelfDescribed, ArmyRightOperand {


}
