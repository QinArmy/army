package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ServerMeta;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SqlDataType;

import java.sql.JDBCType;

public final class DoubleType extends AbstractMappingType {


    public static final DoubleType INSTANCE = new DoubleType();

    public static DoubleType build(Class<?> typeClass) {
        if (typeClass != Double.class) {
            throw AbstractMappingType.createNotSupportJavaTypeException(DoubleType.class, typeClass);
        }
        return INSTANCE;
    }

    private DoubleType() {
    }

    @Override
    public Class<?> javaType() {
        return Double.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.DOUBLE;
    }

    @Override
    public SqlDataType sqlDataType(ServerMeta serverMeta) throws NotSupportDialectException {
        final SqlDataType sqlDataType;
        switch (serverMeta.database()) {
            case MySQL:
                sqlDataType = MySQLDataType.DOUBLE;
                break;
            case Postgre:
                sqlDataType = PostgreDataType.DOUBLE;
                break;
            default:
                throw noMappingError(serverMeta);
        }
        return sqlDataType;
    }

    @Override
    public Object convertBeforeBind(SqlDataType sqlDataType, Object nonNull) {
        final Double value;
        if (nonNull instanceof Double) {
            value = (Double) nonNull;
        } else {
            throw notSupportConvertBeforeBind(nonNull);
        }
        return value;
    }

    @Override
    public Object convertAfterGet(SqlDataType sqlDataType, Object nonNull) {
        if (!(nonNull instanceof Double)) {
            throw notSupportConvertAfterGet(nonNull);
        }
        return nonNull;
    }


}
