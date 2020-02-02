package io.army.meta.mapping;

import io.army.annotation.Column;
import io.army.meta.FieldMeta;
import io.army.util.Precision;

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
    String nullSafeTextValue(Object value);

    boolean isTextValue(String textValue);

    void nullSafeSet(PreparedStatement st,Object value, int index)throws SQLException;

    Object nullSafeGet(ResultSet resultSet, String alias)throws SQLException;
}
