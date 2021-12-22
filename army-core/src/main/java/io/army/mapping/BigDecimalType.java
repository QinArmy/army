package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.JDBCType;

public final class BigDecimalType extends ArmyNoInjectionMapping {

    public static final BigDecimalType INSTANCE = new BigDecimalType();

    public static BigDecimalType build(Class<?> typeClass) {
        if (typeClass != BigDecimal.class) {
            throw AbstractMappingType.createNotSupportJavaTypeException(BigDecimalType.class, typeClass);
        }
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
    public SqlDataType sqlDataType(final ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlDataType sqlDataType;
        switch (serverMeta.database()) {
            case MySQL:
                sqlDataType = MySQLDataType.DECIMAL;
                break;
            case PostgreSQL:
                sqlDataType = PostgreDataType.DECIMAL;
                break;
            case H2:
                sqlDataType = H2DataType.DECIMAL;
                break;
            case Oracle:
                sqlDataType = OracleDataType.NUMBER;
                break;
            default:
                throw noMappingError(serverMeta);

        }
        return sqlDataType;
    }

    @Override
    public BigDecimal convertBeforeBind(SqlDataType sqlDataType, final Object nonNull) {
        final BigDecimal value;
        if (nonNull instanceof BigDecimal) {
            value = (BigDecimal) nonNull;
        } else if (nonNull instanceof Long
                || nonNull instanceof Integer
                || nonNull instanceof Short
                || nonNull instanceof Byte) {
            value = BigDecimal.valueOf(((Number) nonNull).longValue());
        } else if (nonNull instanceof BigInteger) {
            value = new BigDecimal((BigInteger) nonNull);
        } else if (nonNull instanceof Boolean) {
            value = (Boolean) nonNull ? BigDecimal.ONE : BigDecimal.ZERO;
        } else if (nonNull instanceof String) {
            value = new BigDecimal((String) nonNull);
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(SqlDataType sqlDataType, final Object nonNull) {
        if (!(nonNull instanceof BigDecimal)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return nonNull;
    }


}
