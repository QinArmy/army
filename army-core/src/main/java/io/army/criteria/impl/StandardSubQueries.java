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


    static <E, C> ColumnSubQuery.ColumnSelectionSpec<E, ColumnSubQuery<E>, C> columnSubQuery(@Nullable C criteria) {
        return new StandardColumnSubQuery<>(criteria);
    }

    static <E, C> ColumnSubQuery.ColumnSelectionSpec<E, ScalarExpression<E>, C> scalarSubQuery(@Nullable C criteria) {
        return new StandardScalarSubQuery<>(criteria);
    }


    static <C> SelectPartSpec<SubQuery, C> unionAndSubQuery(SubQuery left, UnionType unionType, @Nullable C criteria) {
        return new UnionAndSubQuery<>(left, unionType, criteria);
    }

    static <C> SelectPartSpec<RowSubQuery, C> unionAndRowSubQuery(RowSubQuery left, UnionType unionType
            , @Nullable C criteria) {
        return new UnionRowSubQuery<C>(left, unionType, criteria);
    }

    static <E, C> SelectPartSpec<ColumnSubQuery<E>, C> unionAndColumnSubQuery(ColumnSubQuery<E> left, UnionType unionType
            , @Nullable C criteria) {
        return new UnionColumnSubQuery<>(left, unionType, criteria);
    }

    static <E, C> SelectPartSpec<ScalarExpression<E>, C> unionAndScalarSubQuery(ScalarExpression<E> left
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
        public final UnionSpec<SubQuery, C> bracketsQuery() {
            return StandardUnionQuery.bracketSubQuery(this.asQuery(), this.criteria);
        }

        @Override
        final UnionSpec<SubQuery, C> createUnionQuery(SubQuery left, UnionType unionType, SubQuery right) {
            return StandardUnionQuery.unionSubQuery(left, unionType, right, this.criteria);
        }

        @Override
        final SelectPartSpec<SubQuery, C> asQueryAndSelect(UnionType unionType) {
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
        public final UnionSpec<RowSubQuery, C> bracketsQuery() {
            return StandardUnionQuery.bracketRowSubQuery(this.asQuery(), this.criteria);
        }

        @Override
        final UnionSpec<RowSubQuery, C> createUnionQuery(RowSubQuery left, UnionType unionType, RowSubQuery right) {
            return StandardUnionQuery.unionRowSubQuery(left, unionType, right, this.criteria);
        }

        @Override
        final SelectPartSpec<RowSubQuery, C> asQueryAndSelect(UnionType unionType) {
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
            implements ColumnSubQuery<E>, ColumnSubQuery.ColumnSelectionSpec<E, ColumnSubQuery<E>, C> {

        StandardColumnSubQuery(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public final FromSpec<ColumnSubQuery<E>, C> selectOne(Distinct distinct, Selection selection) {
            Objects.requireNonNull(selection);
            return this.select(distinct, selection);
        }

        @Override
        public final FromSpec<ColumnSubQuery<E>, C> selectOne(Selection selection) {
            Objects.requireNonNull(selection);
            return this.select(selection);
        }

        @Override
        public final FromSpec<ColumnSubQuery<E>, C> selectOne(Distinct distinct, Function<C, Selection> function) {
            return this.selectOne(distinct, function.apply(this.criteria));
        }

        @Override
        public final UnionSpec<ColumnSubQuery<E>, C> bracketsQuery() {
            return StandardUnionQuery.bracketColumnSubQuery(this.asQuery(), this.criteria);
        }

        @Override
        final UnionSpec<ColumnSubQuery<E>, C> createUnionQuery(ColumnSubQuery<E> left, UnionType unionType, ColumnSubQuery<E> right) {
            return StandardUnionQuery.unionColumnSubQuery(left, unionType, right, this.criteria);
        }

        @Override
        final SelectPartSpec<ColumnSubQuery<E>, C> asQueryAndSelect(UnionType unionType) {
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
    private static class StandardScalarSubQuery<E, C> extends StandardSubQueries<ScalarExpression<E>, C>
            implements ScalarSubQuery<E>, ColumnSubQuery.ColumnSelectionSpec<E, ScalarExpression<E>, C> {

        private StandardScalarSubQuery(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public final FromSpec<ScalarExpression<E>, C> selectOne(Distinct distinct, Selection selection) {
            Objects.requireNonNull(selection);
            return this.select(distinct, selection);
        }

        @Override
        public final FromSpec<ScalarExpression<E>, C> selectOne(Selection selection) {
            Objects.requireNonNull(selection);
            return this.select(selection);
        }

        @Override
        public final FromSpec<ScalarExpression<E>, C> selectOne(Distinct distinct, Function<C, Selection> function) {
            return this.selectOne(distinct, function.apply(this.criteria));
        }

        @Override
        public final UnionSpec<ScalarExpression<E>, C> bracketsQuery() {
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
        final UnionSpec<ScalarExpression<E>, C> createUnionQuery(ScalarExpression<E> left, UnionType unionType
                , ScalarExpression<E> right) {
            return StandardUnionQuery.unionScalarSubQuery(left, unionType, right, this.criteria);
        }

        @Override
        final SelectPartSpec<ScalarExpression<E>, C> asQueryAndSelect(UnionType unionType) {
            return StandardSubQueries.unionAndScalarSubQuery(this.asQuery(), unionType, this.criteria);
        }

        @Override
        ScalarExpression<E> internalOnAsQuery() {
            return ScalarSubQueryExpression.create(this);
        }


    }

    /**
     * @see #unionAndScalarSubQuery(ScalarExpression, UnionType, Object)
     */
    private static final class UnionScalarSubQuery<E, C> extends StandardScalarSubQuery<E, C> {

        private final ScalarExpression<E> left;

        private final UnionType unionType;

        private UnionScalarSubQuery(ScalarExpression<E> left, UnionType unionType, @Nullable C criteria) {
            super(criteria);
            this.left = left;
            this.unionType = unionType;
        }

        @Override
        ScalarExpression<E> internalOnAsQuery() {
            final ScalarExpression<E> thisExpression;
            thisExpression = ScalarSubQueryExpression.create(this);
            return StandardUnionQuery.unionScalarSubQuery(this.left, this.unionType, thisExpression, this.criteria)
                    .asQuery();
        }

    }


}
