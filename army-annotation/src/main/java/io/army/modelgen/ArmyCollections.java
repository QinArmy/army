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

package io.army.modelgen;

import java.util.ArrayList;
import java.util.HashMap;

abstract class ArmyCollections {

    private ArmyCollections() {
        throw new UnsupportedOperationException();
    }


    static <K, V> HashMap<K, V> hashMap() {
        return new FinalHashMap<>();
    }

    static <K, V> HashMap<K, V> hashMap(int initialCapacity) {
        return new FinalHashMap<>(initialCapacity);
    }

    static <E> ArrayList<E> arrayList() {
        return new FinalArrayList<>();
    }


    private static final class FinalHashMap<K, V> extends HashMap<K, V> {


        private FinalHashMap() {
        }

        private FinalHashMap(int initialCapacity) {
            super(initialCapacity);
        }


    }

    private static final class FinalArrayList<E> extends ArrayList<E> {

        private FinalArrayList() {
        }
    }


}
