package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySqlType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlType;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class BooleanType extends _ArmyNoInjectionMapping {


    public static final BooleanType INSTANCE = new BooleanType();

    public static BooleanType build(Class<?> typeClass) {
        if (typeClass != Boolean.class) {
            throw AbstractMappingType.createNotSupportJavaTypeException(BooleanType.class, typeClass);
        }
        return INSTANCE;
    }

    public static final String TRUE = "true";

    public static final String FALSE = "false";


    private BooleanType() {
    }

    @Override
    public Class<?> javaType() {
        return Boolean.class;
    }

    @Override
    public SqlType sqlType(final ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlType sqlType;
        switch (serverMeta.database()) {
            case MySQL:
                sqlType = MySqlType.BOOLEAN;
                break;
            case PostgreSQL:
                sqlType = PostgreDataType.BOOLEAN;
                break;
            default:
                throw noMappingError(serverMeta);
        }
        return sqlType;
    }


    @Override
    public Boolean convertBeforeBind(SqlType sqlDataType, final Object nonNull) {
        final Boolean value;
        if (nonNull instanceof Boolean) {
            value = (Boolean) nonNull;
        } else if (nonNull instanceof Integer) {
            value = (Integer) nonNull == 0 ? Boolean.FALSE : Boolean.TRUE;
        } else if (nonNull instanceof Long || nonNull instanceof Short || nonNull instanceof Byte) {
            value = ((Number) nonNull).longValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
        } else if (nonNull instanceof String) {
            if (TRUE.equalsIgnoreCase((String) nonNull)) {
                value = Boolean.TRUE;
            } else if (FALSE.equalsIgnoreCase((String) nonNull)) {
                value = Boolean.FALSE;
            } else {
                throw notSupportConvertBeforeBind(nonNull);
            }
        } else if (nonNull instanceof BigDecimal) {
            value = BigDecimal.ZERO.compareTo((BigDecimal) nonNull) == 0 ? Boolean.FALSE : Boolean.TRUE;
        } else if (nonNull instanceof BigInteger) {
            value = BigInteger.ZERO.compareTo((BigInteger) nonNull) == 0 ? Boolean.FALSE : Boolean.TRUE;
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(SqlType sqlDataType, final Object nonNull) {
        if (!(nonNull instanceof Boolean)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return nonNull;
    }


}
