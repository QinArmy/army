package io.army.meta.mapping;

import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.dialect.NotSupportDialectException;
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
    public SQLDataType sqlDataType(Database database) throws NotSupportDialectException {
        SQLDataType dataType = DATA_TYPE_MAP.get(database.family());
        if (dataType == null) {
            throw MappingMetaUtils.createNotSupportDialectException(this, database);
        }
        return dataType;
    }

    @Override
    public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context) throws SQLException {
        Assert.isInstanceOf(Long.class, nonNullValue, "");
        st.setLong(index, (Long) nonNullValue);
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String alias, ResultColumnMeta resultColumnMeta
            , MappingContext context) throws SQLException {
        return resultSet.getLong(alias);
    }
}
