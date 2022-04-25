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
 * @see Select
 * @see Insert
 * @see Update
 * @see Delete
 * @see SubQuery
 * @since 1.0
 */
public interface Statement {

    /**
     * assert statement prepared
     */
    void prepared();

    boolean isPrepared();


    /**
     * @since 1.0
     */
    String mockAsString(Dialect dialect, Visible visible, boolean beautify);

    /**
     * @since 1.0
     */
    Stmt mockAsStmt(Dialect dialect, Visible visible);


    interface AsClause<AR> {

        AR as(String alias);
    }


    interface BatchParamClause<C, BR> {

        BR paramList(List<?> paramList);

        BR paramList(Supplier<List<?>> supplier);

        BR paramList(Function<C, List<?>> function);

        BR paramList(Function<String, ?> function, String keyName);
    }

    interface RightBracketClause<R> {

        R rightBracket();

    }


    /**
     * <p>
     * This interface representing from clause.
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
     * @param <FB> next clause java type,it's sub interface of {@link LeftBracketClause}.
     * @since 1.0
     */
    interface FromClause<C, FT, FS, FB> {


        FT from(TableMeta<?> table, String tableAlias);

        <T extends TableItem> FS from(Function<C, T> function, String alias);

        <T extends TableItem> FS from(Supplier<T> supplier, String alias);

        FB from();

    }


    /**
     * @since 1.0
     */
    interface OnClause<C, OR> {

        OR on(List<IPredicate> predicateList);

        OR on(IPredicate predicate);

        OR on(IPredicate predicate1, IPredicate predicate2);

        OR on(Function<C, List<IPredicate>> function);

        OR on(Supplier<List<IPredicate>> supplier);

        OR on(Consumer<List<IPredicate>> consumer);

    }


    /**
     * <p>
     * This interface representing join clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer couldn't directly use this interface.
     * </p>
     *
     * @param <C>  criteria object java type
     * @param <JT> next clause java type
     * @param <JS> next clause java type
     * @param <JC> next clause java type
     * @param <JD> next clause java type
     * @param <JE> next clause java type,it's sub interface of {@link LeftBracketClause}.
     * @since 1.0
     */
    interface JoinClause<C, JT, JS, JC, JD, JE> {

        JT leftJoin(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS leftJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS leftJoin(Supplier<T> supplier, String alias);

        JE leftJoin();

        JT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS ifLeftJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS ifLeftJoin(Supplier<T> supplier, String alias);

        JT join(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS join(Function<C, T> function, String alias);

        <T extends TableItem> JS join(Supplier<T> supplier, String alias);

        JE join();

        JT ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS ifJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS ifJoin(Supplier<T> supplier, String alias);

        JT rightJoin(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS rightJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS rightJoin(Supplier<T> supplier, String alias);

        JE rightJoin();

        JT ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS ifRightJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS ifRightJoin(Supplier<T> supplier, String alias);

        JC crossJoin(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JD crossJoin(Function<C, T> function, String alias);

        <T extends TableItem> JD crossJoin(Supplier<T> supplier, String alias);

        JE crossJoin();

        JC ifCrossJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TableItem> JD ifCrossJoin(Function<C, T> function, String alias);

        <T extends TableItem> JD ifCrossJoin(Supplier<T> supplier, String alias);

        JT fullJoin(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS fullJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS fullJoin(Supplier<T> supplier, String alias);

        JE fullJoin();

        JT ifFullJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS ifFullJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS ifFullJoin(Supplier<T> supplier, String alias);

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
     * @param <JT> next clause java type
     * @param <JS> next clause java type
     * @since 1.0
     */
    interface LeftBracketClause<C, JT, JS> {

        LeftBracketClause<C, JT, JS> leftBracket();

        JT leftBracket(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS leftBracket(Function<C, T> function, String alias);

        <T extends TableItem> JS leftBracket(Supplier<T> supplier, String alias);

    }


    interface WhereClause<C, WR, WA> {

        WR where(List<IPredicate> predicateList);

        WR where(Function<C, List<IPredicate>> function);

        WR where(Supplier<List<IPredicate>> supplier);

        WR where(Consumer<List<IPredicate>> consumer);

        WA where(@Nullable IPredicate predicate);
    }


    interface WhereAndClause<C, WA> {

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


}
