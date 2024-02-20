package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.mapping.LongType;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping._NumericType;
import io.army.mapping.array.LongArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;

public final class NoCastLongType extends _NumericType._IntegerType implements NoCastType {

    public static NoCastLongType from(final Class<?> fieldType) {
        if (fieldType != Long.class) {
            throw errorJavaType(NoCastLongType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final NoCastLongType INSTANCE = new NoCastLongType();

    /**
     * private constructor
     */
    private NoCastLongType() {
    }

    @Override
    public Class<?> javaType() {
        return Long.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.LONG;
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return LongArrayType.LINEAR;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        return LongType.mapToDataType(this, meta);
    }


    @Override
    public Long convert(MappingEnv env, Object source) throws CriteriaException {
        return LongType.toLong(this, map(env.serverMeta()), source, Long.MIN_VALUE, Long.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Long beforeBind(DataType dataType, MappingEnv env, Object source) {
        return LongType.toLong(this, dataType, source, Long.MIN_VALUE, Long.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Long afterGet(DataType dataType, MappingEnv env, Object source) {
        return LongType.toLong(this, dataType, source, Long.MIN_VALUE, Long.MAX_VALUE, ACCESS_ERROR_HANDLER);
    }


}
