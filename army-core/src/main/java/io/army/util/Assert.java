package io.army.util;

import java.util.Collection;

/**
 * created  on 2018/11/18.
 */
public abstract class Assert extends org.springframework.util.Assert {

    public static void assertTrue(boolean expression, String format, Object... args) {
        if (!expression) {
            throwIllegalArgumentException(format, args);
        }
    }

    public static void assertNotNull(Object obj, String format, Object... args) {
        if (obj == null) {
            throwIllegalArgumentException(format, args);
        }
    }

    public static void assertNotEmpty(Collection<?> collection, String format, Object... args) {
        if (collection == null || collection.isEmpty()) {
            throwIllegalArgumentException(format, args);
        }
    }

    public static void assertGeZero(int num1, String format, Object... args) {
        assertGe(num1, 0, format, args);
    }

    public static void assertGe(int num1, int num2, String format, Object... args) {
        if (num1 < num2) {
            throwIllegalArgumentException(format, args);
        }
    }


    private static void throwIllegalArgumentException(String format, Object... args) {
        String text = args == null ? format : String.format(format, args);
        throw new IllegalArgumentException(text);
    }


}
