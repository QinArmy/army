package io.army.criteria.impl;

import io.army.criteria.Hint;
import io.army.criteria.SQLModifier;
import io.army.criteria.SortPart;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class MySQLSingleUpdate<C> extends UpdateStatement<
        C,
        MySQLUpdate.OrderBySpec<C>,//WR
        MySQLUpdate.SingleWhereAndSpec<C>, // WA
        MySQLUpdate.SingleWhereSpec<C>>  // SR
        implements MySQLUpdate.SingleWhereSpec<C>, MySQLUpdate.SingleWhereAndSpec<C>, MySQLUpdate.OrderBySpec<C>
        , MySQLUpdate {


    private final TableMeta<?> table;

    private final String tableAlias;

    private List<SortPart> orderByList;

    private long rowCount;

    private MySQLSingleUpdate(TableMeta<?> table, String tableAlisa, @Nullable C criteria) {
        super(criteria);
        this.table = table;
        this.tableAlias = tableAlisa;
    }


    @Override
    public final OrderBySpec<C> orderBy(SortPart sortPart) {
        this.orderByList = Collections.singletonList(sortPart);
        return this;
    }

    @Override
    public final OrderBySpec<C> orderBy(SortPart sortPart1, SortPart sortPart2) {
        this.orderByList = Arrays.asList(sortPart1, sortPart2);
        return this;
    }

    @Override
    public final OrderBySpec<C> orderBy(List<SortPart> sortPartList) {
        this.orderByList = new ArrayList<>(sortPartList);
        return this;
    }

    @Override
    public final OrderBySpec<C> orderBy(Function<C, List<SortPart>> function) {
        return this.orderBy(function.apply(this.criteria));
    }

    @Override
    public final OrderBySpec<C> orderBy(Supplier<List<SortPart>> supplier) {
        return this.orderBy(supplier.get());
    }

    @Override
    public final OrderBySpec<C> ifOrderBy(@Nullable SortPart sortPart) {
        if (sortPart != null) {
            this.orderByList = Collections.singletonList(sortPart);
        }
        return this;
    }

    @Override
    public final OrderBySpec<C> ifOrderBy(Supplier<List<SortPart>> supplier) {
        final List<SortPart> sortPartList;
        sortPartList = supplier.get();
        if (!CollectionUtils.isEmpty(sortPartList)) {
            this.orderByList = new ArrayList<>(sortPartList);
        }
        return this;
    }

    @Override
    public final OrderBySpec<C> ifOrderBy(Function<C, List<SortPart>> function) {
        final List<SortPart> sortPartList;
        sortPartList = function.apply(this.criteria);
        if (!CollectionUtils.isEmpty(sortPartList)) {
            this.orderByList = new ArrayList<>(sortPartList);
        }
        return this;
    }

    @Override
    public final UpdateSpec limit(long rowCount) {
        this.rowCount = rowCount;
        return this;
    }

    @Override
    public final UpdateSpec limit(Supplier<Long> supplier) {
        this.rowCount = supplier.get();
        return this;
    }

    @Override
    public final UpdateSpec limit(Function<C, Long> function) {
        this.rowCount = function.apply(this.criteria);
        return this;
    }

    @Override
    public final UpdateSpec ifLimit(Supplier<Long> supplier) {
        final Long rowCount;
        rowCount = supplier.get();
        if (rowCount != null) {
            this.rowCount = rowCount;
        }
        return this;
    }

    @Override
    public final UpdateSpec ifLimit(Function<C, Long> function) {
        final Long rowCount;
        rowCount = function.apply(this.criteria);
        if (rowCount != null) {
            this.rowCount = rowCount;
        }
        return this;
    }


    private static final class SingleUpdate57<C> extends MySQLSingleUpdate<C> implements MySQLUpdate.SingleUpdateSpec<C> {

        private SingleUpdate57(TableMeta<?> table, String tableAlisa, C criteria) {
            super(table, tableAlisa, criteria);
        }

        @Override
        public SinglePartitionSpec<C> update(Function<C, List<Hint>> hints, List<SQLModifier> sqlModifiers, TableMeta<? extends IDomain> table) {
            return null;
        }

        @Override
        public SingleIndexHintSpec<C> update(Function<C, List<Hint>> hints, List<SQLModifier> sqlModifiers, TableMeta<? extends IDomain> table, String tableAlias) {
            return null;
        }

        @Override
        public SinglePartitionSpec<C> update(SQLModifier sqlModifier, TableMeta<? extends IDomain> table) {
            return null;
        }

        @Override
        public SingleIndexHintSpec<C> update(SQLModifier sqlModifier, TableMeta<? extends IDomain> table, String tableAlias) {
            return null;
        }

        @Override
        public SinglePartitionSpec<C> update(TableMeta<? extends IDomain> table) {
            return null;
        }

        @Override
        public SingleIndexHintSpec<C> update(TableMeta<? extends IDomain> table, String tableAlias) {
            return null;
        }


    }// SingleUpdate57


}
