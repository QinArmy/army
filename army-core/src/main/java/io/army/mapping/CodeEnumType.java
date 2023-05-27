package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;
import io.army.struct.CodeEnum;
import io.army.util._ClassUtils;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>
 * This class is mapping of enum that implements {@link CodeEnum}.
 * </p>
 *
 * @see Enum
 * @see io.army.struct.CodeEnum
 * @since 1.0
 */
public final class CodeEnumType extends _ArmyNoInjectionMapping {

    private static final ConcurrentMap<Class<?>, CodeEnumType> INSTANCE_MAP = new ConcurrentHashMap<>();


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

    private final Class<?> enumClass;

    private final Map<Integer, ? extends CodeEnum> codeMap;

    private CodeEnumType(Class<?> enumClass) {
        this.enumClass = enumClass;
        this.codeMap = getCodeMap(enumClass);
    }

    @Override
    public Class<?> javaType() {
        return this.enumClass;
    }


    @Override
    public SqlType map(final ServerMeta meta) {
        return IntegerType.mapToInteger(this, meta);
    }

    @Override
    public MappingType compatibleFor(Class<?> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public CodeEnum convert(MappingEnv env, final Object nonNull) throws CriteriaException {
        if (!this.enumClass.isInstance(nonNull)) {
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return (CodeEnum) nonNull;
    }

    @Override
    public Integer beforeBind(SqlType type, MappingEnv env, final Object nonNull) {
        if (!this.enumClass.isInstance(nonNull)) {
            throw PARAM_ERROR_HANDLER_0.apply(this, nonNull);
        }
        return ((CodeEnum) nonNull).code();
    }

    @Override
    public CodeEnum afterGet(SqlType type, MappingEnv env, final Object nonNull) {
        final int code;
        if (nonNull instanceof Integer) {
            code = (Integer) nonNull;
        } else if (nonNull instanceof Long) {
            final long v = (Long) nonNull;
            if (v < Integer.MIN_VALUE || v > Integer.MAX_VALUE) {
                throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, nonNull);
            }
            code = (int) v;
        } else if (nonNull instanceof Short || nonNull instanceof Byte) {
            code = ((Number) nonNull).intValue();
        } else if (nonNull instanceof BigInteger) {
            try {
                code = ((BigInteger) nonNull).intValueExact();
            } catch (ArithmeticException e) {
                throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, nonNull);
            }
        } else if (nonNull instanceof String) {
            try {
                code = Integer.parseInt((String) nonNull);
            } catch (NumberFormatException e) {
                throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, nonNull);
            }
        } else {
            throw DATA_ACCESS_ERROR_HANDLER_0.apply(this, nonNull);
        }
        final CodeEnum codeEnum;
        codeEnum = this.codeMap.get(code);
        if (codeEnum == null) {
            String m = String.format("Not found enum instance for code[%s] in enum[%s].",
                    nonNull, this.enumClass.getName());
            throw new DataAccessException(m);
        }
        return codeEnum;
    }


    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & CodeEnum> Map<Integer, T> getCodeMap(Class<?> enumClass) {
        return CodeEnum.getInstanceMap((Class<T>) enumClass);
    }


    /*################################## blow private method ##################################*/


}
