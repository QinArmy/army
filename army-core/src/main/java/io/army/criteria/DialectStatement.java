package io.army.criteria;

import io.army.criteria.impl.inner._Cte;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface DialectStatement extends Statement {

    interface _StaticReturningCommaUnaryClause<Q extends Item>
            extends DqlInsert._DqlInsertSpec<Q> {

        _StaticReturningCommaUnaryClause<Q> comma(Selection selection);

    }


    interface _StaticReturningCommaDualClause<Q extends Item>
            extends DqlInsert._DqlInsertSpec<Q> {

        DqlInsert._DqlInsertSpec<Q> comma(Selection selectItem);

        _StaticReturningCommaDualClause<Q> comma(Selection selectItem1, Selection selectItem2);

    }


    interface _StaticReturningClause<I extends Item, Q extends Item> extends _DmlInsertSpec<I> {

        _StaticReturningCommaUnaryClause<Q> returning(Selection selectItem);

        _StaticReturningCommaDualClause<Q> returning(Selection selectItem1, Selection selectItem2);
    }


    interface _DynamicReturningClause<C, I extends Item, Q extends Item> extends _DmlInsertSpec<I> {

        DqlInsert._DqlInsertSpec<Q> returningAll();

        DqlInsert._DqlInsertSpec<Q> returning(Consumer<Consumer<Selection>> consumer);

        DqlInsert._DqlInsertSpec<Q> returning(BiConsumer<C, Consumer<Selection>> consumer);

    }


    interface _DynamicWithCteClause<C, B extends CteBuilderSpec, WE> {
        WE with(Consumer<B> consumer);

        WE with(BiConsumer<C, B> consumer);

        WE withRecursive(Consumer<B> consumer);

        WE withRecursive(BiConsumer<C, B> consumer);

        WE ifWith(Consumer<B> consumer);

        WE ifWith(BiConsumer<C, B> consumer);

        WE ifWithRecursive(Consumer<B> consumer);

        WE ifWithRecursive(BiConsumer<C, B> consumer);

    }

    interface _StaticWithCteClause<WS> {

        WS with(String name);

        WS withRecursive(String name);

    }


    interface _StaticWithCommaClause<CR> {

        CR comma(String name);
    }

    interface _StaticSpaceClause<SR> {

        SR space();
    }


    /**
     * <p>
     * This interface representing WITH clause(Common Table Expressions).
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  criteria object java type
     * @param <WE> next clause java type
     */
    interface _WithCteClause2<C, SS extends SubStatement, WE> {

        WE with(String cteName, Supplier<? extends SS> supplier);

        WE with(String cteName, Function<C, ? extends SS> function);

        WE with(Consumer<Consumer<_Cte>> consumer);

        WE with(BiConsumer<C, Consumer<_Cte>> consumer);

        WE ifWith(Consumer<Consumer<_Cte>> consumer);

        WE ifWith(BiConsumer<C, Consumer<_Cte>> consumer);

        WE withRecursive(String cteName, Supplier<? extends SS> supplier);

        WE withRecursive(String cteName, Function<C, ? extends SS> function);

        WE withRecursive(Consumer<Consumer<_Cte>> consumer);

        WE withRecursive(BiConsumer<C, Consumer<_Cte>> consumer);

        WE ifWithRecursive(Consumer<Consumer<_Cte>> consumer);

        WE ifWithRecursive(BiConsumer<C, Consumer<_Cte>> consumer);


    }




    interface _FromLateralClause<C, FS> {

        <T extends TabularItem> FS fromLateral(Supplier<T> supplier, String alias);

        <T extends TabularItem> FS fromLateral(Function<C, T> function, String alias);

    }

    /**
     * <p>
     * This interface representing STRAIGHT JOIN clause
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  criteria object java type
     * @param <JT> same with JT with the JT of {@link _JoinClause}
     * @param <JS> same with JT with the JS of {@link _JoinClause}
     * @see _CrossJoinClause
     * @since 1.0
     */
    interface _StraightJoinClause<C, JT, JS> {

        JT straightJoin(TableMeta<?> table, String tableAlias);

        <T extends TabularItem> JS straightJoin(Function<C, T> function, String alias);

        <T extends TabularItem> JS straightJoin(Supplier<T> supplier, String alias);


    }


    interface _JoinLateralClause<C, JS> {

        <T extends TabularItem> JS leftJoinLateral(Supplier<T> supplier, String alias);

        <T extends TabularItem> JS leftJoinLateral(Function<C, T> function, String alias);

        <T extends TabularItem> JS joinLateral(Supplier<T> supplier, String alias);

        <T extends TabularItem> JS joinLateral(Function<C, T> function, String alias);

        <T extends TabularItem> JS rightJoinLateral(Supplier<T> supplier, String alias);

        <T extends TabularItem> JS rightJoinLateral(Function<C, T> function, String alias);

        <T extends TabularItem> JS fullJoinLateral(Supplier<T> supplier, String alias);

        <T extends TabularItem> JS fullJoinLateral(Function<C, T> function, String alias);


    }

    interface _CrossJoinLateralClause<C, FS> {

        <T extends TabularItem> FS crossJoinLateral(Supplier<T> supplier, String alias);

        <T extends TabularItem> FS crossJoinLateral(Function<C, T> function, String alias);

    }


    interface _StraightJoinLateralClause<C, JS> {

        <T extends TabularItem> JS straightJoinLateral(Supplier<T> supplier, String alias);

        <T extends TabularItem> JS straightJoinLateral(Function<C, T> function, String alias);


    }


    /**
     * <p>
     * This interface representing dialect join clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <JP> next clause java type
     * @since 1.0
     */
    interface _DialectJoinClause<JP> {

        JP leftJoin(TableMeta<?> table);

        JP join(TableMeta<?> table);

        JP rightJoin(TableMeta<?> table);

        JP fullJoin(TableMeta<?> table);

    }

    /**
     * <p>
     * This interface representing dialect STRAIGHT JOIN clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <JP> same with the JP of {@link _DialectJoinClause}
     * @see _DialectJoinClause
     * @since 1.0
     */
    interface _DialectStraightJoinClause<JP> {

        JP straightJoin(TableMeta<?> table);

    }

    interface _IfStraightJoinClause<C, FJ> {

        <B extends JoinItemBlock<C>> FJ ifStraightJoin(Supplier<B> supplier);

        <B extends JoinItemBlock<C>> FJ ifStraightJoin(Function<C, B> function);

    }


    /**
     * <p>
     * This interface representing dialect CROSS JOIN clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <FP> same with the FP of {@link _DialectFromClause}
     * @see _DialectFromClause
     * @since 1.0
     */
    interface _DialectCrossJoinClause<FP> {

        FP crossJoin(TableMeta<?> table);


    }

    /**
     * <p>
     * This interface representing JOIN CTE clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <JS> same with the JS of {@link _JoinClause}
     * @since 1.0
     */
    interface _JoinCteClause<JS> {

        JS leftJoin(String cteName);

        JS leftJoin(String cteName, String alias);

        JS join(String cteName);

        JS join(String cteName, String alias);

        JS rightJoin(String cteName);

        JS rightJoin(String cteName, String alias);

        JS fullJoin(String cteName);

        JS fullJoin(String cteName, String alias);

    }

    interface _CrossJoinCteClause<FS> {

        FS crossJoin(String cteName);

        FS crossJoin(String cteName, String alias);

    }


    /**
     * <p>
     * This interface representing STRAIGHT JOIN CTE clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <JS> same with the JS of {@link _JoinClause}
     * @since 1.0
     */
    interface _StraightJoinCteClause<JS> {

        JS straightJoin(String cteName);

        JS straightJoin(String cteName, String alias);

    }


    /**
     * <p>
     * This interface representing dialect left bracket clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <LP> next clause java type
     * @since 1.0
     */
    interface _DialectLeftParenClause<LP> {

        LP leftParen(TableMeta<?> table);
    }

    /**
     * <p>
     * This interface representing  left bracket cte clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <LS> same with the LS of {@link _LeftParenClause}
     * @since 1.0
     */
    interface _LeftParenCteClause<LS> {

        LS leftParen(String cteName);

        LS leftParen(String cteName, String alias);
    }

    interface _LeftParenLateralClause<C, LS> {

        <T extends TabularItem> LS leftParenLateral(Supplier<T> supplier, String alias);

        <T extends TabularItem> LS leftParenLateral(Function<C, T> function, String alias);

    }

    /**
     * <p>
     * This interface representing dialect union clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C>  criteria object java type
     * @param <UR> next clause java type
     * @param <SP> next clause java type
     * @since 1.0
     */
    interface _DialectUnionClause<C, UR, SP> extends Query._QueryUnionClause<C, UR, SP> {

    }


    /**
     * <p>
     * This interface representing FROM clause for single-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>/
     *
     * @param <DT> next clause java type
     * @since 1.0
     */
    interface _SingleDeleteFromClause<DT> {

        DT from(SingleTableMeta<?> table, String alias);

    }




}
