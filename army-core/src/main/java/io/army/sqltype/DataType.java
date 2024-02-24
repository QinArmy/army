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

import io.army.criteria.TypeDef;

/**
 * <p>This is base interface of following:
 * <ul>
 *     <li>{@link SQLType}</li>
 *     <li>{@link CustomType}</li>
 * </ul>
 *
 * @since 0.6.0
 */
public interface DataType extends TypeDef {


    /**
     * <p>SQL type's alias (not type name) in java language.
     *
     * @see #typeName()
     * @see Enum#name()
     */
    String name();


    boolean isUnknown();

    boolean isArray();


    TypeDef parens(long precision);

    TypeDef parens(int precision, int scale);


    /**
     * <p>This interface representing user-defined type or unrecognized database build-in type.
     */
    interface CustomType extends DataType {

        @Override
        TypeDef._TypeDefCharacterSetSpec parens(long precision);

    }


    /**
     * <p>This method is equivalent to {@code   DataType.from(typeName,false)} :
     * <p><strong>NOTE</strong>: only when {@link ArmyType} couldn't express appropriate type,you use this method.<br/>
     * It means you should prefer {@link SQLType}.
     *
     * @param typeName non-null
     * @return {@link DataType} instance
     * @see #from(String, boolean)
     */
    static CustomType from(String typeName) {
        return DataTypeFactory.typeFrom(typeName, false);
    }

    /**
     * <p>Get one {@link DataType} instance
     * <p><strong>NOTE</strong>: only when {@link ArmyType} couldn't express appropriate type,you use this method.<br/>
     * It means you should prefer {@link SQLType}.
     *
     * @param typeName        database data type name,if typeName endWith '[]',then {@link DataType#isArray()} always return true.
     * @param caseSensitivity if false ,then {@link DataType#typeName()} always return upper case.
     * @return {@link DataType} that representing user-defined type.
     * @throws IllegalArgumentException throw when typeName have no text.
     */
    static CustomType from(String typeName, boolean caseSensitivity) {
        return DataTypeFactory.typeFrom(typeName, caseSensitivity);
    }

}
