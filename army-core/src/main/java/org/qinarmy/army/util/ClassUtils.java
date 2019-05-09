package org.qinarmy.army.util;

import org.springframework.lang.NonNull;

/**
 * created  on 2019-02-25.
 */
public abstract class ClassUtils extends org.springframework.util.ClassUtils {


    public static Class<?> loadEntityMetaClass(@NonNull Class<?> entityClass) throws ClassNotFoundException {
        return forName(entityClass.getName() + "_", getDefaultClassLoader());
    }
}
