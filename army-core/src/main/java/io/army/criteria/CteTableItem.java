package io.army.criteria;

/**
 * <p>
 * This interface representing cte table item that present in join expression
 * </p>
 *
 * @since 1.0
 */
public interface CteTableItem extends TableItem {

    String cteName();
}
