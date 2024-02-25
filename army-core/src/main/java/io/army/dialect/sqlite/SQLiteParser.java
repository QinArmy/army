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
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Set;

abstract class SQLiteParser extends _ArmyDialectParser {


    SQLiteParser(DialectEnv dialectEnv, Dialect dialect) {
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

    }


    @Override
    protected final Set<String> createKeyWordSet() {
        return SQLiteDialectUtils.createKeyWordSet();
    }

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
        // support
        return true;
    }

    @Override
    protected final boolean isSetClauseTableAlias() {
        return false;
    }

    @Override
    protected final boolean isTableAliasAfterAs() {
        return false;
    }

    @Override
    protected final boolean isSupportOnlyDefault() {
        return false;
    }

    @Override
    protected final boolean isSupportRowAlias() {
        return false;
    }

    @Override
    protected final boolean isSupportTableOnly() {
        return false;
    }

    @Override
    protected final ChildUpdateMode childUpdateMode() {
        return ChildUpdateMode.WITH_ID;
    }

    @Override
    protected final boolean isSupportSingleUpdateAlias() {
        return false;
    }

    @Override
    protected final boolean isSupportSingleDeleteAlias() {
        return false;
    }

    @Override
    protected final boolean isSupportWithClause() {
        return false;
    }

    @Override
    protected final boolean isSupportWithClauseInInsert() {
        return false;
    }

    @Override
    protected final boolean isSupportWindowClause() {
        return false;
    }

    @Override
    protected final boolean isSupportUpdateRow() {
        return false;
    }

    @Override
    protected final boolean isSupportUpdateDerivedField() {
        return false;
    }

    @Override
    protected final boolean isSupportReturningClause() {
        return false;
    }

    @Override
    protected final boolean isValidateUnionType() {
        return false;
    }

    @Override
    protected final void validateUnionType(_UnionType unionType) {

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
     * @return never {@link IdentifierMode#ESCAPES}
     * @see <a href="https://sqlite.org/lang_naming.html">Database Object Name Resolution</a>
     */
    @Override
    protected final IdentifierMode identifierMode(final String identifier) {
        return scanStandardSimpleIdentifier(identifier);
    }

    /**
     * @see #isUseObjectNameModeMethod()
     * @see <a href="https://sqlite.org/lang_naming.html">Database Object Name Resolution</a>
     */
    @Override
    protected final IdentifierMode objectNameMode(final DatabaseObject object, final String effectiveName) {
        // no bug never here
        throw new UnsupportedOperationException();
    }

    /**
     * @see <a href="https://sqlite.org/lang_naming.html">Database Object Name Resolution</a>
     */
    @Override
    protected final void escapesIdentifier(final String identifier, final StringBuilder sqlBuilder) {
        // no bug never here
        throw new UnsupportedOperationException();
    }


    /**
     * @see <a href="https://sqlite.org/lang_expr.html">Literal Values (Constants)</a>
     */
    @Override
    protected final void bindLiteralNull(MappingType type, DataType dataType, EscapeMode mode, StringBuilder sqlBuilder) {
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
                                        final EscapeMode mode, final StringBuilder sqlBuilder) {
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
            }
            break;
            case TIMESTAMP: {
                if (!(value instanceof LocalDateTime)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(_TimeUtils.DATETIME_FORMATTER_6.format((LocalDateTime) value))
                        .append(_Constant.QUOTE);
            }
            break;
            case TIMESTAMP_WITH_TIMEZONE: {
                if (!(value instanceof OffsetDateTime || value instanceof ZonedDateTime)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(_TimeUtils.OFFSET_DATETIME_FORMATTER_6.format((TemporalAccessor) value))
                        .append(_Constant.QUOTE);
            }
            break;
            case DATE: {
                if (!(value instanceof LocalDate)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(value)
                        .append(_Constant.QUOTE);
            }
            break;
            case TIME: {
                if (!(value instanceof LocalTime)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(_TimeUtils.TIME_FORMATTER_6.format((TemporalAccessor) value))
                        .append(_Constant.QUOTE);
            }
            break;
            case TIME_WITH_TIMEZONE: {
                if (!(value instanceof OffsetTime)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(_TimeUtils.OFFSET_TIME_FORMATTER_6.format((TemporalAccessor) value))
                        .append(_Constant.QUOTE);
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
            }
            break;
            case YEAR_MONTH: {
                if (!(value instanceof YearMonth)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(value)
                        .append(_Constant.QUOTE);
            }
            break;
            case PERIOD: {
                if (!(value instanceof Period)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(value)
                        .append(_Constant.QUOTE);
            }
            break;
            case DURATION: {
                if (!(value instanceof Duration)) {
                    throw ExecutorSupport.beforeBindMethodError(typeMeta.mappingType(), dataType, value);
                }
                sqlBuilder.append(_Constant.QUOTE)
                        .append(value)
                        .append(_Constant.QUOTE);
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
                    }
                } else if (value instanceof String) {
                    escapeText((String) value, 0, ((String) value).length(), sqlBuilder);
                } else if (value instanceof byte[]) {
                    sqlBuilder.append('x')
                            .append(_Constant.QUOTE)
                            .append(HexUtils.hexEscapesText(true, (byte[]) value))
                            .append(_Constant.QUOTE);
                } else if (value instanceof Temporal) {
                    if (value instanceof LocalDateTime) {
                        sqlBuilder.append(_Constant.QUOTE)
                                .append(_TimeUtils.DATETIME_FORMATTER_6.format((LocalDateTime) value))
                                .append(_Constant.QUOTE);
                    } else if (value instanceof OffsetDateTime || value instanceof ZonedDateTime) {
                        sqlBuilder.append(_Constant.QUOTE)
                                .append(_TimeUtils.OFFSET_DATETIME_FORMATTER_6.format((TemporalAccessor) value))
                                .append(_Constant.QUOTE);
                    } else if (value instanceof LocalDate || value instanceof YearMonth) {
                        sqlBuilder.append(_Constant.QUOTE)
                                .append(value)
                                .append(_Constant.QUOTE);
                    } else if (value instanceof LocalTime) {
                        sqlBuilder.append(_Constant.QUOTE)
                                .append(_TimeUtils.TIME_FORMATTER_6.format((TemporalAccessor) value))
                                .append(_Constant.QUOTE);
                    } else if (value instanceof OffsetTime) {
                        sqlBuilder.append(_Constant.QUOTE)
                                .append(_TimeUtils.OFFSET_TIME_FORMATTER_6.format((TemporalAccessor) value))
                                .append(_Constant.QUOTE);
                    } else if (value instanceof Year) {
                        sqlBuilder.append(((Year) value).getValue());
                    } else {
                        final String v = value.toString();
                        escapeText(v, 0, v.length(), sqlBuilder);
                    }
                } else if (value instanceof Period || value instanceof Duration || value instanceof MonthDay) {
                    sqlBuilder.append(_Constant.QUOTE)
                            .append(value)
                            .append(_Constant.QUOTE);
                } else {
                    final String v = value.toString();
                    escapeText(v, 0, v.length(), sqlBuilder);
                }
            }
            break;
            case NULL:
            case UNKNOWN:
                throw ExecutorSupport.mapMethodError(typeMeta.mappingType(), dataType);
            default:
                throw _Exceptions.unexpectedEnum((SQLiteType) dataType);
        }

        return false;
    }

    @Override
    protected final SQLiteDdlParser createDdlDialect() {
        return SQLiteDdlParser.create(this);
    }

    @Override
    protected final boolean existsIgnoreOnConflict() {
        return false;
    }

    @Nullable
    @Override
    protected final CriteriaException supportChildInsert(_Insert._ChildInsert childStmt, Visible visible) {
        return null;
    }

    @Override
    protected final void standardLimitClause(@Nullable _Expression offset, @Nullable _Expression rowCount, _SqlContext context) {

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

        private Standard(DialectEnv dialectEnv, Dialect dialect) {
            super(dialectEnv, dialect);
        }


    } // Standard


}
