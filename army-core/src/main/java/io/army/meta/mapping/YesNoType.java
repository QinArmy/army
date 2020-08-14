package io.army.meta.mapping;

import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.sqldatatype.MySQLDataType;
import io.army.sqldatatype.PostgreDataType;
import io.army.sqldatatype.SQLDataType;
import io.army.util.Assert;
import io.army.util.StringUtils;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class YesNoType extends AbstractMappingType {

    public static final String Y = "Y";

    public static final String N = "N";

    private static final Map<Database, SQLDataType> DATA_TYPE_MAP = createDataTypeMap();

    private static final YesNoType INSTANCE = new YesNoType();

    public static YesNoType build(Class<?> typeClass) {
        Assert.isTrue(Boolean.class == typeClass, "");
        return INSTANCE;
    }

    private static Map<Database, SQLDataType> createDataTypeMap() {
        EnumMap<Database, SQLDataType> map = new EnumMap<>(Database.class);

        map.put(Database.MySQL, MySQLDataType.CHAR);
        map.put(Database.Postgre, PostgreDataType.CHAR);

        return Collections.unmodifiableMap(map);
    }


    private YesNoType() {
    }

    @Override
    public Class<?> javaType() {
        return Boolean.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.CHAR;
    }

    @Override
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context)
            throws SQLException {
        Assert.isInstanceOf(Boolean.class, nonNullValue);
        st.setString(index, Boolean.TRUE.equals(nonNullValue) ? Y : N);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias, ResultColumnMeta resultColumnMeta
            , MappingContext context) throws SQLException {
        String text = resultSet.getString(alias);
        if (text == null) {
            return null;
        }
        Boolean value;
        switch (text) {
            case Y:
                value = Boolean.TRUE;
                break;
            case N:
                value = Boolean.FALSE;
                break;
            default:
                throw new SQLException(String.format("Alias[%s],database return %s,but only 'Y' or 'N'", alias, text));
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
        return StringUtils.quote(Boolean.TRUE.equals(nonNullValue) ? Y : N);
    }
}
