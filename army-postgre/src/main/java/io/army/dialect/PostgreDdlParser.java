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
import io.army.mapping.TextType;
import io.army.meta.*;
import io.army.schema._FieldResult;
import io.army.sqltype.DataType;
import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.List;

final class PostgreDdlParser extends _DdlParser<PostgreParser> {

    static PostgreDdlParser create(PostgreParser dialect) {
        return new PostgreDdlParser(dialect);
    }

    private PostgreDdlParser(PostgreParser dialect) {
        super(dialect);
    }


    @Override
    public List<String> userDefinedTypeStmts() {
        return super.userDefinedTypeStmts();
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-createtable.html">CREATE TABLE— define a new table</a>
     */
    @Override
    public <T> void createTable(final TableMeta<T> table, final List<String> sqlList) {
        final StringBuilder builder;
        builder = createTableCommandBuilder(table);

        // column definitions
        columnDefinitionList(table, builder);

        // table constraint
        for (IndexMeta<T> index : table.indexList()) {
            if (!index.isUnique()) {
                continue;
            }
            builder.append(_Constant.SPACE)
                    .append("\n\t");
            if (index.isPrimaryKey()) {
                builder.append("PRIMARY")
                        .append(_Constant.SPACE)
                        .append("KEY");
            } else {
                builder.append("UNIQUE");
            }
            justIndexFiledNameList(index, builder);
        }


        // right paren
        builder.append('\n')
                .append(_Constant.RIGHT_PAREN);


        sqlList.add(builder.toString());


        // outer of table index definition
        for (IndexMeta<T> index : table.indexList()) {
            if (index.isUnique()) {
                continue;
            }
            builder.setLength(0); // clear
            appendIndexOutTableDef(index, builder);
            sqlList.add(builder.toString());
        }

        // table comment
        builder.setLength(0); // clear
        tableOuterComment(table, builder);
        sqlList.add(builder.toString());

        // column comment
        for (FieldMeta<T> field : table.fieldList()) {
            builder.setLength(0); // clear
            columnOuterComment(field, builder);
            sqlList.add(builder.toString());
        }

    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-droptable.html">DROP TABLE — remove a table</a>
     */
    @Override
    public void dropTable(final List<TableMeta<?>> tableList, final List<String> sqlList) {
        final StringBuilder builder = new StringBuilder();
        dropTableIfExists(builder);
        tableNameList(tableList, builder);
        sqlList.add(builder.toString());
    }

    @Override
    public void modifyTableComment(TableMeta<?> table, List<String> sqlList) {
        final StringBuilder builder = new StringBuilder();
        tableOuterComment(table, builder);
        sqlList.add(builder.toString());
    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-altertable.html">ALTER TABLE — change the definition of a table</a>
     */
    @Override
    public void addColumn(final List<FieldMeta<?>> fieldList, final List<String> sqlList) {
        final StringBuilder builder = new StringBuilder();
        alterTable(true, builder);

        final int listSize = fieldList.size();
        FieldMeta<?> field;
        TableMeta<?> table = null;
        for (int i = 0; i < listSize; i++) {
            field = fieldList.get(i);

            if (i == 0) {
                table = field.tableMeta();
                tableName(table, builder);
            } else if (field.tableMeta() == table) {
                builder.append(_Constant.COMMA);
            } else {
                // no bug,never here
                throw new IllegalArgumentException();
            }
            builder.append("\n\t")
                    .append("ADD")
                    .append(_Constant.SPACE)
                    .append("COLUMN")
                    .append(_Constant.SPACE)
                    .append("IF")
                    .append(_Constant.SPACE_NOT)
                    .append(_Constant.SPACE_EXISTS)
                    .append(_Constant.SPACE);

            columnDefinition(field, builder);

        }
        sqlList.add(builder.toString());
    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-altertable.html">ALTER TABLE — change the definition of a table</a>
     */
    @Override
    public void modifyColumn(final List<_FieldResult> resultList, final List<String> sqlList) {
        final StringBuilder builder = new StringBuilder();
        alterTable(true, builder);

        final int listSize = resultList.size();
        _FieldResult result;
        FieldMeta<?> field;
        TableMeta<?> table = null;
        String safeColumnName;
        for (int i = 0, effectiveCount, totalEffectiveCount = 0, offset, end; i < listSize; i++) {
            result = resultList.get(i);
            field = result.field();
            if (i == 0) {
                table = field.tableMeta();
                tableName(table, builder);
            } else if (field.tableMeta() != table) {
                // no bug,never here
                throw new IllegalArgumentException();
            } else if (totalEffectiveCount > 0) {
                builder.append(_Constant.COMMA);
            }

            safeColumnName = this.parser.safeObjectName(field);

            offset = builder.length();

            builder.append("\n\t")
                    .append("ALTER")
                    .append(_Constant.SPACE)
                    .append("COLUMN")
                    .append(_Constant.SPACE)
                    .append(safeColumnName)
                    .append(_Constant.SPACE);

            end = builder.length();

            effectiveCount = 0; // reset

            if (result.containSqlType()) {
                builder.append("TYPE");
                fieldDataType(field, builder);
                effectiveCount++;
            }

            if (result.containNullable()) {
                if (effectiveCount > 0) {
                    builder.append(_Constant.COMMA)
                            .append(builder, offset, end);
                }
                if (field.nullable()) {
                    builder.append("DROP");
                } else {
                    builder.append("SET");
                }
                builder.append(_Constant.SPACE_NOT_NULL);
                effectiveCount++;
            }

            if (result.containDefault()) {
                if (effectiveCount > 0) {
                    builder.append(_Constant.COMMA)
                            .append(builder, offset, end);
                }
                if (_StringUtils.hasText(field.defaultValue())) {
                    builder.append("SET");
                    columnDefault(field, builder);
                } else {
                    builder.append("DROP")
                            .append(_Constant.SPACE_DEFAULT);

                }
                effectiveCount++;
            }

            if (effectiveCount == 0) {
                builder.setLength(offset);
            } else {
                totalEffectiveCount += effectiveCount;
            }


        } // result loop

        sqlList.add(builder.toString());


        // column comment
        for (int i = 0; i < listSize; i++) {
            result = resultList.get(i);
            if (result.containComment()) {
                field = result.field();
                builder.setLength(0);
                columnOuterComment(field, builder);
                sqlList.add(builder.toString());
            }
        }


    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-createindex.html">CREATE INDEX — define a new index</a>
     */
    @Override
    public <T> void createIndex(final TableMeta<T> table, final List<String> indexNameList, final List<String> sqlList) {
        final StringBuilder builder = new StringBuilder();

        final int nameListSize = indexNameList.size();

        String name;
        for (IndexMeta<T> index : table.indexList()) {
            name = index.name();

            for (int nameIndex = 0; nameIndex < nameListSize; nameIndex++) {
                if (name.equalsIgnoreCase(indexNameList.get(nameIndex))) {
                    builder.setLength(0);
                    appendIndexOutTableDef(index, builder);
                    sqlList.add(builder.toString());
                    break;
                }

            } // inner loop

        } // index loop

    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-dropindex.html">DROP INDEX — remove an index</a>
     */
    @Override
    public <T> void dropIndex(final TableMeta<T> table, final List<String> indexNameList, final List<String> sqlList) {
        final StringBuilder builder = new StringBuilder();
        builder.append("DROP")
                .append(_Constant.SPACE)
                .append("INDEX")
                .append(_Constant.SPACE)
                .append("IF")
                .append(_Constant.SPACE_EXISTS);

        final int nameListSize = indexNameList.size();
        for (int i = 0; i < nameListSize; i++) {
            if (i > 0) {
                builder.append(_Constant.COMMA);
            }
            builder.append("\n\t");
            this.parser.identifier(indexNameList.get(i), builder);

        } //  loop


        sqlList.add(builder.toString());

    }

    @Override
    protected void checkEnclosing(String text) {
        super.checkEnclosing(text);
    }


    @Override
    protected void doModifyColumn(final _FieldResult result, final StringBuilder builder) {
        appendSpaceIfNeed(builder);

        final FieldMeta<?> field;
        field = result.field();
        final String safeColumnName, defaultValue;
        safeColumnName = this.parser.safeObjectName(field);

        int count = 0;
        if (result.containSqlType()) {
            builder.append("\n\t")
                    .append(ALTER_COLUMN_SPACE)
                    .append(safeColumnName)
                    .append(" SET DATA TYPE ");

            final DataType dataType;
            dataType = field.mappingType().map(this.serverMeta);
            if (field.generatorType() == GeneratorType.POST) {
                postDataType(field, dataType, builder);
            } else {
                dataType(field, dataType, builder);
            }

            count++;
        }

        if (result.containNullable()) {
            if (count > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append("\n\t")
                    .append(ALTER_COLUMN_SPACE)
                    .append(safeColumnName);
            if (field.nullable()) {
                builder.append(" DROP");
            } else {
                builder.append(_Constant.SPACE_SET);
            }
            builder.append(_Constant.SPACE_NOT_NULL);

            count++;
        }

        if (result.containDefault()) {
            if (count > 0) {
                builder.append(_Constant.SPACE_COMMA);
            }
            builder.append("\n\t")
                    .append(ALTER_COLUMN_SPACE)
                    .append(safeColumnName);
            if (!_StringUtils.hasText((defaultValue = field.defaultValue()))) {
                builder.append(SPACE_DROP_DEFAULT);
            } else if (checkDefaultComplete(field, defaultValue)) {
                builder.append(SPACE_SET_DEFAULT_SPACE)
                        .append(defaultValue);
            }
        }

    }


    @Override
    protected void dataType(final FieldMeta<?> field, final DataType dataType, final StringBuilder builder) {
        builder.append(_Constant.SPACE);

        if (!(dataType instanceof PostgreType)) {
            this.parser.typeName(field.mappingType(), builder);
        } else switch ((PostgreType) dataType) {
            case UNKNOWN:
                throw _Exceptions.unexpectedEnum((Enum<?>) dataType);
            case DECIMAL:
            case DECIMAL_ARRAY:
                this.appendDecimalDateType(field, dataType, builder);
                break;
            case TIME:
            case TIMETZ:
            case TIMESTAMP:
            case TIMESTAMPTZ:
            case TIME_ARRAY:
            case TIMETZ_ARRAY:
            case TIMESTAMP_ARRAY:
            case TIMESTAMPTZ_ARRAY:
                this.appendTimeDateType(field, dataType, builder);
                break;
            default:
                this.parser.typeName(field.mappingType(), builder);


        }// switch

    }


    @Override
    protected void postDataType(FieldMeta<?> field, DataType dataType, StringBuilder builder) {
        builder.append(_Constant.SPACE);

        switch ((PostgreType) dataType) {
            case SMALLINT:
                builder.append("SMALLSERIAL");
                break;
            case INTEGER:
                builder.append("SERIAL");
                break;
            case BIGINT:
                builder.append("BIGSERIAL");
                break;
            default:
                this.errorMsgList.add(String.format("%s %s don't support %s", field, dataType, GeneratorType.POST));
        }

    }

    @Override
    protected void appendColumnComment(final DatabaseObject object, final StringBuilder builder) {
        builder.append(SPACE_COMMENT)
                .append(_Constant.SPACE_ON);

        if (object instanceof TableMeta) {
            builder.append(" TABLE ");
            this.parser.safeObjectName(object, builder);
        } else if (object instanceof FieldMeta) {
            builder.append(" COLUMN ");
            this.parser.safeObjectName(((FieldMeta<?>) object).tableMeta(), builder);
            builder.append(_Constant.PERIOD);
            this.parser.safeObjectName(object, builder);
        } else {
            //no bug,never here
            throw new IllegalArgumentException();
        }

        builder.append(" IS ");

        this.parser.literal(TextType.INSTANCE, object.comment(), false, builder);

    }

    @Override
    protected void appendPostGenerator(FieldMeta<?> field, StringBuilder builder) {
        //no-op,see this.dataType() method
    }

    @Override
    protected void appendTableOption(TableMeta<?> table, StringBuilder builder) {
        //no-op
    }


    @Override
    protected <T> void appendIndexOutTableDef(final IndexMeta<T> index, final StringBuilder builder) {
        assert builder.length() == 0;

        if (index.isPrimaryKey()) {
            builder.append("ALTER")
                    .append(_Constant.SPACE)
                    .append("TABLE")
                    .append(_Constant.SPACE)
                    .append("IF")
                    .append(_Constant.SPACE_EXISTS)
                    .append(_Constant.SPACE);

            this.parser.safeObjectName(index.tableMeta(), builder);

            builder.append(_Constant.SPACE)
                    .append("ADD")
                    .append(_Constant.SPACE)
                    .append("PRIMARY")
                    .append(_Constant.SPACE)
                    .append("KEY");
        } else {
            builder.append("CREATE");
            if (index.isUnique()) {
                builder.append(_Constant.SPACE)
                        .append("UNIQUE");
            }
            builder.append(_Constant.SPACE)
                    .append("INDEX")
                    .append(_Constant.SPACE);

            this.parser.identifier(index.name(), builder);
            builder.append(_Constant.SPACE_ON_SPACE);
            this.parser.safeObjectName(index.tableMeta(), builder);

            appendIndexType(index, builder);
        }

        appendIndexFieldList(index, builder);

    }


    /**
     * @see #dataType(FieldMeta, DataType, StringBuilder)
     */
    private void appendTimeDateType(final FieldMeta<?> field, final DataType dataType, final StringBuilder builder) {
        String safeTypeName;
        safeTypeName = dataType.name();
        final int index;
        if ((index = safeTypeName.lastIndexOf("_ARRAY")) > 0) {
            safeTypeName = safeTypeName.substring(0, index);
        }
        builder.append(safeTypeName);

        appendTimeTypeScale(field, builder);

        if (index > 0) {
            this.parser.arrayTypeName(safeTypeName, ArrayUtils.dimensionOfType(field.mappingType()), builder);
        }
    }

    /**
     * @see #dataType(FieldMeta, DataType, StringBuilder)
     */
    private void appendDecimalDateType(final FieldMeta<?> field, final DataType dataType, final StringBuilder builder) {
        int precision, scale;
        precision = field.precision();
        scale = field.scale();
        if (precision < scale) {
            this.errorMsgList.add(String.format("%s precision[%s] scale[%s]", field, precision, scale));
            return;
        }
        if (precision == -1) {
            precision = 14;
        }
        if (scale == -1) {
            scale = 2;
        }
        String safeTypeName;
        safeTypeName = dataType.name();
        final int index;
        if ((index = safeTypeName.lastIndexOf(_Constant.UNDERSCORE_ARRAY)) > 0) {
            safeTypeName = safeTypeName.substring(0, index);
        }

        builder.append(safeTypeName)
                .append(_Constant.LEFT_PAREN)
                .append(precision)
                .append(_Constant.COMMA)
                .append(scale)
                .append(_Constant.RIGHT_PAREN);

        if (index > 0) {
            this.parser.arrayTypeName(safeTypeName, ArrayUtils.dimensionOfType(field.mappingType()), builder);
        }

    }


    /**
     * @see #createTable(TableMeta, List)
     * @see #addColumn(List, List)
     * @see <a href="https://www.postgresql.org/docs/current/sql-createtable.html">CREATE TABLE— define a new table</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-altertable.html">ALTER TABLE — change the definition of a table</a>
     */
    @Override
    protected void columnDefinition(final FieldMeta<?> field, final StringBuilder builder) {
        columnName(field, builder);
        fieldDataType(field, builder);
        columnNullConstraint(field, builder);
        columnDefault(field, builder);
    }


    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-comment.html">COMMENT — define or change the comment of an object</a>
     */
    @Override
    void tableOuterComment(final TableMeta<?> table, final StringBuilder builder) {
        builder.append("COMMENT")
                .append(_Constant.SPACE_ON_SPACE)
                .append("TABLE")
                .append(_Constant.SPACE);

        this.parser.safeObjectName(table, builder);

        builder.append(_Constant.SPACE)
                .append("IS")
                .append(_Constant.SPACE);

        this.parser.literal(StringType.INSTANCE, table.comment(), false, builder);

    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-comment.html">COMMENT — define or change the comment of an object</a>
     */
    @Override
    void columnOuterComment(final FieldMeta<?> field, final StringBuilder builder) {
        builder.append("COMMENT")
                .append(_Constant.SPACE_ON_SPACE)
                .append("COLUMN")
                .append(_Constant.SPACE);

        this.parser.safeObjectName(field.tableMeta(), builder);
        builder.append(_Constant.PERIOD);
        this.parser.safeObjectName(field, builder);

        builder.append(_Constant.SPACE)
                .append("IS")
                .append(_Constant.SPACE);

        this.parser.literal(StringType.INSTANCE, field.comment(), false, builder);
    }


    /*-------------------below private methods-------------------*/


    /**
     * @see #columnDefinition(FieldMeta, StringBuilder)
     * @see #modifyColumn(List, List)
     */
    private void fieldDataType(final FieldMeta<?> field, final StringBuilder builder) {
        final DataType dataType;
        dataType = field.mappingType().map(this.parser.serverMeta);
        if (field instanceof PrimaryFieldMeta && field.generatorType() == GeneratorType.POST) {
            postDataType(field, dataType, builder);
        } else {
            dataType(field, dataType, builder);
        }
    }


}
