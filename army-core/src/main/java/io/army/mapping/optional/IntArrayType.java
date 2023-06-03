package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;
import io.army.util.ArrayUtils;

public final class IntArrayType extends _ArmyNoInjectionMapping
        implements MappingType.SqlIntegerType, MappingType.SqlArrayType {

    public static IntArrayType from(final Class<?> javaType) {
        final IntArrayType instance;
        if (javaType == int[].class) {
            instance = LINEAR;
        } else if (ArrayUtils.underlyingComponent(javaType) == int.class) {
            instance = new IntArrayType(javaType);
        } else {
            throw errorJavaType(IntArrayType.class, javaType);
        }
        return instance;
    }

    public static IntArrayType LINEAR = new IntArrayType(int[].class);


    private final Class<?> javaType;

    private IntArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }


    @Override
    public LengthType lengthType() {
        return null;
    }

    @Override
    public MappingType elementType() {
        return null;
    }


    @Override
    public SqlType map(ServerMeta meta) throws NotSupportDialectException {
        return null;
    }

    @Override
    public <Z> MappingType compatibleFor(Class<Z> targetType) throws NoMatchMappingException {
        return null;
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return null;
    }

    @Override
    public Object beforeBind(SqlType type, MappingEnv env, Object nonNull) throws CriteriaException {
        return null;
    }

    @Override
    public Object afterGet(SqlType type, MappingEnv env, Object nonNull) throws DataAccessException {
        return null;
    }


}
