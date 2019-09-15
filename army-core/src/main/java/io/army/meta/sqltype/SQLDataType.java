package io.army.meta.sqltype;

import io.army.dialect.Dialect;

/**
 * A common interface to all dialect-specific data types.
 */
public interface SQLDataType {

    DataKind dataKind();


    /**
     * return sql date type name (e.g {@code DECIMAL(14,2)} {@code DECIMAL(9,6)}) with precision
     *
     * @return sql date type name with precision and scale
     */
    String typeName(int precision, int scale);


    boolean supportDialect(Dialect dialect);

}
