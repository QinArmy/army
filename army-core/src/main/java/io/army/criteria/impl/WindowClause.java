package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLWords;
import io.army.criteria.Statement;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect.Dialect;
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
 * This class is base class of all simple {@link Window}.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class WindowClause<PR, OR, FB, FE, BN, BE, NN>
        extends OrderByClause<OR>
        implements Window._PartitionByExpClause<PR>
        , Statement._StaticOrderByClause<OR>, Window._FrameUnitExpClause<FE>, Window._FrameUnitNoExpClause<FB>
        , Window._FrameBetweenExpClause<BE>, Statement._StaticBetweenClause<BN>
        , Window._FrameBetweenAndExpClause<FE>, Statement._StaticAndClause<NN>
        , Window._FrameExpBoundClause, Window._FrameNonExpBoundClause
        , CriteriaContextSpec, ArmyWindow {


    static Window._SimplePartitionBySpec namedWindow(String windowName, CriteriaContext context
            , @Nullable String existingWindowName) {
        return new SimpleWindow(windowName, context, existingWindowName);
    }


    static Window._SimplePartitionBySpec anonymousWindow(CriteriaContext context
            , @Nullable String existingWindowName) {
        return new SimpleWindow(context, existingWindowName);
    }


    static boolean isStandardWindow(Window window) {
        return window instanceof WindowClause.SimpleWindow;
    }

    private final String windowName;

    final CriteriaContext context;

    private final String refWindowName;

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
    WindowClause(String windowName, CriteriaContext context, @Nullable String existingWindowName) {
        super(context);
        if (!_StringUtils.hasText(windowName)) {
            throw ContextStack.criteriaError(context, _Exceptions::namedWindowNoText);
        } else if (existingWindowName != null && !_StringUtils.hasText(existingWindowName)) {
            throw ContextStack.criteriaError(context, "existingWindowName must be null or non-empty");
        }
        this.windowName = windowName;
        this.context = context;
        this.refWindowName = existingWindowName;
    }


    /**
     * <p>
     * Constructor for anonymous {@link  Window}
     * </p>
     */
    WindowClause(CriteriaContext context, @Nullable String existingWindowName) {
        super(context);
        if (existingWindowName != null && !_StringUtils.hasText(existingWindowName)) {
            throw ContextStack.criteriaError(context, "existingWindowName must be null or non-empty");
        }
        this.windowName = null;
        this.context = context;
        this.refWindowName = existingWindowName;
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
    public final FB rows() {
        return this.frameUnit(FrameUnits.ROWS);
    }

    @Override
    public final FB range() {
        return this.frameUnit(FrameUnits.RANGE);
    }


    @Override
    public final FB ifRows(BooleanSupplier predicate) {
        return this.ifFrameUnit(predicate, FrameUnits.ROWS);
    }

    @Override
    public final FB ifRange(BooleanSupplier predicate) {
        return this.ifFrameUnit(predicate, FrameUnits.RANGE);
    }

    @Override
    public final FE rows(final @Nullable Expression expression) {
        return this.frameUnit(FrameUnits.ROWS, expression);
    }


    @Override
    public final FE rows(Supplier<Expression> supplier) {
        return this.rows(supplier.get());
    }

    @Override
    public final <E> FE rows(Function<E, Expression> valueOperator, E value) {
        return this.rows(valueOperator.apply(value));
    }

    @Override
    public final <E> FE rows(Function<E, Expression> valueOperator, Supplier<E> supplier) {
        return this.rows(valueOperator.apply(supplier.get()));
    }

    @Override
    public final FE rows(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        return this.rows(valueOperator.apply(function.apply(keyName)));
    }

    @Override
    public final FE range(final Expression expression) {
        return this.frameUnit(FrameUnits.RANGE, expression);
    }

    @Override
    public final FE range(Supplier<Expression> supplier) {
        return this.range(supplier.get());
    }

    @Override
    public final <E> FE range(Function<E, Expression> valueOperator, E value) {
        return this.range(valueOperator.apply(value));
    }

    @Override
    public final <E> FE range(Function<E, Expression> valueOperator, Supplier<E> supplier) {
        return this.range(valueOperator.apply(supplier.get()));
    }

    @Override
    public final FE range(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        return this.range(valueOperator.apply(function.apply(keyName)));
    }

    @Override
    public final BN between() {
        if (this.frameUnits != null) {
            if (this.betweenExtent != Boolean.TRUE) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.frameStartExp = null;
        }
        return (BN) this;
    }

    @Override
    public final BE between(final Expression expression) {
        if (this.frameUnits != null) {
            if (this.betweenExtent != Boolean.TRUE) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if ((!(expression instanceof ArmyExpression))) {
                throw ContextStack.nonArmyExp(this.context);
            }
            this.frameStartExp = (ArmyExpression) expression;
        }
        return (BE) this;
    }

    @Override
    public final BE between(Supplier<Expression> supplier) {
        return this.between(supplier.get());
    }

    @Override
    public final <E> BE between(Function<E, Expression> valueOperator, E value) {
        return this.between(valueOperator.apply(value));
    }

    @Override
    public final <E> BE between(Function<E, Expression> valueOperator, Supplier<E> supplier) {
        return this.between(valueOperator.apply(supplier.get()));
    }

    @Override
    public final BE between(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        return this.between(valueOperator.apply(function.apply(keyName)));
    }

    @Override
    public final NN and() {
        if (this.frameUnits != null) {
            if (this.betweenExtent != Boolean.TRUE) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.frameEndExp = null;
        }
        return (NN) this;
    }

    @Override
    public final FE and(final Expression expression) {
        if (this.frameUnits != null) {
            if (this.betweenExtent != Boolean.TRUE) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if ((!(expression instanceof ArmyExpression))) {
                throw ContextStack.nonArmyExp(this.context);
            }
            this.frameEndExp = (ArmyExpression) expression;
        }
        return (FE) this;
    }

    @Override
    public final FE and(Supplier<Expression> supplier) {
        return this.and(supplier.get());
    }

    @Override
    public final <E> FE and(Function<E, Expression> valueOperator, E value) {
        return this.and(valueOperator.apply(value));
    }

    @Override
    public final <E> FE and(Function<E, Expression> valueOperator, Supplier<E> supplier) {
        return this.and(valueOperator.apply(supplier.get()));
    }

    @Override
    public final FE and(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
        return this.and(valueOperator.apply(function.apply(keyName)));
    }

    @Override
    public final ArmyWindow endWindowClause() {
        _Assert.nonPrepared(this.prepared);
        this.endOrderByClause();
        this.prepared = Boolean.TRUE;
        return this;
    }

    @Override
    public final void appendSql(final _SqlContext context) {
        _Assert.prepared(this.prepared);

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();

        final DialectParser parser;
        parser = context.parser();

        //1.window name
        final String windowName = this.windowName;
        if (windowName != null) {
            sqlBuilder.append(_Constant.SPACE);
            parser.identifier(windowName, sqlBuilder)
                    .append(_Constant.SPACE_AS)
                    .append(_Constant.SPACE_LEFT_PAREN);
        }// anonymous window no parens

        //3.reference window name
        final String refWindowName = this.refWindowName;
        if (refWindowName != null) {
            sqlBuilder.append(_Constant.SPACE);
            parser.identifier(refWindowName, sqlBuilder);
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

            if (this instanceof FrameExclusionSpec) {
                ((FrameExclusionSpec) this).appendFrameExclusion(context);
            }

        }
        //7.)
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

    }

    interface FrameExclusionSpec {

        void appendFrameExclusion(_SqlContext context);
    }


    @Override
    public final String windowName() {
        final String name = this.windowName;
        if (name == null) {
            throw new IllegalStateException("this is anonymous window");
        }
        return name;
    }

    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }

    @Override
    public final boolean isPrepared() {
        final Boolean prepared = this.prepared;
        return prepared != null && prepared;
    }

    @Override
    public final void clear() {
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
     * @see #rows()
     * @see #range()
     */
    final FB frameUnit(FrameUnits units) {
        this.frameUnits = units;
        this.betweenExtent = Boolean.TRUE;
        return (FB) this;
    }

    /**
     * @see #ifRows(BooleanSupplier)
     * @see #ifRange(BooleanSupplier)
     */
    final FB ifFrameUnit(BooleanSupplier predicate, FrameUnits units) {
        if (predicate.getAsBoolean()) {
            this.frameUnits = units;
            this.betweenExtent = Boolean.TRUE;
        } else {
            this.frameUnits = null;
            this.betweenExtent = null;
        }
        return (FB) this;
    }


    /**
     * @see #rows(Expression)
     * @see #range(Expression)
     */
    final FE frameUnit(FrameUnits units, final @Nullable Expression expression) {
        if (expression == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (!(expression instanceof ArmyExpression)) {
            throw ContextStack.nonArmyExp(this.context);
        }
        this.frameUnits = units;
        this.betweenExtent = Boolean.FALSE;
        this.frameStartExp = (ArmyExpression) expression;
        return (FE) this;
    }


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

    @Override
    final Dialect statementDialect() {
        throw ContextStack.castCriteriaApi(this.context);
    }


    enum FrameUnits implements SQLWords {

        ROWS(" ROWS"),
        RANGE(" RANGE"),
        GROUPS(" GROUPS");

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


    private static class SimpleWindow extends WindowClause<
            _SimpleOrderBySpec,
            _SimpleFrameUnitsSpec,
            _SimpleFrameBetweenSpec,
            _SimpleFrameEndExpBoundClause,
            _SimpleFrameNonExpBoundClause,
            _SimpleFrameExpBoundClause,
            _SimpleFrameEndNonExpBoundClause>
            implements Window._SimplePartitionBySpec, _SimpleFrameBetweenSpec,
            Window._SimpleFrameEndNonExpBoundClause, Window._SimpleFrameNonExpBoundClause
            , Window._SimpleFrameExpBoundClause, Window._SimpleFrameEndExpBoundClause
            , Window._SimpleFrameBetweenAndClause {


        private SimpleWindow(String windowName, CriteriaContext context, @Nullable String existingWindowName) {
            super(windowName, context, existingWindowName);
        }

        private SimpleWindow(CriteriaContext context, @Nullable String existingWindowName) {
            super(context, existingWindowName);
        }

        @Override
        public final SimpleWindow currentRow() {
            this.bound(FrameBound.CURRENT_ROW);
            return this;
        }

        @Override
        public final SimpleWindow unboundedPreceding() {
            this.bound(FrameBound.UNBOUNDED_PRECEDING);
            return this;
        }

        @Override
        public final SimpleWindow unboundedFollowing() {
            this.bound(FrameBound.UNBOUNDED_FOLLOWING);
            return this;
        }

        @Override
        public final SimpleWindow preceding() {
            this.bound(FrameBound.PRECEDING);
            return this;
        }

        @Override
        public final SimpleWindow following() {
            this.bound(FrameBound.FOLLOWING);
            return this;
        }


    }//StandardSimpleWindow


}
