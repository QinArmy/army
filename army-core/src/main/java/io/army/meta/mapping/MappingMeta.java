package io.army.meta.mapping;

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
     * @return value's text with quote (if need)
     * @throws IllegalArgumentException value error
     */
    default String nonNullTextValue(Object value) {
        return String.valueOf(value);
    }

    boolean isTextValue(String textValue);

    void nonNullSet(PreparedStatement st, Object nonNullValue, int index) throws SQLException;

    @Nullable
    Object nullSafeGet(ResultSet resultSet, String alias) throws SQLException;

    /**
     * @return java class name + {@code #} + {@link JDBCType#name()}
     */
    @Override
    String toString();

    /**
     * Consistent with {@link Object#hashCode()}
     */
    @Override
    int hashCode();

    /**
     * Consistent with {@link Object#equals(Object)}
     */
    @Override
    boolean equals(Object o);
}
