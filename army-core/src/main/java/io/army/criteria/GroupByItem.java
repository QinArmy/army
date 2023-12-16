package io.army.criteria;

/**
 * <p>
 * This interface representing group by item in GROUP BY clause.
 * This interface is base interface of {@link ExpressionItem}
 *
 * @since 0.6.0
 */
public interface GroupByItem extends Item {


    /**
     * This interface is base interface of :
     * <ul>
     *     <li>{@link Expression}</li>
     *     <li>{@link ExpressionGroup}</li>
     * </ul>
     */
    interface ExpressionItem extends GroupByItem {

    }

    /**
     * This interface representing : ( expression [, ...] )
     */
    interface ExpressionGroup extends ExpressionItem {

    }

}
