package io.army.util;


import io.army.modelgen.MetaBridge;

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
        return MetaBridge.camelToUpperCase(camel);
    }


    public static String camelToLowerCase(String camel) {
        return MetaBridge.camelToLowerCase(camel);
    }



    /*################################## private method #############################################*/

}
