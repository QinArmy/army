package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._UnionQuery;
import io.army.dialect.Constant;
import io.army.dialect.Dialect;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.util._Exceptions;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class StandardUnionQuery<Q extends Query, C> extends StandardPartQuery<Q, C> implements _UnionQuery {

    static <C> UnionSpec<Select, C> bracketSelect(Select select, @Nullable C criteria) {
        select.prepared();
        return new BracketSelect<>(select, criteria);
    }

    static <C> UnionSpec<Select, C> unionSelect(Select left, UnionType unionType, Select right, @Nullable C criteria) {
        left.prepared();
        return new UnionSelect<>(left, unionType, right, criteria);
    }


    static <C> UnionSpec<SubQuery, C> bracketSubQuery(SubQuery subQuery, @Nullable C criteria) {
        subQuery.prepared();
        return new BracketSubQuery<>(subQuery, criteria);
    }

    static <C> UnionSubQuery<C> unionSubQuery(SubQuery left, UnionType unionType
            , SubQuery right, @Nullable C criteria) {
        left.prepared();
        return new UnionSubQuery<>(left, unionType, right, criteria);
    }


    static <C> UnionSpec<RowSubQuery, C> bracketRowSubQuery(RowSubQuery subQuery, @Nullable C criteria) {
        subQuery.prepared();
        return new BracketRowSubQuery<>(subQuery, criteria);
    }

    static <C> UnionRowSubQuery<C> unionRowSubQuery(RowSubQuery left, UnionType unionType
            , RowSubQuery right, @Nullable C criteria) {
        left.prepared();
        return new UnionRowSubQuery<>(left, unionType, right, criteria);
    }

    static <C, E> UnionSpec<ColumnSubQuery<E>, C> bracketColumnSubQuery(ColumnSubQuery<E> subQuery, @Nullable C criteria) {
        subQuery.prepared();
        return new BracketColumnSubQuery<>(subQuery, criteria);
    }

    static <C, E> UnionColumnSubQuery<E, C> unionColumnSubQuery(ColumnSubQuery<E> left, UnionType unionType
            , ColumnSubQuery<E> right, @Nullable C criteria) {
        left.prepared();
        return new UnionColumnSubQuery<>(left, unionType, right, criteria);
    }


    static <C, E> UnionSpec<ScalarQueryExpression<E>, C> bracketScalarSubQuery(ScalarQueryExpression<E> subQuery
            , @Nullable C criteria) {
        subQuery.prepared();
        return new BracketScalarSubQuery<>(subQuery, criteria);
    }

    static <C, E> UnionScalarSubQuery<E, C> unionScalarSubQuery(ScalarQueryExpression<E> left, UnionType unionType
            , ScalarQueryExpression<E> right, @Nullable C criteria) {
        left.prepared();
        right.prepared();
        return new UnionScalarSubQuery<>(left, unionType, right, criteria);
    }


    private StandardUnionQuery(@Nullable C criteria) {
        super(criteria);
    }


    @Override
    public final QuerySpec<Q> lock(LockMode lockMode) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    public final QuerySpec<Q> lock(Function<C, LockMode> function) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    public final QuerySpec<Q> ifLock(@Nullable LockMode lockMode) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    public final QuerySpec<Q> ifLock(Supplier<LockMode> supplier) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    public final QuerySpec<Q> ifLock(Function<C, LockMode> function) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    final void internalClear() {
        //no-op
    }


    private static abstract class AbstractUnionSelect<C> extends StandardUnionQuery<Select, C> implements Select {

        private AbstractUnionSelect(@Nullable C criteria) {
            super(criteria);
        }

        @Override
        public final UnionSpec<Select, C> bracketsQuery() {
            return StandardUnionQuery.bracketSelect(this.asQuery(), this.criteria);
        }

        @Override
        final UnionSpec<Select, C> createUnionQuery(Select left, UnionType unionType, Select right) {
            return StandardUnionQuery.unionSelect(left, unionType, right, this.criteria);
        }


        @Override
        final SelectPartSpec<Select, C> asQueryAndSelect(final UnionType unionType) {
            return StandardSelect.unionAndSelect(this.asQuery(), unionType, this.criteria);
        }

        @Override
        final Select internalAsQuery() {
            //must return this
            return this;
        }
    }


    private static final class BracketSelect<C> extends AbstractUnionSelect<C> implements Select {

        private final Select select;

        public BracketSelect(Select select, @Nullable C criteria) {
            super(criteria);
            this.select = select;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(Constant.LEFT_BRACKET);

            context.dialect().select(this.select, context);

            builder.append(Constant.SPACE)
                    .append(Constant.RIGHT_BRACKET);
        }

    }


    private static final class UnionSelect<C> extends AbstractUnionSelect<C> implements Select {

        private final Select left;

        private final UnionType unionType;

        private final Select right;

        private UnionSelect(Select left, UnionType unionType, Select right, @Nullable C criteria) {
            super(criteria);
            this.left = left;
            this.unionType = unionType;
            this.right = right;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final Dialect dialect = context.dialect();
            dialect.select(this.left, context);

            context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(this.unionType.keyWords);

            dialect.select(this.right, context);
        }


    }

    /**
     * <p>
     * This is base class of below:
     *     <ul>
     *         <li>{@link BracketSubQuery}</li>
     *         <li>{@link UnionSubQuery}</li>
     *     </ul>
     * </p>
     */
    private static abstract class AbstractUnionSubQuery<C> extends StandardUnionQuery<SubQuery, C> implements SubQuery {

        final SubQuery left;

        AbstractUnionSubQuery(SubQuery left, @Nullable C criteria) {
            super(criteria);
            this.left = left;
        }

        @Override
        public final List<? extends SelectPart> selectPartList() {
            return this.left.selectPartList();
        }

        @Override
        public final Selection selection(String derivedFieldName) {
            return this.left.selection(derivedFieldName);
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
        final SubQuery internalAsQuery() {
            //must return this
            return this;
        }

    }

    /**
     * @see #bracketSubQuery(SubQuery, Object)
     */
    private static final class BracketSubQuery<C> extends AbstractUnionSubQuery<C> {

        private BracketSubQuery(SubQuery left, @Nullable C criteria) {
            super(left, criteria);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(Constant.LEFT_BRACKET);

            context.dialect().subQuery(this.left, context);

            builder.append(Constant.SPACE)
                    .append(Constant.RIGHT_BRACKET);
        }

    }

    /**
     * @see #unionSubQuery(SubQuery, UnionType, SubQuery, Object)
     */
    static final class UnionSubQuery<C> extends AbstractUnionSubQuery<C> {

        private final UnionType unionType;

        private final SubQuery right;

        private UnionSubQuery(SubQuery left, UnionType unionType, SubQuery right, @Nullable C criteria) {
            super(left, criteria);
            this.unionType = unionType;
            this.right = right;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final Dialect dialect = context.dialect();
            dialect.subQuery(this.left, context);

            context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(this.unionType.keyWords);

            dialect.subQuery(this.right, context);
        }

    }

    /**
     * <p>
     * This class is base class of below:
     *     <ul>
     *         <li>{@link BracketRowSubQuery}</li>
     *         <li>{@link UnionRowSubQuery}</li>
     *     </ul>
     * </p>
     */
    private static abstract class AbstractUnionRowSubQuery<C> extends StandardUnionQuery<RowSubQuery, C>
            implements RowSubQuery {

        final RowSubQuery left;

        private AbstractUnionRowSubQuery(RowSubQuery left, @Nullable C criteria) {
            super(criteria);
            this.left = left;
        }

        @Override
        public final List<? extends SelectPart> selectPartList() {
            return this.left.selectPartList();
        }

        @Override
        public final Selection selection(String derivedFieldName) {
            return this.left.selection(derivedFieldName);
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
        final RowSubQuery internalAsQuery() {
            //must return this
            return this;
        }

    }

    /**
     * @see #bracketRowSubQuery(RowSubQuery, Object)
     */
    private static final class BracketRowSubQuery<C> extends AbstractUnionRowSubQuery<C> {

        private BracketRowSubQuery(RowSubQuery left, @Nullable C criteria) {
            super(left, criteria);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(Constant.LEFT_BRACKET);

            context.dialect().subQuery(this.left, context);

            builder.append(Constant.SPACE)
                    .append(Constant.RIGHT_BRACKET);
        }

    }

    /**
     * @see #unionRowSubQuery(RowSubQuery, UnionType, RowSubQuery, Object)
     */
    static final class UnionRowSubQuery<C> extends AbstractUnionRowSubQuery<C> {

        private final UnionType unionType;

        private final RowSubQuery right;

        private UnionRowSubQuery(RowSubQuery left, UnionType unionType, RowSubQuery right, @Nullable C criteria) {
            super(left, criteria);
            this.unionType = unionType;
            this.right = right;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final Dialect dialect = context.dialect();
            dialect.subQuery(this.left, context);

            context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(this.unionType.keyWords);

            dialect.subQuery(this.right, context);
        }

    }


    /**
     * <p>
     * This class is base class of below:
     *     <ul>
     *         <li>{@link BracketColumnSubQuery}</li>
     *         <li>{@link UnionColumnSubQuery}</li>
     *     </ul>
     * </p>
     */
    private static abstract class AbstractUnionColumnSubQuery<E, C> extends StandardUnionQuery<ColumnSubQuery<E>, C>
            implements ColumnSubQuery<E> {

        final ColumnSubQuery<E> left;

        AbstractUnionColumnSubQuery(ColumnSubQuery<E> left, @Nullable C criteria) {
            super(criteria);
            this.left = left;
        }

        @Override
        public final List<? extends SelectPart> selectPartList() {
            return this.left.selectPartList();
        }

        @Override
        public final Selection selection(String derivedFieldName) {
            return this.left.selection(derivedFieldName);
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
        final ColumnSubQuery<E> internalAsQuery() {
            //must return this
            return this;
        }

    }


    /**
     * @see #bracketColumnSubQuery(ColumnSubQuery, Object)
     */
    private static final class BracketColumnSubQuery<E, C> extends AbstractUnionColumnSubQuery<E, C> {

        private BracketColumnSubQuery(ColumnSubQuery<E> left, @Nullable C criteria) {
            super(left, criteria);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(Constant.LEFT_BRACKET);

            context.dialect().subQuery(this.left, context);

            builder.append(Constant.SPACE)
                    .append(Constant.RIGHT_BRACKET);
        }

    }


    /**
     * @see #unionRowSubQuery(RowSubQuery, UnionType, RowSubQuery, Object)
     */
    static final class UnionColumnSubQuery<E, C> extends AbstractUnionColumnSubQuery<E, C> {

        private final UnionType unionType;

        private final ColumnSubQuery<E> right;

        private UnionColumnSubQuery(ColumnSubQuery<E> left, UnionType unionType
                , ColumnSubQuery<E> right, @Nullable C criteria) {
            super(left, criteria);
            this.unionType = unionType;
            this.right = right;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final Dialect dialect = context.dialect();
            dialect.subQuery(this.left, context);

            context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(this.unionType.keyWords);

            dialect.subQuery(this.right, context);
        }
    }

    /**
     * <p>
     * This class is base class of below:
     *     <ul>
     *         <li>{@link BracketScalarSubQuery}</li>
     *         <li>{@link UnionScalarSubQuery}</li>
     *     </ul>
     * </p>
     */
    private static abstract class AbstractUnionScalarSubQuery<E, C> extends StandardUnionQuery<ScalarQueryExpression<E>, C>
            implements ScalarSubQuery<E> {

        final ScalarQueryExpression<E> left;

        AbstractUnionScalarSubQuery(ScalarQueryExpression<E> left, @Nullable C criteria) {
            super(criteria);
            this.left = left;
        }

        @Override
        public final List<? extends SelectPart> selectPartList() {
            return this.left.selectPartList();
        }

        @Override
        public final Selection selection(String derivedFieldName) {
            return this.left.selection(derivedFieldName);
        }

        @Override
        public final Selection selection() {
            return this.left.selection();
        }

        @Override
        public final ParamMeta paramMeta() {
            return this.left.paramMeta();
        }

        @Override
        public final UnionSpec<ScalarQueryExpression<E>, C> bracketsQuery() {
            return StandardUnionQuery.bracketScalarSubQuery(this.asQuery(), this.criteria);
        }

        @Override
        final UnionSpec<ScalarQueryExpression<E>, C> createUnionQuery(ScalarQueryExpression<E> left, UnionType unionType, ScalarQueryExpression<E> right) {
            return StandardUnionQuery.unionScalarSubQuery(left, unionType, right, this.criteria);
        }

        @Override
        final SelectPartSpec<ScalarQueryExpression<E>, C> asQueryAndSelect(UnionType unionType) {
            return StandardSubQueries.unionAndScalarSubQuery(this.asQuery(), unionType, this.criteria);
        }

        @Override
        final ScalarQueryExpression<E> internalAsQuery() {
            // must return expression
            return ScalarSubQueryExpression.create(this.asQuery());
        }

    }

    /**
     * @see #bracketScalarSubQuery(ScalarQueryExpression, Object)
     */
    private static final class BracketScalarSubQuery<E, C> extends AbstractUnionScalarSubQuery<E, C> {

        private BracketScalarSubQuery(ScalarQueryExpression<E> left, @Nullable C criteria) {
            super(left, criteria);
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(Constant.LEFT_BRACKET);

            context.dialect().subQuery(this.left, context);

            builder.append(Constant.SPACE)
                    .append(Constant.RIGHT_BRACKET);
        }


    }

    /**
     * @see #unionScalarSubQuery(ScalarQueryExpression, UnionType, ScalarQueryExpression, Object)
     */
    static final class UnionScalarSubQuery<E, C> extends AbstractUnionScalarSubQuery<E, C> {

        private final UnionType unionType;

        private final ScalarQueryExpression<E> right;

        public UnionScalarSubQuery(ScalarQueryExpression<E> left, UnionType unionType
                , ScalarQueryExpression<E> right, @Nullable C criteria) {
            super(left, criteria);
            this.unionType = unionType;
            this.right = right;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final Dialect dialect = context.dialect();
            dialect.subQuery(this.left, context);

            context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(this.unionType.keyWords);

            dialect.subQuery(this.right, context);
        }

    }


}
