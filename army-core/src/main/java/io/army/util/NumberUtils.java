package io.army.util;


public abstract class NumberUtils extends org.springframework.util.NumberUtils {


    public static <T extends Number> T parseNumberFromObject(Object object, Class<T> targetClass) {
        if (object == null) {
            return null;
        }
        T value;
        if (object instanceof Number) {
            value = convertNumberToTargetClass((Number) object, targetClass);
        } else if (object instanceof String) {
            value = parseNumber((String) object, targetClass);
        } else {
            throw new IllegalArgumentException(String.format(
                    "object[%s] couldn't convert to %s", object, targetClass));
        }
        return value;
    }

}
