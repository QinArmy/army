package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
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
import java.util.function.Supplier;

/**
 * <p>
 * This class hold the implementations of standard update statement.
 * </p>
 *
 * @since 1.0
 */

abstract class StandardUpdates<I extends Item, F extends TableField, SR, WR, WA>
        extends SingleUpdateStatement<I, F, SR, WR, WA, Object, Object, Object, Object, Object>
        implements StandardUpdate, UpdateStatement, _Statement._WithClauseSpec {

    static _WithSpec<Update> singleUpdate(StandardDialect dialect) {
        return new SimpleUpdateClause<>(dialect, null, SQLs::identity);
    }

    static _DomainUpdateClause simpleDomain() {
        return new SimpleDomainUpdateClause();
    }


    static _BatchWithSpec batchSingle(StandardDialect dialect) {
        return new BatchSingleUpdateClause(dialect);
    }

    static _BatchDomainUpdateClause batchDomain() {
        return new BatchDomainUpdateClause();
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

    private static abstract class SimpleSingleUpdate<I extends Item, F extends TableField> extends StandardUpdates<
            I,
            F,
            _WhereSpec<I, F>,
            _DmlUpdateSpec<I>,
            _WhereAndSpec<I>>
            implements _WhereSpec<I, F>, _WhereAndSpec<I>, Update {


        private SimpleSingleUpdate(UpdateClause<?> clause, TableMeta<?> table, String tableAlias) {
            super(clause, table, tableAlias);
        }

        @Override
        public final _StandardWhereClause<I> sets(Consumer<_ItemPairs<F>> consumer) {
            consumer.accept(CriteriaSupports.itemPairs(this::onAddItemPair));
            return this;
        }


    }//StandardSingleUpdate

    private static final class PrimarySimpleSingleUpdate<I extends Item, F extends TableField>
            extends SimpleSingleUpdate<I, F> {

        private final Function<? super Update, I> function;

        private PrimarySimpleSingleUpdate(SimpleUpdateClause<I> clause, TableMeta<?> table, String tableAlias) {
            super(clause, table, tableAlias);
            this.function = clause.function;
        }

        @Override
        I onAsUpdate() {
            return this.function.apply(this);
        }


    }//PrimarySimpleSingleUpdate


    private static final class SimpleDomainUpdate<F extends TableField> extends SimpleSingleUpdate<Update, F>
            implements _DomainUpdate {

        private List<_ItemPair> childItemPairList;

        private SimpleDomainUpdate(UpdateClause<?> clause, TableMeta<?> table, String tableAlias) {
            super(clause, table, tableAlias);
        }

        @Override
        Update onAsUpdate() {
            this.childItemPairList = _Collections.safeUnmodifiableList(this.childItemPairList);
            return this;
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


    /**
     * <p>
     * This class is standard batch update implementation.
     * </p>
     *
     * @since 1.0
     */
    private static class BatchSingleUpdate<F extends TableField> extends StandardUpdates<
            BatchUpdate,
            F,
            _BatchWhereSpec<BatchUpdate, F>,
            _BatchParamClause<_DmlUpdateSpec<BatchUpdate>>,
            _BatchWhereAndSpec<BatchUpdate>>
            implements _BatchWhereSpec<BatchUpdate, F>,
            _BatchWhereAndSpec<BatchUpdate>,
            BatchUpdate,
            _BatchDml {

        @Override
        public _BatchWhereClause<BatchUpdate> sets(Consumer<_BatchItemPairs<F>> consumer) {
            consumer.accept(CriteriaSupports.batchItemPairs(this::onAddItemPair));
            return this;
        }

        private List<?> paramList;

        private BatchSingleUpdate(UpdateClause<?> clause, TableMeta<?> updateTable, String tableAlias) {
            super(clause, updateTable, tableAlias);
        }

        @Override
        public final <P> _DmlUpdateSpec<BatchUpdate> namedParamList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public final <P> _DmlUpdateSpec<BatchUpdate> namedParamList(Supplier<List<P>> supplier) {
            return this.namedParamList(supplier.get());
        }

        @Override
        public final _DmlUpdateSpec<BatchUpdate> namedParamList(Function<String, ?> function, String keyName) {
            this.paramList = CriteriaUtils.paramList(this.context, (List<?>) function.apply(keyName));
            return this;
        }

        @Override
        public final List<?> paramList() {
            final List<?> list = this.paramList;
            if (list == null || list instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }

        @Override
        final BatchUpdate onAsUpdate() {
            if (this instanceof BatchDomainUpdate) {
                ((BatchDomainUpdate<?>) this).childItemPairList = _Collections.safeUnmodifiableList(((BatchDomainUpdate<?>) this).childItemPairList);
            }
            return this;
        }

        @Override
        final void onClear() {
            if (this instanceof BatchDomainUpdate) {
                ((BatchDomainUpdate<?>) this).childItemPairList = null;
            }
        }

    }//BatchUpdate


    private static final class BatchDomainUpdate<F extends TableField> extends BatchSingleUpdate<F>
            implements _DomainUpdate {

        private List<_ItemPair> childItemPairList;

        private BatchDomainUpdate(BatchDomainUpdateClause clause, TableMeta<?> updateTable, String tableAlias) {
            super(clause, updateTable, tableAlias);
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
        public List<_ItemPair> childItemPairList() {
            final List<_ItemPair> childItemPairList = this.childItemPairList;
            if (childItemPairList == null || childItemPairList instanceof ArrayList) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return childItemPairList;
        }


    }//BatchDomainUpdate


    private static abstract class UpdateClause<WE extends Item> extends CriteriaSupports.WithClause<StandardCtes, WE> {

        private UpdateClause(@Nullable _WithClauseSpec spec, CriteriaContext context) {
            super(spec, context);
        }

        @Override
        final StandardCtes createCteBuilder(boolean recursive) {
            return StandardQueries.cteBuilder(recursive, this.context);
        }

    }//UpdateClause


    private static final class SimpleUpdateClause<I extends Item>
            extends UpdateClause<_SingleUpdateClause<I>>
            implements _SingleUpdateClause<I>,
            StandardUpdate._WithSpec<I> {


        private final Function<? super Update, I> function;

        private SimpleUpdateClause(StandardDialect dialect, @Nullable ArmyStmtSpec spec,
                                   Function<? super Update, I> function) {
            super(spec, CriteriaContexts.primarySingleDmlContext(dialect, spec));
            ContextStack.push(this.context);
            this.function = function;
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
    private static final class SimpleDomainUpdateClause extends UpdateClause<_DomainUpdateClause>
            implements _DomainUpdateClause {

        private SimpleDomainUpdateClause() {
            super(null, CriteriaContexts.primarySingleDmlContext(StandardDialect.STANDARD10, null));
            ContextStack.push(this.context);
        }

        @Override
        public _StandardSetClause<Update, FieldMeta<?>> update(TableMeta<?> table, String tableAlias) {
            return new SimpleDomainUpdate<>(this, table, tableAlias);
        }

        @Override
        public <T> _StandardSetClause<Update, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as,
                                                                   String tableAlias) {
            return new SimpleDomainUpdate<>(this, table, tableAlias);
        }

        @Override
        public <T> _StandardSetClause<Update, FieldMeta<? super T>> update(ChildTableMeta<T> table, SQLs.WordAs as,
                                                                           String tableAlias) {
            return new SimpleDomainUpdate<>(this, table, tableAlias);
        }
    }// SimpleDomainUpdateClause


    private static final class BatchSingleUpdateClause extends UpdateClause<_BatchSingleUpdateClause>
            implements _BatchSingleUpdateClause,
            StandardUpdate._BatchWithSpec {

        private BatchSingleUpdateClause(StandardDialect dialect) {
            super(null, CriteriaContexts.primarySingleDmlContext(dialect, null));
            ContextStack.push(this.context);
        }

        @Override
        public StandardQuery._StaticCteParensSpec<_BatchSingleUpdateClause> with(String name) {
            return StandardQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public StandardQuery._StaticCteParensSpec<_BatchSingleUpdateClause> withRecursive(String name) {
            return StandardQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public <T> _BatchSetClause<BatchUpdate, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as,
                                                                     String tableAlias) {
            return new BatchSingleUpdate<>(this, table, tableAlias);
        }

        @Override
        public <P> _BatchSetClause<BatchUpdate, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, SQLs.WordAs as,
                                                                     String tableAlias) {
            return new BatchSingleUpdate<>(this, table, tableAlias);
        }


    }//BatchSingleUpdateClause


    /**
     * domain api don't support WITH clause.
     */
    private static final class BatchDomainUpdateClause extends UpdateClause<_BatchDomainUpdateClause>
            implements _BatchDomainUpdateClause {


        private BatchDomainUpdateClause() {
            super(null, CriteriaContexts.primarySingleDmlContext(StandardDialect.STANDARD10, null));
            ContextStack.push(this.context);
        }


        @Override
        public _BatchSetClause<BatchUpdate, FieldMeta<?>> update(TableMeta<?> table, String tableAlias) {
            return new BatchDomainUpdate<>(this, table, tableAlias);
        }

        @Override
        public <T> _BatchSetClause<BatchUpdate, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as,
                                                                     String tableAlias) {
            return new BatchDomainUpdate<>(this, table, tableAlias);
        }

        @Override
        public <T> _BatchSetClause<BatchUpdate, FieldMeta<? super T>> update(ChildTableMeta<T> table, SQLs.WordAs as,
                                                                             String tableAlias) {
            return new BatchDomainUpdate<>(this, table, tableAlias);
        }


    }//BatchDomainUpdateClause


}

