package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner.mysql._MySQLSingleDelete;
import io.army.criteria.mysql.MySQLCtes;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLQuery;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class hold the implementations of MySQL single-table DELETE syntax
 * </p>
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link MySQLSimpleDelete}</li>
 *         <li>{@link MySQLBatchDelete}</li>
 *     </ul>
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class MySQLSingleDeletes<I extends Item, BI extends Item>
        extends SingleDeleteStatement<
        I,
        BI,
        MySQLCtes,
        MySQLDelete._SimpleSingleDeleteClause<I>,
        MySQLDelete._OrderBySpec<I>,
        MySQLDelete._SingleWhereAndSpec<I>,
        MySQLDelete._OrderByCommaSpec<I>,
        MySQLDelete._LimitSpec<I>,
        Statement._DmlDeleteSpec<I>,
        Object, Object>
        implements MySQLDelete,
        _MySQLSingleDelete,
        BatchDeleteSpec<BI>,
        MySQLDelete._SingleWithSpec<I>,
        DeleteStatement._SingleDeleteFromClause<MySQLDelete._SinglePartitionSpec<I>>,
        MySQLDelete._SinglePartitionSpec<I>,
        MySQLDelete._SingleWhereAndSpec<I>,
        MySQLDelete._OrderByCommaSpec<I> {


    static _SingleWithSpec<Delete> simple() {
        return new MySQLSimpleDelete<>(null, SQLs.SIMPLE_DELETE, SQLs.ERROR_FUNC);
    }

    static _SingleWithSpec<_BatchParamClause<BatchDelete>> batch() {
        return new MySQLSimpleDelete<>(null, SQLs::forBatchDelete, SQLs.BATCH_DELETE);
    }

    private final Function<? super BatchDeleteSpec<BI>, I> function;

    private final Function<? super BatchDelete, BI> batchFunc;

    private List<Hint> hintList;

    private List<MySQLSyntax.Modifier> modifierList;

    private SingleTableMeta<?> deleteTable;

    private String tableAlias;

    private List<String> partitionList;


    private MySQLSingleDeletes(@Nullable ArmyStmtSpec spec, Function<? super BatchDeleteSpec<BI>, I> function,
                               Function<? super BatchDelete, BI> batchFunc) {
        super(spec, CriteriaContexts.primarySingleDmlContext(MySQLUtils.DIALECT, spec));
        this.function = function;
        this.batchFunc = batchFunc;
    }

    @Override
    public final MySQLQuery._StaticCteParensSpec<_SimpleSingleDeleteClause<I>> with(String name) {
        return MySQLQueries.staticCteComma(this.context, false, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final MySQLQuery._StaticCteParensSpec<_SimpleSingleDeleteClause<I>> withRecursive(String name) {
        return MySQLQueries.staticCteComma(this.context, true, this::endStaticWithClause)
                .comma(name);
    }

    @Override
    public final _SinglePartitionSpec<I> deleteFrom(@Nullable SingleTableMeta<?> table, SQLs.WordAs wordAs,
                                                    @Nullable String alias) {
        if (table == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (!_StringUtils.hasText(alias)) {
            throw ContextStack.criteriaError(this.context, _Exceptions::tableAliasIsEmpty);
        } else if (this.deleteTable != null) {
            throw ContextStack.criteriaError(this.context, _Exceptions::castCriteriaApi);
        }
        this.deleteTable = table;
        this.tableAlias = alias;
        return this;
    }

    @Override
    public final _SingleDeleteFromClause<_SinglePartitionSpec<I>> delete(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers) {
        this.hintList = MySQLUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.context, modifiers, MySQLUtils::deleteModifier);
        return this;
    }

    @Override
    public final _SinglePartitionSpec<I> from(SingleTableMeta<?> table, SQLs.WordAs wordAs, String alias) {
        return this.deleteFrom(table, wordAs, alias);
    }

    @Override
    public final _SingleWhereClause<I> partition(String first, String... rest) {
        this.partitionList = ArrayUtils.unmodifiableListOf(first, rest);
        return this;
    }

    @Override
    public final _SingleWhereClause<I> partition(Consumer<Consumer<String>> consumer) {
        this.partitionList = CriteriaUtils.stringList(this.context, true, consumer);
        return this;
    }

    @Override
    public final _SingleWhereClause<I> ifPartition(Consumer<Consumer<String>> consumer) {
        this.partitionList = CriteriaUtils.stringList(this.context, false, consumer);
        return this;
    }

    @Override
    public final List<Hint> hintList() {
        final List<Hint> list = this.hintList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final List<MySQLs.Modifier> modifierList() {
        final List<MySQLs.Modifier> list = this.modifierList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    public final TableMeta<?> table() {
        final SingleTableMeta<?> deleteTable = this.deleteTable;
        if (deleteTable == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return deleteTable;
    }

    @Override
    public final String tableAlias() {
        final String alias = this.tableAlias;
        if (alias == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return alias;
    }


    @Override
    public final List<String> partitionList() {
        final List<String> list = this.partitionList;
        if (list == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }


    @Override
    final I onAsDelete() {
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }
        if (this.partitionList == null) {
            this.partitionList = Collections.emptyList();
        }
        if (this.deleteTable == null || this.tableAlias == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this.function.apply(this);
    }

    @Override
    final BI onAsBatchDelete(List<?> paramList) {
        return this.batchFunc.apply(new MySQLBatchDelete(this, paramList));
    }

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierList = null;
        this.partitionList = null;
    }

    @Override
    final MySQLCtes createCteBuilder(boolean recursive) {
        return MySQLSupports.mysqlLCteBuilder(recursive, this.context);
    }

    @Override
    final Dialect statementDialect() {
        return MySQLUtils.DIALECT;
    }


    private static final class MySQLSimpleDelete<I extends Item, BI extends Item> extends MySQLSingleDeletes<I, BI> {

        private MySQLSimpleDelete(@Nullable ArmyStmtSpec spec, Function<? super BatchDeleteSpec<BI>, I> function,
                                  Function<? super BatchDelete, BI> batchFunc) {
            super(spec, function, batchFunc);
        }


    }//MySQLSimpleDelete


    private static final class MySQLBatchDelete extends ArmyBathDelete
            implements MySQLDelete, BatchDelete, _MySQLSingleDelete {

        private final List<Hint> hintList;

        private final List<MySQLs.Modifier> modifierList;

        private final List<String> partitionList;

        private final List<? extends SortItem> orderByList;

        private final _Expression rowCountExpression;


        private MySQLBatchDelete(MySQLSingleDeletes<?, ?> stmt, List<?> paramList) {
            super(stmt, paramList);
            this.hintList = stmt.hintList;
            this.modifierList = stmt.modifierList;
            this.partitionList = stmt.partitionList;
            this.orderByList = stmt.orderByList();
            this.rowCountExpression = stmt.rowCountExp();

            if (this.hintList == null || this.modifierList == null || this.partitionList == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
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
        public List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public List<? extends SortItem> orderByList() {
            return this.orderByList;
        }

        @Override
        public _Expression rowCountExp() {
            return this.rowCountExpression;
        }


        @Override
        Dialect statementDialect() {
            return MySQLUtils.DIALECT;
        }


    }//MySQLBatchDelete







}
