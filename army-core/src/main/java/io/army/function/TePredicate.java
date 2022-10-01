package io.army.function;

import io.army.criteria.IPredicate;

@FunctionalInterface
public interface TePredicate<F, S, T> {

    IPredicate apply(F f, S s, T t);

}
