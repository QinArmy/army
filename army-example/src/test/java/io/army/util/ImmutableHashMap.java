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

package io.army.util;

import io.army.type.ImmutableSpec;

import java.util.HashMap;
import java.util.Map;

public final class ImmutableHashMap<K, V> extends HashMap<K, V> implements ImmutableSpec {

    public static <K, V> ImmutableHashMap<K, V> hashMap() {
        return new ImmutableHashMap<>();
    }

    public static <K, V> ImmutableHashMap<K, V> hashMap(int initialCapacity) {
        return new ImmutableHashMap<>(initialCapacity);
    }


    public static <K, V> ImmutableHashMap<K, V> hashMap(Map<? extends K, ? extends V> map) {
        return new ImmutableHashMap<>(map);
    }


    private ImmutableHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    private ImmutableHashMap() {
    }

    private ImmutableHashMap(Map<? extends K, ? extends V> m) {
        super(m);
    }


}
