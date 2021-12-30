package io.army.criteria;

import java.util.function.Function;

public interface ColumnSubQuery<E> extends SubQuery {


    interface StandardColumnSubQuerySpec<C, E>
            extends ColumnSubQuery<ColumnSelectClauseSpec<C, E, ColumnSubQuery<E>>> {

    }

    interface ColumnSelectClauseSpec<C, E, Q extends ColumnSubQuery<E>> {

        StandardQuery.StandardFromSpec<C, Q> selectOne(Distinct distinct, Selection selection);

        StandardQuery.StandardFromSpec<C, Q> selectOne(Selection selection);

        StandardQuery.StandardFromSpec<C, Q> selectOne(Distinct distinct, Function<C, Selection> function);


    }

}
