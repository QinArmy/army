package io.army.meta.mapping;

import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class IntegerType extends AbstractMappingType {

    private static final IntegerType INSTANCE = new IntegerType();


    public static IntegerType build(Class<?> typeClass) {
        Assert.isTrue(Integer.class == typeClass, "");
        return INSTANCE;
    }

    private IntegerType() {
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
    public String nonNullTextValue(Object value) {
        Assert.isInstanceOf(Integer.class,value,"");
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
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index) throws SQLException {
        Assert.isInstanceOf(Integer.class, nonNullValue, "");
        st.setInt(index, (Integer) nonNullValue);
    }

    @Override
    public Object nullSafeGet(ResultSet st, String alias) throws SQLException {
        return st.getInt(alias);
    }
}
