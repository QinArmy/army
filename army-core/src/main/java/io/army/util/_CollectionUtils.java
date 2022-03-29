package io.army.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class _CollectionUtils extends io.qinarmy.util.CollectionUtils {

    public static <T> List<T> asUnmodifiableList(final Collection<T> collection) {
        final List<T> list;
        switch (collection.size()) {
            case 0:
                list = Collections.emptyList();
                break;
            case 1: {
                if (collection instanceof List) {
                    list = Collections.singletonList(((List<T>) collection).get(0));
                } else {
                    List<T> temp = null;
                    for (T v : collection) {
                        temp = Collections.singletonList(v);
                        break;
                    }
                    list = temp;
                }

            }
            break;
            default: {
                list = Collections.unmodifiableList(new ArrayList<>(collection));
            }

        }
        return list;

    }


    /*############################## private method ######################################*/


}
