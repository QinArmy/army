package io.army.criteria;


/**
 * <p>
 * This interface is designed for dialect operator. This interface representing simple operator result. For example:
 * expr COLLATE collation .
 * This interface is base interface of :
 * <ul>
 *     <li>{@link CompoundExpression}</li>
 *     <li>{@link SimpleResultExpression}</li>
 * </ul>
*
 * @since 1.0
 */
public interface SimpleResultExpression extends SimpleExpression, ResultExpression {


}
