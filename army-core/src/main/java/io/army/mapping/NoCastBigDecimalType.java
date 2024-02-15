package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.Database;
import io.army.mapping.array.BigDecimalArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;

import java.math.BigDecimal;

public final class NoCastBigDecimalType extends _NumericType implements MappingType.SqlDecimalType {


    public static NoCastBigDecimalType from(Class<?> javaType) {
        if (javaType != BigDecimal.class) {
            throw errorJavaType(NoCastBigDecimalType.class, javaType);
        }
        return INSTANCE;
    }


    public static final NoCastBigDecimalType INSTANCE = new NoCastBigDecimalType();

    /**
     * private constructor
     */
    private NoCastBigDecimalType() {
    }


    @Override
    public Class<?> javaType() {
        return BigDecimal.class;
    }


    @Override
    public DataType map(final ServerMeta meta) {
        if (meta.serverDatabase() == Database.PostgreSQL) {
            return PostgreType.NO_CAST_DECIMAL;
        }
        return BigDecimalType.mapToSqlType(this, meta);
    }

    @Override
    public MappingType arrayTypeOfThis() throws CriteriaException {
        return BigDecimalArrayType.LINEAR;
    }

    @Override
    public BigDecimal convert(MappingEnv env, Object source) throws CriteriaException {
        return BigDecimalType.toBigDecimal(this, map(env.serverMeta()), source, PARAM_ERROR_HANDLER);
    }

    @Override
    public BigDecimal beforeBind(DataType dataType, MappingEnv env, final Object source) {
        return BigDecimalType.toBigDecimal(this, dataType, source, PARAM_ERROR_HANDLER);
    }

    @Override
    public BigDecimal afterGet(DataType dataType, MappingEnv env, final Object source) {
        return BigDecimalType.toBigDecimal(this, dataType, source, ACCESS_ERROR_HANDLER);
    }


}
