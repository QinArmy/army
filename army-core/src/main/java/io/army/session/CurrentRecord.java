package io.army.session;

import io.army.lang.Nullable;

public interface CurrentRecord {

    @Nullable
    Object get(int indexBasedZero);


    Object getNonNull(int indexBasedZero);

    @Nullable
    <T> T get(int indexBasedZero, Class<T> columnClass);


    <T> T getNonNull(int indexBasedZero, Class<T> columnClass);


    @Nullable
    Object get(String selectionAlias);


    Object getNonNull(String selectionAlias);

    @Nullable
    <T> T get(String selectionAlias, Class<T> columnClass);


    <T> T getNonNull(String selectionAlias, Class<T> columnClass);


}
