package io.army.struct;


import javax.annotation.Nullable;
import java.util.Map;

/**
 * This interface is base interface of the enum that mapping to {@code io.army.mapping.CodeEnumType}.
 * <p>
 * Army will persist {@link #code()} to database table column not {@link Enum#ordinal()}.
 * If you want to persist {@link Enum#name()},then you should use {@code io.army.mapping.NameEnumType},
 * but never persist {@link Enum#ordinal()}.
*
 * @see TextEnum
 * @since 1.0
 */
public interface CodeEnum {


    /**
     * @see Enum#name()
     */
    String name();

    /**
     * @return code that can representing this enum instance
     */
    int code();


    /**
     * @return enum alias
     */
    default String alias() {
        return name();
    }

    default CodeEnum family() {
        return this;
    }


    /*################# static method ############################*/


    @Nullable
    static <T extends Enum<T> & CodeEnum> T resolve(final Class<?> enumClass, final int code) {
        final Map<Integer, T> map;
        map = EnumHelper.getCodeMap(enumClass);
        return map.get(code);
    }

    /**
     * <p>
     * see {@code io.army.mapping.CodeEnumType#getInstanceMap(java.lang.Class)}
     *
     *
     * @return instance map ; unmodified map
     */
    static <T extends Enum<T> & CodeEnum> Map<Integer, T> getInstanceMap(Class<T> clazz) throws IllegalArgumentException {
        return EnumHelper.getCodeMap(clazz);
    }

    static Map<Integer, ? extends CodeEnum> getCodeToEnumMap(final Class<?> javaType) {
        if (!(Enum.class.isAssignableFrom(javaType) && CodeEnum.class.isAssignableFrom(javaType))) {
            String m = String.format("%s not %s type", javaType.getName(), CodeEnum.class.getName());
            throw new IllegalArgumentException(m);
        }
        return EnumHelper.getCodeMap(javaType);
    }


}
