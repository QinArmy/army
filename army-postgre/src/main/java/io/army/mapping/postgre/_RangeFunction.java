package io.army.mapping.postgre;

public interface _RangeFunction<T, R> {

    R apply(T lower, boolean includeLower, T upper, boolean includeUpper);


}
