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


package io.army.jdbc;

import io.army.dialect._Constant;
import io.army.executor.DataAccessException;
import io.army.executor.ExecutorSupport;
import io.army.executor.SyncLocalExecutor;
import io.army.executor.SyncRmExecutor;
import io.army.mapping.MappingType;
import io.army.option.Option;
import io.army.sqltype.DataType;
import io.army.sqltype.SQLiteType;
import io.army.transaction.HandleMode;
import io.army.transaction.Isolation;
import io.army.transaction.TransactionInfo;
import io.army.transaction.TransactionOption;
import io.army.util._Exceptions;
import io.army.util._TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.function.Function;

final class SQLiteExecutor extends JdbcExecutor implements SyncLocalExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(SQLiteExecutor.class);


    static SyncLocalExecutor localExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
        return new SQLiteExecutor(factory, conn, sessionName);
    }

    static SyncRmExecutor rmExecutor(JdbcExecutorFactory factory, final Object connObj, String sessionName) {
        throw new DataAccessException("SQLite don't support XA transaction");
    }

    private static final Option<Boolean> DEFERRED = Option.from("DEFERRED", Boolean.class);

    private static final Option<Boolean> IMMEDIATE = Option.from("IMMEDIATE", Boolean.class);

    private static final Option<Boolean> EXCLUSIVE = Option.from("EXCLUSIVE", Boolean.class);


    private TransactionInfo transactionInfo;

    private boolean sessionReadonly;

    private int sessionIsolationLevel;

    /**
     * private constructor
     */
    private SQLiteExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
        super(factory, conn, sessionName);
        try {
            this.sessionReadonly = conn.isReadOnly();
            this.sessionIsolationLevel = conn.getTransactionIsolation();
        } catch (Exception e) {
            throw handleException(e);
        }
    }


    @Override
    public TransactionInfo sessionTransactionCharacteristics(final Function<Option<?>, ?> optionFunc)
            throws DataAccessException {

        final Isolation isolationLevel;
        final boolean readOnly;
        readOnly = this.sessionReadonly;
        isolationLevel = jdbcIsolationToArmyIsolation(this.sessionIsolationLevel);

        return TransactionInfo.notInTransaction(isolationLevel, readOnly);
    }


    @Override
    public void setTransactionCharacteristics(final TransactionOption option) throws DataAccessException {
        final Isolation isolation;
        isolation = option.isolation();

        this.sessionReadonly = option.isReadOnly();
        if (isolation != null) {
            this.sessionIsolationLevel = armyIsolationToJdbcIsolation(isolation);
        }
    }


    /**
     * @see <a href="https://www.sqlite.org/lang_transaction.html">BEGIN Transaction</a>
     */
    @Override
    public TransactionInfo startTransaction(final TransactionOption option, final HandleMode mode) {
        final StringBuilder builder = new StringBuilder(168);
        int stmtCount = 0;
        if (inTransaction()) {
            handleInTransaction(builder, mode);
            stmtCount++;
        }

        final boolean deferred, immediate, exclusive;
        deferred = Boolean.TRUE.equals(option.valueOf(DEFERRED));
        immediate = Boolean.TRUE.equals(option.valueOf(IMMEDIATE));
        exclusive = Boolean.TRUE.equals(option.valueOf(EXCLUSIVE));

        builder.append("BEGIN");

        if (deferred) {
            if (immediate || exclusive) {
                throw transactionModifierError();
            }
            builder.append(_Constant.SPACE)
                    .append("DEFERRED");
        } else if (immediate) {
            if (exclusive) {
                throw transactionModifierError();
            }
            builder.append(_Constant.SPACE)
                    .append("IMMEDIATE");
        } else if (exclusive) {
            builder.append(_Constant.SPACE)
                    .append("EXCLUSIVE");
        }

        builder.append(_Constant.SPACE)
                .append("TRANSACTION");

        stmtCount++;

        final boolean readOnly = option.isReadOnly();

        final Isolation isolation, finalIsolation;
        isolation = option.isolation();

        final int isolationLevel = this.sessionIsolationLevel;
        final Isolation tempIsolation;
        try {
            if (isolation == null) {
                this.conn.setTransactionIsolation(isolationLevel);
                tempIsolation = jdbcIsolationToArmyIsolation(isolationLevel);
            } else {
                tempIsolation = isolation;
            }
            this.conn.setReadOnly(readOnly);
        } catch (Exception e) {
            throw handleException(e);
        }

        // execute start transaction statements
        finalIsolation = executeStartTransaction(stmtCount, tempIsolation, builder.toString());

        final TransactionInfo.InfoBuilder infoBuilder;
        infoBuilder = TransactionInfo.builder(true, finalIsolation, readOnly);
        infoBuilder.option(option);
        if (deferred) {
            infoBuilder.option(DEFERRED, Boolean.TRUE);
        } else if (immediate) {
            infoBuilder.option(IMMEDIATE, Boolean.TRUE);
        } else if (exclusive) {
            infoBuilder.option(EXCLUSIVE, Boolean.TRUE);
        }

        final TransactionInfo info;
        this.transactionInfo = info = infoBuilder.build();
        return info;
    }


    @Nullable
    @Override
    public TransactionInfo commit(Function<Option<?>, ?> optionFunc) {
        executeSimpleStaticStatement(COMMIT, LOG);
        this.transactionInfo = null;
        return null;
    }

    @Nullable
    @Override
    public TransactionInfo rollback(Function<Option<?>, ?> optionFunc) {
        executeSimpleStaticStatement(ROLLBACK, LOG);
        this.transactionInfo = null;
        return null;
    }

    @Override
    void bind(PreparedStatement stmt, final int indexBasedOne, MappingType type, DataType dataType, final Object value)
            throws SQLException {
        if (!(dataType instanceof SQLiteType)) {
            throw mapMethodError(type, dataType);
        }
        switch ((SQLiteType) dataType) {
            case BOOLEAN: {
                if (!(value instanceof Boolean)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setBoolean(indexBasedOne, (Boolean) value);
            }
            break;
            case TINYINT: {
                if (!(value instanceof Byte)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setObject(indexBasedOne, ((Byte) value).intValue());
            }
            break;
            case SMALLINT: {
                if (!(value instanceof Short)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setObject(indexBasedOne, ((Short) value).intValue());
            }
            break;
            case MEDIUMINT:
            case INTEGER: {
                if (!(value instanceof Integer)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setObject(indexBasedOne, value);
            }
            break;
            case BIGINT:
            case UNSIGNED_BIG_INT:
            case BIT: {
                if (!(value instanceof Long)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setObject(indexBasedOne, value);
            }
            break;
            case DECIMAL: {
                if (value instanceof BigDecimal) {
                    stmt.setString(indexBasedOne, ((BigDecimal) value).toPlainString());
                } else if (value instanceof BigInteger) {
                    stmt.setString(indexBasedOne, value.toString());
                } else {
                    throw beforeBindMethodError(type, dataType, value);
                }
            }
            break;
            case FLOAT: {
                if (!(value instanceof Float)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setObject(indexBasedOne, value);
            }
            break;
            case DOUBLE: {
                if (!(value instanceof Double || value instanceof Float)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setObject(indexBasedOne, value);
            }
            break;
            case CHAR:
            case VARCHAR:
            case TEXT:
            case JSON: {
                if (!(value instanceof String)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setString(indexBasedOne, (String) value);
            }
            break;
            case BINARY:
            case VARBINARY:
            case BLOB: {
                if (!(value instanceof byte[])) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setBytes(indexBasedOne, (byte[]) value);
            }
            break;
            case TIMESTAMP: {
                if (!(value instanceof LocalDateTime)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setString(indexBasedOne, _TimeUtils.DATETIME_FORMATTER_6.format((TemporalAccessor) value));
            }
            break;
            case TIMESTAMP_WITH_TIMEZONE: {
                if (!(value instanceof OffsetDateTime)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setString(indexBasedOne, _TimeUtils.OFFSET_DATETIME_FORMATTER_6.format((TemporalAccessor) value));
            }
            break;
            case DATE: {
                if (!(value instanceof LocalDate)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setString(indexBasedOne, value.toString());
            }
            break;
            case TIME: {
                if (!(value instanceof LocalTime)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setString(indexBasedOne, _TimeUtils.TIME_FORMATTER_6.format((TemporalAccessor) value));
            }
            break;
            case TIME_WITH_TIMEZONE: {
                if (!(value instanceof OffsetTime)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setString(indexBasedOne, _TimeUtils.OFFSET_TIME_FORMATTER_6.format((TemporalAccessor) value));
            }
            break;
            case YEAR: {
                if (value instanceof Short) {
                    stmt.setShort(indexBasedOne, (Short) value);
                } else if (value instanceof Year) {
                    stmt.setInt(indexBasedOne, ((Year) value).getValue());
                } else {
                    throw beforeBindMethodError(type, dataType, value);
                }
            }
            break;
            case MONTH_DAY: {
                if (!(value instanceof MonthDay)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setString(indexBasedOne, value.toString());
            }
            break;
            case YEAR_MONTH: {
                if (!(value instanceof YearMonth)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setString(indexBasedOne, value.toString());
            }
            break;
            case PERIOD: {
                if (!(value instanceof Period)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setString(indexBasedOne, value.toString());
            }
            break;
            case DURATION: {
                if (!(value instanceof Duration)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setString(indexBasedOne, value.toString());
            }
            break;
            case DYNAMIC: {
                if (value instanceof String) {
                    stmt.setString(indexBasedOne, (String) value);
                } else if (value instanceof byte[]) {
                    stmt.setBytes(indexBasedOne, (byte[]) value);
                } else if (value instanceof BigDecimal) {
                    stmt.setString(indexBasedOne, ((BigDecimal) value).toPlainString());
                } else if (value instanceof BigInteger) {
                    stmt.setString(indexBasedOne, value.toString());
                } else if (value instanceof Integer
                        || value instanceof Long
                        || value instanceof Double
                        || value instanceof Float) {
                    stmt.setObject(indexBasedOne, value);
                } else if (value instanceof Short || value instanceof Byte) {
                    stmt.setObject(indexBasedOne, ((Number) value).intValue());
                } else {
                    throw ExecutorSupport.beforeBindMethodError(type, dataType, value);
                }
            }
            break;
            case NULL:
            case UNKNOWN:
            default:
                throw ExecutorSupport.beforeBindMethodError(type, dataType, value);

        }
    }

    @Override
    DataType getDataType(final ResultSetMetaData meta, final int indexBasedOne) throws SQLException {
        return getSQLiteType(meta.getColumnTypeName(indexBasedOne));
    }

    @Nullable
    @Override
    Object get(ResultSet resultSet, final int indexBasedOne, MappingType type, final DataType dataType) throws SQLException {
        final Object value;
        switch ((SQLiteType) dataType) {
            case NULL:
                value = null;
                break;
            case BOOLEAN: {
                if (resultSet.getObject(indexBasedOne) == null) {
                    value = null;
                } else {
                    value = resultSet.getBoolean(indexBasedOne);
                }
            }
            break;
            case TINYINT:
            case SMALLINT:
            case MEDIUMINT:
            case INTEGER: {
                if (resultSet.getObject(indexBasedOne) == null) {
                    value = null;
                } else {
                    value = resultSet.getInt(indexBasedOne);
                }
            }
            break;
            case BIGINT:
            case UNSIGNED_BIG_INT:
            case BIT: {
                if (resultSet.getObject(indexBasedOne) == null) {
                    value = null;
                } else {
                    value = resultSet.getLong(indexBasedOne);
                }
            }
            break;
            case FLOAT: {
                if (resultSet.getObject(indexBasedOne) == null) {
                    value = null;
                } else {
                    value = resultSet.getFloat(indexBasedOne);
                }
            }
            break;
            case DOUBLE: {
                if (resultSet.getObject(indexBasedOne) == null) {
                    value = null;
                } else {
                    value = resultSet.getDouble(indexBasedOne);
                }
            }
            break;
            case DECIMAL:
                value = resultSet.getBigDecimal(indexBasedOne);
                break;
            case CHAR:
            case VARCHAR:
            case TEXT:
            case JSON:
            case TIME:
            case TIME_WITH_TIMEZONE:
            case DATE:
            case YEAR:
            case TIMESTAMP:
            case TIMESTAMP_WITH_TIMEZONE:

            case MONTH_DAY:
            case YEAR_MONTH:
            case PERIOD:
            case DURATION:
                value = resultSet.getString(indexBasedOne);
                break;
            case BINARY:
            case VARBINARY:
            case BLOB:
                value = resultSet.getBytes(indexBasedOne);
                break;
            case DYNAMIC:
            case UNKNOWN:
            default:
                value = resultSet.getObject(indexBasedOne);
        }
        return value;
    }


    @Override
    Isolation readIsolation(String level) {
        // no bug ,never here
        throw new UnsupportedOperationException();
    }


    @Override
    Logger getLogger() {
        return LOG;
    }


    @Nullable
    @Override
    TransactionInfo obtainTransaction() {
        return this.transactionInfo;
    }

    /*-------------------below static methods -------------------*/


    private static Isolation jdbcIsolationToArmyIsolation(final int isolationLevel) {
        final Isolation isolation;
        switch (isolationLevel) {
            case Connection.TRANSACTION_REPEATABLE_READ:
                isolation = Isolation.REPEATABLE_READ;
                break;
            case Connection.TRANSACTION_READ_COMMITTED:
                isolation = Isolation.READ_COMMITTED;
                break;
            case Connection.TRANSACTION_SERIALIZABLE:
                isolation = Isolation.SERIALIZABLE;
                break;
            case Connection.TRANSACTION_READ_UNCOMMITTED:
                isolation = Isolation.READ_UNCOMMITTED;
                break;
            default:
                throw new DataAccessException(String.format("unknown jdbc isolation level[%s]", isolationLevel));
        }
        return isolation;
    }

    private static int armyIsolationToJdbcIsolation(final Isolation isolation) {
        final int isolationLevel;
        if (isolation == Isolation.SERIALIZABLE) {
            isolationLevel = Connection.TRANSACTION_SERIALIZABLE;
        } else if (isolation == Isolation.READ_COMMITTED) {
            isolationLevel = Connection.TRANSACTION_READ_COMMITTED;
        } else if (isolation == Isolation.REPEATABLE_READ) {
            isolationLevel = Connection.TRANSACTION_REPEATABLE_READ;
        } else if (isolation == Isolation.READ_UNCOMMITTED) {
            isolationLevel = Connection.TRANSACTION_READ_UNCOMMITTED;
        } else {
            throw _Exceptions.unknownIsolation(isolation);
        }
        return isolationLevel;
    }


    private static DataAccessException transactionModifierError() {
        return new DataAccessException("couldn't specify DEFERRED,IMMEDIATE,EXCLUSIVE at the same time");
    }


}
