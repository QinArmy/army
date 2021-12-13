package io.army.util;

import io.army.criteria.CriteriaException;
import io.army.criteria.Statement;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

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

    public static void prepared(boolean prepared) {
        if (prepared) {
            throw new IllegalStateException(String.format("%s is non-prepared state.", Statement.class.getName()));
        }
    }

    public static void nonPrepared(boolean prepared) {
        if (!prepared) {
            throw new IllegalStateException(String.format("%s is prepared state.", Statement.class.getName()));
        }
    }

    public static void identifierHasText(String identifier) {
        if (!StringUtils.hasText(identifier)) {
            throw new CriteriaException("Criteria identifier must has text.");
        }
    }

    public static void hasTable(@Nullable TableMeta<?> table) {
        if (table == null) {
            throw new CriteriaException("Criteria must has table.");
        }
    }

}
