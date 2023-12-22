package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.dialect.UnsupportedDialectException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.DataType;

/**
 * @see UnsignedIntegerType
 */
public final class UnsignedLongType extends _ArmyNoInjectionMapping
        implements MappingType.SqlIntegerType, MappingType.SqlUnsignedNumberType {


    public static final UnsignedLongType INSTANCE = new UnsignedLongType();


    private UnsignedLongType() {
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
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        return UnsignedSqlIntType.mapToDataType(this, meta);
    }

    @Override
    public Object convert(MappingEnv env, Object source) throws CriteriaException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object beforeBind(DataType dataType, MappingEnv env, Object source) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(DataType dataType, MappingEnv env, Object source) throws DataAccessException {
        throw new UnsupportedOperationException();
    }

}
