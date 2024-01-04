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

package io.army.criteria;

import io.army.annotation.GeneratorType;
import io.army.annotation.UpdateMode;
import io.army.mapping.MappingType;
import io.army.meta.DatabaseObject;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * <p>
 * This interface representing column of database table. This interface is base interface of below interfaces:
 * <ul>
 *     <li>{@link FieldMeta}</li>
 *      <li>{@link QualifiedField}</li>
 * </ul>
*
 * @see FieldMeta
 * @see QualifiedField
 */
@SuppressWarnings("unused")
public interface TableField extends SqlField, DefiniteExpression, FieldSelection, TypeMeta, DatabaseObject {

    TableMeta<?> tableMeta();

    /**
     * @return domain mapping property java type.
     */
    Class<?> javaType();

    MappingType mappingType();

    /**
     * @return mapping  field name,see {@link Field#getName()}.
     */
    String fieldName();

    /**
     * <p>
     * Equivalence : {@link  FieldMeta#objectName()}
     *
     *
     * @return column name(lower case).
     */
    String columnName();


    boolean codec();

    boolean nullable();

    UpdateMode updateMode();

    boolean insertable();

    @Nullable
    GeneratorType generatorType();



    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();


}
