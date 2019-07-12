package io.army.struct;

import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * created  on 2019-02-24.
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
        ReflectionUtils.doWithFields(clazz, f -> {
            if (!Modifier.isFinal(f.getModifiers())) {
                throw new CodeEnumException("CodeEnum property[%s.%s]  properties must final.",
                        clazz.getName(),
                        f.getName()
                );
            }

            if (!Modifier.isStatic(f.getModifiers())
                    && !Modifier.isPrivate(f.getModifiers())) {
                throw new CodeEnumException("CodeEnum property[%s.%s] enum properties must private",
                        clazz.getName(),
                        f.getName()
                );
            }

        });

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
        Field field = ReflectionUtils.findField(clazz, "CODE_MAP");

        boolean match = field != null
                && field.getType() == Map.class
                && Modifier.isFinal(field.getModifiers())
                && Modifier.isPrivate(field.getModifiers())
                && field.getGenericType() != null;

        if (!match || !(field.getGenericType() instanceof ParameterizedType)) {
            match = false;
        } else {
            ParameterizedType type = (ParameterizedType) field.getGenericType();
            match = type.getActualTypeArguments()[0] == Integer.class && type.getActualTypeArguments()[1] == clazz;
        }

        return match;
    }

    private static boolean hasStaticResolveMethod(Class<? extends CodeEnum> clazz) {
        Method method = ReflectionUtils.findMethod(clazz, "resolve", int.class);
        return method != null &&
                method.getReturnType() == clazz
                && Modifier.isStatic(method.getModifiers())
                && Modifier.isPublic(method.getModifiers())
                ;
    }


}
