package io.army.criteria.postgre;


import io.army.criteria.Expression;
import io.army.criteria.Selection;

public interface PostgreFuncColExp<E> extends Expression<E>, Selection {

    String columnName();

}
