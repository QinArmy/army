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

package io.army.type;

import java.util.ArrayList;
import java.util.Collection;

public final class ArraySqlRecord extends ArrayList<Object> implements SqlRecord {

    public static ArraySqlRecord create(int initialCapacity) {
        return new ArraySqlRecord(initialCapacity);
    }

    public static ArraySqlRecord create() {
        return new ArraySqlRecord();
    }

    public static ArraySqlRecord create(Collection<?> c) {
        return new ArraySqlRecord(c);
    }

    private ArraySqlRecord(int initialCapacity) {
        super(initialCapacity);
    }

    private ArraySqlRecord() {
    }

    private ArraySqlRecord(Collection<?> c) {
        super(c);
    }


}
