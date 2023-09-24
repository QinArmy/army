package io.army.function;

@FunctionalInterface
public interface IntBiFunction<T, R> {

    R apply(int i, T t);

}
