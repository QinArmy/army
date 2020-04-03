package io.army.meta.mapping;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public interface MappingType {

    Class<?> javaType();

    JDBCType jdbcType();


    /**
     * @return value's text without quote
     * @throws IllegalArgumentException value error
     */
    default String nonNullTextValue(Object value) {
        return String.valueOf(value);
    }

    boolean isTextValue(String textValue);

    void nonNullSet(PreparedStatement st, Object nonNullValue, int index) throws SQLException;

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
