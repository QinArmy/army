package io.army.criteria;

import java.util.function.Function;

public interface ColumnSubQuery<E> extends SubQuery {

    interface ColumnSelectionSpec<E, Q extends ColumnSubQuery<E>, C> {

        FromSpec<Q, C> selectOne(Distinct distinct, Selection selection);

        FromSpec<Q, C> selectOne(Selection selection);

        FromSpec<Q, C> selectOne(Distinct distinct, Function<C, Selection> function);


    }

}
