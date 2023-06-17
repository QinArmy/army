package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._BatchStatement;
import io.army.criteria.impl.inner._Cte;
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
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 * This class is an implementation of single-table {@link MySQLUpdate}
 * </p>
 */

abstract class MySQLSingleUpdates<I extends Item, T>
        extends SingleUpdateStatement<
        I,
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
        return new SimpleUpdateClause();
    }

    /**
     * <p>
     * create batch single-table UPDATE statement that is primary statement.
     * </p>
     */
    static _SingleWithSpec<_BatchUpdateParamSpec> batch() {
        return new BatchUpdateClause();
    }


    private final boolean recursive;

    private final List<_Cte> cteList;

    private final List<Hint> hintList;

    private final List<MySQLs.Modifier> modifierList;

    private final List<String> partitionList;

    private List<_IndexHint> indexHintList;

    private MySQLSingleUpdates(MySQLUpdateClause<?> clause) {
        super(clause.context, clause.updateTable, clause.tableAlias);

        this.recursive = clause.isRecursive();
        this.cteList = clause.cteList();
        this.hintList = _Collections.safeList(clause.hintList);
        this.modifierList = _Collections.safeList(clause.modifierList);

        this.partitionList = _Collections.safeList(clause.partitionList);

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
    public final List<String> partitionList() {
        return this.partitionList;
    }

    @Override
    public final _SingleWhereClause<I> sets(Consumer<UpdateStatement._BatchItemPairs<FieldMeta<T>>> consumer) {
        consumer.accept(CriteriaSupports.batchItemPairs(this::onAddItemPair));
        return this;
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
        return this.onAsMySQLUpdate();
    }


    abstract I onAsMySQLUpdate();

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


    private static final class MySQLSimpleUpdate<T> extends MySQLSingleUpdates<Update, T>
            implements Update {

        private MySQLSimpleUpdate(SimpleUpdateClause clause) {
            super(clause);
        }

        @Override
        Update onAsMySQLUpdate() {
            return this;
        }


    }//MySQLSimpleUpdate

    private static final class MySQLBatchUpdate<T> extends MySQLSingleUpdates<_BatchUpdateParamSpec, T>
            implements BatchUpdate, _BatchUpdateParamSpec, _BatchStatement {

        private List<?> paramList;

        private MySQLBatchUpdate(BatchUpdateClause clause) {
            super(clause);
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
        _BatchUpdateParamSpec onAsMySQLUpdate() {
            return this;
        }


    }//MySQLSimpleUpdate

    private static abstract class MySQLUpdateClause<I extends Item>
            extends CriteriaSupports.WithClause<MySQLCtes, _SingleUpdateClause<I>>
            implements MySQLUpdate._SingleWithSpec<I>,
            _SingleUpdateSpaceClause<I> {

        private List<Hint> hintList;

        private List<MySQLs.Modifier> modifierList;

        private TableMeta<?> updateTable;

        private List<String> partitionList;

        private String tableAlias;

        private MySQLUpdateClause(@Nullable ArmyStmtSpec spec) {
            super(spec, CriteriaContexts.primarySingleDmlContext(MySQLUtils.DIALECT, null));
            ContextStack.push(this.context);
        }

        @Override
        public final MySQLQuery._StaticCteParensSpec<_SingleUpdateClause<I>> with(String name) {
            return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public final MySQLQuery._StaticCteParensSpec<_SingleUpdateClause<I>> withRecursive(String name) {
            return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                    .comma(name);
        }

        @Override
        public final _SingleUpdateSpaceClause<I> update(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers) {
            this.hintList = CriteriaUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
            this.modifierList = CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::updateModifier);
            return this;
        }

        @Override
        public final <T> _SingleIndexHintSpec<I, T> update(SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return this.createUpdateStmt();
        }

        @Override
        public final <P> _SingleIndexHintSpec<I, P> update(ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return this.createUpdateStmt();
        }

        @Override
        public final <T> _SinglePartitionClause<I, T> update(SingleTableMeta<T> table) {
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        public final <P> _SinglePartitionClause<I, P> update(ComplexTableMeta<P, ?> table) {
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        public final <T> _SingleIndexHintSpec<I, T> space(SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return this.createUpdateStmt();
        }

        @Override
        public final <P> _SingleIndexHintSpec<I, P> space(ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias) {
            this.updateTable = table;
            this.tableAlias = alias;
            return this.createUpdateStmt();
        }

        @Override
        public final <T> _SinglePartitionClause<I, T> space(SingleTableMeta<T> table) {
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        public final <P> _SinglePartitionClause<I, P> space(ComplexTableMeta<P, ?> table) {
            this.updateTable = table;
            return new SimplePartitionClause<>(this);
        }

        @Override
        final MySQLCtes createCteBuilder(boolean recursive) {
            return MySQLSupports.mysqlLCteBuilder(recursive, this.context);
        }

        abstract <T> _SingleIndexHintSpec<I, T> createUpdateStmt();


    }//PostgreUpdateClause

    private static final class SimpleUpdateClause extends MySQLUpdateClause<Update> {

        private SimpleUpdateClause() {
            super(null);
        }

        @Override
        <T> _SingleIndexHintSpec<Update, T> createUpdateStmt() {
            return new MySQLSimpleUpdate<>(this);
        }


    }//SimpleUpdateClause

    private static final class BatchUpdateClause extends MySQLUpdateClause<_BatchUpdateParamSpec> {

        private BatchUpdateClause() {
            super(null);
        }

        @Override
        <T> _SingleIndexHintSpec<_BatchUpdateParamSpec, T> createUpdateStmt() {
            return new MySQLBatchUpdate<>(this);
        }


    }//BatchUpdateClause


    private static final class SimplePartitionClause<I extends Item, T>
            extends MySQLSupports.PartitionAsClause<_SingleIndexHintSpec<I, T>>
            implements MySQLUpdate._SinglePartitionClause<I, T> {

        private final MySQLUpdateClause<I> clause;

        private SimplePartitionClause(MySQLUpdateClause<I> clause) {
            super(clause.context, _JoinType.NONE, clause.updateTable);
            if (this.table == null) {
                throw ContextStack.nullPointer(clause.context);
            }
            this.clause = clause;
        }

        @Override
        _SingleIndexHintSpec<I, T> asEnd(MySQLSupports.MySQLBlockParams params) {
            final MySQLUpdateClause<I> clause = this.clause;
            clause.partitionList = params.partitionList();
            clause.tableAlias = params.alias();
            return clause.createUpdateStmt();
        }


    }//SimplePartitionClause


}
