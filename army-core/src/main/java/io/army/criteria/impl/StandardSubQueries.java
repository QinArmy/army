package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

abstract class StandardSubQueries<Q extends SubQuery, C> extends StandardQuery<Q, C>
        implements SubQuery {

    static StandardSubQueries<SubQuery, Void> subQuery() {
        if (CriteriaContextStack.peek().criteria() != null) {
            throw new IllegalArgumentException("criteria isn't current context.");
        }
        return new StandardSubQuery<>(null);
    }

    static <C> StandardSubQueries<SubQuery, C> subQuery(@Nullable C criteria) {
        return new StandardSubQuery<>(criteria);
    }


    static <C> StandardSubQueries<RowSubQuery, C> rowSubQuery(@Nullable C criteria) {
        return new StandardRowSubQuery<>(criteria);
    }


    static <E, C> StandardSubQueries<ColumnSubQuery<E>, C> columnSubQuery(Class<E> columnType, C criteria) {

        return new StandardColumnSubQuery<>(criteria, columnType);
    }

    static <E, C> ScalarSubQueryAdaptor<E, C> buildScalarSubQuery(Class<E> javaType, MappingType mappingType
            , C criteria) {
        if (criteria != CriteriaContextStack.pop()) {
            throw new IllegalArgumentException("criteria isn't current context.");
        }
        return new ScalarSubQueryAdaptor<>(javaType, mappingType, criteria);
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
        return null;
    }

    static <E, C> SelectPartSpec<ScalarExpression<E>, C> unionAndScalarSubQuery(ScalarExpression<E> left
            , UnionType unionType, @Nullable C criteria) {
        return null;
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
            return StandardUnionQuery.unionSubQuery(this.left, this.unionType, this, criteria);
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
            return StandardUnionQuery.unionRowSubQuery(this.left, this.unionType, this, criteria);
        }

    }


}
