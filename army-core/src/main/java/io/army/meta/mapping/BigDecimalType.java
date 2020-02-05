package io.army.meta.mapping;

import io.army.util.Assert;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class BigDecimalType implements MappingType {

    private static final BigDecimalType INSTANCE = new BigDecimalType();

    public static BigDecimalType build(Class<?> typeClass) {
        Assert.isTrue(BigDecimal.class == typeClass,"");
        return INSTANCE;
    }

    private BigDecimalType() {
    }

    @Override
    public Class<?> javaType() {
        return BigDecimal.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.DECIMAL;
    }

    @Override
    public String nonNullTextValue(Object value) {
        Assert.isInstanceOf(BigDecimal.class, value, () -> String.format("value[%s] isn't BigDecimal.", value));
        return ((BigDecimal) value).toPlainString();
    }

    @Override
    public boolean isTextValue(String textValue) {
        boolean yes;
        try {
            new BigDecimal(textValue);
            yes = true;
        } catch (Exception e) {
            yes = false;
        }
        return yes;
    }

    @Override
    public void nonNullSet(PreparedStatement st, Object value, int index) throws SQLException {
        Assert.isInstanceOf(BigDecimal.class, value);
        st.setBigDecimal(index, (BigDecimal) value);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias) throws SQLException {
        return resultSet.getBigDecimal(alias);
    }
}
