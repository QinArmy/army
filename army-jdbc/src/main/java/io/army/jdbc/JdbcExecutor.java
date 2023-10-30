package io.army.jdbc;

import io.army.ArmyException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.criteria.CriteriaException;
import io.army.criteria.SQLParam;
import io.army.criteria.Selection;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.session.*;
import io.army.session.executor.DriverSpiHolder;
import io.army.session.executor.ExecutorSupport;
import io.army.session.executor.StmtExecutor;
import io.army.sqltype.SqlType;
import io.army.stmt.*;
import io.army.sync.StreamCommander;
import io.army.sync.StreamOption;
import io.army.sync.StreamOptions;
import io.army.sync.SyncStmtOption;
import io.army.sync.executor.SyncStmtExecutor;
import io.army.type.ImmutableSpec;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;
import io.army.util._TimeUtils;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <p>This class is a abstract implementation of {@link SyncStmtExecutor} with JDBC spi.
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
abstract class JdbcExecutor extends ExecutorSupport implements SyncStmtExecutor, DriverSpiHolder {

    private static final AtomicLong EXECUTOR_IDENTIFIER = new AtomicLong(0);

    final JdbcExecutorFactory factory;

    final Connection conn;

    private final String name;
    private final long identifier;

    /**
     * <p>True : application developer have got the {@link Connection} instance,<br/>
     * so {@link TransactionInfo} perhaps error.
     * <p>More info,see {@link io.army.env.ArmyKey#DRIVER_SPI_MODE}
     */
    private boolean driverSpiOpened;

    JdbcExecutor(JdbcExecutorFactory factory, Connection conn, String name) {
        this.name = name;
        this.factory = factory;
        this.conn = conn;

        if (factory.sessionIdentifierEnable) {
            this.identifier = EXECUTOR_IDENTIFIER.addAndGet(1L);
        } else {
            this.identifier = 0L;
        }
    }


    @Override
    public final long sessionIdentifier() throws DataAccessException {
        return this.identifier;
    }

    @Override
    public final boolean inTransaction() throws DataAccessException {
        return obtainTransaction() != null;
    }

    @Override
    public final boolean isSameFactory(StmtExecutor s) {
        return s instanceof JdbcExecutor && ((JdbcExecutor) s).factory == this.factory;
    }


    @Override
    public final <T> T getDriverSpi(Class<T> spiClass) {
        this.driverSpiOpened = Connection.class.isAssignableFrom(spiClass);
        return spiClass.cast(this.conn);
    }


    @Override
    public final long insertAsLong(final SimpleStmt stmt, final SyncStmtOption option) {

        final List<? extends Selection> selectionList = stmt.selectionList();
        final boolean returningId;
        returningId = selectionList.size() == 1 && selectionList.get(0) instanceof PrimaryFieldMeta;

        final int generatedKeys;
        if (!returningId && stmt instanceof GeneratedKeyStmt) {
            generatedKeys = Statement.RETURN_GENERATED_KEYS;
        } else {
            generatedKeys = Statement.NO_GENERATED_KEYS;
        }

        try (final Statement statement = bindInsertStatement(stmt, option, generatedKeys)) {

            final long rows;

            if (returningId) {
                if (statement instanceof PreparedStatement) {
                    rows = doExtractId(((PreparedStatement) statement).executeQuery(), (GeneratedKeyStmt) stmt);
                } else {
                    rows = doExtractId(statement.executeQuery(stmt.sqlText()), (GeneratedKeyStmt) stmt);
                }
            } else {
                if (this.factory.useLargeUpdate) {
                    if (statement instanceof PreparedStatement) {
                        rows = ((PreparedStatement) statement).executeLargeUpdate();
                    } else {
                        rows = statement.executeLargeUpdate(stmt.sqlText(), generatedKeys);
                    }
                } else if (statement instanceof PreparedStatement) {
                    rows = ((PreparedStatement) statement).executeUpdate();
                } else {
                    rows = statement.executeUpdate(stmt.sqlText(), generatedKeys);
                }

                if (generatedKeys == Statement.RETURN_GENERATED_KEYS) {
                    doExtractId(statement.getGeneratedKeys(), (GeneratedKeyStmt) stmt);
                }
            }
            return rows;
        } catch (Exception e) {
            throw wrapError(e);
        }

    }

    @Override
    public final ResultStates insert(SimpleStmt stmt, SyncStmtOption option) throws DataAccessException {
        return null;
    }

    @Override
    public final long updateAsLong(final SimpleStmt stmt, final SyncStmtOption option) throws DataAccessException {

        try (final Statement statement = bindStatement(stmt, option)) {

            final long rows;

            if (statement instanceof PreparedStatement) {
                if (this.factory.useLargeUpdate) {
                    rows = ((PreparedStatement) statement).executeLargeUpdate();
                } else {
                    rows = ((PreparedStatement) statement).executeUpdate();
                }
            } else if (this.factory.useLargeUpdate) {
                rows = statement.executeLargeUpdate(stmt.sqlText());
            } else {
                rows = statement.executeUpdate(stmt.sqlText());
            }

            return rows;
        } catch (Exception e) {
            throw wrapError(e);
        }

    }

    @Override
    public final ResultStates update(SimpleStmt stmt, SyncStmtOption option) throws DataAccessException {
        return null;
    }

    @Override
    public final List<Long> batchUpdateList(final BatchStmt stmt, final IntFunction<List<Long>> listConstructor,
                                            SyncStmtOption option, final @Nullable TableMeta<?> domainTable,
                                            final @Nullable List<Long> rowsList) {
        final List<Long> resultList;
        if (option.isParseBatchAsMultiStmt()) {
            resultList = executeMultiStmtBatchUpdate(stmt, listConstructor, option, domainTable);
        } else {
            resultList = executeBatchUpdate(stmt, listConstructor, option, domainTable, rowsList);
        }
        return resultList;
    }

    @Override
    public final <R> Stream<R> query(SingleSqlStmt stmt, Class<R> resultClass, SyncStmtOption option)
            throws DataAccessException {
        return executeQuery(stmt, option, beanReaderFunc(stmt, resultClass));

    }

    @Override
    public final <R> Stream<R> queryObject(SingleSqlStmt stmt, Supplier<R> constructor, SyncStmtOption option)
            throws DataAccessException {
        return this.executeQuery(stmt, option, objectReaderFunc(stmt, constructor));
    }

