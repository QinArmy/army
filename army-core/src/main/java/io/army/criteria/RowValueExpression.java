package io.army.criteria;


/**
 * <p>
 * This interface representing row value expression.
 * This interface is only base interface of following : <ul>
 * <li>{@link RowLiteralExpression}</li>
 * <li>{@link RowParamExpression}</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
public interface RowValueExpression extends RowExpression, SqlValueParam.MultiValue {


}
