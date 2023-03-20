package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLSingleUpdate;
import io.army.criteria.mysql.MySQLCtes;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLStatement;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
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
 * This class is an implementation of single-table {@link MySQLUpdate}
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class MySQLSingleUpdates<I extends Item, T, UT extends Item, SR, WR, WA, OR, LR>
        extends SingleUpdateStatement<I, FieldMeta<T>, SR, WR, WA, OR, LR, Object, Object>
        implements _MySQLSingleUpdate,
        MySQLUpdate,
        UpdateStatement,
        MySQLStatement._IndexHintForOrderByClause<UT>,
        MySQLStatement._DynamicIndexHintClause<MySQLStatement._IndexForOrderBySpec<Object>, UT> {

    /**
     * <p>
     * create simple(non-batch) single-table UPDATE statement that is primary statement.
     * </p>
     *
     * @param spec non-null for multi-statement.
     */
    static <I extends Item> _SingleWithSpec<I> simple(@Nullable MultiStmtSpec spec,
                                                      Function<? super Update, I> function) {
        return new SimpleUpdateClause<>(spec, function);
    }

    /**
     * <p>
     * create batch single-table UPDATE statement that is primary statement.
     * </p>
     */
    static _BatchSingleWithSpec<BatchUpdate> batch() {
        return new BatchUpdateClause();
    }

    private final boolean recursive;

    private final List<_Cte> cteList;

    private final List<Hint> hintList;

    private final List<MySQLs.Modifier> modifierList;

    private final List<String> partitionList;

    private List<_IndexHint> indexHintList;

    private MySQLSingleUpdates(UpdateClause<I, ?> clause) {
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
        return MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.USE_INDEX,
                this::indexHintEnd);
    }

    @Override
    public final MySQLQuery._IndexForOrderBySpec<UT> ignoreIndex() {
        return MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.IGNORE_INDEX,
                this::indexHintEnd);
    }

    @Override
    public final MySQLQuery._IndexForOrderBySpec<UT> forceIndex() {
        return MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.FORCE_INDEX,
                this::indexHintEnd);
    }

    @Override
    public final UT ifUseIndex(Consumer<_IndexForOrderBySpec<Object>> consumer) {
        consumer.accept(MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.USE_INDEX,
                this::indexHintEndAndReturnObject));
        return (UT) this;
    }

    @Override
    public final UT ifIgnoreIndex(Consumer<_IndexForOrderBySpec<Object>> consumer) {
        consumer.accept(MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.IGNORE_INDEX,
                this::indexHintEndAndReturnObject));
        return (UT) this;
    }

    @Override
    public final UT ifForceIndex(Consumer<_IndexForOrderBySpec<Object>> consumer) {
        consumer.accept(MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.FORCE_INDEX,
                this::indexHintEndAndReturnObject));
        return (UT) this;
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
        final List<_IndexHint> list = this.indexHintList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }


    @Override
    final I onAsUpdate() {
        this.indexHintList = _CollectionUtils.safeUnmodifiableList(this.indexHintList);
        return this.asMySQLUpdate();
    }

    @Override
    final void onClear() {
        this.indexHintList = null;
        if (this instanceof MySQLSingleUpdates.MySQLBatchUpdate) {
            ((MySQLBatchUpdate<T>) this).paramList = null;
        }
    }

    @Override
    final Dialect statementDialect() {
        return MySQLDialect.MySQL80;
    }

    abstract I asMySQLUpdate();


    @SuppressWarnings("unchecked")
    private UT indexHintEnd(final _IndexHint indexHint) {

        List<_IndexHint> indexHintList = this.indexHintList;
        if (indexHintList == null) {
            indexHintList = new ArrayList<>();
            this.indexHintList = indexHintList;
        } else if (!(indexHintList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        indexHintList.add(indexHint);
        return (UT) this;
    }

    private Object indexHintEndAndReturnObject(final _IndexHint indexHint) {
        this.indexHintEnd(indexHint);
        return Collections.EMPTY_LIST;
    }


    private static final class MySQLSimpleUpdate<I extends Item, T> extends MySQLSingleUpdates<
            I,
            T,
            _SingleIndexHintSpec<I, T>,
            _SingleWhereSpec<I, T>,
            _OrderBySpec<I>,
            _SingleWhereAndSpec<I>,
            _LimitSpec<I>,
            _DmlUpdateSpec<I>>
            implements MySQLUpdate._SingleIndexHintSpec<I, T>,
            MySQLUpdate._SingleWhereSpec<I, T>,
            MySQLUpdate._SingleWhereAndSpec<I>,
            Update {

        private final Function<? super Update, I> function;

        private MySQLSimpleUpdate(SimpleUpdateClause<I> clause) {
            super(clause);
            this.function = clause.function;
        }


        @Override
        public _SingleWhereClause<I> sets(Consumer<ItemPairs<FieldMeta<T>>> consumer) {
            consumer.accept(CriteriaSupports.itemPairs(this::onAddItemPair));
            return this;
        }

        @Override
        I asMySQLUpdate() {
            return this.function.apply(this);
        }


    }//SimpleUpdate


    private static abstract class UpdateClause<I extends Item, WE> extends CriteriaSupports.WithClause<MySQLCtes, WE> {

        List<Hint> hintList;

        List<MySQLs.Modifier> modifierList;

        TableMeta<?> updateTable;

        List<String> partitionList;

        String tableAlias;

        private UpdateClause(@Nullable MultiStmtSpec spec) {
            super(spec, CriteriaContexts.primarySingleDmlContext(spec));
            ContextStack.push(this.context);
        }

        @Override
        final MySQLCtes createCteBuilder(boolean recursive) {
            return MySQLSupports.mySQLCteBuilder(recursive, this.context);
        }

        final void doUpdate(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers,
                            TableMeta<?> table, String alias) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
            this.tableAlias = alias;
        }

        final void doUpdate(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers, TableMeta<?> table) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            this.updateTable = table;
        }


    }//UpdateClause


    private static final class SimpleUpdateClause<I extends Item>
            extends UpdateClause<I, MySQLUpdate._SingleUpdateClause<I>>
            implements MySQLUpdate._SingleWithSpec<I> {

        private final Function<? super Update, I> function;

        private SimpleUpdateClause(@Nullable MultiStmtSpec spec, Function<? super Update, I> function) {
            super(spec);
            this.function = function;
        }

        @Override
        public MySQLQuery._StaticCteParensSpec<_SingleUpdateClause<I>> with(String name) {
            return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public MySQLQuery._StaticCteParensSpec<_SingleUpdateClause<I>> withRecursive(String name) {
            return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public <T> _SingleIndexHintSpec<I, T> update(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers,
                                                     SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias) {
            this.doUpdate(hints, modifiers, table, alias);
            return new MySQLSimpleUpdate<>(this);
        }

        @Override
        public <P> _SingleIndexHintSpec<I, P> update(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers,
                                                     ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias) {
            this.doUpdate(hints, modifiers, table, alias);
            return new MySQLSimpleUpdate<>(this);
        }

        @Override
        public <T> _SingleIndexHintSpec<I, T> update(SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return new MySQLSimpleUpdate<>(this);
        }

        @Override
        public <P> _SingleIndexHintSpec<I, P> update(ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return new MySQLSimpleUpdate<>(this);
        }

        @Override
        public <T> _SinglePartitionClause<I, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table) {
            this.doUpdate(hints, modifiers, table);
            return new SimplePartitionClause<>(this);
        }

        @Override
        public <P> _SinglePartitionClause<I, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table) {
            this.doUpdate(hints, modifiers, table);
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
            extends MySQLSupports.PartitionAsClause<_SingleIndexHintSpec<I, T>>
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
            return new MySQLSimpleUpdate<>(clause);
        }


    }//SimplePartitionClause


    private static final class MySQLBatchUpdate<T> extends MySQLSingleUpdates<
            BatchUpdate,
            T,
            _BatchSingleIndexHintSpec<BatchUpdate, T>,
            _BatchSingleWhereSpec<BatchUpdate, T>,
            _BatchOrderBySpec<BatchUpdate>,
            _BatchSingleWhereAndSpec<BatchUpdate>,
            _BatchLimitSpec<BatchUpdate>,
            _BatchParamClause<_DmlUpdateSpec<BatchUpdate>>>
            implements MySQLUpdate._BatchSingleIndexHintSpec<BatchUpdate, T>,
            MySQLUpdate._BatchSingleWhereSpec<BatchUpdate, T>,
            MySQLUpdate._BatchSingleWhereAndSpec<BatchUpdate>,
            _DmlUpdateSpec<BatchUpdate>,
            BatchUpdate,
            _BatchDml {

        private List<?> paramList;

        private MySQLBatchUpdate(BatchUpdateClause clause) {
            super(clause);
        }

        @Override
        public _BatchSingleWhereClause<BatchUpdate> sets(Consumer<BatchItemPairs<FieldMeta<T>>> consumer) {
            consumer.accept(CriteriaSupports.batchItemPairs(this::onAddItemPair));
            return this;
        }

        @Override
        public <P> _DmlUpdateSpec<BatchUpdate> paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlUpdateSpec<BatchUpdate> paramList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(this.context, supplier.get());
            return this;
        }

        @Override
        public _DmlUpdateSpec<BatchUpdate> paramList(Function<String, ?> function, String keyName) {
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
        BatchUpdate asMySQLUpdate() {
            if (this.paramList == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return this;
        }


    }//MySQLBatchUpdate


    private static final class BatchUpdateClause
            extends UpdateClause<io.army.criteria.BatchUpdate, MySQLUpdate._BatchSingleUpdateClause<io.army.criteria.BatchUpdate>>
            implements MySQLUpdate._BatchSingleWithSpec<io.army.criteria.BatchUpdate> {

        private BatchUpdateClause() {
            super(null);
        }


        @Override
        public MySQLQuery._StaticCteParensSpec<_BatchSingleUpdateClause<io.army.criteria.BatchUpdate>> with(String name) {
            return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public MySQLQuery._StaticCteParensSpec<_BatchSingleUpdateClause<io.army.criteria.BatchUpdate>> withRecursive(String name) {
            return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public <T> _BatchSingleIndexHintSpec<io.army.criteria.BatchUpdate, T> update(Supplier<List<Hint>> hints,
                                                                                     List<MySQLSyntax.Modifier> modifiers,
                                                                                     SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias) {
            this.doUpdate(hints, modifiers, table, alias);
            return new MySQLBatchUpdate<>(this);
        }

        @Override
        public <P> _BatchSingleIndexHintSpec<io.army.criteria.BatchUpdate, P> update(Supplier<List<Hint>> hints,
                                                                                     List<MySQLSyntax.Modifier> modifiers,
                                                                                     ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs,
                                                                                     String alias) {
            this.doUpdate(hints, modifiers, table, alias);
            return new MySQLBatchUpdate<>(this);
        }

        @Override
        public <T> _BatchSingleIndexHintSpec<io.army.criteria.BatchUpdate, T> update(SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return new MySQLBatchUpdate<>(this);
        }

        @Override
        public <P> _BatchSingleIndexHintSpec<io.army.criteria.BatchUpdate, P> update(ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return new MySQLBatchUpdate<>(this);
        }

        @Override
        public <T> _BatchSinglePartitionClause<io.army.criteria.BatchUpdate, T> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, SingleTableMeta<T> table) {
            this.doUpdate(hints, modifiers, table);
            return new BatchPartitionClause<>(this);
        }

        @Override
        public <P> _BatchSinglePartitionClause<io.army.criteria.BatchUpdate, P> update(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers, ComplexTableMeta<P, ?> table) {
            this.doUpdate(hints, modifiers, table);
            return new BatchPartitionClause<>(this);
        }

        @Override
        public <T> _BatchSinglePartitionClause<io.army.criteria.BatchUpdate, T> update(SingleTableMeta<T> table) {
            this.updateTable = table;
            return new BatchPartitionClause<>(this);
        }

        @Override
        public <P> _BatchSinglePartitionClause<io.army.criteria.BatchUpdate, P> update(ComplexTableMeta<P, ?> table) {
            this.updateTable = table;
            return new BatchPartitionClause<>(this);
        }


    }//BatchUpdateClause

    private static final class BatchPartitionClause<T>
            extends MySQLSupports.PartitionAsClause<_BatchSingleIndexHintSpec<BatchUpdate, T>>
            implements MySQLUpdate._BatchSinglePartitionClause<BatchUpdate, T> {

        private final BatchUpdateClause clause;

        private BatchPartitionClause(BatchUpdateClause clause) {
            super(clause.context, _JoinType.NONE, clause.updateTable);
            assert this.table != null;
            this.clause = clause;
        }

        @Override
        _BatchSingleIndexHintSpec<BatchUpdate, T> asEnd(MySQLSupports.MySQLBlockParams params) {
            final BatchUpdateClause clause = this.clause;
            clause.partitionList = params.partitionList();
            clause.tableAlias = params.alias();
            return new MySQLBatchUpdate<>(clause);
        }


    }//BatchPartitionClause


}
