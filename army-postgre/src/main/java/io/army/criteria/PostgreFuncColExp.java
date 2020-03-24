package io.army.criteria;


public interface PostgreFuncColExp<E> extends Expression<E>, Selection {

    String columnName();

}
