package org.qinarmy.army.util;

/**
 * created  on 2018/11/18.
 */
public abstract class ElementUtils {


    public static String getSimpleName(String qualifiedName) {
        int index = qualifiedName.lastIndexOf('.');
        String packageName;
        if (index > 0) {
            packageName = qualifiedName.substring(index + 1);
        } else {
            throw new RuntimeException(String.format("%s is error.", qualifiedName));
        }
        return packageName;
    }

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

    public static String camelToUpperCase(String camel) {
        final int len = camel.length(), maxIndex = len - 1;
        StringBuilder builder = new StringBuilder(camel.length() + 5);
        char ch;
        int preIndex = 0;
        for (int i = 0; i < len; i++) {
            ch = camel.charAt(i);
            if (Character.isUpperCase(ch)) {
                builder.append(camel, preIndex, i);
                builder.append('_');
                preIndex = i;
            } else if (i == maxIndex) {
                builder.append(camel, preIndex, len);
            }
        }
        return builder.toString().toUpperCase();
    }

}
