package io.army.meta.mapping;

import io.army.util.Assert;

import java.sql.*;
import java.time.LocalDateTime;

public final class LocalDateTimeType implements MappingType {

    public static final LocalDateTimeType INSTANCE = new LocalDateTimeType();

    public static LocalDateTimeType build(Class<?> typeClass) {
        return INSTANCE;
    }

    private LocalDateTimeType() {
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
    public String nonNullTextValue(Object value) {
        return null;
    }

    @Override
    public boolean isTextValue(String textValue) {
        return false;
    }


    @Override
    public void nonNullSet(PreparedStatement st, Object value, int index) throws SQLException {
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
