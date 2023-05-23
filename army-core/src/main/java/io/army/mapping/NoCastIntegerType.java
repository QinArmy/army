package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;

public final class NoCastIntegerType extends _NumericType._IntegerType {


    public static final NoCastIntegerType INSTANCE = new NoCastIntegerType();


    public static NoCastIntegerType from(final Class<?> fieldType) {
        if (fieldType != Integer.class) {
            throw errorJavaType(NoCastIntegerType.class, fieldType);
        }
        return INSTANCE;
    }


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
    public SqlType map(final ServerMeta meta) {
        final SqlType type;
        switch (meta.dialectDatabase()) {
            case PostgreSQL:
                type = PostgreDataType.NO_CAST_INTEGER;
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
    public Integer convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return IntegerType._convertToInt(this, nonNull, Integer.MIN_VALUE, Integer.MAX_VALUE, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Integer beforeBind(SqlType type, final MappingEnv env, final Object nonNull) {
        return IntegerType._convertToInt(this, nonNull, Integer.MIN_VALUE, Integer.MAX_VALUE, PARAM_ERROR_HANDLER_0);
    }

    @Override
    public Integer afterGet(SqlType type, final MappingEnv env, Object nonNull) {
        return IntegerType._convertToInt(this, nonNull, Integer.MIN_VALUE, Integer.MAX_VALUE, DATA_ACCESS_ERROR_HANDLER_0);
    }


}
