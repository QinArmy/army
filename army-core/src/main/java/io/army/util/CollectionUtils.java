package io.army.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class CollectionUtils extends org.springframework.util.CollectionUtils {

    public static <T> List<T> unmodifiableList(Collection<T> collection, T... addElements) {
        if (addElements == null || addElements.length == 0) {
            return createUnmodifiableList(collection);
        }
        List<T> list = new ArrayList<>(collection.size() + addElements.length);
        list.addAll(collection);
        Collections.addAll(list, addElements);
        return Collections.unmodifiableList(list);
    }


    /*############################## private method ######################################*/

    private static <T> List<T> createUnmodifiableList(Collection<T> collection) {
        if (collection instanceof List) {
            return Collections.unmodifiableList((List<T>) collection);
        }
        List<T> list = new ArrayList<>(collection);
        return Collections.unmodifiableList(list);
    }

}
