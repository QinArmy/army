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

package io.army.env;

import io.army.dialect.Database;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;

public final class StandardEnvironment implements ArmyEnvironment {

    public static StandardEnvironment from(Map<String, Object> map) {
        return new StandardEnvironment(map);
    }

    private final Map<String, Object> map;

    private StandardEnvironment(Map<String, Object> map) {
        this.map = Collections.unmodifiableMap(_Collections.hashMap(map));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(final ArmyKey<T> key) {
        final Object userValue;
        userValue = this.map.get(key.name);

        final Class<T> javaType = key.javaType;
        final T value;
        if (userValue == null) {
            value = null;
        } else if (javaType.isInstance(userValue)) {
            value = (T) userValue;
        } else if (!(userValue instanceof String)) {
            throw _Exceptions.convertFail(key, userValue, null);
        } else if (key == ArmyKey.DIALECT) {
            final Database database;
            database = this.getRequired(ArmyKey.DATABASE);
            try {
                value = (T) database.dialectOf((String) userValue);
            } catch (IllegalArgumentException e) {
                String m = String.format("%s value error,couldn't get %s.", ArmyKey.DIALECT.name, ArmyKey.DIALECT);
                throw new IllegalStateException(m);
            }
        } else {
            final BiFunction<Class<T>, String, T> function;
            function = Converters.findConvertor(javaType);
            value = function.apply(javaType, (String) userValue);
        }

        return value;
    }


    @Override
    public <T> T getRequired(final ArmyKey<T> key) {
        final T value;
        value = this.get(key);
        if (value == null) {
            String m = String.format("value of %s is null", key.name);
            throw new IllegalStateException(m);
        }
        return value;
    }

    @Override
    public <T> T getOrDefault(final ArmyKey<T> key) {
        T value;
        value = get(key);
        if (value == null) {
            value = key.defaultValue;
            if (value == null) {
                String m = String.format("%s %s no default value.", ArmyKey.class.getName(), key.name);
                throw new IllegalArgumentException(m);
            }
        }
        return value;
    }


}
