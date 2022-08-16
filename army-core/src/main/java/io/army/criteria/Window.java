package io.army.criteria;

import java.util.function.*;

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
     *
     * @param <WR> next clause java type
     * @param <WB> next clause java type
     * @since 1.0
     */
    interface _WindowClause<C, WR, WB> {

        WR window(String windowName);

        WB window(Consumer<Consumer<Window>> consumer);

        WB window(BiConsumer<C, Consumer<Window>> consumer);

        WB ifWindow(Consumer<Consumer<Window>> consumer);

        WB ifWindow(BiConsumer<C, Consumer<Window>> consumer);

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
     * This interface representing comma clause in WINDOW clause.
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
    interface _CommaClause<CR> {

        CR comma(String windowName);
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
     * @param <C>  java criteria object java type
     * @param <NR> next clause java type
     * @since 1.0
     */
    interface _LeftParenNameClause<C, NR> {

        NR leftParen();

        NR leftParen(String existingWindowName);

        NR leftParen(Supplier<String> supplier);

        NR leftParen(Function<C, String> function);

        NR leftParenIf(Supplier<String> supplier);

        NR leftParenIf(Function<C, String> function);

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

        PR partitionBy(Expression exp);

        PR partitionBy(Expression exp1, Expression exp2);

        PR partitionBy(Expression exp1, Expression exp2, Expression exp3);

        <E extends Expression> PR partitionBy(Consumer<Consumer<E>> consumer);

        <E extends Expression> PR partitionBy(BiConsumer<C, Consumer<E>> consumer);

        <E extends Expression> PR ifPartitionBy(Consumer<Consumer<E>> consumer);

        <E extends Expression> PR ifPartitionBy(BiConsumer<C, Consumer<E>> consumer);

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
     * @param <FR> next clause java type,sub interface of {@link _FrameBetweenClause }
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


    interface _OverClause {

        SelectionSpec over(String windowName);
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
     * @param <C> criteria object java type
     * @param <R> {@link Statement._RightParenClause#rightParen()} return java type
     * @since 1.0
     */
    interface _SimpleAsClause<C, R> extends _AsClause<_SimpleLeftParenClause<C, R>> {


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
     * @param <C> criteria object java type
     * @param <R> {@link Statement._RightParenClause#rightParen()} return java type
     * @since 1.0
     */
    interface _SimpleLeftParenClause<C, R>
            extends _LeftParenNameClause<C, _SimplePartitionBySpec<C, R>> {

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
     * @param <R> {@link Statement._RightParenClause#rightParen()} return java type
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
     * @param <C> criteria object java type
     * @param <R> {@link Statement._RightParenClause#rightParen()} return java type
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
     *          <li>{@link Statement._RightParenClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @param <R> {@link Statement._RightParenClause#rightParen()} return java type
     * @since 1.0
     */
    interface _SimpleFrameUnitsSpec<C, R>
            extends _FrameUnitsClause<C, _SimpleFrameBetweenClause<C, R>, _SimpleFrameEndNonExpBoundClause<R>>
            , Statement._RightParenClause<R> {

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
     * @param <C> criteria object java type
     * @param <R> {@link Statement._RightParenClause#rightParen()} return java typ
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
     * @param <C> criteria object java type
     * @param <R> {@link Statement._RightParenClause#rightParen()} return java typ
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
     * @param <C> criteria object java type
     * @param <R> {@link Statement._RightParenClause#rightParen()} return java typ
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
     * @param <C> criteria object java type
     * @param <R> {@link Statement._RightParenClause#rightParen()} return java typ
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
     * @param <R> {@link Statement._RightParenClause#rightParen()} return java typ
     * @since 1.0
     */
    interface _SimpleFrameEndNonExpBoundClause<R> extends _FrameNonExpBoundClause<Statement._Clause> {

        /**
         * {@inheritDoc}
         */
        @Override
        Statement._RightParenClause<R> currentRow();

        /**
         * {@inheritDoc}
         */
        @Override
        Statement._RightParenClause<R> unboundedPreceding();

        /**
         * {@inheritDoc}
         */
        @Override
        Statement._RightParenClause<R> unboundedFollowing();


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
     * @param <R> {@link Statement._RightParenClause#rightParen()} return java typ
     * @since 1.0
     */
    interface _SimpleFrameEndExpBoundClause<R> extends _FrameExpBoundClause<Statement._Clause> {

        /**
         * {@inheritDoc}
         */
        @Override
        Statement._RightParenClause<R> preceding();

        /**
         * {@inheritDoc}
         */
        @Override
        Statement._RightParenClause<R> following();

    }


    interface _SimpleOverLestParenSpec extends Window._SimpleLeftParenClause<Void, SelectionSpec>
            , SelectionSpec {

    }


}
