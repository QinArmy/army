package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLSingleUpdate;
import io.army.criteria.mysql.MySQLCteBuilder;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
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
 * This class is an implementation of {@link _MySQLSingleUpdate}.
 * </p>
 */
abstract class MySQLSingleUpdate<T, UT, PS extends Update._ItemPairBuilder, SR, SD, WR, WA, OR, LR>
        extends SingleUpdate<Update, Item, FieldMeta<T>, PS, SR, SD, WR, WA, OR, LR>
        implements _MySQLSingleUpdate, MySQLUpdate, Update
        , MySQLQuery._IndexHintForOrderByClause<UT> {

    static _SingleWithSpec simple() {
        return new SimpleUpdateClause();
    }

    static _BatchSingleWithSpec batch() {
        return new BatchUpdateClause();
    }


    private final boolean recursive;

    private final List<_Cte> cteList;

    private final List<Hint> hintList;

    private final List<MySQLs.Modifier> modifierList;

    private final List<String> partitionList;

    private List<MySQLIndexHint> indexHintList;


    private MySQLQuery._IndexHintForOrderByClause<UT> hintClause;

    private MySQLSingleUpdate(UpdateClause<?> clause) {
        super(clause.context, clause.updateTable, clause.tableAlias);
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
    public final String toString() {
        final String s;
        if (this.isPrepared()) {
            s = this.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
    }

    @Override
    final Update onAsUpdate() {
        this.hintClause = null;
        final List<MySQLIndexHint> indexHintList = this.indexHintList;
        if (indexHintList == null) {
            this.indexHintList = Collections.emptyList();
        } else {
            this.indexHintList = _CollectionUtils.unmodifiableList(indexHintList);
        }
        if (this instanceof BatchUpdateStatement && ((BatchUpdateStatement<T>) this).paramList == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this;
    }

    @Override
    final void onClear() {
        this.indexHintList = null;
        if (this instanceof BatchUpdateStatement) {
            ((BatchUpdateStatement<T>) this).paramList = null;
        }
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


    private static final class SimpleUpdate<T> extends MySQLSingleUpdate<
            T,
            MySQLUpdate._SingleIndexHintSpec<Update, T>,
            ItemPairs<FieldMeta<T>>,
            MySQLUpdate._SingleWhereSpec<Update, T>,
            MySQLUpdate._SingleWhereClause<Update>,
            MySQLUpdate._OrderBySpec<Update>,
            MySQLUpdate._SingleWhereAndSpec<Update>,
            MySQLUpdate._LimitSpec<Update>,
            Statement._DmlUpdateSpec<Update>>
            implements MySQLUpdate._SingleIndexHintSpec<Update, T>
            , MySQLUpdate._SingleWhereSpec<Update, T>
            , MySQLUpdate._SingleWhereAndSpec<Update> {

        private SimpleUpdate(UpdateClause<?> clause) {
            super(clause);
        }

        @Override
        ItemPairs<FieldMeta<T>> createItemPairBuilder(Consumer<ItemPair> consumer) {
            return CriteriaSupports.itemPairs(consumer);
        }


    }//SimpleUpdate


    private static abstract class UpdateClause<WE> extends CriteriaSupports.WithClause<MySQLCteBuilder, WE> {

        List<Hint> hintList;

        List<MySQLs.Modifier> modifierList;

        TableMeta<?> updateTable;

        List<String> partitionList;

        String tableAlias;

        private UpdateClause() {
            super(CriteriaContexts.primarySingleDmlContext());
            ContextStack.push(this.context);
        }

        @Override
        final MySQLCteBuilder createCteBuilder(boolean recursive) {
            return MySQLSupports.mySQLCteBuilder(recursive, this.context);
        }


    }//UpdateClause


    private static final class SimpleComma implements MySQLUpdate._SingleComma {

        private final boolean recursive;

        private final SimpleUpdateClause clause;

        private final Function<String, MySQLQuery._StaticCteLeftParenSpec<MySQLUpdate._SingleComma>> function;

        private SimpleComma(boolean recursive, SimpleUpdateClause clause) {
            this.recursive = recursive;
            this.clause = clause;
            this.function = MySQLQueries.complexCte(clause.context, this);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_SingleComma> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public <T> _SingleIndexHintSpec<Update, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table, String alias) {
            return this.endWithClause().update(hints, modifiers, table, alias);
        }

        @Override
        public <P> _SingleIndexHintSpec<Update, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table, String alias) {
            return this.endWithClause().update(hints, modifiers, table, alias);
        }

        @Override
        public <T> _SingleIndexHintSpec<Update, T> update(SingleTableMeta<T> table, String alias) {
            return this.endWithClause().update(table, alias);
        }

        @Override
        public <P> _SingleIndexHintSpec<Update, P> update(ComplexTableMeta<P, ?> table, String alias) {
            return this.endWithClause().update(table, alias);
        }

        @Override
        public <T> _SinglePartitionClause<Update, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table) {
            return this.endWithClause().update(hints, modifiers, table);
        }

        @Override
        public <P> _SinglePartitionClause<Update, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table) {
            return this.endWithClause().update(hints, modifiers, table);
        }

        @Override
        public <T> _SinglePartitionClause<Update, T> update(SingleTableMeta<T> table) {
            return this.endWithClause().update(table);
        }

        @Override
        public <P> _SinglePartitionClause<Update, P> update(ComplexTableMeta<P, ?> table) {
            return this.endWithClause().update(table);
        }

        private SimpleUpdateClause endWithClause() {
            final SimpleUpdateClause clause = this.clause;
            clause.endStaticWithClause(this.recursive);
            return clause;
        }


    }//SimpleComma


    private static final class SimpleUpdateClause extends UpdateClause<MySQLUpdate._SingleUpdateClause>
            implements MySQLUpdate._SingleWithSpec {


        private SimpleUpdateClause() {
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_SingleComma> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new SimpleComma(recursive, this).function.apply(name);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_SingleComma> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new SimpleComma(recursive, this).function.apply(name);
        }

        @Override
        public <T> _SingleIndexHintSpec<Update, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table, String alias) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
            this.tableAlias = alias;
            return new SimpleUpdate<>(this);
        }

        @Override
        public <P> _SingleIndexHintSpec<Update, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table, String alias) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
            this.tableAlias = alias;
            return new SimpleUpdate<>(this);
        }

        @Override
        public <T> _SingleIndexHintSpec<Update, T> update(SingleTableMeta<T> table, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return new SimpleUpdate<>(this);
        }

        @Override
        public <P> _SingleIndexHintSpec<Update, P> update(ComplexTableMeta<P, ?> table, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return new SimpleUpdate<>(this);
        }

        @Override
        public <T> _SinglePartitionClause<Update, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        public <P> _SinglePartitionClause<Update, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        public <T> _SinglePartitionClause<Update, T> update(SingleTableMeta<T> table) {
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        public <P> _SinglePartitionClause<Update, P> update(ComplexTableMeta<P, ?> table) {
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }


    }//SimpleUpdateClause

    private static final class SimplePartitionClause<T>
            extends MySQLSupports.PartitionAsClause<MySQLUpdate._SingleIndexHintSpec<Update, T>>
            implements MySQLUpdate._SinglePartitionClause<Update, T> {

        private final SimpleUpdateClause clause;

        private SimplePartitionClause(SimpleUpdateClause clause) {
            super(clause.context, _JoinType.NONE, clause.updateTable);
            assert this.table != null;
            this.clause = clause;
        }

        @Override
        _SingleIndexHintSpec<Update, T> asEnd(MySQLSupports.MySQLBlockParams params) {
            final SimpleUpdateClause clause = this.clause;
            clause.partitionList = params.partitionList();
            clause.tableAlias = params.alias();
            return new SimpleUpdate<>(clause);
        }


    }//SimplePartitionClause


    private static final class BatchComma implements MySQLUpdate._BatchSingleComma {

        private final boolean recursive;

        private final BatchUpdateClause clause;

        private final Function<String, MySQLQuery._StaticCteLeftParenSpec<MySQLUpdate._BatchSingleComma>> function;

        private BatchComma(boolean recursive, BatchUpdateClause clause) {
            this.recursive = recursive;
            this.clause = clause;
            this.function = MySQLQueries.complexCte(clause.context, this);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchSingleComma> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public <T> _BatchSingleIndexHintSpec<Update, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table, String alias) {
            return this.endWithClause().update(hints, modifiers, table, alias);
        }

        @Override
        public <P> _BatchSingleIndexHintSpec<Update, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table, String alias) {
            return this.endWithClause().update(hints, modifiers, table, alias);
        }

        @Override
        public <T> _BatchSingleIndexHintSpec<Update, T> update(SingleTableMeta<T> table, String alias) {
            return this.endWithClause().update(table, alias);
        }

        @Override
        public <P> _BatchSingleIndexHintSpec<Update, P> update(ComplexTableMeta<P, ?> table, String alias) {
            return this.endWithClause().update(table, alias);
        }

        @Override
        public <T> _BatchSinglePartitionClause<Update, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table) {
            return this.endWithClause().update(hints, modifiers, table);
        }

        @Override
        public <P> _BatchSinglePartitionClause<Update, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table) {
            return this.endWithClause().update(hints, modifiers, table);
        }

        @Override
        public <T> _BatchSinglePartitionClause<Update, T> update(SingleTableMeta<T> table) {
            return this.endWithClause().update(table);
        }

        @Override
        public <P> _BatchSinglePartitionClause<Update, P> update(ComplexTableMeta<P, ?> table) {
            return this.endWithClause().update(table);
        }

        private BatchUpdateClause endWithClause() {
            final BatchUpdateClause clause = this.clause;
            clause.endStaticWithClause(this.recursive);
            return clause;
        }


    }//BatchComma


    private static final class BatchUpdateStatement<T> extends MySQLSingleUpdate<
            T,
            MySQLUpdate._BatchSingleIndexHintSpec<Update, T>,
            BatchItemPairs<FieldMeta<T>>,
            MySQLUpdate._BatchSingleWhereSpec<Update, T>,
            MySQLUpdate._BatchSingleWhereClause<Update>,
            MySQLUpdate._BatchOrderBySpec<Update>,
            MySQLUpdate._BatchSingleWhereAndSpec<Update>,
            MySQLUpdate._BatchLimitSpec<Update>,
            Statement._BatchParamClause<_DmlUpdateSpec<Update>>>
            implements MySQLUpdate._BatchSingleIndexHintSpec<Update, T>
            , MySQLUpdate._BatchSingleWhereSpec<Update, T>
            , MySQLUpdate._BatchSingleWhereAndSpec<Update>
            , _DmlUpdateSpec<Update>
            , _BatchDml {

        private List<?> paramList;

        private BatchUpdateStatement(UpdateClause<?> clause) {
            super(clause);
        }


        @Override
        public <P> _DmlUpdateSpec<Update> paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlUpdateSpec<Update> paramList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(this.context, supplier.get());
            return this;
        }

        @Override
        public _DmlUpdateSpec<Update> paramList(Function<String, ?> function, String keyName) {
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

        @Override
        BatchItemPairs<FieldMeta<T>> createItemPairBuilder(Consumer<ItemPair> consumer) {
            return CriteriaSupports.batchItemPairs(consumer);
        }


    }//BatchUpdate


    private static final class BatchUpdateClause extends UpdateClause<MySQLUpdate._BatchSingleUpdateClause>
            implements MySQLUpdate._BatchSingleWithSpec {

        private BatchUpdateClause() {
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchSingleComma> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new BatchComma(recursive, this).function.apply(name);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchSingleComma> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new BatchComma(recursive, this).function.apply(name);
        }

        @Override
        public <T> _BatchSingleIndexHintSpec<Update, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table, String alias) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
            this.tableAlias = alias;
            return new BatchUpdateStatement<>(this);
        }

        @Override
        public <P> _BatchSingleIndexHintSpec<Update, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table, String alias) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
            this.tableAlias = alias;
            return new BatchUpdateStatement<>(this);
        }

        @Override
        public <T> _BatchSingleIndexHintSpec<Update, T> update(SingleTableMeta<T> table, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return new BatchUpdateStatement<>(this);
        }

        @Override
        public <P> _BatchSingleIndexHintSpec<Update, P> update(ComplexTableMeta<P, ?> table, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return new BatchUpdateStatement<>(this);
        }

        @Override
        public <T> _BatchSinglePartitionClause<Update, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
            return new BatchPartitionClause<>(this);
        }

        @Override
        public <P> _BatchSinglePartitionClause<Update, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
            return new BatchPartitionClause<>(this);
        }

        @Override
        public <T> _BatchSinglePartitionClause<Update, T> update(SingleTableMeta<T> table) {
            this.updateTable = table;
            return new BatchPartitionClause<>(this);
        }

        @Override
        public <P> _BatchSinglePartitionClause<Update, P> update(ComplexTableMeta<P, ?> table) {
            this.updateTable = table;
            return new BatchPartitionClause<>(this);
        }


    }//BatchUpdateClause

    private static final class BatchPartitionClause<T>
            extends MySQLSupports.PartitionAsClause<MySQLUpdate._BatchSingleIndexHintSpec<Update, T>>
            implements MySQLUpdate._BatchSinglePartitionClause<Update, T> {

        private final BatchUpdateClause clause;

        private BatchPartitionClause(BatchUpdateClause clause) {
            super(clause.context, _JoinType.NONE, clause.updateTable);
            assert this.table != null;
            this.clause = clause;
        }

        @Override
        _BatchSingleIndexHintSpec<Update, T> asEnd(MySQLSupports.MySQLBlockParams params) {
            final BatchUpdateClause clause = this.clause;
            clause.partitionList = params.partitionList();
            clause.tableAlias = params.alias();
            return new BatchUpdateStatement<>(clause);
        }


    }//SimplePartitionClause


}
