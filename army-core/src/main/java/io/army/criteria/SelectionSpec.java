package io.army.criteria;


import io.army.criteria.impl.Functions;

/**
 * <p>
 * This interface representing the ability that create {@link Selection}.
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link Expression}</li>
 *         <li>{@link Functions.DerivedTableFunction}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface SelectionSpec extends TypeInfer {

    /**
     * @param selectionAlas non-null,non-empty.
     */
    Selection as(String selectionAlas);


}
