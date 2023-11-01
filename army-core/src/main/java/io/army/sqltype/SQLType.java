package io.army.sqltype;

import io.army.dialect.Database;
import io.army.mapping.MappingType;
import io.army.session.ArmyType;
import io.army.session.DataType;

public interface SQLType extends DataType {

    Database database();

    ArmyType armyType();


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


}
