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

import io.army.dialect.impl.DialectParser;
import io.army.meta.ServerMeta;
import io.army.util.ClassUtils;
import io.army.util._Exceptions;

import java.lang.reflect.Method;

public abstract class DialectParserFactory {

    protected DialectParserFactory() {
        throw new UnsupportedOperationException();
    }

    public static DialectParser createDialectParser(final DialectEnv environment) {
        final Database database;
        database = environment.serverMeta().serverDatabase();
        final String className;
        className = switch (database) {
            case MySQL -> "io.army.dialect.mysql.MySQLDialectParserFactory";
            case PostgreSQL -> "io.army.dialect.postgre.PostgreDialectParserFactory";
            case SQLite -> "io.army.dialect.sqlite.SQLiteParserFactory";
            default -> throw _Exceptions.unexpectedEnum(database);
        };

        final Method method;
        method = ClassUtils.getStaticFactoryMethod(className, DialectParser.class, "dialectParser", DialectEnv.class);
        return (DialectParser) ClassUtils.invokeStaticFactoryMethod(method, environment);
    }


    protected static Dialect targetDialect(final DialectEnv environment, final Database database) {
        final ServerMeta meta = environment.serverMeta();
        if (meta.serverDatabase() != database) {
            String m = String.format("%s database isn't %s", meta, database);
            throw new IllegalArgumentException(m);
        }
        final Dialect serverDialect;
        serverDialect = Database.from(meta);
        Dialect targetDialect;
        targetDialect = meta.usedDialect();
        if (!targetDialect.isFamily(serverDialect)) {
            throw _Exceptions.dialectDatabaseNotMatch(targetDialect, meta);
        } else if (targetDialect.compareWith(serverDialect) > 0) {
            throw _Exceptions.dialectVersionNotCompatibility(targetDialect, meta);
        }
        return targetDialect;
    }


}
