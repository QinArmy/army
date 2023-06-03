package io.army.criteria;

import io.army.criteria.dialect.SubDelete;
import io.army.criteria.impl.SQLs;
import io.army.dialect.Dialect;
import io.army.function.*;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.TableMeta;
import io.army.stmt.Stmt;

import java.util.List;
import java.util.function.*;

import static io.army.dialect.Database.*;

/**
 * <p>
 * This interface representing sql statement,this interface is base interface of below:
 * <ul>
 *     <li>{@link Select}</li>
 *     <li>{@link InsertStatement}</li>
 *     <li>{@link UpdateStatement}</li>
 *     <li>{@link DeleteStatement}</li>
 *     <li>{@link SubQuery}</li>
 *     <li>{@link Values}</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
public interface Statement extends Item {

    /**
     * assert statement prepared
     */
    void prepared();

    boolean isPrepared();


    interface StatementMockSpec {

        /**
         * @param none nothing
         */
        String mockAsString(Dialect dialect, Visible visible, boolean none);

        Stmt mockAsStmt(Dialect dialect, Visible visible);

    }


    interface TableModifier extends SQLWords {

    }

    interface DerivedModifier extends SQLWords {

    }

    interface FetchFirstNext {

    }

    interface FetchRow {

    }

    interface FetchOnly {

    }

    interface FetchWithTies {

    }

    interface FetchOnlyWithTies extends FetchOnly, FetchWithTies {

    }


    interface DmlStatementSpec {

    }

    interface JoinBuilder {

    }

    interface AscDesc extends SQLWords {

    }

    interface NullsFirstLast extends SQLWords {

    }

    interface _ExpressionCommaClause {

        _ExpressionCommaClause comma(Expression exp);

    }

    interface _ExpressionSpaceClause {

        _ExpressionCommaClause space(Expression exp);
    }

    interface _ExpressionConsumer {

        _ExpressionConsumer accept(Expression exp);

    }


    interface _ElementCommaClause {

        _ElementCommaClause comma(SQLExpression exp);

        _ElementCommaClause comma(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        _ElementCommaClause comma(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk asterisk);

    }

    interface _ElementSpaceClause {

        _ElementCommaClause space(SQLExpression exp);

        _ElementCommaClause space(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        _ElementCommaClause space(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk asterisk);

    }

    interface _ElementConsumer {

        _ElementConsumer accept(SQLExpression exp);

        _ElementConsumer accept(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

        _ElementConsumer accept(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk asterisk);

    }

    @Deprecated
    interface _ElementObjectCommaClause {

        _ElementObjectCommaClause comma(String keName, SQLExpression exp);

        _ElementObjectCommaClause comma(Expression key, SQLExpression exp);

        _ElementObjectCommaClause comma(String keName, String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk asterisk);

        _ElementObjectCommaClause comma(Expression key, String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk asterisk);
    }

    interface _StaticObjectSpaceClause {

        _StaticObjectCommaClause space(String keyName, Object value);

        _StaticObjectCommaClause space(Expression key, Object value);

    }

    interface _StaticObjectCommaClause {

        _StaticObjectCommaClause comma(String keyName, Object value);

        _StaticObjectCommaClause comma(Expression key, Object value);

    }


    interface _DynamicObjectConsumer {

        _DynamicObjectConsumer accept(String keyName, Object value);

        _DynamicObjectConsumer accept(Expression key, Object value);

    }


    @Deprecated
    interface _ElementObjectSpaceClause {

        _ElementObjectCommaClause space(String keName, SQLExpression exp);

        _ElementObjectCommaClause space(Expression key, SQLExpression exp);

        _ElementObjectCommaClause space(String keName, String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk asterisk);

        _ElementObjectCommaClause space(Expression key, String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk asterisk);
    }

    interface _ElementObjectConsumer {

        _ElementObjectConsumer accept(String keName, SQLExpression value);

        _ElementObjectConsumer accept(Expression key, SQLExpression value);

        _ElementObjectConsumer accept(String keName, String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk asterisk);

        _ElementObjectConsumer accept(Expression key, String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolAsterisk asterisk);

    }

    interface _StringObjectCommaClause {

        _StringObjectCommaClause comma(String key, String value);
    }

    interface _StringObjectSpaceClause {

        _StringObjectCommaClause space(String key, String value);
    }

    interface _StringObjectConsumer {

        _StringObjectConsumer accept(String key, String value);
    }


    /**
     * <p>
     * This interface representing AS clause.
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
    interface _AsClause<AR> extends Item {

        AR as(String alias);
    }

    interface _StaticAsClaus<AR> extends Item {

        AR as();
    }

    interface _StaticBetweenClause<BR> {

        BR between();
    }

    interface _StaticAndClause<AR> extends Item {

        AR and();
    }


    /**
     * <p>
     * This interface representing bind params clause for batch update(delete).
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <R> next clause java type
     * @since 1.0
     */
    interface _BatchParamClause<R extends Item> {

        <P> R namedParamList(List<P> paramList);

        <P> R namedParamList(Supplier<List<P>> supplier);

        R namedParamList(Function<String, ?> function, String keyName);
    }


    interface _LeftParenNestedClause<T extends Item, R extends Item> {

        R leftParen(Function<T, R> function);
    }

    /**
     * <p>
     * This interface representing RIGHT BRACKET clause in join expression.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <RR> next clause java type
     * @since 1.0
     */
    interface _RightParenClause<RR> extends Item {

        RR rightParen();

    }


    /**
     * <p>
     * This interface representing ON clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <OR> next clause java type
     * @since 1.0
     */
    interface _OnClause<OR> extends Item {

        OR on(IPredicate predicate);

        OR on(IPredicate predicate1, IPredicate predicate2);

        OR on(Function<Expression, IPredicate> operator, SQLField operandField);

        OR on(Function<Expression, IPredicate> operator1, SQLField operandField1
                , Function<Expression, IPredicate> operator2, SQLField operandField2);

        OR on(Consumer<Consumer<IPredicate>> consumer);


    }


    /**
     * <p>
     * This interface representing FROM clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <FT> next clause java type
     * @param <FS> next clause java type
     * @since 1.0
     */
    interface _FromClause<FT, FS> extends Item {

        FT from(TableMeta<?> table, SQLs.WordAs as, String tableAlias);

        FS from(DerivedTable derivedTable);

        <T extends DerivedTable> FS from(Supplier<T> supplier);

    }

    interface _FromModifierTabularClause<FT, FS> extends _FromClause<FT, FS> {

        FS from(@Nullable DerivedModifier modifier, DerivedTable derivedTable);

        <T extends DerivedTable> FS from(@Nullable DerivedModifier modifier, Supplier<T> supplier);
    }

    interface _FromModifierClause<FT, FS> extends _FromModifierTabularClause<FT, FS> {

        FT from(@Nullable TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

    }

    interface _FromNestedClause<T extends Item, R extends Item> {

        R from(Function<T, R> function);

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
     * @param <R> same with the FS of {@link _FromClause}
     * @see _FromClause
     * @since 1.0
     */
    interface _FromCteClause<R> {

        R from(String cteName);

        R from(String cteName, SQLs.WordAs wordAs, String alias);

    }

    interface _FromModifierCteClause<R> extends _FromCteClause<R> {

        R from(DerivedModifier modifier, String cteName);

        R from(DerivedModifier modifier, String cteName, SQLs.WordAs wordAs, String alias);
    }


    interface _FromUndoneFunctionClause<R> {

        R from(UndoneFunction func);
    }

    interface _FromModifierUndoneFunctionClause<R> extends _FromUndoneFunctionClause<R> {

        R from(@Nullable DerivedModifier modifier, UndoneFunction func);
    }


    /**
     * <p>
     * This interface representing FROM clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <FT> next clause java type
     * @param <FS> next clause java type
     * @since 1.0
     */
    interface _UsingItemClause<FT, FS> extends Item {

        FT using(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        FS using(DerivedTable derivedTable);

        <T extends DerivedTable> FS using(Supplier<T> supplier);

    }

    interface _UsingModifierTabularClause<FT, FS> extends _UsingItemClause<FT, FS> {

        FS using(@Nullable DerivedModifier modifier, DerivedTable derivedTable);

        <T extends DerivedTable> FS using(@Nullable DerivedModifier modifier, Supplier<T> supplier);
    }

    interface _UsingModifierClause<FT, FS> extends _UsingModifierTabularClause<FT, FS> {

        FT using(@Nullable TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

    }

    interface _UsingNestedClause<T extends Item, R extends Item> {

        R using(Function<T, R> function);

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
     * @param <FC> same with the FS of {@link _FromClause}
     * @see _FromClause
     * @since 1.0
     */
    interface _UsingCteClause<FC> {

        FC using(String cteName);

        FC using(String cteName, SQLs.WordAs wordAs, String alias);

    }

    interface _UsingModifierCteClause<FC> extends _UsingCteClause<FC> {

        FC using(DerivedModifier modifier, String cteName);

        FC using(DerivedModifier modifier, String cteName, SQLs.WordAs wordAs, String alias);
    }


    interface _UsingUndoneFunctionClause<R> {

        R using(UndoneFunction function);
    }

    interface _UsingModifierUndoneFunctionClause<R> extends _UsingUndoneFunctionClause<R> {

        R using(@Nullable DerivedModifier modifier, UndoneFunction function);
    }


    /**
     * <p>
     * This interface representing JOIN clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <JT> next clause java type,it's sub interface of {@link _OnClause}
     * @param <JS> next clause java type,it's sub interface of {@link _OnClause}
     * @see _CrossJoinClause
     * @since 1.0
     */
    interface _JoinClause<JT, JS> extends Item {

        JT leftJoin(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        JS leftJoin(DerivedTable derivedTable);

        <T extends DerivedTable> JS leftJoin(Supplier<T> supplier);

        JT join(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        JS join(DerivedTable derivedTable);

        <T extends DerivedTable> JS join(Supplier<T> supplier);

        JT rightJoin(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        JS rightJoin(DerivedTable derivedTable);


        <T extends DerivedTable> JS rightJoin(Supplier<T> supplier);

        JT fullJoin(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        JS fullJoin(DerivedTable derivedTable);

        <T extends DerivedTable> JS fullJoin(Supplier<T> supplier);

    }


    interface _JoinModifierTabularClause<JT, JS> extends _JoinClause<JT, JS> {

        JS leftJoin(@Nullable DerivedModifier modifier, DerivedTable derivedTable);

        <T extends DerivedTable> JS leftJoin(@Nullable DerivedModifier modifier, Supplier<T> supplier);

        JS join(@Nullable DerivedModifier modifier, DerivedTable derivedTable);

        <T extends DerivedTable> JS join(@Nullable DerivedModifier modifier, Supplier<T> supplier);

        JS rightJoin(@Nullable DerivedModifier modifier, DerivedTable derivedTable);

        <T extends DerivedTable> JS rightJoin(@Nullable DerivedModifier modifier, Supplier<T> supplier);

        JS fullJoin(@Nullable DerivedModifier modifier, DerivedTable derivedTable);

        <T extends DerivedTable> JS fullJoin(@Nullable DerivedModifier modifier, Supplier<T> supplier);
    }

    interface _JoinModifierClause<JT, JS> extends _JoinModifierTabularClause<JT, JS> {

        JT leftJoin(@Nullable TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        JT join(@Nullable TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        JT rightJoin(@Nullable TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        JT fullJoin(@Nullable TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

    }


    interface _JoinUndoneFunctionClause<R> {

        R leftJoin(UndoneFunction func);

        R join(UndoneFunction func);

        R rightJoin(UndoneFunction func);

        R fullJoin(UndoneFunction func);
    }

    interface _JoinModifierUndoneFunctionClause<R> extends _JoinUndoneFunctionClause<R> {

        R leftJoin(@Nullable DerivedModifier modifier, UndoneFunction func);

        R join(@Nullable DerivedModifier modifier, UndoneFunction func);

        R rightJoin(@Nullable DerivedModifier modifier, UndoneFunction func);

        R fullJoin(@Nullable DerivedModifier modifier, UndoneFunction func);

    }


    /**
     * <p>
     * This interface representing CROSS JOIN clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _CrossJoinClause<FT, FS> {

        FT crossJoin(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        FS crossJoin(DerivedTable derivedTable);

        <T extends DerivedTable> FS crossJoin(Supplier<T> supplier);

    }

    interface _CrossJoinModifierTabularClause<FT, FS> extends _CrossJoinClause<FT, FS> {

        FS crossJoin(@Nullable DerivedModifier modifier, DerivedTable derivedTable);

        <T extends DerivedTable> FS crossJoin(@Nullable DerivedModifier modifier, Supplier<T> supplier);
    }

    interface _CrossJoinModifierClause<FT, FS> extends _CrossJoinModifierTabularClause<FT, FS> {

        FT crossJoin(@Nullable TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);


    }

    interface _CrossUndoneFunctionClause<R> {

        R crossJoin(UndoneFunction func);
    }


    interface _CrossModifierUndoneFunctionClause<R> extends _CrossUndoneFunctionClause<R> {

        R crossJoin(@Nullable DerivedModifier modifier, UndoneFunction func);

    }

    interface _JoinNestedClause<T extends Item, R extends Item> {

        R leftJoin(Function<T, R> function);

        R join(Function<T, R> function);

        R rightJoin(Function<T, R> function);

        R fullJoin(Function<T, R> function);

    }

    interface _StraightJoinNestedClause<T extends Item, R extends Item> {

        R straightJoin(Function<T, R> function);
    }

    interface _CrossJoinNestedClause<T extends Item, R extends Item> {

        R crossJoin(Function<T, R> function);
    }


    interface _DynamicJoinClause<B extends JoinBuilder, JD> {

        JD ifLeftJoin(Consumer<B> consumer);

        JD ifJoin(Consumer<B> consumer);

        JD ifRightJoin(Consumer<B> consumer);

        JD ifFullJoin(Consumer<B> consumer);
    }

    interface _DynamicCrossJoinClause<B extends JoinBuilder, JD> {

        JD ifCrossJoin(Consumer<B> consumer);
    }

    interface _DynamicStraightJoinClause<B extends JoinBuilder, JD> {

        JD ifStraightJoin(Consumer<B> consumer);
    }


    /**
     * <p>
     * This interface representing a left bracket clause after key word 'FROM' or key word 'JOIN'.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <LT> next clause java type
     * @param <LS> next clause java type
     * @since 1.0
     */
    interface _NestedLeftParenClause<LT, LS> extends Item {

        LT leftParen(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        LS leftParen(DerivedTable derivedTable);

        <T extends DerivedTable> LS leftParen(Supplier<T> supplier);

    }

    interface _NestedLeftParenModifierTabularClause<LT, LS> extends _NestedLeftParenClause<LT, LS> {

        LS leftParen(@Nullable DerivedModifier modifier, DerivedTable derivedTable);

        <T extends DerivedTable> LS leftParen(@Nullable DerivedModifier modifier, Supplier<T> supplier);

    }

    interface _NestedLeftParenModifierClause<LT, LS> extends _NestedLeftParenModifierTabularClause<LT, LS> {

        LT leftParen(@Nullable TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

    }

    interface _NestedLeftParenUndoneFunctionClause<R> {

        R leftParen(UndoneFunction func);
    }

    interface _NestedLeftParenModifierUndoneFunctionClause<R> extends _NestedLeftParenUndoneFunctionClause<R> {

        R leftParen(@Nullable DerivedModifier modifier, UndoneFunction func);
    }


    interface _NestedTableLeftParenClause<LP> {

        LP leftParen(TableMeta<?> table);

    }


    interface _DynamicTabularItemClause<FT, FS> {

        FT space(TableMeta<?> table, SQLs.WordAs wordAs, String alias);

        FS space(DerivedTable derivedTable);

        <T extends DerivedTable> FS space(Supplier<T> supplier);

    }

    interface _DynamicTabularDerivedModifierClause<FT, FS> extends _DynamicTabularItemClause<FT, FS> {

        FS space(@Nullable DerivedModifier modifier, DerivedTable derivedTable);

        <T extends DerivedTable> FS space(@Nullable DerivedModifier modifier, Supplier<T> supplier);

    }


    interface _DynamicTabularModifierClause<FT, FS> extends _DynamicTabularDerivedModifierClause<FT, FS> {


        FT space(@Nullable TableModifier modifier, TableMeta<?> table, SQLs.WordAs wordAs, String alias);

    }

    interface _DynamicTabularUndoneFunctionClause<R> {

        R space(UndoneFunction func);
    }

    interface _DynamicTabularModifierUndoneFunctionClause<R> extends _DynamicTabularUndoneFunctionClause<R> {

        R space(@Nullable DerivedModifier modifier, UndoneFunction func);
    }


    interface _DynamicTabularCteClause<R extends Item> {

        R space(String cteName);

        R space(String cteName, SQLs.WordAs as, String alias);

    }

    interface _DynamicTabularNestedClause<T extends Item, R extends Item> {

        R space(Function<T, R> function);
    }


    interface _MinWhereClause<WR, WA> {

        WR where(Consumer<Consumer<IPredicate>> consumer);

        WA where(IPredicate predicate);

        WA where(Supplier<IPredicate> supplier);

        WA whereIf(Supplier<IPredicate> supplier);

    }

    interface _MinQueryWhereClause<WR, WA> extends _MinWhereClause<WR, WA> {

        WR ifWhere(Consumer<Consumer<IPredicate>> consumer);
    }

    /**
     * <p>
     * This interface representing WHERE clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <WR> next clause java type
     * @param <WA> next clause java type
     * @since 1.0
     */
    interface _WhereClause<WR, WA> extends _MinWhereClause<WR, WA> {


        WA where(Function<Expression, IPredicate> expOperator, Expression operand);

        WA where(UnaryOperator<IPredicate> expOperator, IPredicate operand);

        <T> WA where(Function<T, IPredicate> expOperator, Supplier<T> supplier);


        WA where(Function<BiFunction<SQLField, String, Expression>, IPredicate> fieldOperator,
                 BiFunction<SQLField, String, Expression> operator);

        //below ordinary operator
        <T> WA where(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                     BiFunction<SimpleExpression, T, Expression> valueOperator, T value);

        <T> WA where(DialectBooleanOperator<T> fieldOperator, BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                     BiFunction<SimpleExpression, T, Expression> func, @Nullable T value);

        // below in operator
        WA where(BiFunction<TeNamedOperator<SQLField>, Integer, IPredicate> expOperator,
                 TeNamedOperator<SQLField> namedOperator, int size);


        WA where(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator, String paramName, int size);

        //below between operator

        WA where(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

        <T> WA where(BetweenValueOperator<T> expOperator, BiFunction<SimpleExpression, T, Expression> operator,
                     T firstValue, SQLs.WordAnd and, T secondValue);

        <T, U> WA where(BetweenDualOperator<T, U> expOperator, BiFunction<SimpleExpression, T, Expression> firstFuncRef,
                        T first, SQLs.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondRef, U second);

        <T> WA whereIf(Function<T, IPredicate> expOperator, Supplier<T> supplier);

        <T> WA whereIf(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                       BiFunction<SimpleExpression, T, Expression> operator, Supplier<T> getter);

        <T> WA whereIf(DialectBooleanOperator<T> fieldOperator, BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                       BiFunction<SimpleExpression, T, Expression> func, Supplier<T> getter);

        WA whereIf(BiFunction<TeNamedOperator<SQLField>, Integer, IPredicate> expOperator,
                   TeNamedOperator<SQLField> namedOperator, Supplier<Integer> supplier);

        //below four argument method

        <K, V> WA whereIf(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator,
                          BiFunction<SimpleExpression, V, Expression> operator, Function<K, V> function, K key);

        <K, V> WA whereIf(DialectBooleanOperator<V> fieldOperator, BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                          BiFunction<SimpleExpression, V, Expression> func, Function<K, V> function, K key);

        WA whereIf(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator, String paramName,
                   Supplier<Integer> supplier);

        //below between where if

        <T> WA whereIf(BetweenValueOperator<T> expOperator, BiFunction<SimpleExpression, T, Expression> operator,
                       Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        <T, U> WA whereIf(BetweenDualOperator<T, U> expOperator, BiFunction<SimpleExpression, T, Expression> firstFuncRef,
                          Supplier<T> firstGetter, SQLs.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondFuncRef,
                          Supplier<U> secondGetter);

        <K, V> WA whereIf(BetweenValueOperator<V> expOperator, BiFunction<SimpleExpression, V, Expression> operator,
                          Function<K, V> function, K firstKey, SQLs.WordAnd and, K secondKey);


    }

    /**
     * <p>
     * This interface representing WHERE clause in SELECT statement.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <WR> next clause java type
     * @param <WA> next clause java type
     * @since 1.0
     */
    interface _QueryWhereClause<WR, WA> extends _WhereClause<WR, WA>, _MinQueryWhereClause<WR, WA> {

    }


    interface _MinWhereAndClause<WA> {

        WA and(IPredicate predicate);

        WA and(Supplier<IPredicate> supplier);

        WA ifAnd(Supplier<IPredicate> supplier);

    }


    /**
     * <p>
     * This interface representing AND clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <WA> next clause java type
     * @since 1.0
     */
    interface _WhereAndClause<WA> extends _MinWhereAndClause<WA> {


        WA and(Function<Expression, IPredicate> expOperator, Expression operand);

        WA and(UnaryOperator<IPredicate> expOperator, IPredicate operand);

        <T> WA and(Function<T, IPredicate> expOperator, Supplier<T> supplier);


        WA and(Function<BiFunction<SQLField, String, Expression>, IPredicate> fieldOperator,
               BiFunction<SQLField, String, Expression> operator);

        //below ordinary operator
        <T> WA and(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                   BiFunction<SimpleExpression, T, Expression> valueOperator, T value);

        <T> WA and(DialectBooleanOperator<T> fieldOperator, BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                   BiFunction<SimpleExpression, T, Expression> func, @Nullable T value);

        // below in operator
        WA and(BiFunction<TeNamedOperator<SQLField>, Integer, IPredicate> expOperator,
               TeNamedOperator<SQLField> namedOperator, int size);


        WA and(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator, String paramName, int size);

        //below between operator

        WA and(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

        <T> WA and(BetweenValueOperator<T> expOperator, BiFunction<SimpleExpression, T, Expression> operator,
                   T firstValue, SQLs.WordAnd and, T secondValue);

        <T, U> WA and(BetweenDualOperator<T, U> expOperator, BiFunction<SimpleExpression, T, Expression> firstFuncRef,
                      T first, SQLs.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondRef, U second);

        <T> WA ifAnd(Function<T, IPredicate> expOperator, Supplier<T> supplier);

        <T> WA ifAnd(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                     BiFunction<SimpleExpression, T, Expression> operator, Supplier<T> getter);

        <T> WA ifAnd(DialectBooleanOperator<T> fieldOperator, BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                     BiFunction<SimpleExpression, T, Expression> func, Supplier<T> getter);

        WA ifAnd(BiFunction<TeNamedOperator<SQLField>, Integer, IPredicate> expOperator,
                 TeNamedOperator<SQLField> namedOperator, Supplier<Integer> supplier);

        //below four argument method

        <K, V> WA ifAnd(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator,
                        BiFunction<SimpleExpression, V, Expression> operator, Function<K, V> function, K key);

        <K, V> WA ifAnd(DialectBooleanOperator<V> fieldOperator, BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                        BiFunction<SimpleExpression, V, Expression> func, Function<K, V> function, K key);

        WA ifAnd(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator, String paramName,
                 Supplier<Integer> supplier);

        //below between where if

        <T> WA ifAnd(BetweenValueOperator<T> expOperator, BiFunction<SimpleExpression, T, Expression> operator,
                     Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        <T, U> WA ifAnd(BetweenDualOperator<T, U> expOperator, BiFunction<SimpleExpression, T, Expression> firstFuncRef,
                        Supplier<T> firstGetter, SQLs.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondFuncRef,
                        Supplier<U> secondGetter);

        <K, V> WA ifAnd(BetweenValueOperator<V> expOperator, BiFunction<SimpleExpression, V, Expression> operator,
                        Function<K, V> function, K firstKey, SQLs.WordAnd and, K secondKey);

    }

    interface _SimpleWhereAndClause extends _WhereAndClause<_SimpleWhereAndClause> {

    }

    interface _SimpleWhereClause extends _WhereClause<Item, _SimpleWhereAndClause> {

    }


    interface _StaticOrderByCommaClause<OR> {


        OR comma(Expression exp, AscDesc ascDesc);

        OR comma(Expression exp1, AscDesc ascDesc1, Expression exp2);

        OR comma(Expression exp1, Expression exp2, AscDesc ascDesc2);

        OR comma(Expression exp1, AscDesc ascDesc1, Expression exp2, AscDesc ascDesc2);

    }

    interface _StaticOrderByNullsCommaClause<OR> extends _StaticOrderByCommaClause<OR> {

        OR comma(Expression exp, NullsFirstLast nullOption);

        OR comma(Expression exp, AscDesc ascDesc, NullsFirstLast nullOption);

    }

    /**
     * <p>
     * This interface representing ORDER BY clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    @Deprecated
    interface _StaticOrderByClause0<OR> extends Item {

        OR orderBy(Expression exp);

        OR orderBy(Expression exp, AscDesc ascDesc);

        OR orderBy(Expression exp1, Expression exp2);

        OR orderBy(Expression exp1, AscDesc ascDesc1, Expression exp2);

        OR orderBy(Expression exp1, Expression exp2, AscDesc ascDesc2);

        OR orderBy(Expression exp1, AscDesc ascDesc1, Expression exp2, AscDesc ascDesc2);

    }

    interface _OrderByCommaClause<R> {

        R spaceComma(SortItem sortItem);

        R spaceComma(SortItem sortItem1, SortItem sortItem2);

        R spaceComma(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3);

        R spaceComma(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3, SortItem sortItem4);
    }

    interface _StaticOrderByClause<R> extends Item {

        R orderBy(SortItem sortItem);

        R orderBy(SortItem sortItem1, SortItem sortItem2);

        R orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3);

        R orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3, SortItem sortItem4);


    }


    interface _DynamicOrderByClause<R> {

        R orderBy(Consumer<Consumer<SortItem>> consumer);

        R ifOrderBy(Consumer<Consumer<SortItem>> consumer);
    }

    interface _SimpleOrderByCommaClause extends _OrderByCommaClause<_SimpleOrderByCommaClause> {

    }

    interface _SimpleOrderByClause extends _StaticOrderByClause<_SimpleOrderByCommaClause>,
            _DynamicOrderByClause<Item> {

    }


    @Deprecated
    interface _DynamicOrderByClause0<B, R> {

        R orderBy(Consumer<B> consumer);

        R ifOrderBy(Consumer<B> consumer);
    }


    /**
     * <p>
     * This interface representing row count limit clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _RowCountLimitClause<R> extends Item {

        R limit(Expression rowCount);

        R limit(BiFunction<MappingType, Number, Expression> operator, long rowCount);

        <N extends Number> R limit(BiFunction<MappingType, Number, Expression> operator, Supplier<N> supplier);

        R limit(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function, String keyName);

        <N extends Number> R ifLimit(BiFunction<MappingType, Number, Expression> operator, Supplier<N> supplier);

        R ifLimit(BiFunction<MappingType, Number, Expression> operator, Function<String, ?> function, String keyName);

        R ifLimit(Supplier<Expression> supplier);

    }


    interface _DmlRowCountLimitClause<LR> extends _RowCountLimitClause<LR> {

        LR limit(BiFunction<MappingType, String, Expression> operator, String paramName);

        LR ifLimit(BiFunction<MappingType, String, Expression> operator, @Nullable String paramName);
    }

    interface _RowCountLimitAllClause<LR> extends _RowCountLimitClause<LR> {

        LR limitAll();

        LR ifLimitAll(BooleanSupplier supplier);

    }


    interface _QueryOffsetClause<R> {

        R offset(Expression start, Query.FetchRow row);


        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                 </ul>
         * @param start    non-negative integer
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        R offset(BiFunction<MappingType, Number, Expression> operator, long start, Query.FetchRow row);


        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                 </ul>
         * @param supplier return non-negative integer
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        <N extends Number> R offset(BiFunction<MappingType, Number, Expression> operator, Supplier<N> supplier
                , Query.FetchRow row);

        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                 </ul>
         * @param function {@link Function#apply(Object)} return non-negative integer
         * @param keyName  keyName that is passed to function
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        R offset(BiFunction<MappingType, Object, Expression> operator, Function<String, ?> function
                , String keyName, Query.FetchRow row);

        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                 </ul>
         * @param start    non-negative integer
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        R ifOffset(BiFunction<MappingType, Number, Expression> operator, @Nullable Number start, Query.FetchRow row);

        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                 </ul>
         * @param supplier return non-negative integer
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        <N extends Number> R ifOffset(BiFunction<MappingType, Number, Expression> operator, Supplier<N> supplier
                , Query.FetchRow row);

        /**
         * @param operator the method reference of below:
         *                 <ul>
         *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                 </ul>
         * @param function {@link Function#apply(Object)} return non-negative integer
         * @param keyName  keyName that is passed to function
         * @param row      {@link SQLs#ROW} or {@link SQLs#ROWS}
         */
        R ifOffset(BiFunction<MappingType, Object, Expression> operator, Function<String, ?> function
                , String keyName, Query.FetchRow row);

    }

    interface _QueryFetchClause<R> {

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param count        non-negative
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, Expression count, Query.FetchRow row
                , Query.FetchOnlyWithTies onlyWithTies);


        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param count        non-negative
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , long count, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param supplier     return non-negative integer
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        <N extends Number> R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param function     {@link Function#apply(Object)} return non-negative integer
         * @param keyName      keyName that is passed to function
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Object, Expression> operator
                , Function<String, ?> function, String keyName, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param count        non-negative
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , @Nullable Number count, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param supplier     return non-negative integer
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        <N extends Number> R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param function     {@link Function#apply(Object)} return non-negative integer
         * @param keyName      keyName that is passed to function
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Object, Expression> operator
                , Function<String, ?> function, String keyName, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);
    }

    interface _LimitClause<LR> extends _RowCountLimitClause<LR> {

        LR limit(Expression offset, Expression rowCount);

        LR limit(BiFunction<MappingType, Number, Expression> operator, long offset, long rowCount);

        <N extends Number> LR limit(BiFunction<MappingType, Number, Expression> operator, Supplier<N> offsetSupplier, Supplier<N> rowCountSupplier);

        LR limit(BiFunction<MappingType, Object, Expression> operator, Function<String, ?> function, String offsetKey, String rowCountKey);

        LR limit(Consumer<BiConsumer<Expression, Expression>> consumer);


        <N extends Number> LR ifLimit(BiFunction<MappingType, Number, Expression> operator, Supplier<N> offsetSupplier, Supplier<N> rowCountSupplier);

        LR ifLimit(BiFunction<MappingType, Object, Expression> operator, Function<String, ?> function, String offsetKey, String rowCountKey);

        LR ifLimit(Consumer<BiConsumer<Expression, Expression>> consumer);

    }

    interface _FetchPercentClause<R> extends _QueryFetchClause<R> {

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param percent      the percentage of the total number of selected rows
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, Expression percent, SQLs.WordPercent wordPercent, Query.FetchRow row
                , Query.FetchOnlyWithTies onlyWithTies);


        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param percent      non-null,the percentage of the total number of selected rows
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Number percent, SQLs.WordPercent wordPercent, Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param supplier     {@link  Supplier#get()} return non-null percent
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        <N extends Number> R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, SQLs.WordPercent wordPercent, Query.FetchRow row
                , Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param function     {@link Function#apply(Object)} return non-null percent
         * @param keyName      keyName that is passed to function
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R fetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Object, Expression> operator
                , Function<String, ?> function, String keyName, SQLs.WordPercent wordPercent
                , Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param percent      nullable,percent
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , @Nullable Number percent, SQLs.WordPercent wordPercent, Query.FetchRow row
                , Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param supplier     return nullable percent
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        <N extends Number> R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Number, Expression> operator
                , Supplier<N> supplier, SQLs.WordPercent wordPercent, Query.FetchRow row
                , Query.FetchOnlyWithTies onlyWithTies);

        /**
         * @param firstOrNext  {@link SQLs#FIRST} or {@link SQLs#NEXT}
         * @param operator     the method reference of below:
         *                     <ul>
         *                          <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                          <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                     </ul>
         * @param function     {@link Function#apply(Object)} return nullable percent
         * @param keyName      keyName that is passed to function
         * @param wordPercent  {@link SQLs#PERCENT}
         * @param row          {@link SQLs#ROW} or {@link SQLs#ROWS}
         * @param onlyWithTies {@link SQLs#ONLY} or {@link SQLs#WITH_TIES}
         */
        R ifFetch(Query.FetchFirstNext firstOrNext, BiFunction<MappingType, Object, Expression> operator
                , Function<String, ?> function, String keyName, SQLs.WordPercent wordPercent
                , Query.FetchRow row, Query.FetchOnlyWithTies onlyWithTies);

    }


    interface _CommaStringDualSpec<PR> extends Statement._RightParenClause<PR> {

        Statement._RightParenClause<PR> comma(String string);

        _CommaStringDualSpec<PR> comma(String string1, String string2);
    }

    interface _CommaStringQuadraSpec<PR> extends Statement._RightParenClause<PR> {

        Statement._RightParenClause<PR> comma(String string);

        Statement._RightParenClause<PR> comma(String string1, String string2);

        Statement._RightParenClause<PR> comma(String string1, String string2, String string3);

        _CommaStringQuadraSpec<PR> comma(String string1, String string2, String string3, String string4);

    }

    interface _LeftParenStringDualClause<PR> extends Item {

        Statement._RightParenClause<PR> leftParen(String string);

        _CommaStringDualSpec<PR> leftParen(String string1, String string2);


    }

    interface _LeftParenStringDynamicClause<RR> extends Item {

        Statement._RightParenClause<RR> leftParen(Consumer<Consumer<String>> consumer);
    }

    interface _LeftParenStringDynamicOptionalClause<RR> extends Item {

        Statement._RightParenClause<RR> leftParenIf(Consumer<Consumer<String>> consumer);

    }


    interface _LeftParenStringDualSpec<RR>
            extends _LeftParenStringDualClause<RR>, _LeftParenStringDynamicClause<RR> {

    }

    interface _LeftParenStringDualOptionalSpec<RR> extends _LeftParenStringDualSpec<RR>
            , _LeftParenStringDynamicOptionalClause<RR> {

    }


    interface _LeftParenStringQuadraSpec<RR> extends _LeftParenStringDualSpec<RR> {

        _CommaStringQuadraSpec<RR> leftParen(String string1, String string2, String string3, String string4);

    }

    interface _LeftParenStringQuadraOptionalSpec<RR> extends _LeftParenStringQuadraSpec<RR>
            , _LeftParenStringDynamicOptionalClause<RR> {


    }


    interface _ParensStringClause<R> extends Item {

        R parens(String first, String... rest);

        R parens(Consumer<Consumer<String>> consumer);
    }

    interface _OptionalParensStringClause<R> extends _ParensStringClause<R> {

        R ifParens(Consumer<Consumer<String>> consumer);
    }


    interface _ParensOnSpec<R> extends _OptionalParensStringClause<_OnClause<R>>, _OnClause<R> {

    }

    interface _AsParensOnClause<R> extends _AsClause<_ParensOnSpec<R>> {

    }


    interface _DmlInsertClause<I extends Item> extends Item {

        I asInsert();

    }

    interface _DqlInsertClause<Q extends Item> {

        Q asReturningInsert();
    }


    interface _DmlUpdateSpec<I extends Item> extends Item {

        I asUpdate();
    }

    interface _DqlUpdateSpec<I extends Item> extends Item {

        I asReturningUpdate();
    }

    interface _DmlDeleteSpec<I extends Item> extends Item {

        I asDelete();
    }

    interface _DqlDeleteSpec<Q extends Item> extends Item {

        Q asReturningDelete();
    }

    interface _MultiStmtSpec extends Item {

        MultiStatement asMultiStmt();

    }

    interface _AsValuesClause<I extends Item> {

        I asValues();

    }


    /**
     * This is base interface of below:
     * <ul>
     *     <li>{@link UpdateStatement}</li>
     *     <li>{@link SubUpdate}</li>
     * </ul>
     *
     * @since 1.0
     */
    interface DmlUpdate {


    }

    /**
     * This is base interface of below:
     * <ul>
     *     <li>{@link DeleteStatement}</li>
     *     <li>{@link SubDelete}</li>
     * </ul>
     *
     * @since 1.0
     */
    @Deprecated
    interface DmlDelete {


    }

    interface _AsCommandClause<I extends Item> extends Item {

        I asCommand();
    }


    /**
     * This is base interface of below:
     * <ul>
     *     <li>{@link InsertStatement}</li>
     * </ul>
     *
     * @since 1.0
     */
    @Deprecated
    interface DmlInsert extends Item {


    }

    @Deprecated
    interface DqlInsert extends Item {

    }

    interface _ArrayExpOperator {

        @Support({H2, PostgreSQL})
        SimpleExpression atElement(int index);

        SimpleExpression atElement(int index1, int index2);

        SimpleExpression atElement(int index1, int index2, int index3, int... restIndex);

        <T> SimpleExpression atElement(BiFunction<MappingType, T, Expression> funcRef, T value);

        <T> SimpleExpression atElement(BiFunction<MappingType, T, Expression> funcRef, T value1, T value2);

        <T> SimpleExpression atElement(BiFunction<MappingType, T, Expression> funcRef, T value1, T value2, T value3);

        <T, U> SimpleExpression atElement(BiFunction<MappingType, T, Expression> funcRef1, T value1, BiFunction<MappingType, U, Expression> funcRef2, U value2);

        <T, U, V> SimpleExpression atElement(BiFunction<MappingType, T, Expression> funcRef1, T value1, BiFunction<MappingType, U, Expression> funcRef2, U value2, BiFunction<MappingType, V, Expression> funcRef3, V value3);


        SimpleExpression atElement(Expression index);

        SimpleExpression atElement(Expression index1, Expression index2);

        SimpleExpression atElement(Expression index1, Expression index2, Expression index3, Expression... restIndex);

        @Support({H2, PostgreSQL})
        ArrayExpression atArray(int index);

        @Support({PostgreSQL})
        ArrayExpression atArray(int index1, int index2);

        @Support({PostgreSQL})
        ArrayExpression atArray(int index1, int index2, int index3, int... restIndex);

        @Support({PostgreSQL})
        ArrayExpression atArray(ArraySubscript index);

        @Support({PostgreSQL})
        ArrayExpression atArray(ArraySubscript index1, ArraySubscript index2);

        @Support({PostgreSQL})
        ArrayExpression atArray(ArraySubscript index1, ArraySubscript index2, ArraySubscript index3, ArraySubscript... restIndex);

        @Support({PostgreSQL})
        <T> ArrayExpression atArray(BiFunction<MappingType, T, Expression> funcRef, T value);

        @Support({PostgreSQL})
        <T> ArrayExpression atArray(BiFunction<MappingType, T, Expression> funcRef, T value1, T value2);

        @Support({PostgreSQL})
        <T> ArrayExpression atArray(BiFunction<MappingType, T, Expression> funcRef, T value1, T value2, T value3);

        @Support({PostgreSQL})
        <T, U> ArrayExpression atArray(BiFunction<MappingType, T, Expression> funcRef1, T value1, BiFunction<MappingType, U, Expression> funcRef2, U value2);

        @Support({PostgreSQL})
        <T, U, V> ArrayExpression atArray(BiFunction<MappingType, T, Expression> funcRef1, T value1, BiFunction<MappingType, U, Expression> funcRef2, U value2, BiFunction<MappingType, V, Expression> funcRef3, V value3);


    }


    interface _JsonExpOperator {

        @Support({MySQL, PostgreSQL})
        JsonExpression arrayElement(int index);

        @Support({MySQL, PostgreSQL})
        JsonExpression objectAttr(String keyName);

        @Support({MySQL, PostgreSQL})
        JsonExpression atPath(String jsonPath);

        @Support({MySQL, PostgreSQL})
        JsonExpression atPath(Expression jsonPath);

        /**
         * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
         *                <ul>
         *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
         *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
         *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
         *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
         *                    <li>developer custom method</li>
         *                </ul>.
         *                The first argument of funcRef always is {@link io.army.mapping.optional.JsonPathType#INSTANCE}.
         * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
         */
        @Support({MySQL, PostgreSQL})
        <T> JsonExpression atPath(BiFunction<MappingType, T, Expression> funcRef, T value);

    }


}
