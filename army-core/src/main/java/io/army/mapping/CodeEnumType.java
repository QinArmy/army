package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlDataType;
import io.army.struct.CodeEnum;

import java.sql.JDBCType;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @see Enum
 * @see io.army.struct.CodeEnum
 */
public final class CodeEnumType extends _ArmyNoInjectionMapping {

    private static final ConcurrentMap<Class<?>, CodeEnumType> INSTANCE_MAP = new ConcurrentHashMap<>();


    public static CodeEnumType build(Class<?> javaType) {
        if (javaType.isEnum() && CodeEnum.class.isAssignableFrom(javaType)) {
            return INSTANCE_MAP.computeIfAbsent(javaType, CodeEnumType::new);
        } else {
            throw createNotSupportJavaTypeException(CodeEnumType.class, javaType);
        }
    }

    private final Class<?> enumClass;

    private CodeEnumType(Class<?> enumClass) {
        this.enumClass = enumClass;
        checkCodeEnum(enumClass);

    }

    @Override
    public Class<?> javaType() {
        return this.enumClass;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.INTEGER;
    }


    @Override
    public SqlDataType sqlDataType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlDataType sqlDataType;
        switch (serverMeta.database()) {
            case MySQL:
                sqlDataType = MySQLDataType.INT;
                break;
            case PostgreSQL:
                sqlDataType = PostgreDataType.INTEGER;
                break;
            default:
                throw noMappingError(serverMeta);
        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(SqlDataType sqlDataType, Object nonNull) {
        return ((CodeEnum) nonNull).code();
    }

    @Override
    public Object convertAfterGet(SqlDataType sqlDataType, Object nonNull) {
        if (!(nonNull instanceof Integer)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        final Object value;
        value = CodeEnum.resolve(this.enumClass, (Integer) nonNull);
        if (value == null) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return value;
    }


    /*################################## blow private method ##################################*/

    @SuppressWarnings("unchecked")
    private static <T extends Enum<T> & CodeEnum> void checkCodeEnum(Class<?> enumClass) {
        CodeEnum.getCodeMap((Class<T>) enumClass);
    }


}
