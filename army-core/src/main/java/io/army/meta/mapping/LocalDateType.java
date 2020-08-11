package io.army.meta.mapping;

import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.dialect.NotSupportDialectException;
import io.army.sqltype.MySQLDataType;
import io.army.sqltype.PostgreDataType;
import io.army.sqltype.SQLDataType;
import io.army.util.Assert;

import java.sql.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

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
        Assert.isInstanceOf(LocalDate.class, nonNullValue, "");
        st.setDate(index, Date.valueOf((LocalDate) nonNullValue));
    }

    @Override
    public Object nullSafeGet(ResultSet st, String alias, ResultColumnMeta resultColumnMeta
            , MappingContext context) throws SQLException {

        Date date = st.getDate(alias);
        LocalDate localDate = null;
        if (date != null) {
            localDate = date.toLocalDate();
        }
        return localDate;
    }
}
