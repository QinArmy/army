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
import io.army.sqltype.PostgreType;
import io.army.util._Exceptions;

import java.util.Locale;

final class PostgreComparer extends ArmySchemaComparer {

    static PostgreComparer create(ServerMeta serverMeta) {
        return new PostgreComparer(serverMeta);
    }


    private PostgreComparer(ServerMeta serverMeta) {
        super(serverMeta);
    }


    @Override
    boolean compareSchema(SchemaInfo schemaInfo, SchemaMeta schemaMeta) {
        final String serverDatabase, serverSchema, catalog, schema;
        serverDatabase = schemaInfo.catalog();
        serverSchema = schemaInfo.schema();
        catalog = schemaMeta.catalog();
        schema = schemaMeta.schema();
        return !((catalog.isEmpty() || catalog.equals(serverDatabase)) && (schema.isEmpty() || schema.equals(serverSchema)));
    }

    @Override
    boolean compareSqlType(final ColumnInfo columnInfo, final FieldMeta<?> field, final DataType dataType) {
        final String typeName;
        typeName = columnInfo.typeName().toLowerCase(Locale.ROOT);
        if (!(dataType instanceof PostgreType)) {
            return !typeName.equals(dataType.typeName().toLowerCase(Locale.ROOT));
        }
        final boolean notMatch;
        switch ((PostgreType) dataType) {
            case BOOLEAN:
                switch (typeName) {
                    case "boolean":
                    case "bool":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case SMALLINT:
                switch (typeName) {
                    case "int2":
                    case "smallint":
                    case "smallserial":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case INTEGER:
                switch (typeName) {
                    case "int":
                    case "int4":
                    case "serial":
                    case "integer":
                    case "xid":  // https://www.postgresql.org/docs/current/datatype-oid.html
                    case "cid":  // https://www.postgresql.org/docs/current/datatype-oid.html
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case BIGINT:
                switch (typeName) {
                    case "int8":
                    case "bigint":
                    case "bigserial":
                    case "serial8":
                    case "xid8":  // https://www.postgresql.org/docs/current/datatype-oid.html  TODO what's tid ?
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case DECIMAL:
                switch (typeName) {
                    case "numeric":
                    case "decimal":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case FLOAT8:
                switch (typeName) {
                    case "float8":
                    case "double precision":
                    case "float":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case REAL:
                switch (typeName) {
                    case "float4":
                    case "real":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TIME:
                switch (typeName) {
                    case "time":
                    case "time without time zone":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TIMETZ:
                switch (typeName) {
                    case "timetz":
                    case "time with time zone":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TIMESTAMP:
                switch (typeName) {
                    case "timestamp":
                    case "timestamp without time zone":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TIMESTAMPTZ:
                switch (typeName) {
                    case "timestamptz":
                    case "timestamp with time zone":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case CHAR:
                switch (typeName) {
                    case "char":
                    case "character":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case VARCHAR:
                switch (typeName) {
                    case "varchar":
                    case "character varying":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TEXT:
                switch (typeName) {
                    case "text":
                    case "txid_snapshot":  // TODO txid_snapshot is text?
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case VARBIT:
                switch (typeName) {
                    case "bit varying":
                    case "varbit":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case BOOLEAN_ARRAY:
                switch (typeName) {
                    case "boolean[]":
                    case "bool[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case SMALLINT_ARRAY:
                switch (typeName) {
                    case "int2[]":
                    case "smallint[]":
                    case "smallserial[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case INTEGER_ARRAY:
                switch (typeName) {
                    case "int[]":
                    case "int4[]":
                    case "integer[]":
                    case "serial[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case BIGINT_ARRAY:
                switch (typeName) {
                    case "int8[]":
                    case "bigint[]":
                    case "serial8[]":
                    case "bigserial[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case DECIMAL_ARRAY:
                switch (typeName) {
                    case "numeric[]":
                    case "decimal[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case FLOAT8_ARRAY:
                switch (typeName) {
                    case "float8[]":
                    case "float[]":
                    case "double precision[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case REAL_ARRAY:
                switch (typeName) {
                    case "float4[]":
                    case "real[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case CHAR_ARRAY:
                switch (typeName) {
                    case "char[]":
                    case "character[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case VARCHAR_ARRAY:
                switch (typeName) {
                    case "varchar[]":
                    case "character varying[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TEXT_ARRAY:
                switch (typeName) {
                    case "text[]":
                    case "txid_snapshot[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TIME_ARRAY:
                switch (typeName) {
                    case "time[]":
                    case "time without time zone[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TIMETZ_ARRAY:
                switch (typeName) {
                    case "timetz[]":
                    case "time with time zone[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TIMESTAMP_ARRAY:
                switch (typeName) {
                    case "timestamp[]":
                    case "timestamp without time zone[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case TIMESTAMPTZ_ARRAY:
                switch (typeName) {
                    case "timestamptz[]":
                    case "timestamp with time zone[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case VARBIT_ARRAY:
                switch (typeName) {
                    case "varbit[]":
                    case "bit varying[]":
                        notMatch = false;
                        break;
                    default:
                        notMatch = true;
                }
                break;
            case REF_CURSOR:
            case UNKNOWN:
                throw _Exceptions.unexpectedEnum((Enum<?>) dataType);
            default:
                notMatch = !typeName.equalsIgnoreCase(dataType.typeName());
        }
        return notMatch;
    }

    @Override
    boolean compareDefault(ColumnInfo columnInfo, FieldMeta<?> field, DataType sqlType) {
        //currently, false
        return false;
    }

    @Override
    boolean supportColumnComment() {
        return true;
    }

    @Override
    boolean supportTableComment() {
        return true;
    }

    @Override
    String primaryKeyName(TableMeta<?> table) {
        //eg: china_region_pkey
        return table.tableName() + "_pkey";
    }


}
