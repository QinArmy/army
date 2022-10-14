package io.army.criteria.impl;

import io.army.criteria.Delete;
import io.army.criteria.ReturningDelete;
import io.army.criteria.StandardDelete;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._BatchDml;
import io.army.dialect.mysql.MySQLDialect;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class representing standard domain delete statement.
 * </p>
 *
 * @since 1.0
 */
abstract class StandardDeletes<WR, WA> extends SingleDelete<Delete, ReturningDelete, WR, WA, Object, Object>
        implements StandardDelete, Delete {

    static StandardDelete._DeleteSpec<Delete> singleDelete() {
        return new SingleDeleteClause();
    }

    static StandardDelete._BatchDeleteSpec<Delete> batchSingleDelete() {
        return new BatchSingleDeleteClause();
    }

    StandardDeletes(CriteriaContext context, TableMeta<?> deleteTable, String tableAlias) {
        super(context, deleteTable, tableAlias);
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


    @Override
    final Delete onAsDelete() {
        if (this instanceof BatchSingleDelete && ((BatchSingleDelete) this).paramList == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this;
    }

    @Override
    final void onClear() {
        if (this instanceof BatchSingleDelete) {
            ((BatchSingleDelete) this).paramList = null;
        }
    }


    /*################################## blow static inner class ##################################*/


    private static final class SimpleSingleDelete extends StandardDeletes<
            _DmlDeleteSpec<Delete>,
            _WhereAndSpec<Delete>> implements _WhereSpec<Delete>
            , _WhereAndSpec<Delete> {

        private SimpleSingleDelete(CriteriaContext context, TableMeta<?> deleteTable, String tableAlias) {
            super(context, deleteTable, tableAlias);
        }


    }//SimpleSingleDelete

    private static final class BatchSingleDelete extends StandardDeletes<
            _BatchParamClause<_DmlDeleteSpec<Delete>>,
            _BatchWhereAndSpec<Delete>> implements _BatchWhereSpec<Delete>
            , _BatchWhereAndSpec<Delete>, _BatchDml {


        private List<?> paramList;

        private BatchSingleDelete(CriteriaContext context, TableMeta<?> deleteTable, String tableAlias) {
            super(context, deleteTable, tableAlias);
        }

        @Override
        public <P> _DmlDeleteSpec<Delete> paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlDeleteSpec<Delete> paramList(Supplier<List<P>> supplier) {
            return this.paramList(supplier.get());
        }

        @Override
        public _DmlDeleteSpec<Delete> paramList(Function<String, ?> function, String keyName) {
            return this.paramList((List<?>) function.apply(keyName));
        }

        @Override
        public List<?> paramList() {
            final List<?> list = this.paramList;
            if (list == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }


    }//BatchSingleDelete


    private static final class SingleDeleteClause implements StandardDelete._DeleteSpec<Delete> {

        private final CriteriaContext context;

        private SingleDeleteClause() {
            this.context = CriteriaContexts.primarySingleDmlContext();
            ContextStack.push(this.context);
        }

        @Override
        public _WhereSpec<Delete> deleteFrom(TableMeta<?> table, String tableAlias) {
            return new SimpleSingleDelete(this.context, table, tableAlias);
        }


    }//SingleDeleteClause


    private static final class BatchSingleDeleteClause implements StandardDelete._BatchDeleteSpec<Delete> {

        private final CriteriaContext context;

        private BatchSingleDeleteClause() {
            this.context = CriteriaContexts.primarySingleDmlContext();
            ContextStack.push(this.context);
        }

        @Override
        public _BatchWhereSpec<Delete> deleteFrom(TableMeta<?> table, String tableAlias) {
            return new BatchSingleDelete(this.context, table, tableAlias);
        }

    }//BatchSingleDeleteClause


}
