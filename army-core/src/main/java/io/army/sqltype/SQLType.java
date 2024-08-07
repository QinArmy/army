/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.sqltype;

import io.army.ArmyException;
import io.army.dialect.Database;
import io.army.mapping.MappingType;

import io.army.lang.Nullable;
import java.util.function.Supplier;

public interface SQLType extends DataType {

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
    SQLType elementType();


    default MappingType mapType(Supplier<? extends ArmyException> errorHandler) {
        throw new UnsupportedOperationException();
    }


}
