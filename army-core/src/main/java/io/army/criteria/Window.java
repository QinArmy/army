package io.army.criteria;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing window that is defined by application developer in query statement.
 * </p>
 *
 * @since 1.0
 */
public interface Window {


    /**
     * <p>
     * This interface representing WINDOW clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     * @param <WR> next clause java type
     * @since 1.0
     */
    interface _WindowClause<WR> {

        WR window(String windowName);

    }

    /**
     * <p>
     * This interface representing AS clause in WINDOW clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <AR> next clause java type
     * @since 1.0
     */
    interface _AsClause<AR> {

        AR as();
    }


    /**
     * <p>
     * This interface representing LEFT BRACKET clause in WINDOW clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  java criteria object java type
     * @param <NR> next clause java type
     * @since 1.0
     */
    interface _LeftBracketClause<C, NR> {

        NR leftBracket();

        NR leftBracket(String existingWindowName);

        NR leftBracket(Supplier<String> supplier);

        NR leftBracket(Function<C, String> function);

        NR leftBracketIf(Supplier<String> supplier);

        NR leftBracketIf(Function<C, String> function);

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
     * @param <C>  java criteria object java type
     * @param <PR> next clause java type
     * @since 1.0
     */
    interface _PartitionByExpClause<C, PR> {

        PR partitionBy(Object exp);

        PR partitionBy(Object exp1, Object exp2);

        PR partitionBy(Object exp1, Object exp2, Object exp3);

        <E extends Expression> PR partitionBy(Function<C, List<E>> function);

        <E extends Expression> PR partitionBy(Supplier<List<E>> supplier);

        PR partitionBy(Consumer<List<Expression>> consumer);

        <E extends Expression> PR ifPartitionBy(Supplier<List<E>> supplier);

        <E extends Expression> PR ifPartitionBy(Function<C, List<E>> function);

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
     * @param <FR> next clause java type
     * @param <FC> next clause java type
     */
    interface _FrameUnitsClause<C, FR, FC> {

        FR rows();

        FR range();

        FR ifRows(Predicate<C> predicate);

        FR ifRange(Predicate<C> predicate);

        FC rows(Object expression);

        FC range(Object expression);

        FC rowsExp(Supplier<?> supplier);

        FC rowsExp(Function<C, ?> function);

        FC rowsExp(Function<String, ?> function, String keyName);

        FC rangeExp(Supplier<?> supplier);

        FC rangeExp(Function<C, ?> function);

        FC rangeExp(Function<String, ?> function, String keyName);

        FC ifRows(Supplier<?> supplier);

        FC ifRows(Function<C, ?> supplier);

        FC ifRange(Supplier<?> supplier);

        FC ifRange(Function<C, ?> supplier);


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
     * @param <BR> next clause java type
     * @param <BC> next clause java type
     */
    interface _FrameBetweenClause<C, BR, BC> {

        BR between();

        BC between(Object expression);

        BC betweenExp(Supplier<?> supplier);

        BC betweenExp(Function<C, ?> function);

        BC betweenExp(Function<String, ?> function, String keyName);

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
     * @param <C>  java criteria object java type
     * @param <AR> next clause java type
     * @param <AC> next clause java type
     */
    interface _FrameBetweenAndClause<C, AR, AC> {

        AR and();

        AC and(Object expression);

        AC andExp(Supplier<?> supplier);

        AC andExp(Function<C, ?> function);

        AC andExp(Function<String, ?> function, String keyName);

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
     * @param <BR> next clause java type
     * @since 1.0
     */
    interface _FrameNonExpBoundClause<BR> {

        BR currentRow();

        BR unboundedPreceding();

        BR unboundedFollowing();

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
     * @param <BR> next clause java type
     * @since 1.0
     */
    interface _FrameExpBoundClause<BR> {

        BR preceding();

