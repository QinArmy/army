package io.army.meta.mapping;

import io.army.dialect.DDLUtils;
import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.sqldatatype.MySQLDataType;
import io.army.sqldatatype.PostgreDataType;
import io.army.sqldatatype.SQLDataType;
import io.army.util.Assert;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public final class LocalTimeType extends AbstractMappingType {

    private static final Map<Database, SQLDataType> DATA_TYPE_MAP = createDataTypeMap();

    private static final LocalTimeType INSTANCE = new LocalTimeType();

    public static LocalTimeType build(Class<?> typeClass) {
        Assert.isTrue(LocalTime.class == typeClass, "");
        return INSTANCE;
    }

    private static Map<Database, SQLDataType> createDataTypeMap() {
        EnumMap<Database, SQLDataType> map = new EnumMap<>(Database.class);

        map.put(Database.MySQL, MySQLDataType.TIME);
        map.put(Database.Postgre, PostgreDataType.TIME_WITHOUT_TIME_ZONE);

        return Collections.unmodifiableMap(map);
    }

    private LocalTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return LocalTime.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.TIME;
    }


    @Override
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context)
            throws SQLException {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(context.zoneId()));
        st.setTime(index, Time.valueOf((LocalTime) nonNullValue), calendar);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias, ResultColumnMeta resultColumnMeta
            , MappingContext context) throws SQLException {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(context.zoneId()));
        Time time = resultSet.getTime(alias, calendar);
        return time == null ? null : time.toLocalTime();
    }

    /*################################## blow protected method ##################################*/

    @Override
    protected Map<Database, SQLDataType> sqlDataTypeMap() {
        return DATA_TYPE_MAP;
    }

    @Override
    protected String doToConstant(@Nullable FieldMeta<?, ?> paramMeta, Object nonNullValue) {
        int precision = 0;
        if (paramMeta != null) {
            precision = paramMeta.precision();
        }
        return DDLUtils.constantForTimeType((LocalDateTime) nonNullValue, precision);
    }

}
