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


    private Postgres() {
    }


    public static PostgreInsert._DomainOptionSpec<Void> domainInsert() {
        throw new UnsupportedOperationException();
    }

    public static <C> PostgreInsert._DomainOptionSpec<C> domainInsert(C criteria) {
        Objects.requireNonNull(criteria);
        throw new UnsupportedOperationException();
    }


    public static PostgreInsert._DomainSubOptionSpec<Void> domainSubInsert() {
        throw new UnsupportedOperationException();
    }

    public static <C> PostgreInsert._DomainSubOptionSpec<C> domainSubInsert(C criteria) {
        Objects.requireNonNull(criteria);
        throw new UnsupportedOperationException();
    }


}