    @Override
    public final <R> Stream<R> queryRecord(SingleSqlStmt stmt, Function<CurrentRecord, R> function, SyncStmtOption option)
            throws DataAccessException {
        return this.executeQuery(stmt, option, recordReaderFunc(stmt.selectionList(), function));
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <R> Stream<R> secondQuery(final TwoStmtModeQuerySpec stmt, final SyncStmtOption option, final List<R> resultList) {
        return null;
    }


    @Override
    public final void close() throws DataAccessException {
        try {
            this.conn.close();
        } catch (Exception e) {
            throw handleException(e);
        }

    }


    @Override
    public final String toString() {
        return _StringUtils.builder(46)
                .append(getClass().getName())
                .append("[sessionName:")
                .append(this.name)
                .append(",hash:")
                .append(System.identityHashCode(this))
                .append(']')
                .toString();
    }



    /*################################## blow packet template ##################################*/

    abstract Logger getLogger();

    @Nullable
    abstract Object bind(PreparedStatement stmt, int indexBasedOne, @Nullable Object attr, MappingType type,
                         SqlType sqlType, Object nonNull)
            throws SQLException;

    abstract SqlType getSqlType(ResultSetMetaData metaData, int indexBasedOne) throws SQLException;

    @Nullable
    abstract Object get(ResultSet resultSet, int indexBasedOne, SqlType sqlType) throws SQLException;

    /**
     * @return current transaction cache instance
     */
    @Nullable
    abstract Transaction obtainTransaction();


    final void setLongText(PreparedStatement stmt, int index, Object nonNull) throws SQLException {
        if (nonNull instanceof String) {
            stmt.setString(index, (String) nonNull);
        } else if (nonNull instanceof Reader) {
            stmt.setCharacterStream(index, (Reader) nonNull);
        } else if (nonNull instanceof Path) {
            try (Reader reader = Files.newBufferedReader((Path) nonNull, StandardCharsets.UTF_8)) {
                stmt.setCharacterStream(index, reader);
            } catch (IOException e) {
                String m = String.format("Parameter[%s] %s[%s] read occur error."
                        , index, Path.class.getName(), nonNull);
                throw new SQLException(m, e);
            }
        }
    }

    final void setLongBinary(PreparedStatement stmt, int index, Object nonNull) throws SQLException {
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
        }
    }

    final ArmyException handleException(Exception cause) {
        // TODO
        return wrapError(cause);
    }

    /*################################## blow private method ##################################*/


    /**
     * @see #batchUpdateList(BatchStmt, IntFunction, SyncStmtOption, TableMeta, List)
     */
    private List<Long> executeBatchUpdate(final BatchStmt stmt, final IntFunction<List<Long>> listConstructor,
                                          final SyncStmtOption option, final @Nullable TableMeta<?> domainTable,
                                          final @Nullable List<Long> rowsList) {
        if (!(rowsList == null || domainTable instanceof ChildTableMeta)) {
            throw new IllegalArgumentException();
        }
        try (final PreparedStatement statement = this.conn.prepareStatement(stmt.sqlText())) {

            for (List<SQLParam> group : stmt.groupList()) {
                bindParameter(statement, group);
                statement.addBatch();
            }

            if (option.isSupportTimeout()) {
                statement.setQueryTimeout(option.restSeconds());
            }

            final List<Long> resultList;

            if (this.factory.useLargeUpdate) {
                final long[] affectedRows;
                affectedRows = statement.executeLargeBatch();
                resultList = this.handleBatchResult(stmt.hasOptimistic(), affectedRows.length,
                        index -> affectedRows[index], listConstructor, domainTable, rowsList
                );
            } else {
                final int[] affectedRows;
                affectedRows = statement.executeBatch();
                resultList = this.handleBatchResult(stmt.hasOptimistic(), affectedRows.length,
                        index -> affectedRows[index], listConstructor, domainTable, rowsList
                );
            }

            return resultList;
        } catch (Exception e) {
            throw wrapError(e);
        }
    }

    /**
     * @see #batchUpdateList(BatchStmt, IntFunction, SyncStmtOption, TableMeta, List)
     */
    private List<Long> executeMultiStmtBatchUpdate(final BatchStmt stmt, final IntFunction<List<Long>> listConstructor,
                                                   SyncStmtOption option, final @Nullable TableMeta<?> domainTable) {
        final List<List<SQLParam>> groupList;
        groupList = stmt.groupList();
        if (groupList.get(0).size() > 0) {
            throw new IllegalArgumentException("stmt error");
        }

        try (Statement statement = this.conn.createStatement()) {

            if (option.isSupportTimeout()) {
                statement.setQueryTimeout(option.restSeconds());
            }

            if (statement.execute(stmt.sqlText())) {
                // sql error
                throw new DataAccessException("error,multi-statement batch update the first result is ResultSet");
            }

            final int stmtSize;
            stmtSize = groupList.size();
            final List<Long> list = listConstructor.apply(stmtSize);
            if (list == null) {
                throw _Exceptions.listConstructorError();
            }

            if (domainTable instanceof ChildTableMeta) {
                handleChildMultiStmtBatchUpdate(statement, stmt, (ChildTableMeta<?>) domainTable, list);
            } else {
                // SingleTableMeta batch update or multi-table batch update.
                handleSimpleMultiStmtBatchUpdate(statement, stmt, domainTable, list);
            }

            if (stmtSize != list.size()) {
                throw _Exceptions.batchCountNotMatch(stmtSize, list.size());
            }
            return list;
        } catch (Exception e) {
            throw wrapError(e);
        }

    }


    private SqlType[] createSqlTypArray(final ResultSetMetaData metaData) throws SQLException {
        final SqlType[] sqlTypeArray = new SqlType[metaData.getColumnCount()];
        for (int i = 0; i < sqlTypeArray.length; i++) {
            sqlTypeArray[i] = this.getSqlType(metaData, i + 1);
        }
        return sqlTypeArray;
    }


    /**
     * @see #insertAsLong(SimpleStmt, SyncStmtOption)
     */
    private Statement bindInsertStatement(final SimpleStmt stmt, final SyncStmtOption option, final int generatedKeys)
            throws TimeoutException, SQLException {

        final List<SQLParam> paramGroup;
        paramGroup = stmt.paramGroup();

        final Statement statement;
        if (!option.isPreferServerPrepare() && paramGroup.size() == 0) {
            statement = this.conn.createStatement();
        } else {
            statement = this.conn.prepareStatement(stmt.sqlText(), generatedKeys);

        }

        try {
            if (statement instanceof PreparedStatement) {
                bindParameter((PreparedStatement) statement, paramGroup);
            }

            bindStatementOption(statement, stmt, option);
            return statement;
        } catch (Throwable e) {
            statement.close();
            throw e;
        }
    }

    /**
     * @see #updateAsLong(SimpleStmt, SyncStmtOption)
     * @see #executeSimpleQuery(SimpleStmt, SyncStmtOption, Function)
     */
    private Statement bindStatement(final SimpleStmt stmt, final SyncStmtOption option)
            throws TimeoutException, SQLException {

        final List<SQLParam> paramGroup;
        paramGroup = stmt.paramGroup();

        final Statement statement;
        if (!option.isPreferServerPrepare() && paramGroup.size() == 0) {
            statement = this.conn.createStatement();
        } else {
            statement = this.conn.prepareStatement(stmt.sqlText());
        }

        try {

            if (statement instanceof PreparedStatement) {
                bindParameter((PreparedStatement) statement, paramGroup);
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
     * @see #bindInsertStatement(SimpleStmt, SyncStmtOption, int)
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
            } else if (fetchSize == 0
                    && option.isPreferClientStream()
                    && this instanceof MySQLExecutor) {
                statement.setFetchSize(Integer.MIN_VALUE);
            }
        }

    }


    /**
     * @see #update(SimpleStmt, int)
     */
    private Statement createUpdateStatement(final String sql, final int paramSize) throws SQLException {
        final Statement statement;
        if (paramSize > 0) {
            statement = this.conn.prepareStatement(sql);
        } else {
            statement = this.conn.createStatement();
        }
        return statement;
    }

    /**
     * @see #doQuery(SimpleStmt, int, Supplier, Function)
     */
    private Statement createQueryStatement(final String sql, final int paramSize) throws SQLException {
        final Statement statement;
        if (paramSize > 0) {
            statement = this.conn.prepareStatement(sql);
        } else {
            statement = this.conn.createStatement();
        }
        return statement;
    }


