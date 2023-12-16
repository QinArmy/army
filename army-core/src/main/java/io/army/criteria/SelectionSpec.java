package io.army.criteria;



/**
 * <p>
 * This interface representing the ability that create {@link Selection}.
 * This interface is base interface of below:
 * <ul>
 *     <li>{@link Expression}</li>
 *     <li>{@link io.army.criteria.impl.Functions._TabularFunction}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface SelectionSpec extends TypeInfer {

    /**
     * @param selectionAlas non-null,non-empty.
     */
    Selection as(String selectionAlas);


}
