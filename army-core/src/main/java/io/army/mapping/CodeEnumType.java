package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.mapping.array.CodeEnumArrayType;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;
import io.army.struct.CodeEnum;
import io.army.util.ArrayUtils;
import io.army.util._ClassUtils;
import io.army.util._Collections;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

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


    public static CodeEnumType from(final Class<?> enumClass) {
        if (!(Enum.class.isAssignableFrom(enumClass) && CodeEnum.class.isAssignableFrom(enumClass))) {
            throw errorJavaType(CodeEnumType.class, enumClass);
        }
        return INSTANCE_MAP.computeIfAbsent(_ClassUtils.getEnumClass(enumClass), CONSTRUCTOR);
    }

    private static final ConcurrentMap<Class<?>, CodeEnumType> INSTANCE_MAP = _Collections.concurrentHashMap();

    private static final Function<Class<?>, CodeEnumType> CONSTRUCTOR = CodeEnumType::new;

    private final Class<?> enumClass;

    private final Map<Integer, ? extends CodeEnum> codeMap;

    /**
     * private constructor
     */
    private CodeEnumType(Class<?> enumClass) {
        this.enumClass = enumClass;
        this.codeMap = CodeEnum.getCodeToEnumMap(enumClass);
    }

    @Override
    public Class<?> javaType() {
        return this.enumClass;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return CodeEnumArrayType.from(ArrayUtils.arrayClassOf(this.enumClass));
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return IntegerType.mapToSqlType(this, meta);
    }

    @Override
    public CodeEnum convert(MappingEnv env, final Object source) throws CriteriaException {
        if (!this.enumClass.isInstance(source)) {
            throw PARAM_ERROR_HANDLER.apply(this, map(env.serverMeta()), source, null);
        }
        return (CodeEnum) source;
    }

    @Override
    public Integer beforeBind(DataType dataType, MappingEnv env, final Object source) {
        if (!this.enumClass.isInstance(source)) {
            throw PARAM_ERROR_HANDLER.apply(this, dataType, source, null);
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
                throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, null);
            }
            code = (int) v;
        } else if (source instanceof Short || source instanceof Byte) {
            code = ((Number) source).intValue();
        } else if (source instanceof BigInteger) {
            try {
                code = ((BigInteger) source).intValueExact();
            } catch (ArithmeticException e) {
                throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, e);
            }
        } else if (source instanceof String) {
            try {
                code = Integer.parseInt((String) source);
            } catch (NumberFormatException e) {
                throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, e);
            }
        } else {
            throw ACCESS_ERROR_HANDLER.apply(this, dataType, source, null);
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



    /*################################## blow private method ##################################*/


}
