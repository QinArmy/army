package io.army.meta.sqltype;

import io.army.dialect.Dialect;
import io.army.util.Precision;

/**
 * this class represent the {@code CHAR}  .
 * see support list
 * <ul>
 * <li><a href="https://docs.oracle.com/en/database/oracle/oracle-database/12.2/sqlrf/Data-Types.html#GUID-85E0A0DD-9E90-4AE1-9AD5-93C89FDCFC49">Oracle 12 g</a></li>
 * <li><a href="https://dev.mysql.com/doc/refman/5.7/en/char.html">MySQL 5.7</a></li>
 * <li><a href="https://www.postgresql.org/docs/11/datatype-character.html">Postgre 11.x</a></li>
 * </ul>
 */
public final class Char extends AbstractStandardSQLDataType {

    public static final Char INSTANCE = new Char();


    private Char() {

    }

    @Override
    public DataKind dataKind() {
        return DataKind.TEXT;
    }

    @Override
    protected String innerTypeName(int precision, int scale) {
        return null;
    }

    @Override
    protected String innerTypeName(int precision) {
        return null;
    }

    @Override
    protected String innerTypeName() {
        return null;
    }

    @Override
    public boolean supportDialect(Dialect dialect) {
        return false;
    }

    @Override
    public Precision defaultPrecision() {
        return Precision.DEFAULT_CHAR_PRECISION;
    }


}
