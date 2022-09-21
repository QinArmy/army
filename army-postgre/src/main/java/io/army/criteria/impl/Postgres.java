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


    public static PostgreInsert._DomainWithCteSpec<Void> domainInsert() {
        throw new UnsupportedOperationException();
    }

    public static <C> PostgreInsert._DomainWithCteSpec<C> domainInsert(C criteria) {
        Objects.requireNonNull(criteria);
        throw new UnsupportedOperationException();
    }


    public static PostgreInsert._DomainSubWithCteSpec<Void> domainSubInsert() {
        throw new UnsupportedOperationException();
    }

    public static <C> PostgreInsert._DomainSubWithCteSpec<C> domainSubInsert(C criteria) {
        Objects.requireNonNull(criteria);
        throw new UnsupportedOperationException();
    }


}
