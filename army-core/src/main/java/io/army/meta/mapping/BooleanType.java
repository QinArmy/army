package io.army.meta.mapping;

import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SQLDataType;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class BooleanType extends AbstractMappingType {


    private static final Map<Database, SQLDataType> DATA_TYPE_MAP = createDataTypeMap();

    private static final BooleanType INSTANCE = new BooleanType();

    public static BooleanType build(Class<?> typeClass) {
        if (typeClass != Boolean.class) {
            throw MappingMetaUtils.createNotSupportJavaTypeException(BooleanType.class, typeClass);
        }
        return INSTANCE;
    }

    private static Map<Database, SQLDataType> createDataTypeMap() {
        EnumMap<Database, SQLDataType> map = new EnumMap<>(Database.class);

        map.put(Database.MySQL, MySQLDataType.BOOLEAN);
        map.put(Database.Postgre, PostgreDataType.BOOLEAN);

        return Collections.unmodifiableMap(map);

    }

    private BooleanType() {
    }

    @Override
    public Class<?> javaType() {
        return Boolean.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.TINYINT;
    }

    @Override
    public boolean singleton() {
        return true;
    }

    @Override
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context)
            throws SQLException {
        Assert.isInstanceOf(Boolean.class, nonNullValue);
        st.setInt(index, Boolean.TRUE.equals(nonNullValue) ? 1 : 0);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias, ResultColumnMeta resultColumnMeta
            , MappingContext context) throws SQLException {
        String value = resultSet.getString(alias);
        Boolean boolValue;
        if (value == null) {
            boolValue = null;
        } else if (value.equals("1")) {
            boolValue = Boolean.TRUE;
        } else {
            boolValue = Boolean.FALSE;
        }
        return boolValue;
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
