package io.army.meta.sqltype.mysql;

import io.army.dialect.Dialect;
import io.army.dialect.MySQLDialect;
import io.army.meta.sqltype.SQLDataType;
import io.army.util.ArrayUtils;

import java.util.List;

public abstract class AbstractMySQLDataType implements SQLDataType {

    protected static final List<Dialect> MYSQL_DIALECT_LIST = ArrayUtils.asUnmodifiableList(
            MySQLDialect.INSTANCE
    );

    @Override
    public List<Dialect> dialectList() {
        return MYSQL_DIALECT_LIST;
    }
}
