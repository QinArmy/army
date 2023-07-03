package io.army.struct;


import io.army.lang.NonNull;

/**
 * @since 1.0
 */
@Deprecated
public interface Compare<T> {


    /**
     * @see CompareResult#resolve(int)
     */
    @NonNull
    CompareResult compare(@NonNull T o);


    interface Comparer {

        boolean equal();

        boolean lessThan();

        boolean lessEqual();

        boolean greatThan();

        boolean greatEqual();

    }

}
