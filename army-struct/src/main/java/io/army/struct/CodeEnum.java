package io.army.struct;


import io.army.lang.Nullable;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 所有要持久化的枚举的基接口.
 * 这个设计是要避免 将 枚举的 {@link Enum#ordinal()} 持久化到数据库,以造成代码的改动不变
 * <p>
 * army 不支持原生枚举,只支持实现 {@link CodeEnum} 的枚举
 * </p>
 * created  on 2018/9/1.
 */
public interface CodeEnum extends Compare<CodeEnum> {

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
    String display();

    default String localizedDisplay() {
        return display();
    }

    default CodeEnum family() {
        return this;
    }


    /*################# static method ############################*/

    @NonNull
    @Override
    default CompareResult compare(@NonNull CodeEnum o) {
        return compare(this, o);
    }


    static CompareResult compare(CodeEnum codeEnum1, CodeEnum codeEnum2) {
        return CompareResult.resolve(codeEnum1.code() - codeEnum2.code());
    }


    @SuppressWarnings("unchecked")
    @Nullable
    static <T extends Enum<T> & CodeEnum> CodeEnum resolve(Class<?> enumClass, int code) {
        return getCodeMap((Class<T>) enumClass).get(code);
    }


    static <T extends Enum<T> & CodeEnum> Map<Integer, T> getCodeMap(Class<T> clazz) throws CodeEnumException {
        CodeEnumHelper.assertCodeEnum(clazz);

        Map<Integer, T> map = CodeEnumHelper.getMap(clazz);

        if (map != null) {
            return map;
        }

        T[] types = clazz.getEnumConstants();
        map = new HashMap<>((int) (types.length / 0.75f));

        for (T type : types) {
            if (map.containsKey(type.code())) {
                throw new CodeEnumException(String.format("Enum[%s] code[%s]duplicate", clazz.getName(), type.code()));
            }
            map.put(type.code(), type);
        }
        map = Collections.unmodifiableMap(map);
        CodeEnumHelper.addMap(clazz, map);
        return map;
    }


}
