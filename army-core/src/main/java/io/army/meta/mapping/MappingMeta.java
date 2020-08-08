package io.army.meta.mapping;

import io.army.dialect.MappingContext;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public interface MappingMeta extends ParamMeta {

    Class<?> javaType();

    JDBCType jdbcType();


    /**
     * design for {@link io.army.dialect.DDL}
     *
     * @return value's text with quote (if need)
     * @throws IllegalArgumentException value error
     */
    default String nonNullTextValue(Object value) {
        return String.valueOf(value);
    }

    boolean isTextValue(String textValue);

    void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context) throws SQLException;

    @Nullable
    Object nullSafeGet(ResultSet resultSet, String alias, MappingContext context) throws SQLException;

}
