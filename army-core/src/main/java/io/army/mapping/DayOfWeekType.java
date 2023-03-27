package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.session.DataAccessException;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;

import java.time.DayOfWeek;

/**
 * <p>
 * This class is mapping class of {@link DayOfWeek}.
 * </p>
 *
 * @since 1.0
 */
public final class DayOfWeekType extends _ArmyNoInjectionMapping {

    public static final DayOfWeekType INSTANCE = new DayOfWeekType();


    public static DayOfWeekType from(final Class<?> javaType) {
        if (javaType != DayOfWeek.class) {
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
        final SqlType type;
        switch (meta.database()) {
            case MySQL:
                type = MySQLTypes.ENUM;
                break;
            case PostgreSQL:
                type = PostgreType.VARCHAR;
                break;
            case Oracle:
            case H2:
            default:
                throw noMappingError(meta);
        }
        return type;
    }

    @Override
    public DayOfWeek convert(final MappingEnv env, final Object nonNull) throws CriteriaException {
        if (!(nonNull instanceof DayOfWeek)) {
            throw dontSupportConvertType(nonNull);
        }
        return (DayOfWeek) nonNull;
    }

    @Override
    public String beforeBind(SqlType type, MappingEnv env, final Object nonNull) throws CriteriaException {
        if (!(nonNull instanceof DayOfWeek)) {
            throw dontSupportConvertType(nonNull);
        }
        return ((DayOfWeek) nonNull).name();
    }

    @Override
    public DayOfWeek afterGet(SqlType type, MappingEnv env, final Object nonNull) throws DataAccessException {
        if (!(nonNull instanceof String)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        try {
            return DayOfWeek.valueOf((String) nonNull);
        } catch (IllegalArgumentException e) {
            throw this.errorValueForMapping(nonNull, e);
        }
    }


}
