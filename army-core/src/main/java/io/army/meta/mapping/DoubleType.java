package io.army.meta.mapping;

import io.army.dialect.MappingContext;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DoubleType extends AbstractMappingType {

    private static final DoubleType INSTANCE = new DoubleType();

    public static DoubleType build(Class<?> typeClass) {
        Assert.isTrue(Double.class == typeClass, "");
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
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context) throws SQLException {
        Assert.isInstanceOf(Double.class, nonNullValue, "");
        st.setDouble(index, (Double) nonNullValue);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias, MappingContext context) throws SQLException {
        return resultSet.getDouble(alias);
    }
}
