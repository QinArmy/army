package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface Delete extends SQLStatement, SQLStatement.SQLAble, SQLDebug, Query {

    interface DeleteSQLAble extends SQLAble {

    }

    interface DeleteAble extends DeleteSQLAble {

        Delete asDelete();
    }

    interface SingleDeleteAble<C> extends DeleteSQLAble {

        SingleDeleteTableRouteAble<C> deleteFrom(TableMeta<? extends IDomain> tableMeta, String tableAlias);
    }

    interface SingleDeleteTableRouteAble<C> extends SingleWhereAble<C> {

        SingleDeleteTableRouteAble<C> route(int databaseIndex, int tableIndex);

        SingleDeleteTableRouteAble<C> route(int tableIndex);
    }

    interface SingleWhereAble<C> extends DeleteSQLAble {

        DeleteAble where(List<IPredicate> predicateList);

        DeleteAble where(Function<C, List<IPredicate>> function);

        SingleWhereAndAble<C> where(IPredicate predicate);
    }


    interface SingleWhereAndAble<C> extends DeleteAble {

        SingleWhereAndAble<C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        SingleWhereAndAble<C> ifAnd(@Nullable IPredicate predicate);

        SingleWhereAndAble<C> ifAnd(Function<C, IPredicate> function);

    }

    /*################################## blow batch delete ##################################*/

    interface BatchDeleteAble<C> extends DeleteSQLAble {

        BatchDeleteTableRouteAble<C> deleteFrom(TableMeta<? extends IDomain> tableMeta, String tableAlias);
    }

    interface BatchDeleteTableRouteAble<C> extends BatchWhereAble<C> {

        BatchWhereAble<C> route(int databaseIndex, int tableIndex);

        BatchWhereAble<C> route(int tableIndex);
    }

    interface BatchWhereAble<C> extends DeleteSQLAble {

        BatchNamedParamAble<C> where(List<IPredicate> predicateList);

        BatchNamedParamAble<C> where(Function<C, List<IPredicate>> function);

        BatchWhereAndAble<C> where(IPredicate predicate);
    }

    interface BatchWhereAndAble<C> extends BatchNamedParamAble<C> {

        BatchWhereAndAble<C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        BatchWhereAndAble<C> ifAnd(@Nullable IPredicate predicate);

        BatchWhereAndAble<C> ifAnd(Function<C, IPredicate> function);

    }

    interface BatchNamedParamAble<C> extends DeleteSQLAble {

        DeleteAble namedParamMaps(List<Map<String, Object>> mapList);

        DeleteAble namedParamMaps(Function<C, List<Map<String, Object>>> function);

        DeleteAble namedParamBeans(List<Object> beanList);

        DeleteAble namedParamBeans(Function<C, List<Object>> function);
    }
}
