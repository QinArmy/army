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
import io.army.criteria.TypeDef;
import io.army.criteria.impl._SQLConsultant;
import io.army.dialect.Database;
import io.army.mapping.MappingType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public enum H2DataType implements SQLType {


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
    public SQLType elementType() {
        return null;
    }

    @Override
    public MappingType mapType(Supplier<? extends ArmyException> errorHandler) {
        return SQLType.super.mapType(errorHandler);
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

    @Override
    public final TypeDef parens(long precision) {
        throw _SQLConsultant.dontSupportPrecision(this);
    }

    @Override
    public final TypeDef parens(int precision, int scale) {
        throw _SQLConsultant.dontSupportPrecisionAndScale(this);
    }


}
