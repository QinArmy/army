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

package io.army.dialect.mysql;

import io.army.dialect._Constant;
import io.army.dialect._DdlParser;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.schema._FieldResult;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.struct.TextEnum;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.List;

final class MySQLDdlParser extends _DdlParser<MySQLParser> {

    static MySQLDdlParser create(MySQLParser dialect) {
        return new MySQLDdlParser(dialect);
    }

    MySQLDdlParser(MySQLParser dialect) {
        super(dialect);
    }

    @Override
    public void modifyTableComment(final TableMeta<?> table, final List<String> sqlList) {
        final StringBuilder builder = new StringBuilder()
                .append("ALTER TABLE ");
        this.parser.safeObjectName(table, builder);
        appendColumnComment(table, builder);

    }


    @Override
    protected <T> void appendIndexOutTableDef(final IndexMeta<T> index, final StringBuilder builder) {
        if (index.isPrimaryKey()) {
            throw new IllegalArgumentException();
        }
        if (builder.length() > 0) {
            builder.append(_Constant.SPACE);
        }
        builder.append("CREATE");
        if (index.isUnique()) {
            builder.append(" UNIQUE");
        }

        builder.append(" INDEX ");
        this.parser.identifier(index.name(), builder);
        appendIndexType(index, builder);

        builder.append(_Constant.SPACE_ON_SPACE);
        this.parser.safeObjectName(index.tableMeta(), builder);
        appendIndexFieldList(index, builder);

    }

    @Override
    protected void dataType(final FieldMeta<?> field, final DataType dataType, final StringBuilder builder) {
        switch ((MySQLType) dataType) {
            case TINYINT:
            case SMALLINT:
            case INT:
            case MEDIUMINT:
            case BIGINT:
            case DATE:
            case YEAR:
            case BOOLEAN:
            case JSON:
            case FLOAT:
            case DOUBLE:

            case TINYTEXT:
            case TINYBLOB:
            case MEDIUMBLOB:
            case MEDIUMTEXT:
            case LONGBLOB:
            case LONGTEXT:

            case GEOMETRY:
            case POINT:
            case LINESTRING:
            case POLYGON:
            case MULTIPOINT:
            case MULTIPOLYGON:
            case MULTILINESTRING:
            case GEOMETRYCOLLECTION:

            case TINYINT_UNSIGNED:
            case SMALLINT_UNSIGNED:
            case MEDIUMINT_UNSIGNED:
            case INT_UNSIGNED:
            case BIGINT_UNSIGNED:
                builder.append(dataType.typeName());
                break;
            case DECIMAL_UNSIGNED:
            case DECIMAL: {
                builder.append("DECIMAL");
                decimalType(field, builder);
                if (dataType == MySQLType.DECIMAL_UNSIGNED) {
                    builder.append(SPACE_UNSIGNED);
                }
            }
            break;
            case TIME:
            case DATETIME: {
                builder.append(dataType.typeName());
                timeTypeScale(field, dataType, builder);
            }
            break;
            case BINARY:
            case CHAR: {
                builder.append(dataType.typeName());
                precision(field, dataType, 0xFF, 1, builder);
            }
            break;
            case VARBINARY:
            case VARCHAR: {
                builder.append(dataType.typeName());
                precision(field, dataType, 0xFFFF, 0xFF, builder);
            }
            break;
            case SET:
                setType(field, builder);
                break;
            case BIT: {
                builder.append(dataType.typeName());
                precision(field, dataType, 64, 1, builder);
            }
            break;
            case ENUM:
                enumType(field, builder);
                break;
            case BLOB:
            case TEXT: {
                builder.append(dataType.typeName());
                if (field.precision() > 0) {
                    precision(field, dataType, 0xFFFF, 0xFFFF, builder);
                }
            }
            break;
            default:
                throw _Exceptions.unexpectedEnum((MySQLType) dataType);
        }


    }

    @Override
    protected void postDataType(FieldMeta<?> field, DataType dataType, StringBuilder builder) {
        dataType(field, dataType, builder);
    }

    @Override
    protected void appendPostGenerator(FieldMeta<?> field, StringBuilder builder) {
        builder.append(" AUTO_INCREMENT");
    }

    @Override
    protected void appendTableOption(final TableMeta<?> table, final StringBuilder builder) {
        final String tableOption, partitionOption;
        tableOption = table.tableOption();
        if (_StringUtils.hasText(tableOption)) {
            // TODO validate
            builder.append(tableOption);
        } else {
            builder.append(" ENGINE = InnoDB CHARACTER SET  = 'utf8mb4'");
        }
        appendColumnComment(table, builder);

        partitionOption = table.partitionOption();
        if (_StringUtils.hasText(partitionOption)) {
            // TODO validate
            builder.append(partitionOption);
        }

    }


    @Override
    protected void doModifyColumn(final _FieldResult result, final StringBuilder builder) {
        appendSpaceIfNeed(builder);

        final FieldMeta<?> field;
        field = result.field();
        final String defaultValue;
        if (result.containSqlType() || result.containNullable() || result.containComment()) {
            builder.append("MODIFY COLUMN ");
            columnDefinition(field, builder);
        } else if (!result.containDefault()) {
            //no bug,never here
            throw new IllegalArgumentException();
        } else if (!_StringUtils.hasText((defaultValue = field.defaultValue()))) {
            builder.append(ALTER_COLUMN_SPACE);
            this.parser.safeObjectName(field, builder);
            builder.append(SPACE_DROP_DEFAULT);
        } else if (checkDefaultComplete(field, defaultValue)) {
            builder.append(ALTER_COLUMN_SPACE)
                    .append(SPACE_SET_DEFAULT_SPACE)
                    .append(defaultValue);
        }

    }

    private void setType(final FieldMeta<?> field, final StringBuilder builder) {
        builder.append("SET(");
        int index = 0;
        for (Object e : field.elementTypes().get(0).getEnumConstants()) {
            if (index > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append(_Constant.QUOTE);
            if (e instanceof TextEnum) {
                builder.append(((TextEnum) e).text());
            } else {
                builder.append(((Enum<?>) e).name());
            }
            builder.append(_Constant.QUOTE);

            index++;
        }
        builder.append(_Constant.SPACE_RIGHT_PAREN);
    }

    private static void enumType(final FieldMeta<?> field, final StringBuilder builder) {
        builder.append("ENUM(");
        int index = 0;
        for (Object e : field.javaType().getEnumConstants()) {
            if (index > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append(_Constant.QUOTE);
            if (e instanceof TextEnum) {
                builder.append(((TextEnum) e).text());
            } else {
                builder.append(((Enum<?>) e).name());
            }
            builder.append(_Constant.QUOTE);
            index++;
        }
        builder.append(_Constant.SPACE_RIGHT_PAREN);
    }


}
