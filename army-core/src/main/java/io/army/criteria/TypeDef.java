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


/**
 * <p>This interface is base interface of {@link io.army.sqltype.DataType} :
 * @since 0.6.0
 */
public interface TypeDef extends TypeItem {

    /**
     * <p>SQL type's type name in database.
     *
     * @return SQL type's name in database
     */
    String typeName();


    interface _TypeDefCollateClause extends TypeDef {

        TypeDef collate(String collationName);

    }

    interface _TypeDefCharacterSetSpec extends _TypeDefCollateClause {

        _TypeDefCollateClause characterSet(String charsetName);

    }


}
