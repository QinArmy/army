package io.army.util;

import io.army.dialect.DialectEnv;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class ReflectionUtils {

    private ReflectionUtils() {
        throw new UnsupportedOperationException();
    }


    public static Method getStaticFactoryMethod(final String className, final Class<?> returnType, final String methodName,
                                                final Class<?>... paramTypeArray) {
        final Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            final Method method;
            method = clazz.getMethod(methodName, paramTypeArray);
            final int modifiers = method.getModifiers();
            if (!(Modifier.isPublic(modifiers)
                    && Modifier.isStatic(modifiers))) {
                final StringBuilder builder = new StringBuilder("Not found factory method ");
                appendMethodErrorInfo(builder, className, methodName, paramTypeArray);
                throw new IllegalArgumentException(builder.toString());
            }

            if (!returnType.isAssignableFrom(method.getReturnType())) {
                final StringBuilder builder = new StringBuilder("Return type ")
                        .append(returnType.getName())
                        .append(" and ");
                appendMethodErrorInfo(builder, className, methodName, paramTypeArray);
                builder.append(" not match.");
                throw new IllegalArgumentException(builder.toString());
            }
            return method;
        } catch (NoSuchMethodException e) {
            String m = String.format("Not found factory method,public static %s %s(%s) in class %s",
                    className, methodName, DialectEnv.class.getName(), className);
            throw new RuntimeException(m, e);
        }
    }

    public static Object invokeStaticFactoryMethod(Method method, Object... paramArray) {
        try {
            final Object result;
            result = method.invoke(null, paramArray);
            if (result == null) {
                String m = String.format("method %s return null", method);
                throw new NullPointerException(m);
            }
            return result;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /*-------------------below private methods -------------------*/

    private static void appendMethodErrorInfo(final StringBuilder builder, final String className,
                                              final String methodName, final Class<?>... paramTypeArray) {
        builder.append("public static ")
                .append(methodName)
                .append('(');
        for (int i = 0; i < paramTypeArray.length; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(paramTypeArray[i].getName());
        }
        builder.append(") of class ")
                .append(className);
    }


}
