package io.army.mapping;

import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SqlType;
import io.army.struct.CodeEnum;
import io.army.struct.CodeEnumException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @see Enum
 * @see io.army.struct.CodeEnum
 */
public final class CodeEnumType extends _ArmyNoInjectionMapping {

    private static final ConcurrentMap<Class<?>, CodeEnumType> INSTANCE_MAP = new ConcurrentHashMap<>();


    public static CodeEnumType create(Class<?> javaType) {
        if (!javaType.isEnum() || !CodeEnum.class.isAssignableFrom(javaType)) {
            throw errorJavaType(CodeEnumType.class, javaType);
        }
        return INSTANCE_MAP.computeIfAbsent(javaType, CodeEnumType::new);
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
    public SqlType map(ServerMeta meta) {
        final SqlType sqlType;
        switch (meta.database()) {
            case MySQL:
                sqlType = MySqlType.INT;
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
    public Integer beforeBind(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        if (!(nonNull instanceof Enum && nonNull instanceof CodeEnum)) {
            throw outRangeOfSqlType(sqlType, nonNull);
        }
        return ((CodeEnum) nonNull).code();
    }

    @Override
    public CodeEnum afterGet(SqlType sqlType, MappingEnvironment env, Object nonNull) {
        if (!(nonNull instanceof Integer)) {
            throw errorJavaTypeForSqlType(sqlType, nonNull);
        }
        final CodeEnum value;
        value = CodeEnum.resolve(this.enumClass, (Integer) nonNull);
        if (value == null) {
            String m = String.format("Not found enum instance for code[%s] in enum[%s]."
                    , nonNull, this.enumClass.getName());
            throw errorValueForSqlType(sqlType, nonNull, new MetaException(m));
        }
        return value;
    }


    /*################################## blow private method ##################################*/

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & CodeEnum> void checkCodeEnum(Class<?> enumClass) {
        try {
            CodeEnum.getCodeMap((Class<T>) enumClass);
        } catch (CodeEnumException e) {
            throw new MetaException(e.getMessage(), e);
        }
    }


}
