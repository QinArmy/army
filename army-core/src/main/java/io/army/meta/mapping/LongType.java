package io.army.meta.mapping;

import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.sqldatatype.MySQLDataType;
import io.army.sqldatatype.PostgreDataType;
import io.army.sqldatatype.SQLDataType;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class LongType extends AbstractMappingType {

    private static final Map<Database, SQLDataType> DATA_TYPE_MAP = createDataTypeMap();

    private static final LongType INSTANCE = new LongType();


    public static LongType build(Class<?> typeClass) {
        Assert.isTrue(Long.class == typeClass, "");
        return INSTANCE;
    }

    private static Map<Database, SQLDataType> createDataTypeMap() {
        EnumMap<Database, SQLDataType> map = new EnumMap<>(Database.class);

        map.put(Database.MySQL, MySQLDataType.BIGINT);
        map.put(Database.Postgre, PostgreDataType.BIGINT);

        return Collections.unmodifiableMap(map);
    }

    private LongType() {
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
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context) throws SQLException {
        Assert.isInstanceOf(Long.class, nonNullValue, "");
        st.setLong(index, (Long) nonNullValue);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias, ResultColumnMeta resultColumnMeta
            , MappingContext context) throws SQLException {
        Object value = resultSet.getObject(alias);
        if (value != null && !(value instanceof Long)) {
            throw new SQLException(String.format(
                    "Alis[%s] return error,value[Class %s] from database isn't Long type."
                    , alias, value.getClass().getName()));
        }
        return value;
    }


    /*################################## blow protected method ##################################*/

    @Override
    protected Map<Database, SQLDataType> sqlDataTypeMap() {
        return DATA_TYPE_MAP;
    }

    @Override
    protected String doToConstant(@Nullable FieldMeta<?, ?> paramMeta, Object nonNullValue) {
        return nonNullValue.toString();
    }

}
