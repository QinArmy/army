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
abstract class EnumHelper {

    private EnumHelper() {
        throw new UnsupportedOperationException();
    }

    private static final ConcurrentMap<Class<?>, Map<Integer, ? extends CodeEnum>> CODE_MAP_HOLDER =
            new FinalConcurrentHashMap<>();

    private static final ConcurrentMap<Class<?>, Map<String, ? extends TextEnum>> TEXT_MAP_HOLDER =
            new FinalConcurrentHashMap<>();


    @SuppressWarnings("unchecked")
    static <T extends Enum<T> & CodeEnum> Map<Integer, T> getCodeMap(final Class<?> enumClass) {
        final Class<T> actualClass;
        if (enumClass.isAnonymousClass()) {
            actualClass = (Class<T>) enumClass.getSuperclass();
        } else {
            actualClass = (Class<T>) enumClass;
        }
        return (Map<Integer, T>) CODE_MAP_HOLDER.computeIfAbsent(actualClass, EnumHelper::createCodeMap);
    }

    @SuppressWarnings("unchecked")
    static <T extends Enum<T> & TextEnum> Map<String, T> getTextMap(final Class<?> enumClass) {
        final Class<T> actualClass;
        if (enumClass.isAnonymousClass()) {
            actualClass = (Class<T>) enumClass.getSuperclass();
        } else {
            actualClass = (Class<T>) enumClass;
        }
        return (Map<String, T>) TEXT_MAP_HOLDER.computeIfAbsent(actualClass, EnumHelper::createTextMap);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Enum<T> & CodeEnum> Map<Integer, ? extends CodeEnum> createCodeMap(final Class<?> javaType)
            throws IllegalArgumentException {
        if (!javaType.isEnum() || !CodeEnum.class.isAssignableFrom(javaType)) {
            String m = String.format("%s isn't %s enum.", javaType.getName(), CodeEnum.class.getName());
            throw new IllegalArgumentException(m);
        }
        assertFieldIsFinal(javaType);
        final Class<T> enumClass = (Class<T>) javaType;
        final T[] types = enumClass.getEnumConstants();
        final Map<Integer, CodeEnum> map = new FinalHashMap<>((int) (types.length / 0.75f));
        for (T type : types) {
            if (map.putIfAbsent(type.code(), type) != null) {
                String m;
                m = String.format("%s.%s code[%s] duplicate", enumClass.getName(), type.name(), type.code());
                throw new IllegalArgumentException(m);
            }
        }
        return Collections.unmodifiableMap(map);
    }


    @SuppressWarnings("unchecked")
    private static <T extends Enum<T> & TextEnum> Map<String, ? extends TextEnum> createTextMap(final Class<?> javaType) {
        if (!javaType.isEnum() || !TextEnum.class.isAssignableFrom(javaType)) {
            String m = String.format("%s isn't %s enum.", javaType.getName(), TextEnum.class.getName());
            throw new IllegalArgumentException(m);
        }
        assertFieldIsFinal(javaType);

        final Class<T> enumClass = (Class<T>) javaType;
        final T[] types = enumClass.getEnumConstants();
        final Map<String, T> map = new FinalHashMap<>((int) (types.length / 0.75f));
        String text;
        for (T type : types) {
            text = type.text();
            if (map.putIfAbsent(text, type) != null) {
                String m;
                m = String.format("%s.%s text[%s] duplicate", enumClass.getName(), type.name(), type.text());
                throw new IllegalArgumentException(m);
            }
        }
        return Collections.unmodifiableMap(map);
    }


    private static void assertFieldIsFinal(final Class<?> enumClass) {
        for (Field f : enumClass.getDeclaredFields()) {
            if (!Modifier.isFinal(f.getModifiers())) {
                String m;
                m = String.format("%s.%s must final.", enumClass.getName(), f.getName());
                throw new IllegalArgumentException(m);
            }
        }
    }


    private static final class FinalHashMap<K, V> extends HashMap<K, V> {

        private FinalHashMap(int initialCapacity) {
            super(initialCapacity);
        }


    }//FinalHashMap


    private static final class FinalConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

        private FinalConcurrentHashMap() {
        }

    }//FinalConcurrentHashMap


}
