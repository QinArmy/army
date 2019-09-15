package io.army.meta.sqltype;

import io.army.dialect.Dialect;
import io.army.dialect.mysql.MySQL57Dialect;
import io.army.dialect.oracle.Oracle12Dialect;
import io.army.dialect.postgre.Postgre11Dialect;
import io.army.util.ArrayUtils;

import java.util.List;

public abstract class AbstractStandardSQLDataType extends AbstractSQLDataType implements SQLDataType {

    List<Dialect> STANDARD_SUPPORT_DIALECT_LIST = ArrayUtils.asUnmodifiableList(
            Oracle12Dialect.INSTANCE,
            MySQL57Dialect.INSTANCE,
            Postgre11Dialect.INSTANCE
    );


}
