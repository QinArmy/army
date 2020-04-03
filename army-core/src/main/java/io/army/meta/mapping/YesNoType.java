package io.army.meta.mapping;

import io.army.domain.IDomain;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class YesNoType implements MappingType {

    private static final YesNoType INSTANCE = new YesNoType();

    public static YesNoType build(Class<?> typeClass) {
        Assert.isTrue(Boolean.class == typeClass, "");
        return INSTANCE;
    }

    public static final String Y = "Y";

    public static final String N = "N";

    private YesNoType() {
    }

    @Override
    public Class<?> javaType() {
        return Boolean.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.CHAR;
    }

    @Override
    public String nonNullTextValue(Object value) {
        Assert.isInstanceOf(Boolean.class,value,"");
        String text;
        if (Boolean.TRUE.equals(value)) {
            text = IDomain.Y;
        } else {
            text = IDomain.N;
        }
        return text;
    }

    @Override
    public boolean isTextValue(String textValue) {
        return IDomain.Y.equals(textValue)
                || IDomain.N.equals(textValue);
    }

    @Override
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index) throws SQLException {
        Assert.isInstanceOf(Boolean.class, nonNullValue);
        st.setString(index, Boolean.TRUE.equals(nonNullValue) ? Y : N);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias) throws SQLException {
        String text = resultSet.getString(alias);
        if (text == null) {
            return null;
        }
        Boolean value;
        switch (text) {
            case Y:
                value = Boolean.TRUE;
                break;
            case N:
                value = Boolean.FALSE;
                break;
            default:
                throw new SQLException(String.format("database return %s,but only 'Y' or 'N'", text));
        }
        return value;
    }
}
