package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLSingleUpdate;
import io.army.criteria.mysql.MySQLCtes;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.util._Collections;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is an implementation of single-table {@link MySQLUpdate}
 * </p>
 */

abstract class MySQLSingleUpdates<I extends Item, BI extends Item, T>
        extends SingleUpdateStatement<
        I,
        BI,
        FieldMeta<T>,
        MySQLUpdate._SingleWhereSpec<I, T>,
        MySQLUpdate._OrderBySpec<I>,
        MySQLUpdate._SingleWhereAndSpec<I>,
        MySQLUpdate._OrderByCommaSpec<I>,
        MySQLUpdate._LimitSpec<I>,
        Statement._DmlUpdateSpec<I>,
        Object, Object>
        implements _MySQLSingleUpdate,
        MySQLUpdate,
        BatchUpdateSpec<BI>,
        Update,
        MySQLUpdate._SingleIndexHintSpec<I, T>,
        MySQLUpdate._SingleWhereSpec<I, T>,
        MySQLUpdate._SingleWhereAndSpec<I>,
        MySQLUpdate._OrderByCommaSpec<I> {

    /**
     * <p>
     * create simple(non-batch) single-table UPDATE statement that is primary statement.
     * </p>
     */
    static _SingleWithSpec<Update> simple() {
        return new SimpleUpdateClause<>(null, SQLs.SIMPLE_UPDATE, SQLs.ERROR_FUNC);
    }

    /**
     * <p>
     * create batch single-table UPDATE statement that is primary statement.
     * </p>
     */
    static _SingleWithSpec<_BatchParamClause<BatchUpdate>> batch() {
        return new SimpleUpdateClause<>(null, SQLs::forBatchUpdate, SQLs.BATCH_UPDATE);
    }

    private final Function<? super BatchUpdateSpec<BI>, I> function;

    private final Function<? super BatchUpdate, BI> batchFunc;

    private final boolean recursive;

    private final List<_Cte> cteList;

    private final List<Hint> hintList;

    private final List<MySQLs.Modifier> modifierList;

    private final List<String> partitionList;

    private List<_IndexHint> indexHintList;

    private MySQLSingleUpdates(SimpleUpdateClause<I, BI> clause) {
        super(clause.context, clause.updateTable, clause.tableAlias);

        this.function = clause.function;
        this.batchFunc = clause.batchFunc;
        this.recursive = clause.isRecursive();
        this.cteList = clause.cteList();

        this.hintList = _Collections.safeList(clause.hintList);
        this.modifierList = _Collections.safeList(clause.modifierList);

        this.partitionList = _Collections.safeList(clause.partitionList);

    }

    @Override
    public final _SingleWhereClause<I> sets(Consumer<_BatchItemPairs<FieldMeta<T>>> consumer) {
        consumer.accept(CriteriaSupports.batchItemPairs(this::onAddItemPair));
        return this;
    }


    /*################################## blow IndexHintClause method ##################################*/


    @Override
    public final _IndexForOrderBySpec<_SingleIndexHintSpec<I, T>> useIndex() {
        return MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.USE_INDEX,
                this::indexHintEnd);
    }

    @Override
    public final _IndexForOrderBySpec<_SingleIndexHintSpec<I, T>> ignoreIndex() {
        return MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.IGNORE_INDEX,
                this::indexHintEnd);
    }

    @Override
    public final _IndexForOrderBySpec<_SingleIndexHintSpec<I, T>> forceIndex() {
        return MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.FORCE_INDEX,
                this::indexHintEnd);
    }


    @Override
    public final _SingleIndexHintSpec<I, T> ifUseIndex(Consumer<_IndexForOrderBySpec<Object>> consumer) {
        consumer.accept(MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.USE_INDEX,
                this::indexHintEnd));
        return this;
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ifIgnoreIndex(Consumer<_IndexForOrderBySpec<Object>> consumer) {
        consumer.accept(MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.IGNORE_INDEX,
                this::indexHintEnd));
        return this;
    }

    @Override
    public final _SingleIndexHintSpec<I, T> ifForceIndex(Consumer<_IndexForOrderBySpec<Object>> consumer) {
        consumer.accept(MySQLSupports.indexHintClause(this.context, MySQLSupports.IndexHintCommand.FORCE_INDEX,
                this::indexHintEnd));
        return this;
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
        this.indexHintList = _Collections.safeUnmodifiableList(this.indexHintList);
        return this.function.apply(this);
    }

    @Override
    final BI onAsBatchUpdate(List<?> paramList) {
        return this.batchFunc.apply(new MySQLBatchSingleUpdate(this, paramList));
    }

    @Override
    final void onClear() {
        this.indexHintList = null;

    }

    @Override
    final Dialect statementDialect() {
        return MySQLUtils.DIALECT;
    }


    private MySQLUpdate._SingleIndexHintSpec<I, T> indexHintEnd(final _IndexHint indexHint) {

        List<_IndexHint> indexHintList = this.indexHintList;
        if (indexHintList == null) {
            indexHintList = _Collections.arrayList();
            this.indexHintList = indexHintList;
        } else if (!(indexHintList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        indexHintList.add(indexHint);
        return this;
    }


    private static final class MySQLSimpleUpdate<I extends Item, BI extends Item, T>
            extends MySQLSingleUpdates<I, BI, T> {


        private MySQLSimpleUpdate(SimpleUpdateClause<I, BI> clause) {
            super(clause);

        }


    }//SimpleUpdate


    private static final class SimpleUpdateClause<I extends Item, BI extends Item>
            extends CriteriaSupports.WithClause<MySQLCtes, _SingleUpdateClause<I>>
            implements MySQLUpdate._SingleWithSpec<I>,
            _SingleUpdateSpaceClause<I> {

        private final Function<? super BatchUpdateSpec<BI>, I> function;

        private final Function<? super BatchUpdate, BI> batchFunc;

        private List<Hint> hintList;

        private List<MySQLs.Modifier> modifierList;

        private TableMeta<?> updateTable;

        private List<String> partitionList;

        private String tableAlias;

        private SimpleUpdateClause(@Nullable ArmyStmtSpec spec, Function<? super BatchUpdateSpec<BI>, I> function,
                                   Function<? super BatchUpdate, BI> batchFunc) {
            super(spec, CriteriaContexts.primarySingleDmlContext(MySQLUtils.DIALECT, spec));
            ContextStack.push(this.context);
            this.function = function;
            this.batchFunc = batchFunc;
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
        public _SingleUpdateSpaceClause<I> update(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return this;
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
        public <T> _SinglePartitionClause<I, T> update(SingleTableMeta<T> table) {
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        public <P> _SinglePartitionClause<I, P> update(ComplexTableMeta<P, ?> table) {
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        public <T> _SingleIndexHintSpec<I, T> space(SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return new MySQLSimpleUpdate<>(this);
        }

        @Override
        public <P> _SingleIndexHintSpec<I, P> space(ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return new MySQLSimpleUpdate<>(this);
        }

        @Override
        public <T> _SinglePartitionClause<I, T> space(SingleTableMeta<T> table) {
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        public <P> _SinglePartitionClause<I, P> space(ComplexTableMeta<P, ?> table) {
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        MySQLCtes createCteBuilder(boolean recursive) {
            return MySQLSupports.mysqlLCteBuilder(recursive, this.context);
        }

    }//SimpleUpdateClause

    private static final class SimplePartitionClause<I extends Item, BI extends Item, T>
            extends MySQLSupports.PartitionAsClause<_SingleIndexHintSpec<I, T>>
            implements MySQLUpdate._SinglePartitionClause<I, T> {

        private final SimpleUpdateClause<I, BI> clause;

        private SimplePartitionClause(SimpleUpdateClause<I, BI> clause) {
            super(clause.context, _JoinType.NONE, clause.updateTable);
            if (this.table == null) {
                throw ContextStack.nullPointer(clause.context);
            }
            this.clause = clause;
        }

        @Override
        _SingleIndexHintSpec<I, T> asEnd(MySQLSupports.MySQLBlockParams params) {
            final SimpleUpdateClause<I, BI> clause = this.clause;
            clause.partitionList = params.partitionList();
            clause.tableAlias = params.alias();
            return new MySQLSimpleUpdate<>(clause);
        }


    }//SimplePartitionClause


    private static final class MySQLBatchSingleUpdate extends ArmySingleBathUpdate
            implements MySQLUpdate,
            BatchUpdate,
            _MySQLSingleUpdate {

        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private final List<String> partitionList;

        private final List<_IndexHint> indexHintList;

        private final List<? extends SortItem> orderByList;

        private final _Expression rowCountExpression;


        private MySQLBatchSingleUpdate(MySQLSingleUpdates<?, ?, ?> clause, List<?> paramList) {
            super(clause, paramList);

            this.hintList = clause.hintList;
            this.modifierList = clause.modifierList;
            this.partitionList = clause.partitionList;
            this.indexHintList = clause.indexHintList;

            assert indexHintList != null;
            this.orderByList = clause.orderByList();
            this.rowCountExpression = clause.rowCountExp();

        }

        @Override
        public _Expression rowCountExp() {
            return this.rowCountExpression;
        }

        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public List<? extends _IndexHint> indexHintList() {
            return this.indexHintList;
        }

        @Override
        public List<? extends SortItem> orderByList() {
            return this.orderByList;
        }

        @Override
        public List<Hint> hintList() {
            return this.hintList;
        }

        @Override
        public List<MySQLs.Modifier> modifierList() {
            return this.modifierList;
        }


        @Override
        Dialect statementDialect() {
            return MySQLUtils.DIALECT;
        }


    }//MySQLBatchSingleUpdate


}
