package io.army.criteria;

import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.*;

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
    interface _WithCteClause<C, SS extends SubStatement, WE> {

        WE with(String cteName, Supplier<? extends SS> supplier);

        WE with(String cteName, Function<C, ? extends SS> function);

        WE with(Consumer<Consumer<Cte>> consumer);

        WE with(BiConsumer<C, Consumer<Cte>> consumer);

        WE ifWith(Consumer<Consumer<Cte>> consumer);

        WE ifWith(BiConsumer<C, Consumer<Cte>> consumer);

        WE withRecursive(String cteName, Supplier<? extends SS> supplier);

        WE withRecursive(String cteName, Function<C, ? extends SS> function);

        WE withRecursive(Consumer<Consumer<Cte>> consumer);

        WE withRecursive(BiConsumer<C, Consumer<Cte>> consumer);

        WE ifWithRecursive(Consumer<Consumer<Cte>> consumer);

        WE ifWithRecursive(BiConsumer<C, Consumer<Cte>> consumer);


    }


    interface _DialectSelectClause<C, W extends SQLWords, SR> extends Query.SelectClause<C, W, SR> {

        SR select(Supplier<List<Hint>> hints, List<W> modifiers, Consumer<Consumer<SelectItem>> consumer);

        SR select(Supplier<List<Hint>> hints, List<W> modifiers, BiConsumer<C, Consumer<SelectItem>> consumer);

        SR select(List<W> modifiers, Consumer<Consumer<SelectItem>> consumer);

        SR select(List<W> modifiers, BiConsumer<C, Consumer<SelectItem>> consumer);

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
     * This interface representing dialect FROM clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <FS> same with the FS of {@link _FromClause}
     * @see _FromClause
     * @since 1.0
     */
    interface _FromCteClause<FS> {

        FS from(String cteName);

        FS from(String cteName, String alias);

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
    interface _DialectLeftBracketClause<LP> {

        LP leftBracket(TableMeta<?> table);
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
     * @param <LS> same with the LS of {@link _LeftBracketClause}
     * @since 1.0
     */
    interface _LeftBracketCteClause<LS> {

        LS leftBracket(String cteName);

        LS leftBracket(String cteName, String alias);
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
     * This interface representing dialect single delete clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <DP> next clause java type
     * @since 1.0
     */
    interface _DialectSingleDeleteClause<DP> {

        DP deleteFrom(SingleTableMeta<?> table);

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

    /**
     * <p>
     * This interface representing dialect FROM clause for single-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>/
     *
     * @param <DP> next clause java type
     * @since 1.0
     */
    interface _DialectSingleDeleteFromClause<DP> {

        DP from(SingleTableMeta<?> table);

    }


}
