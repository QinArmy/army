package io.army.criteria.impl;

import io.army.criteria.Update;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.dialect._DialectUtils;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

/**
 * <p>
 * This class representing standard single domain update statement.
 * </p>
 *
 * @param <T> domain java type
 * @param <C> criteria java type used to dynamic update and sub query
 */
final class StandardUpdate<T extends IDomain, C> extends AbstractUpdate<
        C,// C
        Update.UpdateSpec, // WR
        Update.StandardWhereAndSpec<C>, // WA
        Update.StandardWhereSpec<C>   // SR
        > implements Update.StandardWhereSpec<C>, Update.StandardWhereAndSpec<C>, Update.StandardSetSpec<C>
        , _SingleUpdate {


    static <C> StandardUpdateSpec<C> create(@Nullable C criteria) {
        return new StandardUpdateSpecImpl<>(criteria);
    }

    private final TableMeta<T> table;

    private final String tableAlias;


    private StandardUpdate(TableMeta<T> table, String tableAlias, @Nullable C criteria) {
        super(criteria);
        this.table = table;
        this.tableAlias = tableAlias;
    }

    /*################################## blow RouteSpec method ##################################*/


    /*################################## blow SetSpec method ##################################*/


    @Override
    public TableMeta<?> table() {
        return this.table;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }


    private static final class StandardUpdateSpecImpl<C> implements StandardUpdateSpec<C> {

        private final C criteria;

        private StandardUpdateSpecImpl(@Nullable C criteria) {
            this.criteria = criteria;
        }

        @Override
        public StandardSetSpec<C> update(final TableMeta<?> table, final String tableAlias) {
            _DialectUtils.validateUpdateTableAlias(table, tableAlias);
            return new StandardUpdate<>(table, tableAlias, this.criteria);
        }

    }


}

