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

import io.army.dialect.Database;

import javax.annotation.Nullable;

public enum H2DataType implements SqlType {


    BOOLEAN,
    DECIMAL,
    VARBINARY,
    ENUM,
    ;


    @Override
    public final Database database() {
        return Database.H2;
    }

    @Override
    public ArmyType armyType() {
        return null;
    }

    @Override
    public Class<?> firstJavaType() {
        return null;
    }

    @Nullable
    @Override
    public Class<?> secondJavaType() {
        return null;
    }

    @Nullable
    @Override
    public SqlType elementType() {
        return null;
    }


    @Override
    public String typeName() {
        return null;
    }

    @Override
    public boolean isUnknown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isArray() {
        return false;
    }


}
