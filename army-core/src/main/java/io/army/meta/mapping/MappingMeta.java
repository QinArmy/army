package io.army.meta.mapping;

import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.sqltype.SQLDataType;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public interface MappingMeta extends ParamMeta {

    Class<?> javaType();

    JDBCType jdbcType();

    /**
     * @see io.army.criteria.impl.SQLS#constant(Object, ParamMeta)
     */
    String toConstant(@Nullable FieldMeta<?, ?> paramMeta, Object nonNullValue);

    SQLDataType sqlDataType(Database database) throws NotSupportDialectException;

    void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context) throws SQLException;

    @Nullable
    Object nullSafeGet(ResultSet resultSet, String alias, ResultColumnMeta resultColumnMeta, MappingContext context)
            throws SQLException;

    default boolean singleton() {
        return true;
    }

}
