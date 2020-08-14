package io.army.meta.mapping;

import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.sqldatatype.MySQLDataType;
import io.army.sqldatatype.PostgreDataType;
import io.army.sqldatatype.SQLDataType;
import io.army.util.Assert;

import java.math.BigDecimal;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class BigDecimalType extends AbstractMappingType {

    private static final Map<Database, SQLDataType> DATA_TYPE_MAP = createDataTypeMap();

    private static final BigDecimalType INSTANCE = new BigDecimalType();

    public static BigDecimalType build(Class<?> typeClass) {
        if (typeClass != BigDecimal.class) {
            throw MappingMetaUtils.createNotSupportJavaTypeException(BigDecimalType.class, typeClass);
        }
        return INSTANCE;
    }

    private static Map<Database, SQLDataType> createDataTypeMap() {
        EnumMap<Database, SQLDataType> map = new EnumMap<>(Database.class);

        map.put(Database.MySQL, MySQLDataType.DECIMAL);
        map.put(Database.Postgre, PostgreDataType.DECIMAL);

        return Collections.unmodifiableMap(map);

    }

    private BigDecimalType() {
    }

    @Override
    public boolean singleton() {
        return true;
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
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context)
            throws SQLException {
        Assert.isInstanceOf(BigDecimal.class, nonNullValue);
        st.setBigDecimal(index, (BigDecimal) nonNullValue);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias, ResultColumnMeta resultColumnMeta
            , MappingContext context) throws SQLException {
        return resultSet.getBigDecimal(alias);
    }

    /*################################## blow protected method ##################################*/

    @Override
    protected Map<Database, SQLDataType> sqlDataTypeMap() {
        return DATA_TYPE_MAP;
    }

    @Override
    protected String doToConstant(@Nullable FieldMeta<?, ?> fieldMeta, Object nonNullValue) {
        return ((BigDecimal) nonNullValue).toPlainString();
    }
}
