package io.army.struct;

import io.army.lang.Nullable;

import java.util.Map;

public interface TextEnum {

    String name();

    String text();

    default TextEnum family() {
        return this;
    }


    @SuppressWarnings("unchecked")
    @Nullable
    static <T extends Enum<T> & TextEnum> T resolve(Class<?> enumClass, String text) {
        return TextEnumHelper.getMap((Class<T>) enumClass).get(text);
    }

    /**
     * @return a unmodified map
     */
    static <T extends Enum<T> & TextEnum> Map<String, T> getInstanceMap(Class<T> javaType) {
        return TextEnumHelper.getMap(javaType);
    }

}
