package io.army.meta.mapping;

import io.army.annotation.Column;
import io.army.dialect.Dialect;
import io.army.meta.sqltype.SQLDataType;
import io.army.util.Precision;
import org.springframework.lang.NonNull;

import java.sql.JDBCType;
import java.sql.SQLException;

public interface MappingType<T> {

    Class<?> javaType();

    JDBCType jdbcType();

    Object toSql(T t);

    /**
     */
    T toJava(Object databaseValue) throws SQLException;

    @NonNull
    SQLDataType sqlType(Dialect dialect) throws MappingException;

    /**
     * if return instance equals {@link Precision#EMPTY} , {@link Column#precision()} effective .
     *
     * @return Precision
     */
    @NonNull
    Precision precision();

}
