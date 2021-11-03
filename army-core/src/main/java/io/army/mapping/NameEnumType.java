package io.army.mapping;

import io.army.meta.ServerMeta;
import io.army.sqldatatype.MySQLDataType;
import io.army.sqldatatype.PostgreDataType;
import io.army.sqldatatype.SqlType;

import java.sql.JDBCType;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class NameEnumType extends AbstractMappingType {

    private static final ConcurrentMap<Class<?>, NameEnumType> INSTANCE_MAP = new ConcurrentHashMap<>();

    public static NameEnumType build(Class<?> javaType) {
        if (!javaType.isEnum()) {
            throw MappingMetaUtils.createNotSupportJavaTypeException(NameEnumType.class, javaType);
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
    public SqlType sqlDataType(ServerMeta serverMeta) throws NoMappingException {
        final SqlType sqlDataType;
        switch (serverMeta.database()) {
            case MySQL:
                sqlDataType = MySQLDataType.VARCHAR;
                break;
            case Postgre:
                sqlDataType = PostgreDataType.VARCHAR;
                break;
            default:
                throw noMappingError(javaType(), serverMeta);
        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(ServerMeta serverMeta, Object nonNull) {
        if (!this.enumClass.isInstance(nonNull)) {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return ((Enum<?>) nonNull).name();
    }

    @Override
    public Object convertAfterGet(ServerMeta serverMeta, Object nonNull) {
        return valueOf((String) nonNull);
    }

    private <T extends Enum<T>> T valueOf(String name) {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) this.enumClass;
        return Enum.valueOf(clazz, name);
    }


}
