package io.army.mapping;

import io.army.criteria.CriteriaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PgSqlType;
import io.army.sqltype.SqlType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class XmlType extends _ArmyBuildInMapping {


    public static final XmlType TEXT_INSTANCE = new XmlType(String.class);

    private static final ConcurrentMap<Class<?>, XmlType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static XmlType from(final Class<?> javaType) {
        final XmlType instance;
        if (javaType == String.class) {
            instance = TEXT_INSTANCE;
        } else {
            instance = INSTANCE_MAP.computeIfAbsent(javaType, XmlType::new);
        }
        return instance;
    }

    private final Class<?> javaType;

    private XmlType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public Class<?> javaType() {
        return this.javaType;
    }

    @Override
    public SqlType map(final ServerMeta meta) {
        final SqlType sqlDataType;
        switch (meta.dialectDatabase()) {
            case MySQL:
                sqlDataType = MySQLType.TEXT;
                break;
            case PostgreSQL:
                sqlDataType = PgSqlType.XML;
                break;
            case Oracle:

            case H2:
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);

        }
        return sqlDataType;
    }

    @Override
    public Object convert(MappingEnv env, Object nonNull) throws CriteriaException {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        //TODO
        if (nonNull instanceof String) {
            return (String) nonNull;
        }
        return StringType.beforeBind(type, nonNull);
    }

    @Override
    public String afterGet(SqlType type, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof String)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        return (String) nonNull;
    }
}
