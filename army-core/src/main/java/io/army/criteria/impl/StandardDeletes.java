/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchStatement;
import io.army.criteria.impl.inner._DomainDelete;
import io.army.criteria.standard.StandardCtes;
import io.army.criteria.standard.StandardDelete;
import io.army.criteria.standard.StandardQuery;
import io.army.dialect.Dialect;
import io.army.dialect.MySQLDialect;
import io.army.meta.ChildTableMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import io.army.lang.Nullable;
import java.util.List;

/**
 * <p>This class representing standard domain delete statement.
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
            throw ContextStack.clearStackAndCastCriteriaApi();
        } else if (table == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (tableAlias == null) {
            throw ContextStack.nullPointer(this.context);
        }
        this.deleteTable = table;
        this.tableAlias = tableAlias;
        this.context.singleDmlTable(table, tableAlias);
        return (DR) this;
    }

    @Override
    public final TableMeta<?> table() {
        final TableMeta<?> deleteTable = this.deleteTable;
        if (deleteTable == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return deleteTable;
    }

    @Override
    public final String tableAlias() {
        final String tableAlias = this.tableAlias;
        if (tableAlias == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
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
        public final _WhereSpec<I> deleteFrom(final SingleTableMeta<?> table, SQLs.WordAs as, final String tableAlias) {
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


        @Override
        public final boolean isChildDml() {
            return ((StandardDeletes<?, ?, ?>) this).deleteTable instanceof ChildTableMeta;
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
