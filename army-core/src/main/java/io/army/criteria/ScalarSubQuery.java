package io.army.criteria;

/**
 * @param <E> {@link ScalarSubQuery}'s query result Java Type.
 */
public interface ScalarSubQuery<E> extends ColumnSubQuery<E>, RowSubQuery, TypeInfer {

    Selection selection();


    interface ScalarSelectionSpec<E, C> {

        FromSpec<ScalarSubQuery<E>, C> select(Distinct distinct, Selection selection);

        FromSpec<ScalarSubQuery<E>, C> select(Selection selection);

    }

}
