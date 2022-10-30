package io.army.criteria;


public interface ValuesConstructor<T> extends Insert._StaticAssignmentSetClause<T, ValuesConstructor<T>> {

    /**
     * <p>
     * Start one new row.
     * </p>
     *
     * @return this
     */
    ValuesConstructor<T> row();

}
