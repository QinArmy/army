package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.FieldMeta;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @param <T> domain java class
 * @see SingleUpdateAble
 */
public interface WhereAbleOfSingleUpdate<T extends IDomain, C1, C2> extends SetAbleOfSingleUpdate<T, C1, C2> {

    OrderAbleOfSingleUpdate<T, C1, C2> where(List<IPredicate> predicateList);

    OrderAbleOfSingleUpdate<T, C1, C2> where(Function<C1, List<IPredicate>> function);

    OrderAbleOfSingleUpdate<T, C1, C2> where(BiFunction<C1, C2, List<IPredicate>> biFunction);

    OrderAbleOfSingleUpdate<T, C1, C2> where(Predicate<C1> predicate, List<IPredicate> predicateList);

    OrderAbleOfSingleUpdate<T, C1, C2> where(BiPredicate<C1, C2> biPredicate, List<IPredicate> predicateList);

    OrderAbleOfSingleUpdate<T, C1, C2> where(Predicate<C1> biPredicate, Function<C1, List<IPredicate>> function);

    OrderAbleOfSingleUpdate<T, C1, C2> where(BiPredicate<C1, C2> biPredicate, BiFunction<C1, C2, List<IPredicate>> biFunction);


    WhereAndAbleOfSingleUpdate<T, C1, C2> where(IPredicate predicate);

    WhereAndAbleOfSingleUpdate<T, C1, C2> where(Predicate<C1> testPredicate, IPredicate predicate);

    WhereAndAbleOfSingleUpdate<T, C1, C2> where(BiPredicate<C1, C2> biPredicate, IPredicate predicate);

    WhereAndAbleOfSingleUpdate<T, C1, C2> where(Predicate<C1> testPredicate, Function<C1, IPredicate> function, boolean predicate);

    WhereAndAbleOfSingleUpdate<T, C1, C2> where(BiPredicate<C1, C2> biPredicate, BiFunction<C1, C2, IPredicate> biFunction, boolean predicate);

}
