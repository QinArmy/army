package io.army.struct;


import io.army.lang.Nullable;

import java.util.Map;

/**
 * 所有要持久化的枚举的基接口.
 * 这个设计是要避免 将 枚举的 {@link Enum#ordinal()} 持久化到数据库,以造成代码的改动不变
 * <p>
 * army 不支持原生枚举,只支持实现 {@link CodeEnum} 的枚举
 * </p>
 *
 * @since 1.0
 */
public interface CodeEnum {

    /**
     * @return 用于持久化到数据库中的 code
     */
    int code();

    /**
     * 枚举的 name
     */
    String name();

    /**
     * @return 用于展示到前端的名称
     */
    default String display() {
        return name();
    }

    default CodeEnum family() {
        return this;
    }


    /*################# static method ############################*/


    @SuppressWarnings("unchecked")
    @Nullable
    static <T extends Enum<T> & CodeEnum> T resolve(final Class<?> enumClass, final int code) {
        if (!(Enum.class.isAssignableFrom(enumClass) && CodeEnum.class.isAssignableFrom(enumClass))) {
            String m = String.format("%s isn't %s type", enumClass, CodeEnum.class.getName());
            throw new IllegalArgumentException(m);
        }
        final Class<T> actualClass;
        if (enumClass.isAnonymousClass()) {
            actualClass = (Class<T>) enumClass.getSuperclass();
        } else {
            actualClass = (Class<T>) enumClass;
        }
        return CodeEnumHelper.getMap(actualClass).get(code);
    }


    static <T extends Enum<T> & CodeEnum> Map<Integer, T> getInstanceMap(Class<T> clazz) throws IllegalArgumentException {
        return CodeEnumHelper.getMap(clazz);
    }


}
