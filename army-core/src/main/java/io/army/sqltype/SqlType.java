package io.army.sqltype;

import io.army.dialect.Database;
import io.army.mapping.MappingType;

import javax.annotation.Nullable;

public interface SqlType extends DataType {

    Database database();

    ArmyType armyType();

    Class<?> firstJavaType();

    @Nullable
    Class<?> secondJavaType();


    /**
     * <p>
     * For example:
     *    <ul>
     *        <li>one dimension BIGINT_ARRAY return BIGINT</li>
     *        <li>tow dimension BIGINT_ARRAY return BIGINT too</li>
     *    </ul>
     * <br/>
     *
     * @return element type of array(1-n dimension)
     */
    @Nullable
    SqlType elementType();


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
