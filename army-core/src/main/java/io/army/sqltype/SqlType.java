package io.army.sqltype;

import io.army.ArmyException;
import io.army.dialect.Database;
import io.army.mapping.MappingType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

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


    default MappingType mapType(Supplier<? extends ArmyException> errorHandler) {
        throw new UnsupportedOperationException();
    }


}
