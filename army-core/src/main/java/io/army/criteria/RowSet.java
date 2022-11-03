package io.army.criteria;

/**
 * <p>
 * This interface The statement that can present in UNION clause,this interface is base interface of below:
 *     <ul>
 *         <li>{@link Query}</li>
 *         <li>{@link Values}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface RowSet extends Statement {

    interface _RowSetSpec<R extends Item> {

        R asQuery();

    }

    /**
     * <p>
     * This interface is base interface of below:
     *     <ul>
     *         <li>{@link  Values}</li>
     *         <li>{@link  SubValues}</li>
     *     </ul>
     * </p>
     */
    interface DqlValues extends RowSet {

    }

}
