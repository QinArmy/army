package io.army.util;


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
        return camelToUnderline(camel).toUpperCase(Locale.ROOT);
    }


    public static String camelToLowerCase(String camel) {
        return camelToUnderline(camel).toLowerCase(Locale.ROOT);
    }



    /*################################## private method #############################################*/

    private static String camelToUnderline(String camel) {
        Assert.notNull(camel, "camel required");
        final int len = camel.length(), maxIndex = len - 1;
        StringBuilder builder = new StringBuilder(camel.length() + 5);
        char ch;
        int preIndex = 0;
        for (int i = 0; i < len; i++) {
            ch = camel.charAt(i);
            if (Character.isUpperCase(ch)) {
                builder.append(camel, preIndex, i);
                builder.append('_');
                preIndex = i;
            } else if (i == maxIndex) {
                builder.append(camel, preIndex, len);
            }
        }
        return builder.toString();
    }


}
