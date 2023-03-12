package io.army.criteria;


public interface ValuesConstructor<T> extends InsertStatement._StaticAssignmentSetClause<T, ValuesConstructor<T>> {

    /**
     * <p>
     * Start one new row.
     * </p>
     *
     * @return this
     */
    ValuesConstructor<T> row();

}
