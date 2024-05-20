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

package io.army.dialect;

import io.army.meta.ServerMeta;
import io.army.util._StringUtils;


/**
 * <p>This enum representing SQLite dialect version.
 *
 * @see <a href="https://www.sqlite.org/index.html">SQLite</a>
 * @since 0.6.0
 */
public enum SQLiteDialect implements Dialect {

    SQLite34(34);

    private final byte version;

    SQLiteDialect(int version) {
        assert version <= Byte.MAX_VALUE;
        this.version = (byte) version;
    }

    @Override
    public final Database database() {
        return Database.SQLite;
    }

    @Override
    public final int compareWith(final Dialect o) throws IllegalArgumentException {
        if (!(o instanceof SQLiteDialect)) {
            // no bug,never here
            throw new IllegalArgumentException();
        }
        return this.version - ((SQLiteDialect) o).version;
    }

    @Override
    public final boolean isFamily(Dialect o) {
        return o instanceof SQLiteDialect;
    }

    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


    public static SQLiteDialect from(final ServerMeta meta) {
        final int major = meta.major();
        if (major < 3 || (meta.minor() / 10) < 4) {
            throw Database.unsupportedVersion(meta);
        }
        return SQLiteDialect.SQLite34;
    }


}