    /**
     * @see #queryAsStream(SimpleStmt, int, StreamOptions, Function)
     */
    private Statement createStreamStmt(final String sql, final int paramSize, final StreamOptions options)
            throws SQLException {
        final Statement statement;
        if (paramSize > 0 || options.serverStream == Boolean.TRUE) {
            if (options == StreamOptions.LIST_LIKE) {
                statement = this.conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            } else {
                statement = this.conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                        ResultSet.CLOSE_CURSORS_AT_COMMIT);
                statement.setFetchSize(options.fetchSize);
            }

        } else if (options == StreamOptions.LIST_LIKE || options.serverStream == null) {
            statement = this.conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        } else {
            statement = this.conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);
            setClientStreamFetchSize(statement, options);
        }

        return statement;

    }

    private void setClientStreamFetchSize(final Statement statement, final StreamOptions options)
            throws SQLException {
        switch (this.factory.serverDataBase) {
            case MySQL: {
                if (this instanceof MySQLExecutor) {
                    statement.setFetchSize(Integer.MIN_VALUE);
                } else {
                    statement.setFetchSize(options.fetchSize);
                }
            }
            break;
            case PostgreSQL:
            case Oracle:
            case H2:
                statement.setFetchSize(options.fetchSize);
                break;
            default:
                throw _Exceptions.unexpectedEnum(this.factory.serverDataBase);
        }
    }



    /**
     * @see #query(SingleSqlStmt, Class, SyncStmtOption)
     */
    private <R> Function<ResultSetMetaData, RowReader<R>> beanReaderFunc(
            final SingleSqlStmt stmt, @Nullable final Class<R> resultClass) {
        if (resultClass == null) {
            throw new NullPointerException();
        }
        return metaData -> {
            try {
                return this.createBeanRowReader(metaData, resultClass, stmt);
            } catch (SQLException e) {
                throw wrapError(e);
            }

        };
    }

    /**
     * @see #queryObject(SingleSqlStmt, Supplier, SyncStmtOption)
     */
    private <R> Function<ResultSetMetaData, RowReader<R>> objectReaderFunc(
            final SingleSqlStmt stmt, final @Nullable Supplier<R> constructor) {
        if (constructor == null) {
            throw new NullPointerException();
        }
        return metaData -> {
            try {
                return new ObjectReader<>(this, stmt.selectionList(), stmt instanceof TwoStmtModeQuerySpec,
                        this.createSqlTypArray(metaData), constructor
                );
            } catch (SQLException e) {
                throw wrapError(e);
            }
        };
    }

    /**
     * @see #queryRecord(SingleSqlStmt, Function, SyncStmtOption)
     */
    private <R> Function<ResultSetMetaData, RowReader<R>> recordReaderFunc(
            final List<? extends Selection> selectionList, final @Nullable Function<CurrentRecord, R> function) {
        if (function == null) {
            throw new NullPointerException();
        }
        return metaData -> {
            try {
                return new RecordRowReader<>(this, selectionList, this.createSqlTypArray(metaData), function);
            } catch (SQLException e) {
                throw wrapError(e);
            }
        };
    }


    /**
     * @see #insert(SimpleStmt, SyncStmtOption)
     * @see #update(SimpleStmt, SyncStmtOption)
     * @see #executeSimpleQuery(SimpleStmt, SyncStmtOption, Function)
     * @see #executeBatchQuery(BatchStmt, SyncStmtOption, Function)
     * @see #executeBatchUpdate(BatchStmt, IntFunction, SyncStmtOption, TableMeta, List)
     */
    private void bindParameter(final PreparedStatement statement, final List<SQLParam> paramGroup)
            throws SQLException {

        final ServerMeta serverMeta = this.factory.serverMeta;
        final MappingEnv mappingEnv = this.factory.mappingEnv;
        final boolean truncatedTimeType = this.factory.truncatedTimeType;

        SQLParam sqlParam;
        Object value;
        MappingType mappingType;
        TypeMeta typeMeta;
        SqlType sqlType;
        Object attr = null;

        final int paramSize = paramGroup.size();
        for (int i = 0, paramIndex = 1; i < paramSize; i++) {
            sqlParam = paramGroup.get(i);

            typeMeta = sqlParam.typeMeta();
            if (typeMeta instanceof MappingType) {
                mappingType = (MappingType) typeMeta;
            } else {
                mappingType = typeMeta.mappingType();
            }
            sqlType = mappingType.map(serverMeta);

            if (sqlParam instanceof SingleParam) {
                value = ((SingleParam) sqlParam).value();
                if (value == null) {
                    // bind null
                    statement.setNull(paramIndex++, Types.NULL);
                    continue;
                }
                value = mappingType.beforeBind(sqlType, mappingEnv, value);
                if (truncatedTimeType && value instanceof Temporal && typeMeta instanceof FieldMeta) {
                    value = _TimeUtils.truncatedIfNeed(((FieldMeta<?>) typeMeta).scale(), (Temporal) value);
                }
                //TODO field codec
                attr = bind(statement, paramIndex++, attr, mappingType, sqlType, value);
                continue;
            }

            if (!(sqlParam instanceof MultiParam)) {
                throw _Exceptions.unexpectedSqlParam(sqlParam);
            }

            for (final Object element : ((MultiParam) sqlParam).valueList()) {
                if (element == null) {
                    // bind null
                    statement.setNull(paramIndex++, Types.NULL);
                    continue;
                }
                value = mappingType.beforeBind(sqlType, mappingEnv, element);

                if (truncatedTimeType && value instanceof Temporal && typeMeta instanceof FieldMeta) {
                    value = _TimeUtils.truncatedIfNeed(((FieldMeta<?>) typeMeta).scale(), (Temporal) value);
                }
                //TODO field codec
                attr = bind(statement, paramIndex++, attr, mappingType, sqlType, value);

            }// inner for


        }//outer for

    }

    /**
     * @see #query(SingleSqlStmt, Class, SyncStmtOption)
     * @see #queryObject(SingleSqlStmt, Supplier, SyncStmtOption)
     * @see #queryRecord(SingleSqlStmt, Function, SyncStmtOption)
     */
    private <R> Stream<R> executeQuery(SingleSqlStmt stmt, SyncStmtOption option,
                                       final Function<ResultSetMetaData, RowReader<R>> function) {
        try {
            final Stream<R> stream;
            if (stmt instanceof SimpleStmt) {
                stream = executeSimpleQuery((SimpleStmt) stmt, option, function);
            } else if (!(stmt instanceof BatchStmt)) {
                throw _Exceptions.unexpectedStmt(stmt);
            } else if (option.isParseBatchAsMultiStmt()) {
                stream = executeMultiStmtBatchQuery((BatchStmt) stmt, option, function);
            } else {
                stream = executeBatchQuery((BatchStmt) stmt, option, function);
            }
            return stream;
        } catch (Exception e) {
            throw handleException(e);
        }

    }


    /**
     * invoker must handle all error.
     *
     * @see #executeQuery(SingleSqlStmt, SyncStmtOption, Function)
     */
    private <R> Stream<R> executeSimpleQuery(SimpleStmt stmt, SyncStmtOption option,
                                             final Function<ResultSetMetaData, RowReader<R>> function)
            throws SQLException {

        Statement statement = null;
        ResultSet resultSet = null;

        try {

            statement = bindStatement(stmt, option);
            resultSet = jdbcExecuteQuery(statement, stmt.sqlText());

            final RowReader<R> rowReader;
            rowReader = function.apply(resultSet.getMetaData());

            final RowSpliterator<R> spliterator;
            spliterator = new RowSpliterator<>(statement, resultSet, rowReader, stmt, option);

            return assembleStream(spliterator, option);
        } catch (Throwable e) {
            closeResultSetAndStatement(resultSet, statement);
            throw e;
        }

    }


    /**
     * invoker must handle all error.
     *
     * @see #executeQuery(SingleSqlStmt, SyncStmtOption, Function)
     */
    private <R> Stream<R> executeBatchQuery(BatchStmt stmt, SyncStmtOption option,
                                            final Function<ResultSetMetaData, RowReader<R>> function)
            throws SQLException {

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {

            statement = this.conn.prepareStatement(stmt.sqlText());

            for (List<SQLParam> paramGroup : stmt.groupList()) {
                bindParameter(statement, paramGroup);
                statement.addBatch();
            }

            // bind option
            bindStatementOption(statement, stmt, option);

            resultSet = statement.executeQuery();

            final RowReader<R> rowReader;
            rowReader = function.apply(resultSet.getMetaData());

            final BatchRowSpliterator<R> spliterator;
            spliterator = new BatchRowSpliterator<>(statement, rowReader, stmt, option, resultSet);

            return assembleStream(spliterator, option);
        } catch (Throwable e) {
            closeResultSetAndStatement(resultSet, statement);
            throw e;
        }
    }

    /**
     * invoker must handle all error.
     *
     * @see #executeQuery(SingleSqlStmt, SyncStmtOption, Function)
     */
    private <R> Stream<R> executeMultiStmtBatchQuery(final BatchStmt stmt, SyncStmtOption option,
                                                     final Function<ResultSetMetaData, RowReader<R>> function)
            throws SQLException, TimeoutException {

        final List<List<SQLParam>> groupList = stmt.groupList();

        if (groupList.get(0).size() > 0) {
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

            final RowReader<R> rowReader;
            rowReader = function.apply(resultSet.getMetaData());

            final MultiSmtBatchRowSpliterator<R> spliterator;
            spliterator = new MultiSmtBatchRowSpliterator<>(statement, rowReader, stmt, option, resultSet);

            return assembleStream(spliterator, option);
        } catch (Throwable e) {
            closeResultSetAndStatement(resultSet, statement);
            throw e;
        }
    }

    /**
     * @see #executeSimpleQuery(SimpleStmt, SyncStmtOption, Function)
     */
    private <R> Stream<R> assembleStream(final JdbcRowSpliterator<R> spliterator, final StreamOption option) {
        final Consumer<StreamCommander> consumer;
        consumer = option.streamCommanderConsumer();
        if (consumer != null) {
            consumer.accept(spliterator::cancel); // cancel event
        }
        return StreamSupport.stream(spliterator, false)
                .onClose(spliterator::close); // close event
    }


    /**
     * @see #executeBatchUpdate(BatchStmt, IntFunction, SyncStmtOption, TableMeta, List)
     */
    private List<Long> handleBatchResult(final boolean optimistic, final int bathSize,
                                         final IntToLongFunction accessor,
                                         final IntFunction<List<Long>> listConstructor,
                                         final @Nullable TableMeta<?> domainTable,
                                         final @Nullable List<Long> rowsList) {
        assert rowsList == null || domainTable instanceof ChildTableMeta;

        final List<Long> list;
        if (rowsList == null) {
            list = listConstructor.apply(bathSize);
            if (list == null) {
                throw _Exceptions.listConstructorError();
            }
        } else if (rowsList.size() != bathSize) { // here bathSize representing parent's bathSize ,because army update child and update parent
            throw _Exceptions.childBatchSizeError((ChildTableMeta<?>) domainTable, rowsList.size(), bathSize);
        } else {
            list = rowsList;
        }

        long rows;
        for (int i = 0; i < bathSize; i++) {
            rows = accessor.applyAsLong(i);
            if (optimistic && rows == 0L) {
                throw _Exceptions.batchOptimisticLock(domainTable, i + 1, rows);
            } else if (rowsList == null) {
                list.add(rows);
            } else if (rows != rowsList.get(i)) { // here rows representing parent's rows,because army update child and update parent
                throw _Exceptions.batchChildUpdateRowsError((ChildTableMeta<?>) domainTable, i + 1, rowsList.get(i),
                        rows);
            }
        }

        return list;
    }


    /**
     * @see #executeMultiStmtBatchUpdate(BatchStmt, IntFunction, SyncStmtOption, TableMeta)
     */
    private void handleChildMultiStmtBatchUpdate(final Statement statement, final BatchStmt stmt,
                                                 final ChildTableMeta<?> domainTable, final List<Long> list)
            throws SQLException {

        final boolean useLargeUpdate = this.factory.useLargeUpdate;
        final boolean optimistic = stmt.hasOptimistic();

        final int itemSize, itemPairSize;
        itemSize = stmt.groupList().size();
        if ((itemSize & 1) != 0) {
            // no bug,never here
            throw new IllegalArgumentException(String.format("item count[%s] not event", itemSize));
        }
        itemPairSize = itemSize << 1;

        long itemRows = 0, rows;
        for (int i = 0; i < itemPairSize; i++) {

            if (statement.getMoreResults()) {  // sql error
                statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
                throw _Exceptions.batchUpdateReturnResultSet(domainTable, (i >> 1) + 1);
            }
            if (useLargeUpdate) {
                rows = statement.getLargeUpdateCount();
            } else {
                rows = statement.getUpdateCount();
            }

            if (rows == -1) {
                // no more result,sql error,no bug,never here
                throw _Exceptions.multiStmtBatchUpdateResultCountError(itemPairSize, i);
            }

            if (optimistic && rows == 1) {
                throw _Exceptions.batchOptimisticLock(domainTable, (i >> 1) + 1, rows);
            } else if ((i & 1) == 0) { // this code block representing child's update rows,because army update child and update parent
                itemRows = rows;
                list.add(rows);
            } else if (rows != itemRows) { // this code block representing parent's update rows,because army update child and update parent
                throw _Exceptions.batchChildUpdateRowsError(domainTable, (i >> 1) + 1, itemRows, rows);
            }


        }// for

        if (statement.getMoreResults() || statement.getUpdateCount() != -1) {
            // sql error
            throw _Exceptions.multiStmtBatchUpdateResultCountError(itemPairSize, itemPairSize + 1);
        }

        if (itemSize != list.size()) {
            throw _Exceptions.multiStmtCountAndResultCountNotMatch(domainTable, itemSize, list.size());
        }

    }

    /**
     * @param domainTable <ul>
     *                    <li>null : multi-table batch update </li>
     *                    <li>{@link SingleTableMeta} : single table batch update</li>
     *
     *                    </ul>
     * @see #executeMultiStmtBatchUpdate(BatchStmt, IntFunction, SyncStmtOption, TableMeta)
     */
    private void handleSimpleMultiStmtBatchUpdate(final Statement statement, final BatchStmt stmt,
                                                  final @Nullable TableMeta<?> domainTable,
                                                  final List<Long> list) throws SQLException {

        assert domainTable == null || domainTable instanceof SingleTableMeta;

        final boolean useLargeUpdate = this.factory.useLargeUpdate;
        final boolean optimistic = stmt.hasOptimistic();
        final int itemSize = stmt.groupList().size();

        long rows;
        for (int i = 0; i < itemSize; i++) {
            if (statement.getMoreResults()) {
                statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
                throw _Exceptions.batchUpdateReturnResultSet(domainTable, i + 1);
            }
            if (useLargeUpdate) {
                rows = statement.getLargeUpdateCount();
            } else {
                rows = statement.getUpdateCount();
            }

            if (rows == -1) {
                // no more result,sql error
                throw _Exceptions.multiStmtBatchUpdateResultCountError(itemSize, i);
            }
            if (optimistic && rows == 0) {
                throw _Exceptions.batchOptimisticLock(domainTable, i + 1, rows);
            }
            list.add(rows);

        } // loop for

        if (statement.getMoreResults() || statement.getUpdateCount() != -1) {
            // sql error
            throw _Exceptions.multiStmtBatchUpdateResultCountError(itemSize, itemSize + 1);
        }

        if (itemSize != list.size()) {
            throw _Exceptions.multiStmtCountAndResultCountNotMatch(domainTable, itemSize, list.size());
        }

    }

    /**
     * @see #doQuery(SimpleStmt, int, Supplier, Function)
     */
    private <R> List<R> readReturningInsert(final ResultSet set, final RowReader<R> rowReader,
                                            final GeneratedKeyStmt stmt,
                                            final Supplier<List<R>> listConstructor) throws SQLException {

        try (ResultSet resultSet = set) {

            final boolean optimistic = stmt.hasOptimistic();
            final int idSelectionIndex = stmt.idSelectionIndex();

            final PrimaryFieldMeta<?> idField = stmt.idField();
            final MappingType type = idField.mappingType();
            final SqlType idSqlType = rowReader.sqlTypeArray[idSelectionIndex];


            final MappingEnv env = this.factory.mappingEnv;
            final int rowSize = stmt.rowSize();

            List<R> list = listConstructor.get();
            if (list == null) {
                throw _Exceptions.listConstructorError();
            }
            Object idValue;
            int rowIndex = 0;
            for (final int idIndexBasedOne = idSelectionIndex + 1; resultSet.next(); rowIndex++) {
                if (rowIndex == rowSize) {
                    throw insertedRowsAndGenerateIdNotMatch(rowSize, rowIndex + 1);
                }
                // read id start
                idValue = get(resultSet, idIndexBasedOne, idSqlType);
                if (idValue == null) {
                    throw _Exceptions.idValueIsNull(rowIndex, idField);
                }
                idValue = type.afterGet(idSqlType, env, idValue);
                stmt.setGeneratedIdValue(rowIndex, idValue);
                // read id end
                list.add(rowReader.readOneRow(resultSet)); // read row
            }
            if (optimistic && rowIndex == 0) {
                throw _Exceptions.optimisticLock(0);
            } else if (rowIndex != rowSize) {
                throw insertedRowsAndGenerateIdNotMatch(rowSize, rowIndex);
            }
            if (list instanceof ImmutableSpec
                    && !(stmt instanceof TwoStmtModeQuerySpec && rowReader instanceof ObjectReader)) {
                list = _Collections.unmodifiableListForDeveloper(list);
            }
            return list;
        }
    }


    /**
     * @see #insert(SimpleStmt, SyncStmtOption)
     */
    private int doExtractId(final ResultSet idResultSet, final GeneratedKeyStmt stmt) throws SQLException {

        try (ResultSet resultSet = idResultSet) {

            final PrimaryFieldMeta<?> idField = stmt.idField();
            final MappingType type = idField.mappingType();
            final SqlType sqlType = type.map(this.factory.serverMeta);

            final MappingEnv env = this.factory.mappingEnv;

            final int rowSize = stmt.rowSize();
            Object idValue;
            int rowIndex = 0;
            for (; resultSet.next(); rowIndex++) {
                if (rowIndex == rowSize) {
                    throw insertedRowsAndGenerateIdNotMatch(rowSize, rowIndex);
                }
                idValue = get(resultSet, 1, sqlType);
                if (idValue == null) {
                    throw _Exceptions.idValueIsNull(rowIndex, idField);
                }
                idValue = type.afterGet(sqlType, env, idValue);
                stmt.setGeneratedIdValue(rowIndex, idValue);
            }
            if (rowIndex != rowSize) {
                throw insertedRowsAndGenerateIdNotMatch(rowSize, rowIndex);
            }
            return rowIndex;
        }
    }


    /**
     * @see #executeQuery(SingleSqlStmt, SyncStmtOption, Function)
     */
    private <T> RowReader<T> createBeanRowReader(final ResultSetMetaData metaData, final Class<T> resultClass,
                                                 final SingleSqlStmt stmt) throws SQLException {
        final SqlType[] sqlTypeArray;
        sqlTypeArray = this.createSqlTypArray(metaData);
        final List<? extends Selection> selectionList = stmt.selectionList();
        final RowReader<T> rowReader;
        if ((stmt instanceof TwoStmtQueryStmt && ((TwoStmtQueryStmt) stmt).maxColumnSize() == 1)
                || selectionList.size() == 1) {
            rowReader = new SingleColumnRowReader<>(this, selectionList, sqlTypeArray, resultClass);
        } else {
            rowReader = new BeanRowReader<>(this, selectionList, resultClass, sqlTypeArray);
        }
        return rowReader;
    }



    /*################################## blow static method ##################################*/


    /**
     * @see #executeMultiStmtBatchQuery(BatchStmt, SyncStmtOption, Function)
     * @see MultiSmtBatchRowSpliterator#nextResultSet()
     */
    @Nullable
    private static ResultSet multiStatementNextResultSet(final Statement statement, final int groupIndex,
                                                         final int expectedCount) throws SQLException {
        final ResultSet resultSet;
        if (statement.getMoreResults()) {
            resultSet = statement.getResultSet();
            if (resultSet == null) {
                throw jdbcMultiStmtGetResultSetError();
            } else if (groupIndex >= 0) {
                closeResource(resultSet);
                statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
                throw multiStatementGreaterThanExpected(groupIndex, expectedCount);
            }
        } else if (statement.getUpdateCount() != -1) {
            statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
            if (groupIndex < expectedCount) {
                throw multiStatementPartNotQuery(groupIndex);
            }
            throw multiStatementGreaterThanExpected(groupIndex, expectedCount);
        } else if (groupIndex < expectedCount) {
            throw multiStatementLessThanExpected(groupIndex, expectedCount);
        } else {
            resultSet = null;
        }
        return resultSet;
    }

    static IllegalArgumentException beforeBindReturnError(SqlType sqlType, Object nonNull) {
        String m = String.format("%s beforeBind method return error type[%s] for %s.%s."
                , MappingType.class.getName(), nonNull.getClass().getName(), sqlType.database(), sqlType);
        return new IllegalArgumentException(m);
    }

    static ArmyException wrapError(final Throwable error) {
        final ArmyException e;
        if (error instanceof SQLException) {
            e = new DataAccessException(error);
        } else if (error instanceof ArmyException) {
            e = (ArmyException) error;
        } else {
            e = _Exceptions.unknownError(error);
        }
        return e;
    }

    /*-------------------below private static methods -------------------*/


    /**
     * <p>Invoke {@link PreparedStatement#executeQuery()} or {@link Statement#executeQuery(String)} for {@link ResultSet} auto close.
     *
     * @see #executeSimpleQuery(SimpleStmt, SyncStmtOption, Function)
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

    private static ArmyException handleError(final Throwable error, final @Nullable ResultSet resultSet,
                                             final @Nullable Statement statement)
            throws ArmyException {

        closeResultSetAndStatement(resultSet, statement);

        return wrapError(error);
    }

    private static void closeResultSetAndStatement(final @Nullable ResultSet resultSet,
                                                   final @Nullable Statement statement) {
        if (statement != null) {
            if (resultSet == null) {
                closeResource(statement);
            } else {
                try {
                    closeResource(resultSet);
                } finally {
                    closeResource(statement);
                }
            }

        } else if (resultSet != null) {
            closeResource(resultSet);
        }
    }


    private static void closeResource(final AutoCloseable resource)
            throws ArmyException {
        try {
            resource.close();
        } catch (Exception e) {
            throw wrapError(e);
        }

    }


    private static SQLException insertedRowsAndGenerateIdNotMatch(int insertedRows, int actualCount) {
        String m = String.format("insertedRows[%s] and generateKeys count[%s] not match.", insertedRows,
                actualCount);
        return new SQLException(m);
    }


    private static CriteriaException multiStatementGreaterThanExpected(int groupIndex, int expected) {
        String m = String.format("Multi-statement batch query ResultSet count[%s] greater than expected count[%s]",
                groupIndex, expected
        );  // here groupIndex don't plus 1 .
        throw new CriteriaException(m);
    }

    private static CriteriaException multiStatementLessThanExpected(int groupIndex, int expected) {
        String m = String.format("Multi-statement batch query ResultSet count[%s] less than expected count[%s]",
                groupIndex, expected
        );  // here groupIndex don't plus 1 .
        throw new CriteriaException(m);
    }

    private static CriteriaException multiStatementPartNotQuery(int groupIndex) {
        String m = String.format("Multi-statement batch query number %s result isn't ResultSet", groupIndex + 1);
        return new CriteriaException(m);
    }

    private static DataAccessException jdbcMultiStmtGetResultSetError() {
        String m = "Jdbc error statement.getMoreResults() is true ,but statement.getResultSet() is null";
        return new DataAccessException(m);
    }


    /**
     * <p>This class is responsible for reading a row from {@link ResultSet} with {@link #readOneRow(ResultSet)} method.
     * <p>This class is base class of following
     * <ul>
     *     <li>{@link BeanRowReader}</li>
     *     <li>{@link SingleColumnRowReader}</li>
     *     <li>{@link ObjectReader}</li>
     *     <li>{@link RecordRowReader}</li>
     * </ul>
     *
     * @param <R> row java type
     */
    private static abstract class RowReader<R> {

        final JdbcExecutor executor;

        final List<? extends Selection> selectionList;

        private final SqlType[] sqlTypeArray;

        private final Class<?> resultClass;

        private final MappingType[] compatibleTypeArray;


        private RowReader(JdbcExecutor executor, List<? extends Selection> selectionList,
                          SqlType[] sqlTypeArray, @Nullable Class<?> resultClass) {
            if (selectionList.size() != sqlTypeArray.length) {
                throw _Exceptions.columnCountAndSelectionCountNotMatch(sqlTypeArray.length, selectionList.size());
            }
            this.executor = executor;
            this.selectionList = selectionList;
            this.sqlTypeArray = sqlTypeArray;
            this.resultClass = resultClass;
            this.compatibleTypeArray = new MappingType[sqlTypeArray.length];
        }

        @Nullable
        final R readOneRow(final ResultSet resultSet) throws SQLException {

            final JdbcExecutor executor = this.executor;
            final MappingEnv env = executor.factory.mappingEnv;
            final SqlType[] sqlTypeArray = this.sqlTypeArray;
            final MappingType[] compatibleTypeArray = this.compatibleTypeArray;

            final List<? extends Selection> selectionList = this.selectionList;

            // su class create one row
            final ObjectAccessor accessor;
            accessor = createRow();

            MappingType type;
            Selection selection;
            Object columnValue;
            SqlType sqlType;
            String fieldName;

            final int columnCount = sqlTypeArray.length;
            for (int i = 0; i < columnCount; i++) {
                sqlType = sqlTypeArray[i];

                // dialect executor read one column
                columnValue = executor.get(resultSet, i + 1, sqlType);

                selection = selectionList.get(i);
                fieldName = selection.label();

                if (columnValue == null) {
                    acceptColumn(i, fieldName, null);
                    continue;
                }

                if ((type = compatibleTypeArray[i]) == null) {
                    type = compatibleTypeFrom(selection, this.resultClass, accessor, fieldName);
                    compatibleTypeArray[i] = type;
                }

                // MappingType convert one column
                columnValue = type.afterGet(sqlType, env, columnValue);

                //TODO field codec

                // sub class handle one column
                acceptColumn(i, fieldName, columnValue);

            }

            return endOneRow();
        }

        abstract ObjectAccessor createRow();

        abstract void acceptColumn(int indexBasedZero, String fieldName, @Nullable Object value);

        @Nullable
        abstract R endOneRow();


    }//RowReader

    private static final class BeanRowReader<R> extends RowReader<R> {

        private final ObjectAccessor accessor;

        private final Constructor<R> constructor;

        private R row;

        private BeanRowReader(JdbcExecutor executor, List<? extends Selection> selectionList, Class<R> resultClass,
                              SqlType[] sqlTypeArray) {
            super(executor, selectionList, sqlTypeArray, resultClass);
            this.constructor = ObjectAccessorFactory.getConstructor(resultClass);
            this.accessor = ObjectAccessorFactory.forBean(resultClass);

        }

        @Override
        ObjectAccessor createRow() {
            assert this.row == null;
            this.row = ObjectAccessorFactory.createBean(this.constructor);
            return this.accessor;
        }

        @Override
        void acceptColumn(int indexBasedZero, String fieldName, @Nullable Object value) {
            this.accessor.set(this.row, fieldName, value);
        }


        @Override
        R endOneRow() {
            final R row = this.row;
            assert row != null;
            this.row = null;
            return row;
        }


    }//BeanRowReader

    private static final class SingleColumnRowReader<R> extends RowReader<R> {

        private R row;

        private SingleColumnRowReader(JdbcExecutor executor, List<? extends Selection> selectionList,
                                      SqlType[] sqlTypeArray, Class<R> resultClass) {
            super(executor, selectionList, sqlTypeArray, resultClass);
        }

        @Override
        ObjectAccessor createRow() {
            return ObjectAccessorFactory.PSEUDO_ACCESSOR;
        }

        @SuppressWarnings("unchecked")
        @Override
        void acceptColumn(int indexBasedZero, String fieldName, @Nullable Object value) {
            assert indexBasedZero == 0;
            this.row = (R) value;
        }

        @Nullable
        @Override
        R endOneRow() {
            final R row = this.row;
            if (row != null) {
                this.row = null;
            }
            return row;
        }


    }//SingleColumnRowReader


    private static final class ObjectReader<R> extends RowReader<R> {

        private final Supplier<R> constructor;

        private final boolean twoStmtMode;

        private R row;

        private Class<?> rowJavaClass;

        private ObjectAccessor accessor;

        private ObjectReader(JdbcExecutor executor, List<? extends Selection> selectionList, boolean twoStmtMode,
                             SqlType[] sqlTypeArray,
                             Supplier<R> constructor) {
            super(executor, selectionList, sqlTypeArray, Object.class);
            this.constructor = constructor;
            this.twoStmtMode = twoStmtMode;
        }

        @Override
        ObjectAccessor createRow() {
            assert this.row == null;

            final R row;
            this.row = row = this.constructor.get();
            if (row == null) {
                throw _Exceptions.objectConstructorError();
            }

            Class<?> rowJavaClass = this.rowJavaClass;
            final ObjectAccessor accessor;
            if (rowJavaClass == null || rowJavaClass != row.getClass()) {
                this.rowJavaClass = row.getClass();
                this.accessor = accessor = ObjectAccessorFactory.fromInstance(row);
            } else {
                accessor = this.accessor;
                assert accessor != null;
            }
            return accessor;
        }

        @Override
        void acceptColumn(int indexBasedZero, String fieldName, @Nullable Object value) {
            this.accessor.set(this.row, fieldName, value);
        }


        @Override
        R endOneRow() {
            final R row = this.row;
            assert row != null;
            this.row = null;
            return row;
        }


    }//ObjectReader


    private static final class RecordRowReader<R> extends RowReader<R> implements CurrentRecord {

        private final Function<CurrentRecord, R> function;

        private final Object[] valueArray;

        private Map<String, Integer> aliasToIndexMap;

        private RecordRowReader(JdbcExecutor executor, List<? extends Selection> selectionList,
                                SqlType[] sqlTypeArray, Function<CurrentRecord, R> function) {
            super(executor, selectionList, sqlTypeArray, Object.class);
            this.function = function;
            this.valueArray = new Object[sqlTypeArray.length];
        }

        @Override
        public int getResultNo() {
            return 0;
        }

        @Override
        public ResultRecordMeta getRecordMeta() {
            return null;
        }

        @Override
        public long rowNumber() {
            return 0;
        }

        @Override
        public ResultRecord asResultRecord() {
            return null;
        }

        @Override
        public int getColumnCount() {
            return 0;
        }

        @Override
        public String getColumnLabel(int indexBasedZero) throws IllegalArgumentException {
            return null;
        }

        @Override
        public int getColumnIndex(String columnLabel) throws IllegalArgumentException {
            return 0;
        }

        @Override
        public Object get(int indexBasedZero) {
            return this.valueArray[indexBasedZero];
        }

        @Override
        public Object getNonNull(int indexBasedZero) {
            final Object value;
            value = this.valueArray[indexBasedZero];
            if (value == null) {
                throw new NullPointerException();
            }
            return value;
        }

        @Override
        public Object getOrDefault(int indexBasedZero, Object defaultValue) {
            return null;
        }

        @Override
        public Object getOrSupplier(int indexBasedZero, Supplier<?> supplier) {
            return null;
        }

        @Override
        public <T> T getOrDefault(int indexBasedZero, Class<T> columnClass, T defaultValue) {
            return null;
        }

        @Override
        public <T> T getOrSupplier(int indexBasedZero, Class<T> columnClass, Supplier<T> supplier) {
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T get(int indexBasedZero, Class<T> columnClass) {
            return (T) this.valueArray[indexBasedZero];
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getNonNull(int indexBasedZero, Class<T> columnClass) {
            final Object value;
            value = this.valueArray[indexBasedZero];
            if (value == null) {
                throw new NullPointerException();
            }
            return (T) value;
        }

        @Override
        public Object get(String selectionAlias) {
            return this.get(mapToIndex(selectionAlias));
        }

        @Override
        public Object getNonNull(String selectionAlias) {
            return this.getNonNull(mapToIndex(selectionAlias));
        }

        @Override
        public <T> T get(String selectionAlias, Class<T> columnClass) {
            return this.get(mapToIndex(selectionAlias), columnClass);
        }

        @Override
        public <T> T getNonNull(String selectionAlias, Class<T> columnClass) {
            return this.getNonNull(mapToIndex(selectionAlias), columnClass);
        }

        @Override
        public Object getOrDefault(String selectionLabel, Object defaultValue) {
            return null;
        }

        @Override
        public Object getOrSupplier(String selectionLabel, Supplier<?> supplier) {
            return null;
        }

        @Override
        public <T> T getOrDefault(String selectionLabel, Class<T> columnClass, T defaultValue) {
            return null;
        }

        @Override
        public <T> T getOrSupplier(String selectionLabel, Class<T> columnClass, Supplier<T> supplier) {
            return null;
        }

        @Override
        ObjectAccessor createRow() {
            // no bug,never here
            return ObjectAccessorFactory.PSEUDO_ACCESSOR;
        }

        @Override
        void acceptColumn(int indexBasedZero, String fieldName, @Nullable Object value) {

        }

        @Nullable
        @Override
        R endOneRow() {
            return null;
        }

        private int mapToIndex(final String selectionAlias) {
            final Object[] valueArray = this.valueArray;

            int index = -1;
            if (valueArray.length < 6) {
                final List<? extends Selection> selectionList = this.selectionList;

                for (int i = valueArray.length - 1; i > -1; i--) {  // If alias duplication,then override.
                    if (selectionList.get(i).label().equals(selectionAlias)) {
                        index = i;
                        break;
                    }
                }
            } else {
                Map<String, Integer> aliasToIndexMap = this.aliasToIndexMap;
                if (aliasToIndexMap == null) {
                    this.aliasToIndexMap = aliasToIndexMap = createAliasToIndexMap(this.selectionList);
                }
                index = aliasToIndexMap.getOrDefault(selectionAlias, -1);
            }

            if (index < 0) {
                throw _Exceptions.unknownSelectionAlias(selectionAlias);
            }
            return index;
        }


    }//RecordRowReader

    private static final class SecondRowReader<R> extends RowReader<R> {

        private R currentRow;


        private SecondRowReader(JdbcExecutor executor, List<? extends Selection> selectionList,
                                SqlType[] sqlTypeArray) {
            super(executor, selectionList, sqlTypeArray, Object.class);
        }


        @Override
        ObjectAccessor createRow() {
            final R row = this.currentRow;
            if (row == null) {
                // no bug,never here
                throw new NullPointerException();
            }
            return ObjectAccessorFactory.PSEUDO_ACCESSOR;
        }

        @Override
        void acceptColumn(int indexBasedZero, String fieldName, @Nullable Object value) {

        }

        @Nullable
        @Override
        R endOneRow() {
            return null;
        }


    }//SecondRowReader


    /**
     * <p>This class is responsible for spite rows from {@link ResultSet} to {@link Stream} with {@link #readRowStream(int, Consumer)} method.
     * <p>This class is base class of following
     * <ul>
     *     <li>{@link RowSpliterator}</li>
     *     <li>{@link JdbcBatchSpliterator}</li>
     * </ul>
     *
     * @param <R> row java type
     */
    private static abstract class JdbcRowSpliterator<R> implements Spliterator<R> {

        private final int splitSize;

        private boolean closed;

        private boolean canceled;

        int totalRowCount;

        private JdbcRowSpliterator(StreamOption option) {
            this.splitSize = option.splitSize();

        }

        @Override
        public final boolean tryAdvance(final @Nullable Consumer<? super R> action) {
            if (this.closed || this.canceled) {
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
                close();
                throw e;
            }
        }


        @Override
        public final void forEachRemaining(final @Nullable Consumer<? super R> action) {
            if (this.closed || this.canceled) {
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
                close();
                throw e;
            }
        }


        @Nullable
        @Override
        public final Spliterator<R> trySplit() {
            final int splitSize = this.splitSize;
            if (this.closed || this.canceled || splitSize < 1) {
                return null;
            }

            final List<R> rowList;
            rowList = _Collections.arrayList(Math.min(300, splitSize));

            try {
                readRowStream(0, rowList::add);
            } catch (Exception e) {
                throw handleException(e);
            } catch (Error e) {
                close();
                throw e;
            }

            final Spliterator<R> spliterator;
            if (rowList.size() == 0) {
                spliterator = null;
            } else {
                spliterator = rowList.spliterator();
            }
            return spliterator;
        }


        @Override
        public final long estimateSize() {
            return Long.MAX_VALUE;
        }


        abstract boolean readRowStream(final int rowSize, final Consumer<? super R> action) throws SQLException;

        abstract void doCloseStream();

        abstract ArmyException handleException(Exception cause);


        final void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            doCloseStream();
        }


        /**
         * <p>Read one fetch,if fetchSize is 0 ,read all row.
         *
         * @param fetchSize 0 or positive
         */
        final int readOneFetch(final ResultSet resultSet, final RowReader<R> rowReader, final int fetchSize,
                               final Consumer<? super R> action) throws SQLException {

            int readRowCount = 0;
            while (resultSet.next()) {

                action.accept(rowReader.readOneRow(resultSet));
                readRowCount++;

                if (fetchSize > 0 && readRowCount == fetchSize) {
                    break;
                }

                if (this.canceled) { // canceled must after readRowCount++; because of OptimisticLockException
                    break;
                }

                if (readRowCount < 0) {
                    this.totalRowCount = Integer.MAX_VALUE;
                    readRowCount = 1;
                }

            }

            if (this.totalRowCount != Integer.MAX_VALUE) {
                this.totalRowCount = readRowCount;
            }
            return readRowCount;
        }

        private void cancel() {
            this.canceled = true;
        }

        private static NullPointerException actionIsNull() {
            return new NullPointerException("Action consumer is null");
        }


    }//JdbcRowSpliterator


    private static final class RowSpliterator<R> extends JdbcRowSpliterator<R> {

        private final Statement statement;

        private final ResultSet resultSet;

        private final RowReader<R> rowReader;

        private final boolean hasOptimistic;

        /**
         * @see #executeSimpleQuery(SimpleStmt, SyncStmtOption, Function)
         */
        private RowSpliterator(Statement statement, ResultSet resultSet, RowReader<R> rowReader,
                               SimpleStmt stmt, StreamOption option) {
            super(option);
            this.statement = statement;
            this.resultSet = resultSet;
            this.rowReader = rowReader;
            this.hasOptimistic = stmt.hasOptimistic();

        }

        @Override
        public int characteristics() {
            int bits = 0;
            if (!(this.rowReader instanceof SingleColumnRowReader)) {
                bits |= NONNULL;
            }
            return bits;
        }

        /**
         * @see #tryAdvance(Consumer)
         * @see #forEachRemaining(Consumer)
         */
        boolean readRowStream(final int rowSize, final Consumer<? super R> action) throws SQLException {
            final int readRowCount;
            readRowCount = readOneFetch(this.resultSet, this.rowReader, rowSize, action);
            if (readRowCount == 0 && this.hasOptimistic && this.totalRowCount == 0) {
                throw _Exceptions.optimisticLock();
            }
            return readRowCount > 0;
        }

        @Override
        void doCloseStream() {
            closeResultSetAndStatement(this.resultSet, this.statement);
        }


        @Override
        ArmyException handleException(Exception cause) {
            close();
            return this.rowReader.executor.handleException(cause);
        }


    }//RowSpliterator


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
    private static abstract class JdbcBatchSpliterator<R> extends JdbcRowSpliterator<R> {

        final Statement statement;

        final RowReader<R> rowReader;

        final BatchStmt stmt;

        final SyncStmtOption option;

        private ResultSet resultSet;

        private JdbcBatchSpliterator(Statement statement, RowReader<R> rowReader,
                                     BatchStmt stmt, SyncStmtOption option, ResultSet resultSet) {
            super(option);

            this.statement = statement;
            this.rowReader = rowReader;
            this.stmt = stmt;
            this.option = option;

            this.resultSet = resultSet;

        }

        @Override
        public final int characteristics() {
            int bits = 0;
            if (!(this.rowReader instanceof SingleColumnRowReader)) {
                bits |= NONNULL;
            }
            return bits;
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


        @Override
        final boolean readRowStream(final int rowSize, final Consumer<? super R> action) throws SQLException {
            final boolean hasOptimistic = this.stmt.hasOptimistic();
            final RowReader<R> rowReader = this.rowReader;

            ResultSet resultSet = this.resultSet;
            int readCount = 0;
            while ((resultSet != null)) {

                readCount = readOneFetch(resultSet, rowReader, rowSize, action);
                if (readCount > 0) {
                    break;
                }
                if (hasOptimistic && this.totalRowCount == 0) {
                    throw _Exceptions.optimisticLock();
                }
                this.resultSet = null; // firstly clear
                closeResource(resultSet); // secondly close
                this.resultSet = resultSet = nextResultSet();
            }
            return readCount > 0;
        }

        @Nullable
        abstract ResultSet nextResultSet() throws SQLException, TimeoutException;


    } // BatchJdbcSpliterator


    private static final class BatchRowSpliterator<R> extends JdbcBatchSpliterator<R> {

        private int groupIndex = 1; // here from 1 not 0.

        /**
         * @see JdbcExecutor##executeBatchQuery(BatchStmt, SyncStmtOption, Function)
         */
        private BatchRowSpliterator(PreparedStatement statement, RowReader<R> rowReader,
                                    BatchStmt stmt, SyncStmtOption option, ResultSet resultSet) {
            super(statement, rowReader, stmt, option, resultSet);

        }


        @Override
        ArmyException handleException(Exception cause) {
            close();
            return this.rowReader.executor.handleException(cause);
        }

        @Nullable
        ResultSet nextResultSet() throws SQLException, TimeoutException {

            final BatchStmt stmt = this.stmt;

            final List<List<SQLParam>> paramGroupList = stmt.groupList();
            final int groupIndex = this.groupIndex++; // groupIndex from 1 not 0

            if (groupIndex >= paramGroupList.size()) {
                // here don't close statement, see close()
                return null;
            }
            final JdbcExecutor executor = this.rowReader.executor;
            final PreparedStatement statement = (PreparedStatement) this.statement;

            statement.clearParameters();
            executor.bindParameter(statement, paramGroupList.get(groupIndex));
            executor.bindStatementOption(statement, stmt, this.option);

            return statement.executeQuery();
        }


    }//BatchRowSpliterator

    private static final class MultiSmtBatchRowSpliterator<R> extends JdbcBatchSpliterator<R> {

        private int groupIndex = 1; // here from 1 not 0.

        /**
         * @see JdbcExecutor#executeMultiStmtBatchQuery(BatchStmt, SyncStmtOption, Function)
         */
        private MultiSmtBatchRowSpliterator(Statement statement, RowReader<R> rowReader, BatchStmt stmt,
                                            SyncStmtOption option, ResultSet resultSet) {
            super(statement, rowReader, stmt, option, resultSet);
        }

        @Override
        ArmyException handleException(Exception cause) {
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
            return this.rowReader.executor.handleException(cause);
        }

        @Nullable
        @Override
        ResultSet nextResultSet() throws SQLException, TimeoutException {
            final Statement statement = this.statement;

            final int groupIndex = this.groupIndex++, expectedCount; // groupIndex from 1 not 0
            expectedCount = this.stmt.groupList().size();

            return multiStatementNextResultSet(statement, groupIndex, expectedCount);
        }


    } // MultiSmtBatchRowSpliterator


}
