package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.struct.CodeEnum;
import io.army.util._ClassUtils;
import io.army.util._Collections;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>
 * This class is mapping of enum that implements {@link CodeEnum}.
 * </p>
 *
 * @see Enum
 * @see io.army.struct.CodeEnum
 * @see TextEnumType
 * @see NameEnumType
 * @since 1.0
 */
public final class CodeEnumType extends _ArmyNoInjectionMapping {


    public static CodeEnumType from(final Class<?> javaType) {
        if (!(Enum.class.isAssignableFrom(javaType) && CodeEnum.class.isAssignableFrom(javaType))) {
            throw errorJavaType(CodeEnumType.class, javaType);
        }
        final Class<?> actualType;
        if (javaType.isAnonymousClass()) {
            actualType = javaType.getSuperclass();
        } else {
            actualType = javaType;
        }
        return INSTANCE_MAP.computeIfAbsent(_ClassUtils.getEnumClass(actualType), CodeEnumType::new);
    }

    private static final ConcurrentMap<Class<?>, CodeEnumType> INSTANCE_MAP = _Collections.concurrentHashMap();

    private final Class<?> enumClass;

    private final Map<Integer, ? extends CodeEnum> codeMap;

    /**
     * private constructor
     */
    private CodeEnumType(Class<?> enumClass) {
        this.enumClass = enumClass;
        this.codeMap = getInstanceMap(enumClass);
    }

    @Override
    public Class<?> javaType() {
        return this.enumClass;
    }


    @Override
    public DataType map(final ServerMeta meta) {
        return IntegerType.mapToInteger(this, meta);
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        throw noMatchCompatibleMapping(this, targetType);
    }

    @Override
    public CodeEnum convert(MappingEnv env, final Object source) throws CriteriaException {
        if (!this.enumClass.isInstance(source)) {
            throw PARAM_ERROR_HANDLER_0.apply(this, source);
        }
        return (CodeEnum) source;
    }

    @Override
    public Integer beforeBind(DataType dataType, MappingEnv env, final Object source) {
        if (!this.enumClass.isInstance(source)) {
            throw PARAM_ERROR_HANDLER_0.apply(this, source);
        }
        return ((CodeEnum) source).code();
    }

    @Override
    public CodeEnum afterGet(DataType dataType, MappingEnv env, final Object source) {
        final int code;
        if (source instanceof Integer) {
            code = (Integer) source;
        } else if (source instanceof Long) {
            final long v = (Long) source;
            if (v < Integer.MIN_VALUE || v > Integer.MAX_VALUE) {
                throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, source);
            }
            code = (int) v;
        } else if (source instanceof Short || source instanceof Byte) {
            code = ((Number) source).intValue();
        } else if (source instanceof BigInteger) {
            try {
                code = ((BigInteger) source).intValueExact();
            } catch (ArithmeticException e) {
                throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, source);
            }
        } else if (source instanceof String) {
            try {
                code = Integer.parseInt((String) source);
            } catch (NumberFormatException e) {
                throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, source);
            }
        } else {
            throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, source);
        }
        final CodeEnum codeEnum;
        codeEnum = this.codeMap.get(code);
        if (codeEnum == null) {
            String m = String.format("Not found enum instance for code[%s] in enum[%s].",
                    source, this.enumClass.getName());
            throw new DataAccessException(m);
        }
        return codeEnum;
    }


    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & CodeEnum> Map<Integer, T> getInstanceMap(Class<?> enumClass) {
        return CodeEnum.getInstanceMap((Class<T>) enumClass);
    }


    /*################################## blow private method ##################################*/


}
