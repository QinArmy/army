package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Delete extends SQLStatement, SQLAble, SQLDebug, QueryAble {

    interface DeleteSQLAble extends SQLAble {

    }

    interface DeleteAble extends DeleteSQLAble {

        Delete asDelete();
    }

    interface SingleDeleteAble<C> extends DeleteSQLAble {

        SingleDeleteWhereAble<C> deleteFrom(TableMeta<? extends IDomain> tableMeta, String tableAlias);
    }

    interface SingleDeleteWhereAble<C> extends DeleteSQLAble {

        DeleteAble where(List<IPredicate> predicateList);

        DeleteAble where(Function<C, List<IPredicate>> function);

        SingleDeleteWhereAndAble<C> where(IPredicate predicate);
    }


    interface SingleDeleteWhereAndAble<C> extends DeleteAble {

        SingleDeleteWhereAndAble<C> and(IPredicate predicate);

        SingleDeleteWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        SingleDeleteWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }

}
