package io.army.struct;

import io.army.lang.Nullable;

import java.util.Map;

/**
 * This interface is base interface of the enum that mapping to {@code io.army.mapping.TextEnumType}.
 * <p>
 * Army will persist {@link #text()} to database table column not {@link Enum#name()}.
 * </p>
 *
 * @see CodeEnum
 * @since 1.0
 */
public interface TextEnum {

    String name();

    String text();

    default TextEnum family() {
        return this;
    }


    @Nullable
    static <T extends Enum<T> & TextEnum> T resolve(final Class<?> enumClass, final String text) {
        final Map<String, T> map;
        map = EnumHelper.getTextMap(enumClass);
        return map.get(text);
    }

    /**
     * @return a unmodified map
     */
    static <T extends Enum<T> & TextEnum> Map<String, T> getInstanceMap(Class<T> javaType) {
        return EnumHelper.getTextMap(javaType);
    }

}
