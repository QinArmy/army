package io.army.meta.mapping;

import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class StringType extends AbstractMappingType {

    private static final StringType INSTANCE = new StringType();

    public static StringType build(Class<?> typeClass) {
        Assert.isTrue(String.class ==typeClass,"");
        return INSTANCE;
    }


    private StringType() {
    }

    @Override
    public Class<?> javaType() {
        return String.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.VARCHAR;
    }

    @Override
    public String nonNullTextValue(Object value) {
        return String.valueOf(value);
    }

    @Override
    public boolean isTextValue(String textValue) {
        return true;
    }

    @Override
    public void nonNullSet(PreparedStatement st, Object value, int index) throws SQLException {
        Assert.isInstanceOf(String.class, value, "");
        st.setString(index, (String) value);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias) throws SQLException {
        return resultSet.getString(alias);
    }
}
