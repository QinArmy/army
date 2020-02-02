package io.army.meta.mapping;

import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class IntegerMapping  implements MappingType {

    public static final IntegerMapping INSTANCE = new IntegerMapping();

    private IntegerMapping() {
    }

    @Override
    public Class<?> javaType() {
        return Integer.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.INTEGER;
    }

    @Override
    public String nullSafeTextValue(Object value) {
        return String.valueOf(value);
    }

    @Override
    public boolean isTextValue(String textValue) {
        boolean yes;
        try {
            Integer.parseInt(textValue);
            yes = true;
        } catch (NumberFormatException e) {
            yes = false;
        }
        return yes;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index)throws SQLException {
        Assert.isInstanceOf(Integer.class,value,"");
        st.setInt(index,(Integer) value);
    }

    @Override
    public Object nullSafeGet(ResultSet st, String alias) throws SQLException {
        return st.getInt(alias);
    }
}
