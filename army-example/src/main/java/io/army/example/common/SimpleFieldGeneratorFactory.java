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

package io.army.example.common;

import io.army.generator.FieldGenerator;
import io.army.generator.FieldGeneratorFactory;
import io.army.generator.FieldGeneratorUtils;
import io.army.generator.UUIDGenerator;
import io.army.generator.snowflake.SingleJvmSnowflakeClient;
import io.army.generator.snowflake.SnowflakeGenerator;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;

public final class SimpleFieldGeneratorFactory implements FieldGeneratorFactory {


    @Override
    public FieldGenerator get(final FieldMeta<?> field) {
        final GeneratorMeta meta;
        meta = field.generator();
        if (meta == null) {
            throw FieldGeneratorUtils.noGeneratorMeta(field);
        }
        final Class<?> javaType = meta.javaType();
        final FieldGenerator fieldGenerator;
        if (javaType == SnowflakeGenerator.class) {
            fieldGenerator = SnowflakeGenerator.create(field, SingleJvmSnowflakeClient.INSTANCE);
        } else if (javaType == UUIDGenerator.class) {
            fieldGenerator = UUIDGenerator.create(field);
        } else {
            String m = String.format("Don't support %s.", javaType.getName());
            throw new IllegalArgumentException(m);
        }
        return fieldGenerator;
    }


}
