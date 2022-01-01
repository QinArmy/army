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
 * This class representing standard domain delete statement.
 * </p>
 *
 * @param <C> criteria java type used to crate dynamic delete and sub query
 */
final class StandardDelete<C> extends SingleDelete<
        C,// C
        Delete.DeleteSpec,  // WR
        Delete.StandardWhereAndSpec<C>> // WA
        implements Delete.StandardWhereAndSpec<C>, Delete.StandardWhereSpec<C> {

    static StandardDeleteSpec<Void> create() {
        return new StandardDeleteSpecImpl<>(null);
    }

    static <C> StandardDeleteSpec<C> create(final C criteria) {
        Objects.requireNonNull(criteria);
        return new StandardDeleteSpecImpl<>(criteria);
    }

    private final TableMeta<?> table;

    private final String tableAlias;

    private boolean prepared;

    private StandardDelete(TableMeta<?> table, String tableAlias, @Nullable C criteria) {
        super(criteria);
        this.table = table;
        this.tableAlias = tableAlias;
    }

    /*################################## blow SingleDeleteSpec method ##################################*/


    @Override
    public TableMeta<?> table() {
        _Assert.prepared(this.prepared);
        return this.table;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }



    /*################################## blow static inner class ##################################*/


    private static final class StandardDeleteSpecImpl<C> implements StandardDeleteSpec<C> {

        private final C criteria;

        private StandardDeleteSpecImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public Delete.StandardWhereSpec<C> deleteFrom(TableMeta<? extends IDomain> table, String tableAlias) {
            _DialectUtils.validateTableAlias(tableAlias);
            return new StandardDelete<>(table, tableAlias, this.criteria);
        }


    }


}
