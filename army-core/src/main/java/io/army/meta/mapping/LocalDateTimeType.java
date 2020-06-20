package io.army.meta.mapping;

import io.army.dialect.MappingContext;
import io.army.util.Assert;
import io.army.util.StringUtils;
import io.army.util.TimeUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.TimeZone;

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
           match =false;
        }
        return match;
    }


    @Override
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context)
            throws SQLException {
        Assert.isInstanceOf(LocalDateTime.class, nonNullValue, "");

        st.setTimestamp(index, Timestamp.valueOf((LocalDateTime) nonNullValue)
                , Calendar.getInstance(TimeZone.getTimeZone(context.zoneId())));
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias, MappingContext context) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(alias
                , Calendar.getInstance(TimeZone.getTimeZone(context.zoneId())));
        LocalDateTime dateTime = null;
        if (timestamp != null) {
            dateTime = timestamp.toLocalDateTime();
        }
        return dateTime;
    }
}
