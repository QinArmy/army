package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Window;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;

/**
 * <p>
 * This class is base class of all simple {@link io.army.criteria.Window}.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class SimpleWindow<C, AR, LR, PR, OR, FR, FC, BR, BC, NC, MA, MB, R> implements _Window
        , Window._AsClause<AR>, Window._LeftParenNameClause<C, LR>, Window._PartitionByExpClause<C, PR>
        , Statement._OrderByClause<C, OR>, Window._FrameUnitsClause<C, FR, FC>, Window._FrameBetweenClause<C, BR, BC>
        , Window._FrameExpBoundClause<MA>, Window._FrameBetweenAndClause<C, FC, NC>, Window._FrameNonExpBoundClause<MB>
        , Statement._RightParenClause<R>, CriteriaContextSpec {


    static <C, R> Window._SimpleAsClause<C, R> standard(String windowName, R stmt) {
        return new StandardSimpleWindow<>(windowName, stmt);
    }

    static <C, R> Window._SimpleAsClause<C, R> standard(String windowName, CriteriaContext criteriaContext) {
        if (!_StringUtils.hasText(windowName)) {
            throw CriteriaContextStack.criteriaError(criteriaContext, _Exceptions::namedWindowNoText);
        }
        return new StandardSimpleWindow<>(windowName, criteriaContext);
    }

    static Window._SimpleOverLestParenSpec simpleOverClause(SQLFunctions.WinFuncSpec windowFunc) {
        return new StandardSimpleWindowSpec(windowFunc);
    }


    static boolean isIllegalWindow(Window window, CriteriaContext criteriaContext) {
        final boolean illegal;
        if (window instanceof StandardSimpleWindow) {
            illegal = ((StandardSimpleWindow<?, ?>) window).criteriaContext != criteriaContext;
        } else {
            illegal = true;
        }
        return illegal;
    }

    static boolean isStandardWindow(Window window) {
        return window instanceof StandardSimpleWindow;
    }


    private final String windowName;

    private final R stmt;

    final CriteriaContext criteriaContext;

    final C criteria;

    private String refWindowName;

    private List<_Expression> partitionByList;

    private List<ArmySortItem> orderByList;

    private FrameUnits frameUnits;

    private Boolean betweenExtent;

    private _Expression frameStartExp;

    private FrameBound frameStartBound;

    private _Expression frameEndExp;

    private FrameBound frameEndBound;

    private Boolean prepared;


    SimpleWindow(@Nullable String windowName, CriteriaContext criteriaContext) {
        if (windowName == null && !(this instanceof SQLFunctions.WinFuncSpec)) {
            //no bug,never here
            throw new IllegalArgumentException();
        }
        this.windowName = windowName;
        this.stmt = (R) this;
        this.criteriaContext = criteriaContext;
        if (windowName == null) {
            this.criteria = null;
        } else {
            this.criteria = criteriaContext.criteria();
        }
    }

    SimpleWindow(String windowName, R stmt) {
        this.windowName = windowName;
        this.stmt = stmt;
        this.criteriaContext = ((CriteriaContextSpec) stmt).getCriteriaContext();
        this.criteria = this.criteriaContext.criteria();
    }

    @Override
    public final CriteriaContext getCriteriaContext() {
        return this.criteriaContext;
    }

    @Override
    public final AR as() {
        return (AR) this;
    }

    @Override
    public final LR leftParen() {
        return (LR) this;
    }

    @Override
    public final LR leftParen(String windowName) {
        if (this.refWindowName != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        } else if (this instanceof SQLFunctions.WinFuncSpec) {
            this.criteriaContext.onRefWindow(windowName);
        } else if (!this.criteriaContext.isExistWindow(windowName)) {
            throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::windowNotExists, windowName);
        }
        this.refWindowName = windowName;
        return (LR) this;
    }

    @Override
    public final LR leftParen(Supplier<String> supplier) {
        return this.leftParen(supplier.get());
    }

    @Override
    public final LR leftParen(Function<C, String> function) {
        return this.leftParen(function.apply(this.criteria));
    }

    @Override
    public final LR leftParenIf(Supplier<String> supplier) {
        final String windowName;
        windowName = supplier.get();
        if (windowName != null) {
            this.leftParen(windowName);
        }
        return (LR) this;
    }

    @Override
    public final LR leftParenIf(Function<C, String> function) {
        final String windowName;
        windowName = function.apply(this.criteria);
        if (windowName != null) {
            this.leftParen(windowName);
        }
        return (LR) this;
    }


    @Override
    public final PR partitionBy(Expression exp) {
        this.partitionByList = Collections.singletonList((ArmyExpression) exp);
        return (PR) this;
    }

    @Override
    public final PR partitionBy(Expression exp1, Expression exp2) {
        this.partitionByList = ArrayUtils.asUnmodifiableList(
                (ArmyExpression) exp1,
                (ArmyExpression) exp2
        );
        return (PR) this;
    }

    @Override
    public final PR partitionBy(Expression exp1, Expression exp2, Expression exp3) {
        this.partitionByList = ArrayUtils.asUnmodifiableList(
                (ArmyExpression) exp1,
                (ArmyExpression) exp2,
                (ArmyExpression) exp3
        );
        return (PR) this;
    }

    @Override
    public final <E extends Expression> PR partitionBy(Consumer<Consumer<E>> consumer) {
        consumer.accept(this::addPartitionExp);
        return this.endPartitionBy(true);
    }

    @Override
    public final <E extends Expression> PR partitionBy(BiConsumer<C, Consumer<E>> consumer) {
        consumer.accept(this.criteria, this::addPartitionExp);
        return this.endPartitionBy(true);
    }

    @Override
    public final <E extends Expression> PR ifPartitionBy(Consumer<Consumer<E>> consumer) {
        consumer.accept(this::addPartitionExp);
        return this.endPartitionBy(false);
    }

    @Override
    public final <E extends Expression> PR ifPartitionBy(BiConsumer<C, Consumer<E>> consumer) {
        consumer.accept(this.criteria, this::addPartitionExp);
        return this.endPartitionBy(false);
    }


    @Override
    public final OR orderBy(SortItem sortItem) {
        this.orderByList = Collections.singletonList((ArmySortItem) sortItem);
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2) {
        this.orderByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2
        );
        return (OR) this;
    }

    @Override
    public final OR orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3) {
        this.orderByList = ArrayUtils.asUnmodifiableList(
                (ArmySortItem) sortItem1,
                (ArmySortItem) sortItem2,
                (ArmySortItem) sortItem3
        );
        return (OR) this;
    }

    @Override
    public final OR orderBy(Consumer<Consumer<SortItem>> consumer) {
        return CriteriaSupports.<C, OR>orderByClause(this.criteriaContext, this::orderByEnd)
                .orderBy(consumer);
    }

    @Override
    public final OR orderBy(BiConsumer<C, Consumer<SortItem>> consumer) {
        final Statement._OrderByClause<C, OR> clause;
        if (this instanceof SQLFunctions.WinFuncSpec) {
            clause = CriteriaSupports.voidOrderByClause(this.criteriaContext, this::orderByEnd);
        } else {
            clause = CriteriaSupports.orderByClause(this.criteriaContext, this::orderByEnd);
        }
        return clause.orderBy(consumer);
    }

    @Override
    public final OR ifOrderBy(Function<Object, ? extends SortItem> operator, Supplier<?> operand) {
        return CriteriaSupports.<C, OR>orderByClause(this.criteriaContext, this::orderByEnd)
                .ifOrderBy(operator, operand);
    }

    @Override
    public final OR ifOrderBy(Function<Object, ? extends SortItem> operator, Function<String, ?> operand, String operandKey) {
        return CriteriaSupports.<C, OR>orderByClause(this.criteriaContext, this::orderByEnd)
                .ifOrderBy(operator, operand, operandKey);
    }

    @Override
    public final OR ifOrderBy(BiFunction<Object, Object, ? extends SortItem> operator, Supplier<?> firstOperand, Supplier<?> secondOperand) {
        return CriteriaSupports.<C, OR>orderByClause(this.criteriaContext, this::orderByEnd)
                .ifOrderBy(operator, firstOperand, secondOperand);
    }

    @Override
    public final OR ifOrderBy(BiFunction<Object, Object, ? extends SortItem> operator, Function<String, ?> operand, String firstKey, String secondKey) {
        return CriteriaSupports.<C, OR>orderByClause(this.criteriaContext, this::orderByEnd)
                .ifOrderBy(operator, operand, firstKey, secondKey);
    }

    @Override
    public final OR ifOrderBy(Consumer<Consumer<SortItem>> consumer) {
        return CriteriaSupports.<C, OR>orderByClause(this.criteriaContext, this::orderByEnd)
                .ifOrderBy(consumer);
    }

    @Override
    public final OR ifOrderBy(BiConsumer<C, Consumer<SortItem>> consumer) {
        final Statement._OrderByClause<C, OR> clause;
        if (this instanceof SQLFunctions.WinFuncSpec) {
            clause = CriteriaSupports.voidOrderByClause(this.criteriaContext, this::orderByEnd);
        } else {
            clause = CriteriaSupports.orderByClause(this.criteriaContext, this::orderByEnd);
        }
        return clause.ifOrderBy(consumer);
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
        if (predicate.test(this.criteria)) {
            this.frameUnits = FrameUnits.ROWS;
            this.betweenExtent = Boolean.TRUE;
        } else {
            this.frameUnits = null;
            this.betweenExtent = null;
        }
        return (FR) this;
    }

    @Override
    public final FR ifRange(Predicate<C> predicate) {
        if (predicate.test(this.criteria)) {
            this.frameUnits = FrameUnits.RANGE;
            this.betweenExtent = Boolean.TRUE;
        } else {
            this.frameUnits = null;
            this.betweenExtent = null;
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
        return this.rows(function.apply(this.criteria));
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
        return this.range(function.apply(this.criteria));
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
        exp = supplier.apply(this.criteria);
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
        exp = supplier.apply(this.criteria);
        if (exp != null) {
            this.range(exp);
        }
        return (FC) this;
    }

    @Override
    public final BR between() {
        if (this.frameUnits != null && this.betweenExtent != Boolean.TRUE) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        return (BR) this;
    }

    @Override
    public final BC between(Object expression) {
        if (this.frameUnits != null) {
            if (this.betweenExtent != Boolean.TRUE) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
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
        return this.between(function.apply(this.criteria));
    }

    @Override
    public final BC betweenExp(Function<String, ?> function, String keyName) {
        return this.between(function.apply(keyName));
    }

    @Override
    public final FC and() {
        if (this.frameUnits != null && (this.betweenExtent != Boolean.TRUE || this.frameStartBound == null)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        return (FC) this;
    }

    @Override
    public final NC and(Object expression) {
        if (this.frameUnits != null) {
            if (this.betweenExtent != Boolean.TRUE || this.frameStartBound == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
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
        return this.and(function.apply(this.criteria));
    }

    @Override
    public final NC andExp(Function<String, ?> function, String keyName) {
        return this.and(function.apply(keyName));
    }

    @Override
    public final R rightParen() {
        _Assert.nonPrepared(this.prepared);
        this.prepared = Boolean.TRUE;
        return this.stmt;
    }

    @Override
    public final void appendSql(final _SqlContext context) {
        _Assert.prepared(this.prepared);

        final StringBuilder sqlBuilder = context.sqlBuilder();

        final DialectParser dialect = context.parser();

        //1.window name or window function
        final String windowName = this.windowName;
        if (windowName == null) {
            ((SQLFunctions.WinFuncSpec) this).appendFunc(context);
            sqlBuilder.append(_Constant.SPACE_OVER);
        } else {
            sqlBuilder.append(_Constant.SPACE);
            dialect.identifier(windowName, sqlBuilder)
                    .append(_Constant.SPACE_AS);
        }
        //2.(
        sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
        //3.reference window name
        final String refWindowName = this.refWindowName;
        if (refWindowName != null) {
            sqlBuilder.append(_Constant.SPACE);
            dialect.identifier(refWindowName, sqlBuilder);
        }
        //4.partition_clause
        final List<_Expression> partitionByList = this.partitionByList;
        if (partitionByList != null) {
            final int size = partitionByList.size();
            assert size > 0;
            sqlBuilder.append(_Constant.SPACE_PARTITION_BY);
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                partitionByList.get(i).appendSql(context);
            }
        }
        //5.order_clause
        final List<ArmySortItem> orderByList = this.orderByList;
        if (orderByList != null) {
            final int size = orderByList.size();
            assert size > 0;
            sqlBuilder.append(_Constant.SPACE_ORDER_BY);
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                orderByList.get(i).appendSql(context);
            }
        }
        //6.frame_clause
        final FrameUnits frameUnits = this.frameUnits;
        if (frameUnits != null) {
            final Boolean betweenExtent = this.betweenExtent;
            if (betweenExtent == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            sqlBuilder.append(frameUnits.keyWords);
            if (betweenExtent) {
                sqlBuilder.append(_Constant.SPACE_BETWEEN);
            }
            appendFrameBound(this.frameStartExp, this.frameStartBound, context, sqlBuilder);
            if (betweenExtent) {
                sqlBuilder.append(_Constant.SPACE_AND);
                appendFrameBound(this.frameEndExp, this.frameEndBound, context, sqlBuilder);
            }

        }
        //7.)
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

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

    /*################################## blow package method ##################################*/


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


    private void addPartitionExp(final @Nullable Expression expression) {
        if (expression == null) {
            throw CriteriaContextStack.nullPointer(this.criteriaContext);
        }
        if (!(expression instanceof ArmyExpression)) {
            throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
        }
        List<_Expression> partitionByList = this.partitionByList;
        if (partitionByList == null) {
            this.partitionByList = partitionByList = new ArrayList<>();
        } else if (!(partitionByList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        partitionByList.add((ArmyExpression) expression);
    }


    private PR endPartitionBy(final boolean required) {
        final List<_Expression> partitionByList = this.partitionByList;
        if (partitionByList instanceof ArrayList) {
            this.partitionByList = _CollectionUtils.unmodifiableList(partitionByList);
        } else if (partitionByList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        } else if (required) {
            throw CriteriaContextStack.criteriaError(this.criteriaContext, "partition by claus is empty.");
        } else {
            this.partitionByList = Collections.emptyList();
        }
        return (PR) this;
    }

    private OR orderByEnd(final List<ArmySortItem> itemList) {
        if (this.orderByList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        this.orderByList = itemList;
        return (OR) this;
    }


    private void appendFrameBound(final @Nullable _Expression expression, final FrameBound bound
            , final _SqlContext context, final StringBuilder sqlBuilder) {
        switch (bound) {
            case CURRENT_ROW:
            case UNBOUNDED_PRECEDING:
            case UNBOUNDED_FOLLOWING: {
                if (expression != null) {
                    throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
                }
            }
            break;
            case PRECEDING:
            case FOLLOWING: {
                if (expression == null) {
                    throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
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


    private static class StandardSimpleWindow<C, R> extends SimpleWindow<
            C,
            Window._SimpleLeftParenClause<C, R>,      //AR
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
            implements Window._SimpleAsClause<C, R>, Window._SimpleLeftParenClause<C, R>
            , Window._SimplePartitionBySpec<C, R>, Window._SimpleFrameBetweenClause<C, R>,
            Window._SimpleFrameEndNonExpBoundClause<R>, Window._SimpleFrameNonExpBoundClause<C, R>
            , Window._SimpleFrameExpBoundClause<C, R>, Window._SimpleFrameEndExpBoundClause<R>
            , Window._SimpleFrameBetweenAndClause<C, R>, Window {

        private StandardSimpleWindow(@Nullable String windowName, CriteriaContext criteriaContext) {
            super(windowName, criteriaContext);
        }

        private StandardSimpleWindow(String windowName, R stmt) {
            super(windowName, stmt);
        }

        @Override
        public final StandardSimpleWindow<C, R> currentRow() {
            this.bound(FrameBound.CURRENT_ROW);
            return this;
        }

        @Override
        public final StandardSimpleWindow<C, R> unboundedPreceding() {
            this.bound(FrameBound.UNBOUNDED_PRECEDING);
            return this;
        }

        @Override
        public final StandardSimpleWindow<C, R> unboundedFollowing() {
            this.bound(FrameBound.UNBOUNDED_FOLLOWING);
            return this;
        }

        @Override
        public final StandardSimpleWindow<C, R> preceding() {
            this.bound(FrameBound.PRECEDING);
            return this;
        }

        @Override
        public final StandardSimpleWindow<C, R> following() {
            this.bound(FrameBound.FOLLOWING);
            return this;
        }


    }//StandardSimpleWindow


    private static class StandardSimpleWindowSpec extends StandardSimpleWindow<Void, SelectionSpec>
            implements SelectionSpec, SQLFunctions.WinFuncSpec, Window._SimpleOverLestParenSpec {

        private ParamMeta returnType;

        private final SQLFunctions.WinFuncSpec windowFunc;

        private StandardSimpleWindowSpec(SQLFunctions.WinFuncSpec windowFunc) {
            super(null, windowFunc.getCriteriaContext());
            this.windowFunc = windowFunc;
        }

        @Override
        public final Selection as(final String alias) {
            return Selections.forFunc(this, alias);
        }

        @Override
        public final SelectionSpec asType(ParamMeta paramMeta) {
            if (this.returnType != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.returnType = paramMeta;
            return this;
        }

        @Override
        public final ParamMeta paramMeta() {
            ParamMeta returnType = this.returnType;
            if (returnType == null) {
                returnType = this.windowFunc.paramMeta();
            }
            return returnType;
        }

        @Override
        public final void appendFunc(final _SqlContext context) {
            this.windowFunc.appendFunc(context);
        }


    }//StandardSimpleWindowFunc


}
