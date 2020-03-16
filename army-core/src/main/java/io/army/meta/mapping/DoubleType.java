package io.army.meta.mapping;

import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DoubleType implements MappingType {

    private static final DoubleType INSTANCE = new DoubleType();

    public static DoubleType build(Class<?> typeClass) {
        Assert.isTrue(Double.class == typeClass,"");
        return INSTANCE;
    }


    @Override
    public Class<?> javaType() {
        return Double.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.DOUBLE;
    }

    @Override
    public String nonNullTextValue(Object value) {
        return String.valueOf(value);
    }

    @Override
    public boolean isTextValue(String textValue) {
        boolean match;
        try {
            Double.parseDouble(textValue);
            match = true;
        } catch (NumberFormatException e) {
            match = false;
        }
        return match;
    }

    @Override
    public void nonNullSet(PreparedStatement st, Object value, int index) throws SQLException {
        Assert.isInstanceOf(Double.class, value, "");
        st.setDouble(index, (Double) value);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias) throws SQLException {
        return resultSet.getDouble(alias);
    }
}