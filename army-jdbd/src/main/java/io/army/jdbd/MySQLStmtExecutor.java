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

package io.army.jdbd;

import io.army.mapping.MappingType;
import io.army.option.Option;
import io.army.reactive.executor.ReactiveLocalExecutor;
import io.army.reactive.executor.ReactiveRmExecutor;
import io.army.sqltype.DataType;
import io.army.sqltype.MySQLType;
import io.army.util._Exceptions;
import io.jdbd.meta.JdbdType;
import io.jdbd.result.DataRow;
import io.jdbd.result.ResultRowMeta;
import io.jdbd.session.DatabaseSession;
import io.jdbd.session.LocalDatabaseSession;
import io.jdbd.session.RmDatabaseSession;
import io.jdbd.statement.ParametrizedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;

/**
 * <p>This class is MySQL {@link JdbdExecutor}.
 *
 * @since 0.6.0
 */
abstract class MySQLStmtExecutor extends JdbdExecutor {

    static ReactiveLocalExecutor localExecutor(JdbdStmtExecutorFactory factory, LocalDatabaseSession session, String name) {
        return new LocalExecutor(factory, session, name);
    }

    static ReactiveRmExecutor rmExecutor(JdbdStmtExecutorFactory factory, RmDatabaseSession session, String name) {
        return new RmExecutor(factory, session, name);
    }

    @Nullable
    static io.jdbd.session.Option<?> mapToJdbdDialectOption(Option<?> option) {
        return null;
    }

    @Nullable
    static Option<?> mapToArmyDialectOption(io.jdbd.session.Option<?> option) {
        return null;
    }

    private static final Logger LOG = LoggerFactory.getLogger(MySQLStmtExecutor.class);

    private static final Option<Boolean> WITH_CONSISTENT_SNAPSHOT = Option.from("WITH CONSISTENT SNAPSHOT", Boolean.class);

    /**
     * private constructor
     */
    private MySQLStmtExecutor(JdbdStmtExecutorFactory factory, DatabaseSession session, String name) {
        super(factory, session, name);
    }

