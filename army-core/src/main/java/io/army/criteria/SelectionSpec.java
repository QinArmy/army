package io.army.criteria;


import io.army.criteria.dialect.VarExpression;

/**
 * <p>
 * This interface representing the ability that create {@link Selection}.
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link Expression}</li>
 *         <li>{@link VarExpression}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface SelectionSpec extends TypeInfer.TypeUpdateSpec, Item {

    /**
     * @param selectionAlas non-null,non-empty.
     */
    Selection as(String selectionAlas);


}
