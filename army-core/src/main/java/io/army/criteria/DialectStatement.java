package io.army.criteria;

import io.army.criteria.impl.SQLs;
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


    interface _DynamicReturningClause<I extends Item, Q extends Item> extends _DmlInsertSpec<I> {

        DqlInsert._DqlInsertSpec<Q> returningAll();

        DqlInsert._DqlInsertSpec<Q> returning(Consumer<Consumer<Selection>> consumer);


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
     * @param <JT> same with JT with the JT of {@link _JoinClause}
     * @param <JS> same with JT with the JS of {@link _JoinClause}
     * @see _CrossJoinClause
     * @since 1.0
     */
    interface _StraightJoinClause<JT, JS> {

        JT straightJoin(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        <T extends TabularItem> _AsClause<JS> straightJoin(Supplier<T> supplier);


    }

    interface _StraightJoinModifierClause<JT, JS> extends _StraightJoinClause<JT, JS> {

        JT straightJoin(Query.TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        <T extends TabularItem> _AsClause<JS> straightJoin(Query.TabularModifier modifier, Supplier<T> supplier);


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
     * @param <JC> same with the JS of {@link _JoinClause}
     * @since 1.0
     */
    interface _JoinCteClause<JC> {

        JC leftJoin(String cteName);

        JC leftJoin(String cteName, SQLs.WordAs wordAs, String alias);

        JC join(String cteName);

        JC join(String cteName, SQLs.WordAs wordAs, String alias);

        JC rightJoin(String cteName);

        JC rightJoin(String cteName, SQLs.WordAs wordAs, String alias);

        JC fullJoin(String cteName);

        JC fullJoin(String cteName, SQLs.WordAs wordAs, String alias);

    }

    interface _JoinModifierCteClause<JC> extends _JoinCteClause<JC> {

        JC leftJoin(Query.TabularModifier modifier, String cteName);

        JC leftJoin(Query.TabularModifier modifier, String cteName, SQLs.WordAs wordAs, String alias);

        JC join(Query.TabularModifier modifier, String cteName);

        JC join(Query.TabularModifier modifier, String cteName, SQLs.WordAs wordAs, String alias);

        JC rightJoin(Query.TabularModifier modifier, String cteName);

        JC rightJoin(Query.TabularModifier modifier, String cteName, SQLs.WordAs wordAs, String alias);

        JC fullJoin(Query.TabularModifier modifier, String cteName);

        JC fullJoin(Query.TabularModifier modifier, String cteName, SQLs.WordAs wordAs, String alias);

    }

    interface _CrossJoinCteClause<FC> {

        FC crossJoin(String cteName);

        FC crossJoin(String cteName, SQLs.WordAs wordAs, String alias);

    }

    interface _CrossJoinModifierCteClause<FC> extends _CrossJoinCteClause<FC> {

        FC crossJoin(Query.TabularModifier modifier, String cteName);

        FC crossJoin(Query.TabularModifier modifier, String cteName, SQLs.WordAs wordAs, String alias);

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

        JS straightJoin(String cteName, SQLs.WordAs wordAs, String alias);

    }

    interface _StraightJoinModifierCteClause<JS> extends _StraightJoinCteClause<JS> {

        JS straightJoin(Query.TabularModifier modifier, String cteName);

        JS straightJoin(Query.TabularModifier modifier, String cteName, SQLs.WordAs wordAs, String alias);

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
