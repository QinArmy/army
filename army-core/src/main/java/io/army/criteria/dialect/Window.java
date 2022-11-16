package io.army.criteria.dialect;

import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.lang.Nullable;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing window that is defined by application developer in query statement.
 * </p>
 *
 * @since 1.0
 */
public interface Window extends Item {


    interface Builder extends Item {


    }

    /**
     * <p>
     * This interface representing static WINDOW clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <WR> next clause java type
     * @since 1.0
     */
    @Deprecated
    interface _StaticWindowClause<WR> {

        WR window(String windowName);

    }

    /**
     * <p>
     * This interface representing static comma clause in WINDOW clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <CR> next clause java type
     * @since 1.0
     */
    interface _StaticWindowCommaClause<CR> {

        CR comma(String windowName);
    }

    /**
     * <p>
     * This interface representing dynamic WINDOW clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <WB> sub interface of {@link Builder}
     * @param <WR> next clause java type
     * @since 1.0
     */
    interface _DynamicWindowClause<WB extends Builder, WR> {


        WR window(Consumer<WB> consumer);

        WR ifWindow(Consumer<WB> consumer);

    }


    /**
     * <p>
     * This interface representing LEFT PAREN clause in WINDOW clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <NR> next clause java type
     * @since 1.0
     */
    @Deprecated
    interface _LeftParenNameClause<NR> extends Item {

        NR leftParen();

        NR leftParen(String existingWindowName);

        NR leftParen(Supplier<String> supplier);

        NR leftParenIf(Supplier<String> supplier);

    }

    /**
     * <p>
     * This interface representing PARTITION BY clause in WINDOW clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <PR> next clause java type
     * @since 1.0
     */
    interface _PartitionByExpClause<PR> {

        PR partitionBy(Expression exp);

        PR partitionBy(Expression exp1, Expression exp2);

        PR partitionBy(Expression exp1, Expression exp2, Expression exp3);

        PR partitionBy(Consumer<Consumer<Expression>> consumer);

        PR ifPartitionBy(Consumer<Consumer<Expression>> consumer);

    }


    interface _FrameUnitNoExpClause<FB> {

        FB rows();

        FB range();

        FB ifRows(BooleanSupplier predicate);

        FB ifRange(BooleanSupplier predicate);

    }

    /**
     * <p>
     * This interface representing FRAME_UNITS  clause  in FRAME clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <FC> next clause java type
     */
    interface _FrameUnitExpClause<FC> {

        FC rows(Expression expression);

        FC rows(Supplier<Expression> supplier);

        <E> FC rows(Function<E, Expression> valueOperator, E value);

        <E> FC rows(Function<E, Expression> valueOperator, Supplier<E> supplier);

