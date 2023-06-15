package io.army.criteria.impl;

import io.army.criteria.*;
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

/**
 * <p>
 * This class representing standard domain delete statement.
 * </p>
 *
 * @since 1.0
 */
abstract class StandardDeletes<I extends Item, BI extends Item, WE extends Item, DR>
        extends SingleDeleteStatement<
        I,
        BI,
        StandardCtes,
        WE,
        Statement._DmlDeleteSpec<I>,
        StandardDelete._WhereAndSpec<I>,
        Object, Object, Object, Object, Object>
        implements StandardDelete, DeleteStatement {

    static _WithSpec<Delete> singleDelete(StandardDialect dialect) {
        return new SimpleSingleDelete<>(dialect, null, SQLs.SIMPLE_DELETE, SQLs.ERROR_FUNC);
    }

    static _WithSpec<_BatchParamClause<BatchDelete>> batchSingleDelete(StandardDialect dialect) {
        return new SimpleSingleDelete<>(dialect, null, SQLs::forBatchDelete, SQLs.BATCH_DELETE);
    }

    static _DomainDeleteClause<Delete> domainDelete() {
        return new SimpleDomainDelete<>(SQLs.SIMPLE_DELETE, SQLs.ERROR_FUNC);
    }

    static _DomainDeleteClause<_BatchParamClause<BatchDelete>> batchDomainDelete() {
        return new SimpleDomainDelete<>(SQLs::forBatchDelete, SQLs.BATCH_DELETE);
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
        //no-op
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


    private static final class SimpleSingleDelete<I extends Item, BI extends Item> extends StandardDeletes<
            I,
            BI,
            StandardDelete._StandardDeleteClause<I>,
            _WhereSpec<I>>
            implements _WhereSpec<I>,
            _WhereAndSpec<I>,
            StandardDelete._WithSpec<I>,
            BatchDeleteSpec<BI> {

        private final Function<? super BatchDeleteSpec<BI>, I> function;

        private final Function<? super BatchDelete, BI> batchFunc;


        private SimpleSingleDelete(StandardDialect dialect, @Nullable ArmyStmtSpec spec,
                                   Function<? super BatchDeleteSpec<BI>, I> function,
                                   Function<? super BatchDelete, BI> batchFunc) {
            super(dialect, spec);
            this.function = function;
            this.batchFunc = batchFunc;
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

        @Override
        BI onAsBatchDelete(List<?> paramList) {
            return this.batchFunc.apply(new StandardBatchDelete(this, paramList));
        }


    }//SimpleSingleDelete

    private static final class SimpleDomainDelete<I extends Item, BI extends Item> extends StandardDeletes<
            I,
            BI,
            _DomainDeleteClause<I>,
            _WhereSpec<I>>
            implements _DomainDeleteClause<I>,
            _WhereSpec<I>,
            _WhereAndSpec<I>,
            _DomainDelete,
            BatchDeleteSpec<BI> {

        private final Function<? super BatchDeleteSpec<BI>, I> function;

        private final Function<? super BatchDelete, BI> batchFunc;


        private SimpleDomainDelete(Function<? super BatchDeleteSpec<BI>, I> function,
                                   Function<? super BatchDelete, BI> batchFunc) {
            super(StandardDialect.STANDARD10, null);
            this.function = function;
            this.batchFunc = batchFunc;
        }


        @Override
        public _WhereSpec<I> deleteFrom(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
            return this.deleteFrom(table, tableAlias);
        }

        @Override
        I onAsDelete() {
            return this.function.apply(this);
        }

        @Override
        BI onAsBatchDelete(List<?> paramList) {
            return this.batchFunc.apply(new DomainBatchDelete(this, paramList));
        }


    }//SimpleDomainDelete

    private static final class StandardBatchDelete extends ArmyBathDelete
            implements StandardDelete, BatchDelete {

        private StandardBatchDelete(SimpleSingleDelete<?, ?> statement, List<?> paramList) {
            super(statement, paramList);
        }

        @Override
        Dialect statementDialect() {
            return MySQLDialect.MySQL57;
        }


    }//StandardBatchDelete

    private static final class DomainBatchDelete extends ArmyBathDelete
            implements _DomainDelete, BatchDelete {

        private DomainBatchDelete(SimpleDomainDelete<?, ?> statement, List<?> paramList) {
            super(statement, paramList);
        }

        @Override
        Dialect statementDialect() {
            return MySQLDialect.MySQL57;
        }


    }//DomainBatchDelete


}
