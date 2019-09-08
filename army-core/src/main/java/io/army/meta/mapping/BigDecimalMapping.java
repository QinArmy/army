package io.army.meta.mapping;

import io.army.dialect.Dialect;
import io.army.dialect.MySQLDialect;
import io.army.meta.sqltype.SQLDataType;
import io.army.meta.sqltype.mysql.MySQLDecimal;
import io.army.util.NumberUtils;
import io.army.util.Precision;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.sql.JDBCType;

public final class BigDecimalMapping extends MappingSupport implements MappingType<BigDecimal> {

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
    public Object toSql(BigDecimal bigDecimal) {
        return bigDecimal;
    }

    @Override
    public BigDecimal toJava(Object databaseValue) {
        return NumberUtils.parseNumberFromObject(databaseValue, BigDecimal.class);
    }

    @Override
    public SQLDataType sqlType(Dialect dialect) {

        SQLDataType sqlDataType;
        if (dialect instanceof MySQLDialect) {
            sqlDataType = MySQLDecimal.INSTANCE;
        } else {
            throw unsupportedDialect(dialect);
        }
        return sqlDataType;
    }

    @NonNull
    @Override
    public Precision precision() {
        return Precision.DEFAULT_DECIMAL_PRECISION;
    }
}
