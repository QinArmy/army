package io.army.criteria;

import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Delete extends SQLAble, SQLDebug, QueryAble {

    interface DeleteSQLAble extends SQLAble {

    }

    interface DeleteAble extends DeleteSQLAble {

        Delete asDelete();
    }

    interface SingleDeleteAble<C> extends DeleteSQLAble {

        FromAble<C> delete();
    }

    interface FromAble<C> extends DeleteSQLAble {

        WhereAble<C> from(TableMeta<?> tableMeta);
    }

    interface WhereAble<C> extends DeleteSQLAble {

        DeleteAble where(List<IPredicate> predicateList);

        DeleteAble where(Function<C, List<IPredicate>> function);

        WhereAndAble<C> where(IPredicate predicate);

    }

    interface WhereAndAble<C> extends DeleteAble {

        WhereAndAble<C> and(IPredicate predicate);

        WhereAndAble<C> and(Function<C, IPredicate> function);

        WhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        WhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);
    }


}
