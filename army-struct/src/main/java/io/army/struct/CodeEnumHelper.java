package io.army.struct;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @since 1.0
 */
abstract class CodeEnumHelper {

    private CodeEnumHelper() {
        throw new UnsupportedOperationException();
    }

    private static final ConcurrentMap<Class<?>, Map<Integer, ? extends CodeEnum>> CODE_MAP_HOLDER =
            new ConcurrentHashMap<>();


    @SuppressWarnings("unchecked")
    static <T extends Enum<T> & CodeEnum> Map<Integer, T> getMap(final Class<T> enumClass) {
        return (Map<Integer, T>) CODE_MAP_HOLDER.computeIfAbsent(enumClass, CodeEnumHelper::createCodeMap);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Enum<T> & CodeEnum> Map<Integer, ? extends CodeEnum> createCodeMap(final Class<?> javaType) {
        if (!javaType.isEnum() || TextEnum.class.isAssignableFrom(javaType)) {
            String m = String.format("%s isn't %s enum.", javaType.getName(), CodeEnum.class.getName());
            throw new IllegalArgumentException(m);
        }
        final Class<T> enumClass = (Class<T>) javaType;
        for (Field f : enumClass.getDeclaredFields()) {
            if (!Modifier.isFinal(f.getModifiers())) {
                String m;
                m = String.format("%s.%s must final.", enumClass.getName(), f.getName());
                throw new IllegalArgumentException(m);
            }
        }
        final T[] types = enumClass.getEnumConstants();
        final Map<Integer, T> map = new HashMap<>((int) (types.length / 0.75f));
        for (T type : types) {
            if (map.putIfAbsent(type.code(), type) != null) {
                String m;
                m = String.format("%s.%s code[%s] duplicate", enumClass.getName(), type.name(), type.code());
                throw new IllegalArgumentException(m);
            }
        }
        return Collections.unmodifiableMap(map);
    }


}
