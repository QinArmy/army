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

import javax.annotation.Nullable;

public final class ReactiveKey<T> extends ArmyKey<T> {


    /**
     * @see #EXECUTOR_PROVIDER_MD5
     */
    public static final ReactiveKey<String> EXECUTOR_PROVIDER = new ReactiveKey<>("reactive.executor.provider", String.class, "io.army.jdbd.JdbdExecutorFactoryProvider");

    /**
     * @see #EXECUTOR_PROVIDER
     */
    public static final ReactiveKey<String> EXECUTOR_PROVIDER_MD5 = new ReactiveKey<>("reactive.executor.provider_md5", String.class, "6fbb231ebf613cc0c129439bd9504ae1");

    /**
     * private constructor
     */
    private ReactiveKey(String name, Class<T> javaType, @Nullable T defaultValue) {
        super(name, javaType, defaultValue);
    }


}
