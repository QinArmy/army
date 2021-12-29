package io.army.criteria.impl;

import io.army.criteria.Statement;
import io.army.criteria.SubQuery;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

abstract class MultiQueryDmlStatement<C, JT, JS, WR, WA> extends QueryDmlStatement<C, WR, WA>
        implements Statement.JoinClause<C, JT, JS> {

    MultiQueryDmlStatement(@Nullable C criteria) {
        super(criteria);
    }

    @Override
    public final JT leftJoin(TableMeta<?> table, String tableAlias) {
        return this.createTableBlock(table, tableAlias, JoinType.LEFT_JOIN);
    }

    @Override
    public final JS leftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        return this.createTableBlock(function, subQueryAlia, JoinType.LEFT_JOIN);
    }

    @Override
    public final JS leftJoin(Supplier<SubQuery> supplier, String subQueryAlia) {
        return this.createTableBlock(supplier, subQueryAlia, JoinType.LEFT_JOIN);
    }

    @Override
    public final JT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifCreateTableBlock(predicate, table, tableAlias, JoinType.LEFT_JOIN);
    }

    @Override
    public final JS ifLeftJoin(Function<C, SubQuery> function, String subQueryAlia) {
        return this.ifCreateSubQueryBlock(function, subQueryAlia, JoinType.LEFT_JOIN);
    }

    @Override
    public final JS ifLeftJoin(Supplier<SubQuery> supplier, String subQueryAlia) {
        return this.ifCreateSubQueryBlock(supplier, subQueryAlia, JoinType.LEFT_JOIN);
    }

    @Override
    public final JT join(TableMeta<?> table, String tableAlias) {
        return this.createTableBlock(table, tableAlias, JoinType.JOIN);
    }

    @Override
    public final JS join(Function<C, SubQuery> function, String subQueryAlia) {
        return this.createTableBlock(function, subQueryAlia, JoinType.JOIN);
    }

    @Override
    public final JS join(Supplier<SubQuery> supplier, String subQueryAlia) {
        return this.createTableBlock(supplier, subQueryAlia, JoinType.JOIN);
    }

    @Override
    public final JT ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifCreateTableBlock(predicate, table, tableAlias, JoinType.JOIN);
    }

    @Override
    public final JS ifJoin(Function<C, SubQuery> function, String subQueryAlia) {
        return this.ifCreateSubQueryBlock(function, subQueryAlia, JoinType.JOIN);
    }

    @Override
    public final JS ifJoin(Supplier<SubQuery> supplier, String subQueryAlia) {
        return this.ifCreateSubQueryBlock(supplier, subQueryAlia, JoinType.JOIN);
    }

    @Override
    public final JT rightJoin(TableMeta<?> table, String tableAlias) {
        return this.createTableBlock(table, tableAlias, JoinType.RIGHT_JOIN);
    }

    @Override
    public final JS rightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        return this.createTableBlock(function, subQueryAlia, JoinType.RIGHT_JOIN);
    }

    @Override
    public final JS rightJoin(Supplier<SubQuery> supplier, String subQueryAlia) {
        return this.createTableBlock(supplier, subQueryAlia, JoinType.RIGHT_JOIN);
    }

    @Override
    public final JT ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias) {
        return this.ifCreateTableBlock(predicate, table, tableAlias, JoinType.RIGHT_JOIN);
    }

    @Override
    public final JS ifRightJoin(Function<C, SubQuery> function, String subQueryAlia) {
        return this.ifCreateSubQueryBlock(function, subQueryAlia, JoinType.RIGHT_JOIN);
    }

    @Override
    public final JS ifRightJoin(Supplier<SubQuery> supplier, String subQueryAlia) {
        return this.ifCreateSubQueryBlock(supplier, subQueryAlia, JoinType.RIGHT_JOIN);
    }


    abstract JT createTableBlock(TableMeta<?> table, String tableAlias, JoinType joinType);

    abstract JT ifCreateTableBlock(Predicate<C> predicate, TableMeta<?> table, String tableAlias, JoinType joinType);

    abstract JS createTableBlock(Supplier<SubQuery> supplier, String tableAlias, JoinType joinType);

    abstract JS createTableBlock(Function<C, SubQuery> function, String tableAlias, JoinType joinType);

    abstract JS ifCreateSubQueryBlock(Supplier<SubQuery> supplier, String tableAlias, JoinType joinType);

    abstract JS ifCreateSubQueryBlock(Function<C, SubQuery> function, String tableAlias, JoinType joinType);


}
