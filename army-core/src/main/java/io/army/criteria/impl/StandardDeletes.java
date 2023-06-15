package io.army.criteria.impl;

import io.army.criteria.BatchDelete;
import io.army.criteria.Delete;
import io.army.criteria.DeleteStatement;
import io.army.criteria.Item;
import io.army.criteria.impl.inner._BatchStatement;
import io.army.criteria.impl.inner._DomainDelete;
import io.army.criteria.standard.StandardCtes;
import io.army.criteria.standard.StandardDelete;
import io.army.criteria.standard.StandardQuery;
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
abstract class StandardDeletes<I extends Item, WE extends Item, DR, WR, WA>
        extends SingleDeleteStatement.WithSingleDelete<I, StandardCtes, WE, WR, WA, Object, Object, Object, Object, Object>
        implements StandardDelete, DeleteStatement {

    static _WithSpec<Delete> singleDelete(StandardDialect dialect) {
        return new SimpleSingleDelete<>(dialect, null, SQLs::identity);
    }

    static _BatchWithSpec batchSingleDelete(StandardDialect dialect) {
        return new BatchSingleDelete(dialect);
    }

    static _DomainDeleteClause domainDelete() {
        return new SimpleDomainDelete();
    }

    static _BatchDomainDeleteClause batchDomainDelete() {
        return new BatchDomainDelete();
    }


    private TableMeta<?> deleteTable;

    private String tableAlias;

    private StandardDeletes(StandardDialect dialect, @Nullable ArmyStmtSpec spec) {
        super(spec, CriteriaContexts.primarySingleDmlContext(dialect, spec));
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
            ((StandardBatchDelete<?>) this).paramList = null;
        }
    }

    @Override
    final Dialect statementDialect() {
        return MySQLDialect.MySQL57;
    }

    @Override
    final StandardCtes createCteBuilder(boolean recursive) {
        return StandardQueries.cteBuilder(recursive, this.context);
    }

    /*################################## blow static inner class ##################################*/


    private static final class SimpleSingleDelete<I extends Item> extends StandardDeletes<
            I,
            StandardDelete._StandardDeleteClause<I>,
            _WhereSpec<I>,
            _DmlDeleteSpec<I>,
            _WhereAndSpec<I>>
            implements _WhereSpec<I>,
            _WhereAndSpec<I>,
            StandardDelete._WithSpec<I>,
            Delete {

        private final Function<? super Delete, I> function;


        private SimpleSingleDelete(StandardDialect dialect, @Nullable ArmyStmtSpec spec,
                                   Function<? super Delete, I> function) {
            super(dialect, spec);
            this.function = function;
        }

        @Override
        public StandardQuery._StaticCteParensSpec<_StandardDeleteClause<I>> with(String name) {
            return StandardQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public StandardQuery._StaticCteParensSpec<_StandardDeleteClause<I>> withRecursive(String name) {
            return StandardQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
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
            _DomainDeleteClause,
            _WhereSpec<Delete>,
            _DmlDeleteSpec<Delete>,
            _WhereAndSpec<Delete>>
            implements _DomainDeleteClause,
            _WhereSpec<Delete>,
            _WhereAndSpec<Delete>,
            _DomainDelete,
            Delete {


        private SimpleDomainDelete() {
            super(StandardDialect.STANDARD10, null);
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


    private static abstract class StandardBatchDelete<WE extends Item> extends StandardDeletes<
            BatchDelete,
            WE,
            _BatchWhereSpec<BatchDelete>,
            _BatchParamClause<_DmlDeleteSpec<BatchDelete>>,
            _BatchWhereAndSpec<BatchDelete>>
            implements _BatchWhereSpec<BatchDelete>,
            _BatchWhereAndSpec<BatchDelete>,
            BatchDelete,
            _BatchStatement {

        private List<?> paramList;


        private StandardBatchDelete(StandardDialect dialect) {
            super(dialect, null);
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


    private static final class BatchSingleDelete extends StandardBatchDelete<_BatchDeleteClause>
            implements StandardDelete._BatchWithSpec {

        private BatchSingleDelete(StandardDialect dialect) {
            super(dialect);
        }

        @Override
        public StandardQuery._StaticCteParensSpec<_BatchDeleteClause> with(String name) {
            return StandardQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public StandardQuery._StaticCteParensSpec<_BatchDeleteClause> withRecursive(String name) {
            return StandardQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public _BatchWhereSpec<BatchDelete> deleteFrom(SingleTableMeta<?> table, SQLs.WordAs as, String tableAlias) {
            return this.deleteFrom(table, tableAlias);
        }

    }//BatchSingleDelete


    private static final class BatchDomainDelete extends StandardBatchDelete<Item>
            implements _DomainDelete,
            _BatchDomainDeleteClause {

        private BatchDomainDelete() {
            super(StandardDialect.STANDARD10);
        }

        @Override
        public _BatchWhereSpec<BatchDelete> deleteFrom(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
            return this.deleteFrom(table, tableAlias);
        }


    }//BatchDomainDelete


}
