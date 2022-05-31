package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLMultiDelete;
import io.army.criteria.impl.inner.mysql._MySQLTableBlock;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is the implementation of MySQL 8.0 multi-table delete syntax.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class MySQLMultiDelete<C, WE, DS, DP, JS, JP, WR, WA>
        extends WithCteMultiDelete<C, SubQuery, WE, DS, DS, DP, JS, JS, JP, WR, WA>
        implements _MySQLMultiDelete, MySQLDelete._MultiDeleteClause<C, DS, DP>
        , MySQLDelete._MultiDeleteFromClause<C, DS, DP>, MySQLDelete._MultiDeleteUsingClause<C, DS, DP>
        , _MySQLWithClause {


    static <C> _WithAndMultiDeleteSpec<C> simple(@Nullable C criteria) {
        return new SimpleDelete<>(criteria);
    }

    static <C> _BatchWithAndMultiDeleteSpec<C> batch(@Nullable C criteria) {
        return new BatchDelete<>(criteria);
    }

    private boolean recursive;

    private List<Cte> cteList;

    private List<Hint> hintList;

    private List<MySQLWords> modifierList;

    private boolean usingSyntax;

    private List<String> tableAliasList;

    _OnClause<C, DS> noActionOnClause;

    Object noActionPartitionJoinClause;

    Object noActionPartitionOnClause;

    private MySQLMultiDelete(@Nullable C criteria) {
        super(CriteriaContexts.primaryMultiDmlContext(criteria));
    }


    @Override
    public final _MultiDeleteFromClause<C, DS, DP> delete(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
            , List<String> tableAliasList) {
        if (this.tableAliasList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = MySQLUtils.asHintList(hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isNotDeleteModifier);
        this.tableAliasList = MySQLUtils.asStringList(tableAliasList, this::tableAliasListIsEmpty);
        this.usingSyntax = false;
        return this;
    }

    @Override
    public final _MultiDeleteFromClause<C, DS, DP> delete(Function<C, List<Hint>> hints, List<MySQLWords> modifiers
            , List<String> tableAliasList) {
        if (this.tableAliasList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = MySQLUtils.asHintList(hints.apply(this.criteria), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isNotDeleteModifier);
        this.tableAliasList = MySQLUtils.asStringList(tableAliasList, this::tableAliasListIsEmpty);
        this.usingSyntax = false;
        return this;
    }

    @Override
    public final _MultiDeleteFromClause<C, DS, DP> delete(List<String> tableAliasList) {
        if (this.tableAliasList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.tableAliasList = MySQLUtils.asStringList(tableAliasList, this::tableAliasListIsEmpty);
        this.usingSyntax = false;
        return this;
    }

    @Override
    public final _MultiDeleteFromClause<C, DS, DP> delete(String tableAlias1, String tableAlias2) {
        this.tableAliasList = ArrayUtils.asUnmodifiableList(tableAlias1, tableAlias2);
        this.usingSyntax = false;
        return this;
    }

    @Override
    public final _MultiDeleteUsingClause<C, DS, DP> deleteFrom(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
            , List<String> tableAliasList) {
        if (this.tableAliasList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = MySQLUtils.asHintList(hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isNotDeleteModifier);
        this.tableAliasList = MySQLUtils.asStringList(tableAliasList, this::tableAliasListIsEmpty);
        this.usingSyntax = true;
        return this;
    }

    @Override
    public final _MultiDeleteUsingClause<C, DS, DP> deleteFrom(Function<C, List<Hint>> hints, List<MySQLWords> modifiers
            , List<String> tableAliasList) {
        if (this.tableAliasList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.hintList = MySQLUtils.asHintList(hints.apply(this.criteria), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isNotDeleteModifier);
        this.tableAliasList = MySQLUtils.asStringList(tableAliasList, this::tableAliasListIsEmpty);
        this.usingSyntax = true;
        return this;
    }

    @Override
    public final _MultiDeleteUsingClause<C, DS, DP> deleteFrom(List<String> tableAliasList) {
        if (this.tableAliasList != null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.tableAliasList = MySQLUtils.asStringList(tableAliasList, this::tableAliasListIsEmpty);
        this.usingSyntax = true;
        return this;
    }

    @Override
    public final _MultiDeleteUsingClause<C, DS, DP> deleteFrom(String tableAlias1, String tableAlias2) {
        this.tableAliasList = ArrayUtils.asUnmodifiableList(tableAlias1, tableAlias2);
        this.usingSyntax = true;
        return this;
    }

    @Override
    public final DS from(TableMeta<?> table, String alias) {
        return (DS) this.createAndAddBlock(_JoinType.NONE, table, alias);
    }

    @Override
    public final DP from(TableMeta<?> table) {
        return (DP) this.createClause(_JoinType.NONE, table);
    }


    @Override
    public final <T extends TableItem> DS from(Supplier<T> supplier, String alias) {
        return (DS) this.createAndAddBlock(_JoinType.NONE, supplier.get(), alias);
    }

    @Override
    public final <T extends TableItem> DS from(Function<C, T> function, String alias) {
        return (DS) this.createAndAddBlock(_JoinType.NONE, function.apply(this.criteria), alias);
    }

    @Override
    public final DS from(String cteName) {
        return (DS) this.createAndAddBlock(_JoinType.NONE, this.criteriaContext.refCte(cteName), "");
    }

    @Override
    public final DS from(String cteName, String alias) {
        return (DS) this.createAndAddBlock(_JoinType.NONE, this.criteriaContext.refCte(cteName), alias);
    }

    @Override
    public final DS using(TableMeta<?> table, String alias) {
        return this.from(table, alias);
    }

    @Override
    public final DP using(TableMeta<?> table) {
        return this.from(table);
    }

    @Override
    public final <T extends TableItem> DS using(Supplier<T> supplier, String alias) {
        return this.from(supplier, alias);
    }

    @Override
    public final <T extends TableItem> DS using(Function<C, T> function, String alias) {
        return this.from(function, alias);
    }

    @Override
    public final DS using(String cteName) {
        return this.from(cteName);
    }

    @Override
    public final DS using(String cteName, String alias) {
        return this.from(cteName, alias);
    }


    @Override
    public final boolean isRecursive() {
        return this.recursive;
    }

    @Override
    public final List<Cte> cteList() {
        return this.cteList;
    }

    @Override
    public final List<Hint> hintList() {
        return this.hintList;
    }

    @Override
    public final List<MySQLWords> modifierList() {
        return this.modifierList;
    }

    @Override
    public final boolean usingSyntax() {
        return this.usingSyntax;
    }

    @Override
    public final List<String> tableAliasList() {
        return this.tableAliasList;
    }


    @Override
    public final _TableBlock createAndAddBlock(final _JoinType joinType, final TableItem item, final String alias) {
        final _TableBlock block;
        switch (joinType) {
            case NONE:
            case CROSS_JOIN:
                block = new TableBlock.NoOnTableBlock(joinType, item, alias);
                break;
            case LEFT_JOIN:
            case JOIN:
            case RIGHT_JOIN:
            case FULL_JOIN:
            case STRAIGHT_JOIN:
                block = new OnClauseTableBlock<>(joinType, item, alias, this);
                break;
            default:
                throw _Exceptions.unexpectedEnum(joinType);
        }
        this.criteriaContext.onAddBlock(block);
        return block;
    }

    @Override
    public final Object createClause(final _JoinType joinType, final TableMeta<?> table) {
        final Object clause;
        switch (joinType) {
            case NONE:
            case CROSS_JOIN: {
                if (this instanceof SimpleDelete) {
                    clause = new SimplePartitionJoinClause<>(joinType, table, (SimpleDelete<C>) this);
                } else if (this instanceof BatchDelete) {
                    clause = new BatchPartitionJoinClause<>(joinType, table, (BatchDelete<C>) this);
                } else {
                    throw new IllegalStateException();
                }
            }
            break;
            case LEFT_JOIN:
            case JOIN:
            case RIGHT_JOIN:
            case FULL_JOIN:
            case STRAIGHT_JOIN: {
                if (this instanceof SimpleDelete) {
                    clause = new SimplePartitionOnClause<>(joinType, table, (SimpleDelete<C>) this);
                } else if (this instanceof BatchDelete) {
                    clause = new BatchPartitionOnClause<>(joinType, table, (BatchDelete<C>) this);
                } else {
                    throw new IllegalStateException();
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(joinType);
        }
        return clause;
    }

    @Override
    public final Object getNoActionClause(final _JoinType joinType) {
        final Object noActionClause;
        switch (joinType) {
            case NONE:
            case CROSS_JOIN:
                noActionClause = this;
                break;
            case LEFT_JOIN:
            case JOIN:
            case RIGHT_JOIN:
            case FULL_JOIN:
            case STRAIGHT_JOIN:
                noActionClause = this.getNoActionOnClause();
                break;
            default:
                throw _Exceptions.unexpectedEnum(joinType);
        }
        return noActionClause;
    }

    @Override
    public final Object getNoActionClauseBeforeAs(final _JoinType joinType) {
        final Object noActionClause;
        switch (joinType) {
            case NONE:
            case CROSS_JOIN: {
                Object clause = this.noActionPartitionJoinClause;
                if (clause == null) {
                    if (this instanceof SimpleDelete) {
                        clause = new SimpleNoActionPartitionJoinClause<>((_MultiJoinSpec<C>) this);
                    } else if (this instanceof BatchDelete) {
                        clause = new BatchNoActionPartitionJoinClause<>((_BatchMultiJoinSpec<C>) this);
                    } else {
                        throw new IllegalStateException();
                    }
                    this.noActionPartitionJoinClause = clause;
                }
                noActionClause = clause;
            }
            break;
            case LEFT_JOIN:
            case JOIN:
            case RIGHT_JOIN:
            case FULL_JOIN:
            case STRAIGHT_JOIN: {
                Object clause = this.noActionPartitionOnClause;
                if (clause == null) {
                    if (this instanceof SimpleDelete) {
                        clause = new SimpleNoActionPartitionOnClause<>(this::getNoActionOnClause);
                    } else if (this instanceof BatchDelete) {
                        clause = new BatchNoActionPartitionOnBlock<>(this::getNoActionOnClause);
                    } else {
                        throw new IllegalStateException();
                    }
                    this.noActionPartitionOnClause = clause;
                }
                noActionClause = clause;
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(joinType);
        }
        return noActionClause;
    }


    @Override
    final void validateBeforeClearContext() {
        final List<String> tableAliasList = this.tableAliasList;
        if (tableAliasList == null) {
            throw _Exceptions.castCriteriaApi();
        }
        Map<ParentTableMeta<?>, Boolean> parentMap = null;
        List<ChildTableMeta<?>> childList = null;
        TableMeta<?> table;
        for (String tableAlias : tableAliasList) {
            table = this.criteriaContext.getTable(tableAlias);
            if (table == null) {
                throw _Exceptions.unknownTableAlias(tableAlias);
            }
            if (table instanceof ParentTableMeta) {
                if (parentMap == null) {
                    parentMap = new HashMap<>();
                }
                parentMap.putIfAbsent((ParentTableMeta<?>) table, Boolean.TRUE);
            } else if (table instanceof ChildTableMeta) {
                if (childList == null) {
                    childList = new ArrayList<>();
                }
                childList.add((ChildTableMeta<?>) table);
            }

        }

        if (childList != null) {
            for (ChildTableMeta<?> child : childList) {
                if (parentMap == null || !parentMap.containsKey(child.parentMeta())) {
                    throw _Exceptions.deleteChildButNoParent(child);
                }
            }
            childList.clear();
        }

        if (parentMap != null) {
            parentMap.clear();
        }

    }

    @Override
    final void onAsDelete() {
        if (this.cteList == null) {
            this.cteList = Collections.emptyList();
        }
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }
        final List<String> tableAliasList = this.tableAliasList;
        if (tableAliasList == null || tableAliasList.size() == 0) {
            throw new CriteriaException("tableAliasList must not empty in multi-table delete clause.");
        }
        this.noActionOnClause = null;
        this.noActionPartitionJoinClause = null;
        this.noActionPartitionOnClause = null;
        if (this instanceof BatchDelete && ((BatchDelete<C>) this).paramList == null) {
            throw _Exceptions.batchParamEmpty();
        }
    }

    @Override
    final void onClear() {
        this.cteList = null;
        this.hintList = null;
        this.modifierList = null;
        this.tableAliasList = null;

        if (this instanceof BatchDelete) {
            ((BatchDelete<C>) this).paramList = null;
        }

    }

    @Override
    final void crossJoinEvent(boolean success) {
        //no-op
    }


    final _OnClause<C, DS> getNoActionOnClause() {
        _OnClause<C, DS> clause = this.noActionOnClause;
        if (clause == null) {
            clause = new NoActionOnClause<>((DS) this);
            this.noActionOnClause = clause;
        }
        return clause;
    }

    @Override
    final void doWithCte(boolean recursive, List<Cte> cteList) {
        this.recursive = recursive;
        this.cteList = cteList;
    }

    @Override
    final Dialect defaultDialect() {
        return MySQLUtils.defaultDialect(this);
    }

    @Override
    final void validateDialect(Dialect dialect) {
        MySQLUtils.validateDialect(this, dialect);
    }


    private CriteriaException tableAliasListIsEmpty() {
        return new CriteriaException("table alias list must not empty");
    }



    /*################################## blow inner class ##################################*/


    private static final class SimpleDelete<C> extends MySQLMultiDelete<
            C,
            _MultiDelete57Clause<C>,
            MySQLDelete._MultiJoinSpec<C>,
            MySQLDelete._MultiPartitionJoinClause<C>,
            Statement._OnClause<C, MySQLDelete._MultiJoinSpec<C>>,
            MySQLDelete._MultiPartitionOnClause<C>,
            _DeleteSpec,
            MySQLDelete._MultiWhereAndSpec<C>>
            implements MySQLDelete._WithAndMultiDeleteSpec<C>, MySQLDelete._MultiJoinSpec<C>
            , MySQLDelete._MultiWhereAndSpec<C> {

        private SimpleDelete(@Nullable C criteria) {
            super(criteria);
        }


    }//SimpleDelete

    private static final class BatchDelete<C> extends MySQLMultiDelete<
            C,
            MySQLDelete._BatchMultiDeleteClause<C>,
            MySQLDelete._BatchMultiJoinSpec<C>,
            MySQLDelete._BatchMultiPartitionJoinClause<C>,
            Statement._OnClause<C, MySQLDelete._BatchMultiJoinSpec<C>>,
            MySQLDelete._BatchMultiPartitionOnClause<C>,
            Statement._BatchParamClause<C, Delete._DeleteSpec>,
            MySQLDelete._BatchMultiWhereAndSpec<C>>
            implements MySQLDelete._BatchWithAndMultiDeleteSpec<C>, MySQLDelete._BatchMultiJoinSpec<C>
            , MySQLDelete._BatchMultiWhereAndSpec<C>, Statement._BatchParamClause<C, Delete._DeleteSpec>, _BatchDml {


        private List<?> paramList;

        private BatchDelete(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public <P> _DeleteSpec paramList(List<P> paramList) {
            this.paramList = MySQLUtils.paramList(paramList);
            return this;
        }

        @Override
        public <P> _DeleteSpec paramList(Supplier<List<P>> supplier) {
            this.paramList = MySQLUtils.paramList(supplier.get());
            return this;
        }

        @Override
        public <P> _DeleteSpec paramList(Function<C, List<P>> function) {
            this.paramList = MySQLUtils.paramList(function.apply(this.criteria));
            return this;
        }

        @Override
        public _DeleteSpec paramList(Function<String, ?> function, String keyName) {
            this.paramList = MySQLUtils.paramList((List<?>) function.apply(keyName));
            return this;
        }

        @Override
        public List<?> paramList() {
            return this.paramList;
        }

    }//SimpleDelete


    private static final class SimplePartitionJoinClause<C>
            extends MySQLPartitionClause<C, _AsClause<_MultiJoinSpec<C>>>
            implements _MultiPartitionJoinClause<C>, _AsClause<_MultiJoinSpec<C>> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final SimpleDelete<C> stmt;

        private SimplePartitionJoinClause(_JoinType joinType, TableMeta<?> table, SimpleDelete<C> stmt) {
            super(stmt.criteria);
            this.joinType = joinType;
            this.table = table;
            this.stmt = stmt;
        }

        @Override
        public _MultiJoinSpec<C> as(final String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final TableBlock block;
            if (partitionList == null) {
                block = new TableBlock.NoOnTableBlock(this.joinType, table, alias);
            } else {
                block = new MySQLNoOnBlock(this.joinType, this.table, alias, partitionList);
            }
            this.stmt.criteriaContext.onAddBlock(block);
            return this.stmt;
        }

    }// SimplePartitionJoinClause


    private static final class SimplePartitionOnClause<C>
            extends MySQLPartitionClause<C, _AsClause<_OnClause<C, _MultiJoinSpec<C>>>>
            implements _MultiPartitionOnClause<C>, _AsClause<_OnClause<C, _MultiJoinSpec<C>>> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final SimpleDelete<C> stmt;

        private SimplePartitionOnClause(_JoinType joinType, TableMeta<?> table, SimpleDelete<C> stmt) {
            super(stmt.criteria);
            this.joinType = joinType;
            this.table = table;
            this.stmt = stmt;
        }

        @Override
        public _OnClause<C, _MultiJoinSpec<C>> as(final String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final OnClauseTableBlock<C, _MultiJoinSpec<C>> block;
            if (partitionList == null) {
                block = new OnClauseTableBlock<>(this.joinType, this.table, alias, this.stmt);
            } else {
                block = new PartitionOnBlock<>(this.joinType, this.table, alias, partitionList, this.stmt);
            }
            this.stmt.criteriaContext.onAddBlock(block);
            return block;
        }


    }// SimplePartitionOnClause


    private static final class BatchPartitionJoinClause<C>
            extends MySQLPartitionClause<C, _AsClause<_BatchMultiJoinSpec<C>>>
            implements _AsClause<_BatchMultiJoinSpec<C>>, _BatchMultiPartitionJoinClause<C> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final BatchDelete<C> stmt;

        private BatchPartitionJoinClause(_JoinType joinType, TableMeta<?> table, BatchDelete<C> stmt) {
            super(stmt.criteria);
            this.joinType = joinType;
            this.table = table;
            this.stmt = stmt;
        }

        @Override
        public _BatchMultiJoinSpec<C> as(String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final TableBlock block;
            if (partitionList == null) {
                block = new TableBlock.NoOnTableBlock(this.joinType, this.table, alias);
            } else {
                block = new MySQLNoOnBlock(this.joinType, table, alias, partitionList);
            }
            this.stmt.criteriaContext.onAddBlock(block);
            return this.stmt;
        }
    }// BatchPartitionJoinClause


    private static final class BatchPartitionOnClause<C>
            extends MySQLPartitionClause<C, _AsClause<_OnClause<C, _BatchMultiJoinSpec<C>>>>
            implements _AsClause<_OnClause<C, _BatchMultiJoinSpec<C>>>, _BatchMultiPartitionOnClause<C> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final BatchDelete<C> stmt;

        private BatchPartitionOnClause(_JoinType joinType, TableMeta<?> table, BatchDelete<C> stmt) {
            super(stmt.criteria);
            this.joinType = joinType;
            this.table = table;
            this.stmt = stmt;
        }

        @Override
        public _OnClause<C, _BatchMultiJoinSpec<C>> as(final String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final OnClauseTableBlock<C, _BatchMultiJoinSpec<C>> block;
            if (partitionList == null) {
                block = new OnClauseTableBlock<>(this.joinType, this.table, alias, this.stmt);
            } else {
                block = new PartitionOnBlock<>(this.joinType, this.table, alias, partitionList, this.stmt);
            }
            this.stmt.criteriaContext.onAddBlock(block);
            return block;
        }


    }// BatchPartitionBlock


    private static final class BatchNoActionPartitionJoinClause<C>
            extends MySQLNoActionPartitionClause<C, _AsClause<_BatchMultiJoinSpec<C>>>
            implements _AsClause<_BatchMultiJoinSpec<C>>, _BatchMultiPartitionJoinClause<C> {

        private final _BatchMultiJoinSpec<C> stmt;

        private BatchNoActionPartitionJoinClause(_BatchMultiJoinSpec<C> stmt) {
            this.stmt = stmt;
        }

        @Override
        public _BatchMultiJoinSpec<C> as(String alias) {
            return this.stmt;
        }

    }// BatchNoActionPartitionJoinClause


    private static final class BatchNoActionPartitionOnBlock<C>
            extends MySQLNoActionPartitionClause<C, _AsClause<_OnClause<C, _BatchMultiJoinSpec<C>>>>
            implements _AsClause<_OnClause<C, _BatchMultiJoinSpec<C>>>, _BatchMultiPartitionOnClause<C> {

        private final Supplier<_OnClause<C, ?>> supplier;

        private BatchNoActionPartitionOnBlock(Supplier<_OnClause<C, ?>> supplier) {
            this.supplier = supplier;
        }

        @Override
        public _OnClause<C, _BatchMultiJoinSpec<C>> as(String alias) {
            return (_OnClause<C, _BatchMultiJoinSpec<C>>) this.supplier.get();
        }

    }// BatchNoActionPartitionOnBlock

    private static final class SimpleNoActionPartitionJoinClause<C>
            extends MySQLNoActionPartitionClause<C, _AsClause<_MultiJoinSpec<C>>>
            implements _AsClause<_MultiJoinSpec<C>>, _MultiPartitionJoinClause<C> {

        private final _MultiJoinSpec<C> stmt;

        private SimpleNoActionPartitionJoinClause(_MultiJoinSpec<C> stmt) {
            this.stmt = stmt;
        }

        @Override
        public _MultiJoinSpec<C> as(String alias) {
            return this.stmt;
        }

    }// SimpleNoActionPartitionJoinClause

    private static final class SimpleNoActionPartitionOnClause<C>
            extends MySQLNoActionPartitionClause<C, _AsClause<_OnClause<C, _MultiJoinSpec<C>>>>
            implements _AsClause<_OnClause<C, _MultiJoinSpec<C>>>, _MultiPartitionOnClause<C> {

        private final Supplier<_OnClause<C, ?>> supplier;

        private SimpleNoActionPartitionOnClause(Supplier<_OnClause<C, ?>> supplier) {
            this.supplier = supplier;
        }

        @Override
        public _OnClause<C, _MultiJoinSpec<C>> as(String alias) {
            return (_OnClause<C, _MultiJoinSpec<C>>) this.supplier.get();
        }

    }//SimpleNoActionPartitionOnClause


    private static final class PartitionOnBlock<C, OR> extends OnClauseTableBlock<C, OR>
            implements _MySQLTableBlock {

        private final List<String> partitionList;

        private PartitionOnBlock(_JoinType joinType, TableItem tableItem
                , String alias, List<String> partitionList
                , OR stmt) {
            super(joinType, tableItem, alias, stmt);
            this.partitionList = partitionList;
        }

        @Override
        public List<String> partitionList() {
            return this.partitionList;
        }

        @Override
        public List<? extends _IndexHint> indexHintList() {
            return Collections.emptyList();
        }

    }//PartitionOnBlock


}
