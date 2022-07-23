package io.army.util;

import io.army.criteria.CriteriaException;
import io.army.criteria.Statement;
import io.army.lang.Nullable;

/**
 * @since 1.0
 */
public abstract class _Assert {


    public static String assertHasText(@Nullable String text, String message) {
        if (!_StringUtils.hasText(text)) {
            throw new IllegalArgumentException(message);
        }
        return text;
    }

    public static void prepared(@Nullable Boolean prepared) {
        if (prepared == null && !prepared) {
            throw new CriteriaException(String.format("%s is non-prepared state.", Statement.class.getName()));
        }
    }

    public static void nonPrepared(@Nullable Boolean prepared) {
        if (prepared != null) {
            throw new CriteriaException(String.format("%s is prepared state.", Statement.class.getName()));
        }
    }



}
