package io.army.meta;


import io.army.dialect.Dialect;
import io.army.util.Precision;

import java.util.List;

/**
 * this class represent the {@code NCHAR}  .
 * see support list
 * <ul>
 * <li><a href="https://docs.oracle.com/en/database/oracle/oracle-database/12.2/sqlrf/Data-Types.html#GUID-85E0A0DD-9E90-4AE1-9AD5-93C89FDCFC49">Oracle 12 g</a></li>
 * <li><a href="https://dev.mysql.com/doc/refman/5.7/en/char.html">MySQL 5.7</a></li>
 * <li><a href="https://www.postgresql.org/docs/11/datatype-character.html">Postgre 11.x</a></li>
 * </ul>
 */
public class NCharSQLDataType extends AbstractStandardSQLDataType {

    public static final NCharSQLDataType INSTANCE = new NCharSQLDataType();


    private NCharSQLDataType() {

    }

    @Override
    public String typeName() {
        return "NCHAR";
    }

    @Override
    public String typeName(int precision) {
        return "NCHAR(" + precision + ")";
    }


    @Override
    public Precision defaultPrecision() {
        return Precision.DEFAULT_CHAR_PRECISION;
    }

    @Override
    public List<Dialect> dialectList() {
        return STANDARD_SUPPORT_DIALECT_LIST;
    }
}
