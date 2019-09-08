package io.army.meta.mapping;

import io.army.dialect.Dialect;
import io.army.dialect.MySQLDialect;
import io.army.meta.sqltype.SQLDataType;
import io.army.meta.sqltype.mysql.MySQLInt;
import io.army.util.NumberUtils;
import io.army.util.Precision;

import java.sql.JDBCType;

public final class IntegerMapping extends MappingSupport implements MappingType<Integer> {

    public static final IntegerMapping INSTANCE = new IntegerMapping();

    private IntegerMapping() {
    }

    @Override
    public Class<?> javaType() {
        return Integer.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.INTEGER;
    }

    @Override
    public Object toSql(Integer integer) {
        return integer;
    }

    @Override
    public Integer toJava(Object databaseValue) {
        return NumberUtils.parseNumberFromObject(databaseValue, Integer.class);
    }

    @Override
    public SQLDataType sqlType(Dialect dialect) {
        SQLDataType sqlDataType;
        if (dialect instanceof MySQLDialect) {
            sqlDataType = MySQLInt.INSTANCE;
        } else {
            throw unsupportedDialect(dialect);
        }
        return sqlDataType;
    }

    @Override
    public Precision precision() {
        return Precision.DEFAULT_INT_PRECISION;
    }
}
