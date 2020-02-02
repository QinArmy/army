package io.army.meta.mapping;

import io.army.util.Assert;

import java.sql.*;
import java.time.LocalDateTime;

public final class LocalDateTimeMapping implements MappingType {

    public static final LocalDateTimeMapping INSTANCE = new LocalDateTimeMapping();

    private LocalDateTimeMapping() {
    }

    @Override
    public Class<?> javaType() {
        return LocalDateTime.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.TIMESTAMP;
    }

    @Override
    public String nullSafeTextValue(Object value) {
        return null;
    }

    @Override
    public boolean isTextValue(String textValue) {
        return false;
    }


    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws SQLException {
        Assert.isInstanceOf(LocalDateTime.class, value, "");
        st.setTimestamp(index, Timestamp.valueOf((LocalDateTime) value));
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(alias);
        LocalDateTime dateTime = null;
        if (timestamp != null) {
            dateTime = timestamp.toLocalDateTime();
        }
        return dateTime;
    }
}
