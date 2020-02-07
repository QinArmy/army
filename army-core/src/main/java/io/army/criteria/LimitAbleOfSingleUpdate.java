package io.army.criteria;

import io.army.dialect.func.Func;
import io.army.domain.IDomain;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public interface LimitAbleOfSingleUpdate<T extends IDomain, C1, C2> extends SingleUpdateAble,SQLBuilder {

    SingleUpdateAble limit(int rowCount);

    SingleUpdateAble limit(Function<C1,Integer> function);

    SingleUpdateAble limit(BiFunction<C1,C2,Integer> function);

    SingleUpdateAble limit(Predicate<C1> predicate,int rowCount);

    SingleUpdateAble limit(BiPredicate<C1,C2> predicate, int rowCount);

    SingleUpdateAble limit(Predicate<C1> predicate, Function<C1,Integer> function);

    SingleUpdateAble limit(BiPredicate<C1,C2> predicate, BiFunction<C1,C2,Integer> biFunction);
}
