package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;
import io.army.struct.CodeEnum;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @see Enum
 * @see io.army.struct.CodeEnum
 */
public final class CodeEnumType extends _ArmyNoInjectionMapping {

    private static final ConcurrentMap<Class<?>, CodeEnumType> INSTANCE_MAP = new ConcurrentHashMap<>();


    public static CodeEnumType create(Class<?> javaType) {
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
    public SqlType sqlType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlType sqlDataType;
        switch (serverMeta.database()) {
            case MySQL:
                sqlDataType = MySqlType.INT;
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
    public Object convertBeforeBind(SqlType sqlDataType, Object nonNull) {
        return ((CodeEnum) nonNull).code();
    }

    @Override
    public Object convertAfterGet(SqlType sqlDataType, Object nonNull) {
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
    public static <T extends Enum<T> & CodeEnum> void checkCodeEnum(Class<?> enumClass) {
        CodeEnum.getCodeMap((Class<T>) enumClass);
    }


}
