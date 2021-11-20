package io.army.criteria;


/**
 * @see Expression
 * @see SortSelection
 * @see Selection
 */
public interface SortPart {

    void appendSortPart(SqlContext context);

}
