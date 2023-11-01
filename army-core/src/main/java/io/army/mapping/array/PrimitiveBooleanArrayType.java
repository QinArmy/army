package io.army.mapping.array;

import io.army.criteria.CriteriaException;
import io.army.dialect.NotSupportDialectException;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.NoMatchMappingException;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.SQLType;

public final class PrimitiveBooleanArrayType extends _ArmyNoInjectionMapping
        implements MappingType.SqlArrayType, MappingType.SqlBooleanType {


    public static final PrimitiveBooleanArrayType LINEAR = new PrimitiveBooleanArrayType(boolean[].class);


    private final Class<?> javaType;

    private PrimitiveBooleanArrayType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return null;
    }

    @Override
    public SQLType map(ServerMeta meta) throws NotSupportDialectException {
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
    public Object beforeBind(SQLType type, MappingEnv env, Object nonNull) throws CriteriaException {
        return null;
    }

    @Override
    public Object afterGet(SQLType type, MappingEnv env, Object nonNull) throws DataAccessException {
        return null;
    }

    @Override
    public MappingType elementType() {
        return null;
    }

}
