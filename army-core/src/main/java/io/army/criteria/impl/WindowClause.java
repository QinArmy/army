package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Window;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of all simple {@link io.army.criteria.Window}.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class WindowClause<I extends Item, AR, LR, PR, OR, FR, FC, BR, BC, NC, MA extends Item, MB extends Item, R>
        extends OrderByClause<OR>
        implements _Window
        , Statement._StaticAsClaus<AR>, Window._LeftParenNameClause<LR>, Window._PartitionByExpClause<PR>
        , Statement._OrderByClause<OR>, Window._FrameUnitsClause<FR, FC>, Window._FrameBetweenClause<BR, BC>
        , Window._FrameExpBoundClause<MA>, Window._FrameBetweenAndClause<FC, NC>, Window._FrameNonExpBoundClause<MB>
        , Statement._RightParenClause<R>, CriteriaContextSpec, Window {


    static <I extends Item> Window._SimpleAsClause<I> namedWindow(String windowName, CriteriaContext context
            , Function<_Window, I> function) {
        return new SimpleWindow<>(windowName, context, function);
    }


    static Window._SimpleLeftParenClause<Expression> anonymousWindow(CriteriaContext context
            , Function<_Window, Expression> function) {
        return new SimpleWindow<>(context, function);
    }


    static boolean isStandardWindow(Window window) {
        return window instanceof WindowClause.SimpleWindow;
    }


    private final String windowName;

    private final Function<_Window, R> function;

    final CriteriaContext context;

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


    /**
     * <p>
     * Constructor for named {@link  Window}
     * </p>
     */
    WindowClause(String windowName, CriteriaContext context, Function<_Window, R> function) {
        super(context);
        assert _StringUtils.hasText(windowName);
        this.windowName = windowName;
        this.context = context;
        this.function = function;
    }


    /**
     * <p>
     * Constructor for anonymous {@link  Window}
     * </p>
     */
    WindowClause(CriteriaContext context, Function<_Window, R> function) {
        super(context);
        this.windowName = null;
        this.function = function;
        this.context = context;
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
    public final LR leftParen(final @Nullable String windowName) {
        if (windowName == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (this.refWindowName != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (this.windowName == null) {//anonymous window for over clause
            this.context.onRefWindow(windowName);
        } else if (!this.context.isExistWindow(windowName)) {
            throw ContextStack.criteriaError(this.context, _Exceptions::windowNotExists, windowName);
        }
        this.refWindowName = windowName;
        return (LR) this;
    }

    @Override
    public final LR leftParen(Supplier<String> supplier) {
        return this.leftParen(supplier.get());
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
    public final PR partitionBy(Consumer<Consumer<Expression>> consumer) {
        consumer.accept(this::addPartitionExp);
        return this.endPartitionBy(true);
    }

    @Override
    public final PR ifPartitionBy(Consumer<Consumer<Expression>> consumer) {
        consumer.accept(this::addPartitionExp);
        return this.endPartitionBy(false);
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
    public final FR ifRows(BooleanSupplier predicate) {
        if (predicate.getAsBoolean()) {
            this.frameUnits = FrameUnits.ROWS;
            this.betweenExtent = Boolean.FALSE;
        } else {
            this.frameUnits = null;
            this.betweenExtent = null;
        }
        return (FR) this;
    }

    @Override
    public final FR ifRange(BooleanSupplier predicate) {
        if (predicate.getAsBoolean()) {
            this.frameUnits = FrameUnits.RANGE;
            this.betweenExtent = Boolean.FALSE;
        } else {
            this.frameUnits = null;
            this.betweenExtent = null;
        }
        return (FR) this;
    }

    @Override
    public final FC rows(final Expression expression) {
        if (!(expression instanceof ArmyExpression)) {
            throw ContextStack.nonArmyExp(this.context);
        }
        this.frameUnits = FrameUnits.ROWS;
        this.betweenExtent = Boolean.FALSE;
        this.frameStartExp = (ArmyExpression) expression;
        return (FC) this;
    }

    @Override
    public final FC rows(Supplier<Expression> supplier) {
        return this.rows(supplier.get());
    }

    @Override
    public final <E> FC rows(Function<E, Expression> valueOperator, E value) {
        return this.rows(valueOperator.apply(value));
    }

    @Override
    public final <E> FC rows(Function<E, Expression> valueOperator, Supplier<E> supplier) {
        return this.rows(valueOperator.apply(supplier.get()));
    }

    @Override
    public final FC rows(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        return this.rows(valueOperator.apply(function.apply(keyName)));
    }

    @Override
    public final FC range(final Expression expression) {
        if (!(expression instanceof ArmyExpression)) {
            throw ContextStack.nonArmyExp(this.context);
        }
        this.frameUnits = FrameUnits.RANGE;
        this.betweenExtent = Boolean.FALSE;
        this.frameStartExp = (ArmyExpression) expression;
        return (FC) this;
    }

    @Override
    public final FC range(Supplier<Expression> supplier) {
        return this.range(supplier.get());
    }

    @Override
    public final <E> FC range(Function<E, Expression> valueOperator, E value) {
        return this.range(valueOperator.apply(value));
    }

    @Override
    public final <E> FC range(Function<E, Expression> valueOperator, Supplier<E> supplier) {
        return this.range(valueOperator.apply(supplier.get()));
    }

    @Override
    public final FC range(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        return this.range(valueOperator.apply(function.apply(keyName)));
    }

    @Override
    public final FC ifRows(Supplier<Expression> supplier) {
        final Expression expression;
        expression = supplier.get();
        if (expression != null) {
            this.rows(expression);
        }
        return (FC) this;
    }

    @Override
    public final <E> FC ifRows(Function<E, Expression> valueOperator, @Nullable E value) {
        if (value != null) {
            this.rows(valueOperator.apply(value));
        }
        return (FC) this;
    }

    @Override
    public final <E> FC ifRows(Function<E, Expression> valueOperator, Supplier<E> supplier) {
        final E value;
        value = supplier.get();
        if (value != null) {
            this.rows(valueOperator.apply(value));
        }
        return (FC) this;
    }

    @Override
    public final FC ifRows(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.rows(valueOperator.apply(value));
        }
        return (FC) this;
    }

    @Override
    public final FC ifRange(Supplier<Expression> supplier) {
        final Expression expression;
        expression = supplier.get();
        if (expression != null) {
            this.range(expression);
        }
        return (FC) this;
    }

    @Override
    public final <E> FC ifRange(Function<E, Expression> valueOperator, @Nullable E value) {
        if (value != null) {
            this.range(valueOperator.apply(value));
        }
        return (FC) this;
    }

    @Override
    public final <E> FC ifRange(Function<E, Expression> valueOperator, Supplier<E> supplier) {
        final E value;
        value = supplier.get();
        if (value != null) {
            this.range(valueOperator.apply(value));
        }
        return (FC) this;
    }

    @Override
    public final FC ifRange(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.range(valueOperator.apply(value));
        }
        return (FC) this;
    }

    @Override
    public final BR between() {
        if (this.frameUnits != null) {
            if (this.betweenExtent != Boolean.TRUE) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.frameStartExp = null;
        }
        return (BR) this;
    }

    @Override
    public final BC between(final Expression expression) {
        if (this.frameUnits != null) {
            if (this.betweenExtent != Boolean.TRUE) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if ((!(expression instanceof ArmyExpression))) {
                throw ContextStack.nonArmyExp(this.context);
            }
            this.frameStartExp = (ArmyExpression) expression;
        }
        return (BC) this;
    }

    @Override
    public final BC between(Supplier<Expression> supplier) {
        return this.between(supplier.get());
    }

    @Override
    public final <E> BC between(Function<E, Expression> valueOperator, E value) {
        return this.between(valueOperator.apply(value));
    }

    @Override
    public final <E> BC between(Function<E, Expression> valueOperator, Supplier<E> supplier) {
        return this.between(valueOperator.apply(supplier.get()));
    }

    @Override
    public final BC between(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        return this.between(valueOperator.apply(function.apply(keyName)));
    }

    @Override
    public final FC and() {
        if (this.frameUnits != null) {
            if (this.betweenExtent != Boolean.TRUE) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.frameEndExp = null;
        }
        return (FC) this;
    }

    @Override
    public final NC and(final Expression expression) {
        if (this.frameUnits != null) {
            if (this.betweenExtent != Boolean.TRUE) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if ((!(expression instanceof ArmyExpression))) {
                throw ContextStack.nonArmyExp(this.context);
            }
            this.frameEndExp = (ArmyExpression) expression;
        }
        return (NC) this;
    }

    @Override
    public final NC and(Supplier<Expression> supplier) {
        return this.and(supplier.get());
    }

    @Override
    public final <E> NC and(Function<E, Expression> valueOperator, E value) {
        return this.and(valueOperator.apply(value));
    }

    @Override
    public final <E> NC and(Function<E, Expression> valueOperator, Supplier<E> supplier) {
        return this.and(valueOperator.apply(supplier.get()));
    }

    @Override
    public final NC and(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        return this.and(valueOperator.apply(function.apply(keyName)));
    }

    @Override
    public final R rightParen() {
        _Assert.nonPrepared(this.prepared);
        this.prepared = Boolean.TRUE;
        return this.function.apply(this);
    }

    @Override
    public final void appendSql(final _SqlContext context) {
        _Assert.prepared(this.prepared);

        final StringBuilder sqlBuilder = context.sqlBuilder();

        final DialectParser dialect = context.parser();

        //1.window name
        final String windowName = this.windowName;
        if (windowName != null) {
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
                throw ContextStack.castCriteriaApi(this.context);
            }
            sqlBuilder.append(frameUnits.spaceWord);
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
            throw ContextStack.castCriteriaApi(this.context);
        } else if (!betweenExtent || this.frameStartBound == null) {
            this.frameStartBound = bound;
        } else {
            this.frameEndBound = bound;
        }
    }


    private void addPartitionExp(final @Nullable Expression expression) {
        if (expression == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (!(expression instanceof ArmyExpression)) {
            throw ContextStack.nonArmyExp(this.context);
        }
        List<_Expression> partitionByList = this.partitionByList;
        if (partitionByList == null) {
            this.partitionByList = partitionByList = new ArrayList<>();
        } else if (!(partitionByList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        partitionByList.add((ArmyExpression) expression);
    }


    private PR endPartitionBy(final boolean required) {
        final List<_Expression> partitionByList = this.partitionByList;
        if (partitionByList instanceof ArrayList) {
            this.partitionByList = _CollectionUtils.unmodifiableList(partitionByList);
        } else if (partitionByList != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (required) {
            throw ContextStack.criteriaError(this.context, "partition by claus is empty.");
        } else {
            this.partitionByList = Collections.emptyList();
        }
        return (PR) this;
    }


    private void appendFrameBound(final @Nullable _Expression expression, final FrameBound bound
            , final _SqlContext context, final StringBuilder sqlBuilder) {
        switch (bound) {
            case CURRENT_ROW:
            case UNBOUNDED_PRECEDING:
            case UNBOUNDED_FOLLOWING: {
                if (expression != null) {
                    throw ContextStack.castCriteriaApi(this.context);
                }
            }
            break;
            case PRECEDING:
            case FOLLOWING: {
                if (expression == null) {
                    throw ContextStack.castCriteriaApi(this.context);
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

        private final String spaceWord;

        FrameUnits(String spaceWord) {
            this.spaceWord = spaceWord;
        }

        @Override
        public final String render() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return _StringUtils.builder()
                    .append(FrameUnits.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
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
            return _StringUtils.builder()
                    .append(FrameBound.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.name())
                    .toString();
        }

    }//FrameBound


    private static class SimpleWindow<I extends Item> extends WindowClause<
            I,
            _SimpleLeftParenClause<I>,      //AR
            _SimplePartitionBySpec<I>,        //LR
            _SimpleOrderBySpec<I>,            //PR,
            _SimpleFrameUnitsSpec<I>,         //OR
            _SimpleFrameBetweenClause<I>,     //FR
            _SimpleFrameEndNonExpBoundClause<I>, //FC
            _SimpleFrameNonExpBoundClause<I>, //BR
            _SimpleFrameExpBoundClause<I>,    //BC
            _SimpleFrameEndExpBoundClause<I>,    //NC
            Item,                                //MA
            Item,                                //MB
            I>                                               //R
            implements Window._SimpleAsClause<I>, Window._SimpleLeftParenClause<I>
            , Window._SimplePartitionBySpec<I>, Window._SimpleFrameBetweenClause<I>,
            Window._SimpleFrameEndNonExpBoundClause<I>, Window._SimpleFrameNonExpBoundClause<I>
            , Window._SimpleFrameExpBoundClause<I>, Window._SimpleFrameEndExpBoundClause<I>
            , Window._SimpleFrameBetweenAndClause<I> {


        private SimpleWindow(String windowName, CriteriaContext context, Function<_Window, I> function) {
            super(windowName, context, function);
        }

        private SimpleWindow(CriteriaContext context, Function<_Window, I> function) {
            super(context, function);
        }

        @Override
        public final SimpleWindow<I> currentRow() {
            this.bound(FrameBound.CURRENT_ROW);
            return this;
        }

        @Override
        public final SimpleWindow<I> unboundedPreceding() {
            this.bound(FrameBound.UNBOUNDED_PRECEDING);
            return this;
        }

        @Override
        public final SimpleWindow<I> unboundedFollowing() {
            this.bound(FrameBound.UNBOUNDED_FOLLOWING);
            return this;
        }

        @Override
        public final SimpleWindow<I> preceding() {
            this.bound(FrameBound.PRECEDING);
            return this;
        }

        @Override
        public final SimpleWindow<I> following() {
            this.bound(FrameBound.FOLLOWING);
            return this;
        }


    }//StandardSimpleWindow


}
