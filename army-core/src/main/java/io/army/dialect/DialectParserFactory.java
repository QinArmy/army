package io.army.dialect;

import io.army.dialect.mysql._MySQLDialectFactory;
import io.army.dialect.postgre._PostgreDialectFactory;
import io.army.env.ArmyKey;
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
        database = environment.mappingEnv().serverMeta().dialectDatabase();
        final DialectParser parser;
        switch (database) {
            case MySQL:
                parser = _MySQLDialectFactory.mysqlDialectParser(environment);
                break;
            case Postgre:
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
     * </p>
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
        final ServerMeta meta = environment.mappingEnv().serverMeta();
        if (meta.dialectDatabase() != database) {
            String m = String.format("%s database isn't %s", meta, database);
            throw new IllegalArgumentException(m);
        }
        final Dialect serverDialect;
        serverDialect = Database.from(meta);
        Dialect targetDialect; // TODO get from server meta
        targetDialect = environment.environment().get(ArmyKey.DIALECT);
        if (targetDialect == null) {
            targetDialect = Database.from(meta);
        } else if (!targetDialect.isFamily(serverDialect)) {
            throw _Exceptions.dialectDatabaseNotMatch(targetDialect, meta);
        } else if (targetDialect.compareWith(serverDialect) > 0) {
            throw _Exceptions.dialectVersionNotCompatibility(targetDialect, meta);
        }
        return targetDialect;
    }


}
