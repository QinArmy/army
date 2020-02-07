package io.army.criteria;

import io.army.domain.IDomain;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public interface WhereAndAbleOfSingleUpdate<T extends IDomain, C1, C2> extends OrderAbleOfSingleUpdate<T, C1, C2> {

    WhereAndAbleOfSingleUpdate<T, C1, C2> and(IPredicate predicate);

    WhereAndAbleOfSingleUpdate<T, C1, C2> and( Function<C1, IPredicate> function);

    WhereAndAbleOfSingleUpdate<T, C1, C2> and( BiFunction<C1, C2, IPredicate> biFunction);

    WhereAndAbleOfSingleUpdate<T, C1, C2> and(Predicate<C1> testPredicate, IPredicate predicate);

    WhereAndAbleOfSingleUpdate<T, C1, C2> and(BiPredicate<C1, C2> biPredicate, IPredicate predicate);

    WhereAndAbleOfSingleUpdate<T, C1, C2> and(Predicate<C1> testPredicate, Function<C1, IPredicate> function);

    WhereAndAbleOfSingleUpdate<T, C1, C2> and(BiPredicate<C1, C2> biPredicate, BiFunction<C1, C2, IPredicate> biFunction);
}