    @Override
    final Logger getLogger() {
        return LOG;
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/data-types.html">MySQL Data Types</a>
     */
    @Override
    final DataType getDataType(final ResultRowMeta meta, final int indexBasedZero) {
        return getMySqlType(meta.getDataType(indexBasedZero).typeName());
    }


    @Override
    final void bind(ParametrizedStatement statement, final int indexBasedZero, final MappingType type,
                    final DataType dataType, final @Nullable Object value) {
        if (!(dataType instanceof MySQLType)) {
            throw mapMethodError(type, dataType);
        }
        final io.jdbd.meta.DataType jdbdType;
        final Object bindValue;
        switch ((MySQLType) dataType) {
            case BOOLEAN: {
                if (!(value == null || value instanceof Boolean)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                jdbdType = JdbdType.BOOLEAN;
                bindValue = value;
            }
            break;
            case TINYINT: {
                if (!(value == null || value instanceof Byte)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                jdbdType = JdbdType.TINYINT;
                bindValue = value;
            }
            break;
            case TINYINT_UNSIGNED:
            case SMALLINT: {
                if (!(value == null || value instanceof Short)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                jdbdType = JdbdType.valueOf(dataType.name());
                bindValue = value;
            }
            break;
            case SMALLINT_UNSIGNED:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED:
            case INT: {
                if (!(value == null || value instanceof Integer)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                if (dataType == MySQLType.INT) {
                    jdbdType = JdbdType.INTEGER;
                } else {
                    jdbdType = JdbdType.valueOf(dataType.name());
                }
                bindValue = value;
            }
            break;
            case INT_UNSIGNED:
            case BIGINT:
            case BIT: {
                if (!(value == null || value instanceof Long)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                switch ((MySQLType) dataType) {
                    case INT_UNSIGNED:
                        jdbdType = JdbdType.INTEGER_UNSIGNED;
                        break;
                    case BIGINT:
                        jdbdType = JdbdType.BIGINT;
                        break;
                    case BIT:
                        jdbdType = JdbdType.BIT;
                        break;
                    default:
                        throw _Exceptions.unexpectedEnum((MySQLType) dataType);
                }
                bindValue = value;
            }
            break;
            case DECIMAL:
            case DECIMAL_UNSIGNED: {
                if (!(value == null || value instanceof BigDecimal)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                switch ((MySQLType) dataType) {
                    case DECIMAL:
                        jdbdType = JdbdType.DECIMAL;
                        break;
                    case DECIMAL_UNSIGNED:
                        jdbdType = JdbdType.DECIMAL_UNSIGNED;
                        break;
                    default:
                        throw _Exceptions.unexpectedEnum((MySQLType) dataType);
                }
                bindValue = value;
            }
            break;
            case CHAR:
            case VARCHAR:
            case ENUM:
            case SET:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT: {
                if (!(value == null || value instanceof String)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                jdbdType = JdbdType.valueOf(dataType.name());
                bindValue = value;
            }
            break;
            case JSON:
            case LONGTEXT: {
                jdbdType = JdbdType.valueOf(dataType.name());
                bindValue = toJdbdLongTextValue(type, dataType, value);
            }
            break;
            case DATETIME: {
                if (!(value == null || value instanceof LocalDateTime || value instanceof OffsetDateTime)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                jdbdType = JdbdType.TIMESTAMP;
                bindValue = value;
            }
            break;
            case DATE: {
                if (!(value == null || value instanceof LocalDate)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                jdbdType = JdbdType.DATE;
                bindValue = value;
            }
            break;
            case TIME: {
                if (!(value == null
                        || value instanceof LocalTime
                        || value instanceof OffsetTime
                        || value instanceof Duration)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                jdbdType = JdbdType.TIME;
                bindValue = value;
            }
            break;
            case YEAR: {
                if (!(value instanceof Short)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                jdbdType = JdbdType.YEAR;
                bindValue = value;
            }
            break;
            case FLOAT: {
                if (!(value == null || value instanceof Float)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                jdbdType = JdbdType.FLOAT;
                bindValue = value;
            }
            break;
            case DOUBLE: {
                if (!(value == null || value instanceof Double)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                jdbdType = JdbdType.DOUBLE;
                bindValue = value;
            }
            break;
            case BIGINT_UNSIGNED: {
                if (!(value == null || value instanceof BigInteger)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                jdbdType = JdbdType.BIGINT_UNSIGNED;
                bindValue = value;
            }
            break;
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB: {
                if (!(value == null || value instanceof byte[])) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                jdbdType = JdbdType.valueOf(dataType.name());
                bindValue = value;
            }
            break;
            case LONGBLOB: {
                jdbdType = JdbdType.LONGBLOB;
                bindValue = toJdbdLongBinaryValue(type, dataType, value);
            }
            break;
            case GEOMETRY: {
                jdbdType = JdbdType.GEOMETRY;
                bindValue = toJdbdGeometry(type, dataType, value);
            }
            break;
            case NULL: {
                if (value != null) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                jdbdType = JdbdType.NULL;
                bindValue = null;
            }
            break;

            case UNKNOWN:
            default:
                throw beforeBindMethodError(type, dataType, value);
        }

        statement.bind(indexBasedZero, jdbdType, bindValue);
    }


    @Nullable
    @Override
    final Object get(final DataRow row, final int indexBasedZero, final MappingType type, final DataType dataType) {
        final Object value;
        switch ((MySQLType) dataType) {
            case BOOLEAN:
                value = row.get(indexBasedZero, Boolean.class);
                break;
            case TINYINT:
                value = row.get(indexBasedZero, Byte.class);
                break;
            case TINYINT_UNSIGNED:
            case SMALLINT:
                value = row.get(indexBasedZero, Short.class);
                break;
            case SMALLINT_UNSIGNED:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED:
            case INT:
            case YEAR:
                value = row.get(indexBasedZero, Integer.class);
                break;
            case INT_UNSIGNED:
            case BIGINT:
            case BIT:
                value = row.get(indexBasedZero, Long.class);
                break;
            case DECIMAL:
            case DECIMAL_UNSIGNED:
                value = row.get(indexBasedZero, BigDecimal.class);
                break;
            case DATETIME: {
                if (type instanceof MappingType.SqlOffsetDateTimeType) {
                    value = row.get(indexBasedZero, OffsetDateTime.class);
                } else {
                    value = row.get(indexBasedZero, LocalDateTime.class);
                }
            }
            break;
            case DATE:
                value = row.get(indexBasedZero, LocalDate.class);
                break;
            case TIME: {
                if (type instanceof MappingType.SqlLocalTimeType) {
                    value = row.get(indexBasedZero, LocalTime.class);
                } else if (type instanceof MappingType.SqlOffsetTimeType) {
                    value = row.get(indexBasedZero, OffsetTime.class);
                } else if (type instanceof MappingType.SqlDurationType) {
                    value = row.get(indexBasedZero, Duration.class);
                } else {
                    value = row.get(indexBasedZero, LocalTime.class);
                }
            }
            break;
            case CHAR:
            case VARCHAR:
            case ENUM:
            case SET:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
                value = row.get(indexBasedZero, String.class);
                break;
            case JSON:
            case LONGTEXT:
                value = getLongText(row, indexBasedZero);
                break;
            case FLOAT:
                value = row.get(indexBasedZero, Float.class);
                break;
            case DOUBLE:
                value = row.get(indexBasedZero, Double.class);
                break;
            case BIGINT_UNSIGNED:
                value = row.get(indexBasedZero, BigInteger.class);
                break;
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB: {
                if (type instanceof MappingType.SqlStringType) {
                    value = row.get(indexBasedZero, String.class);
                } else {
                    value = row.get(indexBasedZero, byte[].class);
                }
            }
            break;
            case LONGBLOB:
            case GEOMETRY:
                value = getLongBinary(row, indexBasedZero, type);
                break;
            case NULL: {
                if (!row.isNull(indexBasedZero)) {
                    throw driverError();
                }
                value = null;
            }
            break;
            case UNKNOWN:
                value = row.get(indexBasedZero);
                break;
            default:
                throw _Exceptions.unexpectedEnum((MySQLType) dataType);
        }
        return value;
    }

    private static final class LocalExecutor extends MySQLStmtExecutor implements ReactiveLocalExecutor {

        private LocalExecutor(JdbdStmtExecutorFactory factory, LocalDatabaseSession session, String name) {
            super(factory, session, name);
        }


    } // LocalExecutor

    private static final class RmExecutor extends MySQLStmtExecutor implements ReactiveRmExecutor {

        private RmExecutor(JdbdStmtExecutorFactory factory, RmDatabaseSession session, String name) {
            super(factory, session, name);
        }

    } // RmExecutor


}
