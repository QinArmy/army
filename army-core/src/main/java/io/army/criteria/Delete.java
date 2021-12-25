package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Delete extends Statement, SQLDebug {


    interface DeleteSpec {

        Delete asDelete();
    }

    interface DomainDeleteSpec<C> {

        WhereSpec<C> deleteFrom(TableMeta<? extends IDomain> table, String tableAlias);
    }


    interface WhereSpec<C> {

        DeleteSpec where(List<IPredicate> predicateList);

        DeleteSpec where(Function<C, List<IPredicate>> function);

        DeleteSpec where(Supplier<List<IPredicate>> supplier);

        WhereAndSpec<C> where(IPredicate predicate);
    }


    interface WhereAndSpec<C> extends DeleteSpec {

        WhereAndSpec<C> and(IPredicate predicate);

        WhereAndSpec<C> and(Function<C, IPredicate> function);

        WhereAndSpec<C> and(Supplier<IPredicate> supplier);

        /**
         * @see Expression#ifEqual(Object)
         */
        WhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        WhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

        WhereAndSpec<C> ifAnd(Supplier<IPredicate> supplier);

    }

    /*################################## blow batch delete ##################################*/

    interface BatchDomainDeleteSpec<C> {

        BatchWhereSpec<C> deleteFrom(TableMeta<? extends IDomain> table, String tableAlias);
    }


    interface BatchWhereSpec<C> {

        BatchParamSpec<C> where(List<IPredicate> predicateList);

        BatchParamSpec<C> where(Function<C, List<IPredicate>> function);

        BatchParamSpec<C> where(Supplier<List<IPredicate>> supplier);

        BatchWhereAndSpec<C> where(IPredicate predicate);
    }

    interface BatchWhereAndSpec<C> extends BatchParamSpec<C> {

        BatchWhereAndSpec<C> and(IPredicate predicate);

        BatchWhereAndSpec<C> and(Function<C, IPredicate> function);

        BatchWhereAndSpec<C> and(Supplier<IPredicate> supplier);

        /**
         * @see Expression#ifEqual(Object)
         */
        BatchWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        BatchWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

        BatchWhereAndSpec<C> ifAnd(Supplier<IPredicate> supplier);

    }

    interface BatchParamSpec<C> {

        DeleteSpec paramMaps(List<Map<String, Object>> mapList);

        DeleteSpec paramMaps(Function<C, List<Map<String, Object>>> function);

        DeleteSpec paramMaps(Supplier<List<Map<String, Object>>> supplier);

        DeleteSpec paramBeans(List<Object> beanList);

        DeleteSpec paramBeans(Function<C, List<Object>> function);

        DeleteSpec paramBeans(Supplier<List<Object>> supplier);

    }


}
