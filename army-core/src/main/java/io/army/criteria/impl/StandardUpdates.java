package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._DomainUpdate;
import io.army.criteria.impl.inner._ItemPair;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
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

abstract class StandardUpdates<I extends Item, F extends TableField, PS extends Update._ItemPairBuilder, SR, SD, WR, WA>
        extends SingleUpdate<I, F, PS, SR, SD, WR, WA, Object, Object, Object, Object>
        implements StandardUpdate, Update {

    static <I extends Item> _SingleUpdateClause<I> simpleSingle(Function<Update, I> function) {
        return new PrimarySimpleSingleUpdateClause<>(function);
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
            ItemPairs<F>,
            _WhereSpec<I, F>,
            _StandardWhereClause<I>,
            _DmlUpdateSpec<I>,
            _WhereAndSpec<I>>
            implements _WhereSpec<I, F>, _WhereAndSpec<I> {


        private SimpleSingleUpdate(CriteriaContext context, TableMeta<?> table, String tableAlias) {
            super(context, table, tableAlias);
        }


        @Override
        final ItemPairs<F> createItemPairBuilder(Consumer<ItemPair> consumer) {
            return CriteriaSupports.itemPairs(consumer);
        }


    }//StandardSingleUpdate

    private static final class PrimarySimpleSingleUpdate<I extends Item, F extends TableField>
            extends SimpleSingleUpdate<I, F> {

        private final Function<Update, I> function;

        private PrimarySimpleSingleUpdate(CriteriaContext context, TableMeta<?> table
                , String tableAlias, Function<Update, I> function) {
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
        Update onAsStandardUpdate() {
            final List<_ItemPair> list = this.childItemPairList;
            if (list == null) {
                this.childItemPairList = Collections.emptyList();
            } else if (list instanceof ArrayList) {
                this.childItemPairList = _CollectionUtils.unmodifiableList(list);
            } else {
                throw ContextStack.castCriteriaApi(this.context);
            }
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
            Update,
            F,
            BatchItemPairs<F>,
            _BatchWhereSpec<Update, F>,
            _BatchWhereClause<Update>,
            _BatchParamClause<_DmlUpdateSpec<Update>>,
            _BatchWhereAndSpec<Update>>
            implements _BatchWhereSpec<Update, F>, _BatchWhereAndSpec<Update>, _BatchDml {

        private List<?> paramList;

        private BatchSingleUpdate(CriteriaContext context, TableMeta<?> updateTable, String tableAlias) {
            super(context, updateTable, tableAlias);
        }

        @Override
        public final <P> _DmlUpdateSpec<Update> paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public final <P> _DmlUpdateSpec<Update> paramList(Supplier<List<P>> supplier) {
            return this.paramList(supplier.get());
        }

        @Override
        public final _DmlUpdateSpec<Update> paramList(Function<String, ?> function, String keyName) {
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
        final BatchItemPairs<F> createItemPairBuilder(Consumer<ItemPair> consumer) {
            return CriteriaSupports.batchItemPairs(consumer);
        }

        @Override
        Update onAsStandardUpdate() {
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


    }//BatchDomainUpdate


    private static final class PrimarySimpleSingleUpdateClause<I extends Item> implements _SingleUpdateClause<I> {


        private final CriteriaContext context;

        private final Function<Update, I> function;

        private PrimarySimpleSingleUpdateClause(Function<Update, I> function) {
            this.context = CriteriaContexts.primarySingleDmlContext();
            ContextStack.push(this.context);
            this.function = function;
        }

        @Override
        public <T> _StandardSetClause<I, FieldMeta<T>> update(SingleTableMeta<T> table, String tableAlias) {
            return new PrimarySimpleSingleUpdate<>(this.context, table, tableAlias, this.function);
        }

        @Override
        public <P> _StandardSetClause<I, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, String tableAlias) {
            return new PrimarySimpleSingleUpdate<>(this.context, table, tableAlias, this.function);
        }


    }//PrimarySimpleSingleUpdateClause


    private static final class SimpleDomainUpdateClause implements _DomainUpdateClause {

        private final CriteriaContext context;

        private SimpleDomainUpdateClause() {
            this.context = CriteriaContexts.primarySingleDmlContext();
            ContextStack.push(this.context);
        }

        @Override
        public _StandardSetClause<Update, FieldMeta<?>> update(TableMeta<?> table, String tableAlias) {
            return new SimpleDomainUpdate<>(this.context, table, tableAlias);
        }

    }// SimpleDomainUpdateClause


    private static final class BatchSingleUpdateClause implements _BatchSingleUpdateClause {

        private final CriteriaContext context;

        private BatchSingleUpdateClause() {
            this.context = CriteriaContexts.primarySingleDmlContext();
            ContextStack.push(this.context);
        }

        @Override
        public <T> _BatchSetClause<Update, FieldMeta<T>> update(SingleTableMeta<T> table, String tableAlias) {
            return new BatchSingleUpdate<>(this.context, table, tableAlias);
        }

        @Override
        public <P> _BatchSetClause<Update, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, String tableAlias) {
            return new BatchSingleUpdate<>(this.context, table, tableAlias);
        }


    }//BatchSingleUpdateClause


    private static final class BatchDomainUpdateClause implements _BatchDomainUpdateClause {

        private final CriteriaContext context;

        private BatchDomainUpdateClause() {
            this.context = CriteriaContexts.primarySingleDmlContext();
            ContextStack.push(this.context);
        }

        @Override
        public <T> _BatchSetClause<Update, FieldMeta<? super T>> update(TableMeta<T> table, String tableAlias) {
            return new BatchDomainUpdate<>(this.context, table, tableAlias);
        }

    }//BatchDomainUpdateClause


}

