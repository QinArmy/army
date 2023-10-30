package io.army.bean;

import javax.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;

@FunctionalInterface
interface ValueWriteAccessor {

    void set(Object bean, @Nullable Object value)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;

}
