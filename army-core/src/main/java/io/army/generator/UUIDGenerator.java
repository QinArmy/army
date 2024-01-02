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

package io.army.generator;

import io.army.bean.ReadWrapper;
import io.army.meta.FieldMeta;

import java.util.UUID;

public final class UUIDGenerator implements FieldGenerator {

    private static final UUIDGenerator INSTANCE = new UUIDGenerator();

    public static UUIDGenerator create(final FieldMeta<?> field) {
        if (field.javaType() != String.class) {
            throw errorFiled(field);
        }
        return INSTANCE;
    }

    @Override
    public Object next(FieldMeta<?> field, ReadWrapper domain) throws GeneratorException {
        if (field.javaType() != String.class) {
            throw errorFiled(field);
        }
        return UUID.randomUUID().toString();
    }

    private static IllegalArgumentException errorFiled(FieldMeta<?> field) {
        String m = String.format("%s java type isn't %s.", field, field.javaType().getName());
        return new IllegalArgumentException(m);
    }


}
