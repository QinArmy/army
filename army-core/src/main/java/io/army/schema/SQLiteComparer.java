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

package io.army.schema;

import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.SQLiteType;
import io.army.util._Exceptions;

final class SQLiteComparer extends ArmySchemaComparer {

    static SQLiteComparer create(ServerMeta serverMeta) {
        return new SQLiteComparer(serverMeta);
    }

    private SQLiteComparer(ServerMeta serverMeta) {
        super(serverMeta);
    }

    @Override
    boolean compareSchema(SchemaInfo schemaInfo, SchemaMeta schemaMeta) {
        // SQLite jdbc always return null
        return false;
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_altertable.html">ALTER TABLE</a>
     */
    @Override
    boolean compareSqlType(final ColumnInfo columnInfo, final FieldMeta<?> field, final DataType dataType) {
        if (!(dataType instanceof SQLiteType)) {
            throw _Exceptions.mapMethodError(field.mappingType(), SQLiteType.class);
        }
        return false; // SQLite don't support modify column
    }

    @Override
    boolean compareDefault(final ColumnInfo columnInfo, final FieldMeta<?> field, final DataType sqlType) {
        return false;
    }

    @Override
    boolean supportColumnComment() {
        // SQLite don't support comment
        return false;
    }

    @Override
    boolean supportTableComment() {
        // SQLite don't support comment
        return false;
    }

    @Override
    String primaryKeyName(TableMeta<?> table) {
        return "";
    }


}
