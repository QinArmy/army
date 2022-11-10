package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLSingleUpdate;
import io.army.criteria.mysql.MySQLCtes;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is an implementation of {@link _MySQLSingleUpdate}.
 * </p>
 */
abstract class MySQLSingleUpdate<I extends Item, T, UT, SR, WR, WA, OR, LR>
        extends SingleUpdate<I, FieldMeta<T>, SR, WR, WA, OR, LR, Object, Object>
        implements _MySQLSingleUpdate, MySQLUpdate, Update
        , MySQLQuery._IndexHintForOrderByClause<UT> {

    static <I extends Item> _SingleWithSpec<I> simple(Function<Update, I> function) {
        return new SimpleUpdateClause<>(function);
    }

    static <I extends Item> _BatchSingleWithSpec<I> batch(Function<Update, I> function) {
        return new BatchUpdateClause<>(function);
    }

    private final Function<Update, I> function;


    private final boolean recursive;

    private final List<_Cte> cteList;

    private final List<Hint> hintList;

    private final List<MySQLs.Modifier> modifierList;

    private final List<String> partitionList;

    private List<MySQLIndexHint> indexHintList;


    private MySQLQuery._IndexHintForOrderByClause<UT> hintClause;

    private MySQLSingleUpdate(UpdateClause<I, ?> clause) {
        super(clause.context, clause.updateTable, clause.tableAlias);

        this.function = clause.function;
        this.recursive = clause.isRecursive();
        this.cteList = clause.cteList();
        this.hintList = _CollectionUtils.safeList(clause.hintList);

        this.modifierList = _CollectionUtils.safeList(clause.modifierList);
        this.partitionList = _CollectionUtils.safeList(clause.partitionList);

    }


    /*################################## blow IndexHintClause method ##################################*/

    @Override
    public final MySQLQuery._IndexForOrderBySpec<UT> useIndex() {
        return this.getHintClause().useIndex();
    }

    @Override
    public final MySQLQuery._IndexForOrderBySpec<UT> ignoreIndex() {
        return this.getHintClause().ignoreIndex();
    }

    @Override
    public final MySQLQuery._IndexForOrderBySpec<UT> forceIndex() {
        return this.getHintClause().forceIndex();
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
    public final List<Hint> hintList() {
        return this.hintList;
    }

    @Override
    public final List<MySQLs.Modifier> modifierList() {
        return this.modifierList;
    }


    @Override
    public final List<String> partitionList() {
        return this.partitionList;
    }

    @Override
    public final List<? extends _IndexHint> indexHintList() {
        final List<MySQLIndexHint> list = this.indexHintList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }


    @Override
    final I onAsUpdate() {
        this.hintClause = null;
        final List<MySQLIndexHint> indexHintList = this.indexHintList;
        if (indexHintList == null) {
            this.indexHintList = Collections.emptyList();
        } else {
            this.indexHintList = _CollectionUtils.unmodifiableList(indexHintList);
        }
        if (this instanceof BatchUpdateStatement && ((BatchUpdateStatement<I, T>) this).paramList == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this.function.apply(this);
    }

    @Override
    final void onClear() {
        this.indexHintList = null;
        if (this instanceof BatchUpdateStatement) {
            ((BatchUpdateStatement<I, T>) this).paramList = null;
        }
    }

    @Override
    final Dialect statementDialect() {
        return MySQLDialect.MySQL80;
    }

    /**
     * @see #useIndex()
     * @see #ignoreIndex()
     * @see #forceIndex()
     */
    private MySQLQuery._IndexHintForOrderByClause<UT> getHintClause() {
        MySQLQuery._IndexHintForOrderByClause<UT> clause = this.hintClause;
        if (clause == null) {
            clause = MySQLSupports.indexHintClause(this.context, this::onAddIndexHint);
            this.hintClause = clause;
        }
        return clause;
    }


    @SuppressWarnings("unchecked")
    private UT onAddIndexHint(final MySQLIndexHint indexHint) {
        List<MySQLIndexHint> indexHintList = this.indexHintList;
        if (indexHintList == null) {
            indexHintList = new ArrayList<>();
            this.indexHintList = indexHintList;
        } else if (!(indexHintList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        indexHintList.add(indexHint);
        return (UT) this;
    }


    private static final class SimpleUpdate<I extends Item, T> extends MySQLSingleUpdate<
            I,
            T,
            MySQLUpdate._SingleIndexHintSpec<I, T>,
            MySQLUpdate._SingleWhereSpec<I, T>,
            MySQLUpdate._OrderBySpec<I>,
            MySQLUpdate._SingleWhereAndSpec<I>,
            MySQLUpdate._LimitSpec<I>,
            Statement._DmlUpdateSpec<I>>
            implements MySQLUpdate._SingleIndexHintSpec<I, T>
            , MySQLUpdate._SingleWhereSpec<I, T>
            , MySQLUpdate._SingleWhereAndSpec<I> {

        private SimpleUpdate(SimpleUpdateClause<I> clause) {
            super(clause);
        }


        @Override
        public _SingleWhereClause<I> set(Consumer<ItemPairs<FieldMeta<T>>> consumer) {
            consumer.accept(CriteriaSupports.itemPairs(this::onAddItemPair));
            return this;
        }

        @Override
        public _LimitSpec<I> orderBy(Consumer<SortItems> consumer) {
             consumer.accept(new OrderBySortItems(this));
             if(!this.hasOrderByClause()){
                 throw ContextStack.criteriaError(this.context, _Exceptions::sortItemListIsEmpty);
             }
            return this;
        }

        @Override
        public _LimitSpec<I> ifOrderBy(Consumer<SortItems> consumer) {
            consumer.accept(new OrderBySortItems(this));
            return this;
        }

    }//SimpleUpdate


    private static abstract class UpdateClause<I extends Item, WE>
            extends CriteriaSupports.WithClause<MySQLCtes, WE> {

        private final Function<Update, I> function;

        List<Hint> hintList;

        List<MySQLs.Modifier> modifierList;

        TableMeta<?> updateTable;

        List<String> partitionList;

        String tableAlias;

        private UpdateClause(Function<Update, I> function) {
            super(CriteriaContexts.primarySingleDmlContext());
            this.function = function;
            ContextStack.push(this.context);
        }

        @Override
        final MySQLCtes createCteBuilder(boolean recursive) {
            return MySQLSupports.mySQLCteBuilder(recursive, this.context);
        }


    }//UpdateClause


    private static final class SimpleComma<I extends Item> implements MySQLUpdate._SingleComma<I> {

        private final boolean recursive;

        private final SimpleUpdateClause<I> clause;

        private final Function<String, MySQLQuery._StaticCteLeftParenSpec<MySQLUpdate._SingleComma<I>>> function;

        private SimpleComma(boolean recursive, SimpleUpdateClause<I> clause) {
            this.recursive = recursive;
            this.clause = clause;
            this.function = MySQLQueries.complexCte(clause.context, this);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_SingleComma<I>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public <T> _SingleIndexHintSpec<I, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias) {
            return this.endWithClause().update(hints, modifiers, table, wordAs, alias);
        }

        @Override
        public <P> _SingleIndexHintSpec<I, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias) {
            return this.endWithClause().update(hints, modifiers, table, wordAs, alias);
        }

        @Override
        public <T> _SingleIndexHintSpec<I, T> update(SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias) {
            return this.endWithClause().update(table, wordAs, alias);
        }

        @Override
        public <P> _SingleIndexHintSpec<I, P> update(ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias) {
            return this.endWithClause().update(table, wordAs, alias);
        }

        @Override
        public <T> _SinglePartitionClause<I, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table) {
            return this.endWithClause().update(hints, modifiers, table);
        }

        @Override
        public <P> _SinglePartitionClause<I, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table) {
            return this.endWithClause().update(hints, modifiers, table);
        }

        @Override
        public <T> _SinglePartitionClause<I, T> update(SingleTableMeta<T> table) {
            return this.endWithClause().update(table);
        }

        @Override
        public <P> _SinglePartitionClause<I, P> update(ComplexTableMeta<P, ?> table) {
            return this.endWithClause().update(table);
        }

        private SimpleUpdateClause<I> endWithClause() {
            final SimpleUpdateClause<I> clause = this.clause;
            clause.endStaticWithClause(this.recursive);
            return clause;
        }


    }//SimpleComma


    private static final class SimpleUpdateClause<I extends Item>
            extends UpdateClause<I, MySQLUpdate._SingleUpdateClause<I>>
            implements MySQLUpdate._SingleWithSpec<I> {


        private SimpleUpdateClause(Function<Update, I> function) {
            super(function);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_SingleComma<I>> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new SimpleComma<>(recursive, this).function.apply(name);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_SingleComma<I>> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new SimpleComma<>(recursive, this).function.apply(name);
        }

        @Override
        public <T> _SingleIndexHintSpec<I, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias) {
            assert wordAs == SQLs.AS;
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
            this.tableAlias = alias;
            return new SimpleUpdate<>(this);
        }

        @Override
        public <P> _SingleIndexHintSpec<I, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias) {
            assert wordAs == SQLs.AS;
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
            this.tableAlias = alias;
            return new SimpleUpdate<>(this);
        }

        @Override
        public <T> _SingleIndexHintSpec<I, T> update(SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias) {
            assert wordAs == SQLs.AS;
            this.updateTable = table;
            this.tableAlias = alias;
            return new SimpleUpdate<>(this);
        }

        @Override
        public <P> _SingleIndexHintSpec<I, P> update(ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias) {
            assert wordAs == SQLs.AS;
            this.updateTable = table;
            this.tableAlias = alias;
            return new SimpleUpdate<>(this);
        }

        @Override
        public <T> _SinglePartitionClause<I, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        public <P> _SinglePartitionClause<I, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        public <T> _SinglePartitionClause<I, T> update(SingleTableMeta<T> table) {
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        public <P> _SinglePartitionClause<I, P> update(ComplexTableMeta<P, ?> table) {
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }


    }//SimpleUpdateClause

    private static final class SimplePartitionClause<I extends Item, T>
            extends MySQLSupports.PartitionAsClause<MySQLUpdate._SingleIndexHintSpec<I, T>>
            implements MySQLUpdate._SinglePartitionClause<I, T> {

        private final SimpleUpdateClause<I> clause;

        private SimplePartitionClause(SimpleUpdateClause<I> clause) {
            super(clause.context, _JoinType.NONE, clause.updateTable);
            assert this.table != null;
            this.clause = clause;
        }

        @Override
        _SingleIndexHintSpec<I, T> asEnd(MySQLSupports.MySQLBlockParams params) {
            final SimpleUpdateClause<I> clause = this.clause;
            clause.partitionList = params.partitionList();
            clause.tableAlias = params.alias();
            return new SimpleUpdate<>(clause);
        }


    }//SimplePartitionClause


    private static final class BatchComma<I extends Item> implements MySQLUpdate._BatchSingleComma<I> {

        private final boolean recursive;

        private final BatchUpdateClause<I> clause;

        private final Function<String, MySQLQuery._StaticCteLeftParenSpec<MySQLUpdate._BatchSingleComma<I>>> function;

        private BatchComma(boolean recursive, BatchUpdateClause<I> clause) {
            this.recursive = recursive;
            this.clause = clause;
            this.function = MySQLQueries.complexCte(clause.context, this);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchSingleComma<I>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public <T> _BatchSingleIndexHintSpec<I, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias) {
            return this.endWithClause().update(hints, modifiers, table, wordAs, alias);
        }

        @Override
        public <P> _BatchSingleIndexHintSpec<I, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias) {
            return this.endWithClause().update(hints, modifiers, table, wordAs, alias);
        }

        @Override
        public <T> _BatchSingleIndexHintSpec<I, T> update(SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias) {
            return this.endWithClause().update(table, wordAs, alias);
        }

        @Override
        public <P> _BatchSingleIndexHintSpec<I, P> update(ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias) {
            return this.endWithClause().update(table, wordAs, alias);
        }

        @Override
        public <T> _BatchSinglePartitionClause<I, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table) {
            return this.endWithClause().update(hints, modifiers, table);
        }

        @Override
        public <P> _BatchSinglePartitionClause<I, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table) {
            return this.endWithClause().update(hints, modifiers, table);
        }

        @Override
        public <T> _BatchSinglePartitionClause<I, T> update(SingleTableMeta<T> table) {
            return this.endWithClause().update(table);
        }

        @Override
        public <P> _BatchSinglePartitionClause<I, P> update(ComplexTableMeta<P, ?> table) {
            return this.endWithClause().update(table);
        }

        private BatchUpdateClause<I> endWithClause() {
            final BatchUpdateClause<I> clause = this.clause;
            clause.endStaticWithClause(this.recursive);
            return clause;
        }


    }//BatchComma


    private static final class BatchUpdateStatement<I extends Item, T> extends MySQLSingleUpdate<
            I,
            T,
            MySQLUpdate._BatchSingleIndexHintSpec<I, T>,
            MySQLUpdate._BatchSingleWhereSpec<I, T>,
            MySQLUpdate._BatchOrderBySpec<I>,
            MySQLUpdate._BatchSingleWhereAndSpec<I>,
            MySQLUpdate._BatchLimitSpec<I>,
            Statement._BatchParamClause<_DmlUpdateSpec<I>>>
            implements MySQLUpdate._BatchSingleIndexHintSpec<I, T>
            , MySQLUpdate._BatchSingleWhereSpec<I, T>
            , MySQLUpdate._BatchSingleWhereAndSpec<I>
            , _DmlUpdateSpec<I>
            , _BatchDml {

        private List<?> paramList;

        private BatchUpdateStatement(BatchUpdateClause<I> clause) {
            super(clause);
        }

        @Override
        public _BatchSingleWhereClause<I> set(Consumer<BatchItemPairs<FieldMeta<T>>> consumer) {
            consumer.accept(CriteriaSupports.batchItemPairs(this::onAddItemPair));
            return this;
        }

        @Override
        public <P> _DmlUpdateSpec<I> paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlUpdateSpec<I> paramList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(this.context, supplier.get());
            return this;
        }

        @Override
        public _DmlUpdateSpec<I> paramList(Function<String, ?> function, String keyName) {
            this.paramList = CriteriaUtils.paramList(this.context, (List<?>) function.apply(keyName));
            return this;
        }

        @Override
        public List<?> paramList() {
            final List<?> list = this.paramList;
            if (list == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return list;
        }

    }//BatchUpdate


    private static final class BatchUpdateClause<I extends Item>
            extends UpdateClause<I, MySQLUpdate._BatchSingleUpdateClause<I>>
            implements MySQLUpdate._BatchSingleWithSpec<I> {

        private BatchUpdateClause(Function<Update, I> function) {
            super(function);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchSingleComma<I>> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new BatchComma<>(recursive, this).function.apply(name);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchSingleComma<I>> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new BatchComma<>(recursive, this).function.apply(name);
        }

        @Override
        public <T> _BatchSingleIndexHintSpec<I, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias) {
            assert wordAs == SQLs.AS;
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
            this.tableAlias = alias;
            return new BatchUpdateStatement<>(this);
        }

        @Override
        public <P> _BatchSingleIndexHintSpec<I, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias) {
            assert wordAs == SQLs.AS;
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
            this.tableAlias = alias;
            return new BatchUpdateStatement<>(this);
        }

        @Override
        public <T> _BatchSingleIndexHintSpec<I, T> update(SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias) {
            assert wordAs == SQLs.AS;
            this.updateTable = table;
            this.tableAlias = alias;
            return new BatchUpdateStatement<>(this);
        }

        @Override
        public <P> _BatchSingleIndexHintSpec<I, P> update(ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias) {
            assert wordAs == SQLs.AS;
            this.updateTable = table;
            this.tableAlias = alias;
            return new BatchUpdateStatement<>(this);
        }

        @Override
        public <T> _BatchSinglePartitionClause<I, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
            return new BatchPartitionClause<>(this);
        }

        @Override
        public <P> _BatchSinglePartitionClause<I, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
            return new BatchPartitionClause<>(this);
        }

        @Override
        public <T> _BatchSinglePartitionClause<I, T> update(SingleTableMeta<T> table) {
            this.updateTable = table;
            return new BatchPartitionClause<>(this);
        }

        @Override
        public <P> _BatchSinglePartitionClause<I, P> update(ComplexTableMeta<P, ?> table) {
            this.updateTable = table;
            return new BatchPartitionClause<>(this);
        }


    }//BatchUpdateClause

    private static final class BatchPartitionClause<I extends Item, T>
            extends MySQLSupports.PartitionAsClause<MySQLUpdate._BatchSingleIndexHintSpec<I, T>>
            implements MySQLUpdate._BatchSinglePartitionClause<I, T> {

        private final BatchUpdateClause<I> clause;

        private BatchPartitionClause(BatchUpdateClause<I> clause) {
            super(clause.context, _JoinType.NONE, clause.updateTable);
            assert this.table != null;
            this.clause = clause;
        }

        @Override
        _BatchSingleIndexHintSpec<I, T> asEnd(MySQLSupports.MySQLBlockParams params) {
            final BatchUpdateClause<I> clause = this.clause;
            clause.partitionList = params.partitionList();
            clause.tableAlias = params.alias();
            return new BatchUpdateStatement<>(clause);
        }


    }//BatchPartitionClause


}
