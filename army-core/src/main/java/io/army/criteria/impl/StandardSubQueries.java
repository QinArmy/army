package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.Objects;
import java.util.function.Function;

abstract class StandardSubQueries<Q extends SubQuery, C> extends StandardQuery<Q, C>
        implements SubQuery {


    static <C> StandardSubQueries<SubQuery, C> subQuery(@Nullable C criteria) {
        return new StandardSubQuery<>(criteria);
    }


    static <C> StandardSubQueries<RowSubQuery, C> rowSubQuery(@Nullable C criteria) {
        return new StandardRowSubQuery<>(criteria);
    }


    static <E, C> ColumnSubQuery.ColumnSelectClauseSpec<E, ColumnSubQuery<E>, C> columnSubQuery(@Nullable C criteria) {
        return new StandardColumnSubQuery<>(criteria);
    }

    static <E, C> ColumnSubQuery.ColumnSelectClauseSpec<E, ScalarQueryExpression<E>, C> scalarSubQuery(@Nullable C criteria) {
        return new StandardScalarSubQuery<>(criteria);
    }


    static <C> StandardSelectClauseSpec<SubQuery, C> unionAndSubQuery(SubQuery left, UnionType unionType, @Nullable C criteria) {
        return new UnionAndSubQuery<>(left, unionType, criteria);
    }

    static <C> StandardSelectClauseSpec<RowSubQuery, C> unionAndRowSubQuery(RowSubQuery left, UnionType unionType
            , @Nullable C criteria) {
        return new UnionRowSubQuery<C>(left, unionType, criteria);
    }

    static <E, C> StandardSelectClauseSpec<ColumnSubQuery<E>, C> unionAndColumnSubQuery(ColumnSubQuery<E> left, UnionType unionType
            , @Nullable C criteria) {
        return new UnionColumnSubQuery<>(left, unionType, criteria);
    }

    static <E, C> StandardSelectClauseSpec<ScalarQueryExpression<E>, C> unionAndScalarSubQuery(ScalarQueryExpression<E> left
            , UnionType unionType, @Nullable C criteria) {
        return new UnionScalarSubQuery<>(left, unionType, criteria);
    }

    private final CriteriaContext criteriaContext;

    private StandardSubQueries(@Nullable C criteria) {
        super(criteria);
        this.criteriaContext = new CriteriaContextImpl<>(criteria);
        CriteriaContextStack.push(this.criteriaContext);
    }


    @Override
    public final Selection selection(final String derivedFieldName) {
        Selection selection = null;
        outFor:
        for (SelectPart selectPart : selectPartList()) {
            if (selectPart instanceof Selection) {
                if (derivedFieldName.equals(((Selection) selectPart).alias())) {
                    selection = (Selection) selectPart;
                    break;
                }
            } else if (selectPart instanceof SelectionGroup) {
                for (Selection s : ((SelectionGroup) selectPart).selectionList()) {
                    if (derivedFieldName.equals(s.alias())) {
                        selection = s;
                        break outFor;
                    }
                }
            } else {
                throw _Exceptions.unknownSelectPart(selectPart);
            }
        }
        if (selection == null) {
            throw new CriteriaException(String.format("Not found %s[%s]", Selection.class.getName(), derivedFieldName));
        }
        return selection;
    }

    @Override
    final Q onAsQuery() {
        CriteriaContextStack.pop(this.criteriaContext);
        //must return this
        return internalOnAsQuery();
    }

    @Override
    final void onAddTable(TableMeta<?> table, String tableAlias) {
        this.criteriaContext.onAddTable(table, tableAlias);
    }

    @Override
    final void onAddSubQuery(SubQuery subQuery, String subQueryAlias) {
        this.criteriaContext.onAddSubQuery(subQuery, subQueryAlias);
    }

    abstract Q internalOnAsQuery();


    private static class StandardSubQuery<C> extends StandardSubQueries<SubQuery, C> implements SubQuery {


        StandardSubQuery(@Nullable C criteria) {
            super(criteria);
        }


        @Override
        public final StandardUnionSpec<SubQuery, C> bracketsQuery() {
            return StandardUnionQuery.bracketSubQuery(this.asQuery(), this.criteria);
        }

        @Override
        final StandardUnionSpec<SubQuery, C> createUnionQuery(SubQuery left, UnionType unionType, SubQuery right) {
            return StandardUnionQuery.unionSubQuery(left, unionType, right, this.criteria);
        }

        @Override
        final StandardSelectClauseSpec<SubQuery, C> asQueryAndSelect(UnionType unionType) {
            return StandardSubQueries.unionAndSubQuery(this.asQuery(), unionType, this.criteria);
        }

        @Override
        SubQuery internalOnAsQuery() {
            // here return this
            return this;
        }


    }

    /**
     * @see #unionAndSubQuery(SubQuery, UnionType, Object)
     */
    private static final class UnionAndSubQuery<C> extends StandardSubQuery<C> {

        private final SubQuery left;

        private final UnionType unionType;

        private UnionAndSubQuery(SubQuery left, UnionType unionType, @Nullable C criteria) {
            super(criteria);
            this.left = left;
            this.unionType = unionType;
        }

        @Override
        SubQuery internalOnAsQuery() {
            return StandardUnionQuery.unionSubQuery(this.left, this.unionType, this, criteria)
                    .asQuery();
        }

    }


    /**
     * @see #rowSubQuery(Object)
     */
    private static class StandardRowSubQuery<C> extends StandardSubQueries<RowSubQuery, C> implements RowSubQuery {

        StandardRowSubQuery(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public final StandardUnionSpec<RowSubQuery, C> bracketsQuery() {
            return StandardUnionQuery.bracketRowSubQuery(this.asQuery(), this.criteria);
        }

        @Override
        final StandardUnionSpec<RowSubQuery, C> createUnionQuery(RowSubQuery left, UnionType unionType, RowSubQuery right) {
            return StandardUnionQuery.unionRowSubQuery(left, unionType, right, this.criteria);
        }

        @Override
        final StandardSelectClauseSpec<RowSubQuery, C> asQueryAndSelect(UnionType unionType) {
            return StandardSubQueries.unionAndRowSubQuery(this.asQuery(), unionType, this.criteria);
        }

        @Override
        RowSubQuery internalOnAsQuery() {
            // here return this
            return this;
        }

    }

    /**
     * @see #unionAndRowSubQuery(RowSubQuery, UnionType, Object)
     */
    private static final class UnionRowSubQuery<C> extends StandardRowSubQuery<C> {

        private final RowSubQuery left;

        private final UnionType unionType;

        private UnionRowSubQuery(RowSubQuery left, UnionType unionType, @Nullable C criteria) {
            super(criteria);
            this.left = left;
            this.unionType = unionType;
        }

        @Override
        RowSubQuery internalOnAsQuery() {
            return StandardUnionQuery.unionRowSubQuery(this.left, this.unionType, this, criteria)
                    .asQuery();
        }

    }


    /**
     * @see #columnSubQuery(Object)
     */
    private static class StandardColumnSubQuery<E, C> extends StandardSubQueries<ColumnSubQuery<E>, C>
            implements ColumnSubQuery<E>, ColumnSubQuery.ColumnSelectClauseSpec<E, ColumnSubQuery<E>, C> {

        StandardColumnSubQuery(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public final StandardFromSpec<ColumnSubQuery<E>, C> selectOne(Distinct distinct, Selection selection) {
            Objects.requireNonNull(selection);
            return this.select(distinct, selection);
        }

        @Override
        public final StandardFromSpec<ColumnSubQuery<E>, C> selectOne(Selection selection) {
            Objects.requireNonNull(selection);
            return this.select(selection);
        }

        @Override
        public final StandardFromSpec<ColumnSubQuery<E>, C> selectOne(Distinct distinct, Function<C, Selection> function) {
            return this.selectOne(distinct, function.apply(this.criteria));
        }

        @Override
        public final StandardUnionSpec<ColumnSubQuery<E>, C> bracketsQuery() {
            return StandardUnionQuery.bracketColumnSubQuery(this.asQuery(), this.criteria);
        }

        @Override
        final StandardUnionSpec<ColumnSubQuery<E>, C> createUnionQuery(ColumnSubQuery<E> left, UnionType unionType, ColumnSubQuery<E> right) {
            return StandardUnionQuery.unionColumnSubQuery(left, unionType, right, this.criteria);
        }

        @Override
        final StandardSelectClauseSpec<ColumnSubQuery<E>, C> asQueryAndSelect(UnionType unionType) {
            return StandardSubQueries.unionAndColumnSubQuery(this.asQuery(), unionType, this.criteria);
        }

        @Override
        ColumnSubQuery<E> internalOnAsQuery() {
            // here return this
            return this;
        }

    }


    /**
     * @see #unionAndColumnSubQuery(ColumnSubQuery, UnionType, Object)
     */
    private static final class UnionColumnSubQuery<E, C> extends StandardColumnSubQuery<E, C> {

        private final ColumnSubQuery<E> left;

        private final UnionType unionType;

        private UnionColumnSubQuery(ColumnSubQuery<E> left, UnionType unionType, @Nullable C criteria) {
            super(criteria);
            this.left = left;
            this.unionType = unionType;
        }

        @Override
        ColumnSubQuery<E> internalOnAsQuery() {
            return StandardUnionQuery.unionColumnSubQuery(this.left, this.unionType, this, this.criteria)
                    .asQuery();
        }

    }

    /**
     * @see #scalarSubQuery(Object)
     */
    private static class StandardScalarSubQuery<E, C> extends StandardSubQueries<ScalarQueryExpression<E>, C>
            implements ScalarSubQuery<E>, ColumnSubQuery.ColumnSelectClauseSpec<E, ScalarQueryExpression<E>, C> {

        private StandardScalarSubQuery(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public final StandardFromSpec<ScalarQueryExpression<E>, C> selectOne(Distinct distinct, Selection selection) {
            Objects.requireNonNull(selection);
            return this.select(distinct, selection);
        }

        @Override
        public final StandardFromSpec<ScalarQueryExpression<E>, C> selectOne(Selection selection) {
            Objects.requireNonNull(selection);
            return this.select(selection);
        }

        @Override
        public final StandardFromSpec<ScalarQueryExpression<E>, C> selectOne(Distinct distinct, Function<C, Selection> function) {
            return this.selectOne(distinct, function.apply(this.criteria));
        }

        @Override
        public final StandardUnionSpec<ScalarQueryExpression<E>, C> bracketsQuery() {
            return StandardUnionQuery.bracketScalarSubQuery(this.asQuery(), this.criteria);
        }

        @Override
        public final Selection selection() {
            final Selection selection = (Selection) this.selectPartList().get(0);
            assert selection != null;
            return selection;
        }

        @Override
        public final ParamMeta paramMeta() {
            return selection().paramMeta();
        }

        @Override
        final StandardUnionSpec<ScalarQueryExpression<E>, C> createUnionQuery(ScalarQueryExpression<E> left, UnionType unionType
                , ScalarQueryExpression<E> right) {
            return StandardUnionQuery.unionScalarSubQuery(left, unionType, right, this.criteria);
        }

        @Override
        final StandardSelectClauseSpec<ScalarQueryExpression<E>, C> asQueryAndSelect(UnionType unionType) {
            return StandardSubQueries.unionAndScalarSubQuery(this.asQuery(), unionType, this.criteria);
        }

        @Override
        ScalarQueryExpression<E> internalOnAsQuery() {
            return ScalarSubQueryExpression.create(this);
        }


    }

    /**
     * @see #unionAndScalarSubQuery(ScalarQueryExpression, UnionType, Object)
     */
    private static final class UnionScalarSubQuery<E, C> extends StandardScalarSubQuery<E, C> {

        private final ScalarQueryExpression<E> left;

        private final UnionType unionType;

        private UnionScalarSubQuery(ScalarQueryExpression<E> left, UnionType unionType, @Nullable C criteria) {
            super(criteria);
            this.left = left;
            this.unionType = unionType;
        }

        @Override
        ScalarQueryExpression<E> internalOnAsQuery() {
            final ScalarQueryExpression<E> thisExpression;
            thisExpression = ScalarSubQueryExpression.create(this);
            return StandardUnionQuery.unionScalarSubQuery(this.left, this.unionType, thisExpression, this.criteria)
                    .asQuery();
        }

    }


}
