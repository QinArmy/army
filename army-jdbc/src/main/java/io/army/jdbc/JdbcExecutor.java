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

import io.army.ArmyException;
import io.army.criteria.SQLParam;
import io.army.criteria.Selection;
import io.army.dialect._Constant;
import io.army.executor.DataAccessException;
import io.army.executor.DriverException;
import io.army.executor.StmtExecutor;
import io.army.executor.SyncExecutor;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.mapping.UnsignedBigintType;
import io.army.meta.FieldMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TypeMeta;
import io.army.option.Option;
import io.army.result.*;
import io.army.session.*;
import io.army.sqltype.ArmyType;
import io.army.sqltype.DataType;
import io.army.sqltype.SQLType;
import io.army.stmt.*;
import io.army.transaction.HandleMode;
import io.army.transaction.Isolation;
import io.army.transaction.TransactionInfo;
import io.army.transaction.Xid;
import io.army.util.*;
import org.slf4j.Logger;

import javax.sql.XAConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <p>This class is a abstract implementation of {@link SyncExecutor} with JDBC spi.
 * <p>This class is base class of following jdbd executor:
 * <ul>
 *     <li>{@link MySQLExecutor}</li>
 *     <li>{@link PostgreExecutor}</li>
 * </ul>
 * <p>Following is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 *
 * @see JdbcExecutorFactory
 * @see <a href="https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html">JDBC</a>
 */
abstract class JdbcExecutor extends JdbcExecutorSupport implements SyncExecutor {

    private static final AtomicLong EXECUTOR_IDENTIFIER = new AtomicLong(0);

    final JdbcExecutorFactory factory;

    final Connection conn;

    final String sessionName;
    private final long identifier;

    /**
     * <p>True : application developer have got the {@link Connection} instance,<br/>
     * so {@link TransactionInfo} perhaps error.
     * <p>More info,see {@link io.army.env.ArmyKey#DRIVER_SPI_MODE}
     */
    private boolean driverSpiOpened;

    JdbcExecutor(JdbcExecutorFactory factory, Connection conn, String sessionName) {
        this.sessionName = sessionName;
        this.factory = factory;
        this.conn = conn;

        if (factory.sessionIdentifierEnable) {
            this.identifier = EXECUTOR_IDENTIFIER.addAndGet(1L);
        } else {
            this.identifier = 0L;
        }
    }


    @Override
    public final long sessionIdentifier(Function<Option<?>, ?> sessionFunc) throws DataAccessException {
        return this.identifier;
    }

    @Override
    public final boolean inTransaction(Function<Option<?>, ?> sessionFunc) throws DataAccessException {
        final TransactionInfo info;
        info = obtainTransaction();
        return info != null && info.inTransaction();
    }

    @Override
    public final boolean isSameFactory(StmtExecutor s) {
        return s instanceof JdbcExecutor && ((JdbcExecutor) s).factory == this.factory;
    }

    @Override
    public final boolean isDriverAssignableTo(Class<?> spiClass) {
        return spiClass.isAssignableFrom(this.conn.getClass());
    }

    @Override
    public final <T> T getDriverSpi(final Class<T> spiClass) {
        final T spi;
        if (Connection.class.isAssignableFrom(spiClass)) {
            spi = spiClass.cast(this.conn);
        } else if (this instanceof XaConnectionExecutor && XAConnection.class.isAssignableFrom(spiClass)) {
            spi = spiClass.cast(((XaConnectionExecutor) this).getXAConnection());
        } else {
            spi = spiClass.cast(this.conn);
        }
        this.driverSpiOpened = true;
        return spi;
    }

    @Override
    public final TransactionInfo transactionInfo(Function<Option<?>, ?> sessionFunc) throws DataAccessException {
        final TransactionInfo info;
        info = obtainTransaction();
        if (info != null) {
            return info;
        }
        return sessionTransactionCharacteristics(Option.EMPTY_FUNC, sessionFunc);
    }

