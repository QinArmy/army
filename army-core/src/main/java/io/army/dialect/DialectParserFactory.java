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
import io.army.util.ReflectionUtils;
import io.army.util._Exceptions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class DialectParserFactory {

    protected DialectParserFactory() {
        throw new UnsupportedOperationException();
    }

    public static DialectParser createDialectParser(final DialectEnv environment) {
        final Database database;
        database = environment.serverMeta().serverDatabase();
        final String className;
        switch (database) {
            case MySQL:
                className = "io.army.dialect._MySQLParserFactory";
                break;
            case PostgreSQL:
                className = "io.army.dialect._PostgreParserFactory";
                break;
            case SQLite:
                className = "io.army.dialect.sqlite.SQLiteParserFactory";
                break;
            default:
                throw _Exceptions.unexpectedEnum(database);
        }

        final Method method;
        method = ReflectionUtils.getStaticFactoryMethod(className, DialectParser.class, "dialectParser", DialectEnv.class);
        return (DialectParser) ReflectionUtils.invokeStaticFactoryMethod(method, environment);
    }

    /**
     * <p>Inner method
     */
    @Deprecated
    protected static DialectParser invokeFactoryMethod(Class<?> dialectType, String className, DialectEnv environment) {
        final Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        final String methodName = "create";
        try {
            final Method method;
            method = clazz.getMethod(methodName, DialectEnv.class);
            final int modifiers = method.getModifiers();
            if (!(Modifier.isPublic(modifiers)
                    && Modifier.isStatic(modifiers))) {
                String m = String.format("Not found factory method,public static %s %s(%s) in class %s"
                        , className, methodName, DialectEnv.class.getName(), className);
                throw new RuntimeException(m);
            }
            final DialectParser parser;
            parser = (DialectParser) method.invoke(null, environment);
            if (!dialectType.isInstance(parser)) {
                throw new RuntimeException("return type don't match");
            }
            return parser;
        } catch (NoSuchMethodException e) {
            String m = String.format("Not found factory method,public static %s %s(%s) in class %s"
                    , className, methodName, DialectEnv.class.getName(), className);
            throw new RuntimeException(m, e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

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
