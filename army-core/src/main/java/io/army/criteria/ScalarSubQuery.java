package io.army.criteria;

/**
 * @param <E> {@link ScalarSubQuery}'s query result Java Type.
 */
public interface ScalarSubQuery<E> extends ColumnSubQuery<E>, RowSubQuery, TypeInfer {

    Selection selection();


    interface StandardScalarSubQuerySpec<C, E>
            extends ColumnSubQuery.ColumnSelectClauseSpec<C, E, ScalarQueryExpression<E>> {

    }

}
