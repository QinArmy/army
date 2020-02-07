package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface SingleDelete extends SQLAble {


    interface WhereAbleOfSingleDelete<T extends IDomain, C> extends SingleDelete {

        OrderAbleOfSingleDelete<T, C> where(List<IPredicate> predicates);

        OrderAbleOfSingleDelete<T, C> where(Function<C, List<IPredicate>> function);

        WhereAndOfSingleDelete<T, C> where(IPredicate predicate);

        WhereAndOfSingleDelete<T, C> where(Function<C, IPredicate> function, boolean one);


    }

    interface WhereAndOfSingleDelete<T extends IDomain, C> extends OrderAbleOfSingleDelete<T, C> {

        WhereAndOfSingleDelete<T, C> and(IPredicate predicate);

        WhereAndOfSingleDelete<T, C> and(Function<C, IPredicate> function);

        WhereAndOfSingleDelete<T, C> and(Predicate<C> testPredicate, IPredicate predicate);

        WhereAndOfSingleDelete<T, C> and(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }

    interface OrderAbleOfSingleDelete<T extends IDomain, C> extends LimitAbleOfSingleDelete<T, C> {

        OrderItemOfSingleDelete<T, C> orderBy(Expression<?> orderExp);

        OrderItemOfSingleDelete<T, C> orderBy(Expression<?> orderExp, @Nullable Boolean asc);

        OrderItemOfSingleDelete<T, C> orderBy(Function<C, Expression<?>> orderExpFunction);

        OrderItemOfSingleDelete<T, C> orderBy(Function<C, Expression<?>> orderExpFunction, @Nullable Boolean asc);

        OrderItemOfSingleDelete<T, C> orderBy(Function<C, Expression<?>> orderExpFunction
                , Function<C, Boolean> ascFunction);

        OrderItemOfSingleDelete<T, C> orderBy(Expression<?> orderExp, Function<C, Boolean> ascFunction);

        LimitAbleOfSingleDelete<T, C> orderBy(Predicate<C> testPredicate,Expression<?> orderExp,@Nullable Boolean asc);

        LimitAbleOfSingleDelete<T, C> orderBy(Predicate<C> testPredicate,Function<C,Expression<?>> function
                ,@Nullable Boolean asc);

        LimitAbleOfSingleDelete<T, C> orderBy(Predicate<C> testPredicate,Function<C,Expression<?>> function
                ,Function<C,Boolean> ascFunction);

    }

    interface OrderItemOfSingleDelete<T extends IDomain, C> extends LimitAbleOfSingleDelete<T, C> {

        OrderItemOfSingleDelete<T, C> then(Expression<?> orderExp);

        OrderItemOfSingleDelete<T, C> then(Expression<?> orderExp, @Nullable Boolean asc);

        OrderItemOfSingleDelete<T, C> then(Expression<?> orderExp, Function<C, Boolean> ascFunction);


        OrderItemOfSingleDelete<T, C> then(Predicate<C> testPredicate, Expression<?> orderExp
                , @Nullable Boolean asc);

        OrderItemOfSingleDelete<T, C> then(Predicate<C> testPredicate, Expression<?> orderExp
                , Function<C, Boolean> ascFunction);

        OrderItemOfSingleDelete<T, C> then(Predicate<C> testPredicate, Function<C, Expression<?>> function
                , @Nullable Boolean asc);

    }

    interface LimitAbleOfSingleDelete<T extends IDomain, C> extends SingleDeleteAble, SQLBuilder {

        SingleDeleteAble limit(int rowCount);

        SingleDeleteAble limit(Function<C, Integer> function);

        SingleDeleteAble limit(Predicate<C> testPredicate, int rowCount);

        SingleDeleteAble limit(Predicate<C> testPredicate, Function<C, Integer> function);

    }
}
