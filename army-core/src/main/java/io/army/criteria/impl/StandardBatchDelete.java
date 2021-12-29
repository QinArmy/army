package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.dialect._DialectUtils;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Assert;

import java.util.Objects;

/**
 * <p>
 * This class representing standard batch domain delete statement.
 * </p>
 *
 * @param <C> criteria java type used to create dynamic delete and sub query
 */
final class StandardBatchDelete<C> extends BatchSingleDelete<
        C,// C
        Delete.StandardBatchParamSpec<C>, // WR
        Delete.StandardBatchWhereAndSpec<C>,// WA
        Delete.DeleteSpec> // BR
        implements Delete.StandardBatchWhereSpec<C>, Delete.StandardBatchWhereAndSpec<C> {

    static StandardBatchDeleteSpec<Void> create() {
        return new StandardBatchDeleteSpecImpl<>(null);
    }

    static <C> StandardBatchDeleteSpec<C> create(C criteria) {
        Objects.requireNonNull(criteria);
        return new StandardBatchDeleteSpecImpl<>(criteria);
    }

    private final TableMeta<?> table;

    private final String tableAlias;


    private StandardBatchDelete(TableMeta<?> table, String tableAlias, @Nullable C criteria) {
        super(criteria);
        this.table = table;
        this.tableAlias = tableAlias;
    }

    /*################################## blow BatchSingleDeleteSpec method ##################################*/


    @Override
    public TableMeta<?> table() {
        _Assert.prepared(this.prepared);
        return this.table;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }


    private static final class StandardBatchDeleteSpecImpl<C> implements StandardBatchDeleteSpec<C> {

        private final C criteria;

        private StandardBatchDeleteSpecImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public StandardBatchWhereSpec<C> deleteFrom(TableMeta<? extends IDomain> table, String tableAlias) {
            _DialectUtils.validateTableAlias(tableAlias);
            return new StandardBatchDelete<>(table, tableAlias, this.criteria);
        }

    }


}
