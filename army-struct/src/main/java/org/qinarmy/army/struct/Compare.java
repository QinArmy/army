package org.qinarmy.army.struct;

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

        boolean eq();

        boolean lt();

        boolean le();

        boolean gt();

        boolean ge();

    }

}
