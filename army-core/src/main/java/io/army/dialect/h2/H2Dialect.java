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

package io.army.dialect.h2;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.meta.ServerMeta;

public enum H2Dialect implements Dialect {

    // H214(14)
    ;


    private final byte version;

    H2Dialect(int version) {
        this.version = (byte) version;
    }

    @Override
    public final Database database() {
        return Database.H2;
    }

    @Override
    public final int compareWith(Dialect o) throws IllegalArgumentException {
        if (!(o instanceof H2Dialect)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return this.version - ((H2Dialect) o).version;
    }

    @Override
    public final boolean isFamily(Dialect o) {
        return o instanceof H2Dialect;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", H2Dialect.class.getSimpleName(), this.name());
    }


    public static H2Dialect from(ServerMeta meta) {
        throw new UnsupportedOperationException();
    }


}
