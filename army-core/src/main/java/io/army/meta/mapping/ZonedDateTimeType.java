package io.army.meta.mapping;

import io.army.dialect.DDLUtils;
import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SQLDataType;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public final class ZonedDateTimeType extends AbstractMappingType {


    private static final Map<Database, SQLDataType> DATA_TYPE_MAP = createDataTypeMap();

    private static final ZonedDateTimeType INSTANCE = new ZonedDateTimeType();

    public static ZonedDateTimeType build(Class<?> typeClass) {
        if (typeClass != ZonedDateTime.class) {
            throw MappingMetaUtils.createNotSupportJavaTypeException(ZonedDateTimeType.class, typeClass);
        }
        return INSTANCE;
    }

    private static Map<Database, SQLDataType> createDataTypeMap() {
        EnumMap<Database, SQLDataType> map = new EnumMap<>(Database.class);

        map.put(Database.Postgre, PostgreDataType.TIMESTAMP_WITH_TIME_ZONE);

        return Collections.unmodifiableMap(map);
    }

    private ZonedDateTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return ZonedDateTime.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.TIMESTAMP_WITH_TIMEZONE;
    }

    @Override
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context)
            throws SQLException {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(context.zoneId()));
        st.setTimestamp(index, Timestamp.from(((ZonedDateTime) nonNullValue).toInstant()), calendar);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias, ResultColumnMeta resultColumnMeta
            , MappingContext context) throws SQLException {
        ZoneId zoneId = context.zoneId();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(zoneId));
        Timestamp timestamp = resultSet.getTimestamp(alias, calendar);

        ZonedDateTime dateTime = null;
        if (timestamp != null) {
            dateTime = ZonedDateTime.ofInstant(timestamp.toInstant(), zoneId);
        }
        return dateTime;
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
