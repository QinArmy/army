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

package io.army.dialect.oracle;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.meta.ServerMeta;
import io.army.util._StringUtils;

public enum OracleDialect implements Dialect {

    ORACLE10(10)
    ;


    private final byte version;

    OracleDialect(int version) {
        this.version = (byte) version;
    }

    @Override
    public final Database database() {
        return Database.Oracle;
    }

    @Override
    public final int compareWith(Dialect o) throws IllegalArgumentException {
        if (!(o instanceof OracleDialect)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return this.version - ((OracleDialect) o).version;
    }

    @Override
    public final boolean isFamily(Dialect o) {
        return o instanceof OracleDialect;
    }

    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


    public static OracleDialect from(final ServerMeta meta) {
        //TODO
        throw new UnsupportedOperationException();
    }


}
