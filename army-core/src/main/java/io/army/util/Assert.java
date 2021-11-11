package io.army.util;

import org.springframework.lang.Nullable;

/**
 * @since 1.0
 */
public abstract class Assert extends org.springframework.util.Assert {


    public static String assertHasText(@Nullable String text, String message) {
        if (!StringUtils.hasText(text)) {
            throw new IllegalArgumentException(message);
        }
        return text;
    }

}
