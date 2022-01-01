package io.army.criteria.impl;

import io.army.criteria.Statement;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner._MultiUpdate;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract class MultiUpdate<C, JT, JS, WR, WA, SR> extends UpdateStatement<C, WR, WA, SR>
        implements Statement.JoinClause<C, JT, JS>, _MultiUpdate {

    MultiUpdate(@Nullable C criteria) {
        super(criteria);
    }

    @Override
    public final JT leftJoin(TableMeta<?> table, String tableAlias) {
        return this.createTableBlock(table, tableAlias, JoinType.LEFT_JOIN);
    }


    abstract JT createTableBlock(TableMeta<?> table, String tableAlias, JoinType joinType);

    abstract JT ifCreateTableBlock(Predicate<C> predicate, TableMeta<?> table, String tableAlias, JoinType joinType);

    abstract JS createTableBlock(Supplier<SubQuery> supplier, String tableAlias, JoinType joinType);

    abstract JS createTableBlock(Function<C, SubQuery> function, String tableAlias, JoinType joinType);

    abstract JS ifCreateSubQueryBlock(Supplier<SubQuery> supplier, String tableAlias, JoinType joinType);

    abstract JS ifCreateSubQueryBlock(Function<C, SubQuery> function, String tableAlias, JoinType joinType);


}
