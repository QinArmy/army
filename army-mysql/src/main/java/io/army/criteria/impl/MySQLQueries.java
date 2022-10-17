package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner._Window;
import io.army.criteria.impl.inner.mysql._MySQLQuery;
import io.army.criteria.mysql.MySQLCteBuilder;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLWindowBuilder;
import io.army.dialect._Constant;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * <p>
 * This class is base class of the implementation of {@link MySQLQuery}:
 * </p>
 *
 * @since 1.0
 */
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
        , MySQLQuery._WhereAndSpec<I>
        , MySQLQuery._GroupByWithRollupSpec<I>
        , MySQLQuery._HavingSpec<I>
        , MySQLQuery._WindowCommaSpec<I>
        , MySQLQuery._OrderByWithRollupSpec<I>
        , MySQLQuery._LockOfTableSpec<I>
        , OrderByClause.OrderByEventListener {

    static MySQLQuery._WithCteSpec<Select> primaryQuery() {
        return new SimpleSelect<>(CriteriaContexts.primaryQuery(null), SQLs::_identity);
    }


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

    private List<_Window> windowList;

    private boolean orderByWithRollup;

    private MySQLLockMode lockMode;

    private List<TableMeta<?>> ofTableList;

    private LockWaitOption lockWaitOption;

    private List<String> intoVarList;

    MySQLQueries(CriteriaContext context) {
        super(context);
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
        consumer.accept(new MySQLWindowBuilderImpl(this));
        if (this.windowList == null) {
            throw ContextStack.criteriaError(this.context, _Exceptions::windowListIsEmpty);
        }
        return this;
    }

    @Override
    public final _OrderBySpec<I> ifWindow(Consumer<MySQLWindowBuilder> consumer) {
        consumer.accept(new MySQLWindowBuilderImpl(this));
        return this;
    }

    @Override
    public final Window._SimpleAsClause<_WindowCommaSpec<I>> window(String windowName) {
        if (!_StringUtils.hasText(windowName)) {
            throw ContextStack.criteriaError(this.context, _Exceptions::namedWindowNoText);
        }
        return WindowClause.namedWindow(windowName, this.context, this::onAddWindow);
    }

    @Override
    public final Window._SimpleAsClause<_WindowCommaSpec<I>> comma(final String windowName) {
        if (!_StringUtils.hasText(windowName)) {
            throw ContextStack.criteriaError(this.context, _Exceptions::namedWindowNoText);
        }
        return WindowClause.namedWindow(windowName, this.context, this::onAddWindow);
    }

    @Override
    public final _LockOfTableSpec<I> forUpdate() {
        this.lockMode = MySQLLockMode.FOR_UPDATE;
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> forShare() {
        this.lockMode = MySQLLockMode.FOR_SHARE;
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> ifForUpdate(BooleanSupplier predicate) {
        if (predicate.getAsBoolean()) {
            this.lockMode = MySQLLockMode.FOR_UPDATE;
        } else {
            this.lockMode = null;
        }
        return this;
    }

    @Override
    public final _LockOfTableSpec<I> ifForShare(BooleanSupplier predicate) {
        if (predicate.getAsBoolean()) {
            this.lockMode = MySQLLockMode.FOR_SHARE;
        } else {
            this.lockMode = null;
        }
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> lockInShareMode() {
        this.lockMode = MySQLLockMode.LOCK_IN_SHARE_MODE;
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> ifLockInShareMode(BooleanSupplier predicate) {
        if (predicate.getAsBoolean()) {
            this.lockMode = MySQLLockMode.LOCK_IN_SHARE_MODE;
        } else {
            this.lockMode = null;
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table) {
        if (this.lockMode != null) {
            this.ofTableList = Collections.singletonList(table);
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table1, TableMeta<?> table2) {
        if (this.lockMode != null) {
            this.ofTableList = ArrayUtils.asUnmodifiableList(table1, table2);
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(TableMeta<?> table1, TableMeta<?> table2, TableMeta<?> table3) {
        if (this.lockMode != null) {
            this.ofTableList = ArrayUtils.asUnmodifiableList(table1, table2, table3);
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> of(Consumer<Consumer<TableMeta<?>>> consumer) {
        if (this.lockMode != null) {
            final List<TableMeta<?>> list = new ArrayList<>();
            consumer.accept(list::add);
            if (list.size() == 0) {
                throw CriteriaUtils.ofTableListIsEmpty(this.context);
            }
            this.ofTableList = _CollectionUtils.unmodifiableList(list);
        }
        return this;
    }

    @Override
    public final _LockWaitOptionSpec<I> ifOf(Consumer<Consumer<TableMeta<?>>> consumer) {
        if (this.lockMode != null) {
            final List<TableMeta<?>> list = new ArrayList<>();
            consumer.accept(list::add);
            this.ofTableList = _CollectionUtils.unmodifiableList(list);
        }
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> noWait() {
        if (this.lockMode != null) {
            this.lockWaitOption = LockWaitOption.NOWAIT;
        }
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> skipLocked() {
        if (this.lockMode != null) {
            this.lockWaitOption = LockWaitOption.SKIP_LOCKED;
        }
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> ifNoWait(BooleanSupplier predicate) {
        if (this.lockMode != null) {
            if (predicate.getAsBoolean()) {
                this.lockWaitOption = LockWaitOption.NOWAIT;
            } else {
                this.lockWaitOption = null;
            }
        }
        return this;
    }

    @Override
    public final _IntoOptionSpec<I> ifSkipLocked(BooleanSupplier predicate) {
        if (this.lockMode != null) {
            if (predicate.getAsBoolean()) {
                this.lockWaitOption = LockWaitOption.SKIP_LOCKED;
            } else {
                this.lockWaitOption = null;
            }
        }
        return this;
    }

    @Override
    public final _QuerySpec<I> into(String varName) {
        this.intoVarList = Collections.singletonList(varName);
        return this;
    }

    @Override
    public final _QuerySpec<I> into(String varName1, String varName2) {
        this.intoVarList = ArrayUtils.asUnmodifiableList(varName1, varName2);
        return this;
    }

    @Override
    public final _QuerySpec<I> into(String varName1, String varName2, String varName3) {
        this.intoVarList = ArrayUtils.asUnmodifiableList(varName1, varName2, varName3);
        return this;
    }

    @Override
    public final _QuerySpec<I> into(String varName1, String varName2, String varName3, String varName4) {
        this.intoVarList = ArrayUtils.asUnmodifiableList(varName1, varName2, varName3, varName4);
        return this;
    }

    @Override
    public final _QuerySpec<I> into(List<String> varNameList) {
        this.intoVarList = _CollectionUtils.asUnmodifiableList(varNameList);
        return this;
    }

    @Override
    public final _QuerySpec<I> into(Consumer<Consumer<String>> consumer) {
        final List<String> list = new ArrayList<>();
        consumer.accept(list::add);
        if (list.size() == 0) {
            throw ContextStack.criteriaError(this.context, "no into clause.");
        }
        this.intoVarList = _CollectionUtils.unmodifiableList(list);
        return this;
    }

    @Override
    public final _QuerySpec<I> ifInto(Consumer<Consumer<String>> consumer) {
        final List<String> list = new ArrayList<>();
        consumer.accept(list::add);
        this.intoVarList = _CollectionUtils.unmodifiableList(list);
        return this;
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
        final List<String> list = this.intoVarList;
        if (list == null || list.size() == 0) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }

    @Override
    final MySQLCteBuilder createCteBuilder(boolean recursive) {
        return MySQLSupports.mySQLCteBuilder(recursive, this.context);
    }

    @Override
    final void onEndQuery() {
        if (this.windowList == null) {
            this.windowList = Collections.emptyList();
        }

        if (this.ofTableList == null) {
            this.ofTableList = Collections.emptyList();
        }
        if (this.intoVarList == null) {
            this.intoVarList = Collections.emptyList();
        }
    }


    @Override
    final void onClear() {
        this.windowList = null;
        this.ofTableList = null;
        this.intoVarList = null;
    }

    @Override
    final List<MySQLs.Modifier> asModifierList(@Nullable List<MySQLs.Modifier> modifiers) {
        return CriteriaUtils.asModifierList(this.context, modifiers, MySQLUtils::selectModifier);
    }

    @Override
    final List<Hint> asHintList(@Nullable List<Hint> hints) {
        return MySQLUtils.asHintList(this.context, hints, MySQLHints::castHint);
    }


    @Override
    final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable TableModifier modifier, TableMeta<?> table
            , String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new MySQLSupports.MySQLNoOnBlock<>(joinType, null, table, alias, this);
    }

    @Override
    final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable TabularModifier modifier, TabularItem tableItem
            , String alias) {
        if (modifier != null && modifier != SQLs.LATERAL) {
            throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
        }
        return new MySQLSupports.MySQLNoOnBlock<>(joinType, modifier, tableItem, alias, this);
    }


    @Override
    final _IndexHintOnSpec<I> createTableBlock(_JoinType joinType, @Nullable TableModifier modifier, TableMeta<?> table
            , String tableAlias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new OnTableBlock<>(joinType, null, table, tableAlias, this);
    }

    @Override
    final _OnClause<_JoinSpec<I>> createItemBlock(_JoinType joinType, @Nullable TabularModifier modifier
            , TabularItem tableItem, String alias) {
        if (modifier != null && modifier != SQLs.LATERAL) {
            throw MySQLUtils.dontSupportTabularModifier(this.context, modifier);
        }
        return new OnClauseTableBlock.OnItemTableBlock<>(joinType, modifier, tableItem, alias, this);
    }

    @Override
    final _OnClause<_JoinSpec<I>> createCteBlock(_JoinType joinType, @Nullable TabularModifier modifier
            , TabularItem tableItem, String alias) {
        if (modifier != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return new OnClauseTableBlock.OnItemTableBlock<>(joinType, null, tableItem, alias, this);
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

    /**
     * @see #window(String)
     * @see #comma(String)
     */
    private _WindowCommaSpec<I> onAddWindow(final _Window window) {
        List<_Window> windowList = this.windowList;
        if (windowList == null) {
            windowList = new ArrayList<>();
            this.windowList = windowList;
        } else if (!(window instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        windowList.add(window);
        return this;
    }


    private static final class SimpleSelect<I extends Item> extends MySQLQueries<I>
            implements Select {

        private final Function<Select, I> function;

        private SimpleSelect(CriteriaContext context, Function<Select, I> function) {
            super(context);
            this.function = function;
        }


        @Override
        public String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _UnionAndQuerySpec<I> createQueryUnion(UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);
            return new UnionAndSelectClause<>(this, unionType, this.function);
        }


    }//SimpleSelect

    private static final class SimpleSubQuery<I extends Item> extends MySQLQueries<I>
            implements SubQuery {

        private final Function<SubQuery, I> function;

        private SimpleSubQuery(CriteriaContext context, Function<SubQuery, I> function) {
            super(context);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _UnionAndQuerySpec<I> createQueryUnion(final UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);
            return new UnionAndSubQueryClause<>(this, unionType, this.function);
        }
    }//SimpleSubQuery


    enum MySQLLockMode implements SQLWords {

        FOR_UPDATE(_Constant.SPACE_FOR_UPDATE),
        LOCK_IN_SHARE_MODE(_Constant.SPACE_LOCK_IN_SHARE_MODE),
        FOR_SHARE(_Constant.SPACE_FOR_SHARE);

        final String spaceWords;

        MySQLLockMode(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String render() {
            return this.spaceWords;
        }


        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(MySQLLockMode.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }

    }//MySQLLock


    interface MySQLStaticComplexCommandSpec<I extends Item>
            extends MySQLQuery._MySQLSelectClause<MySQLQuery._CteSpec<I>> {

        void nextCte(String cteName);

    }


    /**
     * @see #window(Consumer)
     * @see #ifWindow(Consumer)
     */
    private static final class MySQLWindowBuilderImpl implements MySQLWindowBuilder {

        private final MySQLQueries<?> stmt;

        private MySQLWindowBuilderImpl(MySQLQueries<?> stmt) {
            this.stmt = stmt;
        }

        @Override
        public Window._SimpleAsClause<MySQLWindowBuilder> comma(final String windowName) {
            if (!_StringUtils.hasText(windowName)) {
                throw ContextStack.criteriaError(this.stmt.context, _Exceptions::namedWindowNoText);
            }
            return WindowClause.namedWindow(windowName, this.stmt.context, this::windowClauseEnd);
        }

        private MySQLWindowBuilder windowClauseEnd(final _Window window) {
            this.stmt.onAddWindow(window);
            return this;
        }

    }//MySQLWindowBuilderImpl


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


    private static final class MySQLBracketSelect<I extends Item>
            extends BracketRowSet<
            I,
            Select,
            MySQLQuery._UnionOrderBySpec<I>,
            MySQLQuery._UnionLimitSpec<I>,
            _QuerySpec<I>,
            MySQLQuery._UnionAndQuerySpec<I>,
            RowSet,
            Void> implements MySQLQuery._UnionOrderBySpec<I>
            , Statement._RightParenClause<MySQLQuery._UnionOrderBySpec<I>>
            , Select {

        private final Function<Select, I> function;

        private MySQLBracketSelect(CriteriaContext context, Function<Select, I> function) {
            super(context);
            this.function = function;
        }

        @Override
        public String toString() {
            final String s;
            if (this.isPrepared()) {
                s = this.mockAsString(MySQLDialect.MySQL80, Visible.ONLY_VISIBLE, true);
            } else {
                s = super.toString();
            }
            return s;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _UnionAndQuerySpec<I> createQueryUnion(final UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);
            return new UnionAndSelectClause<>(this, unionType, this.function);
        }

    }//MySQLBracketSelect


    private static final class MySQLBracketSubQuery<I extends Item>
            extends BracketRowSet<
            I,
            SubQuery,
            MySQLQuery._UnionOrderBySpec<I>,
            MySQLQuery._UnionLimitSpec<I>,
            _QuerySpec<I>,
            MySQLQuery._UnionAndQuerySpec<I>,
            RowSet,
            Void> implements MySQLQuery._UnionOrderBySpec<I>
            , Statement._RightParenClause<MySQLQuery._UnionOrderBySpec<I>>
            , SubQuery {

        private final Function<SubQuery, I> function;

        private MySQLBracketSubQuery(CriteriaContext context, Function<SubQuery, I> function) {
            super(context);
            this.function = function;
        }

        @Override
        I onAsQuery() {
            return this.function.apply(this);
        }

        @Override
        _UnionAndQuerySpec<I> createQueryUnion(final UnionType unionType) {
            UnionType.standardUnionType(this.context, unionType);
            return new UnionAndSubQueryClause<>(this, unionType, this.function);
        }

    }//MySQLBracketSubQuery


    private static final class UnionLeftParenSubQueryClause<I extends Item>
            extends SelectClauseDispatcher<MySQLs.Modifier, MySQLQuery._FromSpec<_RightParenClause<MySQLQuery._UnionOrderBySpec<I>>>>
            implements MySQLQuery._UnionAndQuerySpec<_RightParenClause<MySQLQuery._UnionOrderBySpec<I>>> {

        private final MySQLBracketSubQuery<I> bracket;

        private UnionLeftParenSubQueryClause(MySQLBracketSubQuery<I> bracket) {
            this.bracket = bracket;
        }

        @Override
        public _UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<_RightParenClause<_UnionOrderBySpec<I>>>>> leftParen() {
            final CriteriaContext context;
            context = CriteriaContexts.bracketContext(this.bracket.context);

            final MySQLBracketSubQuery<_RightParenClause<_UnionOrderBySpec<I>>> newBracket;
            newBracket = new MySQLBracketSubQuery<>(context, this.bracket::parenRowSetEnd);
            return new UnionLeftParenSubQueryClause<>(newBracket);
        }

        @Override
        _DynamicHintModifierSelectClause<MySQLs.Modifier, _FromSpec<_RightParenClause<_UnionOrderBySpec<I>>>> createSelectClause() {
            final CriteriaContext context;
            context = CriteriaContexts.primaryQuery(this.bracket.context);
            return new SimpleSubQuery<>(context, this.bracket::parenRowSetEnd);
        }


    }//UnionLeftParenSubQueryClause

    private static final class UnionAndSubQueryClause<I extends Item>
            extends SelectClauseDispatcher<MySQLs.Modifier, MySQLQuery._FromSpec<I>>
            implements MySQLQuery._UnionAndQuerySpec<I> {

        private final SubQuery left;

        private final UnionType unionType;

        private final Function<SubQuery, I> function;

        private UnionAndSubQueryClause(SubQuery left, UnionType unionType, Function<SubQuery, I> function) {
            this.left = left;
            this.unionType = unionType;
            this.function = function;
        }


        @Override
        public _UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final CriteriaContext leftContext, context;
            leftContext = ((CriteriaContextSpec) this.left).getContext();
            context = CriteriaContexts.unionBracketContext(leftContext);

            final MySQLBracketSubQuery<I> bracket;
            bracket = new MySQLBracketSubQuery<>(context, this::unionRight);
            return new UnionLeftParenSubQueryClause<>(bracket);
        }

        @Override
        _DynamicHintModifierSelectClause<MySQLs.Modifier, _FromSpec<I>> createSelectClause() {
            final CriteriaContext leftContext, context;
            leftContext = ((CriteriaContextSpec) this.left).getContext();

            context = CriteriaContexts.unionSubQueryContext(leftContext);
            return new SimpleSubQuery<>(context, this::unionRight);
        }

        private I unionRight(final SubQuery right) {
            return this.function.apply(new UnionSubQuery(this.left, this.unionType, right));
        }


    }//UnionAndSubQueryClause


    private static final class UnionLeftParenSelectClause<I extends Item>
            extends SelectClauseDispatcher<MySQLs.Modifier, MySQLQuery._FromSpec<_RightParenClause<MySQLQuery._UnionOrderBySpec<I>>>>
            implements MySQLQuery._UnionAndQuerySpec<_RightParenClause<MySQLQuery._UnionOrderBySpec<I>>> {

        private final MySQLBracketSelect<I> bracket;

        private UnionLeftParenSelectClause(MySQLBracketSelect<I> bracket) {
            this.bracket = bracket;
        }

        @Override
        public _UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<_RightParenClause<_UnionOrderBySpec<I>>>>> leftParen() {
            final CriteriaContext context;
            context = CriteriaContexts.bracketContext(this.bracket.context);

            final MySQLBracketSelect<_RightParenClause<_UnionOrderBySpec<I>>> newBracket;
            newBracket = new MySQLBracketSelect<>(context, this.bracket::parenRowSetEnd);
            return new UnionLeftParenSelectClause<>(newBracket);
        }

        @Override
        _DynamicHintModifierSelectClause<MySQLs.Modifier, _FromSpec<_RightParenClause<_UnionOrderBySpec<I>>>> createSelectClause() {
            final CriteriaContext context;
            context = CriteriaContexts.primaryQuery(this.bracket.context);
            return new SimpleSelect<>(context, this.bracket::parenRowSetEnd);
        }


    }//UnionLeftParenSelectClause

    private static final class UnionAndSelectClause<I extends Item>
            extends SelectClauseDispatcher<MySQLs.Modifier, MySQLQuery._FromSpec<I>>
            implements MySQLQuery._UnionAndQuerySpec<I> {

        private final Select left;

        private final UnionType unionType;

        private final Function<Select, I> function;

        private UnionAndSelectClause(Select left, UnionType unionType, Function<Select, I> function) {
            this.left = left;
            this.unionType = unionType;
            this.function = function;
        }


        @Override
        public _UnionAndQuerySpec<_RightParenClause<_UnionOrderBySpec<I>>> leftParen() {
            final CriteriaContext leftContext, context;
            leftContext = ((CriteriaContextSpec) this.left).getContext();
            context = CriteriaContexts.unionBracketContext(leftContext);

            final MySQLBracketSelect<I> bracket;
            bracket = new MySQLBracketSelect<>(context, this::unionRight);
            return new UnionLeftParenSelectClause<>(bracket);
        }

        @Override
        _DynamicHintModifierSelectClause<MySQLs.Modifier, _FromSpec<I>> createSelectClause() {
            final CriteriaContext leftContext, context;
            leftContext = ((CriteriaContextSpec) this.left).getContext();

            context = CriteriaContexts.unionSelectContext(leftContext);
            return new SimpleSelect<>(context, this::unionRight);
        }

        private I unionRight(final Select right) {
            return this.function.apply(new UnionSelect(MySQLDialect.MySQL80, this.left, this.unionType, right));
        }


    }//UnionSelectClause


}
