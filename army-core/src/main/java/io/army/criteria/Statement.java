package io.army.criteria;

import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.stmt.Stmt;

import java.util.List;
import java.util.Map;
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


    @Deprecated
    default String mockAsString(Dialect dialect) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default Stmt mockAsStmt(Dialect dialect) {
        throw new UnsupportedOperationException();
    }


    default String mockAsString(Dialect dialect, Visible visible, boolean beautify) {
        throw new UnsupportedOperationException();
    }

    default Stmt mockAsStmt(Dialect dialect, Visible visible) {
        throw new UnsupportedOperationException();
    }


    interface AsClause<AR> {

        AR as(String alias);
    }


    interface BatchParamClause<C, BR> {

        BR paramMaps(List<Map<String, Object>> mapList);

        BR paramMaps(Supplier<List<Map<String, Object>>> supplier);

        BR paramMaps(Function<C, List<Map<String, Object>>> function);

        BR paramBeans(List<?> beanList);

        BR paramBeans(Supplier<List<?>> supplier);

        BR paramBeans(Function<C, List<?>> function);
    }


    interface FromClause<C, FT, FS> {

        FT from(TableMeta<?> table, String tableAlias);

        <T extends TableItem> FS from(Function<C, T> function, String alias);

        <T extends TableItem> FS from(Supplier<T> supplier, String alias);
    }

    interface OnClause<C, OR> {

        OR on(List<IPredicate> predicateList);

        OR on(IPredicate predicate);

        OR on(IPredicate predicate1, IPredicate predicate2);

        OR on(Function<C, List<IPredicate>> function);

        OR on(Supplier<List<IPredicate>> supplier);

        OR on(Consumer<List<IPredicate>> consumer);

    }


    interface JoinClause<C, JT, JS> {

        JT leftJoin(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS leftJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS leftJoin(Supplier<T> supplier, String alias);

        JT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS ifLeftJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS ifLeftJoin(Supplier<T> supplier, String alias);

        JT join(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS join(Function<C, T> function, String alias);

        <T extends TableItem> JS join(Supplier<T> supplier, String alias);

        JT ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS ifJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS ifJoin(Supplier<T> supplier, String alias);

        JT rightJoin(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS rightJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS rightJoin(Supplier<T> supplier, String alias);

        JT ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS ifRightJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS ifRightJoin(Supplier<T> supplier, String alias);

        JT crossJoin(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS crossJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS crossJoin(Supplier<T> supplier, String alias);

        JT ifCrossJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS ifCrossJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS ifCrossJoin(Supplier<T> supplier, String alias);

        JT fullJoin(TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS fullJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS fullJoin(Supplier<T> supplier, String alias);

        JT ifFullJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TableItem> JS ifFullJoin(Function<C, T> function, String alias);

        <T extends TableItem> JS ifFullJoin(Supplier<T> supplier, String alias);

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
         * @see Expression#ifEqual(Object)
         */
        WA ifAnd(@Nullable IPredicate predicate);

        WA ifAnd(Supplier<IPredicate> supplier);

        WA ifAnd(Function<C, IPredicate> function);

    }


}