    @Override
    public final Object setSavePoint(Function<Option<?>, ?> optionFunc, Function<Option<?>, ?> sessionFunc) throws DataAccessException {
        final Object name;
        if (optionFunc == Option.EMPTY_FUNC) {
            name = null;
        } else {
            name = optionFunc.apply(Option.NAME);
        }

        try {
            final Savepoint savepoint;
            if (name instanceof String) {
                savepoint = this.conn.setSavepoint((String) name);
            } else {
                savepoint = this.conn.setSavepoint();
            }
            return savepoint;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @Override
    public final void releaseSavePoint(final Object savepoint, final Function<Option<?>, ?> optionFunc, Function<Option<?>, ?> sessionFunc)
            throws DataAccessException {

        if (!(savepoint instanceof Savepoint)) {
            throw _Exceptions.unknownSavePoint(savepoint);
        }

        try {
            this.conn.releaseSavepoint((Savepoint) savepoint);
        } catch (Exception e) {
            throw handleException(e);
        }

    }

    @Override
    public final void rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc, Function<Option<?>, ?> sessionFunc)
            throws DataAccessException {
        if (!(savepoint instanceof Savepoint)) {
            throw _Exceptions.unknownSavePoint(savepoint);
        }

        try {
            this.conn.rollback((Savepoint) savepoint);
        } catch (Exception e) {
            throw handleException(e);
        }
    }


    @Override
    public final ResultStates update(SimpleStmt stmt, SyncStmtOption option, Function<Option<?>, ?> sessionFunc)
            throws DataAccessException {

        assert !(stmt instanceof GeneratedKeyStmt) || stmt.selectionList().size() == 0;


        try (final Statement statement = bindStatement(stmt, option)) {

            final long affectedRows;
            if (statement instanceof PreparedStatement) {
                if (this.factory.useLargeUpdate) {
                    affectedRows = ((PreparedStatement) statement).executeLargeUpdate();
                } else {
                    affectedRows = ((PreparedStatement) statement).executeUpdate();
                }
            } else if (this.factory.useLargeUpdate) {
                if (stmt instanceof GeneratedKeyStmt) {
                    affectedRows = statement.executeLargeUpdate(stmt.sqlText(), Statement.RETURN_GENERATED_KEYS);
                } else {
                    affectedRows = statement.executeLargeUpdate(stmt.sqlText());
                }
            } else if (stmt instanceof GeneratedKeyStmt) {
                affectedRows = statement.executeUpdate(stmt.sqlText(), Statement.RETURN_GENERATED_KEYS);
            } else {
                affectedRows = statement.executeUpdate(stmt.sqlText());
            }

            final long[] firstIdHolder;
            if (stmt instanceof GeneratedKeyStmt) {
                firstIdHolder = new long[1];
                final int insertRowCount;
                insertRowCount = readRowId(statement.getGeneratedKeys(), firstIdHolder, (GeneratedKeyStmt) stmt);
                if (insertRowCount != affectedRows) {
                    throw driverError();
                }
            } else {
                firstIdHolder = null;
            }


            final Map<Option<?>, Object> optionMap = _Collections.hashMap();
            putExecutorOptions(statement.getWarnings(), optionMap);
            optionMap.put(AFFECTED_ROWS, affectedRows);
            optionMap.put(HAS_COLUMN, Boolean.FALSE);
            if (firstIdHolder != null) {
                optionMap.put(LAST_INSERTED_ID, firstIdHolder[0]);
            }
            if (stmt instanceof DeclareCursorStmt) {
                final JdbcStmtCursor cursor = new JdbcStmtCursor(this, (DeclareCursorStmt) stmt, option, sessionFunc);
                optionMap.put(SyncStmtCursor.SYNC_STMT_CURSOR, cursor);
            }
            return new JdbcResultStates(mergeOptionFunc(optionMap, sessionFunc));
        } catch (Exception e) {
            throw handleException(e);
        }

    }


    @Override
    public final List<Long> batchUpdateList(BatchStmt stmt, @Nullable IntFunction<List<Long>> listConstructor, SyncStmtOption option,
                                            @Nullable LongConsumer consumer, Function<Option<?>, ?> sessionFunc)
            throws DataAccessException {

        final List<Long> resultList;
        if (option.isParseBatchAsMultiStmt()) {
            resultList = executeMultiStmtBatchUpdateAsLong(stmt, listConstructor, option, consumer);
        } else {
            resultList = executeBatchUpdateAsLong(stmt, listConstructor, option, consumer);
        }
        return Collections.unmodifiableList(resultList);
    }

    @Override
    public final Stream<ResultStates> batchUpdate(BatchStmt stmt, SyncStmtOption option, Function<Option<?>, ?> optionFunc) {
        final Stream<ResultStates> stream;

        if (option.isParseBatchAsMultiStmt()) {
            stream = executeMultiStmtBatchUpdate(stmt, option, optionFunc);
        } else {
            stream = executeBatchUpdate(stmt, option, optionFunc);
        }
        return stream;
    }


    @Override
    public final <R> Stream<R> query(SingleSqlStmt stmt, final @Nullable Function<? super CurrentRecord, R> function,
                                     SyncStmtOption option, Function<Option<?>, ?> sessionFunc)
            throws DataAccessException {
        if (function == null) {
            throw _Exceptions.recordMapFuncIsNull();
        }
        try {
            final Stream<R> stream;
            if (stmt instanceof SimpleStmt) {
                stream = executeSimpleQuery((SimpleStmt) stmt, option, function, sessionFunc, false);
            } else if (!(stmt instanceof BatchStmt)) {
                throw _Exceptions.unexpectedStmt(stmt);
            } else if (option.isParseBatchAsMultiStmt()) {
                stream = executeMultiStmtBatchQuery((BatchStmt) stmt, option, function, sessionFunc, false);
            } else {
                stream = executeBatchQuery((BatchStmt) stmt, option, function, sessionFunc, false);
            }
            return stream;
        } catch (Exception e) {
            throw handleException(e);
        }
    }


    @Override
    public final SyncBatchQuery batchQuery(BatchStmt stmt, SyncStmtOption option, Function<Option<?>, ?> sessionFunc) {
        PreparedStatement statement = null;
        try {

            statement = this.conn.prepareStatement(stmt.sqlText());

            return new JdbcBatchQuery(true, this, stmt, option, sessionFunc, statement);
        } catch (Exception e) {
            closeResource(statement);
            throw handleException(e);
        } catch (Throwable e) {
            closeResource(statement);
            throw e;
        }
    }


    @Nullable
    @Override
    public final <T> T valueOf(Option<T> option) {
        return null;
    }


    @Override
    public final void close() throws DataAccessException {
        Throwable error = null;
        try {
            this.conn.close();

        } catch (Throwable e) {
            error = e;
        }

        if (this instanceof XaConnectionExecutor) {
            try {
                ((XaConnectionExecutor) this).closeXaConnection();
            } catch (Exception e) {
                if (error == null) {
                    error = e;
                }
            }
        }

        if (error != null) {
            if (error instanceof Exception) {
                throw handleException((Exception) error);
            } else {
                throw (Error) error;
            }
        }

    }


    @Override
    public final String toString() {
        return _StringUtils.builder(46)
                .append(getClass().getName())
                .append("[sessionName:")
                .append(this.sessionName)
                .append(",hash:")
                .append(System.identityHashCode(this))
                .append(']')
                .toString();
    }



    /*################################## blow packet template ##################################*/

    @SuppressWarnings("unused")
    abstract Logger getLogger();

    abstract void bind(PreparedStatement stmt, int indexBasedOne, MappingType type,
                       DataType dataType, Object value)
            throws SQLException;

    abstract DataType getDataType(ResultSetMetaData meta, int indexBasedOne) throws SQLException;

    @Nullable
    abstract Object get(ResultSet resultSet, int indexBasedOne, MappingType type, DataType dataType) throws SQLException;

    /**
     * @return current transaction cache instance
     */
    @Nullable
    abstract TransactionInfo obtainTransaction();


    /**
     * @see #readIsolationAndClose(ResultSet)
     */
    abstract Isolation readIsolation(String level);


    final void handleInTransaction(final StringBuilder builder, final HandleMode mode) {
        switch (mode) {
            case ERROR_IF_EXISTS:
                throw transactionExistsRejectStart(this.sessionName);
            case COMMIT_IF_EXISTS:
                builder.append(COMMIT)
                        .append(_Constant.SPACE_SEMICOLON_SPACE);
                break;
            case ROLLBACK_IF_EXISTS:
                builder.append(ROLLBACK)
                        .append(_Constant.SPACE_SEMICOLON_SPACE);
                break;
            default:
                throw _Exceptions.unexpectedEnum(mode);
        }
    }


    final Isolation executeStartTransaction(final int stmtCount, final @Nullable Isolation isolation,
                                            final String multiStmtSql) throws DataAccessException {

        printSqlIfNeed(this.factory, this.sessionName, getLogger(), multiStmtSql);

        try (final Statement statement = this.conn.createStatement()) {
            Isolation sessionIsolation = null;
            int batchSize = 0;
            if (this.factory.useMultiStmt) {
                if (statement.execute(multiStmtSql)) {
                    sessionIsolation = readIsolationAndClose(statement.getResultSet());
                } else if (statement.getUpdateCount() == -1) {
                    throw multiStatementLessThanExpected(0, stmtCount); // no result
                }
                batchSize++;
                while (true) {
                    if (statement.getMoreResults()) {
                        assert sessionIsolation == null;
                        sessionIsolation = readIsolationAndClose(statement.getResultSet());
                    } else if (statement.getUpdateCount() == -1) {
                        break;
                    }
                    batchSize++;
                }
            } else if (isolation == null) {
                int start = 0;
                String sql;

                for (int semicolon; (semicolon = multiStmtSql.indexOf(_Constant.SEMICOLON, start)) > 0; start = semicolon + 1) {
                    sql = multiStmtSql.substring(start, semicolon).trim();
                    batchSize++;
                    if (sql.startsWith("SELECT ") || sql.startsWith("SHOW ")) {
                        assert sessionIsolation == null;
                        sessionIsolation = readIsolationAndClose(statement.executeQuery(sql));
                    } else {
                        statement.executeUpdate(sql);
                    }
                }
                statement.executeUpdate(multiStmtSql.substring(start));
                batchSize++;
                assert sessionIsolation != null;
            } else {
                int start = 0;
                for (int semicolon; (semicolon = multiStmtSql.indexOf(_Constant.SEMICOLON, start)) > 0; start = semicolon + 1) {
                    statement.addBatch(multiStmtSql.substring(start, semicolon));
                    batchSize++;
                }
                statement.addBatch(multiStmtSql.substring(start));
                batchSize++;
                statement.executeBatch();
            }

            assert batchSize == stmtCount;

            final Isolation finalIsolation;
            if (isolation == null) {
                assert sessionIsolation != null;
                finalIsolation = sessionIsolation;
            } else {
                finalIsolation = isolation;
            }
            return finalIsolation;
        } catch (Exception e) {
            throw handleException(e);
        }

    }


    /**
     * @see #executeStartTransaction(int, Isolation, String)
     */
    final Isolation readIsolationAndClose(final ResultSet rs) throws SQLException {
        try (ResultSet resultSet = rs) {
            if (!resultSet.next()) {
                throw driverError();
            }
            return readIsolation(resultSet.getString(1));
        }
    }

    /**
     * @throws DataAccessException throw when chain is true and {@link #obtainTransaction()} is null.
     */
    final boolean transactionChain(final Function<Option<?>, ?> optionFunc, final StringBuilder builder)
            throws DataAccessException {

        final Object chainValue;
        if (optionFunc == Option.EMPTY_FUNC) {
            chainValue = null;
        } else {
            chainValue = optionFunc.apply(Option.CHAIN);
        }

        final boolean chain;
        if (chainValue instanceof Boolean) {
            builder.append(_Constant.SPACE_AND);
            chain = (Boolean) chainValue;
            if (!chain) {
                builder.append(" NO");
            } else if (obtainTransaction() == null) {
                throw new DataAccessException("COMMIT AND CHAIN can only be used in transaction blocks");
            }
            builder.append(" CHAIN");
        } else {
            chain = false;
        }
        return chain;
    }


    final Stream<Xid> jdbcRecover(final String sql, Function<DataRecord, Xid> function, StreamOption option,
                                  final Function<Option<?>, ?> sessionFunc) {

        printSqlIfNeed(this.factory, this.sessionName, getLogger(), sql);

        Statement statement = null;
        ResultSet resultSet = null;
        try {

            statement = this.conn.createStatement();

            final int fetchSize = option.fetchSize();
            if (fetchSize > 0) {
                statement.setFetchSize(fetchSize);
            } else if (fetchSize == 0 && option.isPreferClientStream() && this instanceof MySQLExecutor) {
                statement.setFetchSize(Integer.MIN_VALUE);
            }

            resultSet = statement.executeQuery(sql);

            final DataType[] dataTypeArray;
            dataTypeArray = createSqlTypArray(resultSet.getMetaData());

            final XidRowSpliterator spliterator;
            spliterator = new XidRowSpliterator(this, option, statement, resultSet, dataTypeArray, function, sessionFunc);

            final Consumer<StreamCommander> consumer;
            consumer = option.commanderConsumer();
            if (consumer != null) {
                consumer.accept(spliterator::cancel);
            }
            return StreamSupport.stream(spliterator, false)
                    .onClose(spliterator::close);
        } catch (Exception e) {
            closeResultSetAndStatement(resultSet, statement);
            throw handleException(e);
        } catch (Error e) {
            closeResultSetAndStatement(resultSet, statement);
            throw e;
        }
    }


    /**
     * @see #bindParameters(PreparedStatement, List)
     */
    final void bindArmyType(PreparedStatement stmt, final int indexBasedOne, final MappingType type,
                            final DataType dataType, final ArmyType armyType, Object value) throws SQLException {
        switch (armyType) {
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
                stmt.setByte(indexBasedOne, (Byte) value);
            }
            break;
            case TINYINT_UNSIGNED:
            case SMALLINT: {
                if (!(value instanceof Short)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setShort(indexBasedOne, (Short) value);
            }
            break;
            case SMALLINT_UNSIGNED:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED:
            case INTEGER: {
                if (!(value instanceof Integer)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setInt(indexBasedOne, (Integer) value);
            }
            break;
            case INTEGER_UNSIGNED:
            case BIGINT: {
                if (!(value instanceof Long)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setLong(indexBasedOne, (Long) value);
            }
            break;
            case BIGINT_UNSIGNED: {
                if (!(value instanceof BigInteger || value instanceof BigDecimal)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setObject(indexBasedOne, value);
            }
            break;
            case DECIMAL:
            case DECIMAL_UNSIGNED: {
                if (!(value instanceof BigDecimal)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setBigDecimal(indexBasedOne, (BigDecimal) value);
            }
            break;
            case FLOAT: {
                if (!(value instanceof Float)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setFloat(indexBasedOne, (Float) value);
            }
            break;
            case DOUBLE: {
                if (value instanceof Double) {
                    stmt.setDouble(indexBasedOne, (Double) value);
                } else if (value instanceof Float) {
                    stmt.setFloat(indexBasedOne, (Float) value);
                } else {
                    throw beforeBindMethodError(type, dataType, value);
                }
            }
            break;
            case TIME: {
                if (!(value instanceof LocalTime)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setObject(indexBasedOne, value);
            }
            break;
            case YEAR_MONTH:
            case MONTH_DAY:
            case DATE: {
                if (!(value instanceof LocalDate)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setObject(indexBasedOne, value);
            }
            break;
            case TIMESTAMP: {
                if (!(value instanceof LocalDateTime)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setObject(indexBasedOne, value);
            }
            break;
            case TIME_WITH_TIMEZONE: {
                if (!(value instanceof OffsetTime)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setObject(indexBasedOne, value);
            }
            break;
            case TIMESTAMP_WITH_TIMEZONE: {
                if (!(value instanceof OffsetDateTime)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setObject(indexBasedOne, value);
            }
            break;
            case CHAR:
            case VARCHAR:
            case ENUM:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT: {
                if (!(value instanceof String)) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setString(indexBasedOne, (String) value);
            }
            break;
            case JSON:
            case JSONB:
            case LONGTEXT:
                setLongText(stmt, indexBasedOne, type, dataType, value);
                break;
            case BINARY:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB: {
                if (!(value instanceof byte[])) {
                    throw beforeBindMethodError(type, dataType, value);
                }
                stmt.setBytes(indexBasedOne, (byte[]) value);
            }
            break;
            case LONGBLOB:
                setLongBinary(stmt, indexBasedOne, type, dataType, value);
                break;
            case GEOMETRY: {
                if (value instanceof String) {
                    stmt.setString(indexBasedOne, (String) value);
                } else if (value instanceof Reader) {
                    stmt.setCharacterStream(indexBasedOne, (Reader) value);
                } else if (value instanceof byte[]) {
                    stmt.setBytes(indexBasedOne, (byte[]) value);
                } else if (value instanceof InputStream) {
                    stmt.setBinaryStream(indexBasedOne, (InputStream) value);
                } else if (value instanceof Path) {
                    try (InputStream inputStream = Files.newInputStream((Path) value, StandardOpenOption.READ)) {
                        stmt.setBinaryStream(indexBasedOne, inputStream);
                    } catch (IOException e) {
                        String m = String.format("Parameter[%s] %s[%s] read occur error."
                                , indexBasedOne, Path.class.getName(), value);
                        throw new SQLException(m, e);
                    }
                }
            }
            break;
            default:
                throw mapMethodError(type, dataType);

        }


    }

    final void setLongText(PreparedStatement stmt, final int index, final MappingType type, final DataType dataType,
                           final Object nonNull) throws SQLException {
        if (nonNull instanceof String) {
            stmt.setString(index, (String) nonNull);
        } else if (nonNull instanceof Reader) {
            stmt.setCharacterStream(index, (Reader) nonNull);
        } else if (nonNull instanceof Path) {
            try (Reader reader = Files.newBufferedReader((Path) nonNull, StandardCharsets.UTF_8)) {
                stmt.setCharacterStream(index, reader);
            } catch (IOException e) {
                String m = String.format("Parameter[%s] %s[%s] read occur error.", index, Path.class.getName(), nonNull);
                throw new SQLException(m, e);
            }
        } else {
            throw beforeBindMethodError(type, dataType, nonNull);
        }
    }

    final void setLongBinary(PreparedStatement stmt, final int index, final MappingType type, final DataType dataType,
                             final Object nonNull) throws SQLException {
        if (nonNull instanceof byte[]) {
            stmt.setBytes(index, (byte[]) nonNull);
        } else if (nonNull instanceof InputStream) {
            stmt.setBinaryStream(index, (InputStream) nonNull);
        } else if (nonNull instanceof Path) {
            try (InputStream inputStream = Files.newInputStream((Path) nonNull, StandardOpenOption.READ)) {
                stmt.setBinaryStream(index, inputStream);
            } catch (IOException e) {
                String m = String.format("Parameter[%s] %s[%s] read occur error."
                        , index, Path.class.getName(), nonNull);
                throw new SQLException(m, e);
            }
        } else {
            throw beforeBindMethodError(type, dataType, nonNull);
        }
    }


    final int executeSimpleStaticStatement(final String sql, final Logger logger) throws ArmyException {

        try (Statement statement = this.conn.createStatement()) {

            printSqlIfNeed(this.factory, this.sessionName, logger, sql);

            return statement.executeUpdate(sql);
        } catch (Exception e) {
            throw handleException(e);
        }

    }

    final ArmyException handleException(Exception cause) {
        return this.factory.handleException(cause);
    }

    final ArmyException handleRmException(final Exception cause) {
        if (cause instanceof RmSessionException) {
            return (ArmyException) cause;
        }
        // TODO
        return new RmSessionException(cause.getMessage(), cause, RmSessionException.XAER_RMERR);
    }


    final <R> Stream<R> executeCursorFetch(final JdbcStmtCursor stmtCursor,
                                           final Direction direction, final @Nullable Long rowCount,
                                           final @Nullable Function<? super CurrentRecord, R> function,
                                           final @Nullable Consumer<ResultStates> consumer,
                                           final boolean resultItemStream) {
        if (function == null) {
            throw _Exceptions.recordMapFuncIsNull();
        } else if (consumer == null) {
            throw _Exceptions.statesConsumerIsNull();
        }

        ResultSet resultSet = null;
        try {

            final String sql;
            sql = parseCursorFetch(stmtCursor.stmt.safeCursorName(), direction, rowCount);

            printSqlIfNeed(this.factory, this.sessionName, getLogger(), sql);

            final Statement cursorStatement = stmtCursor.statement;

            cursorStatement.clearWarnings();

            resultSet = cursorStatement.executeQuery(sql);

            final ResultSetMetaData metaData;
            metaData = resultSet.getMetaData();

            final DataType[] dataTypeArray = stmtCursor.dataTypeArray;
            readDataTypeArray(metaData, dataTypeArray);

            final List<? extends Selection> selectionList = stmtCursor.selectionList;

            final JdbcCurrentRecord<R> currentRecord;
            currentRecord = new JdbcCurrentRecord<>(this, selectionList, dataTypeArray, function, consumer,
                    metaData, resultItemStream);

            final JdbcCursorRowSpliterator<R> spliterator;
            spliterator = new JdbcCursorRowSpliterator<>(cursorStatement.getWarnings(), stmtCursor.stmtOption,
                    resultSet, currentRecord, stmtCursor.sessionFunc);

            return assembleStream(spliterator, stmtCursor.stmtOption);
        } catch (Exception e) {
            closeResource(resultSet);
            throw handleException(e);
        } catch (Error e) {
            closeResource(resultSet);
            throw e;
        }

    }


    final ResultStates executeCursorMove(final JdbcStmtCursor stmtCursor, final Direction direction,
                                         final @Nullable Long rowCount) {

        try {

            final String sql;
            sql = parseCursorMove(stmtCursor.stmt.safeCursorName(), direction, rowCount);

            printSqlIfNeed(this.factory, this.sessionName, getLogger(), sql);

            final Statement cursorStatement = stmtCursor.statement;
            cursorStatement.clearWarnings();

            final int affectedRows;
            affectedRows = cursorStatement.executeUpdate(sql);

            final Map<Option<?>, Object> map = _Collections.hashMap();
            map.put(AFFECTED_ROWS, affectedRows);

            return new JdbcResultStates(mergeOptionFunc(map, stmtCursor.sessionFunc));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    final void executeCursorClose(final JdbcStmtCursor stmtCursor) {

        try (Statement cursorStatement = stmtCursor.statement) {
            final String sql;
            sql = parseCursorClose(stmtCursor.safeName());

            printSqlIfNeed(this.factory, this.sessionName, getLogger(), sql);

            cursorStatement.clearWarnings();

            cursorStatement.executeUpdate(sql);

        } catch (Exception e) {
            throw handleException(e);
        }
    }


    String parseCursorFetch(String safeCursorName, Direction direction, @Nullable Long rowCount) {
        throw new UnsupportedOperationException("not Override");
    }

    String parseCursorMove(String safeCursorName, Direction direction, @Nullable Long rowCount) {
        throw new UnsupportedOperationException("not Override");
    }


    String parseCursorClose(String safeCursorName) {
        throw new UnsupportedOperationException("not Override");
    }

    /*################################## blow private method ##################################*/


    /**
     * @see #update(SimpleStmt, SyncStmtOption, Function)
     */
    private void putExecutorOptions(final @Nullable SQLWarning jdbcWarning, final Map<Option<?>, Object> map) {
        map.put(SERVER_META, this.factory.serverMeta);
        if (jdbcWarning != null) {
            map.put(WARNING, mapToArmyWarning(jdbcWarning));
        }
        final TransactionInfo info;
        info = obtainTransaction();
        if (info != null) {
            map.put(Option.IN_TRANSACTION, info.inTransaction());
            map.put(Option.READ_ONLY, info.isReadOnly());
        }
    }


    /**
     * @return a unmodified list
     * @see #batchUpdateList(BatchStmt, IntFunction, SyncStmtOption, LongConsumer, Function)
     */
    private List<Long> executeMultiStmtBatchUpdateAsLong(BatchStmt stmt, @Nullable IntFunction<List<Long>> listConstructor,
                                                         SyncStmtOption option, final @Nullable LongConsumer consumer) {
        final List<List<SQLParam>> groupList;
        groupList = stmt.groupList();
        if (groupList.get(0).size() > 0) {
            throw new IllegalArgumentException("stmt error");
        }

        try (Statement statement = this.conn.createStatement()) {

            bindStatementOption(statement, stmt, option);

            if (statement.execute(stmt.sqlText())) {
                statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
                // sql error
                throw _Exceptions.batchUpdateReturnResultSet();
            }

            final int stmtSize;
            stmtSize = groupList.size();
            final List<Long> resultList;
            if (listConstructor == null) {
                resultList = null;
            } else if ((resultList = listConstructor.apply(stmtSize)) == null) {
                throw _Exceptions.listConstructorError();
            }

            final boolean useLargeUpdate = this.factory.useLargeUpdate;
            long updateCount;
            int batchCount = 0;
            while (true) {
                if (useLargeUpdate) {
                    updateCount = statement.getLargeUpdateCount();
                } else {
                    updateCount = statement.getUpdateCount();
                }

                if (updateCount == -1L) {
                    // no more result
                    break;
                }
                if (consumer != null) {
                    consumer.accept(updateCount);
                }
                batchCount++;
                if (resultList != null) {
                    resultList.add(updateCount);
                }
                if (statement.getMoreResults()) {
                    statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
                    // sql error
                    throw _Exceptions.batchUpdateReturnResultSet();
                }

            }

            if (batchCount != stmtSize) {
                throw _Exceptions.batchCountNotMatch(stmtSize, batchCount);
            }
            return _Collections.safeUnmodifiableList(resultList);
        } catch (Exception e) {
            throw wrapError(e);
        }

    }


    /**
     * @return a unmodified list
     * @see #batchUpdateList(BatchStmt, IntFunction, SyncStmtOption, LongConsumer, Function)
     */
    private List<Long> executeBatchUpdateAsLong(BatchStmt stmt, @Nullable IntFunction<List<Long>> listConstructor,
                                                SyncStmtOption option, final @Nullable LongConsumer consumer)
            throws DataAccessException {

        try (final PreparedStatement statement = this.conn.prepareStatement(stmt.sqlText())) {

            for (List<SQLParam> group : stmt.groupList()) {
                bindParameters(statement, group);
                statement.addBatch();
            }

            bindStatementOption(statement, stmt, option);

            final List<Long> resultList;

            if (this.factory.useLargeUpdate) {
                final long[] affectedRowArray;
                affectedRowArray = statement.executeLargeBatch();
                if (listConstructor == null) {
                    resultList = null;
                } else if ((resultList = listConstructor.apply(affectedRowArray.length)) == null) {
                    throw _Exceptions.listConstructorError();
                }
                for (long affectedRow : affectedRowArray) {
                    if (consumer != null) {
                        consumer.accept(affectedRow);
                    }
                    if (resultList != null) {
                        resultList.add(affectedRow);
                    }
                }

            } else {
                final int[] affectedRowArray;
                affectedRowArray = statement.executeBatch();
                if (listConstructor == null) {
                    resultList = null;
                } else if ((resultList = listConstructor.apply(affectedRowArray.length)) == null) {
                    throw _Exceptions.listConstructorError();
                }

                for (long affectedRow : affectedRowArray) {
                    if (consumer != null) {
                        consumer.accept(affectedRow);
                    }
                    if (resultList != null) {
                        resultList.add(affectedRow);
                    }
                }
            }
            return _Collections.safeUnmodifiableList(resultList);
        } catch (Exception e) {
            throw wrapError(e);
        }

    }

    /**
     * @see #batchUpdate(BatchStmt, SyncStmtOption, Function)
     */
    private Stream<ResultStates> executeMultiStmtBatchUpdate(final BatchStmt stmt, final SyncStmtOption option,
                                                             final Function<Option<?>, ?> sessionFunc) {
        final List<List<SQLParam>> groupList;
        groupList = stmt.groupList();
        if (groupList.getFirst().size() > 0) {
            throw new IllegalArgumentException("stmt error");
        }

        try (Statement statement = this.conn.createStatement()) {

            bindStatementOption(statement, stmt, option);

            if (statement.execute(stmt.sqlText())) {
                statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
                // sql error
                throw _Exceptions.batchUpdateReturnResultSet();
            }

            final boolean useLargeUpdate = this.factory.useLargeUpdate;

            final int stmtSize;
            stmtSize = groupList.size();

            final Map<Option<?>, Object> optionMap = _Collections.hashMap();
            putExecutorOptions(statement.getWarnings(), optionMap);
            optionMap.put(BATCH_SIZE, stmtSize);
            optionMap.put(HAS_COLUMN, Boolean.FALSE);

            final List<ResultStates> resultList;
            resultList = _Collections.arrayList(stmtSize);

            Map<Option<?>, Object> resultOptionMap;

            long updateCount;
            int resultNo = 0;
            for (long affectedRows = -1; ; affectedRows = updateCount, resultNo++) {
                if (useLargeUpdate) {
                    updateCount = statement.getLargeUpdateCount();
                } else {
                    updateCount = statement.getUpdateCount();
                }

                if (affectedRows != -1) {
                    resultOptionMap = _Collections.hashMap(optionMap);

                    resultOptionMap.put(AFFECTED_ROWS, affectedRows);
                    resultOptionMap.put(RESULT_NO, resultNo);
                    resultOptionMap.put(BATCH_NO, resultNo);

                    resultOptionMap.put(HAS_MORE_RESULT, updateCount != -1);

                    resultList.add(new JdbcResultStates(mergeOptionFunc(resultOptionMap, sessionFunc)));
                }

                if (updateCount == -1L) {
                    // no more result
                    break;
                }

                if (statement.getMoreResults()) {
                    statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
                    // sql error
                    throw _Exceptions.batchUpdateReturnResultSet();
                }

            } // for loop

            if (resultNo != stmtSize) {
                throw _Exceptions.batchCountNotMatch(stmtSize, resultNo);
            }
            return resultList.stream();
        } catch (Exception e) {
            throw handleException(e);
        }

    }

    /**
     * @see #batchUpdate(BatchStmt, SyncStmtOption, Function)
     */
    private Stream<ResultStates> executeBatchUpdate(final BatchStmt stmt, final SyncStmtOption option,
                                                    final Function<Option<?>, ?> sessionFunc)
            throws DataAccessException {

        try (final PreparedStatement statement = this.conn.prepareStatement(stmt.sqlText())) {

            for (List<SQLParam> group : stmt.groupList()) {
                bindParameters(statement, group);
                statement.addBatch();
            }

            bindStatementOption(statement, stmt, option);

            final int batchSize;
            final IntToLongFunction arrayFunc;
            if (this.factory.useLargeUpdate) {
                final long[] affectedRowArray;
                affectedRowArray = statement.executeLargeBatch();
                batchSize = affectedRowArray.length;
                arrayFunc = index -> affectedRowArray[index];
            } else {
                final int[] affectedRowArray;
                affectedRowArray = statement.executeBatch();
                batchSize = affectedRowArray.length;
                arrayFunc = index -> affectedRowArray[index];
            }

            final Map<Option<?>, Object> optionMap = _Collections.hashMap();
            putExecutorOptions(statement.getWarnings(), optionMap);
            optionMap.put(BATCH_SIZE, batchSize);
            optionMap.put(HAS_COLUMN, Boolean.FALSE);

            final List<ResultStates> resultList = _Collections.arrayList(batchSize);

            Map<Option<?>, Object> resultOptionMap;
            long affectedRows;
            for (int i = 0, batchNo; i < batchSize; i++) {
                batchNo = i + 1;
                affectedRows = arrayFunc.applyAsLong(i);

                resultOptionMap = _Collections.hashMap(optionMap);

                resultOptionMap.put(AFFECTED_ROWS, affectedRows);
                resultOptionMap.put(RESULT_NO, batchNo);
                resultOptionMap.put(BATCH_NO, batchNo);

                resultOptionMap.put(HAS_MORE_RESULT, batchNo < batchSize);

                resultList.add(new JdbcResultStates(mergeOptionFunc(resultOptionMap, sessionFunc)));
            } // for loop
            return resultList.stream();
        } catch (Exception e) {
            throw wrapError(e);
        }

    }


    private DataType[] createSqlTypArray(final ResultSetMetaData metaData) throws SQLException {
        final DataType[] dataTypeArray = new DataType[metaData.getColumnCount()];
        final int columnCount = dataTypeArray.length;
        for (int i = 0; i < columnCount; i++) {
            dataTypeArray[i] = getDataType(metaData, i + 1);
        }
        return dataTypeArray;
    }

    private void readDataTypeArray(final ResultSetMetaData metaData, final DataType[] dataTypeArray) throws SQLException {
        final int columnCount = metaData.getColumnCount();
        if (columnCount != dataTypeArray.length) {
            throw _Exceptions.columnCountAndSelectionCountNotMatch(columnCount, dataTypeArray.length);
        }
        for (int i = 0; i < columnCount; i++) {
            dataTypeArray[i] = getDataType(metaData, i + 1);
        }

    }


    /**
     * @see #update(SimpleStmt, SyncStmtOption, Function)
     */
    private Statement bindStatement(final SimpleStmt stmt, final SyncStmtOption option)
            throws TimeoutException, SQLException {

        final List<SQLParam> paramGroup;
        paramGroup = stmt.paramGroup();

        final Statement statement;

        if (!option.isPreferServerPrepare() && paramGroup.size() == 0 && option.fetchSize() < 1) {
            statement = this.conn.createStatement();
        } else if (stmt instanceof GeneratedKeyStmt && stmt.selectionList().size() == 0) {
            statement = this.conn.prepareStatement(stmt.sqlText(), Statement.RETURN_GENERATED_KEYS);
        } else {
            statement = this.conn.prepareStatement(stmt.sqlText());
        }

        try {
            if (statement instanceof PreparedStatement) {
                bindParameters((PreparedStatement) statement, paramGroup);
            }

            bindStatementOption(statement, stmt, option);
            return statement;
        } catch (Exception e) {
            statement.close();
            throw handleException(e);
        } catch (Throwable e) {
            statement.close();
            throw e;
        }

    }


    /**
     * @see #bindStatement(SimpleStmt, SyncStmtOption)
     */
    private void bindStatementOption(final Statement statement, final SingleSqlStmt stmt,
                                     final SyncStmtOption option) throws SQLException {

        if (option.isSupportTimeout()) {
            statement.setQueryTimeout(option.restSeconds());
        }

        if (stmt.selectionList().size() > 0) {
            final int fetchSize = option.fetchSize();
            if (fetchSize > 0) {
                statement.setFetchSize(fetchSize);
                if (this instanceof PostgreExecutor
                        && this.factory.postgreFetchSizeAutoCommit
                        && this.conn.getAutoCommit()
                        && inTransaction(Option.EMPTY_FUNC)) {
                    // see org.postgresql.core.QueryExecutor.QUERY_FORWARD_CURSOR
                    // see org.postgresql.jdbc.PgStatement.executeInternal()
                    this.conn.setAutoCommit(false); // postgre command ,see io.army.jdbc.PostgreExecutor.handleAutoCommitAfterTransactionEndForPostgreFetchSize()
                }
            } else if (fetchSize == 0
                    && this instanceof MySQLExecutor
                    && option.isPreferClientStream()) {
                statement.setFetchSize(Integer.MIN_VALUE);
            }
        }

    }


    /**
     * @see #update(SimpleStmt, SyncStmtOption, Function)
     * @see #executeBatchQuery(BatchStmt, SyncStmtOption, Function, Function, boolean)
     */
    private void bindParameters(final PreparedStatement statement, final List<SQLParam> paramGroup)
            throws SQLException {

        final ServerMeta serverMeta = this.factory.serverMeta;
        final MappingEnv mappingEnv = this.factory.mappingEnv;
        final boolean truncatedTimeType = this.factory.truncatedTimeType;

        SQLParam sqlParam;
        Object value;
        MappingType type;
        TypeMeta typeMeta;
        DataType dataType;
        Iterator<?> rowParamIterator;
        List<?> rowParamList;
        final int paramSize = paramGroup.size();
        for (int i = 0, paramIndex = 1, rowParamSize; i < paramSize; i++) {
            sqlParam = paramGroup.get(i);

            typeMeta = sqlParam.typeMeta();
            if (typeMeta instanceof MappingType) {
                type = (MappingType) typeMeta;
            } else {
                type = typeMeta.mappingType();
            }
            dataType = type.map(serverMeta);

            if (sqlParam instanceof SingleParam) {
                rowParamList = null;
                rowParamIterator = null;
                rowParamSize = 1;
            } else if ((rowParamList = ((MultiParam) sqlParam).valueList()) instanceof ArrayList<?>) {
                rowParamSize = rowParamList.size();
                rowParamIterator = null;
            } else {
                rowParamSize = rowParamList.size();
                rowParamIterator = rowParamList.iterator();
            }

            for (int rowParamIndex = 0; rowParamIndex < rowParamSize; rowParamIndex++) {

                if (rowParamList == null) {
                    value = ((SingleParam) sqlParam).value();
                } else if (rowParamIterator == null) {
                    value = rowParamList.get(rowParamIndex);
                } else {
                    value = rowParamIterator.next();
                }

                if (value == null) { // jdbd client-prepared support dialect type null ,for example postgre : null::text
                    statement.setNull(paramIndex++, Types.NULL);
                    continue;
                } else if (value == void.class) { // void.class representing out parameter
                    if (!(statement instanceof java.sql.CallableStatement)) {
                        throw _Exceptions.voidClassSupportedByProcedure();
                    } else if (dataType instanceof SQLType) {
                        ((CallableStatement) statement).registerOutParameter(paramIndex++, mapToJdbcType((SQLType) dataType), dataType.typeName());
                    } else {
                        ((CallableStatement) statement).registerOutParameter(paramIndex++, Types.OTHER, dataType.typeName());
                    }
                    continue;
                }

                value = type.beforeBind(dataType, mappingEnv, value);

                if (truncatedTimeType && value instanceof Temporal && typeMeta instanceof FieldMeta) {
                    value = _TimeUtils.truncatedIfNeed(((FieldMeta<?>) typeMeta).scale(), (Temporal) value);
                }

                //TODO field codec

                bind(statement, paramIndex++, type, dataType, value);

            } // inner for loop

        } // outer for

    }


    /**
     * @see #bindParameters(PreparedStatement, List)
     */
    private int mapToJdbcType(final SQLType dataType) {
        final int jdbcType;
        switch (dataType.armyType()) {
            case BOOLEAN:
                jdbcType = Types.BOOLEAN;
                break;
            case TINYINT:
                jdbcType = Types.TINYINT;
                break;
            case TINYINT_UNSIGNED:
            case SMALLINT:
                jdbcType = Types.SMALLINT;
                break;
            case SMALLINT_UNSIGNED:
            case MEDIUMINT:
            case MEDIUMINT_UNSIGNED:
            case INTEGER:
                jdbcType = Types.INTEGER;
                break;
            case INTEGER_UNSIGNED:
            case BIGINT:
                jdbcType = Types.BIGINT;
                break;
            case BIGINT_UNSIGNED:
            case DECIMAL_UNSIGNED:
            case DECIMAL:
            case NUMERIC:
                jdbcType = Types.DECIMAL;
                break;
            case FLOAT:
                jdbcType = Types.FLOAT;
                break;
            case DOUBLE:
                jdbcType = Types.DOUBLE;
                break;
            case BIT:
            case VARBIT: {
                jdbcType = switch (this.factory.serverDatabase) {
                    case MySQL -> Types.BIGINT; // MySQL don't use BIT type
                    case PostgreSQL -> Types.OTHER; // Postgre don't use BIT type
                    default -> throw _Exceptions.unexpectedEnum(this.factory.serverDatabase);
                };
            }
            break;
            case CHAR:
                jdbcType = Types.CHAR;
                break;
            case VARCHAR:
            case TINYTEXT:
            case TEXT:
            case ENUM:
                jdbcType = Types.VARCHAR;
                break;
            case MEDIUMTEXT:
            case LONGTEXT:
                jdbcType = Types.LONGVARCHAR;
                break;
            case BINARY:
                jdbcType = Types.BINARY;
                break;
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
                jdbcType = Types.VARBINARY;
                break;
            case MEDIUMBLOB:
            case LONGBLOB:
                jdbcType = Types.LONGVARBINARY;
                break;
            case TIME:
                jdbcType = Types.TIME;
                break;
            case DATE:
            case YEAR:
            case YEAR_MONTH:
            case MONTH_DAY:
                jdbcType = Types.DATE;
                break;
            case TIMESTAMP:
                jdbcType = Types.TIMESTAMP;
                break;
            case TIME_WITH_TIMEZONE:
                jdbcType = Types.TIME_WITH_TIMEZONE;
                break;
            case TIMESTAMP_WITH_TIMEZONE:
                jdbcType = Types.TIMESTAMP_WITH_TIMEZONE;
                break;
            case XML:
                jdbcType = Types.SQLXML;
                break;
            case ARRAY:
                jdbcType = Types.ARRAY;
                break;
            case ROWID:
                jdbcType = Types.ROWID;
                break;
            case REF_CURSOR:
                jdbcType = Types.REF_CURSOR;
                break;
            case NULL:
                jdbcType = Types.NULL;
                break;
            case JSON:
            case JSONB:
            case DURATION:
            case PERIOD:
            case INTERVAL:
            case GEOMETRY:
            case COMPOSITE:
            case DIALECT_TYPE:
            case UNKNOWN:
            default:
                jdbcType = Types.OTHER;
        }
        return jdbcType;
    }


    /**
     * invoker must handle all error.
     *
     * @see #query(SingleSqlStmt, Function, SyncStmtOption, Function)
     */
    private <R> Stream<R> executeSimpleQuery(final SimpleStmt stmt, final SyncStmtOption option,
                                             final Function<? super CurrentRecord, R> function,
                                             final Function<Option<?>, ?> sessionFunc, final boolean resultItemStream)
            throws SQLException {

        Statement statement = null;
        ResultSet resultSet = null;

        try {

            statement = bindStatement(stmt, option);
            resultSet = jdbcExecuteQuery(statement, stmt.sqlText());

            final List<? extends Selection> selectionList = stmt.selectionList();

            final ResultSetMetaData metaData;
            metaData = resultSet.getMetaData();

            final DataType[] dataTypeArray = new DataType[selectionList.size()];
            readDataTypeArray(metaData, dataTypeArray);


            final JdbcCurrentRecord<R> currentRecord;
            currentRecord = new JdbcCurrentRecord<>(this, selectionList, dataTypeArray, function,
                    option.stateConsumer(), metaData, resultItemStream);

            final JdbcSimpleSpliterator<R> spliterator;
            spliterator = new JdbcSimpleSpliterator<>(true, statement, resultSet, currentRecord, stmt, option, sessionFunc);

            return assembleStream(spliterator, option);
        } catch (Throwable e) {
            closeResultSetAndStatement(resultSet, statement);
            throw e;
        }

    }


    /**
     * invoker must handle all error.
     *
     * @see #query(SingleSqlStmt, Function, SyncStmtOption, Function)
     */
    private <R> Stream<R> executeBatchQuery(BatchStmt stmt, SyncStmtOption option,
                                            Function<? super CurrentRecord, R> function,
                                            final Function<Option<?>, ?> sessionFunc,
                                            final boolean resultItemStream) throws SQLException {

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {

            statement = this.conn.prepareStatement(stmt.sqlText());

            bindParameters(statement, stmt.groupList().getFirst());

            // bind option
            bindStatementOption(statement, stmt, option);

            resultSet = statement.executeQuery();

            final List<? extends Selection> selectionList = stmt.selectionList();

            final ResultSetMetaData metaData;
            metaData = resultSet.getMetaData();

            final DataType[] dataTypeArray = new DataType[selectionList.size()];
            readDataTypeArray(metaData, dataTypeArray);

            final JdbcCurrentRecord<R> currentRecord;
            currentRecord = new JdbcCurrentRecord<>(this, selectionList, dataTypeArray, function, option.stateConsumer(),
                    metaData, resultItemStream);

            final BatchRowSpliterator<R> spliterator;
            spliterator = new BatchRowSpliterator<>(statement, currentRecord, stmt, option, resultSet, sessionFunc);

            return assembleStream(spliterator, option);
        } catch (Throwable e) {
            closeResultSetAndStatement(resultSet, statement);
            throw e;
        }
    }

    /**
     * invoker must handle all error.
     *
     * @see #query(SingleSqlStmt, Function, SyncStmtOption, Function)
     */
    private <R> Stream<R> executeMultiStmtBatchQuery(final BatchStmt stmt, SyncStmtOption option,
                                                     final Function<? super CurrentRecord, R> function,
                                                     final Function<Option<?>, ?> sessionFunc,
                                                     final boolean resultItemStream)
            throws SQLException, TimeoutException {

        final List<List<SQLParam>> groupList = stmt.groupList();

        if (groupList.getFirst().size() > 0) {
            throw new IllegalArgumentException("Batch stmt not multi-statement");
        }
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = this.conn.createStatement();

            bindStatementOption(statement, stmt, option);

            if (statement.execute(stmt.sqlText())) {
                resultSet = multiStatementNextResultSet(statement, 0, groupList.size());
            } else if (statement.getUpdateCount() != -1) {
                throw multiStatementPartNotQuery(0);
            } else {
                throw multiStatementLessThanExpected(0, groupList.size());
            }

            if (resultSet == null) {
                // no bug, never here
                throw multiStatementLessThanExpected(0, groupList.size());
            }

            final List<? extends Selection> selectionList = stmt.selectionList();

            final ResultSetMetaData metaData;
            metaData = resultSet.getMetaData();

            final DataType[] dataTypeArray = new DataType[selectionList.size()];
            readDataTypeArray(metaData, dataTypeArray);

            final JdbcCurrentRecord<R> currentRecord;
            currentRecord = new JdbcCurrentRecord<>(this, selectionList, dataTypeArray, function,
                    option.stateConsumer(), metaData, resultItemStream);

            final MultiSmtBatchRowSpliterator<R> spliterator;
            spliterator = new MultiSmtBatchRowSpliterator<>(statement, currentRecord, stmt, option, resultSet,
                    sessionFunc);

            return assembleStream(spliterator, option);
        } catch (Throwable e) {
            closeResultSetAndStatement(resultSet, statement);
            throw e;
        }
    }


    private <R> Stream<R> assembleStream(final JdbcRowSpliterator<R> spliterator, final SyncStmtOption option) {
        final Consumer<StreamCommander> consumer;
        consumer = option.commanderConsumer();
        if (consumer != null) {
            consumer.accept(spliterator::cancel); // cancel event
        }
        return StreamSupport.stream(spliterator, false)
                .onClose(spliterator::close); // close event
    }


    /**
     * @return row number
     */
    private int readRowId(final ResultSet idResultSet, final @Nullable long[] firstIdHolder,
                          final GeneratedKeyStmt stmt) throws SQLException {

        try (ResultSet resultSet = idResultSet) {

            final PrimaryFieldMeta<?> idField = stmt.idField();
            final MappingType type = idField.mappingType();
            final int idSelectionIndex = stmt.idSelectionIndex();
            final int idColumnIndexBaseOne;
            if (idSelectionIndex < 0) {
                assert stmt.selectionList().size() == 0;
                idColumnIndexBaseOne = 1;
            } else {
                idColumnIndexBaseOne = idSelectionIndex + 1;
            }

            final DataType sqlType;
            sqlType = getDataType(resultSet.getMetaData(), idColumnIndexBaseOne);

            final MappingEnv env = this.factory.mappingEnv;
            final int rowSize = stmt.rowSize();
            final boolean oneRowWithConflict = rowSize == 1 && stmt.hasConflictClause();

            Object idValue;
            int rowIndex = 0;

            for (; resultSet.next(); rowIndex++) {
                if (rowIndex >= rowSize) {
                    if (oneRowWithConflict) {
                        continue;
                    }
                    throw _Exceptions.insertedRowsAndGenerateIdNotMatch(rowSize, rowIndex + 1);
                }
                idValue = get(resultSet, idColumnIndexBaseOne, type, sqlType);
                if (idValue == null) {
                    throw _Exceptions.idValueIsNull(rowIndex, idField);
                }
                if (rowIndex == 0 && firstIdHolder != null) {
                    if (idValue instanceof Long || idValue instanceof Integer) {
                        firstIdHolder[0] = ((Number) idValue).longValue();
                    } else if (idValue instanceof BigInteger && UnsignedBigintType.MAX_VALUE.compareTo((BigInteger) idValue) >= 0) {
                        firstIdHolder[0] = ((BigInteger) idValue).longValueExact();
                    } else {
                        String m = String.format("database server auto increment id type %s is unsupported by army", idValue.getClass().getSimpleName());
                        throw new DataAccessException(m);
                    }
                }
                idValue = type.afterGet(sqlType, env, idValue);
                stmt.setGeneratedIdValue(rowIndex, idValue);
            }
            if (rowIndex != rowSize && !(oneRowWithConflict && rowIndex == 2)) {
                throw _Exceptions.insertedRowsAndGenerateIdNotMatch(rowSize, rowIndex);
            }
            return rowIndex;
        }
    }



    /*################################## blow static method ##################################*/


    static ArmyException wrapError(final Throwable cause) {
        final ArmyException e;
        if (cause instanceof ArmyException) {
            e = (ArmyException) cause;
        } else if (cause instanceof SQLException) {
            final SQLException se = (SQLException) cause;
            // TODO convert to  ServerException
            final String message = String.format("SqlState : %s ,errorCode : %s\n%s",
                    se.getSQLState(), se.getErrorCode(), se.getMessage());
            e = new DriverException(message, cause, se.getSQLState(), se.getErrorCode());
        } else {
            e = _Exceptions.unknownError(cause);
        }
        return e;
    }

    static ArmyException wrapException(final Exception cause) {
        final ArmyException error;
        if (cause instanceof ArmyException) {
            error = (ArmyException) cause;
        } else if (cause instanceof SQLException) {
            final SQLException se = (SQLException) cause;
            // TODO convert to  ServerException
            final String message = String.format("SqlState : %s ,errorCode : %s\n%s",
                    se.getSQLState(), se.getErrorCode(), se.getMessage());
            error = new DriverException(message, cause, se.getSQLState(), se.getErrorCode());
        } else {
            error = new DataAccessException(cause);
        }
        return error;
    }

    /*-------------------below private static methods -------------------*/

    private static Function<Option<?>, ?> mergeOptionFunc(final Map<Option<?>, Object> map,
                                                          final Function<Option<?>, ?> sessionFunc) {
        final Function<Option<?>, ?> mapFunc, optionFunc;
        mapFunc = map::get;

        if (sessionFunc == Option.EMPTY_FUNC) {
            optionFunc = mapFunc;
        } else {
            optionFunc = option -> {
                final Object value;
                value = sessionFunc.apply(option);
                if (value != null) {
                    return value;
                }
                return mapFunc.apply(option);
            };
        }
        return optionFunc;
    }


    private static NullPointerException actionIsNull() {
        return new NullPointerException("Action consumer is null");
    }


    /**
     * <p>Invoke {@link PreparedStatement#executeQuery()} or {@link Statement#executeQuery(String)} for {@link ResultSet} auto close.
     */
    private static ResultSet jdbcExecuteQuery(final Statement statement, final String sql) throws SQLException {
        final ResultSet resultSet;
        if (statement instanceof PreparedStatement) {
            resultSet = ((PreparedStatement) statement).executeQuery();
        } else {
            resultSet = statement.executeQuery(sql);
        }
        return resultSet;
    }




    /*-------------------below static class -------------------*/


    private static class JdbcCurrentRecord<R> extends ArmyStmtCurrentRecord {

        private final JdbcExecutor executor;

        private final List<? extends Selection> selectionList;

        private final DataType[] dataTypeArray;

        private final Function<? super CurrentRecord, R> function;

        private final Consumer<ResultStates> consumer;

        private final boolean resultItemStream;

        private final boolean dontNeedToCreateStates;

        private final JdbcStmtRecordMeta recordMeta;

        private final MappingType[] rawTypeArray;

        private final MappingType[] compatibleTypeArray;

        private long rowNumber = 0L;

        private ResultSet resultSet;


        private JdbcCurrentRecord(JdbcExecutor executor, List<? extends Selection> selectionList,
                                  DataType[] dataTypeArray, Function<? super CurrentRecord, R> function,
                                  Consumer<ResultStates> consumer, ResultSetMetaData meta, boolean resultItemStream) {
            this.executor = executor;
            this.selectionList = selectionList;
            this.dataTypeArray = dataTypeArray;
            this.function = function;

            this.consumer = consumer;
            this.resultItemStream = resultItemStream;
            this.dontNeedToCreateStates = !resultItemStream && consumer == ResultStates.IGNORE_STATES;
            this.rawTypeArray = createRawTypeArray(selectionList);
            this.compatibleTypeArray = new MappingType[dataTypeArray.length];
            this.recordMeta = new JdbcStmtRecordMeta(1, executor, dataTypeArray, selectionList, meta);
        }

        private JdbcCurrentRecord(JdbcCurrentRecord<R> r, ResultSetMetaData meta) {
            this.executor = r.executor;
            this.selectionList = r.selectionList;
            this.dataTypeArray = r.dataTypeArray;
            this.function = r.function;

            this.consumer = r.consumer;
            this.resultItemStream = r.resultItemStream;
            this.dontNeedToCreateStates = r.dontNeedToCreateStates;
            this.rawTypeArray = r.rawTypeArray;
            this.compatibleTypeArray = r.compatibleTypeArray;
            this.recordMeta = new JdbcStmtRecordMeta(r.recordMeta.resultNo() + 1, r.executor, r.dataTypeArray, r.selectionList, meta);
        }


        @Override
        public final ArmyResultRecordMeta getRecordMeta() {
            return this.recordMeta;
        }

        @Override
        public final long rowNumber() {
            return this.rowNumber;
        }

        @Nullable
        @Override
        public final Object get(final int indexBasedZero) {
            this.recordMeta.checkIndex(indexBasedZero);

            return readOneColumn(indexBasedZero, null, null);
        }

        @Nullable
        @Override
        public Object get(final int indexBasedZero, final @Nullable MappingType type) {
            if (type == null) {
                throw new NullPointerException();
            }
            this.recordMeta.checkIndex(indexBasedZero);
            return readOneColumn(indexBasedZero, type, null);
        }

        @SuppressWarnings("unchecked")
        @Nullable
        @Override
        public <T> T get(final int indexBasedZero, final @Nullable Class<T> columnClass) {
            if (columnClass == null) {
                throw new NullPointerException();
            }
            this.recordMeta.checkIndex(indexBasedZero);

            MappingType type;
            type = this.compatibleTypeArray[indexBasedZero];
            if (type == null) {
                type = this.rawTypeArray[indexBasedZero];
            }
            DataType dataType = null;
            if (!columnClass.isAssignableFrom(type.javaType())) {
                dataType = this.dataTypeArray[indexBasedZero];
                type = type.compatibleFor(dataType, columnClass);
                this.compatibleTypeArray[indexBasedZero] = type;
            }
            return (T) readOneColumn(indexBasedZero, type, dataType);
        }



        /*-------------------below protected -------------------*/

        @Override
        protected final Object[] copyValueArray() {
            final MappingType[] typeArray = this.rawTypeArray;
            final int length = typeArray.length;

            final Object[] valueArray = new Object[length];

            for (int i = 0; i < length; i++) {
                valueArray[i] = readOneColumn(i, typeArray[i], null);
            }
            return valueArray;
        }


        /*-------------------below package methods -------------------*/

        @Nullable
        R readOneRow(final ResultSet resultSet) {
            this.resultSet = resultSet; // firstly , store

            try {
                final R row;
                row = this.function.apply(this);
                if (row instanceof CurrentRecord) {
                    throw _Exceptions.recordFuncError(this.function, this);
                }
                this.rowNumber++;
                return row;
            } finally {
                this.resultSet = null;  // clear
            }
        }


        @Nullable
        private Object readOneColumn(final int indexBasedZero, @Nullable MappingType type, @Nullable DataType dataType) {
            if (dataType == null) {
                dataType = this.dataTypeArray[indexBasedZero];
            }

            if (type == null) {
                type = this.rawTypeArray[indexBasedZero];
            }
            final ResultSet resultSet = this.resultSet;
            assert resultSet != null;

            try {

                Object value;
                value = this.executor.get(resultSet, indexBasedZero + 1, type, dataType);

                if (value == null) {
                    return null;
                }

                // TODO 解密 ,脱敏

                value = type.afterGet(dataType, this.executor.factory.mappingEnv, value);
                if (value == MappingType.DOCUMENT_NULL_VALUE) {
                    if (!(type instanceof MappingType.SqlDocumentType)) {
                        throw afterGetMethodError(type, dataType, value);
                    }
                    value = null;
                }
                return value;
            } catch (Exception e) {
                final DataAccessException error;
                error = _Exceptions.columnGetError(indexBasedZero, getColumnLabel(indexBasedZero), e);
                throw this.executor.handleException(error);
            }
        }


        private void acceptResultStates(@Nullable ResultStates states) {
            if (states == null) {
                return;
            }
            final Consumer<ResultStates> statesConsumer = this.consumer;
            if (statesConsumer == null || statesConsumer == ResultStates.IGNORE_STATES || this.resultItemStream) {
                return;
            }

            try {
                statesConsumer.accept(states);
            } catch (Exception e) {
                throw _Exceptions.statesConsumerInvokeError(statesConsumer, e);
            }

        }


    } // JdbcCurrentRecord


    private static abstract class JdbcRowSpliterator<R> implements Spliterator<R> {

        final JdbcExecutor executor;

        final SyncStmtOption stmtOption;

        final Function<Option<?>, ?> sessionFunc;

        private boolean closed;

        boolean canceled;

        private Runnable closeListener;

        private JdbcRowSpliterator(JdbcExecutor executor, SyncStmtOption stmtOption, Function<Option<?>, ?> sessionFunc) {
            this.executor = executor;
            this.stmtOption = stmtOption;
            this.sessionFunc = sessionFunc;
        }

        @Override
        public final boolean tryAdvance(final @Nullable Consumer<? super R> action) {
            if (this.closed) {
                return false;
            }
            try {
                if (action == null) {
                    throw actionIsNull();
                }
                return readRowStream(1, action);
            } catch (Exception e) {
                throw handleException(e);
            } catch (Error e) {
                handleError(e);
                throw e;
            }
        }


        @Override
        public final void forEachRemaining(final @Nullable Consumer<? super R> action) {
            if (this.closed) {
                return;
            }

            try {
                if (action == null) {
                    throw actionIsNull();
                }
                readRowStream(0, action);
            } catch (Exception e) {
                throw handleException(e);
            } catch (Error e) {
                handleError(e);
                throw e;
            }
        }


        @Nullable
        @Override
        public final Spliterator<R> trySplit() {
            final int splitSize = this.stmtOption.splitSize();
            if (this.closed || this.canceled || splitSize < 1) {
                return null;
            }

            final List<R> itemList;
            itemList = _Collections.arrayList(Math.min(300, splitSize));

            try {
                readRowStream(splitSize, itemList::add);
            } catch (Exception e) {
                throw handleException(e);
            } catch (Error e) {
                handleError(e);
                throw e;
            }

            final Spliterator<R> spliterator;
            if (itemList.size() == 0) {
                spliterator = null;
            } else {
                spliterator = itemList.spliterator();
            }
            return spliterator;
        }


        @Override
        public final long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public final int characteristics() {
            return 0;
        }

        abstract boolean readRowStream(final int readSize, final Consumer<? super R> action) throws SQLException;

        abstract void doCloseStream();


        ArmyException handleException(Exception cause) {
            close();
            return this.executor.handleException(cause);
        }


        void handleError(Error cause) {
            close();
        }


        final void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;

            final Runnable closeListener = this.closeListener;
            if (closeListener == null) {
                doCloseStream();
            } else {
                try {
                    closeListener.run();
                } catch (Throwable e) {
                    // ignore, not bug ,no error, simple closeListener
                } finally {
                    doCloseStream();
                }
            }


            final JdbcExecutor executor = this.executor;

            final Logger logger;
            logger = executor.getLogger();
            if (logger.isTraceEnabled()) {
                logger.trace("session[name : {} , executorHash : {}]\nResultItem stream have closed", executor.sessionName,
                        System.identityHashCode(executor)
                );
            }

        }


        final void addCloseListener(final Runnable listener) {
            final Runnable currentListener = this.closeListener;
            if (currentListener == null) {
                this.closeListener = listener;
            } else {
                this.closeListener = () -> {
                    currentListener.run();
                    listener.run();
                };
            }
        }


        private void cancel() {
            this.canceled = true;
        }


    } // JdbcRowSpliterator

    /**
     * <p>This class is responsible for spite rows from {@link ResultSet} to {@link Stream} with {@link #readRowStream(int, Consumer)} method.
     * <p>This class is base class of following
     * <ul>
     *     <li>{@link JdbcSimpleSpliterator}</li>
     *     <li>{@link JdbcBatchSpliterator}</li>
     * </ul>
     *
     * @param <R> row java type
     */
    private static abstract class JdbcStmtRowSpliterator<R> extends JdbcRowSpliterator<R> {

        final JdbcExecutor executor;

        final Statement statement;

        final int fetchSize;

        private final SyncStmtOption option;

        private final StmtType stmtType;

        private int currentFetchRows;


        private JdbcStmtRowSpliterator(JdbcExecutor executor, Statement statement, StmtType stmtType,
                                       SyncStmtOption option, Function<Option<?>, ?> sessionFunc) {
            super(executor, option, sessionFunc);
            this.executor = executor;
            this.statement = statement;
            this.option = option;
            this.stmtType = stmtType;

            this.fetchSize = option.fetchSize();
            assert this.fetchSize > -1;
        }


        /**
         * <p>Read one fetch,if fetchSize is 0 ,read all row.
         *
         * @param readSize 0 or positive
         */
        @SuppressWarnings("unchecked")
        final long readRowSet(final ResultSet resultSet, final long totalCount,
                              final JdbcCurrentRecord<R> currentRecord, final int readSize,
                              final Consumer<? super R> action) throws SQLException {
            assert this.fetchSize < 1;

            final int maxValue = Integer.MAX_VALUE;

            int readRowCount = 0;
            long bigReadCount = 0L;
            while (resultSet.next()) {

                if (readRowCount == 0 && bigReadCount == 0 && totalCount == 0 && currentRecord.resultItemStream) {
                    action.accept((R) currentRecord.recordMeta);
                }

                action.accept(currentRecord.readOneRow(resultSet));
                readRowCount++;

                if (readSize > 0 && readRowCount == readSize) {
                    break;
                }

                if (this.canceled) { // canceled must after readRowCount++; because of OptimisticLockException
                    break;
                }

                if (readRowCount == maxValue) {
                    bigReadCount += readRowCount;
                    readRowCount = 0;
                }

            } // while loop

            bigReadCount += readRowCount;
            return bigReadCount;
        }


        @SuppressWarnings("unchecked")
        final long readWithFetchSize(final ResultSet resultSet, final int batchSize, final int batchNo,
                                     final long totalRowCount, final JdbcCurrentRecord<R> currentRecord,
                                     final int readSize, final Consumer<? super R> action) throws SQLException {

            final int fetchSize = this.fetchSize, maxIntValue = Integer.MAX_VALUE;
            assert fetchSize > 0;

            int readRowCount = 0, currentFetchRows = this.currentFetchRows;
            long bigReadCount = 0L;
            boolean meetReadSize = false;
            while (resultSet.next()) {

                if (currentFetchRows == fetchSize) {
                    emitMoreFetchStates(currentRecord, batchSize, batchNo, currentFetchRows, true, action);
                    currentFetchRows = 0;
                }

                if (readRowCount == 0 && bigReadCount == 0 && totalRowCount == 0 && currentRecord.resultItemStream) {
                    action.accept((R) currentRecord.recordMeta);
                }

                action.accept(currentRecord.readOneRow(resultSet));
                currentFetchRows++;
                readRowCount++;

                if (this.canceled) { // canceled must after readRowCount++; because of OptimisticLockException
                    break;
                }

                if (readSize > 0 && readRowCount == readSize) {
                    meetReadSize = true;
                    break;
                }


                if (readRowCount == maxIntValue) {
                    bigReadCount += readRowCount;
                    readRowCount = 0;
                }


            } // while loop

            if (!meetReadSize && !this.canceled) {
                emitMoreFetchStates(currentRecord, batchSize, batchNo, currentFetchRows, false, action);
            }

            bigReadCount += readRowCount;
            this.currentFetchRows = currentFetchRows;
            return bigReadCount;
        }


        @SuppressWarnings("unchecked")
        final void emitSingleResultStates(final JdbcCurrentRecord<?> currentRecord, final long rowCount,
                                          final Consumer<? super R> action) {
            if (currentRecord.dontNeedToCreateStates) {
                currentRecord.acceptResultStates(null);
                return;
            }

            try {
                final long affectedRows;
                if (this.stmtType == StmtType.QUERY) {
                    affectedRows = 0L;
                } else {
                    affectedRows = rowCount;
                }

                final Map<Option<?>, Object> optionMap = _Collections.hashMap();
                optionMap.put(AFFECTED_ROWS, affectedRows);
                optionMap.put(ROW_COUNT, rowCount);
                optionMap.put(HAS_COLUMN, Boolean.TRUE);

                this.executor.putExecutorOptions(this.statement.getWarnings(), optionMap);

                final Function<Option<?>, ?> optionFunc;
                optionFunc = mergeOptionFunc(optionMap, this.sessionFunc);

                final ResultStates states = new JdbcResultStates(optionFunc);
                if (currentRecord.resultItemStream) {
                    action.accept((R) states);
                }
                currentRecord.acceptResultStates(states);
            } catch (Exception e) {
                throw handleException(e);
            } catch (Error e) {
                handleError(e);
                throw e;
            }
        }


        @SuppressWarnings("unchecked")
        final void emitMultiResultStates(final JdbcCurrentRecord<?> currentRecord, final int resultNo,
                                         final long rowCount, final boolean moreResult, final Consumer<? super R> action) {
            if (currentRecord.dontNeedToCreateStates) {
                currentRecord.acceptResultStates(null);
                return;
            }

            try {
                final long affectedRows;
                if (this.stmtType == StmtType.QUERY) {
                    affectedRows = 0L;
                } else {
                    affectedRows = rowCount;
                }

                final Map<Option<?>, Object> optionMap = _Collections.hashMap();
                optionMap.put(RESULT_NO, resultNo);
                optionMap.put(AFFECTED_ROWS, affectedRows);
                optionMap.put(ROW_COUNT, rowCount);
                optionMap.put(HAS_MORE_RESULT, moreResult);
                optionMap.put(HAS_COLUMN, Boolean.TRUE);

                this.executor.putExecutorOptions(this.statement.getWarnings(), optionMap);

                final Function<Option<?>, ?> optionFunc;
                optionFunc = mergeOptionFunc(optionMap, this.sessionFunc);

                final ResultStates states = new JdbcResultStates(optionFunc);
                if (currentRecord.resultItemStream) {
                    action.accept((R) states);
                }
                currentRecord.acceptResultStates(states);
            } catch (Exception e) {
                throw handleException(e);
            } catch (Error e) {
                handleError(e);
                throw e;
            }
        }

        @SuppressWarnings("unchecked")
        final void emitBatchQueryStates(final JdbcCurrentRecord<?> currentRecord, final int batchSize,
                                        final int batchNo, final long rowCount, final Consumer<? super R> action) {
            if (currentRecord.dontNeedToCreateStates) {
                currentRecord.acceptResultStates(null);
                return;
            }

            try {
                final long affectedRows;
                if (this.stmtType == StmtType.QUERY) {
                    affectedRows = 0L;
                } else {
                    affectedRows = rowCount;
                }
                final Map<Option<?>, Object> optionMap = _Collections.hashMap();
                optionMap.put(AFFECTED_ROWS, affectedRows);
                optionMap.put(ROW_COUNT, rowCount);

                optionMap.put(BATCH_SIZE, batchSize);
                optionMap.put(BATCH_NO, batchNo);
                optionMap.put(HAS_COLUMN, Boolean.TRUE);

                this.executor.putExecutorOptions(this.statement.getWarnings(), optionMap);

                final Function<Option<?>, ?> optionFunc;
                optionFunc = mergeOptionFunc(optionMap, this.sessionFunc);

                final ResultStates states;
                states = new JdbcResultStates(optionFunc);

                if (currentRecord.resultItemStream) {
                    action.accept((R) states);
                }
                currentRecord.acceptResultStates(states);
            } catch (Exception e) {
                throw handleException(e);
            } catch (Error e) {
                handleError(e);
                throw e;
            }
        }


        @SuppressWarnings("unchecked")
        final void emitMoreFetchStates(final JdbcCurrentRecord<?> currentRecord, final int batchSize,
                                       final int batchNo, final int fetchRows,
                                       final boolean moreFetch, final Consumer<? super R> action) {

            if (currentRecord.dontNeedToCreateStates) {
                currentRecord.acceptResultStates(null);
                return;
            }

            try {
                final long affectedRows;
                if (this.stmtType == StmtType.QUERY) {
                    affectedRows = 0L;
                } else {
                    affectedRows = fetchRows;
                }

                final Map<Option<?>, Object> optionMap = _Collections.hashMap();
                optionMap.put(AFFECTED_ROWS, affectedRows);
                optionMap.put(ROW_COUNT, (long) fetchRows);
                optionMap.put(HAS_MORE_FETCH, moreFetch); // not multi result
                optionMap.put(HAS_COLUMN, Boolean.TRUE);

                if (batchSize > 0) {
                    optionMap.put(BATCH_SIZE, batchSize);
                    optionMap.put(BATCH_NO, batchNo);
                }

                this.executor.putExecutorOptions(this.statement.getWarnings(), optionMap);

                final Function<Option<?>, ?> optionFunc;
                optionFunc = mergeOptionFunc(optionMap, this.sessionFunc);

                final ResultStates states;
                states = new JdbcResultStates(optionFunc);

                if (currentRecord.resultItemStream) {
                    action.accept((R) states);
                }
                currentRecord.acceptResultStates(states);
            } catch (Exception e) {
                throw handleException(e);
            } catch (Error e) {
                handleError(e);
                throw e;
            }
        }


    } // JdbcRowSpliterator


    private static final class JdbcSimpleSpliterator<R> extends JdbcStmtRowSpliterator<R> {

        final boolean needCloseStatement;

        final Statement statement;

        final SingleSqlStmt stmt;

        final ResultSet resultSet;

        final JdbcCurrentRecord<R> currentRecord;

        final boolean hasOptimistic;

        private long totalRowCount = 0L;


        private JdbcSimpleSpliterator(boolean needCloseStatement, Statement statement, ResultSet resultSet,
                                      JdbcCurrentRecord<R> currentRecord, SingleSqlStmt stmt,
                                      SyncStmtOption option, Function<Option<?>, ?> sessionFunc) {
            super(currentRecord.executor, statement, stmt.stmtType(), option, sessionFunc);
            this.needCloseStatement = needCloseStatement;
            this.statement = statement;
            this.stmt = stmt;
            this.resultSet = resultSet;
            this.currentRecord = currentRecord;

            this.hasOptimistic = stmt.hasOptimistic();

        }


        @Override
        void doCloseStream() {
            if (this.needCloseStatement) {
                closeResultSetAndStatement(this.resultSet, this.statement);
            } else {
                closeResource(this.resultSet);
            }
        }


        @Override
        boolean readRowStream(final int readSize, final Consumer<? super R> action) throws SQLException {
            final int fetchSize = this.fetchSize;
            final long originalTotalRowCount = this.totalRowCount, readRowCount;

            if (fetchSize > 0) {
                readRowCount = readWithFetchSize(this.resultSet, -1, 0, originalTotalRowCount, this.currentRecord, readSize, action);
            } else {
                readRowCount = readRowSet(this.resultSet, originalTotalRowCount, this.currentRecord, readSize, action);
            }

            if (readRowCount > 0) {
                this.totalRowCount += readRowCount;
            } else if (this.hasOptimistic && originalTotalRowCount == 0L) {
                throw _Exceptions.optimisticLock();
            }

            if (this.canceled) {
                close();
            } else if (fetchSize < 1
                    && (readSize == 0 || (readSize > 0 && readRowCount < readSize))) {
                // readRowSet() dont' emit ResultStates,so here emit
                emitSingleResultStates(this.currentRecord, this.totalRowCount, action);
                close();
            }
            return readRowCount > 0;
        }


    } // JdbcSimpleSpliterator


    /**
     * <p>This class is responsible for spite rows from multi {@link ResultSet} to {@link Stream} with {@link #readRowStream(int, Consumer)} method.
     * <p>This class is base class of following
     * <ul>
     *     <li>{@link BatchRowSpliterator}</li>
     *     <li>{@link MultiSmtBatchRowSpliterator}</li>
     * </ul>
     *
     * @param <R> row java type
     */
    private static abstract class JdbcBatchSpliterator<R> extends JdbcStmtRowSpliterator<R> {

        final Statement statement;

        final BatchStmt stmt;

        final SyncStmtOption option;

        JdbcCurrentRecord<R> currentRecord;

        private ResultSet resultSet;

        private int batchNo = 1; // from 1 not 0

        private long currentResultTotalRows = 0L;

        private JdbcBatchSpliterator(Statement statement, JdbcCurrentRecord<R> currentRecord,
                                     BatchStmt stmt, SyncStmtOption option, ResultSet resultSet,
                                     Function<Option<?>, ?> sessionFunc) {
            super(currentRecord.executor, statement, stmt.stmtType(), option, sessionFunc);

            this.statement = statement;
            this.currentRecord = currentRecord;
            this.stmt = stmt;
            this.option = option;

            this.resultSet = resultSet;

        }


        @Override
        final void doCloseStream() {
            final ResultSet resultSet = this.resultSet;
            if (resultSet == null) {
                closeResource(this.statement);
            } else {
                this.resultSet = null; // firstly clear
                closeResultSetAndStatement(resultSet, this.statement);
            }

        }


        @SuppressWarnings("unchecked")
        @Override
        final boolean readRowStream(final int readSize, final Consumer<? super R> action) throws SQLException {
            final boolean hasOptimistic = this.stmt.hasOptimistic();
            final int batchSize;
            if (this instanceof BatchRowSpliterator) {
                batchSize = this.stmt.groupList().size();
            } else {
                batchSize = 0; // multi-statement
            }

            JdbcCurrentRecord<R> currentRecord = this.currentRecord;

            ResultSet resultSet = this.resultSet;
            long multiSetRowCount = 0, readCount, currentResultTotalRows = this.currentResultTotalRows;
            for (int restReadSize = readSize; resultSet != null; ) {

                if (currentRecord.resultItemStream && currentResultTotalRows == 0) {
                    action.accept((R) currentRecord.recordMeta);
                }

                if (batchSize > 0 && this.fetchSize > 0) {
                    readCount = readWithFetchSize(resultSet, batchSize, this.batchNo, currentResultTotalRows, currentRecord, restReadSize, action);
                } else {
                    readCount = readRowSet(resultSet, currentResultTotalRows, currentRecord, restReadSize, action);
                }

                if (readCount > 0) {
                    multiSetRowCount += readCount;
                    currentResultTotalRows += readCount;
                    if (readSize > 0 && restReadSize == readCount) {
                        break;
                    }
                    if (readSize > 0) {
                        restReadSize -= ((int) readCount);
                    }
                } else if (hasOptimistic && currentResultTotalRows == 0L) {
                    throw _Exceptions.optimisticLock();
                }

                this.resultSet = null; // firstly clear
                closeResource(resultSet); // secondly close

                if (this.canceled) {
                    break;
                }

                this.resultSet = resultSet = nextResultSet();

                if (this.fetchSize < 1) {
                    if (batchSize > 0) { // emit ResultStates
                        emitBatchQueryStates(currentRecord, batchSize, this.batchNo, currentResultTotalRows, action);
                    } else {
                        emitMultiResultStates(currentRecord, this.batchNo, currentResultTotalRows, resultSet != null, action);
                    }
                }

                if (resultSet == null) {
                    break;
                }

                // reset for next result set
                this.batchNo++;
                currentResultTotalRows = 0L;

                this.currentRecord = currentRecord = new JdbcCurrentRecord<>(currentRecord, resultSet.getMetaData());

                assert currentRecord.resultNo() == this.batchNo;

            } // for loop

            this.currentResultTotalRows = currentResultTotalRows;

            if (readSize == 0 || (readSize > 0 && multiSetRowCount < readSize) || this.canceled) {
                close();
            }
            return multiSetRowCount > 0;
        }


        @Nullable
        abstract ResultSet nextResultSet() throws SQLException, TimeoutException;


    } // BatchJdbcSpliterator


    private static final class BatchRowSpliterator<R> extends JdbcBatchSpliterator<R> {

        private int groupIndex = 1; // here from 1 not 0.


        private BatchRowSpliterator(PreparedStatement statement, JdbcCurrentRecord<R> rowReader,
                                    BatchStmt stmt, SyncStmtOption option, ResultSet resultSet,
                                    Function<Option<?>, ?> sessionFunc) {
            super(statement, rowReader, stmt, option, resultSet, sessionFunc);

        }


        @Nullable
        ResultSet nextResultSet() throws SQLException, TimeoutException {
            if (this.canceled) {
                return null;
            }
            final BatchStmt stmt = this.stmt;

            final List<List<SQLParam>> paramGroupList = stmt.groupList();
            final int groupIndex = this.groupIndex++; // groupIndex from 1 not 0

            if (groupIndex >= paramGroupList.size()) {
                // here don't close statement, see close()
                return null;
            }
            final JdbcExecutor executor = this.currentRecord.executor;
            final PreparedStatement statement = (PreparedStatement) this.statement;

            statement.clearParameters();
            statement.clearWarnings();
            executor.bindParameters(statement, paramGroupList.get(groupIndex));
            executor.bindStatementOption(statement, stmt, this.option);

            return statement.executeQuery();
        }


    } //BatchRowSpliterator


    private static final class JdbcCursorRowSpliterator<R> extends JdbcRowSpliterator<R> {

        private final SQLWarning jdbcWarning;

        private final ResultSet resultSet;

        private final JdbcCurrentRecord<R> currentRecord;

        private long totalRowCount;

        private JdbcCursorRowSpliterator(@Nullable SQLWarning jdbcWarning, SyncStmtOption stmtOption, ResultSet resultSet,
                                         JdbcCurrentRecord<R> currentRecord, Function<Option<?>, ?> sessionFunc) {
            super(currentRecord.executor, stmtOption, sessionFunc);
            this.jdbcWarning = jdbcWarning;
            this.resultSet = resultSet;
            this.currentRecord = currentRecord;
        }


        @SuppressWarnings("unchecked")
        boolean readRowStream(final int readSize, final Consumer<? super R> action) throws SQLException {
            final int maxValue = Integer.MAX_VALUE;
            final ResultSet resultSet = this.resultSet;
            final JdbcCurrentRecord<R> currentRecord = this.currentRecord;

            final long originalTotalRowCount = this.totalRowCount;

            int readRowCount = 0;
            long bigReadCount = originalTotalRowCount;
            boolean meetReadSize = false;
            while (resultSet.next()) {

                if (readRowCount == 0 && bigReadCount == 0L && currentRecord.resultItemStream) {
                    action.accept((R) currentRecord.getRecordMeta());
                }

                action.accept(currentRecord.readOneRow(resultSet));

                readRowCount++;


                if (this.canceled) {
                    break;
                }

                if (readSize > 0 && readRowCount == readSize) {
                    meetReadSize = true;
                    break;
                }

                if (readRowCount == maxValue) {
                    bigReadCount += readRowCount;
                    readRowCount = 0;
                }

            } // while loop

            this.totalRowCount = bigReadCount += readRowCount;

            if (!meetReadSize) {
                // ResultSet end or canceled
                if (!this.canceled) {
                    emitResultStates(bigReadCount, action);
                }

                close();
            }

            return bigReadCount > originalTotalRowCount;
        }


        @Override
        void doCloseStream() {
            closeResource(this.resultSet);
        }


        @SuppressWarnings("unchecked")
        private void emitResultStates(final long totalRowCount, final Consumer<? super R> action) {

            final JdbcCurrentRecord<?> currentRecord = this.currentRecord;
            if (currentRecord.dontNeedToCreateStates) {
                currentRecord.acceptResultStates(null);
                return;
            }

            final Map<Option<?>, Object> map = _Collections.hashMap();
            map.put(ROW_COUNT, totalRowCount);
            map.put(HAS_COLUMN, Boolean.TRUE);
            this.currentRecord.executor.putExecutorOptions(this.jdbcWarning, map);

            final ResultStates states;
            states = new JdbcResultStates(mergeOptionFunc(map, this.sessionFunc));

            if (this.currentRecord.resultItemStream) {
                action.accept((R) states);
            }

            this.currentRecord.acceptResultStates(states);
        }


    } // JdbcCursorRowSpliterator

    private static final class MultiSmtBatchRowSpliterator<R> extends JdbcBatchSpliterator<R> {

        private int groupIndex = 1; // here from 1 not 0.


        private MultiSmtBatchRowSpliterator(Statement statement, JdbcCurrentRecord<R> rowReader, BatchStmt stmt,
                                            SyncStmtOption option, ResultSet resultSet, Function<Option<?>, ?> sessionFunc) {
            super(statement, rowReader, stmt, option, resultSet, sessionFunc);
        }


        @Nullable
        @Override
        ResultSet nextResultSet() throws SQLException, TimeoutException {

            final Statement statement = this.statement;
            if (this.canceled) {
                statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
                return null;
            }
            final int groupIndex = this.groupIndex++, expectedCount; // groupIndex from 1 not 0
            expectedCount = this.stmt.groupList().size();

            return multiStatementNextResultSet(statement, groupIndex, expectedCount);
        }

        @Override
        ArmyException handleException(Exception cause) {
            onError();
            return this.executor.handleException(cause);
        }


        @Override
        void handleError(Error cause) {
            onError();
        }


        private void onError() {
            boolean closed = false;
            try {
                this.statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
            } catch (Throwable e) {
                closed = true;
                close();
            }

            if (!closed) {
                close();
            }

        }


    } // MultiSmtBatchRowSpliterator


    private static final class XidRowSpliterator extends ArmyDriverCurrentRecord implements Spliterator<Xid> {

        private final JdbcExecutor executor;

        private final TransactionInfo info;

        private final StreamOption option;

        private final Statement statement;

        private final ResultSet resultSet;

        private final Function<DataRecord, Xid> function;

        private final Function<Option<?>, ?> sessionFunc;

        private final ArmyResultRecordMeta meta;


        private long rowCount;

        private boolean canceled;
        private boolean closed;


        private XidRowSpliterator(JdbcExecutor executor, StreamOption option, Statement statement, ResultSet resultSet,
                                  DataType[] dataTypeArray, Function<DataRecord, Xid> function,
                                  Function<Option<?>, ?> sessionFunc) throws SQLException {
            this.executor = executor;
            this.info = executor.obtainTransaction();
            this.option = option;
            this.statement = statement;
            this.resultSet = resultSet;

            this.function = function;
            this.sessionFunc = sessionFunc;
            this.meta = new JdbcProcRecordMeta(1, executor, dataTypeArray, resultSet.getMetaData());
        }

        @Override
        public ArmyResultRecordMeta getRecordMeta() {
            return this.meta;
        }

        @Override
        protected Object[] copyValueArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long rowNumber() {
            return this.rowCount;
        }

        @Nullable
        @Override
        public Object get(int indexBasedZero) {
            try {
                return this.resultSet.getObject(this.meta.checkIndexAndToBasedOne(indexBasedZero));
            } catch (Exception e) {
                throw this.executor.handleException(e);
            }
        }

        @Nullable
        @Override
        public <T> T get(final int indexBasedZero, final Class<T> columnClass) {
            if (columnClass != Integer.class
                    && columnClass != String.class
                    && columnClass != Long.class
                    && columnClass != Boolean.class) {
                String m = String.format("don't support convert to %s", columnClass.getName());
                // no bug,never here
                throw new DataAccessException(m);
            }

            try {
                return this.resultSet.getObject(this.meta.checkIndexAndToBasedOne(indexBasedZero), columnClass);
            } catch (Exception e) {
                throw this.executor.handleException(e);
            }
        }

        @Nullable
        @Override
        public Object get(int indexBasedZero, MappingType type) {
            throw new UnsupportedOperationException();
        }

        @Nullable
        @Override
        public <T> T get(int indexBasedZero, Class<T> columnClass, MappingType type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T getNonNull(int indexBasedZero, Class<T> columnClass, MappingType type) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T getOrDefault(int indexBasedZero, Class<T> columnClass, MappingType type, T defaultValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T getOrSupplier(int indexBasedZero, Class<T> columnClass, MappingType type, Supplier<T> supplier) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean tryAdvance(Consumer<? super Xid> action) {
            if (this.closed || this.canceled) {
                return false;
            }
            try {
                return readRowStream(1, action);
            } catch (Exception e) {
                close();
                throw this.executor.handleException(e);
            } catch (Error e) {
                close();
                throw e;
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super Xid> action) {
            if (this.closed || this.canceled) {
                return;
            }
            try {
                readRowStream(0, action);
            } catch (Exception e) {
                close();
                throw this.executor.handleException(e);
            } catch (Error e) {
                close();
                throw e;
            }
        }


        @Nullable
        @Override
        public Spliterator<Xid> trySplit() {
            final int splitSize = this.option.splitSize();
            if (this.closed || this.canceled || splitSize < 1) {
                return null;
            }

            final List<Xid> itemList;
            itemList = _Collections.arrayList(Math.min(300, splitSize));
            try {
                readRowStream(splitSize, itemList::add);
            } catch (Exception e) {
                close();
                throw this.executor.handleException(e);
            } catch (Error e) {
                close();
                throw e;
            }

            final Spliterator<Xid> spliterator;
            if (itemList.size() == 0) {
                spliterator = null;
            } else {
                spliterator = itemList.spliterator();
            }
            return spliterator;
        }


        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return 0;
        }

        private void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            closeResultSetAndStatement(this.resultSet, this.statement);
        }

        private void cancel() {
            this.canceled = true;
        }


        private boolean readRowStream(final int readSize, final @Nullable Consumer<? super Xid> action)
                throws SQLException {

            if (action == null) {
                throw new NullPointerException();
            }

            final ResultSet resultSet = this.resultSet;
            final Function<DataRecord, Xid> function = this.function;
            final int maxValue = Integer.MAX_VALUE;

            long totalRowCount = this.rowCount;
            int readRowCount = 0;
            while (resultSet.next()) {

                action.accept(function.apply(this));

                readRowCount++;

                if (this.canceled) {
                    break;
                }

                if (readSize > 0 && readRowCount == readSize) {
                    break;
                }

                if (readRowCount == maxValue) {
                    totalRowCount += readRowCount;
                    readRowCount = 0;
                }

            }

            totalRowCount += readRowCount;

            this.rowCount = totalRowCount;

            if (this.canceled) {
                close();
            } else if (readSize == 0 || readSize > readRowCount) {
                emitStates(totalRowCount);
                close();
            }

            return readRowCount > 0;
        }

        private void emitStates(final long rowCount) {
            final Consumer<ResultStates> consumer;
            consumer = this.option.stateConsumer();
            if (consumer == ResultStates.IGNORE_STATES) {
                return;
            }

            try {
                final Map<Option<?>, Object> optionMap = _Collections.hashMap();
                optionMap.put(ROW_COUNT, rowCount);
                optionMap.put(HAS_COLUMN, Boolean.TRUE);
                this.executor.putExecutorOptions(this.statement.getWarnings(), optionMap);

                final ResultStates states;
                states = new JdbcResultStates(mergeOptionFunc(optionMap, this.sessionFunc));
                consumer.accept(states);
            } catch (Exception e) {
                close();
                throw this.executor.handleException(e);
            } catch (Error e) {
                close();
                throw e;
            }

        }


    } // XidRowSpliterator


    private static final class JdbcBatchQuery implements SyncBatchQuery {

        private final JdbcExecutor executor;

        private final BatchStmt stmt;

        private final SyncStmtOption stmtOption;

        private final Function<Option<?>, ?> sessionFunc;

        private final PreparedStatement statement;

        private final int batchSize;

        private final boolean closeStatement;

        private final DataType[] dataTypeArray;

        private ResultType nextResultType;

        private int nextBatchIndex = 0;

        private JdbcSimpleSpliterator<?> lastSpliterator;

        private Class<?> resultClass;

        private Supplier<?> objectConstructor;

        private Function<DataRecord, ?> classRowFunc, objectRowFunc;

        private boolean closed;

        /**
         * @see #batchQuery(BatchStmt, SyncStmtOption, Function)
         */
        private JdbcBatchQuery(boolean closeStatement, JdbcExecutor executor, BatchStmt stmt, SyncStmtOption stmtOption,
                               Function<Option<?>, ?> sessionFunc, PreparedStatement statement) {
            this.executor = executor;
            this.stmt = stmt;
            this.stmtOption = stmtOption;
            this.sessionFunc = sessionFunc;

            this.closeStatement = closeStatement;
            this.statement = statement;
            this.dataTypeArray = new DataType[stmt.selectionList().size()];
            this.batchSize = stmt.groupList().size();

            if (this.batchSize == 1) {
                this.nextResultType = ResultType.NONE;
            } else {
                this.nextResultType = ResultType.QUERY;
            }
        }

        @Override
        public ResultType nextType() throws ArmyException {
            if (this.closed) {
                throw _Exceptions.multiResultHaveClosed();
            }
            return this.nextResultType;
        }

        @Override
        public int batchNo() {
            if (this.closed) {
                throw _Exceptions.multiResultHaveClosed();
            }
            return this.nextBatchIndex;
        }

        @Override
        public int batchSize() {
            return this.stmt.groupList().size();
        }

        @Override
        public <R> R queryOne(Class<R> resultClass) throws ArmyException {
            return query(resultClass, ResultStates.IGNORE_STATES)
                    .reduce(StreamFunctions::atMostOne)
                    .orElse(null);
        }

        @Override
        public <R> R queryOneObject(Supplier<R> constructor) throws ArmyException {
            return queryObject(constructor, ResultStates.IGNORE_STATES)
                    .reduce(StreamFunctions::atMostOne)
                    .orElse(null);
        }

        @Override
        public <R> R queryOneRecord(Function<? super CurrentRecord, R> function) throws ArmyException {
            return queryRecord(function, ResultStates.IGNORE_STATES)
                    .reduce(StreamFunctions::atMostOne)
                    .orElse(null);
        }

        @Override
        public <R> Stream<R> query(Class<R> resultClass) throws ArmyException {
            return query(resultClass, ResultStates.IGNORE_STATES);
        }


        @SuppressWarnings("unchecked")
        @Override
        public <R> Stream<R> query(final Class<R> resultClass, final Consumer<ResultStates> consumer) throws ArmyException {
            Function<DataRecord, R> rowFunc = null;
            final Class<?> prevResultClass = this.resultClass;
            if (prevResultClass == null) {
                this.resultClass = resultClass;
            } else if (resultClass == prevResultClass) {
                rowFunc = (Function<DataRecord, R>) this.classRowFunc;
            }
            if (rowFunc == null) {
                this.classRowFunc = rowFunc = RowFunctions.classRowFunc(resultClass, this.stmt);
            }
            return queryRecord(rowFunc, consumer);
        }

        @Override
        public <R> Stream<R> queryObject(Supplier<R> constructor) throws ArmyException {
            return queryObject(constructor, ResultStates.IGNORE_STATES);
        }


        @SuppressWarnings("unchecked")
        @Override
        public <R> Stream<R> queryObject(final Supplier<R> constructor, final Consumer<ResultStates> consumer) throws ArmyException {
            Function<DataRecord, R> rowFunc = null;
            final Supplier<?> prevObjectConstructor = this.objectConstructor;
            if (prevObjectConstructor == null) {
                this.objectConstructor = constructor;
            } else if (prevObjectConstructor == constructor) {
                rowFunc = (Function<DataRecord, R>) this.objectRowFunc;
            }
            if (rowFunc == null) {
                this.objectRowFunc = rowFunc = RowFunctions.objectRowFunc(constructor, this.stmt.selectionList(), true);
            }
            return queryRecord(rowFunc, consumer);
        }

        @Override
        public <R> Stream<R> queryRecord(Function<? super CurrentRecord, R> function) throws ArmyException {
            return queryRecord(function, ResultStates.IGNORE_STATES);
        }

        @Override
        public <R> Stream<R> queryRecord(final @Nullable Function<? super CurrentRecord, R> function, final @Nullable Consumer<ResultStates> consumer)
                throws ArmyException {

            if (function == null) {
                throw _Exceptions.recordMapFuncIsNull();
            } else if (consumer == null) {
                throw _Exceptions.statesConsumerIsNull();
            }

            final JdbcExecutor executor = this.executor;
            final BatchStmt stmt = this.stmt;
            final int batchIndex = this.nextBatchIndex++;
            if (batchIndex >= this.batchSize) {
                throw _Exceptions.batchQueryHaveEnded();
            } else if (this.lastSpliterator != null) {
                throw _Exceptions.lastStreamDontEnd();
            }

            if (batchIndex + 1 == this.batchSize) {
                this.nextResultType = ResultType.NONE;
            }

            ResultSet resultSet = null;
            try {
                final PreparedStatement statement = this.statement;

                statement.clearParameters();
                statement.clearBatch();
                statement.clearWarnings();

                final List<SQLParam> paramList = stmt.groupList().get(batchIndex);
                final SyncStmtOption stmtOption = this.stmtOption;

                executor.bindParameters(statement, paramList);
                executor.bindStatementOption(statement, stmt, stmtOption);

                resultSet = statement.executeQuery();

                final ResultSetMetaData metaData;
                metaData = resultSet.getMetaData();

                final DataType[] dataTypeArray = this.dataTypeArray;
                executor.readDataTypeArray(metaData, dataTypeArray);

                final JdbcCurrentRecord<R> currentRecord;
                currentRecord = new JdbcCurrentRecord<>(executor, this.stmt.selectionList(), dataTypeArray,
                        function, combineConsumer(consumer, stmtOption), metaData, false);

                final JdbcSimpleSpliterator<R> spliterator;
                this.lastSpliterator = spliterator = new JdbcSimpleSpliterator<>(false, statement, resultSet,
                        currentRecord, stmt, stmtOption, this.sessionFunc);

                spliterator.addCloseListener(this::streamCloseListener);

                return executor.assembleStream(spliterator, stmtOption);
            } catch (Exception e) {
                closeResource(resultSet);
                throw executor.handleException(e);
            } catch (Throwable e) {
                closeResource(resultSet);
                throw e;
            }


        }

        @Override
        public void close() throws ArmyException {
            if (this.closed) {
                return;
            }
            this.closed = true;
            final JdbcSimpleSpliterator<?> spliterator = this.lastSpliterator;
            if (spliterator == null) {
                if (this.closeStatement) {
                    closeResource(this.statement);
                }
            } else {
                try {
                    spliterator.close();
                } catch (Exception e) {
                    throw this.executor.handleException(e);
                } finally {
                    if (this.closeStatement) {
                        closeResource(this.statement);
                    }
                }
            }

        }

        private void streamCloseListener() {
            this.lastSpliterator = null;
        }


    } //JdbcBatchQuery


}
