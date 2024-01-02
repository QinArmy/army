/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.mapping;

import io.army.annotation.Mapping;
import io.army.mapping.array.*;
import io.army.meta.MetaException;
import io.army.struct.CodeEnum;
import io.army.util.ArrayUtils;

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
import java.time.temporal.Temporal;
import java.util.BitSet;
import java.util.UUID;

public abstract class _MappingFactory {

    private _MappingFactory() {
        throw new UnsupportedOperationException();
    }


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

        if (javaType == byte[].class) {
            type = VarBinaryType.INSTANCE;
        } else if (Enum.class.isAssignableFrom(javaType)) {
            if (CodeEnum.class.isAssignableFrom(javaType)) {
                type = CodeEnumType.from(javaType);
            } else if (TextEnumType.class.isAssignableFrom(javaType)) {
                type = TextEnumType.from(javaType);
            } else if (Month.class == javaType) {
                type = MonthType.DEFAULT;
            } else if (DayOfWeek.class == javaType) {
                type = DayOfWeekType.DEFAULT;
            } else {
                type = NameEnumType.from(javaType);
            }
        } else if (javaType.isArray()) {
            type = getDefaultArrayType(javaType);
        } else if (javaType == String.class) {
            type = StringType.INSTANCE;
        } else if (javaType == Boolean.class || javaType == boolean.class) {
            type = BooleanType.INSTANCE;
        } else if (Number.class.isAssignableFrom(javaType)) {
            if (javaType == Integer.class) {
                type = IntegerType.INSTANCE;
            } else if (javaType == Long.class) {
                type = LongType.INSTANCE;
            } else if (javaType == BigDecimal.class) {
                type = BigDecimalType.INSTANCE;
            } else if (javaType == BigInteger.class) {
                type = BigIntegerType.INSTANCE;
            } else if (javaType == Double.class) {
                type = DoubleType.INSTANCE;
            } else if (javaType == Float.class) {
                type = FloatType.INSTANCE;
            } else if (javaType == Short.class) {
                type = ShortType.INSTANCE;
            } else if (javaType == Byte.class) {
                type = ByteType.INSTANCE;
            } else {
                type = null;
            }
        } else if (Temporal.class.isAssignableFrom(javaType)) {
            if (javaType == LocalDateTime.class) {
                type = LocalDateTimeType.INSTANCE;
            } else if (javaType == LocalDate.class) {
                type = LocalDateType.INSTANCE;
            } else if (javaType == LocalTime.class) {
                type = LocalTimeType.INSTANCE;
            } else if (javaType == OffsetDateTime.class) {
                type = OffsetDateTimeType.INSTANCE;
            } else if (javaType == ZonedDateTime.class) {
                type = ZonedDateTimeType.INSTANCE;
            } else if (javaType == OffsetTime.class) {
                type = OffsetTimeType.INSTANCE;
            } else if (javaType == Instant.class) {
                type = InstantType.INSTANCE;
            } else if (javaType == Year.class) {
                type = YearType.INSTANCE;
            } else if (javaType == YearMonth.class) {
                type = YearMonthType.INSTANCE;
            } else {
                type = null;
            }
        } else if (ZoneId.class.isAssignableFrom(javaType)) {
            type = ZoneIdType.INSTANCE;
        } else if (BitSet.class.isAssignableFrom(javaType)) {
            type = BitSetType.INSTANCE;
        } else if (javaType == Character.class || javaType == char.class) {
            type = CharacterType.INSTANCE;
        } else if (javaType == MonthDay.class) {
            type = MonthDayType.INSTANCE;
        } else if (javaType == UUID.class) {
            type = UUIDType.INSTANCE;
        } else if (javaType.isPrimitive()) {
            if (javaType == int.class) {
                type = IntegerType.INSTANCE;
            } else if (javaType == long.class) {
                type = LongType.INSTANCE;
            } else if (javaType == double.class) {
                type = DoubleType.INSTANCE;
            } else if (javaType == float.class) {
                type = FloatType.INSTANCE;
            } else if (javaType == short.class) {
                type = ShortType.INSTANCE;
            } else if (javaType == byte.class) {
                type = ByteType.INSTANCE;
            } else {
                type = null;
            }
        } else {
            type = null;
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
        final Class<?> componentType;
        componentType = ArrayUtils.underlyingComponent(arrayJavaType);

        final MappingType type;
        if (componentType == byte.class) {
            type = VarBinaryArrayType.from(arrayJavaType);
        } else if (Enum.class.isAssignableFrom(componentType)) {
            if (CodeEnum.class.isAssignableFrom(componentType)) {
                type = CodeEnumArrayType.from(arrayJavaType);
            } else if (TextEnumType.class.isAssignableFrom(componentType)) {
                type = TextEnumArrayType.from(arrayJavaType);
            } else if (Month.class == componentType) {
                type = MonthArrayType.from(arrayJavaType);
            } else if (DayOfWeek.class == componentType) {
                type = DayOfWeekArrayType.from(arrayJavaType);
            } else {
                type = NameEnumArrayType.from(arrayJavaType);
            }
        } else if (componentType == String.class) {
            type = StringArrayType.from(arrayJavaType);
        } else if (componentType == Boolean.class || componentType == boolean.class) {
            type = BooleanArrayType.from(arrayJavaType);
        } else if (Number.class.isAssignableFrom(componentType)) {
            if (componentType == Integer.class) {
                type = IntegerArrayType.from(arrayJavaType);
            } else if (componentType == Long.class) {
                type = LongArrayType.from(arrayJavaType);
            } else if (componentType == BigDecimal.class) {
                type = BigDecimalArrayType.from(arrayJavaType);
            } else if (componentType == BigInteger.class) {
                type = BigIntegerArrayType.from(arrayJavaType);
            } else if (componentType == Double.class) {
                type = DoubleArrayType.from(arrayJavaType);
            } else if (componentType == Float.class) {
                type = FloatArrayType.from(arrayJavaType);
            } else if (componentType == Short.class) {
                type = ShortArrayType.from(arrayJavaType);
            } else if (componentType == Byte.class) {
                type = ByteArrayType.from(arrayJavaType);
            } else {
                type = null;
            }
        } else if (Temporal.class.isAssignableFrom(componentType)) {
            if (componentType == LocalDateTime.class) {
                type = LocalDateTimeArrayType.from(arrayJavaType);
            } else if (componentType == LocalDate.class) {
                type = LocalDateArrayType.from(arrayJavaType);
            } else if (componentType == LocalTime.class) {
                type = LocalTimeArrayType.from(arrayJavaType);
            } else if (componentType == OffsetDateTime.class) {
                type = OffsetDateTimeArrayType.from(arrayJavaType);
            } else if (componentType == ZonedDateTime.class) {
                type = ZonedDateTimeArrayType.from(arrayJavaType);
            } else if (componentType == OffsetTime.class) {
                type = OffsetTimeArrayType.from(arrayJavaType);
            } else if (componentType == Instant.class) {
                type = InstantArrayType.from(arrayJavaType);
            } else if (componentType == Year.class) {
                type = YearArrayType.from(arrayJavaType);
            } else if (componentType == YearMonth.class) {
                type = YearMonthArrayType.from(arrayJavaType);
            } else {
                type = null;
            }
        } else if (ZoneId.class.isAssignableFrom(componentType)) {
            type = ZoneIdArrayType.from(arrayJavaType);
        } else if (BitSet.class.isAssignableFrom(componentType)) {
            type = BitSetArrayType.from(arrayJavaType);
        } else if (componentType == Character.class || componentType == char.class) {
            type = CharacterArrayType.from(arrayJavaType);
        } else if (componentType == MonthDay.class) {
            type = MonthDayArrayType.from(arrayJavaType);
        } else if (componentType == UUID.class) {
            type = UUIDArrayType.from(arrayJavaType);
        } else if (componentType.isPrimitive()) {
            if (componentType == int.class) {
                type = IntegerArrayType.from(arrayJavaType);
            } else if (componentType == long.class) {
                type = LongArrayType.from(arrayJavaType);
            } else if (componentType == double.class) {
                type = DoubleArrayType.from(arrayJavaType);
            } else if (componentType == float.class) {
                type = FloatArrayType.from(arrayJavaType);
            } else if (componentType == short.class) {
                type = ShortArrayType.from(arrayJavaType);
            } else {
                type = null;
            }
        } else {
            type = null;
        }
        return type;
    }


}
