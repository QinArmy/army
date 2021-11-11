package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.H2DataType;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlDataType;

import java.sql.JDBCType;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class NameEnumType extends AbstractMappingType {

    private static final ConcurrentMap<Class<?>, NameEnumType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static NameEnumType build(Class<?> javaType) {
        if (!javaType.isEnum()) {
            throw AbstractMappingType.createNotSupportJavaTypeException(NameEnumType.class, javaType);
        }
        return INSTANCE_MAP.computeIfAbsent(javaType, NameEnumType::new);
    }

    private final Class<?> enumClass;

    private NameEnumType(Class<?> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public Class<?> javaType() {
        return this.enumClass;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.VARCHAR;
    }

    @Override
    public SqlDataType sqlDataType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlDataType sqlDataType;
        switch (serverMeta.database()) {
            case MySQL:
                sqlDataType = MySQLDataType.ENUM;
                break;
            case PostgreSQL:
                sqlDataType = PostgreDataType.VARCHAR;
                break;
            case H2:
                sqlDataType = H2DataType.ENUM;
                break;
            default:
                throw noMappingError(serverMeta);
        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(SqlDataType sqlDataType, Object nonNull) {
        if (!this.enumClass.isInstance(nonNull)) {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return ((Enum<?>) nonNull).name();
    }

    @Override
    public Object convertAfterGet(SqlDataType sqlDataType, Object nonNull) {
        return valueOf((String) nonNull);
    }

    private <T extends Enum<T>> T valueOf(String name) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) this.enumClass;
        return Enum.valueOf(clazz, name);
    }


}
