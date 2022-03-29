package io.army.util;

import io.army.criteria.CriteriaException;
import io.army.criteria.NamedParam;
import io.army.criteria.Statement;
import io.army.lang.Nullable;
import io.army.stmt.ParamValue;

import java.util.List;

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

    public static void prepared(boolean prepared) {
        if (!prepared) {
            throw new CriteriaException(String.format("%s is non-prepared state.", Statement.class.getName()));
        }
    }

    public static void nonPrepared(boolean prepared) {
        if (prepared) {
            throw new CriteriaException(String.format("%s is prepared state.", Statement.class.getName()));
        }
    }


    public static void noNamedParam(List<ParamValue> paramGroup) {
        for (ParamValue paramValue : paramGroup) {
            if (paramValue instanceof NamedParam) {
                throw _Exceptions.namedParamInNonBatch((NamedParam) paramValue);
            }
        }
    }


}
