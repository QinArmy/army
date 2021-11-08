package io.army.util;

import io.army.lang.Nullable;
import io.army.meta.FieldMeta;

import java.lang.reflect.Method;

public abstract class ReflectionUtils extends org.springframework.util.ReflectionUtils {


    @Nullable
    public static Method findSetterMethod(FieldMeta<?, ?> fieldMeta) {
        return findMethod(fieldMeta.tableMeta().javaType()
                , setterMethodName(fieldMeta.fieldName())
                , fieldMeta.javaType());
    }

    private static String setterMethodName(String propertyName) {
        String setterName;
        if (propertyName.length() > 1) {
            setterName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        } else {
            setterName = "set" + Character.toUpperCase(propertyName.charAt(0));
        }
        return setterName;
    }

}
