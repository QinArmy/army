package io.army.meta.mapping;

import io.army.struct.CodeEnum;
import io.army.struct.CodeEnumException;
import io.army.util.Assert;
import io.army.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * @see Enum
 * @see io.army.struct.CodeEnum
 */
 public final class CodeEnumMapping implements MappingType {


    private final Class<?> enumClass;


    public CodeEnumMapping(Class<?> enumClass) {
        this.enumClass = enumClass;
        Assert.isTrue(enumClass.isEnum(), () -> String.format("javaType[%s] isn't Enum", enumClass));
        Assert.isAssignable(CodeEnum.class, enumClass);
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
    public String nullSafeTextValue(Object value) {
        CodeEnum codeEnum = (CodeEnum) value;
        return Integer.toString(codeEnum.code());
    }

    @Override
    public boolean isTextValue(String textValue) {
        boolean yes;
        try {
            int code = Integer.parseInt(textValue);
            CodeEnum codeEnum = getCodeEnum(code);
            yes = codeEnum != null;
        } catch (NumberFormatException | CodeEnumException e) {
            yes = false;
        }
        return yes;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws SQLException {
        Assert.isInstanceOf(CodeEnum.class, value);
        st.setInt(index, ((CodeEnum) value).code());

    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias) throws SQLException {
        int code = resultSet.getInt(alias);
        Object value = getCodeEnum(code);
        if (value == null) {
            throw new SQLException(String.format("alias[%s] corresponding value[%s] isn't %s code"
                    , alias, code, enumClass.getName()));
        }
        return value;
    }

    /*################################## blow private method ##################################*/

    @SuppressWarnings("unchecked")
    private static <T extends Enum<T> & CodeEnum> void checkCodeEnum(Class<?> enumClass) {
        CodeEnum.getCodeMap((Class<T>) enumClass);
    }

    private <T extends Enum<T> & CodeEnum> CodeEnum getCodeEnum(int code) {
        @SuppressWarnings("unchecked")
        Map<Integer, T> codeMap = CodeEnum.getCodeMap((Class<T>) enumClass);
        return codeMap.get(code);

    }

}
