package io.army.meta.sqltype.oracle;

import io.army.dialect.Dialect;
import io.army.dialect.Oracle12Dialect;
import io.army.meta.sqltype.SQLDataType;
import org.qinarmy.foundation.util.ArrayUtils;

import java.util.List;


public abstract class OracleSQLDataType implements SQLDataType {

    private final List<Dialect> ORACLE_DIALECT_LIST = ArrayUtils.asUnmodifiableList(
            Oracle12Dialect.INSTANCE
    );


    @Override
    public final List<Dialect> dialectList() {
        return ORACLE_DIALECT_LIST;
    }
}
