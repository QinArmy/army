package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.SortItem;
import io.army.criteria.Statement;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect.Dialect;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.IntegerType;
import io.army.util._Assert;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * <p>
 * This class is base class of all simple {@link Window}.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class SQLWindow<PR, OR, OD, FS, FB, BR, DC, R>
        extends OrderByClause<OR, OD>
        implements Window._PartitionByExpClause<PR>,
        Window._PartitionByCommaClause<PR>,
        Statement._StaticOrderByClause<OR>,
        Window._StaticFrameUnitRowsRangeGroupsSpec<FS, FB>,
        Window._FrameUnitSpaceClause<FS, FB>,
        Window._DynamicFrameUnitRowsRangeGroupsClause<DC, R>,
        Window._FrameBetweenClause<BR>,
        Window._FrameBetweenAndClause<BR>,
        CriteriaContextSpec,
        ArmyWindow {


    static ArmyWindow namedGlobalWindow(final CriteriaContext context, final String windowName) {
        if (!_StringUtils.hasText(windowName)) {
            throw ContextStack.criteriaError(context, _Exceptions::namedWindowNoText);
        }
        context.onAddWindow(windowName);
        return new SimpleWindow(windowName);
    }

    static ArmyWindow namedRefWindow(CriteriaContext context, String windowName, @Nullable String refWindowName) {
        if (!_StringUtils.hasText(windowName)) {
            throw ContextStack.criteriaError(context, _Exceptions::namedWindowNoText);
        }
        final ArmyWindow window;
        if (refWindowName == null) {
            window = new SimpleWindow(windowName);
        } else if (_StringUtils.hasText(refWindowName)) {
            context.onRefWindow(refWindowName);
            window = new SimpleWindow(windowName, refWindowName);
        } else {
            throw ContextStack.criteriaError(context, "exists window name must be null or has text.");
        }
        context.onAddWindow(windowName);
        return window;
    }


    static boolean isSimpleWindow(final @Nullable Window window) {
        return window instanceof SQLWindow.SimpleWindow;
    }

    final String windowName;

    final CriteriaContext context;

    private final String refWindowName;

    private List<_Expression> partitionByList;

    private FrameUnits frameUnits;

    private Boolean betweenExtent;

    private ArmyExpression frameStartExp;

    private FrameBound frameStartBound;

    private ArmyExpression frameEndExp;

    private FrameBound frameEndBound;

    private Boolean prepared;


    /**
     * <p>
     * Constructor for named {@link  Window}
     * </p>
     */
    SQLWindow(final String windowName, final CriteriaContext context, final @Nullable String existingWindowName) {
        super(context);
        if (!_StringUtils.hasText(windowName)) {
            throw ContextStack.criteriaError(context, _Exceptions::namedWindowNoText);
        } else if (existingWindowName != null) {
            if (!_StringUtils.hasText(existingWindowName)) {
                throw ContextStack.criteriaError(context, "existingWindowName must be null or non-empty");
            }
            context.onRefWindow(existingWindowName);
        }
        context.onAddWindow(windowName);
        this.windowName = windowName;
        this.context = context;
        this.refWindowName = existingWindowName;
    }


    /**
     * <p>
     * Constructor for anonymous {@link  Window}
     * </p>
     */
    SQLWindow(final CriteriaContext context, final @Nullable String existingWindowName) {
        super(context);
        if (existingWindowName != null) {
            if (!_StringUtils.hasText(existingWindowName)) {
                throw ContextStack.criteriaError(context, "existingWindowName must be null or non-empty");
            }
            context.onRefWindow(existingWindowName);
        }
        this.windowName = null;
        this.context = context;
        this.refWindowName = existingWindowName;
    }

    @Override
    public final PR partitionBy(Expression exp) {
        this.addPartitionExp(exp);
        return (PR) this;
    }

    @Override
    public final PR partitionBy(Expression exp1, Expression exp2) {
        this.addPartitionExp(exp1);
        this.addPartitionExp(exp2);
        return (PR) this;
    }

    @Override
    public final PR partitionBy(Expression exp1, Expression exp2, Expression exp3) {
        this.addPartitionExp(exp1);
        this.addPartitionExp(exp2);
        this.addPartitionExp(exp3);
        return (PR) this;
    }

    @Override
    public final PR partitionBy(Expression exp1, Expression exp2, Expression exp3, Expression exp4) {
        this.addPartitionExp(exp1);
        this.addPartitionExp(exp2);
        this.addPartitionExp(exp3);
        this.addPartitionExp(exp4);
        return (PR) this;
    }

    @Override
    public final PR comma(Expression exp) {
        this.addPartitionExp(exp);
        return (PR) this;
    }

    @Override
    public final PR comma(Expression exp1, Expression exp2) {
        this.addPartitionExp(exp1);
        this.addPartitionExp(exp2);
        return (PR) this;
    }

    @Override
    public final PR comma(Expression exp1, Expression exp2, Expression exp3) {
        this.addPartitionExp(exp1);
        this.addPartitionExp(exp2);
        this.addPartitionExp(exp3);
        return (PR) this;
    }

    @Override
    public final PR comma(Expression exp1, Expression exp2, Expression exp3, Expression exp4) {
        this.addPartitionExp(exp1);
        this.addPartitionExp(exp2);
        this.addPartitionExp(exp3);
        this.addPartitionExp(exp4);
        return (PR) this;
    }

    @Override
    public final PR partitionBy(Consumer<Consumer<Expression>> consumer) {
        consumer.accept(this::addPartitionExp);
        return this.endPartitionIfNeed(true);
    }

    @Override
    public final PR ifPartitionBy(Consumer<Consumer<Expression>> consumer) {
        consumer.accept(this::addPartitionExp);
        return this.endPartitionIfNeed(false);
    }


    @Override
    public final FS rows(final RowModifier modifier) {
        return this.startExtent(FrameUnits.ROWS, modifier);
    }

    @Override
    public final FS rows(Expression exp, ExpModifier modifier) {
        return this.startExtent(FrameUnits.ROWS, exp, modifier);
    }

    @Override
    public final <T> FS rows(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier modifier) {
        return this.startExtent(FrameUnits.ROWS, funcRef.apply(IntegerType.INSTANCE, value), modifier);
    }

    @Override
    public final FS range(RowModifier modifier) {
        return this.startExtent(FrameUnits.RANGE, modifier);
    }

    @Override
    public final FS range(Expression exp, ExpModifier modifier) {
        return this.startExtent(FrameUnits.RANGE, exp, modifier);
    }

    @Override
    public final <T> FS range(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier modifier) {
        return this.startExtent(FrameUnits.RANGE, funcRef.apply(IntegerType.INSTANCE, value), modifier);
    }

    @Override
    public final FS groups(RowModifier modifier) {
        return this.startExtent(FrameUnits.GROUPS, modifier);
    }

    @Override
    public final FS groups(Expression exp, ExpModifier modifier) {
        return this.startExtent(FrameUnits.GROUPS, exp, modifier);
    }

    @Override
    public final <T> FS groups(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier modifier) {
        return this.startExtent(FrameUnits.GROUPS, funcRef.apply(IntegerType.INSTANCE, value), modifier);
    }


    @Override
    public final FB rows() {
        return this.betweenExtent(FrameUnits.ROWS);
    }

    @Override
    public final FB range() {
        return this.betweenExtent(FrameUnits.RANGE);
    }

    @Override
    public final FB groups() {
        return this.betweenExtent(FrameUnits.GROUPS);
    }

    @Override
    public final R ifRows(Consumer<DC> consumer) {
        return this.ifFrame(FrameUnits.ROWS, consumer);
    }

    @Override
    public final R ifRange(Consumer<DC> consumer) {
        return this.ifFrame(FrameUnits.RANGE, consumer);
    }

    @Override
    public final R ifGroups(Consumer<DC> consumer) {
        return this.ifFrame(FrameUnits.GROUPS, consumer);
    }

    @Override
    public final FS space(final RowModifier modifier) {
        if (this.frameUnits == null || this.frameStartBound != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (!(modifier instanceof WindowRowModifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        this.frameStartExp = null;
        this.frameStartBound = (WindowRowModifier) modifier;
        this.betweenExtent = Boolean.FALSE;
        return (FS) this;
    }

    @Override
    public final FS space(final Expression exp, final ExpModifier modifier) {
        if (this.frameUnits == null || this.frameStartBound != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (!(exp instanceof ArmyExpression)) {
            throw ContextStack.nonArmyExp(this.context);
        } else if (!(modifier instanceof WindowExpModifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        this.frameStartExp = (ArmyExpression) exp;
        this.frameStartBound = (WindowExpModifier) modifier;
        this.betweenExtent = Boolean.FALSE;
        return (FS) this;
    }

    @Override
    public final <T> FS space(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier modifier) {
        return this.space(funcRef.apply(IntegerType.INSTANCE, value), modifier);
    }

    @Override
    public final FB space() {
        if (this.frameUnits == null || this.betweenExtent != null || this.frameStartBound != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.betweenExtent = Boolean.TRUE;
        return (FB) this;
    }

    @Override
    public final BR between(final RowModifier frameStart, SQLs.WordAnd and, final RowModifier frameEnd) {
        if (this.frameUnits == null
                || this.betweenExtent != Boolean.TRUE
                || this.frameStartBound != null
                || this.frameEndBound != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (!(frameStart instanceof WindowRowModifier)) {
            throw CriteriaUtils.errorModifier(this.context, frameStart);
        } else if (!(frameEnd instanceof WindowRowModifier)) {
            throw CriteriaUtils.errorModifier(this.context, frameEnd);
        }
        this.frameStartExp = null;
        this.frameStartBound = (WindowRowModifier) frameStart;
        this.frameEndExp = null;
        this.frameEndBound = (WindowRowModifier) frameEnd;
        return (BR) this;
    }

    @Override
    public final BR between(final Expression startExp, final ExpModifier startModifier, SQLs.WordAnd and,
                            final Expression endExp, final ExpModifier endModifier) {
        if (this.frameUnits == null
                || this.betweenExtent != Boolean.TRUE
                || this.frameStartBound != null
                || this.frameEndBound != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (!(startExp instanceof ArmyExpression)) {
            throw ContextStack.nonArmyExp(this.context);
        } else if (!(endExp instanceof ArmyExpression)) {
            throw ContextStack.nonArmyExp(this.context);
        } else if (!(startModifier instanceof WindowExpModifier)) {
            throw CriteriaUtils.errorModifier(this.context, startModifier);
        } else if (!(endModifier instanceof WindowExpModifier)) {
            throw CriteriaUtils.errorModifier(this.context, endModifier);
        }
        this.frameStartExp = (ArmyExpression) startExp;
        this.frameStartBound = (WindowExpModifier) startModifier;
        this.frameEndExp = (ArmyExpression) endExp;
        this.frameEndBound = (WindowExpModifier) endModifier;
        return (BR) this;
    }

    @Override
    public final BR between(final RowModifier frameStart, SQLs.WordAnd and, final Expression endExp,
                            final ExpModifier endModifier) {
        if (this.frameUnits == null
                || this.betweenExtent != Boolean.TRUE
                || this.frameStartBound != null
                || this.frameEndBound != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (!(frameStart instanceof WindowRowModifier)) {
            throw CriteriaUtils.errorModifier(this.context, frameStart);
        } else if (!(endExp instanceof ArmyExpression)) {
            throw ContextStack.nonArmyExp(this.context);
        } else if (!(endModifier instanceof WindowExpModifier)) {
            throw CriteriaUtils.errorModifier(this.context, endModifier);
        }
        this.frameStartExp = null;
        this.frameStartBound = (WindowRowModifier) frameStart;
        this.frameEndExp = (ArmyExpression) endExp;
        this.frameEndBound = (WindowExpModifier) endModifier;
        return (BR) this;
    }

    @Override
    public final BR between(final Expression startExp, final ExpModifier startModifier, SQLs.WordAnd and,
                            final RowModifier frameEnd) {
        if (this.frameUnits == null
                || this.betweenExtent != Boolean.TRUE
                || this.frameStartBound != null
                || this.frameEndBound != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (!(startExp instanceof ArmyExpression)) {
            throw ContextStack.nonArmyExp(this.context);
        } else if (!(startModifier instanceof WindowExpModifier)) {
            throw CriteriaUtils.errorModifier(this.context, startModifier);
        } else if (!(frameEnd instanceof WindowRowModifier)) {
            throw CriteriaUtils.errorModifier(this.context, frameEnd);
        }
        this.frameStartExp = (ArmyExpression) startExp;
        this.frameStartBound = (WindowExpModifier) startModifier;
        this.frameEndExp = null;
        this.frameEndBound = (WindowRowModifier) frameEnd;
        return (BR) this;
    }

    @Override
    public final _FrameBetweenAndClause<BR> between(final Expression startExp, final ExpModifier startModifier) {
        if (this.frameUnits == null
                || this.betweenExtent != Boolean.TRUE
                || this.frameStartBound != null
                || this.frameEndBound != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (!(startExp instanceof ArmyExpression)) {
            throw ContextStack.nonArmyExp(this.context);
        } else if (!(startModifier instanceof WindowExpModifier)) {
            throw CriteriaUtils.errorModifier(this.context, startModifier);
        }
        this.frameStartExp = (ArmyExpression) startExp;
        this.frameStartBound = (WindowExpModifier) startModifier;
        return this;
    }

    @Override
    public final <T> BR between(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier startModifier,
                                SQLs.WordAnd and, RowModifier frameEnd) {
        return this.between(funcRef.apply(IntegerType.INSTANCE, value), startModifier, and, frameEnd);
    }

    @Override
    public final <T> BR between(RowModifier frameStart, SQLs.WordAnd and,
                                BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier endModifier) {
        return this.between(frameStart, and, funcRef.apply(IntegerType.INSTANCE, value), endModifier);
    }

    @Override
    public final <T> BR between(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier startModifier,
                                SQLs.WordAnd and, Expression endExp, ExpModifier endModifier) {
        return this.between(funcRef.apply(IntegerType.INSTANCE, value), startModifier, and, endExp, endModifier);
    }

    @Override
    public final <T> BR between(Expression startExp, ExpModifier startModifier, SQLs.WordAnd and,
                                BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier endModifier) {
        return this.between(startExp, startModifier, and, funcRef.apply(IntegerType.INSTANCE, value), endModifier);
    }

    @Override
    public final <T, U> BR between(BiFunction<IntegerType, T, Expression> funcRefForStart, T startValue,
                                   ExpModifier startModifier, SQLs.WordAnd and,
                                   BiFunction<IntegerType, U, Expression> funcRefForEnd, U endValue,
                                   ExpModifier endModifier) {
        return this.between(funcRefForStart.apply(IntegerType.INSTANCE, startValue), startModifier, and,
                funcRefForEnd.apply(IntegerType.INSTANCE, endValue), endModifier);
    }

    @Override
    public final BR and(final Expression endExp, final ExpModifier endModifier) {
        if (this.frameUnits == null
                || this.betweenExtent != Boolean.TRUE
                || this.frameStartBound == null
                || this.frameEndBound != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (!(endExp instanceof ArmyExpression)) {
            throw ContextStack.nonArmyExp(this.context);
        } else if (!(endModifier instanceof WindowExpModifier)) {
            throw CriteriaUtils.errorModifier(this.context, endModifier);
        }
        this.frameEndExp = (ArmyExpression) endExp;
        this.frameEndBound = (WindowExpModifier) endModifier;
        return (BR) this;
    }

    @Override
    public final <T> BR and(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier endModifier) {
        return this.and(funcRef.apply(IntegerType.INSTANCE, value), endModifier);
    }

    @Override
    public final BR and(final RowModifier frameEnd) {
        if (this.frameUnits == null
                || this.betweenExtent != Boolean.TRUE
                || this.frameStartBound == null
                || this.frameEndBound != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (!(frameEnd instanceof WindowRowModifier)) {
            throw CriteriaUtils.errorModifier(this.context, frameEnd);
        }
        this.frameEndExp = null;
        this.frameEndBound = (WindowRowModifier) frameEnd;
        return (BR) this;
    }


    @Override
    public final ArmyWindow endWindowClause() {
        _Assert.nonPrepared(this.prepared);
        this.endPartitionIfNeed(false);
        this.endOrderByClauseIfNeed();

        final FrameUnits units = this.frameUnits;
        final Boolean betweenExtent = this.betweenExtent;
        final FrameBound frameStartBound = this.frameStartBound;
        if (units != null && betweenExtent == null) {
            //dynamic frame clause no space invoking
            assert frameStartBound == null && this.frameEndBound == null;
            this.frameUnits = null;
        } else if (units != null && frameStartBound == null) {
            //dynamic frame clause, space invoking but no between invoking
            String m = String.format("You invoking dynamic %s and space method,but don't invoking between method.",
                    units.name());
            throw ContextStack.criteriaError(this.context, m);
        } else if (units != null && betweenExtent == Boolean.TRUE && this.frameEndBound == null) {
            //dynamic frame clause, space invoking and between invoking but no and invoking
            String m = String.format("You invoking dynamic %s and space method and between method,but don't invoking and method.",
                    units.name());
            throw ContextStack.criteriaError(this.context, m);
        }
        this.prepared = Boolean.TRUE;
        return this;
    }

    @Override
    public final void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
        _Assert.prepared(this.prepared);


        final DialectParser parser;
        parser = context.parser();

        //1.window name
        final String windowName = this.windowName;
        if (windowName != null) {
            sqlBuilder.append(_Constant.SPACE);
            parser.identifier(windowName, sqlBuilder)
                    .append(_Constant.SPACE_AS);
        }// anonymous window

        sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
        //3.reference window name
        final String refWindowName = this.refWindowName;
        if (refWindowName != null) {
            sqlBuilder.append(_Constant.SPACE);
            parser.identifier(refWindowName, sqlBuilder);
        }
        //4.partition_clause
        final List<_Expression> partitionByList = this.partitionByList;
        final int partitionSize;
        if (partitionByList != null && (partitionSize = partitionByList.size()) > 0) {
            sqlBuilder.append(_Constant.SPACE_PARTITION_BY);
            for (int i = 0; i < partitionSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                partitionByList.get(i).appendSql(sqlBuilder, context);
            }
        }
        //5.order_clause
        final List<? extends SortItem> orderByList = this.orderByList();
        final int orderItemSize;
        if ((orderItemSize = orderByList.size()) > 0) {
            sqlBuilder.append(_Constant.SPACE_ORDER_BY);
            for (int i = 0; i < orderItemSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                ((ArmySortItem) orderByList.get(i)).appendSql(sqlBuilder, context);
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
            ArmyExpression frameExp = this.frameStartExp;
            FrameBound frameBound = this.frameStartBound;
            if (frameExp == null) {
                assert frameBound instanceof WindowRowModifier;
                sqlBuilder.append(((WindowRowModifier) frameBound).spaceWords);
            } else {
                assert frameBound instanceof WindowExpModifier;
                frameExp.appendSql(sqlBuilder, context);
                sqlBuilder.append(((WindowExpModifier) frameBound).spaceWords);
            }
            if (betweenExtent) {
                sqlBuilder.append(_Constant.SPACE_AND);
                frameExp = this.frameEndExp;
                frameBound = this.frameEndBound;
                if (frameExp == null) {
                    assert frameBound instanceof WindowRowModifier;
                    sqlBuilder.append(((WindowRowModifier) frameBound).spaceWords);
                } else {
                    assert frameBound instanceof WindowExpModifier;
                    frameExp.appendSql(sqlBuilder, context);
                    sqlBuilder.append(((WindowExpModifier) frameBound).spaceWords);
                }

            }

            if (this instanceof SQLExcludeWindow) {
                final FrameExclusion exclusion = ((SQLExcludeWindow<?, ?, ?, ?, ?, ?, ?, ?>) this).exclusion;
                if (exclusion != null) {
                    sqlBuilder.append(exclusion.spaceWords);
                }

            }

        }
        //7.)
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

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
    public final void clear() {
        this.partitionByList = null;
        this.frameUnits = null;
        this.betweenExtent = null;
        this.frameStartExp = null;

        this.frameStartBound = null;
        this.frameEndExp = null;
        this.frameEndBound = null;
        this.prepared = false;
    }

    /*################################## blow package method ##################################*/


    private R ifFrame(final FrameUnits units, final Consumer<DC> consumer) {
        if (this.frameUnits != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.frameUnits = units;
        consumer.accept((DC) this);
        return (R) this;
    }

    /**
     * @see #rows(RowModifier)
     * @see #range(RowModifier)
     * @see #groups(RowModifier)
     */
    private FS startExtent(final FrameUnits units, final RowModifier modifier) {
        if (this.frameUnits != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (!(modifier instanceof WindowRowModifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        this.frameUnits = units;
        this.frameStartBound = (WindowRowModifier) modifier;
        this.betweenExtent = Boolean.FALSE;
        return (FS) this;
    }

    /**
     * @see #rows(Expression, ExpModifier)
     * @see #range(Expression, ExpModifier)
     * @see #groups(Expression, ExpModifier)
     */
    private FS startExtent(final FrameUnits units, final Expression exp, final ExpModifier modifier) {
        if (this.frameUnits != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (!(exp instanceof ArmyExpression)) {
            throw ContextStack.nonArmyExp(this.context);
        } else if (!(modifier instanceof WindowExpModifier)) {
            throw CriteriaUtils.errorModifier(this.context, modifier);
        }
        this.frameUnits = units;
        this.frameStartExp = (ArmyExpression) exp;
        this.frameStartBound = (WindowExpModifier) modifier;
        this.betweenExtent = Boolean.FALSE;
        return (FS) this;
    }

    /**
     * @see #rows()
     * @see #range()
     * @see #groups()
     */
    private FB betweenExtent(final FrameUnits units) {
        if (this.frameUnits != null) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        this.frameUnits = units;
        this.betweenExtent = Boolean.TRUE;
        return (FB) this;
    }


    private void addPartitionExp(final @Nullable Expression expression) {
        if (expression == null) {
            throw ContextStack.nullPointer(this.context);
        } else if (!(expression instanceof ArmyExpression)) {
            throw ContextStack.nonArmyExp(this.context);
        }
        List<_Expression> partitionByList = this.partitionByList;
        if (partitionByList == null) {
            this.partitionByList = partitionByList = _Collections.arrayList();
        } else if (!(partitionByList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        partitionByList.add((ArmyExpression) expression);
    }


    /**
     * @see #endWindowClause()
     */
    private PR endPartitionIfNeed(final boolean required) {
        final List<_Expression> partitionByList = this.partitionByList;
        if (partitionByList instanceof ArrayList) {
            this.partitionByList = _Collections.unmodifiableList(partitionByList);
        } else if (partitionByList == null) {
            if (required) {
                throw ContextStack.criteriaError(this.context, "partition by claus is empty.");
            }
            this.partitionByList = _Collections.emptyList();
        }
        return (PR) this;
    }


    @Override
    final Dialect statementDialect() {
        throw ContextStack.castCriteriaApi(this.context);
    }


    private static CriteriaException refWindowNotExists(CriteriaContext context, String existingWindowName) {
        String m = String.format("reference window[%s] not exists.", existingWindowName);
        return ContextStack.criteriaError(context, m);
    }


    private enum FrameUnits {

        ROWS(" ROWS"),
        RANGE(" RANGE"),
        GROUPS(" GROUPS");

        private final String spaceWord;

        FrameUnits(String spaceWord) {
            this.spaceWord = spaceWord;
        }


    }// FrameUnits

    private interface FrameBound {


    }

    enum WindowRowModifier implements Window.RowModifier, FrameBound {

        UNBOUNDED_PRECEDING(" UNBOUNDED PRECEDING"),
        CURRENT_ROW(" CURRENT ROW"),

        UNBOUNDED_FOLLOWING(" UNBOUNDED FOLLOWING");

        private final String spaceWords;

        WindowRowModifier(String spaceWords) {
            this.spaceWords = spaceWords;
        }


        @Override
        public final String toString() {
            return SQLs.sqlKeyWordsToString(this);
        }

    }//WindowRowModifier


    enum WindowExpModifier implements ExpModifier, FrameBound {

        PRECEDING(" PRECEDING"),

        FOLLOWING(" FOLLOWING");

        private final String spaceWords;

        WindowExpModifier(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String toString() {
            return SQLs.sqlKeyWordsToString(this);
        }

    }//WindowExpModifier


    private enum FrameExclusion {

        EXCLUDE_CURRENT_ROW(" EXCLUDE CURRENT ROW"),
        EXCLUDE_GROUP(" EXCLUDE GROUP"),
        EXCLUDE_TIES(" EXCLUDE TIES"),
        EXCLUDE_NO_OTHERS(" EXCLUDE NO OTHERS");

        private final String spaceWords;

        FrameExclusion(String spaceWords) {
            this.spaceWords = spaceWords;
        }

    }//FrameExclusion

    static abstract class SQLExcludeWindow<PR, OR, OD, FS, FB, BR, DC, R> extends SQLWindow<PR, OR, OD, FS, FB, BR, DC, R>
            implements Window._FrameExclusionClause<R> {

        private FrameExclusion exclusion;

        SQLExcludeWindow(String windowName, CriteriaContext context, @Nullable String existingWindowName) {
            super(windowName, context, existingWindowName);
        }

        SQLExcludeWindow(CriteriaContext context, @Nullable String existingWindowName) {
            super(context, existingWindowName);
        }


        @Override
        public final R excludeCurrentRow() {
            return this.exclusionOption(FrameExclusion.EXCLUDE_CURRENT_ROW);
        }

        @Override
        public final R excludeGroup() {
            return this.exclusionOption(FrameExclusion.EXCLUDE_GROUP);
        }

        @Override
        public final R excludeTies() {
            return this.exclusionOption(FrameExclusion.EXCLUDE_TIES);
        }

        @Override
        public final R excludeNoOthers() {
            return this.exclusionOption(FrameExclusion.EXCLUDE_NO_OTHERS);
        }

        @Override
        public final R ifExcludeCurrentRow(BooleanSupplier predicate) {
            return this.ifExclusionOption(FrameExclusion.EXCLUDE_CURRENT_ROW, predicate);
        }

        @Override
        public final R ifExcludeGroup(BooleanSupplier predicate) {
            return this.ifExclusionOption(FrameExclusion.EXCLUDE_GROUP, predicate);
        }

        @Override
        public final R ifExcludeTies(BooleanSupplier predicate) {
            return this.ifExclusionOption(FrameExclusion.EXCLUDE_TIES, predicate);
        }

        @Override
        public final R ifExcludeNoOthers(BooleanSupplier predicate) {
            return this.ifExclusionOption(FrameExclusion.EXCLUDE_NO_OTHERS, predicate);
        }

        private R exclusionOption(final FrameExclusion exclusion) {
            if (this.exclusion != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.exclusion = exclusion;
            return (R) this;
        }


        private R ifExclusionOption(final FrameExclusion exclusion, final BooleanSupplier predicate) {
            if (this.exclusion != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if (predicate.getAsBoolean()) {
                this.exclusion = exclusion;
            }
            return (R) this;
        }


    }//SQLExcludeWindow


    static final class SimpleWindow implements ArmyWindow {

        private final String windowName;

        private final String refWindowName;

        private SimpleWindow(String windowName) {
            this.windowName = windowName;
            this.refWindowName = null;
        }

        private SimpleWindow(String windowName, String refWindowName) {
            this.windowName = windowName;
            this.refWindowName = refWindowName;
        }

        @Override
        public String windowName() {
            return this.windowName;
        }

        @Override
        public ArmyWindow endWindowClause() {
            //no-op
            return this;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {

            sqlBuilder.append(_Constant.SPACE);

            context.identifier(this.windowName, sqlBuilder)
                    .append(_Constant.SPACE_AS);

            final String refWindowName = this.refWindowName;
            if (refWindowName == null) {
                sqlBuilder.append(_Constant.PARENS);
            } else {
                sqlBuilder.append(_Constant.LEFT_PAREN)
                        .append(_Constant.SPACE);
                context.identifier(refWindowName, sqlBuilder)
                        .append(_Constant.SPACE_RIGHT_PAREN);
            }

        }


        @Override
        public void prepared() {
            //no-op
        }

        @Override
        public void clear() {
            //no-op
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.windowName, this.refWindowName);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof SQLWindow.SimpleWindow) {
                final SimpleWindow o = (SimpleWindow) obj;
                match = o.windowName.equals(this.windowName)
                        && Objects.equals(o.refWindowName, this.refWindowName);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final StringBuilder builder;
            builder = new StringBuilder()
                    .append(_Constant.SPACE)
                    .append(this.windowName)
                    .append(_Constant.SPACE_AS);

            if (this.refWindowName == null) {
                builder.append(_Constant.PARENS);
            } else {
                builder.append(_Constant.LEFT_PAREN)
                        .append(_Constant.SPACE)
                        .append(this.refWindowName)
                        .append(_Constant.SPACE_RIGHT_PAREN);
            }
            return builder.toString();
        }


    }//SimpleWindowSpec


}
