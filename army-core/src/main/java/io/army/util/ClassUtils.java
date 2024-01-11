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

package io.army.util;

import javax.annotation.Nullable;

/**
 * @since 0.6.0
 */
public abstract class ClassUtils {

    public static final String PUBLISHER_CLASS_NAME = "org.reactivestreams.Publisher";

    public static final String FLUX_CLASS_NAME = "reactor.core.publisher.Flux";

    public static boolean isReactivePresent() {
        return isPresent("io.army.reactive.Session", null);
    }

    public static boolean isSyncPresent() {
        return isPresent("io.army.sync.Session", null);
    }


    private ClassUtils() {
        throw new UnsupportedOperationException();
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

    public static boolean isAssignableFrom(String className, @Nullable ClassLoader classLoader, Class<?> target) {
        try {
            return Class.forName(className, false, classLoader).isAssignableFrom(target);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
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

    public static boolean isWrapperClass(final Class<?> wrapperClass, final Class<?> primitiveClass) {
        final boolean match;
        if (primitiveClass == int.class) {
            match = wrapperClass == Integer.class;
        } else if (primitiveClass == long.class) {
            match = wrapperClass == Long.class;
        } else if (primitiveClass == boolean.class) {
            match = wrapperClass == Boolean.class;
        } else if (primitiveClass == short.class) {
            match = wrapperClass == Short.class;
        } else if (primitiveClass == byte.class) {
            match = wrapperClass == Byte.class;
        } else if (primitiveClass == double.class) {
            match = wrapperClass == Double.class;
        } else if (primitiveClass == float.class) {
            match = wrapperClass == Float.class;
        } else if (primitiveClass == char.class) {
            match = wrapperClass == Character.class;
        } else if (primitiveClass == void.class) {
            match = wrapperClass == Void.class;
        } else {
            match = false;
        }
        return match;
    }


}
