package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner.mysql._MySQLSingleDelete;
import io.army.criteria.mysql.MySQLCteBuilder;
import io.army.criteria.mysql.MySQLDelete;
import io.army.criteria.mysql.MySQLQuery;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.SingleTableMeta;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class hold the implementations of MySQL single-table DELETE syntax
 * </p>
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link MySQLSingleDelete.SimpleDeleteStatement}</li>
 *         <li>{@link MySQLSingleDelete.BatchDeleteStatement}</li>
 *     </ul>
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class MySQLSingleDelete<I extends Item, WE, DT, PR, WR, WA, OR, LR>
        extends SingleDelete.WithSingleDelete<I, Item, MySQLCteBuilder, WE, WR, WA, OR, LR, Object, Object>
        implements MySQLDelete, _MySQLSingleDelete, Delete
        , MySQLDelete._SingleDeleteClause<DT>
        , MySQLQuery._PartitionClause<PR>
        , Delete._SingleDeleteFromClause<DT> {


    static <I extends Item> _SingleWithSpec<I> simple(Function<Delete, I> function) {
        return new SimpleDeleteStatement<>(function);
    }

    static <I extends Item> _BatchSingleWithSpec<I> batch(Function<Delete, I> function) {
        return new BatchDeleteStatement<>(function);
    }

    private final Function<Delete, I> function;

    private List<Hint> hintList;

    private List<MySQLSyntax.Modifier> modifierList;

    private SingleTableMeta<?> deleteTable;

    private String tableAlias;

    private List<String> partitionList;


    private MySQLSingleDelete(Function<Delete, I> function) {
        super(CriteriaContexts.primarySingleDmlContext());
        this.function = function;
    }

    @Override
    public final DT deleteFrom(final @Nullable SingleTableMeta<?> table, SQLs.WordAs wordAs
            , final @Nullable String tableAlias) {
        assert wordAs == SQLs.AS;
        if (this.deleteTable != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        if (table == null || tableAlias == null) {
            throw ContextStack.nullPointer(this.context);
        }
        this.deleteTable = table;
        this.tableAlias = tableAlias;
        return (DT) this;
    }

    @Override
    public final _SingleDeleteFromClause<DT> delete(Supplier<List<Hint>> hints, List<MySQLSyntax.Modifier> modifiers) {
        this.hintList = MySQLUtils.asHintList(this.context, hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(this.context, modifierList, MySQLUtils::deleteModifier);
        return this;
    }

    @Override
    public final DT from(SingleTableMeta<?> table, StandardSyntax.WordAs wordAs, String alias) {
        return this.deleteFrom(table, wordAs, alias);
    }

    @Override
    public final _LeftParenStringQuadraOptionalSpec<PR> partition() {
        return CriteriaSupports.stringQuadra(this.context, this::partitionEnd);
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
    public final SingleTableMeta<?> table() {
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
        if (this instanceof BatchDeleteStatement && ((BatchDeleteStatement<I>) this).paramList == null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return this.function.apply(this);
    }

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierList = null;
        this.partitionList = null;
        if (this instanceof BatchDeleteStatement) {
            ((BatchDeleteStatement<I>) this).paramList = null;
        }

    }

    @Override
    final MySQLCteBuilder createCteBuilder(boolean recursive) {
        return MySQLSupports.mySQLCteBuilder(recursive, this.context);
    }


    private PR partitionEnd(final List<String> list) {
        if (this.partitionList != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.partitionList = list;
        return (PR) this;
    }


    private static final class SingleComma<I extends Item> implements MySQLDelete._SingleComma<I> {

        private final boolean recursive;

        private final SimpleDeleteStatement<I> statement;

        private final Function<String, MySQLQuery._StaticCteLeftParenSpec<MySQLDelete._SingleComma<I>>> function;

        private SingleComma(boolean recursive, SimpleDeleteStatement<I> statement) {
            this.recursive = recursive;
            this.statement = statement;
            this.function = MySQLQueries.complexCte(statement.context, this);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_SingleComma<I>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public _SinglePartitionSpec<I> deleteFrom(SingleTableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            return this.endStaticWithClause().deleteFrom(table, wordAs, tableAlias);
        }

        @Override
        public _SingleDeleteFromClause<_SinglePartitionSpec<I>> delete(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers) {
            return this.endStaticWithClause().delete(hints, modifiers);
        }

        private SimpleDeleteStatement<I> endStaticWithClause() {
            final SimpleDeleteStatement<I> statement = this.statement;
            statement.endStaticWithClause(this.recursive);
            return statement;
        }


    }//SingleComma


    private static final class SimpleDeleteStatement<I extends Item> extends MySQLSingleDelete<
            I,
            MySQLDelete._SimpleSingleDeleteClause<I>,
            MySQLDelete._SinglePartitionSpec<I>,
            MySQLDelete._SingleWhereClause<I>,
            MySQLDelete._OrderBySpec<I>,
            MySQLDelete._SingleWhereAndSpec<I>,
            MySQLDelete._LimitSpec<I>,
            Statement._DmlDeleteSpec<I>>
            implements MySQLDelete._SingleWithSpec<I>
            , MySQLDelete._SinglePartitionSpec<I>
            , MySQLDelete._SingleWhereAndSpec<I> {

        private SimpleDeleteStatement(Function<Delete, I> function) {
            super(function);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_SingleComma<I>> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new SingleComma<>(recursive, this).function.apply(name);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_SingleComma<I>> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new SingleComma<>(recursive, this).function.apply(name);
        }


    }//SimpleDeleteStatement


    private static final class BatchSingleComma<I extends Item> implements _BatchSingleComma<I> {

        private final boolean recursive;

        private final BatchDeleteStatement<I> statement;

        private final Function<String, MySQLQuery._StaticCteLeftParenSpec<_BatchSingleComma<I>>> function;

        private BatchSingleComma(boolean recursive, BatchDeleteStatement<I> statement) {
            this.recursive = recursive;
            this.statement = statement;
            this.function = MySQLQueries.complexCte(statement.context, this);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchSingleComma<I>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        public _BatchSinglePartitionSpec<I> deleteFrom(SingleTableMeta<?> table, SQLs.WordAs wordAs, String tableAlias) {
            return this.endStaticWithClause().deleteFrom(table, wordAs, tableAlias);
        }

        @Override
        public _SingleDeleteFromClause<_BatchSinglePartitionSpec<I>> delete(Supplier<List<Hint>> hints
                , List<MySQLSyntax.Modifier> modifiers) {
            return this.endStaticWithClause().delete(hints, modifiers);
        }

        private BatchDeleteStatement<I> endStaticWithClause() {
            final BatchDeleteStatement<I> statement = this.statement;
            statement.endStaticWithClause(this.recursive);
            return statement;
        }


    }//BatchSingleComma


    private static final class BatchDeleteStatement<I extends Item> extends MySQLSingleDelete<
            I,
            MySQLDelete._BatchSingleDeleteClause<I>,
            MySQLDelete._BatchSinglePartitionSpec<I>,
            MySQLDelete._BatchSingleWhereClause<I>,
            MySQLDelete._BatchOrderBySpec<I>,
            MySQLDelete._BatchSingleWhereAndSpec<I>,
            MySQLDelete._BatchLimitSpec<I>,
            Statement._BatchParamClause<_DmlDeleteSpec<I>>>
            implements MySQLDelete._BatchSingleWithSpec<I>
            , MySQLDelete._BatchSinglePartitionSpec<I>
            , MySQLDelete._BatchSingleWhereAndSpec<I>
            , _BatchDml {

        private List<?> paramList;

        private BatchDeleteStatement(Function<Delete, I> function) {
            super(function);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchSingleComma<I>> with(String name) {
            final boolean recursive = false;
            this.context.onBeforeWithClause(recursive);
            return new BatchSingleComma<>(recursive, this).function.apply(name);
        }

        @Override
        public MySQLQuery._StaticCteLeftParenSpec<_BatchSingleComma<I>> withRecursive(String name) {
            final boolean recursive = true;
            this.context.onBeforeWithClause(recursive);
            return new BatchSingleComma<>(recursive, this).function.apply(name);
        }

        @Override
        public <P> _DmlDeleteSpec<I> paramList(List<P> paramList) {
            this.paramList = CriteriaUtils.paramList(this.context, paramList);
            return this;
        }

        @Override
        public <P> _DmlDeleteSpec<I> paramList(Supplier<List<P>> supplier) {
            this.paramList = CriteriaUtils.paramList(this.context, supplier.get());
            return this;
        }

        @Override
        public _DmlDeleteSpec<I> paramList(Function<String, ?> function, String keyName) {
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


    }//BatchDeleteStatement


}