        FC rows(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        FC range(Expression expression);

        FC range(Supplier<Expression> supplier);

        <E> FC range(Function<E, Expression> valueOperator, E value);

        <E> FC range(Function<E, Expression> valueOperator, Supplier<E> supplier);

        FC range(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        FC ifRows(Supplier<Expression> supplier);

        <E> FC ifRows(Function<E, Expression> valueOperator, @Nullable E value);

        <E> FC ifRows(Function<E, Expression> valueOperator, Supplier<E> supplier);

        FC ifRows(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        FC ifRange(Supplier<Expression> supplier);

        <E> FC ifRange(Function<E, Expression> valueOperator, @Nullable E value);

        <E> FC ifRange(Function<E, Expression> valueOperator, Supplier<E> supplier);

        FC ifRange(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);


    }


    /**
     * <p>
     * This interface representing BETWEEN  clause  in FRAME clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <BE> next clause java type
     */
    interface _FrameBetweenExpClause<BE> extends Item {

        BE between(Expression expression);

        BE between(Supplier<Expression> supplier);

        <E> BE between(Function<E, Expression> valueOperator, E value);

        <E> BE between(Function<E, Expression> valueOperator, Supplier<E> supplier);

        BE between(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

    }


    /**
     * <p>
     * This interface representing AND clause  in FRAME clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <AE> next clause java type
     */
    interface _FrameBetweenAndExpClause<AE> extends Item {

        AE and(Expression expression);

        AE and(Supplier<Expression> supplier);

        <E> AE and(Function<E, Expression> valueOperator, E value);

        <E> AE and(Function<E, Expression> valueOperator, Supplier<E> supplier);

        AE and(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

    }

    /**
     * <p>
     * This interface representing FRAME_START or FRAME_END  clause  in FRAME clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _FrameNonExpBoundClause {

        Item currentRow();

        Item unboundedPreceding();

        Item unboundedFollowing();

    }

    /**
     * <p>
     * This interface representing FRAME_START or FRAME_END  clause  in FRAME clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _FrameExpBoundClause {

        Item preceding();

        Item following();

    }


    interface _OverClause {

        Expression over(String windowName);

        Expression over();

    }


    @Deprecated
    interface _OverWindowClause<E extends Expression> extends _OverClause {

        @Override
        E over(String windowName);

        @Override
        E over();
    }


    /**
     * <p>
     * This interface representing FRAME_START or FRAME_END  clause  in FRAME clause for simple window.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _SimpleFrameEndExpBoundClause extends _FrameExpBoundClause {


    }

    /**
     * <p>
     * This interface representing FRAME_START or FRAME_END  clause  in FRAME clause for simple window.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _SimpleFrameEndNonExpBoundClause extends _FrameNonExpBoundClause {

    }

    /**
     * <p>
     * This interface representing FRAME_START or FRAME_END  clause  in FRAME clause for simple window.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _SimpleFrameExpBoundClause extends _FrameExpBoundClause {


        @Override
        _SimpleFrameBetweenAndClause preceding();

        @Override
        _SimpleFrameBetweenAndClause following();


    }

    /**
     * <p>
     * This interface representing FRAME_START or FRAME_END  clause  in FRAME clause for simple window.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _SimpleFrameNonExpBoundClause
            extends _FrameNonExpBoundClause {

        @Override
        _SimpleFrameBetweenAndClause currentRow();

        @Override
        _SimpleFrameBetweenAndClause unboundedPreceding();

        @Override
        _SimpleFrameBetweenAndClause unboundedFollowing();

    }

    /**
     * <p>
     * This interface representing AND clause  in FRAME clause for simple window.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _SimpleFrameBetweenAndClause
            extends _FrameBetweenAndExpClause<_SimpleFrameEndExpBoundClause>
            , Statement._StaticAndClause<_SimpleFrameEndNonExpBoundClause> {

    }

    /**
     * <p>
     * This interface representing BETWEEN clause  in FRAME clause for simple window.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _SimpleFrameBetweenSpec
            extends _FrameBetweenExpClause<_SimpleFrameExpBoundClause>
            , Statement._StaticBetweenClause<_SimpleFrameNonExpBoundClause>
            , _SimpleFrameNonExpBoundClause {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _FrameUnitExpClause}</li>
     *          <li>{@link Statement._RightParenClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _SimpleFrameUnitsSpec
            extends _FrameUnitExpClause<_SimpleFrameEndExpBoundClause>
            , _FrameUnitNoExpClause<_SimpleFrameBetweenSpec> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._StaticOrderByClause} clause in WINDOW clause</li>
     *          <li>the composite {@link _SimpleFrameUnitsSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _SimpleOrderBySpec extends Statement._StaticOrderByClause<_SimpleFrameUnitsSpec>
            , _SimpleFrameUnitsSpec {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _PartitionByExpClause} in WINDOW clause</li>
     *          <li>the composite {@link _SimpleOrderBySpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _SimplePartitionBySpec extends _PartitionByExpClause<_SimpleOrderBySpec>
            , _SimpleOrderBySpec {

    }

    /**
     * <p>
     * This interface representing LEFT BRACKET clause in WINDOW clause for simple window.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <I> {@link Statement._RightParenClause#rightParen()} return java type
     * @since 1.0
     */
    @Deprecated
    interface _SimpleLeftParenClause<I extends Item> extends _LeftParenNameClause<_SimplePartitionBySpec> {

    }


    /**
     * <p>
     * This interface representing AS clause in WINDOW clause for simple window.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <I> {@link Statement._RightParenClause#rightParen()} return java type
     * @since 1.0
     */
    @Deprecated
    interface _SimpleAsClause<I extends Item> extends Statement._StaticAsClaus<_SimpleLeftParenClause<I>> {


    }


}
