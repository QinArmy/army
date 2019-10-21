package io.army.meta.mapping;

import io.army.util.Assert;
import io.army.util.Precision;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.sql.JDBCType;

public final class BigDecimalMapping extends MappingSupport implements MappingType {

    public static final BigDecimalMapping INSTANCE = new BigDecimalMapping();

    private BigDecimalMapping() {
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
    public String nullSafeTextValue(Object value) {
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

    @NonNull
    @Override
    public Precision precision() {
        return Precision.DEFAULT_DECIMAL_PRECISION;
    }
}
