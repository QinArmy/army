package io.army.mapping;

import io.army.dialect.DialectEnvironment;
import io.army.dialect.NotSupportDialectException;
import io.army.meta.ParamMeta;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;

import java.sql.JDBCType;


public interface MappingType extends ParamMeta {

    Class<?> javaType();

    @Deprecated
    default JDBCType jdbcType() {
        throw new UnsupportedOperationException();
    }

    SqlType sqlType(ServerMeta serverMeta) throws NotSupportDialectException;

    default Object convertBeforeBind(SqlType sqlDataType, DialectEnvironment env, Object nonNull) {
        throw new UnsupportedOperationException();
    }

    default Object convertAfterGet(SqlType sqlDataType, DialectEnvironment env, Object nonNull) {
        throw new UnsupportedOperationException();
    }

    default Object convertBeforeBind(SqlType sqlDataType, Object nonNull) {
        throw new UnsupportedOperationException();
    }

    default Object convertAfterGet(SqlType sqlDataType, Object nonNull) {
        throw new UnsupportedOperationException();
    }

}
