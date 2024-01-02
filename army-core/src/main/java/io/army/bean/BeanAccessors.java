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

import io.army.util._Collections;

import java.util.Map;

final class BeanAccessors {

    final Class<?> beanClass;

    final Map<String, Class<?>> fieldTypeMap;
    final Map<String, ? extends ValueReadAccessor> readerMap;

    final Map<String, ? extends ValueWriteAccessor> writerMap;

    BeanAccessors(final Class<?> beanClass, Map<String, Class<?>> fieldTypeMap,
                  final Map<String, ? extends ValueReadAccessor> readerMap,
                  final Map<String, ? extends ValueWriteAccessor> writerMap) {
        this.beanClass = beanClass;
        this.fieldTypeMap = _Collections.unmodifiableMap(fieldTypeMap);
        this.readerMap = _Collections.unmodifiableMap(readerMap);
        this.writerMap = _Collections.unmodifiableMap(writerMap);

    }


}
