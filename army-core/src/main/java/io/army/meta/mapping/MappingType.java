package io.army.meta.mapping;

import io.army.annotation.Column;
import io.army.util.Precision;
import org.springframework.lang.NonNull;

import java.sql.JDBCType;


public interface MappingType {

    Class<?> javaType();

    JDBCType jdbcType();


    String textValue(Object value);

    boolean isTextValue(String textValue);

    /**
     * if return instance equals {@link Precision#EMPTY} , {@link Column#precision()} effective .
     *
     * @return Precision
     */
    @NonNull
    Precision precision();

}
