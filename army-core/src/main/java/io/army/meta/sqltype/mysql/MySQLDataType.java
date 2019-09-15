package io.army.meta.sqltype.mysql;

import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQL57Dialect;
import io.army.meta.sqltype.AbstractSQLDataType;
import io.army.util.ArrayUtils;

import java.util.Set;

abstract class MySQLDataType extends AbstractSQLDataType {

    protected static final Set<Dialect> MYSQL_DIALECT_SET = ArrayUtils.asUnmodifiableSet(
            MySQL57Dialect.INSTANCE
    );


    @Override
    public boolean supportDialect(Dialect dialect) {
        return MYSQL_DIALECT_SET.contains(dialect);
    }
}
