package io.army.criteria;

/**
 * @see Select
 * @see Insert
 * @see Update
 * @see Delete
 * @see SubQuery
 */
public interface Statement {

    /**
     * assert statement prepared
     */
    void prepared();

    @Deprecated
    interface SQLAble {

    }
}
