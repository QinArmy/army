package io.army.criteria;

/**
 * @param <E> {@link ScalarSubQuery}'s query result Java Type.
 */
public interface ScalarSubQuery<E> extends ColumnSubQuery<E>, RowSubQuery, Expression<E> {


    interface ScalarSelectionSpec<E, C> extends QuerySQLSpec {

        FromSpec<ScalarSubQuery<E>, C> select(Distinct distinct, Selection selection);

        FromSpec<ScalarSubQuery<E>, C> select(Selection selection);

    }

}
