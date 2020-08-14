package io.army.struct;

import io.army.lang.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @since 1.0
 */
public abstract class CodeEnumHelper {

    private static final ConcurrentMap<Class<?>, Map<Integer, ? extends CodeEnum>> CODE_MAP_HOLDER =
            new ConcurrentHashMap<>();

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & CodeEnum> Map<Integer, T> getMap(Class<T> clazz) {
        return (Map<Integer, T>) CODE_MAP_HOLDER.get(clazz);
    }

    static <T extends Enum<T> & CodeEnum> void addMap(Class<T> clazz, Map<Integer, T> map) {
        CODE_MAP_HOLDER.putIfAbsent(clazz, map);
    }


    public static void assertCodeEnum(Class<? extends CodeEnum> clazz) throws CodeEnumException {

        for (Field f : clazz.getDeclaredFields()) {
            if (!Modifier.isFinal(f.getModifiers())) {
                throw new CodeEnumException("CodeEnum property[%s.%s]  properties must final.",
                        clazz.getName(),
                        f.getName()
                );
            }

        }

        if (!hasStaticCodeMap(clazz)) {
            throw new CodeEnumException("Not found property[Map<Integer,%s> CODE_MAP = CodeEnum.getCodeMap( %s.class );d]  in CodeEnum[%s] ",
                    clazz.getSimpleName(),
                    clazz.getSimpleName(),
                    clazz.getName());
        }

        if (!hasStaticResolveMethod(clazz)) {
            throw new CodeEnumException("Not found method[public static %s resolve(int code)]",
                    clazz.getSimpleName()
            );
        }
    }


    private static boolean hasStaticCodeMap(Class<? extends CodeEnum> clazz) {
        Field field = findCodeMapField(clazz);

        boolean match = field != null
                && field.getType() == Map.class
                && Modifier.isFinal(field.getModifiers())
                && Modifier.isPrivate(field.getModifiers());

        if (!match || !(field.getGenericType() instanceof ParameterizedType)) {
            match = false;
        } else {
            ParameterizedType type = (ParameterizedType) field.getGenericType();
            match = type.getActualTypeArguments()[0] == Integer.class && type.getActualTypeArguments()[1] == clazz;
        }

        return match;
    }

    private static boolean hasStaticResolveMethod(Class<? extends CodeEnum> clazz) {
        Method method = findResolveMethod(clazz);
        return method != null &&
                method.getReturnType() == clazz
                && Modifier.isStatic(method.getModifiers())
                && Modifier.isPublic(method.getModifiers())
                ;
    }

    private static Method findResolveMethod(Class<?> clazz) {
        Method method;
        try {
            method = clazz.getDeclaredMethod("resolve", int.class);
        } catch (NoSuchMethodException e) {
            method = null;
        }
        return method;
    }

    private static Field findCodeMapField(Class<?> clazz) {
        Field field;
        try {
            field = clazz.getDeclaredField("CODE_MAP");
        } catch (NoSuchFieldException e) {
            field = null;
        }
        return field;
    }


}
