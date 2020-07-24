package io.army.criteria;

/**
 * @see Select
 * @see Insert
 * @see Update
 * @see Delete
 * @see SubQuery
 * @see PartQuery
 */
public interface SQLStatement {

    boolean prepared();

}
