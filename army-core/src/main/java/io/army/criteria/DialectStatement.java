package io.army.criteria;

import io.army.criteria.dialect.Returnings;
import io.army.criteria.impl.SQLs;
import io.army.function.ParensStringFunction;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface DialectStatement extends Statement {


    interface _StaticReturningCommaClause<R> {

        R comma(Selection selection);

        R comma(Expression expression, SQLs.WordAs wordAs, String alias);

        R comma(Supplier<Selection> supplier);

        R comma(NamedExpression exp1, NamedExpression exp2);

        R comma(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3);

        R comma(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3, NamedExpression exp4);
    }


    interface _StaticReturningClause<R> {

        R returning(Selection selection);

        R returning(Expression expression, SQLs.WordAs wordAs, String alias);

        R returning(Supplier<Selection> supplier);

        R returning(NamedExpression exp1, NamedExpression exp2);

        R returning(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3);

        R returning(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3, NamedExpression exp4);

    }


    interface _DynamicReturningClause<R> {

        R returningAll();

        R returning(Consumer<Returnings> consumer);


    }


    interface _DerivedAsClause<R> extends _AsClause<R> {

        R as(String alias, Function<ParensStringFunction, List<String>> function);
    }


    interface _WhereCurrentOfClause<R> {

        R whereCurrentOf(String cursorName);
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

        <T extends DerivedTable> JS straightJoin(Supplier<T> supplier);


    }

    interface _StraightJoinModifierTabularClause<JT, JS> extends _StraightJoinClause<JT, JS> {

        <T extends DerivedTable> JS straightJoin(Query.DerivedModifier modifier, Supplier<T> supplier);
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

        <B> FJ ifStraightJoin(Supplier<B> supplier);

        <B> FJ ifStraightJoin(Function<C, B> function);

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

        JC leftJoin(Query.DerivedModifier modifier, String cteName);

        JC leftJoin(Query.DerivedModifier modifier, String cteName, SQLs.WordAs wordAs, String alias);

        JC join(Query.DerivedModifier modifier, String cteName);

        JC join(Query.DerivedModifier modifier, String cteName, SQLs.WordAs wordAs, String alias);

        JC rightJoin(Query.DerivedModifier modifier, String cteName);

        JC rightJoin(Query.DerivedModifier modifier, String cteName, SQLs.WordAs wordAs, String alias);

        JC fullJoin(Query.DerivedModifier modifier, String cteName);

        JC fullJoin(Query.DerivedModifier modifier, String cteName, SQLs.WordAs wordAs, String alias);

    }

    interface _CrossJoinCteClause<FC> {

        FC crossJoin(String cteName);

        FC crossJoin(String cteName, SQLs.WordAs wordAs, String alias);

    }

    interface _CrossJoinModifierCteClause<FC> extends _CrossJoinCteClause<FC> {

        FC crossJoin(Query.DerivedModifier modifier, String cteName);

        FC crossJoin(Query.DerivedModifier modifier, String cteName, SQLs.WordAs wordAs, String alias);

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

        JS straightJoin(Query.DerivedModifier modifier, String cteName);

        JS straightJoin(Query.DerivedModifier modifier, String cteName, SQLs.WordAs wordAs, String alias);

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
     * @param <LS> same with the LS of {@link _NestedLeftParenClause}
     * @since 1.0
     */
    interface _LeftParenCteClause<LS> {

        LS leftParen(String cteName);

        LS leftParen(String cteName, SQLs.WordAs wordAs, String alias);
    }


    interface _SimpleCteLeftParenSpec<I extends Item> extends _LeftParenStringQuadraOptionalSpec<_StaticAsClaus<I>> {

    }


    interface _DynamicWithClause<B extends CteBuilderSpec, WE> extends Item {
        WE with(Consumer<B> consumer);

        WE withRecursive(Consumer<B> consumer);

        WE ifWith(Consumer<B> consumer);

        WE ifWithRecursive(Consumer<B> consumer);

    }

    interface _StaticWithClause<WS> {

        WS with(String name);

        WS withRecursive(String name);

    }

    interface _StaticWithCommaClause<CR> extends Item {

        CR comma(String name);
    }

    interface _StaticSpaceClause<SR> {

        SR space();
    }


}
