package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.criteria.impl.inner._StandardQuery;
import io.army.criteria.impl.inner._TableBlock;
import io.army.dialect.mysql.MySQLDialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is a implementation of {@link StandardQuery}.
 * </p>
 *
 * @see StandardUnionQuery
 * @since 1.0
 */
abstract class StandardSimpleQuery<C, Q extends Query> extends SimpleQuery<
        C,
        Q,
        SQLs.Modifier,
        StandardQuery._StandardFromSpec<C, Q>, // SR
        StandardQuery._JoinSpec<C, Q>,// FT
        StandardQuery._JoinSpec<C, Q>,// FS
        Void,                               // FP
        StandardQuery._JoinSpec<C, Q>,     // FJ
        Statement._OnClause<C, StandardQuery._JoinSpec<C, Q>>, // JT
        Statement._OnClause<C, StandardQuery._JoinSpec<C, Q>>, // JS
        Void,                               // JP
        StandardQuery._GroupBySpec<C, Q>, // WR
        StandardQuery._WhereAndSpec<C, Q>, // AR
        StandardQuery._HavingSpec<C, Q>, // GR
        StandardQuery._OrderBySpec<C, Q>, // HR
        StandardQuery._LimitSpec<C, Q>, // OR
        StandardQuery._LockSpec<C, Q>, // LR
        StandardQuery._UnionOrderBySpec<C, Q>, // UR
        StandardQuery._StandardSelectClause<C, Q>> // SP

        implements StandardQuery, StandardQuery._StandardSelectClause<C, Q>, StandardQuery._StandardFromSpec<C, Q>
        , StandardQuery._JoinSpec<C, Q>, StandardQuery._WhereAndSpec<C, Q>, StandardQuery._HavingSpec<C, Q>
        , _StandardQuery {


    static <C> _StandardSelectClause<C, Select> query(@Nullable C criteria) {
        return new SimpleSelect<>(CriteriaContexts.primaryQueryContext(criteria));
    }

    static <C> _StandardSelectClause<C, SubQuery> subQuery(@Nullable C criteria) {
        return new SimpleSubQuery<>(CriteriaContexts.subQueryContext(criteria));
    }

    static <C> _StandardSelectClause<C, ScalarExpression> scalarSubQuery(@Nullable C criteria) {
        return new SimpleScalarSubQuery<>(CriteriaContexts.subQueryContext(criteria));
    }

    static <C, CT> StandardQuery._StandardSelectClause<C, StandardInsert._ParentInsertQuery<CT>> parentInsertQuery(@Nullable C criteria, Function<SubQuery, StandardInsert._ChildPartSpec<CT>> function) {
        return new ParentInsertSubQuery<>(CriteriaContexts.subQueryContext(criteria), function);
    }

    static <C> StandardQuery._StandardSelectClause<C, StandardInsert._InsertQuery> insertQuery(@Nullable C criteria, Function<SubQuery, Insert._InsertSpec> function) {
        return new InsertSubQuery<>(CriteriaContexts.subQueryContext(criteria), function);
    }

    @SuppressWarnings("unchecked")
    static <C, Q extends Query> _StandardSelectClause<C, Q> unionAndQuery(Q query, UnionType unionType) {
        final _StandardSelectClause<C, ?> spec;
        final CriteriaContext criteriaContext;
        if (query instanceof Select) {
            criteriaContext = CriteriaContexts.primaryQueryContextFrom(query);
            spec = new UnionAndSelect<>((Select) query, unionType, criteriaContext);
        } else if (query instanceof ScalarSubQuery) {
            criteriaContext = CriteriaContexts.subQueryContextFrom(query);
            spec = new UnionAndScalarSubQuery<>((ScalarExpression) query, unionType, criteriaContext);
        } else if (query instanceof SubQuery) {
            criteriaContext = CriteriaContexts.subQueryContextFrom(query);
            spec = new UnionAndSubQuery<>((SubQuery) query, unionType, criteriaContext);
        } else {
            throw _Exceptions.unknownRowSetType(query);
        }
        return (_StandardSelectClause<C, Q>) spec;
    }


    private LockMode lockMode;

    private StandardSimpleQuery(CriteriaContext context) {
        super(context);

    }

    @Override
    public final _UnionSpec<C, Q> lock(@Nullable LockMode lockMode) {
        if (lockMode == null) {
            throw CriteriaContextStack.nullPointer(this.context);
        }
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> lock(Function<C, LockMode> function) {
        final LockMode lockMode;
        lockMode = function.apply(this.criteria);
        if (lockMode == null) {
            throw CriteriaContextStack.nullPointer(this.context);
        }
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> ifLock(@Nullable LockMode lockMode) {
        this.lockMode = lockMode;
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> ifLock(Supplier<LockMode> supplier) {
        this.lockMode = supplier.get();
        return this;
    }

    @Override
    public final _UnionSpec<C, Q> ifLock(Function<C, LockMode> function) {
        this.lockMode = function.apply(this.criteria);
        return this;
    }

    @Override
    public final String toString() {
        final String s;
        if (this instanceof Select && this.isPrepared()) {
            s = this.mockAsString(MySQLDialect.MySQL57, Visible.ONLY_VISIBLE, true);
        } else {
            s = super.toString();
        }
        return s;
    }


    @Override
    public final _TableBlock createNoOnTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String alias) {
        if (itemWord != null) {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        return new TableBlock.NoOnTableBlock(joinType, table, alias);
    }

    @Override
    public final _TableBlock createNoOnItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
        if (itemWord != null) {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        return new TableBlock.NoOnTableBlock(joinType, tableItem, alias);
    }

    @Override
    public final _TableBlock createDynamicBlock(final _JoinType joinType, final DynamicBlock<?> block) {
        return CriteriaUtils.createStandardDynamicBlock(joinType, block);
    }

    @Override
    public final _OnClause<C, _JoinSpec<C, Q>> createTableBlock(_JoinType joinType, @Nullable ItemWord itemWord, TableMeta<?> table, String tableAlias) {
        if (itemWord != null) {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        return new OnClauseTableBlock<>(joinType, table, tableAlias, this);
    }

    @Override
    public final _OnClause<C, _JoinSpec<C, Q>> createItemBlock(_JoinType joinType, @Nullable ItemWord itemWord, TabularItem tableItem, String alias) {
        if (itemWord != null) {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        return new OnClauseTableBlock<>(joinType, tableItem, alias, this);
    }


    @Override
    final _StandardSelectClause<C, Q> asUnionAndRowSet(final UnionType unionType) {
        return StandardSimpleQuery.unionAndQuery(this.asQuery(), unionType);
    }


    @Override
    final Q onAsQuery(final boolean fromAsQueryMethod) {
        return this.finallyAsQuery(fromAsQueryMethod);
    }


    @Override
    final void onClear() {
        this.lockMode = null;
    }

    @Override
    final _UnionOrderBySpec<C, Q> createBracketQuery(RowSet rowSet) {
        return StandardUnionQuery.bracketQuery(rowSet);
    }

    @SuppressWarnings("unchecked")
    @Override
    final _UnionOrderBySpec<C, Q> createUnionRowSet(RowSet left, UnionType unionType, RowSet right) {
        return StandardUnionQuery.unionQuery((Q) left, unionType, right);
    }

    @Override
    final _UnionOrderBySpec<C, Q> getNoActionUnionRowSet(RowSet rowSet) {
        return StandardUnionQuery.noActionQuery(rowSet);
    }

    @Override
    final List<SQLs.Modifier> asModifierList(@Nullable List<SQLs.Modifier> modifiers) {
        return CriteriaUtils.asModifierList(this.context, modifiers, CriteriaUtils::standardModifier);
    }

    @Override
    final List<Hint> asHintList(@Nullable List<Hint> hints) {
        //standard statement don't hints
        throw CriteriaContextStack.castCriteriaApi(this.context);
    }

    @Override
    public final LockMode lockMode() {
        return this.lockMode;
    }



    /*################################## blow private inter class method ##################################*/


    private static final class SimpleSelect<C> extends StandardSimpleQuery<C, Select>
            implements Select {

        private SimpleSelect(CriteriaContext criteriaContext) {
            super(criteriaContext);
        }

    }//SimpleSelect


    /**
     * @see #subQuery(Object)
     */
    private static class SimpleSubQuery<C, Q extends SubQuery> extends StandardSimpleQuery<C, Q>
            implements SubQuery {

        private SimpleSubQuery(CriteriaContext criteriaContext) {
            super(criteriaContext);
        }


    } // SimpleSubQuery

    interface ParentInsertSubQuerySpec<CT> {

        StandardInsert._ChildPartSpec<CT> fromLeft(SubQuery query);

    }


    private static final class ParentInsertSubQuery<C, CT>
            extends StandardSimpleQuery<C, StandardInsert._ParentInsertQuery<CT>>
            implements SubQuery, StandardInsert._ParentInsertQuery<CT>, ParentInsertSubQuerySpec<CT> {

        private final Function<SubQuery, StandardInsert._ChildPartSpec<CT>> function;

        private ParentInsertSubQuery(CriteriaContext context, Function<SubQuery, StandardInsert._ChildPartSpec<CT>> function) {
            super(context);
            this.function = function;
        }

        @Override
        public Insert asInsert() {
            this.prepared();
            return this.function.apply(this)
                    .asInsert();
        }

        @Override
        public CT child() {
            this.prepared();
            return this.function.apply(this)
                    .child();
        }

        @Override
        public StandardInsert._ChildPartSpec<CT> fromLeft(final SubQuery query) {
            return this.function.apply(query);
        }


    }//ParentInsertSubQuery

    interface InsertSubQuerySpec {

        Insert._InsertSpec fromLeft(SubQuery query);
    }


    private static final class InsertSubQuery<C> extends StandardSimpleQuery<C, StandardInsert._InsertQuery>
            implements SubQuery, StandardInsert._InsertQuery, InsertSubQuerySpec {

        private final Function<SubQuery, Insert._InsertSpec> function;

        private InsertSubQuery(CriteriaContext context, Function<SubQuery, Insert._InsertSpec> function) {
            super(context);
            this.function = function;
        }

        @Override
        public Insert asInsert() {
            this.prepared();
            return this.function.apply(this)
                    .asInsert();
        }

        @Override
        public Insert._InsertSpec fromLeft(final SubQuery query) {
            return this.function.apply(query);
        }


    }//InsertSubQuery


    /**
     * @see #scalarSubQuery(Object)
     */
    private static final class SimpleScalarSubQuery<C> extends SimpleSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private SimpleScalarSubQuery(CriteriaContext criteriaContext) {
            super(criteriaContext);
        }


    }// SimpleScalarSubQuery


    private static abstract class UnionAndQuery<C, Q extends Query> extends StandardSimpleQuery<C, Q>
            implements UnionAndRowSet {

        private final Q left;

        private final UnionType unionType;

        UnionAndQuery(Q left, UnionType unionType, CriteriaContext context) {
            super(context);
            this.left = left;
            this.unionType = unionType;
        }

        @Override
        public final RowSet leftRowSet() {
            return this.left;
        }

        @Override
        public final UnionType unionType() {
            return this.unionType;
        }

    }//UnionAndQuery


    private static final class UnionAndSelect<C> extends UnionAndQuery<C, Select>
            implements Select {

        private UnionAndSelect(Select left, UnionType unionType, CriteriaContext context) {
            super(left, unionType, context);
        }

    } // UnionAndSelect


    private static class UnionAndSubQuery<C, Q extends SubQuery> extends UnionAndQuery<C, Q>
            implements SubQuery, _SelfDescribed {

        private UnionAndSubQuery(Q left, UnionType unionType, CriteriaContext context) {
            super(left, unionType, context);
        }

    }// UnionAndSubQuery


    private static final class UnionAndScalarSubQuery<C> extends UnionAndSubQuery<C, ScalarExpression>
            implements ScalarSubQuery {

        private UnionAndScalarSubQuery(ScalarExpression left, UnionType unionType, CriteriaContext context) {
            super(left, unionType, context);
        }


    }// UnionAndScalarSubQuery


}
