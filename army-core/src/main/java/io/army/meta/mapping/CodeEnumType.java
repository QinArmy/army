package io.army.meta.mapping;

import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.sqltype.SQLDataType;
import io.army.struct.CodeEnum;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @see Enum
 * @see io.army.struct.CodeEnum
 */
public final class CodeEnumType extends AbstractMappingType {

    private static final Map<Database, SQLDataType> DATA_TYPE_MAP = IntegerType.DATA_TYPE_MAP;

    private static final ConcurrentMap<Class<?>, CodeEnumType> INSTANCE_MAP = new ConcurrentHashMap<>();


    public static CodeEnumType build(Class<?> javaType) {
        if (javaType.isEnum() && CodeEnum.class.isAssignableFrom(javaType)) {
            return INSTANCE_MAP.computeIfAbsent(javaType, CodeEnumType::new);
        } else {
            throw MappingMetaUtils.createNotSupportJavaTypeException(CodeEnumType.class, javaType);
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
    public boolean singleton() {
        return false;
    }

    @Override
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context)
            throws SQLException {
        Assert.isInstanceOf(CodeEnum.class, nonNullValue);
        st.setInt(index, ((CodeEnum) nonNullValue).code());

    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias, ResultColumnMeta resultColumnMeta
            , MappingContext context) throws SQLException {
        int code = resultSet.getInt(alias);
        CodeEnum codeEnum = CodeEnum.resolve(this.enumClass, code);
        if (codeEnum == null) {
            throw new SQLException(String.format("CodeEnum[%s] couldn't resolve code[%s]."
                    , this.enumClass.getName(), code));
        }
        return code;
    }

    /*################################## blow protected method ##################################*/

    @Override
    protected Map<Database, SQLDataType> sqlDataTypeMap() {
        return DATA_TYPE_MAP;
    }

    @Override
    protected String doToConstant(@Nullable FieldMeta<?, ?> paramMeta, Object nonNullValue) {
        return Integer.toString(((CodeEnum) nonNullValue).code());
    }


    /*################################## blow private method ##################################*/

    @SuppressWarnings("unchecked")
    private static <T extends Enum<T> & CodeEnum> void checkCodeEnum(Class<?> enumClass) {
        CodeEnum.getCodeMap((Class<T>) enumClass);
    }


}
