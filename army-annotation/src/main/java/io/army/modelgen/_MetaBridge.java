/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.modelgen;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    public static final List<String> RESERVED_FIELDS = asUnmodifiableList(
            ID, CREATE_TIME, UPDATE_TIME, VERSION, VISIBLE);


    public static final String TABLE_META = "T";

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
    @Nonnull
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

    public static boolean isReserved(final String fieldName) {
        final boolean match;
        switch (fieldName) {
            case _MetaBridge.ID:
            case _MetaBridge.CREATE_TIME:
            case _MetaBridge.UPDATE_TIME:
            case _MetaBridge.VERSION:
            case _MetaBridge.VISIBLE:
                match = true;
                break;
            default:
                match = false;
        }
        return match;

    }


    public static String createErrorMessage(final String title, final List<String> errorMsgList) {
        final StringBuilder builder = new StringBuilder(errorMsgList.size() * 20)
                .append(title);
        final int size = errorMsgList.size();
        for (int i = 0; i < size; i++) {
            builder.append('\n')
                    .append(i + 1)
                    .append(" : ")
                    .append(errorMsgList.get(i));
        }
        return builder.toString();
    }


}
