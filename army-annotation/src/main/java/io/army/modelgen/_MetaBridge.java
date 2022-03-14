package io.army.modelgen;


import io.army.lang.NonNull;
import io.army.lang.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Meta Constant set
 */
public abstract class _MetaBridge {

    private _MetaBridge() {
        throw new UnsupportedOperationException();
    }


    public static final String ID = "id";
    public static final String CREATE_TIME = "createTime";
    public static final String VISIBLE = "visible";
    public static final String UPDATE_TIME = "updateTime";

    public static final String VERSION = "version";

    public static final List<String> RESERVED_PROPS = asUnmodifiableList(
            ID, CREATE_TIME, UPDATE_TIME, VERSION, VISIBLE);


    public static final String TABLE_META = "T";

    public static final String TABLE_NAME = "TABLE_NAME";

    public static final String FIELD_COUNT = "FIELD_COUNT";

    public static final String META_CLASS_NAME_SUFFIX = "_";


    public static String camelToUpperCase(String camel) {
        return camelToUnderline(camel).toUpperCase(Locale.ROOT);
    }

    public static String camelToLowerCase(String camel) {
        return camelToUnderline(camel).toLowerCase(Locale.ROOT);
    }

    private static String camelToUnderline(final String camel) {
        final int len = camel.length();
        final StringBuilder builder = new StringBuilder(camel.length() + 5);
        char ch;
        int preIndex = 0;
        for (int i = 0; i < len; i++) {
            ch = camel.charAt(i);
            if (Character.isUpperCase(ch)) {
                builder.append(camel, preIndex, i);
                builder.append('_');
                preIndex = i;
            }
        }
        builder.append(camel, preIndex, len);
        return builder.toString();
    }


    @SafeVarargs
    @SuppressWarnings("varargs")
    @NonNull
    public static <T> List<T> asUnmodifiableList(@Nullable T... e) {
        final List<T> list;
        if (e == null || e.length == 0) {
            list = Collections.emptyList();
        } else if (e.length == 1) {
            list = Collections.singletonList(e[0]);
        } else {
            final List<T> temp = new ArrayList<>(e.length);
            Collections.addAll(temp, e);
            list = Collections.unmodifiableList(temp);
        }
        return list;
    }


}
