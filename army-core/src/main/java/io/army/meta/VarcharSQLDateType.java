package io.army.meta;

import io.army.criteria.dialect.Dialect;
import io.army.criteria.dialect.Mysql57Dialect;
import io.army.criteria.dialect.Oracle12Dialect;
import io.army.criteria.dialect.Postgre11Dialect;
import io.army.util.ArrayUtils;
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
public final class VarcharSQLDateType implements SQLType {

    private static final VarcharSQLDateType INSTANCE = new VarcharSQLDateType();

    private static final Precision DEFAULT_PRECISION = new Precision(255, null);

    private static final List<Dialect> SUPPORT_DIALECT_LIST = ArrayUtils.asUnmodifiableList(
            Mysql57Dialect.INSTANCE,
            Oracle12Dialect.INSTANCE,
            Postgre11Dialect.INSTANCE
    );

    private VarcharSQLDateType() {
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
    public String typeName(int precision, int scale) {
        return typeName(precision);
    }

    @Override
    public Precision defaultPrecision() {
        return DEFAULT_PRECISION;
    }

    @Override
    public List<Dialect> dialectList() {
        return SUPPORT_DIALECT_LIST;
    }
}
