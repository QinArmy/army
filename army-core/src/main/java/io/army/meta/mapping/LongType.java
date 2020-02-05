package io.army.meta.mapping;

import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class LongType implements MappingType {

    private static final LongType INSTANCE = new LongType();


    public static LongType build(Class<?> typeClass){
        Assert.isTrue(Long.class ==typeClass,"");
        return INSTANCE;
    }

    private LongType() {
    }

    @Override
    public Class<?> javaType() {
        return Long.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.BIGINT;
    }


    @Override
    public String nonNullTextValue(Object value) {
        return null;
    }

    @Override
    public boolean isTextValue(String textValue) {
        boolean yes;
        try {
            Long.parseLong(textValue);
            yes = true;
        } catch (NumberFormatException e) {
            yes = false;
        }
        return yes;
    }

    @Override
    public void nonNullSet(PreparedStatement st, Object value, int index) throws SQLException {
        Assert.isInstanceOf(Long.class, value, "");
        st.setLong(index, (Long) value);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias) throws SQLException {
        return resultSet.getLong(alias);
    }
}
