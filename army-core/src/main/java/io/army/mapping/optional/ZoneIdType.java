package io.army.mapping.optional;

import io.army.criteria.CriteriaException;
import io.army.mapping.MappingEnv;
import io.army.mapping._ArmyNoInjectionMapping;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.time.ZoneId;

public final class ZoneIdType extends _ArmyNoInjectionMapping {

    public static final ZoneIdType INSTANCE = new ZoneIdType();

    private ZoneIdType() {
    }

    @Override
    public Class<?> javaType() {
        return ZoneId.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        return super.convert(env, nonNull);
    }

    @Override
    public Object beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        //TODO
        throw new UnsupportedOperationException();
    }


}
