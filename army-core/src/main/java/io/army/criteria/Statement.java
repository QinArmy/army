package io.army.criteria;

import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @see Select
 * @see Insert
 * @see Update
 * @see Delete
 * @see SubQuery
 */
public interface Statement {

    /**
     * assert statement prepared
     */
    void prepared();


    interface AsClause<AR> {

        AR as(String alias);
    }


    interface BatchParamClause<C, BR> {

        BR paramMaps(List<Map<String, Object>> mapList);

        BR paramMaps(Supplier<List<Map<String, Object>>> supplier);

        BR paramMaps(Function<C, List<Map<String, Object>>> function);

        BR paramBeans(List<Object> beanList);

        BR paramBeans(Supplier<List<Object>> supplier);

        BR paramBeans(Function<C, List<Object>> function);
    }


    interface FromClause<C, FT, FS> {

        FT from(TableMeta<?> table, String tableAlias);

        <T extends TablePart> FS from(Function<C, T> function, String alias);

        <T extends TablePart> FS from(Supplier<T> supplier, String alias);
    }

    interface OnClause<C, OR> {

        OR on(List<IPredicate> predicateList);

        OR on(IPredicate predicate);

        OR on(IPredicate predicate1, IPredicate predicate2);

        OR on(Function<C, List<IPredicate>> function);

        OR on(Supplier<List<IPredicate>> supplier);

    }


    interface JoinClause<C, JT, JS> {

        JT leftJoin(TableMeta<?> table, String tableAlias);

        <T extends TablePart> JS leftJoin(Function<C, T> function, String alias);

        <T extends TablePart> JS leftJoin(Supplier<T> supplier, String alias);

        JT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TablePart> JS ifLeftJoin(Function<C, T> function, String alias);

        <T extends TablePart> JS ifLeftJoin(Supplier<T> supplier, String alias);

        JT join(TableMeta<?> table, String tableAlias);

        <T extends TablePart> JS join(Function<C, T> function, String alias);

        <T extends TablePart> JS join(Supplier<T> supplier, String alias);

        JT ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TablePart> JS ifJoin(Function<C, T> function, String alias);

        <T extends TablePart> JS ifJoin(Supplier<T> supplier, String alias);

        JT rightJoin(TableMeta<?> table, String tableAlias);

        <T extends TablePart> JS rightJoin(Function<C, T> function, String alias);

        <T extends TablePart> JS rightJoin(Supplier<T> supplier, String alias);

        JT ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TablePart> JS ifRightJoin(Function<C, T> function, String alias);

        <T extends TablePart> JS ifRightJoin(Supplier<T> supplier, String alias);

        JT crossJoin(TableMeta<?> table, String tableAlias);

        <T extends TablePart> JS crossJoin(Function<C, T> function, String alias);

        <T extends TablePart> JS crossJoin(Supplier<T> supplier, String alias);

        JT ifCrossJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TablePart> JS ifCrossJoin(Function<C, T> function, String alias);

        <T extends TablePart> JS ifCrossJoin(Supplier<T> supplier, String alias);

        JT fullJoin(TableMeta<?> table, String tableAlias);

        <T extends TablePart> JS fullJoin(Function<C, T> function, String alias);

        <T extends TablePart> JS fullJoin(Supplier<T> supplier, String alias);

        JT ifFullJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        <T extends TablePart> JS ifFullJoin(Function<C, T> function, String alias);

        <T extends TablePart> JS ifFullJoin(Supplier<T> supplier, String alias);

    }


    interface WhereClause<C, WR, WA> {

        WR where(List<IPredicate> predicateList);

        WR where(Function<C, List<IPredicate>> function);

        WR where(Supplier<List<IPredicate>> supplier);

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
