package io.army.dialect.func;

import io.army.criteria.Expression;
import io.army.dialect.SQLDialect;

public interface SQLFunc<E> extends Expression<E> {

    SQLDialect database();

    String funcName();

    boolean hasArguments();



}
