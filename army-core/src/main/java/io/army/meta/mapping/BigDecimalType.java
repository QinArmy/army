package io.army.meta.mapping;

import io.army.dialect.MappingContext;
import io.army.util.Assert;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class BigDecimalType extends AbstractMappingType {

    private static final BigDecimalType INSTANCE = new BigDecimalType();

    public static BigDecimalType build(Class<?> typeClass) {
        Assert.isTrue(typeClass == BigDecimal.class
                , () -> String.format("typeClass[%s] isn't java.math.BigDecimal", typeClass.getName()));
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
        Assert.isInstanceOf(BigDecimal.class, value, () -> String.format("value[%s] isn'field BigDecimal.", value));
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
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context) throws SQLException {
        Assert.isInstanceOf(BigDecimal.class, nonNullValue);
        st.setBigDecimal(index, (BigDecimal) nonNullValue);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias, MappingContext context) throws SQLException {
        return resultSet.getBigDecimal(alias);
    }
}
