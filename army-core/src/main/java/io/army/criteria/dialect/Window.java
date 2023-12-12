package io.army.criteria.dialect;

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.SimpleExpression;
import io.army.criteria.Statement;
import io.army.criteria.impl.SQLs;

import javax.annotation.Nullable;

import io.army.mapping.IntegerType;

import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * <p>
 * This interface representing window that is defined by application developer in query statement.
 * * @since 1.0
 */
public interface Window extends Item {


    interface _WindowAsClause<T extends Item, R extends Item> extends Item {

        R as();

        R as(@Nullable String existingWindowName);

        R as(Consumer<T> consumer);

        R as(@Nullable String existingWindowName, Consumer<T> consumer);

    }


    @FunctionalInterface
    interface Builder<T extends Item> extends Item {

        _WindowAsClause<T, Item> window(String windowName);

    }


    /**
     * <p>
     * This interface representing static comma clause in WINDOW clause.
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @param <CR> next clause java type
     * @since 1.0
     */
    interface _StaticWindowCommaClause<CR> {

        CR comma(String windowName);
    }


    /**
     * <p>
     * This interface representing dynamic WINDOW clause.
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @param <T> sub interface of {@link Builder}
     * @param <R> next clause java type
     * @since 1.0
     */
    interface _DynamicWindowClause<T extends Item, R extends Item> {


        R windows(Consumer<Builder<T>> consumer);

        R ifWindows(Consumer<Builder<T>> consumer);

    }


    interface _WindowSpec {

    }

    interface _PartitionByCommaClause<R> {

        R comma(Expression exp);

        R comma(Expression exp1, Expression exp2);

        R comma(Expression exp1, Expression exp2, Expression exp3);

        R comma(Expression exp1, Expression exp2, Expression exp3, Expression exp4);

    }

    /**
     * <p>
     * This interface representing PARTITION BY clause in WINDOW clause.
     *     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     *     *
     * @param <R> next clause java type
     * @since 1.0
     */
    interface _PartitionByExpClause<R> extends _WindowSpec {

        R partitionBy(Expression exp);

        R partitionBy(Expression exp1, Expression exp2);

        R partitionBy(Expression exp1, Expression exp2, Expression exp3);

        R partitionBy(Expression exp1, Expression exp2, Expression exp3, Expression exp4);

        R partitionBy(Consumer<Consumer<Expression>> consumer);

        R ifPartitionBy(Consumer<Consumer<Expression>> consumer);

    }


