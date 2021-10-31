package io.army.meta.mapping;

import io.army.criteria.impl.Sqls;
import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.dialect.NotSupportDialectException;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.ServerMeta;
import io.army.sqldatatype.SQLDataType;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public interface MappingType extends ParamMeta {

    Class<?> javaType();

    default Class<?> reactiveJavaType(Database database) {
        return javaType();
    }

    JDBCType jdbcType();

    /**
     * @see Sqls#constant(Object, ParamMeta)
     */
    String toConstant(@Nullable FieldMeta<?, ?> paramMeta, Object nonNullValue);

    SQLDataType sqlDataType(Database database) throws NotSupportDialectException;

    default SQLDataType sqlDataType(ServerMeta serverMeta) {
        return null;
    }

    default Object convertBeforeBind(Object nonNull){
        return null;
    }

    default Object convertAfterGet(Object nonNull){
        return null;
    }

    default Object encodeForReactive(Object nonNullValue, MappingContext context) {
        return nonNullValue;
    }

    default Object decodeForReactive(Object nonNullValue, MappingContext context) {
        return nonNullValue;
    }


    void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context) throws SQLException;

    @Nullable
    Object nullSafeGet(ResultSet resultSet, String alias, MappingContext context)
            throws SQLException;

    default boolean singleton() {
        return true;
    }

}
