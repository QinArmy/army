/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.record;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public interface DataRecord extends ResultItem, ResultItem.ResultAccessSpec {

    ResultRecordMeta getRecordMeta();

    @Nullable
    Object get(int indexBasedZero);


    Object getNonNull(int indexBasedZero);

    Object getOrDefault(int indexBasedZero, Object defaultValue);

    Object getOrSupplier(int indexBasedZero, Supplier<?> supplier);


    /**
     * <p>java doc see
     * <ul>
     *     <li>{@link ResultRecord#get(int, Class)}</li>
     *     <li>{@link CurrentRecord#get(int, Class)}</li>
     * </ul>
     */
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