    /**
     * <p>
     *     <ul>
     *         <li>UNBOUNDED PRECEDING</li>
     *         <li>CURRENT ROW</li>
     *         <li>UNBOUNDED FOLLOWING</li>
     *     </ul>
     *     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface RowModifier {

    }

    /**
     * <p>
     *     <ul>
     *         <li>PRECEDING</li>
     *         <li>FOLLOWING</li>
     *     </ul>
     *     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface ExpModifier {

    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _StaticFrameUnitRowsClause<RS, RB> {

        RS rows(RowModifier modifier);

        RS rows(Expression exp, ExpModifier modifier);

        <T> RS rows(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier modifier);

        RB rows();


    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _StaticFrameUnitRangeClause<RS, RB> {

        RS range(RowModifier modifier);

        RS range(Expression exp, ExpModifier modifier);

        <T> RS range(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier modifier);

        RB range();


    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _StaticFrameUnitRowsRangeSpec<RS, RB> extends _StaticFrameUnitRowsClause<RS, RB>,
            _StaticFrameUnitRangeClause<RS, RB> {

    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _StaticFrameUnitGroupsClause<RS, RB> {

        RS groups(RowModifier modifier);

        RS groups(Expression exp, ExpModifier modifier);

        <T> RS groups(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier modifier);

        RB groups();


    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _StaticFrameUnitRowsRangeGroupsSpec<RS, RB> extends _StaticFrameUnitRowsRangeSpec<RS, RB>,
            _StaticFrameUnitGroupsClause<RS, RB> {

    }

    interface _FrameUnitSpaceClause<RS, RB> {

        RS space(RowModifier modifier);

        RS space(Expression exp, ExpModifier modifier);

        <T> RS space(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier modifier);

        RB space();
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _DynamicFrameUnitRowsClause<T, R> {


        R ifRows(Consumer<T> consumer);

    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _DynamicFrameUnitRangeClause<T, R> {


        R ifRange(Consumer<T> consumer);

    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _DynamicFrameUnitRowsRangeClause<T, R> extends _DynamicFrameUnitRowsClause<T, R>,
            _DynamicFrameUnitRangeClause<T, R> {


    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _DynamicFrameUnitGroupsClause<T, R> {


        R ifGroups(Consumer<T> consumer);

    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _DynamicFrameUnitRowsRangeGroupsClause<T, R> extends _DynamicFrameUnitRowsRangeClause<T, R>,
            _DynamicFrameUnitGroupsClause<T, R> {


    }


    interface _FrameBetweenAndClause<R> {

        R and(Expression endExp, ExpModifier endModifier);

        <T> R and(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier endModifier);

        R and(RowModifier frameEnd);

    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-frames.html">MySQL Window Function Frame Specification</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _FrameBetweenClause<R> {

        R between(RowModifier frameStart, SQLs.WordAnd and, RowModifier frameEnd);

        R between(Expression startExp, ExpModifier startModifier, SQLs.WordAnd and, Expression endExp, ExpModifier endModifier);

        R between(RowModifier frameStart, SQLs.WordAnd and, Expression endExp, ExpModifier endModifier);

        R between(Expression startExp, ExpModifier startModifier, SQLs.WordAnd and, RowModifier frameEnd);

        _FrameBetweenAndClause<R> between(Expression startExp, ExpModifier startModifier);

        <T> R between(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier startModifier, SQLs.WordAnd and, RowModifier frameEnd);

        <T> R between(RowModifier frameStart, SQLs.WordAnd and, BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier endModifier);

        <T> R between(BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier startModifier, SQLs.WordAnd and, Expression endExp, ExpModifier endModifier);

        <T> R between(Expression startExp, ExpModifier startModifier, SQLs.WordAnd and, BiFunction<IntegerType, T, Expression> funcRef, T value, ExpModifier endModifier);

        <T, U> R between(BiFunction<IntegerType, T, Expression> funcRefForStart, T startValue, ExpModifier startModifier, SQLs.WordAnd and, BiFunction<IntegerType, U, Expression> funcRefForEnd, U endValue, ExpModifier endModifier);

    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Postgre Window Function Calls</a>
     */
    interface _FrameExclusionClause<R> {

        R excludeCurrentRow();

        R excludeGroup();

        R excludeTies();

        R excludeNoOthers();

        R ifExcludeCurrentRow(BooleanSupplier predicate);

        R ifExcludeGroup(BooleanSupplier predicate);

        R ifExcludeTies(BooleanSupplier predicate);

        R ifExcludeNoOthers(BooleanSupplier predicate);
    }


    /*-------------------below standard window -------------------*/

    interface _StandardFrameBetweenClause extends _FrameBetweenClause<Item> {

    }

    interface _StandardFrameUnitSpaceSpec extends _FrameUnitSpaceClause<Item, _StandardFrameBetweenClause> {

    }

    interface _StandardFrameExtentSpec extends _StaticFrameUnitRowsRangeSpec<Item, _StandardFrameBetweenClause>,
            _DynamicFrameUnitRowsRangeClause<_StandardFrameUnitSpaceSpec, Item> {

    }

    interface _StandardOrderByCommaSpec extends Statement._OrderByCommaClause<_StandardOrderByCommaSpec>,
            _StandardFrameExtentSpec {

    }

    interface _StandardOrderBySpec extends Statement._StaticOrderByClause<_StandardOrderByCommaSpec>,
            Statement._DynamicOrderByClause<_StandardFrameExtentSpec>,
            _StandardFrameExtentSpec {

    }

    interface _StandardPartitionByCommaSpec extends _PartitionByCommaClause<_StandardPartitionByCommaSpec>,
            _StandardOrderBySpec {

    }

    interface _StandardPartitionBySpec extends _PartitionByExpClause<_StandardPartitionByCommaSpec>,
            _StandardOrderBySpec {

    }


    /*-------------------below over clause -------------------*/

    interface _OverWindowClause<T extends _WindowSpec> extends Item {

        SimpleExpression over();

        SimpleExpression over(@Nullable String existingWindowName);

        SimpleExpression over(Consumer<T> consumer);

        SimpleExpression over(@Nullable String existingWindowName, Consumer<T> consumer);

    }


}
