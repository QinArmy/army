package io.army.mapping;

import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLTypes;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.struct.CodeEnum;
import io.army.util._ClassUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @see Enum
 * @see io.army.struct.CodeEnum
 */
public final class CodeEnumType extends _ArmyNoInjectionMapping {

    private static final ConcurrentMap<Class<?>, CodeEnumType> INSTANCE_MAP = new ConcurrentHashMap<>();


    public static CodeEnumType from(final Class<?> fieldType) {
        if (!(Enum.class.isAssignableFrom(fieldType) && CodeEnum.class.isAssignableFrom(fieldType))) {
            throw errorJavaType(CodeEnumType.class, fieldType);
        }
        return INSTANCE_MAP.computeIfAbsent(_ClassUtils.getEnumClass(fieldType), CodeEnumType::new);
    }

    private final Class<?> enumClass;

    private final Map<Integer, ? extends CodeEnum> codeMap;

    private CodeEnumType(Class<?> enumClass) {
        this.enumClass = enumClass;
        this.codeMap = getCodeMap(enumClass);
    }

    @Override
    public Class<?> javaType() {
        return this.enumClass;
    }


    @Override
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySQLTypes.INT;
                break;
            case PostgreSQL:
                sqlType = PostgreType.INTEGER;
                break;
            default:
                throw noMappingError(meta);
        }
        return sqlType;
    }

    @Override
    public Integer beforeBind(SqlType type, MappingEnv env, Object nonNull) {
        if (!this.enumClass.isInstance(nonNull)) {
            throw outRangeOfSqlType(type, nonNull);
        }
        return ((CodeEnum) nonNull).code();
    }

    @Override
    public CodeEnum afterGet(SqlType type, MappingEnv env, Object nonNull) {
        if (!(nonNull instanceof Integer)) {
            throw errorJavaTypeForSqlType(type, nonNull);
        }
        final CodeEnum value;
        value = this.codeMap.get(nonNull);
        if (value == null) {
            String m = String.format("Not found enum instance for code[%s] in enum[%s]."
                    , nonNull, this.enumClass.getName());
            throw errorValueForSqlType(type, nonNull, new MetaException(m));
        }
        return value;
    }


    /*################################## blow private method ##################################*/

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & CodeEnum> Map<Integer, T> getCodeMap(Class<?> enumClass) {
        return CodeEnum.getInstanceMap((Class<T>) enumClass);
    }


}
