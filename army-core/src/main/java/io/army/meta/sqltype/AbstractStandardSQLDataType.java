package io.army.meta.sqltype;

import io.army.dialect.Dialect;
import io.army.dialect.MySQL57Dialect;
import io.army.dialect.Oracle12Dialect;
import io.army.dialect.Postgre11Dialect;
import io.army.util.ArrayUtils;

import java.util.List;

public abstract class AbstractStandardSQLDataType implements SQLDataType {

    List<Dialect> STANDARD_SUPPORT_DIALECT_LIST = ArrayUtils.asUnmodifiableList(
            Oracle12Dialect.INSTANCE,
            MySQL57Dialect.INSTANCE,
            Postgre11Dialect.INSTANCE
    );


}
