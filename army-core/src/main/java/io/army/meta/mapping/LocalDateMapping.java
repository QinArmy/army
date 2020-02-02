package io.army.meta.mapping;

import io.army.util.Assert;
import io.army.util.TimeUtils;

import java.sql.*;
import java.time.LocalDate;

public final class LocalDateMapping implements MappingType {

    public static final LocalDateMapping INSTANCE = new LocalDateMapping();


    private LocalDateMapping() {
    }

    @Override
    public Class<?> javaType() {
        return LocalDate.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.DATE;
    }

    @Override
    public String nullSafeTextValue(Object value) {
        LocalDate date = (LocalDate) value;
        return date.format(TimeUtils.DATE_FORMATTER);
    }


    @Override
    public boolean isTextValue(String textValue) {
        boolean yes;
        try {
            LocalDate.parse(textValue, TimeUtils.DATE_FORMATTER);
            yes = true;
        } catch (Exception e) {
            yes = false;
        }
        return yes;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws SQLException {
        Assert.isInstanceOf(LocalDate.class, value, "");
        st.setDate(index, Date.valueOf((LocalDate) value));
    }

    @Override
    public Object nullSafeGet(ResultSet st, String alias) throws SQLException {
        Date date = st.getDate(alias);
        LocalDate localDate = null;
        if (date != null) {
            localDate = date.toLocalDate();
        }
        return localDate;
    }
}
