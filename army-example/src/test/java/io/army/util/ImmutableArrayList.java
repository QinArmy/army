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

import java.util.ArrayList;
import java.util.Collection;

public final class ImmutableArrayList<E> extends ArrayList<E> implements ImmutableSpec {

    public static <E> ImmutableArrayList<E> arrayList() {
        return new ImmutableArrayList<>();
    }

    public static <E> ImmutableArrayList<E> arrayList(int initialCapacity) {
        return new ImmutableArrayList<>(initialCapacity);
    }

    public static <E> ImmutableArrayList<E> arrayList(Collection<? extends E> c) {
        return new ImmutableArrayList<>(c);
    }


    private ImmutableArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    private ImmutableArrayList() {
    }

    private ImmutableArrayList(Collection<? extends E> c) {
        super(c);
    }


}
