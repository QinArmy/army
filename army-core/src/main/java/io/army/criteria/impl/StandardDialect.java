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

package io.army.criteria.impl;

import io.army.dialect.Database;
import io.army.dialect.Dialect;


/**
 * package enum,This enum is designed for standard statement. For example : {@link StandardQueries}
 *
 * @since 0.6.0
 */
enum StandardDialect implements Dialect {

    STANDARD10(10),
    STANDARD20(20);

    private final byte version;

    StandardDialect(int version) {
        this.version = (byte) version;
    }

    @Override
    public final Database database() {
        //no bug,never here
        throw new UnsupportedOperationException();
    }

    @Override
    public final int compareWith(final Dialect o) {
        if (!(o instanceof StandardDialect)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return this.version - ((StandardDialect) o).version;
    }

    @Override
    public final boolean isFamily(final Dialect o) {
        return o instanceof StandardDialect;
    }

    @Override
    public final String toString() {
        return CriteriaUtils.enumToString(this);
    }


}
