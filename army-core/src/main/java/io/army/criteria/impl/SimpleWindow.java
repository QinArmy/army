package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Window;
import io.army.dialect.Constant;
import io.army.dialect._Dialect;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.util.ArrayUtils;
import io.army.util._Assert;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of all simple {@link io.army.criteria.Window}.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class SimpleWindow<C, AR, LR, PR, OR, FR, FC, BR, BC, NC, MA, MB, R> implements _Window
        , Window._AsClause<AR>, Window._LeftBracketClause<C, LR>, Window._PartitionByExpClause<C, PR>
        , Statement._OrderByClause<C, OR>, Window._FrameUnitsClause<C, FR, FC>, Window._FrameBetweenClause<C, BR, BC>
        , Window._FrameExpBoundClause<MA>, Window._FrameBetweenAndClause<C, FC, NC>, Window._FrameNonExpBoundClause<MB>
        , Statement._RightBracketClause<R> {


    static <C, R> Window._SimpleAsClause<C, R> standard(String windowName, R stmt) {
        return new StandardSimpleWindow<>(windowName, stmt);
    }

    static <C, R> Window._SimpleAsClause<C, R> standard(String windowName, CriteriaContext criteriaContext) {
        return new StandardSimpleWindow<>(windowName, criteriaContext);
    }


    private final String windowName;

    private final R stmt;

    private final CriteriaContext criteriaContext;

    private String refWindowName;

    private List<_Expression> partitionByList;

    private List<ArmySortItem> orderByList;

    private FrameUnits frameUnits;

    private Boolean betweenExtent;

    private _Expression frameStartExp;

    private FrameBound frameStartBound;

    private _Expression frameEndExp;

    private FrameBound frameEndBound;

    private boolean prepared;


    SimpleWindow(String windowName, CriteriaContext criteriaContext) {
        this.windowName = windowName;
        this.stmt = (R) this;
        this.criteriaContext = criteriaContext;
    }

    SimpleWindow(String windowName, R stmt) {
        this.windowName = windowName;
        this.stmt = stmt;
        this.criteriaContext = ((CriteriaContextSpec) stmt).getCriteriaContext();
    }

    @Override
    public final AR as() {
        return (AR) this;
    }

    @Override
    public final LR leftBracket() {
        return (LR) this;
    }

    @Override
    public final LR leftBracket(String existingWindowName) {
        if (!this.criteriaContext.isExistWindow(existingWindowName)) {
            throw _Exceptions.windowNotExists(existingWindowName);
        }
        this.refWindowName = existingWindowName;
        return (LR) this;
    }

    @Override
    public final LR leftBracket(Supplier<String> supplier) {
        return this.leftBracket(supplier.get());
    }

    @Override
    public final LR leftBracket(Function<C, String> function) {
        return this.leftBracket(function.apply(this.criteriaContext.criteria()));
    }

    @Override
    public final LR leftBracketIf(Supplier<String> supplier) {
        final String windowName;
        windowName = supplier.get();
        if (windowName != null) {
            this.leftBracket(windowName);
        }
        return (LR) this;
    }

    @Override
    public final LR leftBracketIf(Function<C, String> function) {
        final String windowName;
        windowName = function.apply(this.criteriaContext.criteria());
        if (windowName != null) {
            this.leftBracket(windowName);
        }
        return (LR) this;
    }


    @Override
    public final PR partitionBy(Object exp) {
        this.partitionByList = Collections.singletonList(SQLs._nonNullExp(exp));
        return (PR) this;
    }

    @Override
    public final PR partitionBy(Object exp1, Object exp2) {
        this.partitionByList = ArrayUtils.asUnmodifiableList(SQLs._nonNullExp(exp1), SQLs._nonNullExp(exp2));
        return (PR) this;
    }

    @Override
    public final PR partitionBy(Object exp1, Object exp2, Object exp3) {
        this.partitionByList = ArrayUtils.asUnmodifiableList(
                SQLs._nonNullExp(exp1),
                SQLs._nonNullExp(exp2),
                SQLs._nonNullExp(exp3)
        );
        return (PR) this;
    }

    @Override
    public final <E extends Expression> PR partitionBy(Function<C, List<E>> function) {
        this.partitionByList = CriteriaUtils.asExpressionList(function.apply(this.criteriaContext.criteria()));
        return (PR) this;
    }

    @Override
    public final <E extends Expression> PR partitionBy(Supplier<List<E>> supplier) {
        this.partitionByList = CriteriaUtils.asExpressionList(supplier.get());
        return (PR) this;
    }

    @Override
    public final PR partitionBy(Consumer<List<Expression>> consumer) {
        final List<Expression> expressionList = new ArrayList<>();
        consumer.accept(expressionList);
        this.partitionByList = CriteriaUtils.asExpressionList(expressionList);
        return (PR) this;
    }

    @Override
    public final <E extends Expression> PR ifPartitionBy(Supplier<List<E>> supplier) {
        final List<E> expressionList;
        expressionList = supplier.get();
        if (expressionList != null && expressionList.size() > 0) {
            this.partitionByList = CriteriaUtils.asExpressionList(expressionList);
        }
        return (PR) this;
    }

    @Override
    public final <E extends Expression> PR ifPartitionBy(Function<C, List<E>> function) {
        final List<E> expressionList;
        expressionList = function.apply(this.criteriaContext.criteria());
        if (expressionList != null && expressionList.size() > 0) {
            this.partitionByList = CriteriaUtils.asExpressionList(expressionList);
        }
        return (PR) this;
    }

    @Override
    public final OR orderBy(Object sortItem) {
        this.orderByList = Collections.singletonList(SQLs._nonNullSortItem(sortItem));
        return (OR) this;
    }

    @Override
    public final OR orderBy(Object sortItem1, Object sortItem2) {
        this.orderByList = ArrayUtils.asUnmodifiableList(
                SQLs._nonNullSortItem(sortItem1),
                SQLs._nonNullSortItem(sortItem2)
        );
        return (OR) this;
    }

    @Override
    public final OR orderBy(Object sortItem1, Object sortItem2, Object sortItem3) {
        this.orderByList = ArrayUtils.asUnmodifiableList(
                SQLs._nonNullSortItem(sortItem1),
                SQLs._nonNullSortItem(sortItem2),
                SQLs._nonNullSortItem(sortItem3)
        );
        return (OR) this;
    }

    @Override
    public final <S extends SortItem> OR orderBy(Function<C, List<S>> function) {
        this.orderByList = CriteriaUtils.asSortItemList(function.apply(this.criteriaContext.criteria()));
        return (OR) this;
    }

    @Override
    public final <S extends SortItem> OR orderBy(Supplier<List<S>> supplier) {
        this.orderByList = CriteriaUtils.asSortItemList(supplier.get());
        return (OR) this;
    }

    @Override
    public final OR orderBy(Consumer<List<SortItem>> consumer) {
        final List<SortItem> sortItemList = new ArrayList<>();
        consumer.accept(sortItemList);
        this.orderByList = CriteriaUtils.asSortItemList(sortItemList);
        return (OR) this;
    }

    @Override
    public final <S extends SortItem> OR ifOrderBy(Supplier<List<S>> supplier) {
        final List<S> sortItemList;
        sortItemList = supplier.get();
        if (sortItemList != null && sortItemList.size() > 0) {
            this.orderByList = CriteriaUtils.asSortItemList(sortItemList);
        }
        return (OR) this;
    }

    @Override
    public final <S extends SortItem> OR ifOrderBy(Function<C, List<S>> function) {
        final List<S> sortItemList;
        sortItemList = function.apply(this.criteriaContext.criteria());
        if (sortItemList != null && sortItemList.size() > 0) {
            this.orderByList = CriteriaUtils.asSortItemList(sortItemList);
        }
        return (OR) this;
    }

    @Override
    public final FR rows() {
        this.frameUnits = FrameUnits.ROWS;
        this.betweenExtent = Boolean.TRUE;
        return (FR) this;
    }

    @Override
    public final FR range() {
        this.frameUnits = FrameUnits.RANGE;
        this.betweenExtent = Boolean.TRUE;
        return (FR) this;
    }

    @Override
    public final FR ifRows(Predicate<C> predicate) {
        if (predicate.test(this.criteriaContext.criteria())) {
            this.frameUnits = FrameUnits.ROWS;
            this.betweenExtent = Boolean.TRUE;
        }
        return (FR) this;
    }

    @Override
    public final FR ifRange(Predicate<C> predicate) {
        if (predicate.test(this.criteriaContext.criteria())) {
            this.frameUnits = FrameUnits.RANGE;
            this.betweenExtent = Boolean.TRUE;
        }
        return (FR) this;
    }

    @Override
    public final FC rows(@Nullable Object expression) {
        this.frameUnits = FrameUnits.ROWS;
        this.betweenExtent = Boolean.FALSE;
        this.frameStartExp = SQLs._nonNullExp(expression);
        return (FC) this;
    }

    @Override
    public final FC range(@Nullable Object expression) {
        this.frameUnits = FrameUnits.RANGE;
        this.betweenExtent = Boolean.FALSE;
        this.frameStartExp = SQLs._nonNullExp(expression);
        return (FC) this;
    }

    @Override
    public final FC rowsExp(Supplier<?> supplier) {
        return this.rows(supplier.get());
    }

    @Override
    public final FC rowsExp(Function<C, ?> function) {
        return this.rows(function.apply(this.criteriaContext.criteria()));
    }

    @Override
    public final FC rowsExp(Function<String, ?> function, String keyName) {
        return this.rows(function.apply(keyName));
    }

    @Override
    public final FC rangeExp(Supplier<?> supplier) {
        return this.range(supplier.get());
    }

    @Override
    public final FC rangeExp(Function<C, ?> function) {
        return this.range(function.apply(this.criteriaContext.criteria()));
    }

    @Override
    public final FC rangeExp(Function<String, ?> function, String keyName) {
        return this.range(function.apply(keyName));
    }

    @Override
    public final FC ifRows(Supplier<?> supplier) {
        final Object exp;
        exp = supplier.get();
        if (exp != null) {
            this.rows(exp);
        }
        return (FC) this;
    }

    @Override
    public final FC ifRows(Function<C, ?> supplier) {
        final Object exp;
        exp = supplier.apply(this.criteriaContext.criteria());
        if (exp != null) {
            this.rows(exp);
        }
        return (FC) this;
    }

    @Override
    public final FC ifRange(Supplier<?> supplier) {
        final Object exp;
        exp = supplier.get();
        if (exp != null) {
            this.range(exp);
        }
        return (FC) this;
    }

    @Override
    public final FC ifRange(Function<C, ?> supplier) {
        final Object exp;
        exp = supplier.apply(this.criteriaContext.criteria());
        if (exp != null) {
            this.range(exp);
        }
        return (FC) this;
    }

    @Override
    public final BR between() {
        if (this.frameUnits != null) {
            if (this.betweenExtent != Boolean.TRUE) {
                throw _Exceptions.castCriteriaApi();
            }
        }
        return (BR) this;
    }

    @Override
    public final BC between(Object expression) {
        if (this.frameUnits != null) {
            if (this.betweenExtent != Boolean.TRUE) {
                throw _Exceptions.castCriteriaApi();
            }
            this.frameStartExp = SQLs._nonNullExp(expression);
        }
        return (BC) this;
    }

    @Override
    public final BC betweenExp(Supplier<?> supplier) {
        return this.between(supplier.get());
    }

    @Override
    public final BC betweenExp(Function<C, ?> function) {
        return this.between(function.apply(this.criteriaContext.criteria()));
    }

    @Override
    public final BC betweenExp(Function<String, ?> function, String keyName) {
        return this.between(function.apply(keyName));
    }

    @Override
    public final FC and() {
        if (this.frameUnits != null) {
            if (this.betweenExtent != Boolean.TRUE || this.frameStartBound == null) {
                throw _Exceptions.castCriteriaApi();
            }
        }
        return (FC) this;
    }

    @Override
    public final NC and(Object expression) {
        if (this.frameUnits != null) {
            if (this.betweenExtent != Boolean.TRUE || this.frameStartBound == null) {
                throw _Exceptions.castCriteriaApi();
            }
            this.frameEndExp = SQLs._nonNullExp(expression);
        }
        return (NC) this;
    }

    @Override
    public final NC andExp(Supplier<?> supplier) {
        return this.and(supplier.get());
    }

    @Override
    public final NC andExp(Function<C, ?> function) {
        return this.and(function.apply(this.criteriaContext.criteria()));
    }

    @Override
    public final NC andExp(Function<String, ?> function, String keyName) {
        return this.and(function.apply(keyName));
    }

    @Override
    public final R rightBracket() {
        _Assert.nonPrepared(this.prepared);
        if (this.refWindowName == null
                && this.partitionByList == null
                && this.orderByList == null
                && this.frameUnits == null) {
            throw _Exceptions.castCriteriaApi();
        }
        this.prepared = true;
        return this.stmt;
    }

    @Override
    public final void appendSql(final _SqlContext context) {
        final StringBuilder sqlBuilder = context.sqlBuilder();

        final _Dialect dialect = context.dialect();

        //1.window name
        final String windowName = this.windowName;
        if (_StringUtils.hasText(windowName)) {
            sqlBuilder.append(Constant.SPACE);
            dialect.quoteIfNeed(windowName, sqlBuilder)
                    .append(Constant.SPACE_AS);
        }
        //2.(
        sqlBuilder.append(Constant.SPACE_LEFT_BRACKET);
        //3.reference window name
        final String refWindowName = this.refWindowName;
        if (refWindowName != null) {
            sqlBuilder.append(Constant.SPACE);
            dialect.quoteIfNeed(refWindowName, sqlBuilder);
        }
        //4.partition_clause
        final List<_Expression> partitionByList = this.partitionByList;
        if (partitionByList != null) {
            final int size = partitionByList.size();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sqlBuilder.append(Constant.SPACE_COMMA);
                }
                partitionByList.get(i).appendSql(context);
            }
        }
        //5.order_clause
        final List<ArmySortItem> orderByList = this.orderByList;
        if (orderByList != null) {
            final int size = orderByList.size();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sqlBuilder.append(Constant.SPACE_COMMA);
                }
                orderByList.get(i).appendSql(context);
            }
        }
        //6.frame_clause
        final FrameUnits frameUnits = this.frameUnits;
        if (frameUnits != null) {
            final Boolean betweenExtent = this.betweenExtent;
            if (betweenExtent == null) {
                throw _Exceptions.castCriteriaApi();
            }
            sqlBuilder.append(frameUnits.keyWords);
            if (betweenExtent) {
                sqlBuilder.append(Constant.SPACE_BETWEEN);
            }
            appendFrameBound(this.frameStartExp, this.frameStartBound, context, sqlBuilder);
            if (betweenExtent) {
                sqlBuilder.append(Constant.SPACE_AND);
                appendFrameBound(this.frameEndExp, this.frameEndBound, context, sqlBuilder);
            }

        }
        //7.)
        sqlBuilder.append(Constant.SPACE_RIGHT_BRACKET);


    }


    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final void clear() {
        this.refWindowName = null;
        this.partitionByList = null;
        this.orderByList = null;
        this.frameUnits = null;

        this.betweenExtent = null;
        this.frameStartExp = null;
        this.frameStartBound = null;
        this.frameEndExp = null;

        this.frameEndBound = null;
        this.prepared = false;
    }

    /*################################## blow private method ##################################*/

    /**
     * @see #preceding()
     * @see #following()
     * @see #currentRow()
     * @see #unboundedPreceding()
     * @see #unboundedFollowing()
     */
    final void bound(final FrameBound bound) {
        if (this.frameUnits == null) {
            return;
        }
        final Boolean betweenExtent = this.betweenExtent;
        if (betweenExtent == null) {
            throw _Exceptions.castCriteriaApi();
        } else if (!betweenExtent || this.frameStartBound == null) {
            this.frameStartBound = bound;
        } else {
            this.frameEndBound = bound;
        }
    }


    private static void appendFrameBound(final @Nullable _Expression expression, final FrameBound bound
            , final _SqlContext context, final StringBuilder sqlBuilder) {
        switch (bound) {
            case CURRENT_ROW:
            case UNBOUNDED_PRECEDING:
            case UNBOUNDED_FOLLOWING: {
                if (expression != null) {
                    throw _Exceptions.castCriteriaApi();
                }
            }
            break;
            case PRECEDING:
            case FOLLOWING: {
                if (expression == null) {
                    throw _Exceptions.castCriteriaApi();
                }
                expression.appendSql(context);
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum(bound);
        }
        sqlBuilder.append(bound.keyWords);
    }


    private enum FrameUnits implements SQLWords {

        ROWS(" ROWS"),
        RANGE(" RANGE");

        private final String keyWords;

        FrameUnits(String keyWords) {
            this.keyWords = keyWords;
        }

        @Override
        public final String render() {
            return this.keyWords;
        }

        @Override
        public final String toString() {
            return String.format("%s.%s", FrameUnits.class.getSimpleName(), this.name());
        }

    }// FrameUnits

    enum FrameBound implements SQLWords {

        CURRENT_ROW(" CURRENT ROW"),

        UNBOUNDED_PRECEDING(" UNBOUNDED PRECEDING"),
        UNBOUNDED_FOLLOWING(" UNBOUNDED FOLLOWING"),
        PRECEDING(" PRECEDING"),
        FOLLOWING(" FOLLOWING");

        private final String keyWords;

        FrameBound(String keyWords) {
            this.keyWords = keyWords;
        }

        @Override
        public final String render() {
            return this.keyWords;
        }

        @Override
        public final String toString() {
            return String.format("%s.%s", FrameBound.class.getSimpleName(), this.name());
        }

    }//FrameBound


    private static final class StandardSimpleWindow<C, R> extends SimpleWindow<
            C,
            Window._SimpleLeftBracketClause<C, R>,      //AR
            Window._SimplePartitionBySpec<C, R>,        //LR
            Window._SimpleOrderBySpec<C, R>,            //PR,
            Window._SimpleFrameUnitsSpec<C, R>,         //OR
            Window._SimpleFrameBetweenClause<C, R>,     //FR
            Window._SimpleFrameEndNonExpBoundClause<R>, //FC
            Window._SimpleFrameNonExpBoundClause<C, R>, //BR
            Window._SimpleFrameExpBoundClause<C, R>,    //BC
            Window._SimpleFrameEndExpBoundClause<R>,    //NC
            Statement._Clause,                                //MA
            Statement._Clause,                                //MB
            R>                                               //R
            implements Window._SimpleAsClause<C, R>, Window._SimpleLeftBracketClause<C, R>
            , Window._SimplePartitionBySpec<C, R>, Window._SimpleFrameBetweenClause<C, R>,
            Window._SimpleFrameEndNonExpBoundClause<R>, Window._SimpleFrameNonExpBoundClause<C, R>
            , Window._SimpleFrameExpBoundClause<C, R>, Window._SimpleFrameEndExpBoundClause<R>
            , Window._SimpleFrameBetweenAndClause<C, R> {

        private StandardSimpleWindow(String windowName, CriteriaContext criteriaContext) {
            super(windowName, criteriaContext);
        }

        private StandardSimpleWindow(String windowName, R stmt) {
            super(windowName, stmt);
        }

        @Override
        public StandardSimpleWindow<C, R> currentRow() {
            this.bound(FrameBound.CURRENT_ROW);
            return this;
        }

        @Override
        public StandardSimpleWindow<C, R> unboundedPreceding() {
            this.bound(FrameBound.UNBOUNDED_PRECEDING);
            return this;
        }

        @Override
        public StandardSimpleWindow<C, R> unboundedFollowing() {
            this.bound(FrameBound.UNBOUNDED_FOLLOWING);
            return this;
        }

        @Override
        public StandardSimpleWindow<C, R> preceding() {
            this.bound(FrameBound.PRECEDING);
            return this;
        }

        @Override
        public StandardSimpleWindow<C, R> following() {
            this.bound(FrameBound.FOLLOWING);
            return this;
        }


    }//StandardSimpleWindow


}
