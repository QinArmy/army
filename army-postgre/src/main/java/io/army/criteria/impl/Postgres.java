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


    public static PostgreInsert._ValueOptionSpec<Void> valueInsert() {
        throw new UnsupportedOperationException();
    }

    public static <C> PostgreInsert._ValueOptionSpec<C> valueInsert(C criteria) {
        Objects.requireNonNull(criteria);
        throw new UnsupportedOperationException();
    }

    public static PostgreInsert._ValueSubOptionSpec<Void> valueSubInsert() {
        throw new UnsupportedOperationException();
    }

    public static <C> PostgreInsert._ValueSubOptionSpec<C> valueSubInsert(C criteria) {
        Objects.requireNonNull(criteria);
        throw new UnsupportedOperationException();
    }

    public static PostgreInsert._QueryOptionSpec<Void> queryInsert() {
        throw new UnsupportedOperationException();
    }

    public static <C> PostgreInsert._QueryOptionSpec<C> queryInsert(C criteria) {
        Objects.requireNonNull(criteria);
        throw new UnsupportedOperationException();
    }

    public static PostgreInsert._QuerySubOptionSpec<Void> querySubInsert() {
        throw new UnsupportedOperationException();
    }

    public static <C> PostgreInsert._QuerySubOptionSpec<C> querySubInsert(C criteria) {
        Objects.requireNonNull(criteria);
        throw new UnsupportedOperationException();
    }


}
