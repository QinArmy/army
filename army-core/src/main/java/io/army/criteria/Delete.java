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

        NoJoinFromAble<C> delete();
    }

    interface MultiDeleteAble<C> extends DeleteSQLAble {

        FromAble<C> delete();
    }

    interface FromAble<C> extends DeleteSQLAble {

        JoinAble<C> from(TableMeta<?> tableMeta, String tableAlias);
    }

    interface NoJoinFromAble<C> extends DeleteSQLAble {

        WhereAble<C> from(TableMeta<?> tableMeta);
    }

    interface JoinAble<C> extends WhereAble<C> {

        OnAble<C> leftJoin(TableAble tableAble, String tableAlias);

        OnAble<C> join(TableAble tableAble, String tableAlias);

        OnAble<C> rightJoin(TableAble tableAble, String tableAlias);
    }

    interface OnAble<C> extends DeleteSQLAble {

        JoinAble<C> on(List<IPredicate> predicateList);

        JoinAble<C> on(IPredicate predicate);

        JoinAble<C> on(Function<C, List<IPredicate>> function);
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
