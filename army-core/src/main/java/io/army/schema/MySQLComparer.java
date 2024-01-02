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

import io.army.dialect.Database;
import io.army.meta.FieldMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

final class MySQLComparer extends ArmySchemaComparer {

    static MySQLComparer create(ServerMeta serverMeta) {
        return new MySQLComparer(serverMeta);
    }

    private static final Logger LOG = LoggerFactory.getLogger(MySQLComparer.class);

    private MySQLComparer(ServerMeta serverMeta) {
        super(serverMeta);
        if (serverMeta.serverDatabase() != Database.MySQL) {
            throw new IllegalArgumentException("serverMeta error.");
        }
    }

    @Override
    boolean compareSchema(SchemaInfo schemaInfo, SchemaMeta schemaMeta) {
        String database, catalog, schema;
        database = schemaInfo.catalog();
        if (database == null) {
            database = schemaInfo.schema();
        }
        catalog = schemaMeta.catalog();
        schema = schemaMeta.schema();
        return (!catalog.isEmpty() && !catalog.equals(database))
                || (!schema.isEmpty() && !schema.equals(database));
    }

    @Override
    boolean compareSqlType(final ColumnInfo columnInfo, final FieldMeta<?> field, final DataType dataType) {
        final String upperCaseTypeName;
        upperCaseTypeName = columnInfo.typeName().toUpperCase(Locale.ROOT);

        if (!(dataType instanceof MySQLType)) {
            return true;
        }

        final boolean match;
        switch ((MySQLType) dataType) {
            case BOOLEAN: {
                switch (upperCaseTypeName) {
                    case "BOOLEAN":
                    case "TINYINT":
                        match = true;
                        break;
                    case "BIT": {
                        match = this.jdbc;  // MySQL connector/J
                    }
                    break;
                    default:
                        match = false;

                } // inner switch
            }
            break;
            case INT: {
                switch (upperCaseTypeName) {
                    case "INT":
                    case "INTEGER":
                        match = true;
                        break;
                    default:
                        match = false;
                }
            }
            break;
            case INT_UNSIGNED: {
                switch (upperCaseTypeName) {
                    case "INT UNSIGNED":
                    case "INTEGER UNSIGNED":
                        match = true;
                        break;
                    default:
                        match = false;
                }
            }
            break;
            case CHAR:
            case VARCHAR:
            case BINARY:
            case VARBINARY: {
                final boolean nameMatch;
                nameMatch = upperCaseTypeName.equalsIgnoreCase(dataType.typeName());
                final int precision = field.precision();
                match = nameMatch && (precision == -1 || precision == columnInfo.precision());
            }
            break;
            case DECIMAL:
            case DECIMAL_UNSIGNED: {
                final boolean nameMatch;
                nameMatch = upperCaseTypeName.equalsIgnoreCase(dataType.typeName());
                final int precision, scale;
                precision = field.precision();
                scale = field.scale();
                match = nameMatch
                        && (precision == -1 || precision == columnInfo.precision())
                        && (scale == -1 || scale == columnInfo.scale());
            }
            break;
            case GEOMETRYCOLLECTION: {
                switch (upperCaseTypeName) {
                    case "GEOMCOLLECTION":
                    case "GEOMETRYCOLLECTION":
                        match = true;
                        break;
                    default:
                        match = false;
                }
            }
            break;
            default:
                match = upperCaseTypeName.equalsIgnoreCase(dataType.typeName());
        }
        return !match;
    }

    @Override
    boolean compareDefault(ColumnInfo columnInfo, FieldMeta<?> field, DataType sqlType) {
//        switch ((MySqlType) sqlType) {
//            case INT:
//            case BIGINT:
//            case DECIMAL:
//            case BOOLEAN:
//            case DATETIME:
//            case DATE:
//            case TIME:
//            case YEAR:
//
//            case CHAR:
//            case VARCHAR:
//            case ENUM:
//            case JSON:
//            case SET:
//            case TINYTEXT:
//            case TEXT:
//            case MEDIUMTEXT:
//            case LONGTEXT:
//
//            case BINARY:
//            case VARBINARY:
//            case TINYBLOB:
//            case BLOB:
//            case MEDIUMBLOB:
//            case LONGBLOB:
//
//            case BIT:
//            case FLOAT:
//            case DOUBLE:
//
//            case TINYINT:
//            case TINYINT_UNSIGNED:
//            case SMALLINT:
//            case SMALLINT_UNSIGNED:
//            case MEDIUMINT:
//            case MEDIUMINT_UNSIGNED:
//            case INT_UNSIGNED:
//            case BIGINT_UNSIGNED:
//            case DECIMAL_UNSIGNED:
//
//            case POINT:
//            case LINESTRING:
//            case POLYGON:
//            case MULTIPOINT:
//            case MULTIPOLYGON:
//            case MULTILINESTRING:
//            case GEOMETRYCOLLECTION:
//                break;
//            default:
//        } //TODO
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
        return "PRIMARY";
    }


}
