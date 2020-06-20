package io.army.meta.mapping;

import io.army.dialect.MappingContext;
import io.army.util.Assert;
import io.army.util.StringUtils;
import io.army.util.TimeUtils;

import java.sql.*;
import java.time.LocalTime;

public final class LocalTimeType extends AbstractMappingType {

    private static final LocalTimeType INSTANCE = new LocalTimeType();

    public static LocalTimeType build(Class<?> typeClass) {
        Assert.isTrue(LocalTime.class == typeClass, "");
        return INSTANCE;
    }

    @Override
    public Class<?> javaType() {
        return LocalTime.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.DATE;
    }

    @Override
    public String nonNullTextValue(Object value) {
        return StringUtils.quote(
                ((LocalTime) value).format(TimeUtils.TIME_FORMATTER)
        );
    }

    @Override
    public boolean isTextValue(String textValue) {
        boolean match;
        try {
            LocalTime.parse(textValue, TimeUtils.TIME_FORMATTER);
            match = true;
        } catch (Exception e) {
            match = false;
        }
        return match;
    }

    @Override
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context) throws SQLException {
        st.setTime(index, Time.valueOf((LocalTime) nonNullValue));
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias, MappingContext context) throws SQLException {
        Time time = resultSet.getTime(alias);
        return time == null ? null : time.toLocalTime();
    }
}
