package io.army.mapping;

import io.army.meta.ParamMeta;
import io.army.meta.ServerMeta;
import io.army.sqldatatype.SqlType;

import java.sql.JDBCType;


public interface MappingType extends ParamMeta {

    Class<?> javaType();

    JDBCType jdbcType();

    SqlType sqlDataType(ServerMeta serverMeta) throws NoMappingException;

    Object convertBeforeBind(ServerMeta serverMeta, Object nonNull);

    Object convertAfterGet(ServerMeta serverMeta, Object nonNull);

}
