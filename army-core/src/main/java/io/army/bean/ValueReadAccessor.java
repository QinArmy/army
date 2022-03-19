package io.army.bean;

import io.army.lang.Nullable;

import java.lang.reflect.InvocationTargetException;

@FunctionalInterface
interface ValueReadAccessor {

    @Nullable
    Object get(Object bean)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;

}
