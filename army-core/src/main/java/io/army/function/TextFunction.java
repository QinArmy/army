package io.army.function;

@FunctionalInterface
public interface TextFunction<T> {

    T apply(String text, int offset, int end);

}
