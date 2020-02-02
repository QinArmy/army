package io.army.meta.mapping;

import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class LongMapping implements MappingType {

    public static final LongMapping INSTANCE = new LongMapping();

    private LongMapping() {
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
    public String nullSafeTextValue(Object value) {
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
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws SQLException {
        Assert.isInstanceOf(Long.class, value, "");
        st.setLong(index, (Long) value);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias) throws SQLException {
        return resultSet.getLong(alias);
    }
}
