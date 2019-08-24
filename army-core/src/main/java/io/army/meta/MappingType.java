package io.army.meta;

import io.army.annotation.Column;
import io.army.dialect.Dialect;
import io.army.util.Precision;
import org.springframework.lang.NonNull;

import java.sql.JDBCType;

public interface MappingType<T> {

    Class<?> javaType();

    JDBCType jdbcType();

    Object toSql(T t);

    /**
     * @param databaseValue
     * @return
     * @throws IllegalArgumentException
     */
    T toJava(Object databaseValue);

    SQLDataType sqlType(Dialect dialect);

    /**
     * if return instance equals {@link Precision#EMPTY} , {@link Column#length()} effective .
     *
     * @return Precision
     */
    @NonNull
    Precision precision();

}
