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

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

class BeanReadAccessor implements ReadAccessor {

    final BeanAccessors accessors;

    BeanReadAccessor(BeanAccessors accessors) {
        this.accessors = accessors;
    }


    @Override
    public final boolean isReadable(String propertyName) {
        return this.accessors.readerMap.get(propertyName) != null;
    }

    @Override
    public final Object get(final Object target, final String propertyName) throws ObjectAccessException {
        if (!this.accessors.beanClass.isInstance(target)) {
            Objects.requireNonNull(target);
            String m = String.format("%s isn't %s type."
                    , target.getClass().getName(), this.accessors.beanClass.getName());
            throw new IllegalArgumentException(m);
        }
        final ValueReadAccessor accessor;
        accessor = accessors.readerMap.get(propertyName);
        if (accessor == null) {
            throw invalidProperty(propertyName);
        }
        try {
            return accessor.get(target);
        } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
            throw accessError(propertyName, e);
        }
    }


    @Override
    public final Class<?> getAccessedType() {
        return this.accessors.beanClass;
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public final String toString() {
        final String m;
        if (this instanceof ObjectAccessor) {
            m = String.format("%s of %s.", ObjectAccessor.class.getName(), this.accessors.beanClass.getName());
        } else {
            m = String.format("%s of %s.", ReadAccessor.class.getName(), this.accessors.beanClass.getName());
        }
        return m;
    }

    final InvalidPropertyException invalidProperty(String propertyName) {
        final Class<?> beanClass = this.accessors.beanClass;
        String m = String.format("%s is invalid property for %s", propertyName, beanClass.getName());
        throw new InvalidPropertyException(m, beanClass, propertyName);
    }

    final InvalidPropertyException accessError(String propertyName, Throwable cause) {
        final Class<?> beanClass = this.accessors.beanClass;
        String m = String.format("%s property of %s access occur error.", propertyName, beanClass.getName());
        return new InvalidPropertyException(m, beanClass, propertyName, cause);
    }


}
