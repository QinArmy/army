package io.army.meta.mapping;

import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.dialect.NotSupportDialectException;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SQLDataType;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DoubleType extends AbstractMappingType {

    private static final DoubleType INSTANCE = new DoubleType();

    public static DoubleType build(Class<?> typeClass) {
        if (typeClass != Double.class) {
            throw MappingMetaUtils.createNotSupportJavaTypeException(DoubleType.class, typeClass);
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
    public SQLDataType sqlDataType(Database database) throws NotSupportDialectException {
        SQLDataType dataType;
        switch (database.family()) {
            case MySQL:
                dataType = MySQLDataType.DOUBLE;
                break;
            case Postgre:
                dataType = PostgreDataType.DOUBLE_PRECISION;
                break;
            default:
                throw MappingMetaUtils.createNotSupportDialectException(this, database);
        }
        return dataType;
    }

    @Override
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context)
            throws SQLException {
        Assert.isInstanceOf(Double.class, nonNullValue, "");
        st.setDouble(index, (Double) nonNullValue);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias, ResultColumnMeta resultColumnMeta
            , MappingContext context) throws SQLException {
        return resultSet.getDouble(alias);
    }

}
