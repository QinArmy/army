package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._DomainUpdate;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner._Statement;
import io.army.criteria.standard.StandardCtes;
import io.army.criteria.standard.StandardQuery;
import io.army.criteria.standard.StandardUpdate;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.util._Collections;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This class hold the implementations of standard update statement.
 * </p>
 *
 * @since 1.0
 */

abstract class StandardUpdates<I extends Item, BI extends Item, F extends TableField, SR, WR, WA>
        extends SingleUpdateStatement<I, BI, F, SR, WR, WA, Object, Object, Object, Object, Object>
        implements StandardUpdate, UpdateStatement, _Statement._WithClauseSpec {

    static _WithSpec<Update> singleUpdate(StandardDialect dialect) {
        return new SimpleUpdateClause<>(dialect, null, SQLs.SIMPLE_UPDATE, SQLs.ERROR_FUNC);
    }

    static _WithSpec<_BatchParamClause<BatchUpdate>> batchSingleUpdate(StandardDialect dialect) {
        return new SimpleUpdateClause<>(dialect, null, SQLs::forBatchUpdate, SQLs.BATCH_UPDATE);
    }

    static _DomainUpdateClause<Update> simpleDomain() {
        return new SimpleDomainUpdateClause<>(SQLs.SIMPLE_UPDATE, SQLs.ERROR_FUNC);
    }

    static _DomainUpdateClause<_BatchParamClause<BatchUpdate>> batchDomain() {
        return new SimpleDomainUpdateClause<>(SQLs::forBatchUpdate, SQLs.BATCH_UPDATE);
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


    private static abstract class SimpleSingleUpdate<I extends Item, BI extends Item, F extends TableField>
            extends StandardUpdates<
            I,
            BI,
            F,
            _WhereSpec<I, F>,
            _DmlUpdateSpec<I>,
            _WhereAndSpec<I>>
            implements _WhereSpec<I, F>, _WhereAndSpec<I>, BatchUpdateSpec<BI> {


        private SimpleSingleUpdate(UpdateClause<?> clause, TableMeta<?> table, String tableAlias) {
            super(clause, table, tableAlias);
        }

        @Override
        public final _StandardWhereClause<I> sets(Consumer<_BatchItemPairs<F>> consumer) {
            consumer.accept(CriteriaSupports.batchItemPairs(this::onAddItemPair));
            return this;
        }


    }//StandardSingleUpdate

    private static final class PrimarySimpleSingleUpdate<I extends Item, BI extends Item, F extends TableField>
            extends SimpleSingleUpdate<I, BI, F> {

        private final Function<? super BatchUpdateSpec<BI>, I> function;

        private final Function<? super BatchUpdate, BI> batchFunc;

        private PrimarySimpleSingleUpdate(SimpleUpdateClause<I, BI> clause, TableMeta<?> table, String tableAlias) {
            super(clause, table, tableAlias);
            this.function = clause.function;
            this.batchFunc = clause.batchFunc;
        }

        @Override
        I onAsUpdate() {
            return this.function.apply(this);
        }

        @Override
        BI onAsBatchUpdate(List<?> paramList) {
            return this.batchFunc.apply(new StandardBatchStatement(this, paramList));
        }


    }//PrimarySimpleSingleUpdate


    private static final class SimpleDomainUpdate<I extends Item, BI extends Item, F extends TableField>
            extends SimpleSingleUpdate<I, BI, F>
            implements _DomainUpdate {

        private final Function<? super BatchUpdateSpec<BI>, I> function;

        private final Function<? super BatchUpdate, BI> batchFunc;

        private List<_ItemPair> childItemPairList;

        private SimpleDomainUpdate(SimpleDomainUpdateClause<I, BI> clause, TableMeta<?> table, String tableAlias) {
            super(clause, table, tableAlias);
            this.function = clause.function;
            this.batchFunc = clause.batchFunc;
        }

        @Override
        I onAsUpdate() {
            this.childItemPairList = _Collections.safeUnmodifiableList(this.childItemPairList);
            return this.function.apply(this);
        }

        @Override
        BI onAsBatchUpdate(final List<?> paramList) {
            return this.batchFunc.apply(new DomainBatchStatement(this, paramList));
        }

        @Override
        void onClear() {
            this.childItemPairList = null;
        }

        @Override
        void onAddChildItemPair(final SQLs.ArmyItemPair pair) {
            List<_ItemPair> childItemPairList = this.childItemPairList;
            if (childItemPairList == null) {
                this.childItemPairList = childItemPairList = _Collections.arrayList();
            } else if (!(childItemPairList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            childItemPairList.add(pair);
        }

        @Override
        boolean isNoChildItemPair() {
            final List<_ItemPair> childItemPairList = this.childItemPairList;
            return childItemPairList == null || childItemPairList.size() == 0;
        }


        @Override
        public List<_ItemPair> childItemPairList() {
            final List<_ItemPair> childItemPairList = this.childItemPairList;
            if (childItemPairList == null || childItemPairList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return childItemPairList;
        }


    }//SimpleDomainUpdate


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


    private static final class SimpleUpdateClause<I extends Item, BI extends Item>
            extends UpdateClause<_SingleUpdateClause<I>>
            implements _SingleUpdateClause<I>,
            StandardUpdate._WithSpec<I> {


        private final Function<? super BatchUpdateSpec<BI>, I> function;

        private final Function<? super BatchUpdate, BI> batchFunc;

        private SimpleUpdateClause(StandardDialect dialect, @Nullable ArmyStmtSpec spec,
                                   Function<? super BatchUpdateSpec<BI>, I> function,
                                   Function<? super BatchUpdate, BI> batchFunc) {
            super(spec, CriteriaContexts.primarySingleDmlContext(dialect, spec));
            ContextStack.push(this.context);
            this.function = function;
            this.batchFunc = batchFunc;
        }

        @Override
        public StandardQuery._StaticCteParensSpec<_SingleUpdateClause<I>> with(String name) {
            return StandardQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public StandardQuery._StaticCteParensSpec<_SingleUpdateClause<I>> withRecursive(String name) {
            return StandardQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public <T> _StandardSetClause<I, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as,
                                                              String tableAlias) {
            return new PrimarySimpleSingleUpdate<>(this, table, tableAlias);
        }

        @Override
        public <P> _StandardSetClause<I, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, SQLs.WordAs as,
                                                              String tableAlias) {
            return new PrimarySimpleSingleUpdate<>(this, table, tableAlias);
        }


    }//PrimarySimpleSingleUpdateClause


    /**
     * domain api don't support WITH clause.
     */
    private static final class SimpleDomainUpdateClause<I extends Item, BI extends Item>
            extends UpdateClause<_DomainUpdateClause<I>>
            implements _DomainUpdateClause<I> {

        private final Function<? super BatchUpdateSpec<BI>, I> function;

        private final Function<? super BatchUpdate, BI> batchFunc;

        private SimpleDomainUpdateClause(Function<? super BatchUpdateSpec<BI>, I> function,
                                         Function<? super BatchUpdate, BI> batchFunc) {
            super(null, CriteriaContexts.primarySingleDmlContext(StandardDialect.STANDARD10, null));
            ContextStack.push(this.context);
            this.function = function;
            this.batchFunc = batchFunc;
        }

        @Override
        public _StandardSetClause<I, FieldMeta<?>> update(TableMeta<?> table, String tableAlias) {
            return new SimpleDomainUpdate<>(this, table, tableAlias);
        }

        @Override
        public <T> _StandardSetClause<I, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as,
                                                              String tableAlias) {
            return new SimpleDomainUpdate<>(this, table, tableAlias);
        }

        @Override
        public <T> _StandardSetClause<I, FieldMeta<? super T>> update(ChildTableMeta<T> table, SQLs.WordAs as,
                                                                      String tableAlias) {
            return new SimpleDomainUpdate<>(this, table, tableAlias);
        }

    }// SimpleDomainUpdateClause


    private static final class StandardBatchStatement extends ArmySingleBathUpdate
            implements StandardUpdate, BatchUpdate {

        private StandardBatchStatement(SimpleSingleUpdate<?, ?, ?> statement, List<?> paramList) {
            super(statement, paramList);
        }

        @Override
        Dialect statementDialect() {
            return MySQLDialect.MySQL57;
        }


    }//StandardBatchStatement

    private static final class DomainBatchStatement extends ArmySingleBathUpdate
            implements _DomainUpdate, BatchUpdate {

        private final List<_ItemPair> childItemPairList;

        private DomainBatchStatement(SimpleDomainUpdate<?, ?, ?> statement, List<?> paramList) {
            super(statement, paramList);
            this.childItemPairList = statement.childItemPairList;
            assert this.childItemPairList != null;
        }

        @Override
        public List<_ItemPair> childItemPairList() {
            return this.childItemPairList;
        }

        @Override
        Dialect statementDialect() {
            return MySQLDialect.MySQL57;
        }


    }//StandardBatchStatement


}

