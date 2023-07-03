package io.army.criteria;


/**
 * <p>
 * This interface representing value expression.
 * This interface is only base interface of following : <ul>
 * <li>{@link LiteralExpression}</li>
 * <li>{@link ParamExpression}</li>
 * </ul>
 * </p>
 *
 * @see RowValueExpression
 * @since 1.0
 */
public interface ValueExpression extends SimpleExpression, SqlValueParam.SingleValue {


}
