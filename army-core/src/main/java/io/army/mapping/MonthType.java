package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.time.Month;
import java.time.MonthDay;

public final class MonthType extends _ArmyNoInjectionMapping {

    public static final MonthType INSTANCE = new MonthType();

    public static MonthType form(final Class<?> javaType) {
        if (javaType != MonthDay.class) {
            throw errorJavaType(MonthType.class, javaType);
        }
        return INSTANCE;
    }

    private MonthType() {
    }

    @Override
    public Class<?> javaType() {
        return Month.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
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
