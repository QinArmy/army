package io.army.criteria;

import io.army.dialect.Dialect;
import io.army.function.TePredicate;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.stmt.Stmt;

import java.util.List;
import java.util.function.*;

/**
 * <p>
 * This interface representing sql statement,this interface is base interface of below:
 * <ul>
 *     <li>{@link Select}</li>
 *     <li>{@link Insert}</li>
 *     <li>{@link Update}</li>
 *     <li>{@link Delete}</li>
 *     <li>{@link SubQuery}</li>
 *     <li>{@link Values}</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
public interface Statement {

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
    interface _AsClause<AR> {

        AR as(String alias);
    }

    interface _StaticAsClaus<AR> {

        AR as();
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
     * @param <C>  criteria object java type
     * @param <BR> next clause java type
     * @since 1.0
     */
    interface _BatchParamClause<C, BR> {

        <P> BR paramList(List<P> paramList);

        <P> BR paramList(Supplier<List<P>> supplier);

        <P> BR paramList(Function<C, List<P>> function);

        BR paramList(Function<String, ?> function, String keyName);
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
    interface _RightParenClause<RR> extends _Clause {

        RR rightParen();

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
     * @param <C>  criteria object java type.
     * @param <FT> next clause java type
     * @param <FS> next clause java type
     * @since 1.0
     */
    interface _FromClause<C, FT, FS> {

        FT from(TableMeta<?> table, String tableAlias);

        <T extends TabularItem> FS from(Supplier<T> supplier, String alias);

        <T extends TabularItem> FS from(Function<C, T> function, String alias);

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
     * @param <C>  criteria object java type.
     * @param <OR> next clause java type
     * @since 1.0
     */
    interface _OnClause<C, OR> {

        OR on(IPredicate predicate);

        OR on(IPredicate predicate1, IPredicate predicate2);

        OR on(Function<Object, IPredicate> operator, DataField operandField);

        OR on(Function<Object, IPredicate> operator1, DataField operandField1
                , Function<Object, IPredicate> operator2, DataField operandField2);

        OR on(Consumer<Consumer<IPredicate>> consumer);

        OR on(BiConsumer<C, Consumer<IPredicate>> consumer);

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
     * @param <C>  criteria object java type
     * @param <JT> next clause java type,it's sub interface of {@link _OnClause}
     * @param <JS> next clause java type,it's sub interface of {@link _OnClause}
     * @see _CrossJoinClause
     * @since 1.0
     */
    interface _JoinClause<C, JT, JS> {

        JT leftJoin(TableMeta<?> table, String tableAlias);

        <T extends TabularItem> JS leftJoin(Function<C, T> function, String alias);

        <T extends TabularItem> JS leftJoin(Supplier<T> supplier, String alias);

        JT join(TableMeta<?> table, String tableAlias);

        <T extends TabularItem> JS join(Function<C, T> function, String alias);

        <T extends TabularItem> JS join(Supplier<T> supplier, String alias);

        JT rightJoin(TableMeta<?> table, String tableAlias);

        <T extends TabularItem> JS rightJoin(Function<C, T> function, String alias);

        <T extends TabularItem> JS rightJoin(Supplier<T> supplier, String alias);

        JT fullJoin(TableMeta<?> table, String tableAlias);

        <T extends TabularItem> JS fullJoin(Function<C, T> function, String alias);

        <T extends TabularItem> JS fullJoin(Supplier<T> supplier, String alias);

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
     * @param <C>  criteria object java type
     * @param <FT> same with the FT of {@link _FromClause}
     * @param <FS> same with the FS of {@link _FromClause}
     * @since 1.0
     */
    interface _CrossJoinClause<C, FT, FS> {

        FT crossJoin(TableMeta<?> table, String tableAlias);

        <T extends TabularItem> FS crossJoin(Function<C, T> function, String alias);

        <T extends TabularItem> FS crossJoin(Supplier<T> supplier, String alias);

    }


    interface _IfJoinClause<C, FJ> {

        <B extends JoinItemBlock<C>> FJ ifLeftJoin(Supplier<B> supplier);

        <B extends JoinItemBlock<C>> FJ ifLeftJoin(Function<C, B> function);

        <B extends JoinItemBlock<C>> FJ ifJoin(Supplier<B> supplier);

        <B extends JoinItemBlock<C>> FJ ifJoin(Function<C, B> function);

        <B extends JoinItemBlock<C>> FJ ifRightJoin(Supplier<B> supplier);

        <B extends JoinItemBlock<C>> FJ ifRightJoin(Function<C, B> function);

        <B extends JoinItemBlock<C>> FJ ifFullJoin(Supplier<B> supplier);

        <B extends JoinItemBlock<C>> FJ ifFullJoin(Function<C, B> function);

        <B extends ItemBlock<C>> FJ ifCrossJoin(Supplier<B> supplier);

        <B extends ItemBlock<C>> FJ ifCrossJoin(Function<C, B> function);

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
     * @param <C>  criteria object java type
     * @param <LT> next clause java type
     * @param <LS> next clause java type
     * @since 1.0
     */
    interface _LeftParenClause<C, LT, LS> {

        LT leftParen(TableMeta<?> table, String tableAlias);

        <T extends TabularItem> LS leftParen(Supplier<T> supplier, String alias);

        <T extends TabularItem> LS leftParen(Function<C, T> function, String alias);

    }

    interface _MinWhereClause<C, WR, WA> {

        WR where(Consumer<Consumer<IPredicate>> consumer);

        WR where(BiConsumer<C, Consumer<IPredicate>> consumer);

        WA where(IPredicate predicate);

        WA where(Supplier<IPredicate> supplier);

        WA where(Function<C, IPredicate> function);

        WA whereIf(Supplier<IPredicate> supplier);

        WA whereIf(Function<C, IPredicate> function);
    }

    interface _MinQueryWhereClause<C, WR, WA> extends _MinWhereClause<C, WR, WA> {

        WR ifWhere(Consumer<Consumer<IPredicate>> consumer);

        WR ifWhere(BiConsumer<C, Consumer<IPredicate>> consumer);
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
    interface _WhereClause<C, WR, WA> extends _MinWhereClause<C, WR, WA> {

        WA where(Function<Expression, IPredicate> expOperator, Expression operand);

        WA where(Function<Expression, IPredicate> expOperator, Supplier<Expression> supplier);

        WA where(Function<Expression, IPredicate> expOperator, Function<C, Expression> function);

        <T> WA where(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, T operand);

        <T> WA where(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

        WA where(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

        <T> WA where(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, T first, T second);

        <T> WA where(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier);

        WA where(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, String secondKey);

        WA whereIf(Function<Expression, IPredicate> expOperator, Supplier<Expression> supplier);

        WA whereIf(Function<Expression, IPredicate> expOperator, Function<C, Expression> function);

        <T> WA whereIf(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, T operand);

        <T> WA whereIf(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

        WA whereIf(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

        <T> WA whereIf(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, T first, T second);

        <T> WA whereIf(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier);

        WA whereIf(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, String secondKey);


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
    interface _QueryWhereClause<C, WR, WA> extends _WhereClause<C, WR, WA>, _MinQueryWhereClause<C, WR, WA> {

    }


    interface _MinWhereAndClause<C, WA> {

        WA and(IPredicate predicate);

        WA and(Supplier<IPredicate> supplier);

        WA and(Function<C, IPredicate> function);

        WA ifAnd(Supplier<IPredicate> supplier);

        WA ifAnd(Function<C, IPredicate> function);
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
    interface _WhereAndClause<C, WA> extends _MinWhereAndClause<C, WA> {

        WA and(Function<Expression, IPredicate> expOperator, Expression operand);

        WA and(Function<Expression, IPredicate> expOperator, Supplier<Expression> supplier);

        WA and(Function<Expression, IPredicate> expOperator, Function<C, Expression> function);

        <T> WA and(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, T operand);

        <T> WA and(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

        WA and(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

        <T> WA and(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, T first, T second);

        <T> WA and(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier);

        WA and(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, String secondKey);

        WA ifAnd(Function<Expression, IPredicate> expOperator, Supplier<Expression> supplier);

        WA ifAnd(Function<Expression, IPredicate> expOperator, Function<C, Expression> function);

        <T> WA ifAnd(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, @Nullable T operand);

        <T> WA ifAnd(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

        WA ifAnd(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

        <T> WA ifAnd(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, @Nullable T first, @Nullable T second);

        <T> WA ifAnd(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier);

        WA ifAnd(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, String secondKey);


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
    interface _OrderByClause<C, OR> {

        OR orderBy(SortItem sortItem);

        OR orderBy(SortItem sortItem1, SortItem sortItem2);

        OR orderBy(SortItem sortItem1, SortItem sortItem2, SortItem sortItem3);

        OR orderBy(Consumer<Consumer<SortItem>> consumer);

        OR orderBy(BiConsumer<C, Consumer<SortItem>> consumer);

        OR ifOrderBy(Consumer<Consumer<SortItem>> consumer);

        OR ifOrderBy(BiConsumer<C, Consumer<SortItem>> consumer);

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
    interface _RowCountLimitClause<C, LR> {

        LR limit(long rowCount);

        LR limit(Supplier<? extends Number> supplier);

        LR limit(Function<C, ? extends Number> function);

        LR limit(Function<String, ?> function, String keyName);

        LR ifLimit(Supplier<? extends Number> supplier);

        LR ifLimit(Function<C, ? extends Number> function);

        LR ifLimit(Function<String, ?> function, String keyName);

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

    interface _LeftParenStringDualClause<PR> {

        Statement._RightParenClause<PR> leftParen(String string);

        _CommaStringDualSpec<PR> leftParen(String string1, String string2);


    }

    interface _LeftParenStringDynamicClause<C, RR> {

        Statement._RightParenClause<RR> leftParen(Consumer<Consumer<String>> consumer);

        Statement._RightParenClause<RR> leftParen(BiConsumer<C, Consumer<String>> consumer);
    }

    interface _LeftParenStringDynamicOptionalClause<C, RR> {

        Statement._RightParenClause<RR> leftParenIf(Consumer<Consumer<String>> consumer);

        Statement._RightParenClause<RR> leftParenIf(BiConsumer<C, Consumer<String>> consumer);
    }


    interface _LeftParenStringDualSpec<C, RR> extends _LeftParenStringDualClause<RR>, _LeftParenStringDynamicClause<C, RR> {

    }

    interface _LeftParenStringDualOptionalSpec<C, RR> extends _LeftParenStringDualSpec<C, RR>
            , _LeftParenStringDynamicOptionalClause<C, RR> {

    }


    interface _LeftParenStringQuadraSpec<C, RR> extends _LeftParenStringDualSpec<C, RR> {

        _CommaStringQuadraSpec<RR> leftParen(String string1, String string2, String string3, String string4);

    }

    interface _LeftParenStringQuadraOptionalSpec<C, RR> extends _LeftParenStringQuadraSpec<C, RR>
            , _LeftParenStringDynamicOptionalClause<C, RR> {


    }


    /**
     * <p>
     * This interface representing any sql clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _Clause {


    }


}
