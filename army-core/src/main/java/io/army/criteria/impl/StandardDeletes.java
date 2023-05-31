package io.army.criteria.impl;

import io.army.criteria.BatchDelete;
import io.army.criteria.Delete;
import io.army.criteria.DeleteStatement;
import io.army.criteria.Item;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._DomainDelete;
import io.army.criteria.standard.StandardDelete;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.SingleTableMeta;
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
abstract class StandardDeletes<I extends Item, DR, WR, WA>
        extends SingleDeleteStatement<I, WR, WA, Object, Object, Object, Object, Object>
        implements StandardDelete, DeleteStatement {

    static _StandardDeleteClause<Delete> singleDelete() {
        return new SimpleSingleDelete<>(null, SQLs::_identity);
    }

    static _BatchDeleteClause batchSingleDelete() {
        return new BatchSingleDelete();
    }

    static _DomainDeleteClause domainDelete() {
        return new SimpleDomainDelete();
    }

    static _BatchDomainDeleteClause batchDomainDelete() {
        return new BatchDomainDelete();
    }


    private TableMeta<?> deleteTable;

    private String tableAlias;

    private StandardDeletes(@Nullable ArmyStmtSpec spec) {
        super(CriteriaContexts.primarySingleDmlContext(spec));
    }


    @SuppressWarnings("unchecked")
    final DR deleteFrom(final @Nullable TableMeta<?> table, final @Nullable String tableAlias) {
        if (this.deleteTable != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (table == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (tableAlias == null) {
            throw ContextStack.nullPointer(this.context);
        }
        this.deleteTable = table;
        this.tableAlias = tableAlias;
        return (DR) this;
    }

    @Override
    public final TableMeta<?> table() {
        final TableMeta<?> deleteTable = this.deleteTable;
        if (deleteTable == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return deleteTable;
    }

    @Override
    public final String tableAlias() {
        final String tableAlias = this.tableAlias;
        if (tableAlias == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return tableAlias;
    }

    @Override
    final void onClear() {
        if (this instanceof StandardBatchDelete) {
            ((StandardBatchDelete) this).paramList = null;
        }
    }

    @Override
    final Dialect statementDialect() {
        return MySQLDialect.MySQL57;
    }

    /*################################## blow static inner class ##################################*/


    private static final class SimpleSingleDelete<I extends Item> extends StandardDeletes<
            I,
            _WhereSpec<I>,
            _DmlDeleteSpec<I>,
            _WhereAndSpec<I>>
            implements _WhereSpec<I>,
            _WhereAndSpec<I>,
            StandardDelete._StandardDeleteClause<I>,
            Delete {

        private final Function<? super Delete, I> function;


        private SimpleSingleDelete(@Nullable ArmyStmtSpec spec, Function<? super Delete, I> function) {
            super(spec);
            this.function = function;
        }


        @Override
        public _WhereSpec<I> deleteFrom(final @Nullable SingleTableMeta<?> table, SQLs.WordAs as,
                                        final @Nullable String tableAlias) {
            return this.deleteFrom(table, tableAlias);
        }

        @Override
        I onAsDelete() {
            return this.function.apply(this);
        }


    }//SimpleSingleDelete

    private static final class SimpleDomainDelete extends StandardDeletes<
            Delete,
            _WhereSpec<Delete>,
            _DmlDeleteSpec<Delete>,
            _WhereAndSpec<Delete>>
            implements _DomainDeleteClause,
            _WhereSpec<Delete>,
            _WhereAndSpec<Delete>,
            _DomainDelete,
            Delete {


        private SimpleDomainDelete() {
            super(null);
        }


        @Override
        public _WhereSpec<Delete> deleteFrom(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
            return this.deleteFrom(table, tableAlias);
        }

        @Override
        Delete onAsDelete() {
            return this;
        }

    }//SimpleDomainDelete


    private static abstract class StandardBatchDelete extends StandardDeletes<
            BatchDelete,
            _BatchWhereSpec<BatchDelete>,
            _BatchParamClause<_DmlDeleteSpec<BatchDelete>>,
            _BatchWhereAndSpec<BatchDelete>>
            implements _BatchWhereSpec<BatchDelete>,
            _BatchWhereAndSpec<BatchDelete>,
            BatchDelete,
            _BatchDml {

        private List<?> paramList;


        private StandardBatchDelete() {
            super(null);
        }


        @Override
        public final <P> _DmlDeleteSpec<BatchDelete> namedParamList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public final <P> _DmlDeleteSpec<BatchDelete> namedParamList(Supplier<List<P>> supplier) {
            return this.namedParamList(supplier.get());
        }

        @Override
        public final _DmlDeleteSpec<BatchDelete> namedParamList(Function<String, ?> function, String keyName) {
            return this.namedParamList((List<?>) function.apply(keyName));
        }

        @Override
        public final List<?> paramList() {
            final List<?> list = this.paramList;
            if (list == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }

        @Override
        final BatchDelete onAsDelete() {
            if (this.paramList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return this;
        }


    }//BatchDelete


    private static final class BatchSingleDelete extends StandardBatchDelete
            implements _BatchDeleteClause {

        private BatchSingleDelete() {
        }

        @Override
        public _BatchWhereSpec<BatchDelete> deleteFrom(SingleTableMeta<?> table, SQLsSyntax.WordAs as, String tableAlias) {
            return this.deleteFrom(table, tableAlias);
        }

    }//BatchSingleDelete


    private static final class BatchDomainDelete extends StandardBatchDelete
            implements _DomainDelete,
            _BatchDomainDeleteClause {

        private BatchDomainDelete() {
        }

        @Override
        public _BatchWhereSpec<BatchDelete> deleteFrom(TableMeta<?> table, SQLsSyntax.WordAs as, String tableAlias) {
            return this.deleteFrom(table, tableAlias);
        }


    }//BatchDomainDelete


}
