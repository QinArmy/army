package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public interface OrderAbleOfSingleUpdate<T extends IDomain, C1, C2> extends SingleUpdate.LimitAbleOfSingleUpdate<T,C1,C2> {

    OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(Expression<?> orderExp);

    OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy( Function<C1, Expression<?>> function);

    OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy( BiFunction<C1, C2, Expression<?>> function);

    OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(Predicate<C1> testPredicate, Expression<?> orderExp);

    OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(BiPredicate<C1, C2> biPredicate, Expression<?> orderExp);

    OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(Predicate<C1> testPredicate, Function<C1, Expression<?>> function);

    OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(BiPredicate<C1, C2> biPredicate, BiFunction<C1, C2, Expression<?>> biFunction);


    OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(Expression<?> orderExp, @Nullable Boolean asc);

    OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(Function<C1, Expression<?>> function, @Nullable Boolean asc);

    OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy( BiFunction<C1, C2, Expression<?>> function, @Nullable Boolean asc);

    OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(Predicate<C1> testPredicate, Function<C1, Expression<?>> function, @Nullable Boolean asc);

    OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(BiPredicate<C1, C2> biPredicate, BiFunction<C1, C2, Expression<?>> function, BiFunction<C1,C2, Boolean> ascFunction);

    OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(Predicate<C1> testPredicate, Expression<?> orderExp, Function<C1, Boolean> ascFunction);

    OrderItemAbleOfSingleUpdate<T, C1, C2> orderBy(BiPredicate<C1, C2> biPredicate, Expression<?> orderExp, BiFunction<C1,C2, Boolean> ascFunction);
}
