package io.army.domain;


/**
 *
 */
public interface IDomain {


    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();

}
