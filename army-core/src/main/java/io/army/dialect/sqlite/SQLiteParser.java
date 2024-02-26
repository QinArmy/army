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

import io.army.criteria.CriteriaException;
import io.army.criteria.SQLWords;
import io.army.criteria.Visible;
import io.army.criteria.impl._UnionType;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.*;
import io.army.env.EscapeMode;
import io.army.mapping.MappingType;
import io.army.meta.DatabaseObject;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.session.executor.ExecutorSupport;
import io.army.sqltype.DataType;
import io.army.sqltype.SQLiteType;
import io.army.util.HexUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;
import io.army.util._TimeUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Set;

abstract class SQLiteParser extends _ArmyDialectParser {

    static SQLiteParser standard(DialectEnv dialectEnv, SQLiteDialect dialect) {
        return new Standard(dialectEnv, dialect);
    }


    SQLiteParser(DialectEnv dialectEnv, SQLiteDialect dialect) {
        super(dialectEnv, dialect);
        switch (this.literalEscapeMode) {
            case DEFAULT:
            case DEFAULT_NO_TYPE:
                break;
            default:
                throw _Exceptions.literalEscapeModeError(this.literalEscapeMode, this.dialect);

        }

        switch (this.identifierEscapeMode) {
            case DEFAULT:
            case DEFAULT_NO_TYPE:
                break;
            default:
                throw _Exceptions.identifierEscapeModeError(this.identifierEscapeMode, this.dialect);

        }
    }

    @Override
    public final void typeName(MappingType type, StringBuilder sqlBuilder) {
        final DataType dataType;
        dataType = type.map(this.serverMeta);
        if (!(dataType instanceof SQLiteType)) {
            unrecognizedTypeName(type, dataType, false, sqlBuilder);
        } else switch ((SQLiteType) dataType) {
            case UNKNOWN:
            case NULL:
                throw ExecutorSupport.mapMethodError(type, dataType);
            default:
                sqlBuilder.append(dataType.typeName());
        }
    }


    @Override
    protected final Set<String> createKeyWordSet() {
        return SQLiteDialectUtils.createKeyWordSet();
    }

    /**
     * @see <a href="https://sqlite.org/lang_naming.html">Database Object Name Resolution</a>
     * @see <a href="https://www.sqlite.org/lang_keywords.html">SQLite Keywords</a>
     */
    @Override
    protected final char identifierDelimitedQuote() {
        return _Constant.DOUBLE_QUOTE;
    }

    @Override
    protected final String defaultFuncName() {
        // SQLite don't support DEFAULT() function
        throw new UnsupportedOperationException();
    }

