package io.army.meta.sqltype.oracle;

import io.army.dialect.Dialect;
import io.army.dialect.oracle.Oracle12Dialect;
import io.army.meta.sqltype.AbstractSQLDataType;
import io.army.util.ArrayUtils;

import java.util.Set;


abstract class OracleSQLDataType extends AbstractSQLDataType {

    protected final Set<Dialect> ORACLE_DIALECT_SET = ArrayUtils.asUnmodifiableSet(
            Oracle12Dialect.INSTANCE
    );



    @Override
    public boolean supportDialect(Dialect dialect) {
        return ORACLE_DIALECT_SET.contains(dialect);
    }
}
