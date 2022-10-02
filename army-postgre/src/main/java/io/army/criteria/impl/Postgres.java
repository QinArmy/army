package io.army.criteria.impl;


import io.army.criteria.postgre.PostgreInsert;

import java.util.Objects;

/**
 * <p>
 * This class is Postgre SQL syntax utils.
 * </p>
 *
 * @since 1.0
 */
public abstract class Postgres extends PostgreFuncSyntax {


    /**
     * private constructor
     */
    private Postgres() {
    }


    public static PostgreInsert._PrimaryOptionSpec<Void> singleInsert() {
        return PostgreInserts.primaryInsert(null);
    }

    /**
     * @param criteria non-null criteria instance,java bean or {@link java.util.Map}.
     */
    public static <C> PostgreInsert._PrimaryOptionSpec<C> singleInsert(C criteria) {
        Objects.requireNonNull(criteria);
        return PostgreInserts.primaryInsert(criteria);
    }


}
