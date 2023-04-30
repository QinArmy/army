package io.army.sqltype;

import io.army.dialect.Database;
import io.army.mapping.MappingType;

public interface SqlType {


    /**
     * @see Enum#name()
     */
    String name();

    Database database();

    boolean isUserDefined();

    boolean isUnknown();

    default boolean isNoPrecision() {
        throw new UnsupportedOperationException();
    }

    default boolean isSupportPrecision() {
        throw new UnsupportedOperationException();
    }

    default boolean isSupportPrecisionScale() {
        throw new UnsupportedOperationException();
    }

    default boolean isSupportCharset() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default MappingType mappingType() {
        throw new UnsupportedOperationException();
    }

    default void sqlTypeName(MappingType type, StringBuilder builder) {
        throw new UnsupportedOperationException();
    }

}
