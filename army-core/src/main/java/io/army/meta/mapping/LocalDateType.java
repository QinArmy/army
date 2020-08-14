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

import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public final class LocalDateType extends AbstractMappingType {

    private static final Map<Database, SQLDataType> DATA_TYPE_MAP = createDataTypeMap();

    private static final LocalDateType INSTANCE = new LocalDateType();

    public static LocalDateType build(Class<?> typeClass) {
        Assert.isTrue(LocalDate.class == typeClass, "");
        return INSTANCE;
    }

    private static Map<Database, SQLDataType> createDataTypeMap() {
        EnumMap<Database, SQLDataType> map = new EnumMap<>(Database.class);

        map.put(Database.MySQL, MySQLDataType.DATE);
        map.put(Database.Postgre, PostgreDataType.DATE);

        return Collections.unmodifiableMap(map);
    }


    private LocalDateType() {
    }

    @Override
    public Class<?> javaType() {
        return LocalDate.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.DATE;
    }

    @Override
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context)
            throws SQLException {
        Assert.isInstanceOf(LocalDate.class, nonNullValue, "");
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(context.zoneId()));
        st.setDate(index, Date.valueOf((LocalDate) nonNullValue), calendar);
    }

    @Override
    public Object nullSafeGet(ResultSet st, String alias, ResultColumnMeta resultColumnMeta
            , MappingContext context) throws SQLException {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(context.zoneId()));
        Date date = st.getDate(alias, calendar);
        LocalDate localDate = null;
        if (date != null) {
            localDate = date.toLocalDate();
        }
        return localDate;
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
