package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.SqlType;

public final class DayOfWeekType extends _ArmyNoInjectionMapping {

    public static final DayOfWeekType INSTANCE = new DayOfWeekType();


    public static DayOfWeekType from(final Class<?> javaType) {
        if (javaType != DayOfWeekType.class) {
            throw errorJavaType(DayOfWeekType.class, javaType);
        }
        return INSTANCE;
    }


    private DayOfWeekType() {
    }

    @Override
    public Class<?> javaType() {
        return DayOfWeekType.class;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySqlType.ENUM;
                break;
            case PostgreSQL:
            case Oracle:
            case H2:
            default:
                throw noMappingError(meta);
        }
        return sqlType;
    }

    @Override
    public DayOfWeekType convert(final MappingEnv env, final Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String beforeBind(SqlType sqlType, MappingEnv env, Object nonNull) {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public DayOfWeekType afterGet(SqlType sqlType, MappingEnv env, Object nonNull) {
        //TODO
        throw new UnsupportedOperationException();
    }


}
