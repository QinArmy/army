package io.army.util;

import io.army.lang.Nullable;

import java.util.Collections;
import java.util.List;

public abstract class CollectionUtils extends io.qinarmy.util.CollectionUtils {


    public static <T> List<T> asUnmodifiableList(@Nullable List<T> nullList) {
        final List<T> list;
        if (nullList == null) {
            list = Collections.emptyList();
        } else {
            list = unmodifiableList(nullList);
        }
        return list;
    }


    /*############################## private method ######################################*/


}
