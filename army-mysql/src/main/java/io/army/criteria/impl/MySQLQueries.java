package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQLQuery;
import io.army.criteria.mysql.MySQLCteBuilder;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLWindowBuilder;
import io.army.dialect._Constant;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.*;


/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link MySQL80SimpleQuery}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class MySQLQueries<I extends Item> extends SimpleQueries.WithCteSimpleQueries<
        I,
        MySQLCteBuilder,
        MySQLQuery._MySQLSelectClause<I>,
        MySQLSyntax.Modifier,
        MySQLQuery._FromSpec<I>,
        MySQLQuery._IndexHintJoinSpec<I>,
        MySQLQuery._JoinSpec<I>,
        MySQLQuery._JoinSpec<I>,
        MySQLQuery._IndexHintOnSpec<I>,
        Statement._OnClause<MySQLQuery._JoinSpec<I>>,
        Statement._OnClause<MySQLQuery._JoinSpec<I>>,
        MySQLQuery._GroupBySpec<I>,
        MySQLQuery._WhereAndSpec<I>,
        MySQLQuery._GroupByWithRollupSpec<I>,
        MySQLQuery._WindowSpec<I>,
        MySQLQuery._OrderByWithRollupSpec<I>,
        MySQLQuery._LockOptionSpec<I>,
        MySQLQuery._UnionAndQuerySpec<I>>
        implements _MySQLQuery, MySQLQuery
        , MySQLQuery._WithCteSpec<I>
        , MySQLQuery._FromSpec<I>
        , MySQLQuery._IndexHintJoinSpec<I>
        , MySQLQuery._GroupByWithRollupSpec<I>
        , MySQLQuery._HavingSpec<I>
        , MySQLQuery._OrderByWithRollupSpec<I>
        , MySQLQuery._LockOfTableSpec<I>
        , OrderByClause.OrderByEventListener {


    static <I extends Item> MySQLQuery._WithCteSpec<I> subQuery(CriteriaContext outerContext
            , Function<SubQuery, I> function) {
        throw new UnsupportedOperationException();
    }

    static <I extends Item> MySQLStaticComplexCommandSpec<I> staticComplexCommand(CriteriaContext outerContext
            , String cteName, I cteComma) {
        return new StaticComplexCommand<>(outerContext, cteName, cteComma);
    }

    private MySQLSupports.MySQLNoOnBlock<_IndexHintJoinSpec<I>> noOnBlock;

    /**
     * @see #onOrderByEvent()
     */
    private Boolean groupByWithRollup;

    private boolean orderByWithRollup;

    private List<String> intoVarList;

    MySQLQueries(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }


    @Override
    public final _StaticCteLeftParenSpec<_CteComma<I>> with(String name) {
        return this.createComplexCommand(false, name);
    }

    @Override
    public final _StaticCteLeftParenSpec<_CteComma<I>> withRecursive(String name) {
        return this.createComplexCommand(true, name);
    }


    @Override
    public final _PartitionJoinSpec<I> from(TableMeta<?> table) {
        return new PartitionJoinClause<>(this, _JoinType.NONE, table);
    }

    @Override
    public final _IndexPurposeBySpec<_IndexHintJoinSpec<I>> useIndex() {
        return this.getIndexHintClause().useIndex();
    }

    @Override
    public final _IndexPurposeBySpec<_IndexHintJoinSpec<I>> ignoreIndex() {
        return this.getIndexHintClause().ignoreIndex();
    }

    @Override
    public final _IndexPurposeBySpec<_IndexHintJoinSpec<I>> forceIndex() {
        return this.getIndexHintClause().forceIndex();
    }


    @Override
    public final _PartitionOnSpec<I> leftJoin(TableMeta<?> table) {
        return new PartitionOnClause<>(this, _JoinType.LEFT_JOIN, table);
    }

    @Override
    public final _PartitionOnSpec<I> join(TableMeta<?> table) {
        return new PartitionOnClause<>(this, _JoinType.JOIN, table);
    }

    @Override
    public final _PartitionOnSpec<I> rightJoin(TableMeta<?> table) {
        return new PartitionOnClause<>(this, _JoinType.RIGHT_JOIN, table);
    }

    @Override
    public final _PartitionOnSpec<I> fullJoin(TableMeta<?> table) {
        return new PartitionOnClause<>(this, _JoinType.FULL_JOIN, table);
    }

    @Override
    public final _PartitionOnSpec<I> straightJoin(TableMeta<?> table) {
        return new PartitionOnClause<>(this, _JoinType.STRAIGHT_JOIN, table);
    }

    @Override
    public final _PartitionJoinSpec<I> crossJoin(TableMeta<?> table) {
        return new PartitionJoinClause<>(this, _JoinType.CROSS_JOIN, table);
    }

    /**
     * @see #onOrderByEvent()
     */
    @Override
    public final _HavingSpec<I> withRollup() {
        if (this.groupByWithRollup == null) {//@see #onOrderByEvent()
            this.groupByWithRollup = Boolean.TRUE;
        } else {
            this.orderByWithRollup = true;
        }
        return this;
    }

    /**
     * @see #onOrderByEvent()
     */
    @Override
    public final _HavingSpec<I> ifWithRollup(final BooleanSupplier supplier) {
        if (supplier.getAsBoolean()) {
            if (this.groupByWithRollup == null) {//@see #onOrderByEvent()
                this.groupByWithRollup = Boolean.TRUE;
            } else {
                this.orderByWithRollup = true;
            }
        } else if (this.groupByWithRollup == null) {//@see #onOrderByEvent()
            this.groupByWithRollup = Boolean.FALSE;
        } else {
            this.orderByWithRollup = false;
        }
        return this;
    }

    /**
     * @see #withRollup()
     * @see #ifWithRollup(BooleanSupplier)
     */
    @Override
    public final void onOrderByEvent() {
        if (this.groupByWithRollup == null) {
            this.groupByWithRollup = Boolean.FALSE;
        }
    }

    @Override
    public final _OrderBySpec<I> window(Consumer<MySQLWindowBuilder> consumer) {
        return this;
    }

    @Override
    public final Window._SimpleAsClause<_WindowCommaSpec<I>> window(String windowName) {
        return null;
    }

    @Override
    public final _LockOptionSpec<I> limit(Expression offset, Expression rowCount) {
        return this;
    }

    @Override
    public final _LockOptionSpec<I> limit(BiFunction<MappingType, Number, Expression> operator, long offset, long rowCount) {
        return this;
    }

    @Override
    public final <N extends Number> _LockOptionSpec<I> limit(BiFunction<MappingType, Number, Expression> operator, Supplier<N> offsetSupplier, Supplier<N> rowCountSupplier) {
        return this;
    }

    @Override
    public final _LockOptionSpec<I> limit(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function, String offsetKey, String rowCountKey) {
        return this;
    }

    @Override
    public final _LockOptionSpec<I> limit(Consumer<BiConsumer<Expression, Expression>> consumer) {
        return this;
    }

    @Override
    public final <N extends Number> _LockOptionSpec<I> ifLimit(BiFunction<MappingType, Number, Expression> operator, Supplier<N> offsetSupplier, Supplier<N> rowCountSupplier) {
        return this;
    }

    @Override
    public final _LockOptionSpec<I> ifLimit(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function, String offsetKey, String rowCountKey) {
        return this;
    }

    @Override
    public final _LockOptionSpec<I> ifLimit(Consumer<BiConsumer<Expression, Expression>> consumer) {
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> forUpdate() {
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> forShare() {
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> ifForUpdate(BooleanSupplier supplier) {
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> ifForShare(BooleanSupplier supplier) {
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> lockInShareMode() {
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> ifLockInShareMode(BooleanSupplier supplier) {
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table) {
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table1, TableMeta<?> table2) {
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table1, TableMeta<?> table2, TableMeta<?> table3) {
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(Consumer<Consumer<TableMeta<?>>> consumer) {
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> ifOf(Consumer<Consumer<TableMeta<?>>> consumer) {
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> noWait() {
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> skipLocked() {
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> ifNoWait(BooleanSupplier supplier) {
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> ifSkipLocked(BooleanSupplier supplier) {
        return this;
    }

    @Override
    public final _QuerySpec<I> into(String varName) {
        return this;
    }

    @Override
    public final _QuerySpec<I> into(String varName1, String varName2) {
        return this;
    }

    @Override
    public final _QuerySpec<I> into(String varName1, String varName2, String varName3) {
        return this;
    }

    @Override
    public final _QuerySpec<I> into(String varName1, String varName2, String varName3, String varName4) {
        return this;
    }

    @Override
    public final _QuerySpec<I> into(List<String> varNameList) {
        return this;
    }

    @Override
    public final _QuerySpec<I> into(Consumer<Consumer<String>> consumer) {
        return this;
    }


    @Override
    public final _Expression offset() {
        return null;
    }

    @Override
    public final boolean groupByWithRollUp() {
        final Boolean withRollup = this.groupByWithRollup;
        return withRollup != null && withRollup;
    }

    @Override
    public final boolean orderByWithRollup() {
        return this.orderByWithRollup;
    }

    @Override
    public final List<String> intoVarList() {
        return null;
    }

    @Override
    final MySQLCteBuilder createCteBuilder(boolean recursive) {
        return null;
    }

    @Override
    final void onEndQuery() {

    }


    @Override
    final void onClear() {

    }

    @Override
    final List<MySQLSyntax.Modifier> asModifierList(@Nullable List<MySQLSyntax.Modifier> modifiers) {
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
    final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable TableModifier itemWord, TableMeta<?> table
            , String alias) {
        return new MySQLSupports.MySQLNoOnBlock<>(joinType, itemWord, table, alias, this);
    }

    @Override
    final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable TabularModifier itemWord, TabularItem tableItem
            , String alias) {
        return new MySQLSupports.MySQLNoOnBlock<>(joinType, itemWord, tableItem, alias, this);
    }

    @Override
    final _IndexHintOnSpec<I> createTableBlock(_JoinType joinType, @Nullable TableModifier itemWord, TableMeta<?> table
            , String tableAlias) {
        return new OnTableBlock<>(joinType, itemWord, table, tableAlias, this);
    }

    @Override
    final _OnClause<_JoinSpec<I>> createItemBlock(_JoinType joinType, @Nullable TabularModifier itemWord
            , TabularItem tableItem, String alias) {
        return new OnClauseTableBlock.OnItemTableBlock<>(joinType, itemWord, tableItem, alias, this);
    }

    @Override
    final _OnClause<_JoinSpec<I>> createCteBlock(_JoinType joinType, @Nullable TabularModifier itemWord
            , TabularItem tableItem, String alias) {
        return new OnClauseTableBlock.OnItemTableBlock<>(joinType, itemWord, tableItem, alias, this);
    }



    /*################################## blow private method ##################################*/

    /**
     * @see #with(String)
     * @see #withRecursive(String)
     */
    private _StaticCteLeftParenSpec<_CteComma<I>> createComplexCommand(final boolean recursive
            , final @Nullable String name) {
        if (name == null) {
            throw ContextStack.nullPointer(this.context);
        }
        final CriteriaContext context = this.context;
        context.onBeforeWithClause(recursive);
        context.onStartCte(name);

        final MySQLCteComma<I> comma;
        comma = new MySQLCteComma<>(this, recursive, name);
        return comma.complexCommand;
    }


    /**
     * @see #useIndex()
     * @see #ignoreIndex()
     * @see #forceIndex()
     */
    private _QueryIndexHintClause<_IndexHintJoinSpec<I>> getIndexHintClause() {
        final MySQLSupports.MySQLNoOnBlock<_IndexHintJoinSpec<I>> noOnBlock = this.noOnBlock;
        if (noOnBlock == null || this.context.lastBlock() != noOnBlock) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return noOnBlock.getUseIndexClause();
    }


    enum MySQLLockMode implements SQLWords {

        FOR_UPDATE(_Constant.FOR_UPDATE),
        LOCK_IN_SHARE_MODE(_Constant.LOCK_IN_SHARE_MODE),
        FOR_SHARE(_Constant.FOR_SHARE);

        final String words;

        MySQLLockMode(String words) {
            this.words = words;
        }

        @Override
        public final String render() {
            return this.words;
        }


        @Override
        public final String toString() {
            return String.format("%s.%s", MySQLLockMode.class.getSimpleName(), this.name());
        }

    }//MySQLLock

    enum MySQLLockOption implements SQLWords {

        NOWAIT(" NOWAIT"),
        SKIP_LOCKED(" SKIP LOCKED");

        final String words;

        MySQLLockOption(String words) {
            this.words = words;
        }

        @Override
        public final String render() {
            return this.words;
        }

        @Override
        public final String toString() {
            return String.format("%s.%s", MySQLLockOption.class.getSimpleName(), this.name());
        }

    }//MySQLLockOption


    interface MySQLStaticComplexCommandSpec<I extends Item>
            extends MySQLQuery._MySQLSelectClause<MySQLQuery._CteSpec<I>> {

        void nextCte(String cteName);

    }


    private static final class PartitionJoinClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<_IndexHintJoinSpec<I>>
            implements MySQLQuery._PartitionJoinSpec<I> {

        private final MySQLQueries<I> stmt;

        private PartitionJoinClause(MySQLQueries<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _IndexHintJoinSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLQueries<I> stmt = this.stmt;

            MySQLSupports.MySQLNoOnBlock<_IndexHintJoinSpec<I>> block;
            block = new MySQLSupports.MySQLNoOnBlock<>(params, stmt);

            stmt.context.onAddBlock(block);
            stmt.noOnBlock = block;// update noOnBlock
            return stmt;
        }


    }//PartitionJoinClause


    private static final class OnTableBlock<I extends Item>
            extends MySQLSupports.MySQLOnBlock<_IndexHintOnSpec<I>, _JoinSpec<I>>
            implements _IndexHintOnSpec<I> {

        private OnTableBlock(_JoinType joinType, @Nullable SQLWords itemWord
                , TabularItem tableItem, String alias, _JoinSpec<I> stmt) {
            super(joinType, itemWord, tableItem, alias, stmt);
        }

        private OnTableBlock(MySQLSupports.MySQLBlockParams params, _JoinSpec<I> stmt) {
            super(params, stmt);
        }


    }//OnTableBlock

    private static final class PartitionOnClause<I extends Item>
            extends MySQLSupports.PartitionAsClause<_IndexHintOnSpec<I>>
            implements MySQLQuery._PartitionOnSpec<I> {

        private final MySQLQueries<I> stmt;

        private PartitionOnClause(MySQLQueries<I> stmt, _JoinType joinType, TableMeta<?> table) {
            super(stmt.context, joinType, table);
            this.stmt = stmt;
        }

        @Override
        _IndexHintOnSpec<I> asEnd(final MySQLSupports.MySQLBlockParams params) {
            final MySQLQueries<I> stmt = this.stmt;
            final OnTableBlock<I> block;
            block = new OnTableBlock<>(params, stmt);
            stmt.context.onAddBlock(block);
            return block;
        }


    }//PartitionOnClause

    private static final class MySQLCteComma<I extends Item>
            extends SimpleQueries.SelectClauseDispatcher<MySQLSyntax.Modifier, MySQLQuery._FromSpec<I>>
            implements MySQLQuery._CteComma<I> {

        private final boolean recursive;
        private final MySQLQueries<I> statement;

        private final StaticComplexCommand<_CteComma<I>> complexCommand;

        private MySQLCteComma(MySQLQueries<I> statement, boolean recursive, String cteName) {
            this.statement = statement;
            this.recursive = recursive;
            this.complexCommand = new StaticComplexCommand<>(statement.context, cteName, this);
        }

        @Override
        public _StaticCteLeftParenSpec<_CteComma<I>> comma(final @Nullable String name) {
            final StaticComplexCommand<_CteComma<I>> complexCommand = this.complexCommand;
            if (name == null) {
                throw ContextStack.nullPointer(complexCommand.context);
            } else if (complexCommand.cteName != null) {
                throw ContextStack.castCriteriaApi(this.statement.context);
            }
            complexCommand.context.onStartCte(name);
            complexCommand.cteName = name;
            return complexCommand;
        }

        @Override
        _DynamicHintModifierSelectClause<MySQLSyntax.Modifier, _FromSpec<I>> createSelectClause() {
            final MySQLQueries<I> statement = this.statement;
            statement.endStaticWithClause(this.recursive);
            return statement;
        }


    }//MySQLCteComma


    private static final class StaticComplexCommand<I extends Item>
            extends SimpleQueries.ComplexSelectCommand<
            MySQLs.Modifier,
            MySQLQuery._FromSpec<MySQLQuery._CteSpec<I>>,
            _StaticCteAsClause<I>>
            implements MySQLStaticComplexCommandSpec<I>
            , MySQLQuery._StaticCteLeftParenSpec<I>
            , _RightParenClause<_StaticCteAsClause<I>>
            , MySQLQuery._CteSpec<I> {

        private final I cteComma;

        private String cteName;

        private List<String> columnAliasList;

        private StaticComplexCommand(CriteriaContext context, String cteName, I cteComma) {
            super(context);
            this.cteName = cteName;
            this.cteComma = cteComma;
        }

        @Override
        public _MySQLSelectClause<_CteSpec<I>> as() {
            if (this.cteName == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return this;
        }

        @Override
        public void nextCte(String cteName) {
            if (this.cteName != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.cteName = cteName;
        }

        @Override
        _DynamicHintModifierSelectClause<MySQLs.Modifier, _FromSpec<_CteSpec<I>>> createSelectClause() {
            return MySQLQueries.subQuery(this.context, this::queryEnd);
        }

        _StaticCteAsClause<I> columnAliasClauseEnd(final List<String> list) {
            this.columnAliasList = list;
            this.context.onCteColumnAlias(this.cteName, list);
            return this;
        }

        private _CteSpec<I> queryEnd(final SubQuery query) {
            CriteriaUtils.createAndAddCte(this.context, this.cteName, this.columnAliasList, query);
            this.cteName = null;
            this.columnAliasList = null;
            return this;
        }

        @Override
        public I asCte() {
            return this.cteComma;
        }


    }//StaticComplexCommand


}
