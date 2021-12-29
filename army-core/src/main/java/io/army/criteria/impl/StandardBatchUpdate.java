package io.army.criteria.impl;

import io.army.criteria.Update;
import io.army.criteria.impl.inner._BatchSingleUpdate;
import io.army.dialect._DialectUtils;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.Objects;

/**
 * <p>
 * This class representing standard batch domain update statement.
 * </p>
 *
 * @param <T> domain java type
 * @param <C> criteria java type used to dynamic update and sub query
 */
final class StandardBatchUpdate<T extends IDomain, C> extends BatchSingleUpdate<
        T,//
        C, //C
        Update.StandardBatchParamSpec<C>, //WR
        Update.StandardBatchWhereAndSpec<C>, //WA
        Update.StandardBatchWhereAndSpec<C>, // AR
        Update.StandardBatchWhereSpec<T, C>,   //SR
        Update.UpdateSpec                   // BR
        > implements _BatchSingleUpdate, Update.StandardBatchParamSpec<C>, Update.StandardBatchWhereAndSpec<C>
        , Update.StandardBatchWhereSpec<T, C> {

    static StandardBatchUpdateSpec<Void> create() {
        return new BatchUpdateSpecImpl<>(null);
    }

    static <C> StandardBatchUpdateSpec<C> create(C criteria) {
        Objects.requireNonNull(criteria);
        return new BatchUpdateSpecImpl<>(criteria);
    }

    private final TableMeta<T> table;

    private final String tableAlias;

    private StandardBatchUpdate(TableMeta<T> table, String tableAlias, @Nullable C criteria) {
        super(criteria);
        this.table = table;
        this.tableAlias = tableAlias;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public TableMeta<?> table() {
        return this.table;
    }

    private static final class BatchUpdateSpecImpl<C> implements StandardBatchUpdateSpec<C> {

        private final C criteria;

        private BatchUpdateSpecImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public <T extends IDomain> StandardBatchSetSpec<T, C> update(final TableMeta<T> table, final String tableAlias) {
            _DialectUtils.validateUpdateTableAlias(table, tableAlias);
            return new StandardBatchUpdate<>(table, tableAlias, this.criteria);
        }

    }


}
