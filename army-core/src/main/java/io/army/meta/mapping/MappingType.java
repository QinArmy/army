package io.army.meta.mapping;

import io.army.annotation.Column;
import io.army.util.Precision;

import java.sql.JDBCType;


public interface MappingType {

    Class<?> javaType();

    JDBCType jdbcType();


    /**
     * @return value's text without quote
     * @throws IllegalArgumentException value error
     */
    String nullSafeTextValue(Object value);

    boolean isTextValue(String textValue);

    /**
     * if return instance equals {@link Precision#EMPTY} , {@link Column#precision()} effective .
     *
     * @return Precision
     */
    int precision();

    int scale();

}
