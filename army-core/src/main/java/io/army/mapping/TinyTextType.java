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

package io.army.mapping;

import io.army.dialect.UnsupportedDialectException;
import io.army.mapping.array.TinyTextArrayType;
import io.army.meta.ServerMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.sqltype.PostgreType;
import io.army.sqltype.SQLiteType;

public final class TinyTextType extends ArmyTextType {

    public static TinyTextType from(final Class<?> javaType) {
        if (javaType != String.class) {
            throw errorJavaType(TinyTextType.class, javaType);
        }
        return INSTANCE;
    }

    public static final TinyTextType INSTANCE = new TinyTextType();

    /**
     * private constructor
     */
    private TinyTextType() {
    }


    @Override
    public LengthType lengthType() {
        return LengthType.DEFAULT;
    }

    @Override
    public MappingType arrayTypeOfThis() {
        return TinyTextArrayType.LINEAR;
    }


    @Override
    public DataType map(ServerMeta meta) throws UnsupportedDialectException {
        final DataType dataType;
        switch (meta.serverDatabase()) {
            case MySQL:
                dataType = MySQLType.TINYTEXT;
                break;
            case PostgreSQL:
                dataType = PostgreType.TEXT;
                break;
            case SQLite:
                dataType = SQLiteType.TEXT;
                break;
            default:
                throw MAP_ERROR_HANDLER.apply(this, meta);
        }
        return dataType;
    }


}
