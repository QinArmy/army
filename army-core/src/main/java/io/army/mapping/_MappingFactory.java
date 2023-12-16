package io.army.mapping;

import io.army.annotation.Mapping;
import io.army.mapping.array.IntegerArrayType;
import io.army.meta.MetaException;
import io.army.struct.CodeEnum;
import io.army.util.ArrayUtils;
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.time.*;
import java.util.BitSet;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public abstract class _MappingFactory {

    private _MappingFactory() {
        throw new UnsupportedOperationException();
    }


    private static final Map<Class<?>, Function<Class<?>, MappingType>> DEFAULT_MAPPING_MAP = createDefaultMappingMap();


    public static MappingType getDefault(Class<?> javaType) throws MetaException {
        final MappingType type;
        type = getDefaultIfMatch(javaType);
        if (type == null) {
            String m = String.format("Not found default mapping for %s .", javaType.getName());
            throw new MetaException(m);
        }
        return type;
    }

    @Nullable
    public static MappingType getDefaultIfMatch(final Class<?> javaType) {
        final MappingType type;
        if (!Enum.class.isAssignableFrom(javaType)) {
            if (javaType.isArray()) {
                type = getDefaultArrayType(javaType);
            } else {
                final Function<Class<?>, MappingType> function;
                function = DEFAULT_MAPPING_MAP.get(javaType);
                if (function == null) {
                    type = null;
                } else {
                    type = function.apply(javaType);
                }
            }

        } else if (CodeEnum.class.isAssignableFrom(javaType)) {
            type = CodeEnumType.from(javaType);
        } else if (TextEnumType.class.isAssignableFrom(javaType)) {
            type = TextEnumType.from(javaType);
        } else if (Month.class.isAssignableFrom(javaType)) {
            type = MonthType.DEFAULT;
        } else if (DayOfWeek.class.isAssignableFrom(javaType)) {
            type = DayOfWeekType.DEFAULT;
        } else {
            type = NameEnumType.from(javaType);
        }
        return type;
    }


    public static MappingType map(final Mapping mapping, final Field field) {
        final Class<?> mappingClass;
        try {
            mappingClass = Class.forName(mapping.value());
        } catch (ClassNotFoundException e) {
            String m = String.format("Not found %s.%s mapping type %s ."
                    , field.getDeclaringClass().getName(), field.getName(), mapping.value());
            throw new MetaException(m);
        }
        if (!MappingType.class.isAssignableFrom(mappingClass)) {
            String m = String.format("%s.%s mapping type %s error."
                    , field.getDeclaringClass().getName(), field.getName(), mapping.value());
            throw new MetaException(m);
        }
        final boolean textMapping, elementMapping;
        textMapping = TextMappingType.class.isAssignableFrom(mappingClass);
        elementMapping = MultiGenericsMappingType.class.isAssignableFrom(mappingClass);

        try {
            final Method method;
            final Object mappingType;
            if (textMapping && elementMapping) {
                method = mappingClass.getDeclaredMethod("forMixture", Class.class, Class[].class, Charset.class);
                assertFactoryMethod(method);
                final Charset charset = Charset.forName(mapping.charset());
                mappingType = method.invoke(null, field.getType(), mapping.elements(), charset);
            } else if (textMapping) {
                method = mappingClass.getDeclaredMethod("forText", Class.class, Charset.class);
                assertFactoryMethod(method);
                mappingType = method.invoke(null, field.getType(), Charset.forName(mapping.charset()));
            } else if (elementMapping) {
                method = mappingClass.getDeclaredMethod("forElements", Class.class, Class[].class);
                assertFactoryMethod(method);
                mappingType = method.invoke(null, field.getType(), mapping.elements());
            } else {
                method = mappingClass.getDeclaredMethod("from", Class.class);
                assertFactoryMethod(method);
                mappingType = method.invoke(null, field.getType());
            }
            if (mappingType == null) {
                String m = String.format("%s %s factory method return null.", mappingClass.getName(), method.getName());
                throw new MetaException(m);
            }
            return (MappingType) mappingType;
        } catch (NoSuchMethodException | IllegalAccessException e) {
            String m = String.format("%s factory method definition error for %s.%s"
                    , mappingClass.getName(), field.getDeclaringClass().getName(), field.getName());
            throw new MetaException(m, e);
        } catch (InvocationTargetException e) {
            String m = String.format("Factory method of %s invocation occur error for %s.%s"
                    , mappingClass.getName(), field.getDeclaringClass().getName(), field.getName());
            throw new MetaException(m, e);
        } catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
            String m = String.format("%s.%s %s.charset() error."
                    , field.getDeclaringClass().getName(), field.getName(), Mapping.class.getName());
            throw new MetaException(m, e);
        }

    }


    public static MappingType map(Class<?> mappingClass, Class<?> javaType) throws MetaException {
        final MappingType mappingType;
        if (CodeEnum.class.isAssignableFrom(javaType)) {
            if (!javaType.isEnum()) {
                String m = String.format("%s isn't enum.", javaType.getName());
                throw new MetaException(m);
            }
            mappingType = CodeEnumType.from(javaType);
        } else if (javaType.isEnum()) {
            mappingType = NameEnumType.from(javaType);
        } else {
            mappingType = createMappingType(mappingClass, javaType);
        }
        return mappingType;
    }

    private static MappingType createMappingType(Class<?> mappingClass, Class<?> javaType) throws MetaException {
        if (!MappingType.class.isAssignableFrom(mappingClass)) {
            String m = String.format("%s isn't %s instance.", mappingClass.getName(), MappingType.class.getName());
            throw new MetaException(m);
        }
        try {
            final Method method;
            method = mappingClass.getMethod("create", Class.class);
            if (!(Modifier.isPublic(method.getModifiers())
                    && Modifier.isStatic(method.getModifiers())
                    && mappingClass == method.getReturnType())) {
                String m = String.format("%s create(Class<?> typeClass) method definite error."
                        , mappingClass.getName());
                throw new MetaException(m);
            }
            final MappingType mappingType;
            mappingType = (MappingType) method.invoke(null, javaType);
            if (mappingType == null) {
                String m = String.format("%s create(Class<?> javaType) method return null.", mappingClass.getName());
                throw new MetaException(m);
            }
            final Class<?> actualType = mappingType.javaType();
            if (!actualType.isAssignableFrom(javaType)) {
                String m = String.format("%s javaType() return value[%s] and java type[%s] not match."
                        , mappingClass.getName(), actualType.getName(), javaType.getName());
                throw new MetaException(m);
            }
            return mappingType;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new MetaException(e.getMessage(), e);
        }

    }


    private static Map<Class<?>, Function<Class<?>, MappingType>> createDefaultMappingMap() {
        final Map<Class<?>, Function<Class<?>, MappingType>> map = _Collections.hashMap();

        map.put(Byte.class, ByteType::from);
        map.put(Short.class, ShortType::from);
        map.put(Integer.class, IntegerType::from);
        map.put(Long.class, LongType::from);

        map.put(Float.class, FloatType::from);
        map.put(Double.class, DoubleType::from);
        map.put(BigDecimal.class, BigDecimalType::from);
        map.put(BigInteger.class, BigIntegerType::from);

        map.put(Boolean.class, BooleanType::from);
        map.put(byte[].class, VarBinaryType::from);
        map.put(String.class, StringType::from);
        map.put(LocalDateTime.class, LocalDateTimeType::from);

        map.put(LocalDate.class, LocalDateType::from);
        map.put(LocalTime.class, LocalTimeType::from);
        map.put(MonthDay.class, MonthDayType::from);
        map.put(YearMonth.class, YearMonthType::from);

        map.put(Year.class, YearType::from);
        map.put(OffsetDateTime.class, OffsetDateTimeType::from);
        map.put(OffsetTime.class, OffsetTimeType::from);
        map.put(BitSet.class, BitSetType::from);

        map.put(Character.class, CharacterType::from);
        return Collections.unmodifiableMap(map);
    }


    private static void assertFactoryMethod(final Method method) {
        final int modifiers;
        modifiers = method.getModifiers();
        if (!(Modifier.isPublic(modifiers)
                && Modifier.isStatic(modifiers)
                && method.getDeclaringClass().isAssignableFrom(method.getReturnType()))) {
            String m = String.format("Not found %s method (static factory method) in %s ."
                    , method.getName(), method.getDeclaringClass().getName());
            throw new MetaException(m);
        }

    }

    @Nullable
    private static MappingType getDefaultArrayType(final Class<?> arrayJavaType) {
        MappingType type;
        if (ArrayUtils.underlyingComponent(arrayJavaType) == Integer.class) {
            type = IntegerArrayType.from(arrayJavaType);
        } else {
            type = null;
        }
        return type;
    }


}
