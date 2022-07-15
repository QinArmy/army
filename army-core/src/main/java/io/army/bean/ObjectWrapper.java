package io.army.bean;

import io.army.lang.Nullable;

public interface ObjectWrapper extends ReadWrapper {

    boolean isWritable(String propertyName);

    void set(String propertyName, @Nullable Object value) throws ObjectAccessException;


    ReadWrapper readonlyWrapper();


}
