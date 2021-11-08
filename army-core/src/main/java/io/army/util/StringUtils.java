package io.army.util;


import io.army.modelgen._MetaBridge;

import java.util.Locale;

public abstract class StringUtils extends org.springframework.util.StringUtils {

    protected StringUtils() {
        throw new UnsupportedOperationException();
    }

    public static String toLowerCase(String text) {
        return text == null ? null : text.toLowerCase(Locale.ROOT);
    }

    public static String toUpperCase(String text) {
        return text == null ? null : text.toUpperCase(Locale.ROOT);
    }

    public static String camelToUpperCase(String camel) {
        return _MetaBridge.camelToUpperCase(camel);
    }


    public static String camelToLowerCase(String camel) {
        return _MetaBridge.camelToLowerCase(camel);
    }



    /*################################## private method #############################################*/

}
