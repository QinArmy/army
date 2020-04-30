package io.army.meta.mapping;

import io.army.util.Assert;
import io.army.util.StringUtils;
import io.army.util.TimeUtils;

import java.sql.*;
import java.time.LocalDate;

public final class LocalDateType implements MappingType {

    private static final LocalDateType INSTANCE = new LocalDateType();

    public static LocalDateType build(Class<?> typeClass){
        Assert.isTrue(LocalDate.class == typeClass,"");
        return INSTANCE;
    }

    private LocalDateType() {
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
    public String nonNullTextValue(Object value) {
        return StringUtils.quote(
                ((LocalDate) value).format(TimeUtils.DATE_FORMATTER)
        );
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
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index) throws SQLException {
        Assert.isInstanceOf(LocalDate.class, nonNullValue, "");
        st.setDate(index, Date.valueOf((LocalDate) nonNullValue));
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
