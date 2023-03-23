package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._DomainUpdate;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.standard.StandardUpdate;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
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
        extends SingleUpdateStatement<I, F, SR, WR, WA, Object, Object, Object, Object>
        implements StandardUpdate, UpdateStatement {

    static _SingleUpdateClause<Update> singleUpdate() {
        return new PrimarySimpleSingleUpdateClause<>(null, SQLs::_identity);
    }

    static _DomainUpdateClause simpleDomain() {
        return new SimpleDomainUpdateClause();
    }


    static _BatchSingleUpdateClause batchSingle() {
        return new BatchSingleUpdateClause();
    }

    static _BatchDomainUpdateClause batchDomain() {
        return new BatchDomainUpdateClause();
    }


    private StandardUpdates(CriteriaContext context, TableMeta<?> updateTable, String tableAlias) {
        super(context, updateTable, tableAlias);
    }


    @Override
    final I onAsUpdate() {
        if (this instanceof SimpleDomainUpdate) {
            final List<_ItemPair> childItemList = ((SimpleDomainUpdate<F>) this).childItemPairList;
            if (childItemList == null) {
                ((SimpleDomainUpdate<F>) this).childItemPairList = Collections.emptyList();
            } else {
                ((SimpleDomainUpdate<F>) this).childItemPairList = _CollectionUtils.unmodifiableList(childItemList);
            }
        } else if (this instanceof BatchDomainUpdate) {
            final List<_ItemPair> childItemList = ((BatchDomainUpdate<F>) this).childItemPairList;
            if (childItemList == null) {
                ((BatchDomainUpdate<F>) this).childItemPairList = Collections.emptyList();
            } else {
                ((BatchDomainUpdate<F>) this).childItemPairList = _CollectionUtils.unmodifiableList(childItemList);
            }
        } else if (this instanceof StandardUpdates.BatchSingleUpdate
                && ((BatchSingleUpdate<F>) this).paramList == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this.onAsStandardUpdate();
    }

    abstract I onAsStandardUpdate();


    @Override
    final void onClear() {
        if (this instanceof SimpleDomainUpdate) {
            ((SimpleDomainUpdate<F>) this).childItemPairList = null;
        } else if (this instanceof BatchDomainUpdate) {
            ((BatchDomainUpdate<F>) this).childItemPairList = null;
        } else if (this instanceof BatchSingleUpdate) {
            ((BatchSingleUpdate<F>) this).paramList = null;
        }
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


        private SimpleSingleUpdate(CriteriaContext context, TableMeta<?> table, String tableAlias) {
            super(context, table, tableAlias);
        }

        @Override
        public final _StandardWhereClause<I> sets(Consumer<ItemPairs<F>> consumer) {
            consumer.accept(CriteriaSupports.itemPairs(this::onAddItemPair));
            return this;
        }


    }//StandardSingleUpdate

    private static final class PrimarySimpleSingleUpdate<I extends Item, F extends TableField>
            extends SimpleSingleUpdate<I, F> {

        private final Function<? super Update, I> function;

        private PrimarySimpleSingleUpdate(CriteriaContext context, TableMeta<?> table
                , String tableAlias, Function<? super Update, I> function) {
            super(context, table, tableAlias);
            this.function = function;
        }

        @Override
        I onAsStandardUpdate() {
            return this.function.apply(this);
        }


    }//PrimarySimpleSingleUpdate


    private static final class SimpleDomainUpdate<F extends TableField> extends SimpleSingleUpdate<Update, F>
            implements _DomainUpdate {

        private List<_ItemPair> childItemPairList;

        private SimpleDomainUpdate(CriteriaContext context, TableMeta<?> table, String tableAlias) {
            super(context, table, tableAlias);
        }

        @Override
        void onAddChildItemPair(SQLs.ArmyItemPair pair) {
            List<_ItemPair> childItemPairList = this.childItemPairList;
            if (childItemPairList == null) {
                this.childItemPairList = childItemPairList = new ArrayList<>();
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
        Update onAsStandardUpdate() {
            return this;
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
        public _BatchWhereClause<BatchUpdate> sets(Consumer<BatchItemPairs<F>> consumer) {
            consumer.accept(CriteriaSupports.batchItemPairs(this::onAddItemPair));
            return this;
        }

        private List<?> paramList;

        private BatchSingleUpdate(CriteriaContext context, TableMeta<?> updateTable, String tableAlias) {
            super(context, updateTable, tableAlias);
        }

        @Override
        public final <P> _DmlUpdateSpec<BatchUpdate> paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public final <P> _DmlUpdateSpec<BatchUpdate> paramList(Supplier<List<P>> supplier) {
            return this.paramList(supplier.get());
        }

        @Override
        public final _DmlUpdateSpec<BatchUpdate> paramList(Function<String, ?> function, String keyName) {
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
        BatchUpdate onAsStandardUpdate() {
            return this;
        }


    }//BatchUpdate


    private static final class BatchDomainUpdate<F extends TableField> extends BatchSingleUpdate<F>
            implements _DomainUpdate {

        private List<_ItemPair> childItemPairList;

        private BatchDomainUpdate(CriteriaContext context, TableMeta<?> updateTable, String tableAlias) {
            super(context, updateTable, tableAlias);
        }

        @Override
        void onAddChildItemPair(SQLs.ArmyItemPair pair) {
            List<_ItemPair> childItemPairList = this.childItemPairList;
            if (childItemPairList == null) {
                this.childItemPairList = childItemPairList = new ArrayList<>();
            } else if (!(childItemPairList instanceof ArrayList)) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            childItemPairList.add(pair);
        }

        @Override
        BatchUpdate onAsStandardUpdate() {
            return this;
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


    private static final class PrimarySimpleSingleUpdateClause<I extends Item> implements _SingleUpdateClause<I> {


        private final CriteriaContext context;

        private final Function<? super Update, I> function;

        private PrimarySimpleSingleUpdateClause(@Nullable ArmyStmtSpec spec, Function<? super Update, I> function) {
            this.context = CriteriaContexts.primarySingleDmlContext(spec);
            ContextStack.push(this.context);
            this.function = function;
        }


        @Override
        public <T> _StandardSetClause<I, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as,
                                                              String tableAlias) {
            return new PrimarySimpleSingleUpdate<>(this.context, table, tableAlias, this.function);
        }

        @Override
        public <P> _StandardSetClause<I, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, SQLs.WordAs as,
                                                              String tableAlias) {
            return new PrimarySimpleSingleUpdate<>(this.context, table, tableAlias, this.function);
        }

    }//PrimarySimpleSingleUpdateClause


    private static final class SimpleDomainUpdateClause implements _DomainUpdateClause {

        private final CriteriaContext context;

        private SimpleDomainUpdateClause() {
            this.context = CriteriaContexts.primarySingleDmlContext(null);
            ContextStack.push(this.context);
        }

        @Override
        public _StandardSetClause<Update, FieldMeta<?>> update(TableMeta<?> table, String tableAlias) {
            return new SimpleDomainUpdate<>(this.context, table, tableAlias);
        }

        @Override
        public <T> _StandardSetClause<Update, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as,
                                                                   String tableAlias) {
            return new SimpleDomainUpdate<>(this.context, table, tableAlias);
        }

        @Override
        public <T> _StandardSetClause<Update, FieldMeta<? super T>> update(ChildTableMeta<T> table, SQLs.WordAs as,
                                                                           String tableAlias) {
            return new SimpleDomainUpdate<>(this.context, table, tableAlias);
        }
    }// SimpleDomainUpdateClause


    private static final class BatchSingleUpdateClause implements _BatchSingleUpdateClause {

        private final CriteriaContext context;

        private BatchSingleUpdateClause() {
            this.context = CriteriaContexts.primarySingleDmlContext(null);
            ContextStack.push(this.context);
        }


        @Override
        public <T> _BatchSetClause<BatchUpdate, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as,
                                                                     String tableAlias) {
            return new BatchSingleUpdate<>(this.context, table, tableAlias);
        }

        @Override
        public <P> _BatchSetClause<BatchUpdate, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, SQLs.WordAs as,
                                                                     String tableAlias) {
            return new BatchSingleUpdate<>(this.context, table, tableAlias);
        }


    }//BatchSingleUpdateClause


    private static final class BatchDomainUpdateClause implements _BatchDomainUpdateClause {

        private final CriteriaContext context;

        private BatchDomainUpdateClause() {
            this.context = CriteriaContexts.primarySingleDmlContext(null);
            ContextStack.push(this.context);
        }


        @Override
        public _BatchSetClause<BatchUpdate, FieldMeta<?>> update(TableMeta<?> table, String tableAlias) {
            return new BatchDomainUpdate<>(this.context, table, tableAlias);
        }

        @Override
        public <T> _BatchSetClause<BatchUpdate, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as,
                                                                     String tableAlias) {
            return new BatchDomainUpdate<>(this.context, table, tableAlias);
        }

        @Override
        public <T> _BatchSetClause<BatchUpdate, FieldMeta<? super T>> update(ChildTableMeta<T> table, SQLs.WordAs as,
                                                                             String tableAlias) {
            return new BatchDomainUpdate<>(this.context, table, tableAlias);
        }


    }//BatchDomainUpdateClause


}

