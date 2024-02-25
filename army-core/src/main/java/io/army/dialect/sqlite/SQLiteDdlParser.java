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

package io.army.dialect.sqlite;

import io.army.dialect._DdlParser;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.schema._FieldResult;
import io.army.sqltype.DataType;
import io.army.sqltype.SQLiteType;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.List;

/**
 * @see <a href="https://www.sqlite.org/lang_createtable.html">CREATE TABLE</a>
 * @since 0.6.7
 */
final class SQLiteDdlParser extends _DdlParser<SQLiteParser> {

    static SQLiteDdlParser create(SQLiteParser parser) {
        return new SQLiteDdlParser(parser);
    }

    private SQLiteDdlParser(SQLiteParser parser) {
        super(parser);
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_createtable.html">CREATE TABLE</a>
     */
    @Override
    public void modifyTableComment(TableMeta<?> table, List<String> sqlList) {

    }

    /**
     * @see <a href="https://www.sqlite.org/lang_createtable.html">CREATE TABLE</a>
     */
    @Override
    protected void dataType(final FieldMeta<?> field, final DataType dataType, final StringBuilder builder) {

        switch ((SQLiteType) dataType) {
            case BOOLEAN:

            case TINYINT:
            case SMALLINT:
            case MEDIUMINT:
            case INTEGER:
            case BIGINT:

            case DOUBLE:
            case BIT:

            case VARCHAR:
            case TEXT:
            case JSON:

            case VARBINARY:
            case BLOB:

            case DATE:
            case YEAR:

            case MONTH_DAY:
            case YEAR_MONTH:
            case PERIOD:
            case DURATION:
                builder.append(dataType.typeName());
                break;
            case DECIMAL: {
                builder.append(dataType.typeName());
                decimalType(field, builder);
            }
            break;
            case TIME:
            case TIME_WITH_TIMEZONE:
            case TIMESTAMP:
            case TIMESTAMP_WITH_TIMEZONE: {
                builder.append(dataType.typeName());
                timeTypeScale(field, dataType, builder);
            }
            break;
            case DYNAMIC:
                // no-op
                break;
            case NULL:
            case UNKNOWN:
            default:
                throw _Exceptions.unexpectedEnum((SQLiteType) dataType);
        }
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_createtable.html">CREATE TABLE</a>
     */
    @Override
    protected void postDataType(FieldMeta<?> field, DataType dataType, StringBuilder builder) {
        dataType(field, dataType, builder);
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_createtable.html">CREATE TABLE</a>
     */
    @Override
    protected void appendTableOption(TableMeta<?> table, StringBuilder builder) {
        final String optionClause;
        optionClause = table.tableOption();
        if (_StringUtils.hasText(optionClause)) {

        }
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_createtable.html">CREATE TABLE</a>
     */
    @Override
    protected void appendPostGenerator(FieldMeta<?> field, StringBuilder builder) {
        builder.append(" AUTOINCREMENT");
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_createtable.html">CREATE TABLE</a>
     */
    @Override
    protected void doModifyColumn(_FieldResult result, StringBuilder builder) {

    }


}
