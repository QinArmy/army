package io.army.meta;

import io.army.meta.oracle.OracleSQLDataType;
import io.army.util.Precision;


/**
 * this class represent the {@code BINARY_DOUBLE} of oracle .
 * see support list
 * <ul>
 * <li><a href="https://docs.oracle.com/en/database/oracle/oracle-database/12.2/sqlrf/Data-Types.html#GUID-CFE7487C-A4D0-4E90-A836-2697C45BDD10">Oracle 12 g</a></li>
 * </ul>
 */
public final class BinaryDoubleSQLDataType extends OracleSQLDataType {


    private static final BinaryDoubleSQLDataType INSTANCE = new BinaryDoubleSQLDataType();


    private BinaryDoubleSQLDataType() {

    }


    public String typeName() {
        return "BINARY_DOUBLE";
    }


    public String typeName(int precision) {
        return "BINARY_DOUBLE(" + precision + ")";
    }


    public String typeName(int precision, int scale) {
        return "BINARY_DOUBLE(" + precision + "," + scale + ")";
    }

    @Override
    public Precision defaultPrecision() {
        return Precision.EMPTY;
    }
}
