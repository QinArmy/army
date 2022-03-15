package io.army.struct;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

abstract class TextEnumHelper {

    private TextEnumHelper() {
        throw new UnsupportedOperationException();
    }

    private static final ConcurrentMap<Class<?>, Map<String, ? extends TextEnum>> TEXT_MAP_HOLDER =
            new ConcurrentHashMap<>();


    @SuppressWarnings("unchecked")
    static <T extends Enum<T> & TextEnum> Map<String, T> getMap(final Class<T> enumClass) {
        return (Map<String, T>) TEXT_MAP_HOLDER.computeIfAbsent(enumClass, TextEnumHelper::createTextMap);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Enum<T> & TextEnum> Map<String, ? extends TextEnum> createTextMap(final Class<?> javaType) {
        if (!javaType.isEnum() || !TextEnum.class.isAssignableFrom(javaType)) {
            String m = String.format("%s isn't %s enum.", javaType.getName(), TextEnum.class.getName());
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
        final Map<String, T> map = new HashMap<>((int) (types.length / 0.75f));
        String text;
        for (T type : types) {
            text = type.text();
            for (char c : text.toCharArray()) {
                if (c == '\'' || c == '\\') {
                    String m;
                    m = String.format("%s.%s text() contain quote or back slash", javaType.getName(), type.name());
                    throw new IllegalArgumentException(m);
                }
            }
            if (map.putIfAbsent(text, type) != null) {
                String m;
                m = String.format("%s.%s text[%s] duplicate", enumClass.getName(), type.name(), type.text());
                throw new IllegalArgumentException(m);
            }
        }
        return Collections.unmodifiableMap(map);
    }


}
