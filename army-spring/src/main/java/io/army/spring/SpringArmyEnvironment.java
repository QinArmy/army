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

package io.army.spring;

import io.army.dialect.Database;
import io.army.env.ArmyEnvironment;
import io.army.env.ArmyKey;
import org.springframework.core.env.Environment;

public final class SpringArmyEnvironment implements ArmyEnvironment {

    public static SpringArmyEnvironment create(String factoryName, Environment env) {
        return new SpringArmyEnvironment(factoryName, env);
    }

    private static final String ARMY_PERIOD = "army.";

    private final String prefix;

    private final Environment env;

    /**
     * private constructor
     */
    private SpringArmyEnvironment(String factoryName, Environment env) {
        this.prefix = ARMY_PERIOD + factoryName + '.';
        this.env = env;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(final ArmyKey<T> key) {
        final Environment env = this.env;

        T value;
        if (key == ArmyKey.DIALECT) {
            final Database database;
            database = getRequired(ArmyKey.DATABASE);

            String dialectName;
            dialectName = env.getProperty(this.prefix + key.name, String.class);
            if (dialectName == null) {
                dialectName = env.getRequiredProperty(ARMY_PERIOD + key.name, String.class);
            }
            try {
                value = (T) database.dialectOf(dialectName);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(e);
            }
        } else if ((value = env.getProperty(this.prefix + key.name, key.javaType)) == null) {
            value = env.getProperty(ARMY_PERIOD + key.name, key.javaType);
        }
        return value;
    }

    @Override
    public <T> T getRequired(ArmyKey<T> key) throws IllegalStateException {
        final T value;
        value = get(key);
        if (value == null) {
            String m = String.format("value of %s is null", key.name);
            throw new IllegalStateException(m);
        }
        return value;
    }

    @Override
    public <T> T getOrDefault(final ArmyKey<T> key) {
        final T value;
        value = get(key);
        if (value != null) {
            return value;
        }
        final T defaultValue;
        defaultValue = key.defaultValue;
        if (defaultValue == null) {
            throw new IllegalArgumentException(String.format("%s no default value.", key.name));
        }
        return defaultValue;
    }

}
