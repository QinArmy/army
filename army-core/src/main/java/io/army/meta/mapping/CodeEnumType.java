package io.army.meta.mapping;

import io.army.struct.CodeEnum;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @see Enum
 * @see io.army.struct.CodeEnum
 */
public final class CodeEnumType extends AbstractMappingType {

    private static final ConcurrentMap<Class<?>, CodeEnumType> INSTANCE_MAP = new ConcurrentHashMap<>();


    public static CodeEnumType build(Class<?> javaType) {
        Assert.isTrue(javaType.isEnum(), () -> String.format("javaType[%s] isn'field Enum", javaType));
        Assert.isAssignable(CodeEnum.class, javaType);
        return INSTANCE_MAP.computeIfAbsent(javaType, CodeEnumType::new);
    }

    private final Class<?> enumClass;

    private CodeEnumType(Class<?> enumClass) {
        this.enumClass = enumClass;
        checkCodeEnum(enumClass);

    }

    @Override
    public Class<?> javaType() {
        return enumClass;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.INTEGER;
    }

    @Override
    public String nonNullTextValue(Object value) {
        CodeEnum codeEnum = (CodeEnum) value;
        return Integer.toString(codeEnum.code());
    }

    @Override
    public boolean isTextValue(String textValue) {
        boolean yes;
        try {
            yes = CodeEnum.resolve(enumClass, Integer.parseInt(textValue)) != null;
        } catch (Exception e) {
            yes = false;
        }
        return yes;
    }

    @Override
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index) throws SQLException {
        Assert.isInstanceOf(CodeEnum.class, nonNullValue);
        st.setInt(index, ((CodeEnum) nonNullValue).code());

    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias) throws SQLException {
        int code = resultSet.getInt(alias);
        return CodeEnum.resolve(enumClass, code);
    }

    /*################################## blow private method ##################################*/

    @SuppressWarnings("unchecked")
    private static <T extends Enum<T> & CodeEnum> void checkCodeEnum(Class<?> enumClass) {
        CodeEnum.getCodeMap((Class<T>) enumClass);
    }


}
