package io.army.util;

/**
 * created  on 2018/11/18.
 */
public abstract class ElementUtils {


    public static String getPackageName(String qualifiedName) {
        int index = qualifiedName.lastIndexOf('.');
        String packageName;
        if (index > 0) {
            packageName = qualifiedName.substring(0, index);
        } else {
            throw new RuntimeException(String.format("%s is error.", qualifiedName));
        }
        return packageName;
    }

}
