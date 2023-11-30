package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

public final class NoCastIntegerType extends _NumericType._IntegerType {

    public static NoCastIntegerType from(final Class<?> fieldType) {
        if (fieldType != Integer.class) {
            throw errorJavaType(NoCastIntegerType.class, fieldType);
        }
        return INSTANCE;
    }

    public static final NoCastIntegerType INSTANCE = new NoCastIntegerType();

    /**
     * private constructor
     */
    private NoCastIntegerType() {
    }

    @Override
    public Class<?> javaType() {
        return Integer.class;
    }

    @Override
    public LengthType lengthType() {
        return LengthType.DEFAULT;
    }

    @Override
    public DataType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.serverDatabase()) {
            case PostgreSQL:
                type = PostgreType.NO_CAST_INTEGER;
                break;
            case MySQL:
            case Oracle:
            case H2:
            default:
                type = IntegerType.mapToInteger(this, meta);
        }
        return type;
    }

    @Override
    public Integer convert(MappingEnv env, Object source) throws CriteriaException {
        return IntegerType.toInt(this, map(env.serverMeta()), source, Integer.MIN_VALUE, Integer.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Integer beforeBind(DataType dataType, final MappingEnv env, final Object source) {
        return IntegerType.toInt(this, dataType, source, Integer.MIN_VALUE, Integer.MAX_VALUE, PARAM_ERROR_HANDLER);
    }

    @Override
    public Integer afterGet(DataType dataType, final MappingEnv env, Object source) {
        return IntegerType.toInt(this, dataType, source, Integer.MIN_VALUE, Integer.MAX_VALUE, ACCESS_ERROR_HANDLER);
    }


}
