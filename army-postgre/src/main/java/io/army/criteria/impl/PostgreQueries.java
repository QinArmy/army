package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.postgre._PostgreQuery;
import io.army.criteria.postgre.*;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class PostgreQueries<I extends Item> extends SimpleQueries.WithCteSimpleQueries<
        I,
        PostgreCteBuilder,
        PostgreQuery._PostgreSelectClause<I>,
        PostgreSyntax.Modifier,
        PostgreQuery._FromSpec<I>,
        PostgreQuery._TableSampleJoinSpec<I>,
        PostgreQuery._JoinSpec<I>,
        PostgreQuery._JoinSpec<I>,
        PostgreQuery._TableSampleOnSpec<I>,
        Statement._OnClause<PostgreQuery._JoinSpec<I>>,
        Statement._OnClause<PostgreQuery._JoinSpec<I>>,
        PostgreQuery._GroupBySpec<I>,
        PostgreQuery._WhereAndSpec<I>,
        PostgreQuery._HavingSpec<I>,
        PostgreQuery._WindowSpec<I>,
        PostgreQuery._LimitSpec<I>,
        PostgreQuery._OffsetSpec<I>,
        PostgreQuery._FetchSpec<I>,
        PostgreQuery._LockSpec<I>,
        PostgreQuery._UnionAndQuerySpec<I>>
        implements PostgreQuery, _PostgreQuery
        , PostgreQuery._WithSpec<I>
        , PostgreQuery._FromSpec<I>
        , PostgreQuery._JoinSpec<I>
        , PostgreQuery._WindowCommaSpec<I>
        , PostgreQuery._HavingSpec<I>
        , PostgreQuery._FetchSpec<I>
        , PostgreQuery._LockOfTableSpec<I> {


    static <Q extends Item> PostgreQuery._WithSpec<Q> primaryQuery() {
        throw new UnsupportedOperationException();
    }

    static <Q extends Item> PostgreQuery._WithSpec<Q> subQuery(CriteriaContext outerContext
            , Function<SubQuery, Q> function) {
        throw new UnsupportedOperationException();
    }

    static <I extends Item> Function<String, _StaticCteLeftParenSpec<I>> complexCte(CriteriaContext outerContext
            , I comma) {
        throw new UnsupportedOperationException();

    }

    static <I extends Item> PostgreQuery._DynamicSubMaterializedSpec<I> dynamicCteQuery(CriteriaContext outerContext
            , Function<SubStatement, I> function) {
        throw new UnsupportedOperationException();
    }


    private PostgreQueries(CriteriaContext context) {
        super(context);
    }


    @Override
    public final _StaticCteLeftParenSpec<_CteComma<I>> with(String name) {
        final boolean recursive = false;
        this.context.onBeforeWithClause(recursive);
        return new CteComma<>(recursive, this).function.apply(name);
    }

    @Override
    public final _StaticCteLeftParenSpec<_CteComma<I>> withRecursive(String name) {
        final boolean recursive = true;
        this.context.onBeforeWithClause(recursive);
        return new CteComma<>(recursive, this).function.apply(name);
    }

    @Override
    public final _NestedLeftParenSpec<_JoinSpec<I>> from() {
        return null;
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> leftJoin() {
        return null;
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> join() {
        return null;
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> rightJoin() {
        return null;
    }

    @Override
    public final _NestedLeftParenSpec<_OnClause<_JoinSpec<I>>> fullJoin() {
        return null;
    }

    @Override
    public final _NestedLeftParenSpec<_JoinSpec<I>> crossJoin() {
        return null;
    }

    @Override
    public final _JoinSpec<I> ifLeftJoin(Consumer<PostgreJoins> consumer) {
        return null;
    }

    @Override
    public final _JoinSpec<I> ifJoin(Consumer<PostgreJoins> consumer) {
        return null;
    }

    @Override
    public final _JoinSpec<I> ifRightJoin(Consumer<PostgreJoins> consumer) {
        return null;
    }

    @Override
    public final _JoinSpec<I> ifFullJoin(Consumer<PostgreJoins> consumer) {
        return null;
    }

    @Override
    public final _JoinSpec<I> ifCrossJoin(Consumer<PostgreCrosses> consumer) {
        return null;
    }


    @Override
    public final _WindowAsClause<_WindowCommaSpec<I>> window(String windowName) {
        return null;
    }

    @Override
    public final _OrderBySpec<I> window(Consumer<PostgreWindows> consumer) {
        return null;
    }

    @Override
    public final _OrderBySpec<I> ifWindow(Consumer<PostgreWindows> consumer) {
        return null;
    }

    @Override
    public final _WindowAsClause<_WindowCommaSpec<I>> comma(String windowName) {
        return null;
    }


    @Override
    public final _LockOfTableSpec<I> forUpdate() {
        return null;
    }

    @Override
    public final _LockOfTableSpec<I> ifForUpdate(BooleanSupplier predicate) {
        return null;
    }

    @Override
    public final _LockOfTableSpec<I> forShare() {
        return null;
    }

    @Override
    public final _LockOfTableSpec<I> ifForShare(BooleanSupplier predicate) {
        return null;
    }

    @Override
    public final _LockOfTableSpec<I> forNoKeyUpdate() {
        return null;
    }

    @Override
    public final _LockOfTableSpec<I> forKeyShare() {
        return null;
    }

    @Override
    public final _LockOfTableSpec<I> ifForNoKeyUpdate(BooleanSupplier supplier) {
        return null;
    }

    @Override
    public final _LockOfTableSpec<I> ifForKeyShare(BooleanSupplier supplier) {
        return null;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table) {
        return null;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table1, TableMeta<?> table2) {
        return null;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table1, TableMeta<?> table2, TableMeta<?> table3) {
        return null;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(Consumer<Consumer<TableMeta<?>>> consumer) {
        return null;
    }

    @Override
    public final _LockWaitOptionSpec<I> ifOf(Consumer<Consumer<TableMeta<?>>> consumer) {
        return null;
    }

    @Override
    public final _LockSpec<I> noWait() {
        return null;
    }

    @Override
    public final _LockSpec<I> skipLocked() {
        return null;
    }

    @Override
    public final _LockSpec<I> ifNoWait(BooleanSupplier predicate) {
        return null;
    }

    @Override
    public final _LockSpec<I> ifSkipLocked(BooleanSupplier predicate) {
        return null;
    }

    @Override
    final void onEndQuery() {

    }


    @Override
    final void onClear() {

    }

    @Override
    final List<PostgreSyntax.Modifier> asModifierList(@Nullable List<PostgreSyntax.Modifier> modifiers) {
        return null;
    }

    @Override
    final List<Hint> asHintList(@Nullable List<Hint> hints) {
        return null;
    }

    @Override
    final _UnionAndQuerySpec<I> createQueryUnion(UnionType unionType) {
        return null;
    }


    @Override
    final PostgreCteBuilder createCteBuilder(boolean recursive) {
        return null;
    }


    @Override
    final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable TableModifier modifier, TableMeta<?> table, String alias) {
        return null;
    }

    @Override
    final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable TabularModifier modifier, TabularItem tableItem, String alias) {
        return null;
    }

    @Override
    final _TableSampleOnSpec<I> createTableBlock(_JoinType joinType, @Nullable TableModifier modifier, TableMeta<?> table, String tableAlias) {
        return null;
    }

    @Override
    final _OnClause<_JoinSpec<I>> createItemBlock(_JoinType joinType, @Nullable TabularModifier modifier, TabularItem tableItem, String alias) {
        return null;
    }

    @Override
    final _OnClause<_JoinSpec<I>> createCteBlock(_JoinType joinType, @Nullable TabularModifier modifier, TabularItem tableItem, String alias) {
        return null;
    }


    private static final class CteComma<I extends Item>
            extends SelectClauseDispatcher<PostgreSyntax.Modifier, PostgreQuery._FromSpec<I>>
            implements PostgreQuery._CteComma<I> {

        private final boolean recursive;

        private final PostgreQueries<I> clause;

        private final Function<String, PostgreStatement._StaticCteLeftParenSpec<PostgreQuery._CteComma<I>>> function;


        private CteComma(boolean recursive, PostgreQueries<I> clause) {
            this.recursive = recursive;
            this.clause = clause;
            this.function = PostgreQueries.complexCte(clause.context, this);
        }

        @Override
        public _StaticCteLeftParenSpec<_CteComma<I>> comma(String name) {
            return this.function.apply(name);
        }

        @Override
        _DynamicHintModifierSelectClause<PostgreSyntax.Modifier, _FromSpec<I>> createSelectClause() {
            this.clause.endStaticWithClause(this.recursive);
            return this.clause;
        }


    }//CteComma


}
