package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface Delete extends SQLStatement, SQLDebug {

    interface DeleteSQLSpec {

    }

    interface DeleteSpec extends DeleteSQLSpec {

        Delete asDelete();
    }

    interface SingleDeleteSpec<C> extends DeleteSQLSpec {

        SingleDeleteTableRouteSpec<C> deleteFrom(TableMeta<? extends IDomain> tableMeta, String tableAlias);
    }

    interface SingleDeleteTableRouteSpec<C> extends SingleDeleteWhereSpec<C> {

        SingleDeleteTableRouteSpec<C> route(int databaseIndex, int tableIndex);

        SingleDeleteTableRouteSpec<C> route(int tableIndex);
    }

    interface SingleDeleteWhereSpec<C> extends DeleteSQLSpec {

        DeleteSpec where(List<IPredicate> predicateList);

        DeleteSpec where(Function<C, List<IPredicate>> function);

        SingleDeleteWhereAndSpec<C> where(IPredicate predicate);
    }


    interface SingleDeleteWhereAndSpec<C> extends DeleteSpec {

        SingleDeleteWhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        SingleDeleteWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        SingleDeleteWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

    }

    /*################################## blow batch delete ##################################*/

    interface BatchSingleDeleteSpec<C> extends DeleteSQLSpec {

        BatchSingleDeleteWhereSpec<C> deleteFrom(TableMeta<? extends IDomain> tableMeta, String tableAlias);
    }


    interface BatchSingleDeleteWhereSpec<C> extends DeleteSQLSpec {

        BatchSingleDeleteNamedParamSpec<C> where(List<IPredicate> predicateList);

        BatchSingleDeleteNamedParamSpec<C> where(Function<C, List<IPredicate>> function);

        BatchSingleDeleteWhereAndSpec<C> where(IPredicate predicate);
    }

    interface BatchSingleDeleteWhereAndSpec<C> extends BatchSingleDeleteNamedParamSpec<C> {

        BatchSingleDeleteWhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        BatchSingleDeleteWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        BatchSingleDeleteWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

    }

    interface BatchSingleDeleteNamedParamSpec<C> extends DeleteSQLSpec {

        DeleteSpec namedParamMaps(List<Map<String, Object>> mapList);

        DeleteSpec namedParamMaps(Function<C, List<Map<String, Object>>> function);

        DeleteSpec namedParamBeans(List<Object> beanList);

        DeleteSpec namedParamBeans(Function<C, List<Object>> function);
    }
}
