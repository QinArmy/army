package io.army.dialect.func;

public interface SQLFuncDescribe {

    String name();

    boolean hasArguments();

    Class<?> returnJavaType();

}
