package io.army.meta.mapping;

import io.army.dialect.MappingContext;
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
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context) throws SQLException {
        Assert.isInstanceOf(String.class, nonNullValue, "");
        st.setString(index, (String) nonNullValue);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias, MappingContext context) throws SQLException {
        return resultSet.getString(alias);
    }
}
