package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public interface OrderAbleOfUpdate<T extends IDomain, C1, C2> extends SingleUpdate.LimitAbleOfUpdate<T,C1,C2> {

    OrderItemAbleOfUpdate<T, C1, C2> orderBy(Expression<?> orderExp);

    OrderItemAbleOfUpdate<T, C1, C2> orderBy(Function<C1, Expression<?>> function);

    OrderItemAbleOfUpdate<T, C1, C2> orderBy(BiFunction<C1, C2, Expression<?>> function);

    OrderItemAbleOfUpdate<T, C1, C2> orderBy(Predicate<C1> testPredicate, Expression<?> orderExp);

    OrderItemAbleOfUpdate<T, C1, C2> orderBy(BiPredicate<C1, C2> biPredicate, Expression<?> orderExp);

    OrderItemAbleOfUpdate<T, C1, C2> orderBy(Predicate<C1> testPredicate, Function<C1, Expression<?>> function);

    OrderItemAbleOfUpdate<T, C1, C2> orderBy(BiPredicate<C1, C2> biPredicate, BiFunction<C1, C2, Expression<?>> biFunction);


    OrderItemAbleOfUpdate<T, C1, C2> orderBy(Expression<?> orderExp, @Nullable Boolean asc);

    OrderItemAbleOfUpdate<T, C1, C2> orderBy(Function<C1, Expression<?>> function, @Nullable Boolean asc);

    OrderItemAbleOfUpdate<T, C1, C2> orderBy(BiFunction<C1, C2, Expression<?>> function, @Nullable Boolean asc);

    OrderItemAbleOfUpdate<T, C1, C2> orderBy(Predicate<C1> testPredicate, Function<C1, Expression<?>> function, @Nullable Boolean asc);

    OrderItemAbleOfUpdate<T, C1, C2> orderBy(BiPredicate<C1, C2> biPredicate, BiFunction<C1, C2, Expression<?>> function, BiFunction<C1,C2, Boolean> ascFunction);

    OrderItemAbleOfUpdate<T, C1, C2> orderBy(Predicate<C1> testPredicate, Expression<?> orderExp, Function<C1, Boolean> ascFunction);

    OrderItemAbleOfUpdate<T, C1, C2> orderBy(BiPredicate<C1, C2> biPredicate, Expression<?> orderExp, BiFunction<C1,C2, Boolean> ascFunction);
}
