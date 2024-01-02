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
import io.army.criteria.impl.inner.*;
import io.army.criteria.standard.StandardCtes;
import io.army.criteria.standard.StandardQuery;
import io.army.criteria.standard.StandardUpdate;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.meta.*;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * <p>
 * This class hold the implementations of standard update statement.
 *
 * @since 0.6.0
 */

abstract class StandardUpdates<I extends Item, F extends TableField, SR, WR, WA>
        extends SingleUpdateStatement<I, F, SR, WR, WA, Object, Object, Object, Object, Object>
        implements StandardUpdate, UpdateStatement, _Statement._WithClauseSpec {

    static _WithSpec<Update> singleUpdate(StandardDialect dialect) {
        return new StandardSimpleUpdateClause(dialect);
    }

    static _WithSpec<_BatchUpdateParamSpec> batchSingleUpdate(StandardDialect dialect) {
        return new StandardBatchUpdateClause(dialect);
    }

    static _DomainUpdateClause<Update> simpleDomain() {
        return new DomainSimpleUpdateClaus();
    }

    static _DomainUpdateClause<_BatchUpdateParamSpec> batchDomain() {
        return new DomainBatchUpdateClaus();
    }


    private final boolean recursive;

    private final List<_Cte> cteList;


    private StandardUpdates(UpdateClause<?> clause, TableMeta<?> updateTable, String tableAlias) {
        super(clause.context, updateTable, tableAlias);
        this.recursive = clause.isRecursive();
        this.cteList = clause.cteList();
    }

    @Override
    public final boolean isRecursive() {
        return this.recursive;
    }

    @Override
    public final List<_Cte> cteList() {
        return this.cteList;
    }

    @Override
    final Dialect statementDialect() {
        return MySQLDialect.MySQL57;
    }


    private static abstract class SimpleSingleUpdate<I extends Item, F extends TableField>
            extends StandardUpdates<
            I,
            F,
            _WhereSpec<I, F>,
            _DmlUpdateSpec<I>,
            _WhereAndSpec<I>>
            implements _WhereSpec<I, F>, _WhereAndSpec<I> {


        private SimpleSingleUpdate(UpdateClause<?> clause, TableMeta<?> table, String tableAlias) {
            super(clause, table, tableAlias);
        }

        @Override
        public final _StandardWhereClause<I> sets(Consumer<_BatchItemPairs<F>> consumer) {
            consumer.accept(CriteriaSupports.batchItemPairs(this::onAddItemPair));
            return this;
        }


    }//SimpleSingleUpdate

    private static final class StandardSimpleUpdate<F extends TableField> extends SimpleSingleUpdate<Update, F>
            implements Update, StandardUpdate {

        private StandardSimpleUpdate(StandardSimpleUpdateClause clause, TableMeta<?> table, String tableAlias) {
            super(clause, table, tableAlias);
        }


        @Override
        Update onAsUpdate() {
            return this;
        }


    }//StandardSimpleUpdate


    private static final class StandardBatchUpdate<F extends TableField>
            extends SimpleSingleUpdate<_BatchUpdateParamSpec, F>
            implements BatchUpdate, StandardUpdate, _BatchStatement, _BatchUpdateParamSpec {

        private List<?> paramList;

        private StandardBatchUpdate(StandardBatchUpdateClause clause, TableMeta<?> table, String tableAlias) {
            super(clause, table, tableAlias);
        }

        @Override
        public BatchUpdate namedParamList(final List<?> paramList) {
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
        _BatchUpdateParamSpec onAsUpdate() {
            return this;
        }


    }// StandardBatchUpdate


    private static abstract class DomainUpdateStatement<I extends Item, F extends TableField>
            extends SimpleSingleUpdate<I, F>
            implements _DomainUpdate {

        private List<_ItemPair> childItemPairList;

        private DomainUpdateStatement(DomainUpdateClause<?> clause, TableMeta<?> table, String tableAlias) {
            super(clause, table, tableAlias);
        }

        @Override
        I onAsUpdate() {
            this.childItemPairList = _Collections.safeUnmodifiableList(this.childItemPairList);
            return this.onAsDomainUpdate();
        }

        abstract I onAsDomainUpdate();


        @Override
        final void onClear() {
            this.childItemPairList = null;
        }

        @Override
        final void onAddChildItemPair(final SQLs.ArmyItemPair pair) {
            List<_ItemPair> childItemPairList = this.childItemPairList;
            if (childItemPairList == null) {
                this.childItemPairList = childItemPairList = _Collections.arrayList();
            } else if (!(childItemPairList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            childItemPairList.add(pair);
        }

        @Override
        final boolean isNoChildItemPair() {
            final List<_ItemPair> childItemPairList = this.childItemPairList;
            return childItemPairList == null || childItemPairList.size() == 0;
        }


        @Override
        public final List<_ItemPair> childItemPairList() {
            final List<_ItemPair> childItemPairList = this.childItemPairList;
            if (childItemPairList == null || childItemPairList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return childItemPairList;
        }


    }//DomainUpdateStatement

    private static final class DomainSimpleUpdate<F extends TableField> extends DomainUpdateStatement<Update, F>
            implements Update {

        private DomainSimpleUpdate(DomainSimpleUpdateClaus clause, TableMeta<?> table, String tableAlias) {
            super(clause, table, tableAlias);
        }

        @Override
        Update onAsDomainUpdate() {
            return this;
        }


    }//DomainSimpleUpdate

    private static final class DomainBatchUpdate<F extends TableField>
            extends DomainUpdateStatement<_BatchUpdateParamSpec, F>
            implements BatchUpdate, _BatchStatement, _BatchUpdateParamSpec {

        private List<?> paramList;

        private DomainBatchUpdate(DomainBatchUpdateClaus clause, TableMeta<?> table, String tableAlias) {
            super(clause, table, tableAlias);
        }

        @Override
        public BatchUpdate namedParamList(final List<?> paramList) {
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
        _BatchUpdateParamSpec onAsDomainUpdate() {
            return this;
        }

    }//DomainBatchUpdate


    private static abstract class UpdateClause<WE extends Item>
            extends CriteriaSupports.WithClause<StandardCtes, WE> {

        private UpdateClause(@Nullable _WithClauseSpec spec, CriteriaContext context) {
            super(spec, context);
        }

        @Override
        final StandardCtes createCteBuilder(boolean recursive) {
            return StandardQueries.cteBuilder(recursive, this.context);
        }

    }//UpdateClause


    private static abstract class StandardUpdateClause<I extends Item>
            extends UpdateClause<_SingleUpdateClause<I>>
            implements _SingleUpdateClause<I>,
            StandardUpdate._WithSpec<I> {


        private StandardUpdateClause(StandardDialect dialect, @Nullable ArmyStmtSpec spec) {
            super(spec, CriteriaContexts.primarySingleDmlContext(dialect, spec));
            ContextStack.push(this.context);
        }

        @Override
        public final StandardQuery._StaticCteParensSpec<_SingleUpdateClause<I>> with(String name) {
            return StandardQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public final StandardQuery._StaticCteParensSpec<_SingleUpdateClause<I>> withRecursive(String name) {
            return StandardQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public final <T> _StandardSetClause<I, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as,
                                                                    String tableAlias) {
            return this.createUpdateStmt(table, tableAlias);
        }

        @Override
        public final <P> _StandardSetClause<I, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, SQLs.WordAs as,
                                                                    String tableAlias) {
            return this.createUpdateStmt(table, tableAlias);
        }

        abstract <T> _StandardSetClause<I, FieldMeta<T>> createUpdateStmt(TableMeta<?> updateTable, String tableAlias);


    }//SingleUpdateClause

    private static final class StandardSimpleUpdateClause extends StandardUpdateClause<Update> {

        private StandardSimpleUpdateClause(StandardDialect dialect) {
            super(dialect, null);
        }

        @Override
        <T> _StandardSetClause<Update, FieldMeta<T>> createUpdateStmt(TableMeta<?> updateTable, String tableAlias) {
            return new StandardSimpleUpdate<>(this, updateTable, tableAlias);
        }


    }//StandardSimpleUpdateClause

    private static final class StandardBatchUpdateClause extends StandardUpdateClause<_BatchUpdateParamSpec> {

        private StandardBatchUpdateClause(StandardDialect dialect) {
            super(dialect, null);
        }

        @Override
        <T> _StandardSetClause<_BatchUpdateParamSpec, FieldMeta<T>> createUpdateStmt(TableMeta<?> updateTable,
                                                                                     String tableAlias) {
            return new StandardBatchUpdate<>(this, updateTable, tableAlias);
        }


    }//StandardBatchUpdateClause


    /**
     * domain api don't support WITH clause.
     */
    private static abstract class DomainUpdateClause<I extends Item> extends UpdateClause<_DomainUpdateClause<I>>
            implements _DomainUpdateClause<I> {

        private DomainUpdateClause() {
            super(null, CriteriaContexts.primarySingleDmlContext(StandardDialect.STANDARD10, null));
            ContextStack.push(this.context);
        }


    }// DomainUpdateClause

    private static final class DomainSimpleUpdateClaus extends DomainUpdateClause<Update> {

        private DomainSimpleUpdateClaus() {
        }

        @Override
        public _StandardSetClause<Update, FieldMeta<?>> update(TableMeta<?> table, String tableAlias) {
            return new DomainSimpleUpdate<>(this, table, tableAlias);
        }

        @Override
        public <T> _StandardSetClause<Update, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as,
                                                                   String tableAlias) {
            return new DomainSimpleUpdate<>(this, table, tableAlias);
        }

        @Override
        public <T> _StandardSetClause<Update, FieldMeta<? super T>> update(ChildTableMeta<T> table, SQLs.WordAs as,
                                                                           String tableAlias) {
            return new DomainSimpleUpdate<>(this, table, tableAlias);
        }


    }//DomainSimpleUpdateClaus

    private static final class DomainBatchUpdateClaus
            extends DomainUpdateClause<_BatchUpdateParamSpec> {

        private DomainBatchUpdateClaus() {
        }

        @Override
        public _StandardSetClause<_BatchUpdateParamSpec, FieldMeta<?>> update(TableMeta<?> table, String tableAlias) {
            return new DomainBatchUpdate<>(this, table, tableAlias);
        }

        @Override
        public <T> _StandardSetClause<_BatchUpdateParamSpec, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as,
                                                                                  String tableAlias) {
            return new DomainBatchUpdate<>(this, table, tableAlias);
        }

        @Override
        public <T> _StandardSetClause<_BatchUpdateParamSpec, FieldMeta<? super T>> update(ChildTableMeta<T> table, SQLs.WordAs as,
                                                                                          String tableAlias) {
            return new DomainBatchUpdate<>(this, table, tableAlias);
        }

    }// DomainBatchUpdateClaus


}

