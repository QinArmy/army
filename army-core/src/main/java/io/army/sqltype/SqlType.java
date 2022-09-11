package io.army.sqltype;

import io.army.dialect.Database;

public interface SqlType {


    /**
     * @see Enum#name()
     */
    String name();

    Database database();

    default boolean supportNoPrecision() {
        throw new UnsupportedOperationException();
    }

    default boolean supportPrecision() {
        throw new UnsupportedOperationException();
    }

    default boolean supportPrecisionScale() {
        throw new UnsupportedOperationException();
    }

}
