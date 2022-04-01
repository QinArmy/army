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
    static <T extends Enum<T> & CodeEnum> T resolve(Class<?> enumClass, int code) {
        return CodeEnumHelper.getMap((Class<T>) enumClass).get(code);
    }


    static <T extends Enum<T> & CodeEnum> Map<Integer, T> getInstanceMap(Class<T> clazz) throws IllegalArgumentException {
        return CodeEnumHelper.getMap(clazz);
    }


}
