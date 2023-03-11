package io.army.criteria.impl;

import io.army.criteria.DeleteStatement;
import io.army.criteria.DmlStatement;
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
        extends SingleDeleteStatement<I, WR, WA, Object, Object, Object, Object>
        implements StandardDelete, DeleteStatement {

    static <I extends Item> _StandardDeleteClause<I> singleDelete(Function<DeleteStatement, I> function) {
        return new SimpleSingleDelete<>(function);
    }

    static <I extends Item> _BatchDeleteClause<I> batchSingleDelete(Function<DeleteStatement, I> function) {
        return new BatchSingleDelete<>(function);
    }

    static _DomainDeleteClause domainDelete() {
        return new SimpleDomainDelete();
    }

    static _BatchDomainDeleteClause batchDomainDelete() {
        return new BatchDomainDelete();
    }


    private TableMeta<?> deleteTable;

    private String tableAlias;

    private StandardDeletes() {
        super(CriteriaContexts.primarySingleDmlContext(null, null));
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
        if (this instanceof StandardDeletes.BatchDelete) {
            ((BatchDelete<?>) this).paramList = null;
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
            StandardDelete._StandardDeleteClause<I> {

        private final Function<DeleteStatement, I> function;


        private SimpleSingleDelete(Function<DeleteStatement, I> function) {
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
            DeleteStatement,
            _WhereSpec<DeleteStatement>,
            _DmlDeleteSpec<DeleteStatement>,
            _WhereAndSpec<DeleteStatement>>
            implements _DomainDeleteClause,
            _WhereSpec<DeleteStatement>,
            _WhereAndSpec<DeleteStatement>,
            _DomainDelete {


        private SimpleDomainDelete() {
        }


        @Override
        public _WhereSpec<DeleteStatement> deleteFrom(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
            return this.deleteFrom(table, tableAlias);
        }

        @Override
        DeleteStatement onAsDelete() {
            return this;
        }

    }//SimpleDomainDelete


    private static abstract class BatchDelete<I extends Item> extends StandardDeletes<
            I,
            _BatchWhereSpec<I>,
            _BatchParamClause<_DmlDeleteSpec<I>>,
            _BatchWhereAndSpec<I>>
            implements _BatchWhereSpec<I>,
            _BatchWhereAndSpec<I>,
            _BatchDml
            , DmlStatement {

        private final Function<DeleteStatement, I> function;

        private List<?> paramList;


        private BatchDelete(Function<DeleteStatement, I> function) {
            this.function = function;
        }


        @Override
        public final <P> _DmlDeleteSpec<I> paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public final <P> _DmlDeleteSpec<I> paramList(Supplier<List<P>> supplier) {
            return this.paramList(supplier.get());
        }

        @Override
        public final _DmlDeleteSpec<I> paramList(Function<String, ?> function, String keyName) {
            return this.paramList((List<?>) function.apply(keyName));
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
        final I onAsDelete() {
            if (this.paramList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return this.function.apply(this);
        }


    }//BatchDelete


    private static final class BatchSingleDelete<I extends Item> extends BatchDelete<I>
            implements _BatchDeleteClause<I> {

        private BatchSingleDelete(Function<DeleteStatement, I> function) {
            super(function);
        }

        @Override
        public _BatchWhereSpec<I> deleteFrom(SingleTableMeta<?> table, SQLsSyntax.WordAs as, String tableAlias) {
            return this.deleteFrom(table, tableAlias);
        }

    }//BatchSingleDelete


    private static final class BatchDomainDelete extends BatchDelete<DeleteStatement>
            implements _DomainDelete,
            _BatchDomainDeleteClause {

        private BatchDomainDelete() {
            super(SQLs._DELETE_IDENTITY);
        }

        @Override
        public _BatchWhereSpec<DeleteStatement> deleteFrom(TableMeta<?> table, SQLsSyntax.WordAs as, String tableAlias) {
            return this.deleteFrom(table, tableAlias);
        }


    }//BatchDomainDelete


}
