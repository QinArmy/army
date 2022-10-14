package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.criteria.StandardStatement;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._BatchDml;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class representing standard domain delete statement.
 * </p>
 *
 * @param <C> criteria object java type
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class StandardDelete<C, DR, WR, WA> extends SingleDelete0<C, WR, WA, Delete>
        implements io.army.criteria.StandardDelete.StandardDeleteFromClause<DR>, StandardStatement, Delete, Delete._DeleteSpec {

    static <C> io.army.criteria.StandardDelete.StandardDeleteSpec<C> simple(@Nullable C criteria) {
        return new SimpleDelete<>(criteria);
    }

    static <C> io.army.criteria.StandardDelete.StandardBatchDeleteSpec<C> batch(@Nullable C criteria) {
        return new BatchDelete<>(criteria);
    }

    private TableMeta<?> table;

    private String tableAlias;

    private StandardDelete(@Nullable C criteria) {
        super(CriteriaContexts.primarySingleDmlContext(criteria));
    }

    @Override
    public final DR deleteFrom(TableMeta<?> table, String tableAlias) {
        if (this.table != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.table = table;
        this.tableAlias = tableAlias;
        return (DR) this;
    }

    @Override
    final void onAsDelete() {
        if (this.table == null || this.tableAlias == null) {
            throw _Exceptions.castCriteriaApi();
        }
        if (this instanceof BatchDelete && ((BatchDelete<C>) this).paramList == null) {
            throw _Exceptions.castCriteriaApi();
        }
    }

    @Override
    final void onClear() {
        this.tableAlias = null;
        if (this instanceof BatchDelete) {
            ((BatchDelete<C>) this).paramList = null;
        }
    }

    @Override
    public final TableMeta<?> table() {
        this.prepared();
        return this.table;
    }

    @Override
    public final String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public final String toString() {
        final String s;
        if (this.isPrepared()) {
            s = this.mockAsString(MySQLDialect.MySQL57, Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
    }


    /*################################## blow static inner class ##################################*/

    private static final class SimpleDelete<C> extends StandardDelete<
            C,
            io.army.criteria.StandardDelete.StandardWhereSpec<C>,
            _DeleteSpec,
            io.army.criteria.StandardDelete.StandardWhereAndSpec<C>>
            implements io.army.criteria.StandardDelete.StandardWhereSpec<C>, io.army.criteria.StandardDelete.StandardWhereAndSpec<C>
            , io.army.criteria.StandardDelete.StandardDeleteSpec<C> {

        private SimpleDelete(@Nullable C criteria) {
            super(criteria);
        }


    }//SimpleDelete

    private static final class BatchDelete<C> extends StandardDelete<
            C,
            io.army.criteria.StandardDelete.StandardBatchWhereSpec<C>,
            _BatchParamClause<C, _DeleteSpec>,
            io.army.criteria.StandardDelete.StandardBatchWhereAndSpec<C>>
            implements io.army.criteria.StandardDelete.StandardBatchWhereAndSpec<C>, io.army.criteria.StandardDelete.StandardBatchWhereSpec<C>
            , _BatchParamClause<C, _DeleteSpec>, io.army.criteria.StandardDelete.StandardBatchDeleteSpec<C>, _BatchDml {

        private List<?> paramList;

        private BatchDelete(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public <P> _DeleteSpec paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(paramList);
            return this;
        }

        @Override
        public <P> _DeleteSpec paramList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(supplier.get());
            return this;
        }

        @Override
        public <P> _DeleteSpec paramList(Function<C, List<P>> function) {
            this.paramList = CriteriaUtils.paramList(function.apply(this.criteria));
            return this;
        }

        @Override
        public _DeleteSpec paramList(Function<String, ?> function, String keyName) {
            this.paramList = CriteriaUtils.paramList((List<?>) function.apply(keyName));
            return this;
        }

        @Override
        public List<?> paramList() {
            return this.paramList;
        }

    }// BatchDelete


}
