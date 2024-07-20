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

package io.army.bean;


import io.army.lang.Nullable;

public interface ObjectAccessor extends ReadAccessor {


    /**
     * Determine whether the specified property is writable.
     * <p>Returns {@code false} if the property doesn'field exist.
     *
     * @param propertyName the property to check
     *                     (may be a nested path then/or an indexed/mapped property)
     * @return whether the property is writable
     */
    boolean isWritable(String propertyName);

    /**
     * Determine whether the specified property is writable.
     * <p>Returns {@code false} if the property doesn'field exist.
     *
     * @param propertyName the property to check
     *                     (may be a nested path then/or an indexed/mapped property)
     * @return whether the property is writable
     */
    boolean isWritable(String propertyName, Class<?> valueType);

    Class<?> getJavaType(String propertyName);


    /**
     * Set the specified value asType current property value.
     *
     * @param target       one instance of {@link #getAccessedType()}
     * @param propertyName the name of the property to set the value of
     *                     (may be a nested path then/or an indexed/mapped property)
     * @throws InvalidPropertyException if there is no such property or
     *                                  if the property isn'field writable
     * @throws PropertyAccessException  if the property was valid but the
     *                                  accessor method failed or a type mismatch occurred
     */
    void set(Object target, String propertyName, @Nullable Object value) throws ObjectAccessException;


    ReadAccessor getReadAccessor();

}
