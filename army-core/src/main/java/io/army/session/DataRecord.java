package io.army.session;

import io.army.lang.Nullable;

import java.util.function.Supplier;

public interface DataRecord extends ResultItem, ResultItem.ResultAccessSpec {

    ResultRecordMeta getRecordMeta();


    @Nullable
    Object get(int indexBasedZero);


    Object getNonNull(int indexBasedZero);

    Object getOrDefault(int indexBasedZero, Object defaultValue);

    Object getOrSupplier(int indexBasedZero, Supplier<?> supplier);


    @Nullable
    <T> T get(int indexBasedZero, Class<T> columnClass);


    <T> T getNonNull(int indexBasedZero, Class<T> columnClass);

    <T> T getOrDefault(int indexBasedZero, Class<T> columnClass, T defaultValue);

    <T> T getOrSupplier(int indexBasedZero, Class<T> columnClass, Supplier<T> supplier);


    @Nullable
    Object get(String selectionLabel);


    Object getNonNull(String selectionLabel);

    Object getOrDefault(String selectionLabel, Object defaultValue);

    Object getOrSupplier(String selectionLabel, Supplier<?> supplier);

    @Nullable
    <T> T get(String selectionLabel, Class<T> columnClass);


    <T> T getNonNull(String selectionLabel, Class<T> columnClass);


    <T> T getOrDefault(String selectionLabel, Class<T> columnClass, T defaultValue);

    <T> T getOrSupplier(String selectionLabel, Class<T> columnClass, Supplier<T> supplier);

}
