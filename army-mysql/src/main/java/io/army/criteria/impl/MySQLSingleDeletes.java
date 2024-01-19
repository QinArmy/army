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
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._BatchStatement;
import io.army.criteria.impl.inner.mysql._MySQLSingleDelete;
import io.army.criteria.mysql.MySQLCtes;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLQuery;
import io.army.dialect.Dialect;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 * This class hold the implementations of MySQL single-table DELETE syntax
 * <p>
 * This class is base class of below:
 * <ul>
 *     <li>{@link MySQLSimpleDelete}</li>
 *     <li>{@link MySQLBatchDelete}</li>
 * </ul>
 */
abstract class MySQLSingleDeletes<I extends Item>
        extends SingleDeleteStatement<
        I,
        MySQLCtes,
        MySQLDelete._SimpleSingleDeleteClause<I>,
        MySQLDelete._OrderBySpec<I>,
        MySQLDelete._SingleWhereAndSpec<I>,
        MySQLDelete._OrderByCommaSpec<I>,
        MySQLDelete._LimitSpec<I>,
        Statement._DmlDeleteSpec<I>,
        Object, Object>
        implements MySQLDelete,
        _MySQLSingleDelete,
        MySQLDelete._SingleWithSpec<I>,
        DeleteStatement._SingleDeleteFromClause<MySQLDelete._SinglePartitionSpec<I>>,
        MySQLDelete._SinglePartitionSpec<I>,
        MySQLDelete._SingleWhereAndSpec<I>,
        MySQLDelete._OrderByCommaSpec<I> {


    static _SingleWithSpec<Delete> simple() {
        return new MySQLSimpleDelete();
    }

    static _SingleWithSpec<_BatchDeleteParamSpec> batch() {
        return new MySQLBatchDelete();
    }

    private List<Hint> hintList;

    private List<MySQLs.Modifier> modifierList;

    private SingleTableMeta<?> deleteTable;

    private String tableAlias;

    private List<String> partitionList;


    private MySQLSingleDeletes(@Nullable ArmyStmtSpec spec) {
        super(spec, CriteriaContexts.primarySingleDmlContext(MySQLUtils.DIALECT, spec));
    }

    @Override
    public final MySQLQuery._StaticCteParensSpec<_SimpleSingleDeleteClause<I>> with(String name) {
        return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final MySQLQuery._StaticCteParensSpec<_SimpleSingleDeleteClause<I>> withRecursive(String name) {
        return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final _SinglePartitionSpec<I> deleteFrom(@Nullable SingleTableMeta<?> table, SQLs.WordAs wordAs,
                                                    @Nullable String alias) {
        if (table == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (!_StringUtils.hasText(alias)) {
            throw ContextStack.criteriaError(this.context, _Exceptions::tableAliasIsEmpty);
        } else if (this.deleteTable != null) {
            throw ContextStack.criteriaError(this.context, _Exceptions::castCriteriaApi);
        }
        this.deleteTable = table;
        this.tableAlias = alias;
        return this;
    }


    @Override
    public final DeleteStatement._SingleDeleteFromClause<_SinglePartitionSpec<I>> delete(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers) {
        this.hintList = MySQLUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.context, modifiers, MySQLUtils::deleteModifier);
        return this;
    }

    @Override
    public final _SinglePartitionSpec<I> from(SingleTableMeta<?> table, SQLs.WordAs wordAs, String alias) {
        return this.deleteFrom(table, wordAs, alias);
    }

    @Override
    public final _SingleWhereClause<I> partition(String first, String... rest) {
        this.partitionList = ArrayUtils.unmodifiableListOf(first, rest);
        return this;
    }

    @Override
    public final _SingleWhereClause<I> partition(Consumer<Consumer<String>> consumer) {
        this.partitionList = CriteriaUtils.stringList(this.context, true, consumer);
        return this;
    }

    @Override
    public final _SingleWhereClause<I> ifPartition(Consumer<Consumer<String>> consumer) {
        this.partitionList = CriteriaUtils.stringList(this.context, false, consumer);
        return this;
    }

    @Override
    public final List<Hint> hintList() {
        final List<Hint> list = this.hintList;
        if (list == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return list;
    }

    @Override
    public final List<MySQLs.Modifier> modifierList() {
        final List<MySQLs.Modifier> list = this.modifierList;
        if (list == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return list;
    }

    @Override
    public final TableMeta<?> table() {
        final SingleTableMeta<?> deleteTable = this.deleteTable;
        if (deleteTable == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return deleteTable;
    }

    @Override
    public final String tableAlias() {
        final String alias = this.tableAlias;
        if (alias == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return alias;
    }


    @Override
    public final List<String> partitionList() {
        final List<String> list = this.partitionList;
        if (list == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return list;
    }


    @Override
    final I onAsDelete() {
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }
        if (this.partitionList == null) {
            this.partitionList = Collections.emptyList();
        }
        if (this.deleteTable == null || this.tableAlias == null) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return this.onAsMySQLDelete();
    }


    abstract I onAsMySQLDelete();

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierList = null;
        this.partitionList = null;
    }

    @Override
    final MySQLCtes createCteBuilder(boolean recursive) {
        return MySQLSupports.mysqlLCteBuilder(recursive, this.context);
    }

    @Override
    final Dialect statementDialect() {
        return MySQLUtils.DIALECT;
    }


    private static final class MySQLSimpleDelete extends MySQLSingleDeletes<Delete>
            implements Delete {

        private MySQLSimpleDelete() {
            super(null);
        }

        @Override
        Delete onAsMySQLDelete() {
            return this;
        }


    }//MySQLSimpleDelete

    private static final class MySQLBatchDelete extends MySQLSingleDeletes<_BatchDeleteParamSpec>
            implements BatchDelete, _BatchStatement, _BatchDeleteParamSpec {

        private List<?> paramList;

        private MySQLBatchDelete() {
            super(null);
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
        _BatchDeleteParamSpec onAsMySQLDelete() {
            return this;
        }


    }//MySQLBatchDelete


}
