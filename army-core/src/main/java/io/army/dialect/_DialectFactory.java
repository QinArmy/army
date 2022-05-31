package io.army.dialect;

import io.army.dialect.mysql._MySQLDialectFactory;
import io.army.env.ArmyKey;
import io.army.meta.ServerMeta;
import io.army.util._Exceptions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class _DialectFactory {

    protected _DialectFactory() {
        throw new UnsupportedOperationException();
    }

    public static _Dialect createDialect(_DialectEnvironment environment) {
        final Database database = environment.serverMeta().database();
        final _Dialect dialect;
        switch (database) {
            case MySQL:
                dialect = _MySQLDialectFactory.createDialect(environment);
                break;
            case PostgreSQL:
            case Firebird:
            case Oracle:
            case H2:
            default:
                throw _Exceptions.unexpectedEnum(database);
        }
        return dialect;
    }

    /**
     * <p>
     * Inner method
     * </p>
     */
    @SuppressWarnings("unchecked")
    protected static <T extends _AbstractDialect> T invokeFactoryMethod(Class<T> dialectType, String className
            , _DialectEnvironment environment) {
        final Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        final String methodName = "create";
        try {
            final Method method;
            method = clazz.getMethod(methodName, _DialectEnvironment.class);
            final int modifiers = method.getModifiers();
            if (!(Modifier.isPublic(modifiers)
                    && Modifier.isStatic(modifiers)
                    && dialectType.isAssignableFrom(method.getReturnType()))) {
                String m = String.format("Not found factory method,public static %s %s(%s) in class %s"
                        , className, methodName, _DialectEnvironment.class.getName(), className);
                throw new RuntimeException(m);
            }
            final T dialect;
            dialect = (T) method.invoke(null, environment);
            assert dialect != null;
            return dialect;
        } catch (NoSuchMethodException e) {
            String m = String.format("Not found factory method,public static %s %s(%s) in class %s"
                    , className, methodName, _DialectEnvironment.class.getName(), className);
            throw new RuntimeException(m, e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

    protected static Dialect targetDialect(final _DialectEnvironment environment, final Database database) {
        final ServerMeta meta = environment.serverMeta();
        if (meta.database() != database) {
            String m = String.format("%s database isn't %s", meta, database);
            throw new IllegalArgumentException(m);
        }
        final Dialect serverDialect;
        serverDialect = Dialect.from(meta);
        Dialect targetDialect;
        targetDialect = environment.environment().get(ArmyKey.DIALECT);
        if (targetDialect == null) {
            targetDialect = Dialect.from(meta);
        } else if (targetDialect.database != serverDialect.database) {
            throw _Exceptions.dialectDatabaseNotMatch(targetDialect, meta);
        } else if (targetDialect.version() > serverDialect.version()) {
            throw _Exceptions.dialectVersionNotCompatibility(targetDialect, meta);
        }
        return targetDialect;
    }


}
