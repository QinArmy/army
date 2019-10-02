package io.army.meta.mapping;

import io.army.util.NumberUtils;
import io.army.util.Precision;

import java.sql.JDBCType;
import java.sql.SQLException;

public final class LongMapping extends AbstractMappingType<Long> {

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
    protected Object nonNullToSql(Long aLong) {
        return aLong;
    }

    @Override
    protected Long nonNullToJava(Object databaseValue) throws SQLException {
        return NumberUtils.parseNumberFromObject(databaseValue, Long.class);
    }


    @Override
    public Precision precision() {
        return Precision.DEFAULT_BIGINT_PRECISION;
    }
}
