package io.army.criteria.impl;

import io.army.DialectMode;
import io.army.beans.ReadWrapper;
import io.army.criteria.Delete;
import io.army.criteria.Statement;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._BatchDml;
import io.army.dialect._MockDialects;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.stmt.BatchStmt;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class representing standard domain delete statement.
 * </p>
 *
 * @param <C> criteria java type used to crate dynamic delete and sub query
 */
abstract class StandardDelete<C, DR, WR, WA> extends SingleDelete<C, WR, WA>
        implements Delete.StandardDeleteClause<DR> {

    static <C> StandardDeleteSpec<C> simple(@Nullable C criteria) {
        return new SimpleDelete<>(criteria);
    }

    static <C> StandardBatchDeleteSpec<C> batch(@Nullable C criteria) {
        return new BatchDelete<>(criteria);
    }


    private TableMeta<?> table;

    private String tableAlias;

    private StandardDelete(@Nullable C criteria) {
        super(CriteriaUtils.primaryContext(criteria));
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }

    @Override
    public final DR deleteFrom(TableMeta<? extends IDomain> table, String tableAlias) {
        if (this.table != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.table = table;
        this.tableAlias = tableAlias;
        return (DR) this;
    }

    @Override
    void onAsDelete() {
        if (this.table == null || this.tableAlias == null) {
            throw _Exceptions.castCriteriaApi();
        }
        if (this instanceof BatchDelete && ((BatchDelete<C>) this).wrapperList == null) {
            throw _Exceptions.castCriteriaApi();
        }
    }

    @Override
    void onClear() {
        this.tableAlias = null;
        if (this instanceof BatchDelete) {
            ((BatchDelete<C>) this).wrapperList = null;
        }
    }

    @Override
    public final TableMeta<?> table() {
        prepared();
        return this.table;
    }

    @Override
    public final String tableAlias() {
        return this.tableAlias;
    }


    @Override
    public String toString() {
        final String s;
        if (this.isPrepared()) {
            s = this.mockAsString(DialectMode.MySQL57);
        } else {
            s = super.toString();
        }
        return s;
    }

    @Override
    public final void mock(DialectMode mode) {
        System.out.println(this.mockAsString(mode));
    }

    @Override
    public final String mockAsString(DialectMode mode) {
        final Stmt stmt;
        stmt = this.mockAsStmt(mode);
        final StringBuilder builder = new StringBuilder();
        if (stmt instanceof SimpleStmt) {
            final SimpleStmt simpleStmt = (SimpleStmt) stmt;
            _Assert.noNamedParam(simpleStmt.paramGroup());
            builder.append("delete sql:\n")
                    .append(simpleStmt.sql());

        } else if (stmt instanceof BatchStmt) {
            builder.append("batch delete sql:\n")
                    .append(((BatchStmt) stmt).sql());
        } else {
            throw new IllegalStateException("stmt error.");
        }
        return builder.toString();
    }

    @Override
    public final Stmt mockAsStmt(DialectMode mode) {
        return _MockDialects.from(mode).delete(this, Visible.ONLY_VISIBLE);
    }



    /*################################## blow static inner class ##################################*/

    private static final class SimpleDelete<C> extends StandardDelete<
            C,
            Delete.StandardWhereSpec<C>,
            Delete.DeleteSpec,
            Delete.StandardWhereAndSpec<C>>
            implements Delete.StandardWhereSpec<C>, Delete.StandardWhereAndSpec<C>
            , Delete.StandardDeleteSpec<C> {

        private SimpleDelete(@Nullable C criteria) {
            super(criteria);
        }


    }//SimpleDelete

    private static final class BatchDelete<C> extends StandardDelete<
            C,
            Delete.StandardBatchWhereSpec<C>,
            Statement.BatchParamClause<C, Delete.DeleteSpec>,
            Delete.StandardBatchWhereAndSpec<C>>
            implements Delete.StandardBatchWhereAndSpec<C>, Delete.StandardBatchWhereSpec<C>
            , Statement.BatchParamClause<C, Delete.DeleteSpec>, Delete.StandardBatchDeleteSpec<C>, _BatchDml {

        private List<ReadWrapper> wrapperList;

        private BatchDelete(@Nullable C criteria) {
            super(criteria);
        }


        @Override
        public DeleteSpec paramMaps(List<Map<String, Object>> mapList) {
            this.wrapperList = CriteriaUtils.paramMaps(mapList);
            return this;
        }

        @Override
        public DeleteSpec paramMaps(Supplier<List<Map<String, Object>>> supplier) {
            return this.paramMaps(supplier.get());
        }

        @Override
        public DeleteSpec paramMaps(Function<C, List<Map<String, Object>>> function) {
            return this.paramMaps(function.apply(this.criteria));
        }

        @Override
        public DeleteSpec paramBeans(List<?> beanList) {
            this.wrapperList = CriteriaUtils.paramBeans(beanList);
            return this;
        }

        @Override
        public DeleteSpec paramBeans(Supplier<List<?>> supplier) {
            return this.paramBeans(supplier.get());
        }

        @Override
        public DeleteSpec paramBeans(Function<C, List<?>> function) {
            return this.paramBeans(function.apply(this.criteria));
        }

        @Override
        public List<ReadWrapper> wrapperList() {
            return this.wrapperList;
        }

    }// BatchDelete


}
