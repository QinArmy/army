package io.army.util;

import javax.annotation.Nullable;

/**
 * @since 1.0
 */
public abstract class _ClassUtils {

    public static final String PUBLISHER_CLASS_NAME = "org.reactivestreams.Publisher";

    public static final String FLUX_CLASS_NAME = "reactor.core.publisher.Flux";

    public static boolean isReactivePresent() {
        return isPresent("io.army.reactive.Session", null);
    }

    public static boolean isSyncPresent() {
        return isPresent("io.army.sync.Session", null);
    }

    public static boolean isPresent(String className, @Nullable ClassLoader classLoader) {
        boolean present;
        try {
            Class.forName(className, false, classLoader);
            present = true;
        } catch (ClassNotFoundException e) {
            present = false;
        }
        return present;
    }



    @Nullable
    public static String safeClassName(@Nullable Object value) {
        return value == null ? null : value.getClass().getName();
    }

    public static Class<?> enumClass(Class<?> clazz) {
        if (!Enum.class.isAssignableFrom(clazz)) {
            String m = String.format("%s isn't enum", clazz.getName());
            throw new IllegalArgumentException(m);
        }
        if (clazz.isAnonymousClass()) {
            clazz = clazz.getSuperclass();
        }
        return clazz;
    }

    @Nullable
    public static Class<?> tryLoadClass(final String className, @Nullable ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        Class<?> clazz;
        try {
            clazz = Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException e) {
            clazz = null;
        }
        return clazz;
    }


}
