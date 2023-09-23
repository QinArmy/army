package io.army.session;

import io.army.lang.Nullable;

import java.util.function.Supplier;

public interface CurrentRecord {

    /**
     * Returns the number of row
     *
     * @return the number of row
     */
    int getColumnCount();

    /**
     * Get column label of appropriate column
     *
     * @param indexBasedZero index based zero,the first value is 0 .
     * @return the suggested column title              .
     * @throws IllegalArgumentException throw when indexBasedZero error
     */
    String getColumnLabel(int indexBasedZero) throws IllegalArgumentException;


    /**
     * <p>
     * Get column index , if columnLabel duplication ,then return last index that have same columnLabel.
     * <br/>
     *
     * @param columnLabel column label
     * @return index based zero,the first value is 0 .
     * @throws IllegalArgumentException throw when indexBasedZero error
     */
    int getColumnIndex(String columnLabel) throws IllegalArgumentException;

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
