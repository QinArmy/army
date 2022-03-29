package io.army.util;


import io.army.modelgen._MetaBridge;

import java.util.Locale;

public abstract class _StringUtils extends io.qinarmy.util.StringUtils {

    protected _StringUtils() {
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

    public static boolean isBinary(String text) {
        final char[] array = text.toCharArray();
        boolean match = array.length > 0;
        for (char c : array) {
            if (c != '0' && c != '1') {
                match = false;
                break;
            }
        }
        return match;
    }



    /*################################## private method #############################################*/

}
