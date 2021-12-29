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


    interface SelectClause<C, FC> {

        <S extends SelectPart> FC select(Function<C, Hint> hints, List<SQLModifier> modifiers, Function<C, List<S>> function);

        <S extends SelectPart> FC select(List<SQLModifier> modifiers, Function<C, List<S>> function);

        <S extends SelectPart> FC select(List<SQLModifier> modifiers, Supplier<List<S>> supplier);

        <S extends SelectPart> FC select(Function<C, List<S>> function);

        <S extends SelectPart> FC select(Supplier<List<S>> supplier);

        FC select(SelectPart selectPart);

        FC select(SelectPart selectPart1, SelectPart selectPart2);

        FC select(SelectPart selectPart1, SelectPart selectPart2, SelectPart selectPart3);

        <S extends SelectPart> FC select(List<SQLModifier> modifiers, List<S> selectPartList);

        <S extends SelectPart> FC select(List<S> selectPartList);

    }

    interface FromClause<C, FT, FS> {

        FT from(TableMeta<?> table, String tableAlias);

        FS from(Function<C, SubQuery> function, String subQueryAlia);

        FS from(Supplier<SubQuery> supplier, String subQueryAlia);
    }

    interface OnClause<C, O> {

        O on(List<IPredicate> predicateList);

        O on(IPredicate predicate);

        O on(IPredicate predicate1, IPredicate predicate2);

        O on(Function<C, List<IPredicate>> function);

        O on(Supplier<List<IPredicate>> supplier);

        O onId();

        O onParent();
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


    interface GroupClause<C, G> {

        G groupBy(SortPart sortPart);

        G groupBy(SortPart sortPart1, SortPart sortPart2);

        G groupBy(List<SortPart> sortPartList);

        G groupBy(Function<C, List<SortPart>> function);

        G groupBy(Supplier<List<SortPart>> supplier);

        G ifGroupBy(@Nullable SortPart sortPart);

        G ifGroupBy(Supplier<List<SortPart>> supplier);

        G ifGroupBy(Function<C, List<SortPart>> function);
    }


    interface HavingClause<C, H> {

        H having(IPredicate predicate);

        H having(IPredicate predicate1, IPredicate predicate2);

        H having(List<IPredicate> predicateList);

        H having(Supplier<List<IPredicate>> supplier);

        H having(Function<C, List<IPredicate>> function);

        H ifHaving(@Nullable IPredicate predicate);

        H ifHaving(Supplier<List<IPredicate>> supplier);

        H ifHaving(Function<C, List<IPredicate>> function);

    }

    interface LockClause<C, LC> {

        LC lock(SQLModifier lockMode);

        LC lock(Function<C, SQLModifier> function);

        LC ifLock(@Nullable SQLModifier lockMode);

        LC ifLock(Supplier<SQLModifier> supplier);

        LC ifLock(Function<C, SQLModifier> function);
    }


    interface UnionClause<C, U, SP, Q extends Query> {

        U bracketsQuery();

        U union(Function<C, Q> function);

        U union(Supplier<Q> supplier);

        SP union();

        SP unionAll();

        SP unionDistinct();

        U unionAll(Function<C, Q> function);

        U unionDistinct(Function<C, Q> function);

        U unionAll(Supplier<Q> supplier);

        U unionDistinct(Supplier<Q> supplier);


    }


    interface LimitClause<C, L> {

        L limit(long rowCount);

        L limit(long offset, long rowCount);


        L limit(Function<C, LimitOption> function);

        L limit(Supplier<LimitOption> supplier);

        L ifLimit(Function<C, LimitOption> function);

        L ifLimit(Supplier<LimitOption> supplier);

    }


    interface OrderByClause<C, O> {

        O orderBy(SortPart sortPart);

        O orderBy(SortPart sortPart1, SortPart sortPart2);

        O orderBy(List<SortPart> sortPartList);

        O orderBy(Function<C, List<SortPart>> function);

        O orderBy(Supplier<List<SortPart>> supplier);

        O ifOrderBy(@Nullable SortPart sortPart);

        O ifOrderBy(Supplier<List<SortPart>> supplier);

        O ifOrderBy(Function<C, List<SortPart>> function);

    }


}
