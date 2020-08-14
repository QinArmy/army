package io.army.meta.mapping;

import io.army.dialect.DDLUtils;
import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.sqldatatype.PostgreDataType;
import io.army.sqldatatype.SQLDataType;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

public final class OffsetDateTimeType extends AbstractMappingType {

    private static final Map<Database, SQLDataType> DATA_TYPE_MAP = createDataTypeMap();

    private static final OffsetDateTimeType INSTANCE = new OffsetDateTimeType();

    public static OffsetDateTimeType build(Class<?> typeClass) {
        if (typeClass != OffsetDateTime.class) {
            throw MappingMetaUtils.createNotSupportJavaTypeException(OffsetDateTimeType.class, typeClass);
        }
        return INSTANCE;
    }

    private static Map<Database, SQLDataType> createDataTypeMap() {
        EnumMap<Database, SQLDataType> map = new EnumMap<>(Database.class);

        map.put(Database.Postgre, PostgreDataType.TIMESTAMP_WITH_TIME_ZONE);

        return Collections.unmodifiableMap(map);
    }

    private OffsetDateTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return OffsetDateTime.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.TIMESTAMP_WITH_TIMEZONE;
    }

    @Override
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context)
            throws SQLException {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(context.zoneId()));
        st.setTimestamp(index, Timestamp.from(((OffsetDateTime) nonNullValue).toInstant()), calendar);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias, ResultColumnMeta resultColumnMeta
            , MappingContext context) throws SQLException {
        ZoneId zoneId = context.zoneId();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(zoneId));
        Timestamp timestamp = resultSet.getTimestamp(alias, calendar);

        OffsetDateTime dateTime = null;
        if (timestamp != null) {
            dateTime = OffsetDateTime.ofInstant(timestamp.toInstant(), zoneId);
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
