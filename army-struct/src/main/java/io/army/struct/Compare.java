package io.army.struct;

import org.springframework.lang.NonNull;

/**
 * created  on 2019-02-23.
 */
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