        BR following();

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
     * @param <C> java criteria object java type
     * @param <R> {@link Statement._RightBracketClause#rightBracket()} return java type
     * @since 1.0
     */
    interface _SimpleAsClause<C, R> extends _AsClause<_SimpleLeftBracketClause<C, R>> {


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
     * @param <C> java criteria object java type
     * @param <R> {@link Statement._RightBracketClause#rightBracket()} return java type
     * @since 1.0
     */
    interface _SimpleLeftBracketClause<C, R>
            extends _LeftBracketClause<C, _SimplePartitionBySpec<C, R>> {

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
     * @param <C> criteria object java type
     * @param <R> {@link Statement._RightBracketClause#rightBracket()} return java type
     * @since 1.0
     */
    interface _SimplePartitionBySpec<C, R> extends _PartitionByExpClause<C, _SimpleOrderBySpec<C, R>>
            , _SimpleOrderBySpec<C, R> {


    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._OrderByClause} clause in WINDOW clause</li>
     *          <li>the composite {@link _SimpleFrameUnitsSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <R> {@link Statement._RightBracketClause#rightBracket()} return java type
     * @since 1.0
     */
    interface _SimpleOrderBySpec<C, R> extends Statement._OrderByClause<C, _SimpleFrameUnitsSpec<C, R>>
            , _SimpleFrameUnitsSpec<C, R> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _FrameUnitsClause}</li>
     *          <li>{@link Statement._RightBracketClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <R> {@link Statement._RightBracketClause#rightBracket()} return java type
     * @since 1.0
     */
    interface _SimpleFrameUnitsSpec<C, R>
            extends _FrameUnitsClause<C, _SimpleFrameBetweenClause<C, R>, _SimpleFrameEndNonExpBoundClause<R>>
            , Statement._RightBracketClause<R> {

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
     * @param <C> java criteria object java type
     * @param <R> {@link Statement._RightBracketClause#rightBracket()} return java typ
     * @since 1.0
     */
    interface _SimpleFrameBetweenClause<C, R>
            extends _FrameBetweenClause<C, _SimpleFrameNonExpBoundClause<C, R>, _SimpleFrameExpBoundClause<C, R>> {

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
     * @param <C> java criteria object java type
     * @param <R> {@link Statement._RightBracketClause#rightBracket()} return java typ
     * @since 1.0
     */
    interface _SimpleFrameBetweenAndClause<C, R>
            extends Statement._Clause
            , _FrameBetweenAndClause<C, _SimpleFrameEndNonExpBoundClause<R>, _SimpleFrameEndExpBoundClause<R>> {

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
     * @param <C> java criteria object java type
     * @param <R> {@link Statement._RightBracketClause#rightBracket()} return java typ
     * @since 1.0
     */
    interface _SimpleFrameNonExpBoundClause<C, R> extends _FrameNonExpBoundClause<Statement._Clause> {

        /**
         * {@inheritDoc}
         */
        @Override
        _SimpleFrameBetweenAndClause<C, R> currentRow();

        /**
         * {@inheritDoc}
         */
        @Override
        _SimpleFrameBetweenAndClause<C, R> unboundedPreceding();

        /**
         * {@inheritDoc}
         */
        @Override
        _SimpleFrameBetweenAndClause<C, R> unboundedFollowing();


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
     * @param <C> java criteria object java type
     * @param <R> {@link Statement._RightBracketClause#rightBracket()} return java typ
     * @since 1.0
     */
    interface _SimpleFrameExpBoundClause<C, R> extends _FrameExpBoundClause<Statement._Clause> {

        /**
         * {@inheritDoc}
         */
        @Override
        _SimpleFrameBetweenAndClause<C, R> preceding();

        /**
         * {@inheritDoc}
         */
        @Override
        _SimpleFrameBetweenAndClause<C, R> following();

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
     * @param <R> {@link Statement._RightBracketClause#rightBracket()} return java typ
     * @since 1.0
     */
    interface _SimpleFrameEndNonExpBoundClause<R> extends _FrameNonExpBoundClause<Statement._Clause> {

        /**
         * {@inheritDoc}
         */
        @Override
        Statement._RightBracketClause<R> currentRow();

        /**
         * {@inheritDoc}
         */
        @Override
        Statement._RightBracketClause<R> unboundedPreceding();

        /**
         * {@inheritDoc}
         */
        @Override
        Statement._RightBracketClause<R> unboundedFollowing();


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
     * @param <R> {@link Statement._RightBracketClause#rightBracket()} return java typ
     * @since 1.0
     */
    interface _SimpleFrameEndExpBoundClause<R> extends _FrameExpBoundClause<Statement._Clause> {

        /**
         * {@inheritDoc}
         */
        @Override
        Statement._RightBracketClause<R> preceding();

        /**
         * {@inheritDoc}
         */
        @Override
        Statement._RightBracketClause<R> following();

    }


}
