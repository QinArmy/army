package io.army.criteria;

import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface DeleteAble extends SQLAble, SQLDebug {

    interface SingleDeleteAble {

    }

    interface FromAble<C> extends SingleDeleteAble {

        WhereAble<C> from(TableMeta<?> tableMeta);
    }

    interface WhereAble< C> extends SingleDeleteAble {

        DeleteAble where(List<IPredicate> predicates);

        DeleteAble where(Function<C, List<IPredicate>> function);

        WhereAndAble< C> where(IPredicate predicate);


    }

    interface WhereAndAble< C> extends DeleteAble,SingleDeleteAble {

        WhereAndAble< C> and(IPredicate predicate);

        WhereAndAble< C> and(Function<C, IPredicate> function);

        WhereAndAble< C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        WhereAndAble< C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);
    }



}
