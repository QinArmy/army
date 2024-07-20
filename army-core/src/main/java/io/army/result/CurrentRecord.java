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

package io.army.result;


import io.army.lang.Nullable;
import io.army.mapping.MappingType;

import java.util.function.Supplier;

public interface CurrentRecord extends DataRecord {

    /**
     * row number of current row
     *
     * @return the row number of current row, based 1 . The first value is 1 .
     */
    long rowNumber();

    /**
     * <p>
     * Create one {@link ResultRecord} with coping all column data.
     * <br/>
     *
     * @return new {@link ResultRecord}
     */
    ResultRecord asResultRecord();


    /**
     * <p>found compatible {@link MappingType} for columnClass
     */
    @Nullable
    @Override
    <T> T get(int indexBasedZero, Class<T> columnClass);


    @Nullable
    Object get(int indexBasedZero, MappingType type);

    @Nullable
    <T> T get(int indexBasedZero, Class<T> columnClass, MappingType type);

    <T> T getNonNull(int indexBasedZero, Class<T> columnClass, MappingType type);

    <T> T getOrDefault(int indexBasedZero, Class<T> columnClass, MappingType type, T defaultValue);

    <T> T getOrSupplier(int indexBasedZero, Class<T> columnClass, MappingType type, Supplier<T> supplier);


}
