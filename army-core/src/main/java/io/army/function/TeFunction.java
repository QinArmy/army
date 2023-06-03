package io.army.function;

@FunctionalInterface
public interface TeFunction<T, U, V, R> {

     R apply(T t, U u, V v);


}
