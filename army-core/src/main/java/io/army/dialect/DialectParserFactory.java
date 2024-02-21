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

import io.army.dialect.mysql._MySQLDialectFactory;
import io.army.dialect.postgre._PostgreDialectFactory;
import io.army.meta.ServerMeta;
import io.army.util._Exceptions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class DialectParserFactory {

    protected DialectParserFactory() {
        throw new UnsupportedOperationException();
    }

    public static DialectParser createDialect(DialectEnv environment) {
        final Database database;
        database = environment.serverMeta().serverDatabase();
        final DialectParser parser;
        switch (database) {
            case MySQL:
                parser = _MySQLDialectFactory.mysqlDialectParser(environment);
                break;
            case PostgreSQL:
                parser = _PostgreDialectFactory.postgreDialectParser(environment);
                break;
            case Oracle:
            default:
                throw _Exceptions.unexpectedEnum(database);
        }
        return parser;
    }

    /**
     * <p>
     * Inner method
     *
     */
    @SuppressWarnings("unchecked")
    protected static <T extends ArmyParser> T invokeFactoryMethod(Class<T> dialectType, String className
            , DialectEnv environment) {
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
                    && Modifier.isStatic(modifiers)
                    && dialectType.isAssignableFrom(method.getReturnType()))) {
                String m = String.format("Not found factory method,public static %s %s(%s) in class %s"
                        , className, methodName, DialectEnv.class.getName(), className);
                throw new RuntimeException(m);
            }
            final T dialect;
            dialect = (T) method.invoke(null, environment);
            assert dialect != null;
            return dialect;
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
