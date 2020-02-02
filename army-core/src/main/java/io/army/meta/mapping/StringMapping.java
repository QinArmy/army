package io.army.meta.mapping;

import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class StringMapping extends AbstractMappingType {

    public static final StringMapping INSTANCE = new StringMapping();


    private StringMapping() {
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
    public String nullSafeTextValue(Object value) {
        return String.valueOf(value);
    }

    @Override
    public boolean isTextValue(String textValue) {
        return true;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws SQLException {
        Assert.isInstanceOf(String.class, value, "");
        st.setString(index, (String) value);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias) throws SQLException {
        return resultSet.getString(alias);
    }
}
