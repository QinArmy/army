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

    @Deprecated
    interface SQLAble {

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

        FS from(Function<C, SubQuery> function, String subQueryAlia);

        FS from(Supplier<SubQuery> supplier, String subQueryAlia);
    }

    interface OnClause<C, OR> {

        OR on(List<IPredicate> predicateList);

        OR on(IPredicate predicate);

        OR on(IPredicate predicate1, IPredicate predicate2);

        OR on(Function<C, List<IPredicate>> function);

        OR on(Supplier<List<IPredicate>> supplier);

        OR onId();

        OR onParent();
    }


    interface JoinClause<C, JT, JS> {

        JT leftJoin(TableMeta<?> table, String tableAlias);

        JS leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        JS leftJoin(Supplier<SubQuery> supplier, String subQueryAlia);

        JT ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        JS ifLeftJoin(Function<C, SubQuery> function, String subQueryAlia);

        JS ifLeftJoin(Supplier<SubQuery> supplier, String subQueryAlia);

        JT join(TableMeta<?> table, String tableAlias);

        JS join(Function<C, SubQuery> function, String subQueryAlia);

        JS join(Supplier<SubQuery> supplier, String subQueryAlia);

        JT ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        JS ifJoin(Function<C, SubQuery> function, String subQueryAlia);

        JS ifJoin(Supplier<SubQuery> supplier, String subQueryAlia);

        JT rightJoin(TableMeta<?> table, String tableAlias);

        JS rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        JS rightJoin(Supplier<SubQuery> supplier, String subQueryAlia);

        JT ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        JS ifRightJoin(Function<C, SubQuery> function, String subQueryAlia);

        JS ifRightJoin(Supplier<SubQuery> supplier, String subQueryAlia);

    }


    interface WhereClause<C, WR, WA> {

        WR where(List<IPredicate> predicateList);

        WR where(Function<C, List<IPredicate>> function);

        WR where(Supplier<List<IPredicate>> supplier);

        WA where(IPredicate predicate);
    }


    interface WhereAndClause<C, AR> {

        AR and(IPredicate predicate);

        AR and(Supplier<IPredicate> supplier);

        AR and(Function<C, IPredicate> function);

        /**
         * @see Expression#ifEqual(Object)
         */
        AR ifAnd(@Nullable IPredicate predicate);

        AR ifAnd(Supplier<IPredicate> supplier);

        AR ifAnd(Function<C, IPredicate> function);

    }










}
