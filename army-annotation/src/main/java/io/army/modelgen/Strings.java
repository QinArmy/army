package io.army.modelgen;

import io.army.lang.Nullable;

import java.util.Locale;

abstract class Strings {

    private Strings() {
        throw new UnsupportedOperationException();
    }

    static boolean hasText(@Nullable String str) {
        return str != null && str.length() > 0 && containsText(str);
    }

    static String toLowerCase(String text) {
        return text.toLowerCase(Locale.ROOT);
    }

    static String getShortName(final String className) {
        final int index;
        index = className.lastIndexOf('.');
        return index > 0 ? className.substring(index + 1) : className;
    }

    private static boolean containsText(CharSequence str) {
        boolean match = false;
        final int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                match = true;
                break;
            }
        }
        return match;
    }


}
