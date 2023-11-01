package io.army.session.record;

import javax.annotation.Nullable;
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

    /*-------------------below label methods -------------------*/

    @Nullable
    Object get(String columnLabel);


    Object getNonNull(String columnLabel);

    Object getOrDefault(String columnLabel, Object defaultValue);

    Object getOrSupplier(String columnLabel, Supplier<?> supplier);

    @Nullable
    <T> T get(String columnLabel, Class<T> columnClass);


    <T> T getNonNull(String columnLabel, Class<T> columnClass);


    <T> T getOrDefault(String columnLabel, Class<T> columnClass, T defaultValue);

    <T> T getOrSupplier(String columnLabel, Class<T> columnClass, Supplier<T> supplier);


}
