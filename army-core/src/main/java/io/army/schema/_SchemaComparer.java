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

import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

import java.util.Collection;

public interface _SchemaComparer {

    _SchemaResult compare(SchemaInfo schemaInfo, SchemaMeta schemaMeta, Collection<TableMeta<?>> tableMetas);

    static _SchemaComparer create(final ServerMeta serverMeta) {
        final _SchemaComparer comparer;
        switch (serverMeta.serverDatabase()) {
            case MySQL:
                comparer = MySQLComparer.create(serverMeta);
                break;
            case PostgreSQL:
                comparer = PostgreComparer.create(serverMeta);
                break;
            case Oracle:
            case H2:

            default:
                throw _Exceptions.unexpectedEnum(serverMeta.serverDatabase());
        }
        return comparer;
    }

}
