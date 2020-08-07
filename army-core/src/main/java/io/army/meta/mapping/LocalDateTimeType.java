package io.army.meta.mapping;

import io.army.dialect.MappingContext;
import io.army.util.Assert;
import io.army.util.StringUtils;
import io.army.util.TimeUtils;

import java.sql.*;
import java.time.LocalDateTime;

public final class LocalDateTimeType extends AbstractMappingType {

    private static final LocalDateTimeType INSTANCE = new LocalDateTimeType();

    public static LocalDateTimeType build(Class<?> typeClass) {
        Assert.isTrue(LocalDateTime.class == typeClass, "");
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
        return StringUtils.quote(
                ((LocalDateTime) value).format(TimeUtils.DATE_TIME_FORMATTER)
        );
    }

    @Override
    public boolean isTextValue(String textValue) {
        boolean match;
        try {
            LocalDateTime.parse(textValue);
            match = true;
        } catch (Exception e) {
            match = false;
        }
        return match;
    }


    @Override
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context)
            throws SQLException {
        // use setString ,avoid use setTimestamp ,because MySQL connector 8.0+ bug about add US zone.
        st.setString(index, ((LocalDateTime) nonNullValue).format(TimeUtils.DATE_TIME_FORMATTER));
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias, MappingContext context) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(alias);

        LocalDateTime dateTime = null;
        if (timestamp != null) {
            dateTime = timestamp.toLocalDateTime();
        }
        return dateTime;
    }
}
