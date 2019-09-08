package io.army.meta.mapping;

import io.army.dialect.Dialect;
import io.army.dialect.MySQLDialect;
import io.army.meta.sqltype.SQLDataType;
import io.army.meta.sqltype.mysql.MySQLBigInt;
import io.army.util.NumberUtils;
import io.army.util.Precision;

import java.sql.JDBCType;

public final class LongMapping extends MappingSupport implements MappingType<Long> {

    public static final LongMapping INSTANCE = new LongMapping();

    private LongMapping() {
    }

    @Override
    public Class<?> javaType() {
        return Long.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.BIGINT;
    }

    @Override
    public Object toSql(Long aLong) {
        return aLong;
    }

    @Override
    public Long toJava(Object databaseValue) {
        return NumberUtils.parseNumberFromObject(databaseValue, Long.class);
    }

    @Override
    public SQLDataType sqlType(Dialect dialect) {
        SQLDataType sqlDataType;
        if (dialect instanceof MySQLDialect) {
            sqlDataType = MySQLBigInt.INSTANCE;
        } else {
            throw unsupportedDialect(dialect);
        }
        return sqlDataType;
    }

    @Override
    public Precision precision() {
        return Precision.DEFAULT_BIGINT_PRECISION;
    }
}
