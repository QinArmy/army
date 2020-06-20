package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Delete extends SQLStatement, SQLAble, SQLDebug, QueryAble {

    interface DeleteSQLAble extends SQLAble {

    }

    interface DeleteAble extends DeleteSQLAble {

        Delete asDelete();
    }

    interface SingleDeleteAble<C> extends DeleteSQLAble {

        SingleWhereAble<C> deleteFrom(TableMeta<? extends IDomain> tableMeta, String tableAlias);
    }

    interface SingleWhereAble<C> extends DeleteSQLAble {

        DeleteAble where(List<IPredicate> predicateList);

        DeleteAble where(Function<C, List<IPredicate>> function);

        SingleWhereAndAble<C> where(IPredicate predicate);
    }


    interface SingleWhereAndAble<C> extends DeleteAble {

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        SingleWhereAndAble<C> and(@Nullable IPredicate predicate);

        SingleWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }

    /*################################## blow batch delete ##################################*/

    interface BatchDeleteAble<C> extends DeleteSQLAble {

        BatchWhereAble<C> deleteFrom(TableMeta<? extends IDomain> tableMeta, String tableAlias);
    }

    interface BatchWhereAble<C> extends DeleteSQLAble {

        BatchNamedParamAble<C> where(List<IPredicate> predicateList);

        BatchNamedParamAble<C> where(Function<C, List<IPredicate>> function);

        BatchWhereAndAble<C> where(IPredicate predicate);
    }

    interface BatchWhereAndAble<C> extends BatchNamedParamAble<C> {

        BatchWhereAndAble<C> and(IPredicate predicate);

        BatchWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        BatchWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }

    interface BatchNamedParamAble<C> extends DeleteSQLAble {

        DeleteAble namedParamMaps(Collection<Map<String, Object>> mapCollection);

        DeleteAble namedParamMaps(Function<C, Collection<Map<String, Object>>> function);

        DeleteAble namedParamBeans(Collection<Object> beanCollection);

        DeleteAble namedParamBeans(Function<C, Collection<Object>> function);
    }
}