    @Override
    protected final boolean isSupportZone() {
        //true  support
        return true;
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_update.html">update statement</a>
     * @see <a href="https://www.sqlite.org/lang_insert.html">upsert clause</a>
     */
    @Override
    protected final boolean isSetClauseTableAlias() {
        // false , SQLite don't support
        return false;
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_select.html">SELECT statement</a>
     */
    @Override
    protected final boolean isTableAliasAfterAs() {
        // true , SQLite support
        return true;
    }

    @Override
    protected final boolean isSupportOnlyDefault() {
        // false , SQLite don't support default() function
        return false;
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_insert.html">upsert clause</a>
     */
    @Override
    protected final boolean isSupportRowAlias() {
        // false , SQLite don't support
        return false;
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_select.html">SELECT statement</a>
     */
    @Override
    protected final boolean isSupportTableOnly() {
        // false , SQLite don't support
        return false;
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_update.html">UPDATE statement</a>
     * @see <a href="https://www.sqlite.org/lang_delete.html">DELETE statement</a>
     */
    @Override
    protected final ChildUpdateMode childUpdateMode() {
        return ChildUpdateMode.WITH_ID;
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_update.html">UPDATE statement</a>
     */
    @Override
    protected final boolean isSupportSingleUpdateAlias() {
        // true , SQLite support
        return true;
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_delete.html">DELETE statement</a>
     */
    @Override
    protected final boolean isSupportSingleDeleteAlias() {
        // true , SQLite support
        return true;
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_insert.html">INSERT clause</a>
     * @see <a href="https://www.sqlite.org/lang_select.html">SELECT statement</a>
     * @see <a href="https://www.sqlite.org/lang_update.html">UPDATE statement</a>
     * @see <a href="https://www.sqlite.org/lang_delete.html">DELETE statement</a>
     */
    @Override
    protected final boolean isSupportWithClause() {
        // true , SQLite support WITH clause
        return true;
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_insert.html">INSERT clause</a>
     */
    @Override
    protected final boolean isSupportWithClauseInInsert() {
        // true , SQLite support WITH clause
        return true;
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_select.html">SELECT statement</a>
     */
    @Override
    protected final boolean isSupportWindowClause() {
        // true , SQLite support WINDOW clause
        return true;
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_update.html">UPDATE statement</a>
     */
    @Override
    protected final boolean isSupportUpdateRow() {
        // true , SQLite support
        return true;
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_update.html">UPDATE statement</a>
     */
    @Override
    protected final boolean isSupportUpdateDerivedField() {
        // false , SQLite don't support
        return false;
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_insert.html">INSERT clause</a>
     * @see <a href="https://www.sqlite.org/lang_update.html">UPDATE statement</a>
     * @see <a href="https://www.sqlite.org/lang_delete.html">DELETE statement</a>
     * @see <a href="https://www.sqlite.org/lang_insert.html">RETURNING clause</a>
     */
    @Override
    protected final boolean isSupportReturningClause() {
        // true , SQLite support
        return true;
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_select.html">SELECT statement</a>
     */
    @Override
    protected final boolean isValidateUnionType() {
        // false , SQLite don't need
        return false;
    }

    /**
     * @see #isValidateUnionType()
     */
    @Override
    protected final void validateUnionType(_UnionType unionType) {
        // no bug,never here
        throw new UnsupportedOperationException();
    }

    /**
     * @see <a href="https://sqlite.org/lang_naming.html">Database Object Name Resolution</a>
     */
    @Override
    protected final String qualifiedSchemaName(final ServerMeta meta) {
        final String catalog, schema, name;
        catalog = meta.catalog();
        schema = meta.schema();

        final boolean catalogHasText, schemaHasText;
        catalogHasText = _StringUtils.hasText(catalog);
        schemaHasText = _StringUtils.hasText(schema);

        if (catalogHasText && schemaHasText) {
            name = _StringUtils.builder(catalog.length() + 1 + schema.length())
                    .append(catalog)
                    .append(_Constant.PERIOD)
                    .append(schema)
                    .toString();
        } else if (catalogHasText) {
            name = catalog;
        } else if (schemaHasText) {
            name = schema;
        } else {
            name = "main";
        }
        return name;
    }

    @Override
    protected final boolean isUseObjectNameModeMethod() {
        // don't use object name mode
        return false;
    }

    /**
     * @see <a href="https://sqlite.org/lang_naming.html">Database Object Name Resolution</a>
     * @see <a href="https://www.sqlite.org/lang_keywords.html">SQLite Keywords</a>
     */
    @Override
    protected final IdentifierMode identifierMode(final String identifier) {
        final int length = identifier.length();
        if (length == 0) {
            return IdentifierMode.ERROR;

        }

        IdentifierMode mode = null;
        char ch;
        for (int i = 0; i < length; i++) {
            ch = identifier.charAt(i);
            if (ch == _Constant.DOUBLE_QUOTE) {
                mode = IdentifierMode.ESCAPES;
                break;
            } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_') {
                continue;
            } else if (ch >= '0' && ch <= '9') {
                if (i == 0) {
                    mode = IdentifierMode.QUOTING;
                }
                continue;
            }

            if (mode == null) {
                mode = IdentifierMode.QUOTING;
            }

        } // loop for

        if (mode == null) {
            mode = IdentifierMode.SIMPLE;
        }
        return mode;
    }


    /**
     * @see #isUseObjectNameModeMethod()
     * @see <a href="https://sqlite.org/lang_naming.html">Database Object Name Resolution</a>
     * @see <a href="https://www.sqlite.org/lang_keywords.html">SQLite Keywords</a>
     */
    @Override
    protected final IdentifierMode objectNameMode(final DatabaseObject object, final String effectiveName) {
        // no bug never here
        throw new UnsupportedOperationException();
    }

    /**
     * @see <a href="https://sqlite.org/lang_naming.html">Database Object Name Resolution</a>
     * @see <a href="https://www.sqlite.org/lang_keywords.html">SQLite Keywords</a>
     */
    @Override
    protected final void escapesIdentifier(final String identifier, final StringBuilder sqlBuilder) {
        final int length = identifier.length();

        sqlBuilder.append(_Constant.DOUBLE_QUOTE);

        int lastWritten = 0;
        for (int i = 0; i < length; i++) {
            if (identifier.charAt(i) == _Constant.DOUBLE_QUOTE) {
                if (i > lastWritten) {
                    sqlBuilder.append(identifier, lastWritten, i);
                }
                sqlBuilder.append(_Constant.DOUBLE_QUOTE);
                lastWritten = i; // not i + 1 as current char wasn't written
            }

        } // loop for

        if (lastWritten < length) {
            sqlBuilder.append(identifier, lastWritten, length);
        }
        sqlBuilder.append(_Constant.DOUBLE_QUOTE);

    }


    /**
     * @see <a href="https://sqlite.org/lang_expr.html">Literal Values (Constants)</a>
     */
    @Override
    protected final void bindLiteralNull(MappingType type, DataType dataType, EscapeMode mode, boolean typeName, StringBuilder sqlBuilder) {
        switch (mode) {
            case DEFAULT:
            case DEFAULT_NO_TYPE:
                break;
            default:
                throw _Exceptions.literalEscapeModeError(mode, this.dialect);

        }
        sqlBuilder.append(_Constant.NULL);
    }

    /**
     * @see <a href="https://sqlite.org/lang_expr.html">Literal Values (Constants)</a>
     */
    @Override
    protected final boolean bindLiteral(final TypeMeta typeMeta, final DataType dataType, final Object value,
                                        final EscapeMode mode, boolean typeName, final StringBuilder sqlBuilder) {
        if (!(dataType instanceof SQLiteType)) {
            throw _Exceptions.unrecognizedTypeLiteral(this.dialectDatabase, dataType);
        }
        switch (mode) {
            case DEFAULT:
            case DEFAULT_NO_TYPE:
                break;
            default:
                throw _Exceptions.literalEscapeModeError(mode, this.dialect);

        }

        boolean encloseOrEscape = false;

        switch ((SQLiteType) dataType) {
            case BOOLEAN:
                _Literals.bindBoolean(typeMeta, dataType, value, sqlBuilder);
                break;
            case TINYINT: {
                if (!(value instanceof Byte)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
            }
            break;
            case SMALLINT: {
                if (!(value instanceof Short)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
            }
            break;
            case MEDIUMINT:
            case INTEGER: {
                if (!(value instanceof Integer)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
            }
            break;
            case BIGINT: {
                if (!(value instanceof Long)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
            }
            break;
            case FLOAT: {
                if (!(value instanceof Float)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
            }
            break;
            case DOUBLE: {
                if (!(value instanceof Double || value instanceof Float)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
            }
            break;
            case DECIMAL:
                _Literals.bindBigDecimal(typeMeta, dataType, value, sqlBuilder);
                break;
            case BIT: {
                if (!(value instanceof Long || value instanceof Integer)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(value);
            }
            break;
            case VARCHAR:
            case TEXT:
            case JSON: {
                if (!(value instanceof String)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                escapeText((String) value, 0, ((String) value).length(), sqlBuilder);
                encloseOrEscape = true;
            }
            break;
            case VARBINARY:
            case BLOB: {
                if (!(value instanceof byte[])) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append('x')
                        .append(_Constant.QUOTE)
                        .append(HexUtils.hexEscapesText(true, (byte[]) value))
                        .append(_Constant.QUOTE);

                encloseOrEscape = true;
            }
            break;
            case TIMESTAMP: {
                if (!(value instanceof LocalDateTime)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(_TimeUtils.DATETIME_FORMATTER_6.format((LocalDateTime) value))
                        .append(_Constant.QUOTE);
                encloseOrEscape = true;
            }
            break;
            case TIMESTAMP_WITH_TIMEZONE: {
                if (!(value instanceof OffsetDateTime || value instanceof ZonedDateTime)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(_TimeUtils.OFFSET_DATETIME_FORMATTER_6.format((TemporalAccessor) value))
                        .append(_Constant.QUOTE);
                encloseOrEscape = true;
            }
            break;
            case DATE: {
                if (!(value instanceof LocalDate)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(DateTimeFormatter.ISO_LOCAL_DATE.format((TemporalAccessor) value))
                        .append(_Constant.QUOTE);
                encloseOrEscape = true;
            }
            break;
            case TIME: {
                if (!(value instanceof LocalTime)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(_TimeUtils.TIME_FORMATTER_6.format((TemporalAccessor) value))
                        .append(_Constant.QUOTE);
                encloseOrEscape = true;
            }
            break;
            case TIME_WITH_TIMEZONE: {
                if (!(value instanceof OffsetTime)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(_TimeUtils.OFFSET_TIME_FORMATTER_6.format((TemporalAccessor) value))
                        .append(_Constant.QUOTE);
                encloseOrEscape = true;
            }
            break;
            case YEAR: {
                if (value instanceof Short) {
                    sqlBuilder.append(value);
                } else if (value instanceof Year) {
                    sqlBuilder.append(((Year) value).getValue());
                } else {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
            }
            break;
            case MONTH_DAY: {
                if (!(value instanceof MonthDay)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(value)
                        .append(_Constant.QUOTE);
                encloseOrEscape = true;
            }
            break;
            case YEAR_MONTH: {
                if (!(value instanceof YearMonth)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(value)
                        .append(_Constant.QUOTE);
                encloseOrEscape = true;
            }
            break;
            case PERIOD: {
                if (!(value instanceof Period)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(value)
                        .append(_Constant.QUOTE);
                encloseOrEscape = true;
            }
            break;
            case DURATION: {
                if (!(value instanceof Duration)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(value)
                        .append(_Constant.QUOTE);
                encloseOrEscape = true;
            }
            break;
            case DYNAMIC: {
                if (value instanceof BigDecimal) {
                    _Literals.bindBigDecimal(typeMeta, dataType, value, sqlBuilder);
                } else if (value instanceof Number) {
                    if (value instanceof Integer
                            || value instanceof Long
                            || value instanceof Double
                            || value instanceof Float
                            || value instanceof Short
                            || value instanceof Byte
                            || value instanceof BigInteger) {
                        sqlBuilder.append(value);
                    } else {
                        final String v = value.toString();
                        escapeText(v, 0, v.length(), sqlBuilder);
                        encloseOrEscape = true;
                    }
                } else if (value instanceof String) {
                    escapeText((String) value, 0, ((String) value).length(), sqlBuilder);
                    encloseOrEscape = true;
                } else if (value instanceof byte[]) {
                    sqlBuilder.append('x')
                            .append(_Constant.QUOTE)
                            .append(HexUtils.hexEscapesText(true, (byte[]) value))
                            .append(_Constant.QUOTE);
                    encloseOrEscape = true;
                } else {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
            }
            break;
            case NULL:
            case UNKNOWN:
                throw ExecutorSupport.mapMethodError(typeMeta.mappingType(), dataType);
            default:
                throw _Exceptions.unexpectedEnum((SQLiteType) dataType);
        }

        return encloseOrEscape;
    }

    @Override
    protected final SQLiteDdlParser createDdlDialect() {
        return SQLiteDdlParser.create(this);
    }


    @Nullable
    @Override
    protected final CriteriaException supportChildInsert(_Insert._ChildInsert childStmt, Visible visible) {
        return null;
    }

    /**
     * @see <a href="https://www.sqlite.org/lang_select.html">SELECT statement</a>
     */
    @Override
    protected final void standardLimitClause(final @Nullable _Expression offset, final @Nullable _Expression rowCount,
                                             _SqlContext context) {

        final StringBuilder sqlBuilder;
        if (offset != null && rowCount != null) {
            sqlBuilder = context.sqlBuilder().append(_Constant.SPACE_LIMIT);
            offset.appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            rowCount.appendSql(sqlBuilder, context);
        } else if (rowCount != null) {
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_LIMIT);
            rowCount.appendSql(sqlBuilder, context);
        }

    }

    /**
     * @see <a href="https://www.sqlite.org/lang_select.html">SELECT statement</a>
     */
    @Override
    protected final void standardLockClause(final SQLWords lockMode, final _SqlContext context) {
        throw _Exceptions.dontSupportForUpdateClause(this.dialect);
    }

    static void escapeText(final CharSequence value, final int offset, final int end, final StringBuilder builder) {


        builder.append(_Constant.QUOTE);

        int lastWritten = 0;
        for (int i = offset; i < end; i++) {
            if (value.charAt(i) == _Constant.QUOTE) {
                if (i > lastWritten) {
                    builder.append(value, lastWritten, i);
                }
                builder.append(_Constant.QUOTE);
                lastWritten = i; // not i + 1 as current char wasn't written
            }

        }

        if (lastWritten < end) {
            builder.append(value, lastWritten, end);
        }

        builder.append(_Constant.QUOTE);

    }


    private static class Standard extends SQLiteParser {

        private Standard(DialectEnv dialectEnv, SQLiteDialect dialect) {
            super(dialectEnv, dialect);
        }


    } // Standard


}
