package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchDml;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql._MySQLHint;
import io.army.criteria.impl.inner.mysql._MySQLMultiUpdate;
import io.army.criteria.impl.inner.mysql._MySQLWithClause;
import io.army.criteria.mysql.MySQLQuery;
import io.army.criteria.mysql.MySQLUpdate;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link SimpleUpdate} ,MySQL multi update api implementation</li>
 *         <li>{@link BatchUpdate} ,MySQL batch multi update api implementation</li>
 *     </ul>
 * </p>
 */
@SuppressWarnings("unchecked")
abstract class MySQLMultiUpdate<C, WE, SR, UT, US, UP, IR, JT, JS, JP, WR, WA>
        extends WithCteMultiUpdate<C, WE, SR, UT, US, UP, JT, JS, JP, WR, WA>
        implements MySQLUpdate.MultiUpdateClause<C, UT, US, UP>, MySQLQuery._IndexHintClause<C, IR, UT>
        , MySQLQuery._IndexForJoinClause<C, UT>, MySQLQuery._MySQLJoinClause<C, JT, JS>
        , Statement._CrossJoinClause<C, UT, US>, MySQLQuery._MySQLDialectJoinClause<C, JP>
        , DialectStatement._DialectCrossJoinClause<C, UP>, _MySQLMultiUpdate {


    static <C> _MultiUpdate57Clause<C> simple(@Nullable C criteria) {
        return new SimpleUpdate<>(criteria);
    }

    static <C> _BatchMultiUpdateClause<C> batch(@Nullable C criteria) {
        return new BatchUpdate<>(criteria);
    }

    private List<_MySQLHint> hintList;

    private List<MySQLWords> modifierList;

    private MySQLIndexHint.Command command;

    private boolean updateCrossValid = true;

    private MySQLMultiUpdate(@Nullable C criteria) {
        super(CriteriaContexts.multiDmlContext(criteria));
        CriteriaContextStack.setContextStack(this.criteriaContext);
    }

    @Override
    public final UP update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
            , TableMeta<?> table) {
        this.hintList = MySQLUtils.asHintList(hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isUpdateModifier);
        return (UP) this.createClause(_JoinType.NONE, table);
    }

    @Override
    public final UT update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
            , TableMeta<?> table, String tableAlias) {
        this.hintList = MySQLUtils.asHintList(hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isUpdateModifier);
        this.createAndAddBlock(_JoinType.NONE, table, tableAlias);
        return (UT) this;
    }

    @Override
    public final UP update(TableMeta<?> table) {
        return (UP) this.createClause(_JoinType.NONE, table);
    }

    @Override
    public final UT update(TableMeta<?> table, String tableAlias) {
        this.createAndAddBlock(_JoinType.NONE, table, tableAlias);
        return (UT) this;
    }

    @Override
    public final <T extends TableItem> US update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
            , Supplier<T> supplier, String alias) {
        this.hintList = MySQLUtils.asHintList(hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isUpdateModifier);
        this.createAndAddBlock(_JoinType.NONE, supplier.get(), alias);
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US update(Supplier<T> supplier, String alias) {
        this.createAndAddBlock(_JoinType.NONE, supplier.get(), alias);
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US update(Supplier<List<Hint>> hints, List<MySQLWords> modifiers
            , Function<C, T> function, String alias) {
        this.hintList = MySQLUtils.asHintList(hints.get(), MySQLHints::castHint);
        this.modifierList = MySQLUtils.asModifierList(modifiers, MySQLUtils::isUpdateModifier);
        this.createAndAddBlock(_JoinType.NONE, function.apply(this.criteria), alias);
        return (US) this;
    }

    @Override
    public final <T extends TableItem> US update(Function<C, T> function, String alias) {
        this.createAndAddBlock(_JoinType.NONE, function.apply(this.criteria), alias);
        return (US) this;
    }

    /*################################## blow IndexHintClause method ##################################*/

    @Override
    public final IR useIndex() {
        if (this.updateCrossValid) {
            this.command = MySQLIndexHint.Command.USER_INDEX;
        }
        return (IR) this;
    }

    @Override
    public final IR ignoreIndex() {
        if (this.updateCrossValid) {
            this.command = MySQLIndexHint.Command.IGNORE_INDEX;
        }
        return (IR) this;
    }

    @Override
    public final IR forceIndex() {
        if (this.updateCrossValid) {
            this.command = MySQLIndexHint.Command.FORCE_INDEX;
        }
        return (IR) this;
    }

    @Override
    public final IR ifUseIndex(Predicate<C> predicate) {
        if (this.updateCrossValid && predicate.test(this.criteria)) {
            this.command = MySQLIndexHint.Command.USER_INDEX;
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifIgnoreIndex(Predicate<C> predicate) {
        if (this.updateCrossValid && predicate.test(this.criteria)) {
            this.command = MySQLIndexHint.Command.IGNORE_INDEX;
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final IR ifForceIndex(Predicate<C> predicate) {
        if (this.updateCrossValid && predicate.test(this.criteria)) {
            this.command = MySQLIndexHint.Command.FORCE_INDEX;
        } else {
            this.command = null;
        }
        return (IR) this;
    }

    @Override
    public final UT useIndex(List<String> indexList) {
        if (this.updateCrossValid) {
            this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, false, indexList);
        }
        return (UT) this;
    }

    @Override
    public final UT ignoreIndex(List<String> indexList) {
        if (this.updateCrossValid) {
            this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, false, indexList);
        }
        return (UT) this;
    }

    @Override
    public final UT forceIndex(List<String> indexList) {
        if (this.updateCrossValid) {
            this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, false, indexList);
        }
        return (UT) this;
    }

    @Override
    public final UT ifUseIndex(Function<C, List<String>> function) {
        if (this.updateCrossValid) {
            final List<String> indexList;
            indexList = function.apply(this.criteria);
            if (indexList != null && indexList.size() > 0) {
                this.addIndexHint(MySQLIndexHint.Command.USER_INDEX, false, indexList);
            }
        }
        return (UT) this;
    }

    @Override
    public final UT ifIgnoreIndex(Function<C, List<String>> function) {
        if (this.updateCrossValid) {
            final List<String> indexList;
            indexList = function.apply(this.criteria);
            if (indexList != null && indexList.size() > 0) {
                this.addIndexHint(MySQLIndexHint.Command.IGNORE_INDEX, false, indexList);
            }
        }
        return (UT) this;
    }

    @Override
    public final UT ifForceIndex(Function<C, List<String>> function) {
        if (this.updateCrossValid) {
            final List<String> indexList;
            indexList = function.apply(this.criteria);
            if (indexList != null && indexList.size() > 0) {
                this.addIndexHint(MySQLIndexHint.Command.FORCE_INDEX, false, indexList);
            }
        }
        return (UT) this;
    }

    /*################################## blow IndexJoinClause method ##################################*/

    @Override
    public final UT forJoin(List<String> indexList) {
        final MySQLIndexHint.Command command = this.command;
        if (command != null) {
            this.command = null;
            this.addIndexHint(command, true, indexList);
        }
        return (UT) this;
    }

    @Override
    public final UT forJoin(Function<C, List<String>> function) {
        final MySQLIndexHint.Command command = this.command;
        if (command != null) {
            this.command = null;
            this.addIndexHint(command, true, function.apply(this.criteria));
        }
        return (UT) this;
    }


    @Override
    public final List<_MySQLHint> hintList() {
        return this.hintList;
    }

    @Override
    public final List<MySQLWords> modifierList() {
        return this.modifierList;
    }


    /*################################## blow package template method ##################################*/


    @Override
    final void doOnAsUpdate() {
        this.command = null;
        if (this.hintList == null) {
            this.hintList = Collections.emptyList();
        }
        if (this.modifierList == null) {
            this.modifierList = Collections.emptyList();
        }

        if (this instanceof SimpleUpdate) {
            final SimpleUpdate<C> stmt = (SimpleUpdate<C>) this;
            if (stmt.cteList == null) {
                stmt.cteList = Collections.emptyList();
            }
            stmt.noActionOnClause = null;
            stmt.noActionPartitionJoinClause = null;
            stmt.noActionPartitionOnClause = null;
        } else if (this instanceof BatchUpdate) {
            final BatchUpdate<C> stmt = (BatchUpdate<C>) this;
            if (stmt.paramList == null) {
                throw _Exceptions.batchParamEmpty();
            }
            if (stmt.cteList == null) {
                stmt.cteList = Collections.emptyList();
            }
            stmt.noActionOnClause = null;
            stmt.noActionPartitionJoinClause = null;
            stmt.noActionPartitionOnClause = null;
        } else {
            throw new IllegalStateException();
        }

    }

    @Override
    final void onClear() {
        this.hintList = null;
        this.modifierList = null;

        if (this instanceof SimpleUpdate) {
            final SimpleUpdate<C> stmt = (SimpleUpdate<C>) this;

            stmt.cteList = null;
            stmt.noActionOnClause = null;
            stmt.noActionPartitionJoinClause = null;
            stmt.noActionPartitionOnClause = null;
        } else if (this instanceof BatchUpdate) {
            final BatchUpdate<C> stmt = (BatchUpdate<C>) this;

            stmt.paramList = null;
            stmt.cteList = null;
            stmt.noActionOnClause = null;
            stmt.noActionPartitionJoinClause = null;

            stmt.noActionPartitionOnClause = null;
        } else {
            throw new IllegalStateException();
        }

    }

    @Override
    final Dialect defaultDialect() {
        return MySQLUtils.defaultDialect(this);
    }

    @Override
    final void validateDialect(Dialect dialect) {
        MySQLUtils.validateDialect(this, dialect);
    }

    @Override
    final void crossJoinEvent(boolean success) {
        this.updateCrossValid = success;
    }


    /*################################## blow private method ##################################*/

    private UT addIndexHint(final MySQLIndexHint.Command command, final boolean forJoin
            , final @Nullable List<String> indexNames) {

        if (indexNames == null || indexNames.size() == 0) {
            throw MySQLUtils.indexListIsEmpty();
        }
        final _TableBlock block;
        block = this.criteriaContext.lastTableBlockWithoutOnClause();
        if (!(block instanceof MySQLNoOnBlock)) {
            throw _Exceptions.castCriteriaApi();
        }
        final MySQLNoOnBlock noOnBlock = (MySQLNoOnBlock) block;
        List<MySQLIndexHint> indexHintList = noOnBlock.indexHintList;
        if (indexHintList == null) {
            indexHintList = new ArrayList<>();
            noOnBlock.indexHintList = indexHintList;
        }
        final MySQLIndexHint.Purpose purpose;
        if (forJoin) {
            purpose = MySQLIndexHint.Purpose.FOR_JOIN;
        } else {
            purpose = null;
        }
        indexHintList.add(new MySQLIndexHint(command, purpose, indexNames));
        return (UT) this;
    }


    /*################################## blow inner class  ##################################*/


    /**
     * <p>
     * This class is the implementation of  multi-table update api.
     * </p>
     */
    private static final class SimpleUpdate<C> extends MySQLMultiUpdate<
            C,
            MySQLUpdate._MultiUpdate57Clause<C>,                    //WE
            MySQLUpdate._MultiWhereSpec<C>,                         //SR
            MySQLUpdate._IndexHintJoinSpec<C>,                      //UT
            MySQLUpdate._MultiJoinSpec<C>,                          //US
            MySQLUpdate._MultiPartitionJoinClause<C>,               //UP
            MySQLUpdate._IndexForJoinJoinClause<C>,                 //IR
            MySQLUpdate._MultiIndexHintOnSpec<C>,                   //JT
            Statement._OnClause<C, MySQLUpdate._MultiJoinSpec<C>>,  //JS
            MySQLUpdate._MultiPartitionOnClause<C>,                 //JP
            Update._UpdateSpec,                                     //WR
            MySQLUpdate._MultiWhereAndSpec<C>>                      //WA
            implements MySQLUpdate._MultiWhereAndSpec<C>, MySQLUpdate._MultiJoinSpec<C>
            , MySQLUpdate._IndexHintJoinSpec<C>, MySQLUpdate._WithAndMultiUpdateSpec<C>
            , MySQLUpdate._IndexForJoinJoinClause<C>, MySQLUpdate._MultiWhereSpec<C>, _MySQLWithClause {

        private boolean recursive;

        private List<Cte> cteList;

        private _MultiIndexHintOnSpec<C> noActionOnClause;

        private _MultiPartitionOnClause<C> noActionPartitionOnClause;

        private _MultiPartitionJoinClause<C> noActionPartitionJoinClause;

        private SimpleUpdate(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public List<Cte> cteList() {
            return this.cteList;
        }

        @Override
        void doWithCte(boolean recursive, List<Cte> cteList) {
            this.recursive = recursive;
            this.cteList = cteList;
        }


        @Override
        public _TableBlock createAndAddBlock(final _JoinType joinType, final Object item, final String alias) {
            Objects.requireNonNull(item);
            final _TableBlock tableBlock;
            switch (joinType) {
                case NONE:
                case CROSS_JOIN: {
                    if (item instanceof TableMeta) {
                        tableBlock = new MySQLNoOnBlock(joinType, item, alias);
                    } else {
                        tableBlock = new TableBlock.NoOnTableBlock(joinType, item, alias);
                    }
                }
                break;
                case LEFT_JOIN:
                case JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN:
                case STRAIGHT_JOIN: {
                    if (item instanceof TableMeta) {
                        tableBlock = new SimpleIndexHintOnBlock<>(joinType, (TableMeta<?>) item, alias, this);
                    } else {
                        tableBlock = new OnClauseTableBlock<>(joinType, item, alias, this);
                    }
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(joinType);
            }
            return tableBlock;
        }

        @Override
        public Object createClause(final _JoinType joinType, final TableMeta<?> table) {
            final Object clause;
            switch (joinType) {
                case NONE:
                case CROSS_JOIN:
                    clause = new SimplePartitionJoinClause<>(joinType, table, this);
                    break;
                case LEFT_JOIN:
                case JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN:
                case STRAIGHT_JOIN:
                    clause = new SimplePartitionOnClause<>(joinType, table, this);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(joinType);
            }
            return clause;
        }

        @Override
        public Object getNoActionClause(final _JoinType joinType) {
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
        public Object getNoActionClauseBeforeAs(_JoinType joinType) {
            final Object noActionClause;
            switch (joinType) {
                case NONE:
                case CROSS_JOIN: {
                    _MultiPartitionJoinClause<C> clause = this.noActionPartitionJoinClause;
                    if (clause == null) {
                        clause = new SimpleNoActionPartitionJoinClause<>(this);
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
                    _MultiPartitionOnClause<C> clause = this.noActionPartitionOnClause;
                    if (clause == null) {
                        clause = new SimpleNoActionPartitionOnClause<>(this::getNoActionOnClause);
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

        private _MultiIndexHintOnSpec<C> getNoActionOnClause() {
            _MultiIndexHintOnSpec<C> clause = this.noActionOnClause;
            if (clause == null) {
                clause = new SimpleNoActionIndexHintOnClause<>(this);
                this.noActionOnClause = clause;
            }
            return clause;
        }


    }// SimpleMultiUpdate

    /**
     * <p>
     * This class is the implementation of batch multi-table update api.
     * </p>
     *
     * @see #batch(Object)
     */
    private static final class BatchUpdate<C> extends MySQLMultiUpdate<
            C,
            MySQLUpdate._BatchMultiUpdateClause<C>,             //WE
            MySQLUpdate._BatchMultiWhereSpec<C>,                //SR
            MySQLUpdate._BatchMultiIndexHintJoinSpec<C>,        //UT
            MySQLUpdate._BatchMultiJoinSpec<C>,                 //US
            MySQLUpdate._BatchMultiPartitionJoinClause<C>,      //UP
            MySQLUpdate._BatchIndexForJoinJoinClause<C>,        //IR
            MySQLUpdate._BatchMultiIndexHintOnSpec<C>,          //JT
            Statement._OnClause<C, _BatchMultiJoinSpec<C>>,     //JS
            MySQLUpdate._BatchMultiPartitionOnClause<C>,        //JP
            MySQLUpdate._BatchParamClause<C, _UpdateSpec>,      //WR
            MySQLUpdate._BatchMultiWhereAndSpec<C>>             //WA
            implements MySQLUpdate._BatchWithAndMultiUpdateSpec<C>, MySQLUpdate._BatchMultiJoinSpec<C>
            , MySQLUpdate._BatchMultiWhereSpec<C>, MySQLUpdate._BatchMultiWhereAndSpec<C>
            , MySQLUpdate._BatchMultiIndexHintJoinSpec<C>, MySQLUpdate._BatchIndexForJoinJoinClause<C>
            , _BatchDml, _MySQLWithClause {

        private boolean recursive;

        private List<Cte> cteList;

        private List<?> paramList;

        private _BatchMultiIndexHintOnSpec<C> noActionOnClause;

        private _BatchMultiPartitionJoinClause<C> noActionPartitionJoinClause;

        private _BatchMultiPartitionOnClause<C> noActionPartitionOnClause;


        private BatchUpdate(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        void doWithCte(boolean recursive, List<Cte> cteList) {
            this.recursive = recursive;
            this.cteList = cteList;
        }

        @Override
        public <P> _UpdateSpec paramList(List<P> paramList) {
            this.paramList = MySQLUtils.paramList(paramList);
            return this;
        }

        @Override
        public <P> _UpdateSpec paramList(Supplier<List<P>> supplier) {
            this.paramList = MySQLUtils.paramList(supplier.get());
            return this;
        }

        @Override
        public <P> _UpdateSpec paramList(Function<C, List<P>> function) {
            this.paramList = MySQLUtils.paramList(function.apply(this.criteria));
            return this;
        }

        @Override
        public _UpdateSpec paramList(Function<String, ?> function, String keyName) {
            this.paramList = MySQLUtils.paramList((List<?>) function.apply(keyName));
            return this;
        }


        @Override
        public _TableBlock createAndAddBlock(final _JoinType joinType, final Object item, final String alias) {
            Objects.requireNonNull(item);
            final _TableBlock tableBlock;
            switch (joinType) {
                case NONE:
                case CROSS_JOIN: {
                    if (item instanceof TableMeta) {
                        tableBlock = new MySQLNoOnBlock(joinType, item, alias);
                    } else {
                        tableBlock = new TableBlock.NoOnTableBlock(joinType, item, alias);
                    }
                }
                break;
                case LEFT_JOIN:
                case JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN:
                case STRAIGHT_JOIN: {
                    if (item instanceof TableMeta) {
                        tableBlock = new BatchIndexHintOnBlock<>(joinType, (TableMeta<?>) item, alias, this);
                    } else {
                        tableBlock = new OnClauseTableBlock<>(joinType, item, alias, this);
                    }
                }
                break;
                default:
                    throw _Exceptions.unexpectedEnum(joinType);
            }
            return tableBlock;
        }

        @Override
        public Object createClause(final _JoinType joinType, final TableMeta<?> table) {
            final Object clause;
            switch (joinType) {
                case NONE:
                case CROSS_JOIN:
                    clause = new BatchPartitionJoinClause<>(joinType, table, this);
                    break;
                case LEFT_JOIN:
                case JOIN:
                case RIGHT_JOIN:
                case FULL_JOIN:
                case STRAIGHT_JOIN:
                    clause = new BatchPartitionOnClause<>(joinType, table, this);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(joinType);
            }
            return clause;
        }

        @Override
        public Object getNoActionClause(final _JoinType joinType) {
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
        public Object getNoActionClauseBeforeAs(final _JoinType joinType) {
            final Object noActionClause;
            switch (joinType) {
                case NONE:
                case CROSS_JOIN: {
                    _BatchMultiPartitionJoinClause<C> clause = this.noActionPartitionJoinClause;
                    if (clause == null) {
                        clause = new BatchNoActionPartitionJoinClause<>(this);
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
                    _BatchMultiPartitionOnClause<C> clause = this.noActionPartitionOnClause;
                    if (clause == null) {
                        clause = new BatchNoActionPartitionOnClause<>(this::getNoActionOnClause);
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
        public List<?> paramList() {
            return this.paramList;
        }

        @Override
        public boolean isRecursive() {
            return this.recursive;
        }

        @Override
        public List<Cte> cteList() {
            return this.cteList;
        }


        private _BatchMultiIndexHintOnSpec<C> getNoActionOnClause() {
            _BatchMultiIndexHintOnSpec<C> clause = this.noActionOnClause;
            if (clause == null) {
                clause = new BatchNoActionIndexHintOnClause<>(this);
                this.noActionOnClause = clause;
            }
            return clause;
        }


    }// BatchUpdate


    private static final class SimplePartitionJoinClause<C>
            extends MySQLPartitionClause<C, _AsClause<_IndexHintJoinSpec<C>>>
            implements _MultiPartitionJoinClause<C>, _AsClause<_IndexHintJoinSpec<C>> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final SimpleUpdate<C> update;

        private SimplePartitionJoinClause(_JoinType joinType, TableMeta<?> table, SimpleUpdate<C> update) {
            super(update.criteria);
            this.joinType = joinType;
            this.table = table;
            this.update = update;
        }

        @Override
        public _IndexHintJoinSpec<C> as(final String tableAlias) {
            Objects.requireNonNull(tableAlias);
            final List<String> partitionList = this.partitionList;
            final _TableBlock block;
            if (partitionList == null) {
                block = new MySQLNoOnBlock(this.joinType, this.table, tableAlias);
            } else {
                block = new MySQLNoOnBlock(this.joinType, this.table, tableAlias, partitionList);
            }
            this.update.criteriaContext.onAddBlock(block);
            if (this.joinType == _JoinType.CROSS_JOIN) {
                this.update.crossJoinEvent(true);
            }
            return this.update;
        }

    }// SimplePartitionJoinClause


    private static final class SimplePartitionOnClause<C>
            extends MySQLPartitionClause<C, _AsClause<_MultiIndexHintOnSpec<C>>>
            implements _AsClause<_MultiIndexHintOnSpec<C>>, _MultiPartitionOnClause<C> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final SimpleUpdate<C> update;

        private SimplePartitionOnClause(_JoinType joinType, TableMeta<?> table, SimpleUpdate<C> update) {
            super(update.criteria);
            this.joinType = joinType;
            this.table = table;
            this.update = update;
        }

        @Override
        public _MultiIndexHintOnSpec<C> as(final String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final SimpleIndexHintOnBlock<C> block;
            if (partitionList == null) {
                block = new SimpleIndexHintOnBlock<>(this.joinType, this.table, alias, this.update);
            } else {
                block = new SimpleIndexHintOnBlock<>(this.joinType, this.table, alias, partitionList, this.update);
            }
            this.update.criteriaContext.onAddBlock(block);
            return block;
        }

    }// SimplePartitionOnClause


    private static class SimpleIndexHintOnBlock<C> extends MySQLIndexHintOnBlock<
            C,
            MySQLUpdate._IndexForJoinOnClause<C>,
            MySQLUpdate._MultiIndexHintOnSpec<C>,
            MySQLUpdate._MultiJoinSpec<C>>
            implements MySQLUpdate._MultiIndexHintOnSpec<C>, MySQLUpdate._IndexForJoinOnClause<C> {

        public SimpleIndexHintOnBlock(_JoinType joinType, TableMeta<?> table, String alias, _MultiJoinSpec<C> stmt) {
            super(joinType, table, alias, stmt);
        }

        public SimpleIndexHintOnBlock(_JoinType joinType, TableMeta<?> table, String alias
                , List<String> partitionList, _MultiJoinSpec<C> stmt) {
            super(joinType, table, alias, partitionList, stmt);
        }

    }// SimpleIndexHintOnBlock


    private static final class SimpleNoActionIndexHintOnClause<C> extends MySQLNoActionIndexHintOnClause<
            C,
            _IndexForJoinOnClause<C>,
            _MultiIndexHintOnSpec<C>,
            _MultiJoinSpec<C>>
            implements _MultiIndexHintOnSpec<C>, _IndexForJoinOnClause<C> {

        private SimpleNoActionIndexHintOnClause(_MultiJoinSpec<C> update) {
            super(update);
        }

    }// SimpleNoActionIndexHintOnBlock

    private static final class SimpleNoActionPartitionJoinClause<C> extends MySQLNoActionPartitionClause<
            C,
            _AsClause<MySQLUpdate._IndexHintJoinSpec<C>>>
            implements MySQLUpdate._MultiPartitionJoinClause<C>, _AsClause<_IndexHintJoinSpec<C>> {

        private final _IndexHintJoinSpec<C> spec;

        private SimpleNoActionPartitionJoinClause(_IndexHintJoinSpec<C> spec) {
            this.spec = spec;
        }

        @Override
        public _IndexHintJoinSpec<C> as(String alias) {
            return this.spec;
        }

    }// SimpleNoActionPartitionJoinClause


    private static final class SimpleNoActionPartitionOnClause<C> extends MySQLNoActionPartitionClause<
            C,
            _AsClause<_MultiIndexHintOnSpec<C>>>
            implements _MultiPartitionOnClause<C>, _AsClause<_MultiIndexHintOnSpec<C>> {

        private final Supplier<_MultiIndexHintOnSpec<C>> supplier;


        private SimpleNoActionPartitionOnClause(Supplier<_MultiIndexHintOnSpec<C>> supplier) {
            this.supplier = supplier;
        }

        @Override
        public _MultiIndexHintOnSpec<C> as(String alias) {
            return this.supplier.get();
        }

    }// SimpleNoActionPartitionOnClause


    private static final class BatchIndexHintOnBlock<C> extends MySQLIndexHintOnBlock<
            C,
            MySQLUpdate._BatchIndexForJoinOnClause<C>,
            MySQLUpdate._BatchMultiIndexHintOnSpec<C>,
            MySQLUpdate._BatchMultiJoinSpec<C>>
            implements MySQLUpdate._BatchMultiIndexHintOnSpec<C>, MySQLUpdate._BatchIndexForJoinOnClause<C> {

        private BatchIndexHintOnBlock(_JoinType joinType, TableMeta<?> table
                , String alias, _BatchMultiJoinSpec<C> stmt) {
            super(joinType, table, alias, stmt);
        }

        private BatchIndexHintOnBlock(_JoinType joinType, TableMeta<?> table
                , String alias, List<String> partitionList
                , _BatchMultiJoinSpec<C> stmt) {
            super(joinType, table, alias, partitionList, stmt);
        }

    }//BatchIndexHintOnBlock


    private static final class BatchPartitionJoinClause<C>
            extends MySQLPartitionClause<C, _AsClause<_BatchMultiIndexHintJoinSpec<C>>>
            implements _BatchMultiPartitionJoinClause<C>, _AsClause<_BatchMultiIndexHintJoinSpec<C>> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final BatchUpdate<C> update;

        private BatchPartitionJoinClause(_JoinType joinType, TableMeta<?> table, BatchUpdate<C> update) {
            super(update.criteria);
            this.joinType = joinType;
            this.table = table;
            this.update = update;
        }

        @Override
        public _BatchMultiIndexHintJoinSpec<C> as(String alias) {
            Objects.requireNonNull(alias);
            final List<String> partitionList = this.partitionList;
            final _TableBlock block;
            if (partitionList == null) {
                block = new MySQLNoOnBlock(this.joinType, this.table, alias);
            } else {
                block = new MySQLNoOnBlock(this.joinType, this.table, alias, partitionList);
            }
            this.update.criteriaContext.onAddBlock(block);
            if (this.joinType == _JoinType.CROSS_JOIN) {
                this.update.crossJoinEvent(true);
            }
            return this.update;
        }
    }// BatchPartitionJoinClause


    private static final class BatchPartitionOnClause<C>
            extends MySQLPartitionClause<C, _AsClause<_BatchMultiIndexHintOnSpec<C>>>
            implements _AsClause<_BatchMultiIndexHintOnSpec<C>>
            , _BatchMultiPartitionOnClause<C> {

        private final _JoinType joinType;

        private final TableMeta<?> table;

        private final BatchUpdate<C> update;

        private BatchPartitionOnClause(_JoinType joinType, TableMeta<?> table, BatchUpdate<C> update) {
            super(update.criteria);
            this.update = update;
            this.joinType = joinType;
            this.table = table;
        }

        @Override
        public _BatchMultiIndexHintOnSpec<C> as(final String tableAlias) {
            Objects.requireNonNull(tableAlias);
            final List<String> partitionList = this.partitionList;
            final BatchIndexHintOnBlock<C> block;
            if (partitionList == null) {
                block = new BatchIndexHintOnBlock<>(this.joinType, this.table, tableAlias, this.update);
            } else {
                block = new BatchIndexHintOnBlock<>(this.joinType, this.table, tableAlias
                        , partitionList, this.update);
            }
            this.update.criteriaContext.onAddBlock(block);
            return block;
        }


    }// BatchPartitionOnBlock


    private static final class BatchNoActionIndexHintOnClause<C> extends MySQLNoActionIndexHintOnClause<
            C,
            MySQLUpdate._BatchIndexForJoinOnClause<C>,
            MySQLUpdate._BatchMultiIndexHintOnSpec<C>,
            MySQLUpdate._BatchMultiJoinSpec<C>>
            implements _BatchMultiIndexHintOnSpec<C>, _BatchIndexForJoinOnClause<C> {

        private BatchNoActionIndexHintOnClause(_BatchMultiJoinSpec<C> update) {
            super(update);
        }

    }// BatchNoActionIndexHintOnClause

    private static final class BatchNoActionPartitionJoinClause<C> extends MySQLNoActionPartitionClause<
            C, _AsClause<_BatchMultiIndexHintJoinSpec<C>>>
            implements _BatchMultiPartitionJoinClause<C>, _AsClause<_BatchMultiIndexHintJoinSpec<C>> {

        private final _BatchMultiIndexHintJoinSpec<C> spec;

        private BatchNoActionPartitionJoinClause(_BatchMultiIndexHintJoinSpec<C> spec) {
            this.spec = spec;
        }

        @Override
        public _BatchMultiIndexHintJoinSpec<C> as(String alias) {
            return this.spec;
        }

    }//BatchNoActionPartitionJoinClause


    private static final class BatchNoActionPartitionOnClause<C> extends MySQLNoActionPartitionClause<
            C, _AsClause<_BatchMultiIndexHintOnSpec<C>>>
            implements _BatchMultiPartitionOnClause<C>, _AsClause<_BatchMultiIndexHintOnSpec<C>> {

        private final Supplier<_BatchMultiIndexHintOnSpec<C>> supplier;

        private BatchNoActionPartitionOnClause(Supplier<_BatchMultiIndexHintOnSpec<C>> supplier) {
            this.supplier = supplier;
        }

        @Override
        public _BatchMultiIndexHintOnSpec<C> as(String tableAlias) {
            return this.supplier.get();
        }
    }//BatchNoActionPartitionOnClause


}
