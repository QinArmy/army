package io.army.struct;


import javax.annotation.Nonnull;

/**
 * @since 1.0
 */
@Deprecated
public interface Compare<T> {


    /**
     * @see CompareResult#resolve(int)
     */
    @Nonnull
    CompareResult compare(@Nonnull T o);


    interface Comparer {

        boolean equal();

        boolean lessThan();

        boolean lessEqual();

        boolean greatThan();

        boolean greatEqual();

    }

}
