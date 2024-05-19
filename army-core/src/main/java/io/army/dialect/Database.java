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


import io.army.dialect.h2.H2Dialect;
import io.army.dialect.mysql.MySQLDialect;
import io.army.dialect.oracle.OracleDialect;
import io.army.dialect.sqlite.SQLiteDialect;
import io.army.meta.ServerMeta;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>This enum representing database product family.
 *
 * @since 0.6.0
 */
public enum Database {

    MySQL(MySQLDialect::values, MySQLDialect::valueOf),
    Oracle(OracleDialect::values, OracleDialect::valueOf),
    PostgreSQL(PostgreDialect::values, PostgreDialect::valueOf),
    H2(H2Dialect::values, H2Dialect::valueOf),
    SQLite(SQLiteDialect::values, SQLiteDialect::valueOf);


    private final Supplier<Dialect[]> supplier;

    private final Function<String, Dialect> function;


    Database(Supplier<Dialect[]> supplier, Function<String, Dialect> function) {
        this.supplier = supplier;
        this.function = function;
    }


    public final Dialect[] dialects() {
        return this.supplier.get();
    }

    public final Dialect dialectOf(String name) throws IllegalArgumentException {
        return this.function.apply(name);
    }

    public final boolean isCompatible(Dialect dialect) {
        return dialect.database() == this;
    }


    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }

    public static Dialect from(final ServerMeta meta) {
        return switch (meta.serverDatabase()) {
            case MySQL -> MySQLDialect.from(meta);
            case PostgreSQL -> PostgreDialect.from(meta);
            case SQLite -> SQLiteDialect.from(meta);
            case Oracle -> OracleDialect.from(meta);
            case H2 -> H2Dialect.from(meta);
            default -> throw _Exceptions.unexpectedEnum(meta.serverDatabase());
        };
    }

    public static Database mapToDatabase(final String productFamily, final @Nullable Function<String, Database> func) {
        final Database database;
        switch (productFamily) {
            case "MySQL":
                database = Database.MySQL;
                break;
            case "PostgreSQL":
                database = Database.PostgreSQL;
                break;
            case "SQLite":
                database = Database.SQLite;
                break;
            case "H2":
                database = Database.H2;
                break;
            case "Oracle":
                database = Database.Oracle;
                break;
            default:
                if (func == null) {
                    database = null;
                } else {
                    database = func.apply(productFamily);
                }
        }
        if (database == null) {
            throw _Exceptions.unsupportedDatabaseFamily(productFamily);
        }
        return database;
    }


    public static IllegalArgumentException unsupportedVersion(ServerMeta meta) {
        return new IllegalArgumentException(String.format("unsupported %s", meta));
    }


}
