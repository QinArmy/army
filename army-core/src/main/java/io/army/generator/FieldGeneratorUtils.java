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

import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;

public abstract class FieldGeneratorUtils {


    protected FieldGeneratorUtils() {
        throw new UnsupportedOperationException();
    }


    public static GeneratorException dontSupportJavaType(Class<? extends FieldGenerator> generatorClass
            , FieldMeta<?> field) {
        String m = String.format("%s don't support java type[%s] of %s."
                , generatorClass.getName(), field.javaType().getName(), field);
        return new GeneratorException(m);
    }

    public static IllegalArgumentException noGeneratorMeta(FieldMeta<?> field) {
        String m = String.format("%s no %s.", field, GeneratorMeta.class.getName());
        return new IllegalArgumentException(m);
    }


}
