package io.army.meta.sqltype;

import io.army.dialect.Dialect;
import io.army.util.Precision;

import java.util.List;

/**
 * this class represent the {@code VARCHAR} of database .
 * see document list
 * <ul>
 * <li><a href="https://dev.mysql.com/doc/refman/5.7/en/char.html">MySQL 5.7</a></li>
 * <li><a href="https://docs.oracle.com/en/database/oracle/oracle-database/12.2/sqlrf/Data-Types.html#GUID-DF7E10FC-A461-4325-A295-3FD4D150809E">Oracle 12g</a></li>
 * <li><a href="https://www.postgresql.org/docs/11/datatype-character.html">Postgre 11.x</a></li>
 * </ul>
 */
public final class Varchar implements SQLDataType {

    public static final Varchar INSTANCE = new Varchar();


    private Varchar() {

    }

    @Override
    public String typeName() {
        return "VARCHAR";
    }

    @Override
    public String typeName(int precision) {
        return "VARCHAR(" + precision + ")";
    }


    @Override
    public Precision defaultPrecision() {
        return Precision.DEFAULT_CHAR_PRECISION;
    }

    @Override
    public List<Dialect> dialectList() {
        return null;
    }
}
