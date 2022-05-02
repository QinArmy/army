package io.army.criteria;

import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.stmt.Stmt;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

    String mockAsString(Dialect dialect, Visible visible, boolean beautify);

    Stmt mockAsStmt(Dialect dialect, Visible visible);


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
     * @param <C>  criteria object java type.
     * @param <BR> next clause java type
     * @since 1.0
     */
    interface BatchParamClause<C, BR> {

        BR paramList(List<?> paramList);

        BR paramList(Supplier<List<?>> supplier);

        BR paramList(Function<C, List<?>> function);

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
     * @param <R> next clause java type
     * @since 1.0
     */
    interface _RightBracketClause<RR> extends _Clause {

        RR rightBracket();

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

        <T extends TableItem> FS from(Function<C, T> function, String alias);

        <T extends TableItem> FS from(Supplier<T> supplier, String alias);


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

        OR on(List<IPredicate> predicateList);

        OR on(IPredicate predicate);

        OR on(IPredicate predicate1, IPredicate predicate2);

        OR on(Function<C, List<IPredicate>> function);

        OR on(Supplier<List<IPredicate>> supplier);

        OR on(Consumer<List<IPredicate>> consumer);

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

        JT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS ifLeftJoin(Supplier<T> supplier, String alias);

        <T extends TableItem> JS ifLeftJoin(Function<C, T> function, String alias);

        JT join(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS join(Function<C, T> function, String alias);

        <T extends TableItem> JS join(Supplier<T> supplier, String alias);

        JT ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS ifJoin(Supplier<T> supplier, String alias);

        <T extends TableItem> JS ifJoin(Function<C, T> function, String alias);

        JT rightJoin(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS rightJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS rightJoin(Supplier<T> supplier, String alias);

        JT ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS ifRightJoin(Supplier<T> supplier, String alias);

        <T extends TableItem> JS ifRightJoin(Function<C, T> function, String alias);

        JT fullJoin(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS fullJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS fullJoin(Supplier<T> supplier, String alias);

        JT ifFullJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS ifFullJoin(Supplier<T> supplier, String alias);

        <T extends TableItem> JS ifFullJoin(Function<C, T> function, String alias);

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

        FT ifCrossJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TableItem> FS ifCrossJoin(Function<C, T> function, String alias);

        <T extends TableItem> FS ifCrossJoin(Supplier<T> supplier, String alias);

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
    interface _LeftBracketClause<C, LT, LS> {

        _LeftBracketClause<C, LT, LS> leftBracket();

        LT leftBracket(TableMeta<?> table, String tableAlias);

        <T extends TableItem> LS leftBracket(Function<C, T> function, String alias);

        <T extends TableItem> LS leftBracket(Supplier<T> supplier, String alias);

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

        WA where(@Nullable IPredicate predicate);
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

        /**
         * @see Expression#ifEqual(Supplier)
         */
        WA ifAnd(@Nullable IPredicate predicate);

        WA ifAnd(Supplier<IPredicate> supplier);

        WA ifAnd(Function<C, IPredicate> function);

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

        OR orderBy(Object sortItem);

        OR orderBy(Object sortItem1, Object sortItem2);

        OR orderBy(Object sortItem1, Object sortItem2, Object sortItem3);

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
