package io.army.dialect.func;

import io.army.criteria.Expression;
import io.army.dialect.DataBase;

public interface SQLFunc<E> extends Expression<E> {

    DataBase database();

    String funcName();

    boolean hasArguments();



}
