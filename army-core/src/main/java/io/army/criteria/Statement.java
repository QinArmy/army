package io.army.criteria;

import io.army.dialect.Dialect;
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

        <T extends TableItem> FS from(Supplier<T> supplier, String alias);

        <T extends TableItem> FS from(Function<C, T> function, String alias);

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

        <T extends TableItem> JS leftJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS leftJoin(Supplier<T> supplier, String alias);

        JT join(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS join(Function<C, T> function, String alias);

        <T extends TableItem> JS join(Supplier<T> supplier, String alias);

        JT rightJoin(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS rightJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS rightJoin(Supplier<T> supplier, String alias);

        JT fullJoin(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS fullJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS fullJoin(Supplier<T> supplier, String alias);

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

        <T extends TableItem> FS crossJoin(Function<C, T> function, String alias);

        <T extends TableItem> FS crossJoin(Supplier<T> supplier, String alias);

    }


    interface _IfJoinClause<C, FJ> {

        <B extends JoinItemBlock> FJ ifLeftJoin(Supplier<B> supplier);

        <B extends JoinItemBlock> FJ ifLeftJoin(Function<C, B> function);

        <B extends JoinItemBlock> FJ ifJoin(Supplier<B> supplier);

        <B extends JoinItemBlock> FJ ifJoin(Function<C, B> function);

        <B extends JoinItemBlock> FJ ifRightJoin(Supplier<B> supplier);

        <B extends JoinItemBlock> FJ ifRightJoin(Function<C, B> function);

        <B extends JoinItemBlock> FJ ifFullJoin(Supplier<B> supplier);

        <B extends JoinItemBlock> FJ ifFullJoin(Function<C, B> function);

        <B extends CrossItemBlock> FJ ifCrossJoin(Supplier<B> supplier);

        <B extends CrossItemBlock> FJ ifCrossJoin(Function<C, B> function);

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

        LT leftBracket(TableMeta<?> table, String tableAlias);

        <T extends TableItem> LS leftBracket(Supplier<T> supplier, String alias);

        <T extends TableItem> LS leftBracket(Function<C, T> function, String alias);

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
    interface _WhereClause<C, WR, WA> {

        WR where(Supplier<List<IPredicate>> supplier);

        WR where(Function<C, List<IPredicate>> function);

        WR where(Consumer<List<IPredicate>> consumer);

        WA where(IPredicate predicate);

        WA where(Function<Object, IPredicate> operator, DataField operand);

        WA where(Function<Object, IPredicate> operator, Supplier<?> operand);

        WA where(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName);

        WA where(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        WA where(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand, String firstKey, String secondKey);

        WA whereIfNonNull(@Nullable Function<Object, IPredicate> operator, @Nullable Object operand);

        WA whereIfNonNull(@Nullable BiFunction<Object, Object, IPredicate> operator, @Nullable Object firstOperand, @Nullable Object secondOperand);

        WA whereIfNonNull(@Nullable Function<Object, ? extends Expression> firstOperator, @Nullable Object firstOperand
                , BiFunction<Expression, Object, IPredicate> secondOperator, Object secondOperand);

        WA whereIf(Supplier<IPredicate> supplier);

        WA whereIf(Function<C, IPredicate> function);

        WA whereIf(Function<Object, IPredicate> operator, Supplier<?> operand);

        WA whereIf(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName);

        WA whereIf(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        WA whereIf(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand, String firstKey, String secondKey);

        WA whereIf(Function<Object, ? extends Expression> firstOperator, Supplier<?> firstOperand
                , BiFunction<Expression, Object, IPredicate> secondOperator, Object secondOperand);

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
    interface _QueryWhereClause<C, WR, WA> extends _WhereClause<C, WR, WA> {

        WR ifWhere(Supplier<List<IPredicate>> supplier);

        WR ifWhere(Function<C, List<IPredicate>> function);
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
    interface _WhereAndClause<C, WA> {

        WA and(IPredicate predicate);

        WA and(Supplier<IPredicate> supplier);

        WA and(Function<C, IPredicate> function);

        WA and(Function<Object, IPredicate> operator, DataField operand);

        WA and(Function<Object, IPredicate> operator, Supplier<?> operand);

        WA and(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName);

        WA and(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        WA and(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand, String firstKey, String secondKey);

        WA ifNonNullAnd(@Nullable Function<Object, IPredicate> operator, @Nullable Object operand);

        WA ifNonNullAnd(@Nullable BiFunction<Object, Object, IPredicate> operator, @Nullable Object firstOperand, @Nullable Object secondOperand);

        WA ifNonNullAnd(@Nullable Function<Object, ? extends Expression> firstOperator, @Nullable Object firstOperand
                , BiFunction<Expression, Object, IPredicate> secondOperator, Object secondOperand);

        WA ifAnd(Supplier<IPredicate> supplier);

        WA ifAnd(Function<C, IPredicate> function);

        WA ifAnd(Function<Object, IPredicate> operator, Supplier<?> operand);

        WA ifAnd(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName);

        WA ifAnd(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand, Supplier<?> secondOperand);

        WA ifAnd(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand, String firstKey, String secondKey);

        WA ifAnd(Function<Object, ? extends Expression> firstOperator, Supplier<?> firstOperand
                , BiFunction<Expression, Object, IPredicate> secondOperator, Object secondOperand);

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

        <S extends SortItem> OR orderBy(Function<C, List<S>> function);

        <S extends SortItem> OR orderBy(Supplier<List<S>> supplier);

        OR orderBy(Consumer<List<SortItem>> consumer);

        <S extends SortItem> OR ifOrderBy(Supplier<List<S>> supplier);

        <S extends SortItem> OR ifOrderBy(Function<C, List<S>> function);

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
