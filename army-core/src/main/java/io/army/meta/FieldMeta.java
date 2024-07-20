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

package io.army.meta;

import io.army.criteria.TypeTableField;
import io.army.modelgen._MetaBridge;

import io.army.lang.Nullable;
import java.util.List;

/**
 * <p> this interface representing a Java class then tableMeta column mapping.
 *
 * @param <T> representing Domain Java Type
 */
public interface FieldMeta<T> extends TypeTableField<T> {


    boolean primary();

    boolean unique();

    boolean index();


    /**
     * <p>
     * if this field representing {@link _MetaBridge#ID}
     *
     */
    @Nullable
    GeneratorMeta generator();

    @Nullable
    FieldMeta<?> dependField();


    /**
     * (Optional) The columnSize for a decimal (exact numeric)
     * column. (Applies only if a decimal column is used.)
     * Value must be set by developer if used when generating
     * the DDL for the column.
     */
    int precision();

    /**
     * (Optional) The scale for a decimal (exact numeric) column.
     * (Applies only if a decimal column is used.)
     */
    int scale();


    boolean insertable();

    String comment();

    String defaultValue();


    /**
     * <p>
     * If {@link #javaType()} is below type then return element java type,
     * <ul>
     *     <li>{@link java.util.Collection}</li>
     *     <li>{@link java.util.Set }</li>
     *     <li>{@link java.util.List }</li>
     *     <li>{@link java.util.Map}</li>
     * </ul>
     * else return {@code void.class}.
     *
     */
    List<Class<?>> elementTypes();


}
