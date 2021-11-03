package io.army.mapping;

import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.sqldatatype.MySQLDataType;
import io.army.sqldatatype.PostgreDataType;
import io.army.sqldatatype.SqlType;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class IntegerType extends AbstractMappingType {

    static final Map<Database, SqlType> DATA_TYPE_MAP = createDataTypeMap();

    private static final IntegerType INSTANCE = new IntegerType();


    public static IntegerType build(Class<?> typeClass) {
        if (typeClass != Integer.class) {
            throw MappingMetaUtils.createNotSupportJavaTypeException(IntegerType.class, typeClass);
        }
        return INSTANCE;
    }

    private static Map<Database, SqlType> createDataTypeMap() {
        EnumMap<Database, SqlType> map = new EnumMap<>(Database.class);

        map.put(Database.MySQL, MySQLDataType.INT);
        map.put(Database.Postgre, PostgreDataType.INTEGER);

        return Collections.unmodifiableMap(map);
    }

    private IntegerType() {
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
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context)
            throws SQLException {
        Assert.isInstanceOf(Integer.class, nonNullValue, "");
        st.setInt(index, (Integer) nonNullValue);
    }

    @Override
    public Object nullSafeGet(ResultSet st, String alias, ResultColumnMeta resultColumnMeta
            , MappingContext context) throws SQLException {
        Object value = st.getObject(alias);
        if (value != null && !(value instanceof Integer)) {
            throw new SQLException(String.format(
                    "Alis[%s] return error,value[Class %s] from database isn't Integer type."
                    , alias, value.getClass().getName()));
        }
        return value;
    }

    /*################################## blow protected method ##################################*/

    @Override
    protected Map<Database, SqlType> sqlDataTypeMap() {
        return DATA_TYPE_MAP;
    }

    @Override
    protected String doToConstant(@Nullable FieldMeta<?, ?> paramMeta, Object nonNullValue) {
        return nonNullValue.toString();
    }
}
