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

package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.mapping.StringType;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.TableMeta;
import io.army.schema.FieldResult;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.struct.TextEnum;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.List;

final class MySQLDdlParser extends ArmyDdlParser<MySQLParser> {

    static MySQLDdlParser create(MySQLParser dialect) {
        return new MySQLDdlParser(dialect);
    }

    MySQLDdlParser(MySQLParser dialect) {
        super(dialect);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.4/en/create-table.html">CREATE TABLE Statement</a>
     */
    @Override
    public <T> void createTable(final TableMeta<T> table, final List<String> sqlList) {
        final StringBuilder builder;
        builder = createTableCommandBuilder(table)
                .append(_Constant.LEFT_PAREN);

        // column definitions
        columnDefinitionList(table, builder);

        // index definitions
        for (IndexMeta<T> index : table.indexList()) {
            builder.append(_Constant.COMMA)
                    .append("\n\t");

            indexDefinition(index, builder);

        } // index loop

        // right paren
        builder.append('\n')
                .append(_Constant.RIGHT_PAREN);

        tableOptions(table, builder);

        tablePartitionOptions(table, builder);

        sqlList.add(builder.toString());

    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.4/en/alter-table.html">ALTER TABLE Statement</a>
     */
    @Override
    public void modifyTableComment(final TableMeta<?> table, final List<String> sqlList) {
        final StringBuilder builder = new StringBuilder();
        builder.append("ALTER")
                .append(_Constant.SPACE)
                .append("TABLE");

        tableName(table, builder);

        builder.append(_Constant.SPACE)
                .append("COMMENT")
                .append(_Constant.SPACE_EQUAL_SPACE);

        this.parser.safeLiteral(StringType.INSTANCE, table.comment(), false, builder);

        sqlList.add(builder.toString());
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
    void tableOptions(final TableMeta<?> table, final StringBuilder builder) {
        builder.append('\n');
        final String options = table.tableOptions();
        if (_StringUtils.hasText(options)) {
            // optimize me
            builder.append(options);
        } else {
            builder.append("COMMENT")
                    .append(_Constant.SPACE_EQUAL_SPACE);
            this.parser.safeLiteral(StringType.INSTANCE, table.comment(), false, builder);

        }
    }

    @Override
    void tablePartitionOptions(final TableMeta<?> table, final StringBuilder builder) {
        final String options = table.partitionOptions();
        if (_StringUtils.hasText(options)) {
            // optimize me
            builder.append('\n')
                    .append(options);
        }
    }

    @Override
    protected void dataType(final FieldMeta<?> field, final DataType dataType, final StringBuilder builder) {
        builder.append(_Constant.SPACE);

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
    protected <T> void indexDefinition(final IndexMeta<T> index, final StringBuilder builder) {

        final boolean primaryKey = index.isPrimaryKey();

        if (primaryKey) {
            builder.append("PRIMARY")
                    .append(_Constant.SPACE);
        } else if (index.isUnique()) {
            builder.append("UNIQUE")
                    .append(_Constant.SPACE);
        }
        builder.append("KEY");
        if (!primaryKey) {
            indexName(index, builder);
        }

        indexFieldList(index, builder);
    }

    @Override
    protected void postDataType(FieldMeta<?> field, DataType dataType, StringBuilder builder) {
        dataType(field, dataType, builder);
    }

    @Override
    protected void columnDefinition(final FieldMeta<?> field, final StringBuilder builder) {
        columnName(field, builder);
        dataType(field, field.mappingType().map(this.parser.serverMeta), builder);
        columnNullConstraint(field, builder);
        columnDefault(field, builder);

        if (field instanceof PrimaryFieldMeta<?> && field.generatorType() == GeneratorType.POST) {
            autoIncrement(builder);
        }
        columnComment(field, builder);

    }

    @Override
    protected void appendPostGenerator(FieldMeta<?> field, StringBuilder builder) {
        builder.append(" AUTO_INCREMENT");
    }

    @Override
    protected void appendTableOption(final TableMeta<?> table, final StringBuilder builder) {
        final String tableOption, partitionOption;
        tableOption = table.tableOptions();
        if (_StringUtils.hasText(tableOption)) {
            // TODO validate
            builder.append(tableOption);
        } else {
            builder.append(" ENGINE = InnoDB CHARACTER SET  = 'utf8mb4'");
        }
        appendColumnComment(table, builder);

        partitionOption = table.partitionOptions();
        if (_StringUtils.hasText(partitionOption)) {
            // TODO validate
            builder.append('\n')
                    .append(partitionOption);
        }

    }


    @Override
    protected void doModifyColumn(final FieldResult result, final StringBuilder builder) {
        appendSpaceIfNeed(builder);

        final FieldMeta<?> field;
        field = result.field();
        final String defaultValue;
        if (result.containSqlType() || result.containNotNull() || result.containComment()) {
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
