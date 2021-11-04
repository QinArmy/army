package io.army.domain;


/**
 *
 */
public interface IDomain {

    Object getId();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();

}
