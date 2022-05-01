package io.army.criteria;

import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface DialectStatement extends Statement {


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
    interface _WithCteClause<C, WE> {

        WE with(String cteName, Supplier<? extends SubQuery> supplier);

        WE with(String cteName, Function<C, ? extends SubQuery> function);

        WE with(Supplier<List<Cte>> supplier);

        WE with(Function<C, List<Cte>> function);

        WE ifWith(Supplier<List<Cte>> supplier);

        WE ifWith(Function<C, List<Cte>> function);

        WE withRecursive(String cteName, Supplier<? extends SubQuery> supplier);

        WE withRecursive(String cteName, Function<C, ? extends SubQuery> function);

        WE withRecursive(Supplier<List<Cte>> supplier);

        WE withRecursive(Function<C, List<Cte>> function);

        WE ifWithRecursive(Supplier<List<Cte>> supplier);

        WE ifWithRecursive(Function<C, List<Cte>> function);


    }


    interface _DialectSelectClause<C, W extends SQLWords, SR> extends Query.SelectClause<C, W, SR> {


        <S extends SelectItem> SR select(Supplier<List<Hint>> hints, List<W> modifiers, Function<C, List<S>> function);

        <S extends SelectItem> SR select(Supplier<List<Hint>> hints, List<W> modifiers, Supplier<List<S>> supplier);

        <S extends SelectItem> SR select(List<W> modifiers, Function<C, List<S>> function);

        <S extends SelectItem> SR select(List<W> modifiers, Supplier<List<S>> supplier);

    }

    /**
     * <p>
     * This interface representing dialect FROM clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <FP> next clause java type
     * @see _FromClause
     * @since 1.0
     */
    interface _DialectFromClause<FP> {

        FP from(TableMeta<?> table);
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

        <T extends TableItem> JS straightJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS straightJoin(Supplier<T> supplier, String alias);

        JT ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS ifStraightJoin(Supplier<T> supplier, String alias);

        <T extends TableItem> JS ifStraightJoin(Function<C, T> function, String alias);

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
    interface _DialectJoinClause<C, JP> {

        JP leftJoin(TableMeta<?> table);

        JP ifLeftJoin(Predicate<C> predicate, TableMeta<?> table);

        JP join(TableMeta<?> table);

        JP ifJoin(Predicate<C> predicate, TableMeta<?> table);

        JP rightJoin(TableMeta<?> table);

        JP ifRightJoin(Predicate<C> predicate, TableMeta<?> table);

        JP fullJoin(TableMeta<?> table);

        JP ifFullJoin(Predicate<C> predicate, TableMeta<?> table);
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
    interface _DialectStraightJoinClause<C, JP> {

        JP straightJoin(TableMeta<?> table);

        JP ifStraightJoin(Predicate<C> predicate, TableMeta<?> table);
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
    interface _DialectCrossJoinClause<C, FP> {

        FP crossJoin(TableMeta<?> table);

        FP ifCrossJoin(Predicate<C> predicate, TableMeta<?> table);

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
     * @param <C>  criteria object java type
     * @param <LT> next clause java type
     * @param <LS> next clause java type
     * @param <LP> next clause java type
     * @since 1.0
     */
    interface _DialectLeftBracketClause<C, LT, LS, LP> extends _LeftBracketClause<C, JT, JS> {

        @Override
        _DialectLeftBracketClause<C, LT, LS, LP> leftBracket();

        LP leftBracket(TableMeta<?> table);
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


}
