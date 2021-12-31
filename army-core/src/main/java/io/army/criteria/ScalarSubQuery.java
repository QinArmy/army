package io.army.criteria;

/**
 * @param <E> {@link ScalarSubQuery}'s query result Java Type.
 */
public interface ScalarSubQuery<E> extends ColumnSubQuery<E>, RowSubQuery, TypeInfer {

    default Selection selection() {
        throw new UnsupportedOperationException();
    }


    interface StandardScalarSubQuerySpec<C, E>
            extends StandardQuery.StandardSelectClauseSpec<C, ScalarQueryExpression<E>> {

    }

}
