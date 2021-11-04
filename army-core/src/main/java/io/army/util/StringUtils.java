package io.army.util;


import io.army.modelgen.MetaConstant;

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
        return MetaConstant.camelToUpperCase(camel);
    }


    public static String camelToLowerCase(String camel) {
        return MetaConstant.camelToLowerCase(camel);
    }



    /*################################## private method #############################################*/

}
