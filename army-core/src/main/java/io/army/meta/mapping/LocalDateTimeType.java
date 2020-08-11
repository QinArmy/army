package io.army.meta.mapping;

import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.dialect.NotSupportDialectException;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SQLDataType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public final class LocalDateTimeType extends AbstractMappingType {

    private static final Map<Database, SQLDataType> DATA_TYPE_MAP = createDataTypeMap();

    private static final LocalDateTimeType INSTANCE = new LocalDateTimeType();

    public static LocalDateTimeType build(Class<?> typeClass) {
        if (typeClass != LocalDateTime.class) {
            throw MappingMetaUtils.createNotSupportJavaTypeException(LocalDateTimeType.class, typeClass);
        }
        return INSTANCE;
    }

    private static Map<Database, SQLDataType> createDataTypeMap() {
        EnumMap<Database, SQLDataType> map = new EnumMap<>(Database.class);

        map.put(Database.MySQL, MySQLDataType.DATETIME);
        map.put(Database.Postgre, PostgreDataType.TIMESTAMP_WITHOUT_TIME_ZONE);

        return Collections.unmodifiableMap(map);
    }

    private LocalDateTimeType() {
    }

    @Override
    public Class<?> javaType() {
        return LocalDateTime.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.TIMESTAMP;
    }


    @Override
    public SQLDataType sqlDataType(Database database) throws NotSupportDialectException {
        SQLDataType dataType = DATA_TYPE_MAP.get(database.family());
        if (dataType == null) {
            throw MappingMetaUtils.createNotSupportDialectException(this, database);
        }
        return dataType;
    }

    @Override
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context)
            throws SQLException {
        // use Calendar ,avoid use setTimestamp ,because MySQL connector 8.0+ bug about add US zone.
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(context.zoneId()));
        st.setTimestamp(index, Timestamp.valueOf((LocalDateTime) nonNullValue), calendar);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias, ResultColumnMeta resultColumnMeta
            , MappingContext context) throws SQLException {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(context.zoneId()));
        Timestamp timestamp = resultSet.getTimestamp(alias, calendar);

        LocalDateTime dateTime = null;
        if (timestamp != null) {
            dateTime = timestamp.toLocalDateTime();
        }
        return dateTime;
    }
}
