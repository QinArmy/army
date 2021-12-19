package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface Delete extends Statement, SQLDebug {


    interface DeleteSpec {

        Delete asDelete();
    }

    interface DomainDeleteSpec<C> {

        WhereSpec<C> deleteFrom(TableMeta<? extends IDomain> tableMeta, String tableAlias);
    }


    interface WhereSpec<C> {

        DeleteSpec where(List<IPredicate> predicateList);

        DeleteSpec where(Function<C, List<IPredicate>> function);

        WhereAndSpec<C> where(IPredicate predicate);
    }


    interface WhereAndSpec<C> extends DeleteSpec {

        WhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see Expression#ifEqual(Object)
         */
        WhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        WhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

    }

    /*################################## blow batch delete ##################################*/

    interface BatchDomainDeleteSpec<C> {

        BatchWhereSpec<C> deleteFrom(TableMeta<? extends IDomain> table, String tableAlias);
    }


    interface BatchWhereSpec<C> {

        BatchParamSpec<C> where(List<IPredicate> predicateList);

        BatchParamSpec<C> where(Function<C, List<IPredicate>> function);

        BatchWhereAndSpec<C> where(IPredicate predicate);
    }

    interface BatchWhereAndSpec<C> extends BatchParamSpec<C> {

        BatchWhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see Expression#ifEqual(Object)
         */
        BatchWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        BatchWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

    }

    interface BatchParamSpec<C> {

        DeleteSpec paramMaps(List<Map<String, Object>> mapList);

        DeleteSpec paramMaps(Function<C, List<Map<String, Object>>> function);

        DeleteSpec paramBeans(List<Object> beanList);

        DeleteSpec paramBeans(Function<C, List<Object>> function);

    }


}
