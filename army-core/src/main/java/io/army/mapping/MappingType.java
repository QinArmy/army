package io.army.mapping;

import io.army.dialect.NotSupportDialectException;
import io.army.meta.ParamMeta;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlDataType;

import java.sql.JDBCType;


public interface MappingType extends ParamMeta {

    Class<?> javaType();

    JDBCType jdbcType();

    SqlDataType sqlDataType(ServerMeta serverMeta) throws NotSupportDialectException;

    Object convertBeforeBind(SqlDataType sqlDataType, Object nonNull);

    Object convertAfterGet(SqlDataType sqlDataType, Object nonNull);

}
