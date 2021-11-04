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

    static <T extends Enum<T> & CodeEnum> Map<Integer, T> getMap(final Class<T> clazz) {
        @SuppressWarnings("unchecked")
        Map<Integer, T> map = (Map<Integer, T>) CODE_MAP_HOLDER.get(clazz);
        if (map != null) {
            return map;
        }

        for (Field f : clazz.getDeclaredFields()) {
            if (!Modifier.isFinal(f.getModifiers())) {
                throw new CodeEnumException("CodeEnum property[%s.%s]  properties must final.",
                        clazz.getName(),
                        f.getName()
                );
            }

        }

        final T[] types = clazz.getEnumConstants();
        map = new HashMap<>((int) (types.length / 0.75f));

        for (T type : types) {
            if (map.containsKey(type.code())) {
                throw new CodeEnumException(String.format("Enum[%s] code[%s]duplicate", clazz.getName(), type.code()));
            }
            map.put(type.code(), type);
        }
        map = Collections.unmodifiableMap(map);
        CODE_MAP_HOLDER.putIfAbsent(clazz, map);
        return map;
    }


}
