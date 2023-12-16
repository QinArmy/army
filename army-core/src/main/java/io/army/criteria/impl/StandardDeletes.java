package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchStatement;
import io.army.criteria.impl.inner._DomainDelete;
import io.army.criteria.standard.StandardCtes;
import io.army.criteria.standard.StandardDelete;
import io.army.criteria.standard.StandardQuery;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;

import javax.annotation.Nullable;

import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.List;

/**
 * <p>
 * This class representing standard domain delete statement.
 *
 * @since 0.6.0
 */
abstract class StandardDeletes<I extends Item, WE extends Item, DR>
        extends SingleDeleteStatement<
        I,
        StandardCtes,
        WE,
        Statement._DmlDeleteSpec<I>,
        StandardDelete._WhereAndSpec<I>,
        Object, Object, Object, Object, Object>
        implements StandardDelete, DeleteStatement {

    static _WithSpec<Delete> singleDelete(StandardDialect dialect) {
        return new StandardSimpleDelete(dialect, null);
    }

    static _WithSpec<_BatchDeleteParamSpec> batchSingleDelete(StandardDialect dialect) {
        return new StandardBatchDelete(dialect, null);
    }

    static _DomainDeleteClause<Delete> domainDelete() {
        return new DomainSimpleDelete();
    }

    static _DomainDeleteClause<_BatchDeleteParamSpec> batchDomainDelete() {
        return new DomainBatchDelete();
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


    private static abstract class StandardDeleteStatement<I extends Item> extends StandardDeletes<
            I,
            StandardDelete._StandardDeleteClause<I>,
            _WhereSpec<I>>
            implements _WhereSpec<I>,
            _WhereAndSpec<I>,
            StandardDelete._WithSpec<I>,
            StandardDelete {

        private StandardDeleteStatement(StandardDialect dialect, @Nullable ArmyStmtSpec spec) {
            super(dialect, spec);
        }

        @Override
        public final StandardQuery._StaticCteParensSpec<_StandardDeleteClause<I>> with(String name) {
            return StandardQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public final StandardQuery._StaticCteParensSpec<_StandardDeleteClause<I>> withRecursive(String name) {
            return StandardQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public final _WhereSpec<I> deleteFrom(final @Nullable SingleTableMeta<?> table, SQLs.WordAs as,
                                              final @Nullable String tableAlias) {
            return this.deleteFrom(table, tableAlias);
        }


    }//StandardDeleteStatement

    private static final class StandardSimpleDelete extends StandardDeleteStatement<Delete>
            implements Delete {

        private StandardSimpleDelete(StandardDialect dialect, @Nullable ArmyStmtSpec spec) {
            super(dialect, spec);
        }

        @Override
        Delete onAsDelete() {
            return this;
        }


    }//StandardSimpleDelete

    private static final class StandardBatchDelete extends StandardDeleteStatement<_BatchDeleteParamSpec>
            implements BatchDelete, _BatchStatement, _BatchDeleteParamSpec {

        private List<?> paramList;

        private StandardBatchDelete(StandardDialect dialect, @Nullable ArmyStmtSpec spec) {
            super(dialect, spec);
        }

        @Override
        public BatchDelete namedParamList(final List<?> paramList) {
            if (this.paramList != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            this.paramList = CriteriaUtils.paramList(paramList);
            return this;
        }

        @Override
        public List<?> paramList() {
            final List<?> list = this.paramList;
            if (list == null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            return list;
        }

        @Override
        _BatchDeleteParamSpec onAsDelete() {
            return this;
        }


    }//StandardBatchDelete


    private static abstract class DomainDeleteStatement<I extends Item> extends StandardDeletes<
            I,
            _DomainDeleteClause<I>,
            _WhereSpec<I>>
            implements _DomainDeleteClause<I>,
            _WhereSpec<I>,
            _WhereAndSpec<I>,
            _DomainDelete {

        private DomainDeleteStatement() {
            super(StandardDialect.STANDARD10, null);
        }


        @Override
        public final _WhereSpec<I> deleteFrom(TableMeta<?> table, SQLs.WordAs as, String tableAlias) {
            return this.deleteFrom(table, tableAlias);
        }


    }//DomainDeleteStatement

    private static final class DomainSimpleDelete extends DomainDeleteStatement<Delete>
            implements Delete {

        private DomainSimpleDelete() {
        }

        @Override
        Delete onAsDelete() {
            return this;
        }


    }//DomainSimpleDelete


    private static final class DomainBatchDelete extends DomainDeleteStatement<_BatchDeleteParamSpec>
            implements BatchDelete, _BatchStatement, _BatchDeleteParamSpec {

        private List<?> paramList;

        private DomainBatchDelete() {
        }

        @Override
        public BatchDelete namedParamList(final List<?> paramList) {
            if (this.paramList != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            this.paramList = CriteriaUtils.paramList(paramList);
            return this;
        }

        @Override
        public List<?> paramList() {
            final List<?> list = this.paramList;
            if (list == null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            return list;
        }

        @Override
        _BatchDeleteParamSpec onAsDelete() {
            return this;
        }

    }//DomainBatchDelete


}
