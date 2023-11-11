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
import io.army.session.executor.ExecutorSupport;
import io.army.session.executor.StmtExecutor;
import io.army.session.record.CurrentRecord;
import io.army.session.record.DataRecord;
import io.army.session.record.ResultStates;
import io.army.sqltype.DataType;
import io.army.sqltype.SqlType;
import io.army.stmt.*;
import io.army.sync.StreamCommander;
import io.army.sync.StreamOption;
import io.army.sync.SyncStmtCursor;
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
abstract class JdbcExecutor extends JdbcExecutorSupport implements SyncStmtExecutor {

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
    public final boolean isDriverAssignableTo(Class<?> spiClass) {
        return spiClass.isAssignableFrom(this.conn.getClass());
    }

    @Override
    public final <T> T getDriverSpi(Class<T> spiClass) {
        this.driverSpiOpened = Connection.class.isAssignableFrom(spiClass);
        return spiClass.cast(this.conn);
    }


    @Override
    public final Object setSavePoint(Function<Option<?>, ?> optionFunc) throws DataAccessException {
        final Object name;
        if (optionFunc == Option.EMPTY_OPTION_FUNC) {
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
    public final void releaseSavePoint(final Object savepoint, final Function<Option<?>, ?> optionFunc)
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
    public final void rollbackToSavePoint(Object savepoint, Function<Option<?>, ?> optionFunc)
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


    @SuppressWarnings("unchecked")
    @Override
    public final <R> R insert(final SimpleStmt stmt, final SyncStmtOption option, final Class<R> resultClass)
            throws DataAccessException {

        if (resultClass != Long.class && resultClass != ResultStates.class) {
            throw new IllegalArgumentException();
        }

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
                    rows = readRowId(((PreparedStatement) statement).executeQuery(), (GeneratedKeyStmt) stmt);
                } else {
                    rows = readRowId(statement.executeQuery(stmt.sqlText()), (GeneratedKeyStmt) stmt);
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
                    readRowId(statement.getGeneratedKeys(), (GeneratedKeyStmt) stmt);
                }
            }

            final R r;
            if (resultClass == Long.class) {
                r = (R) Long.valueOf(rows);
            } else {
                r = (R) new SingleUpdateStates(obtainTransaction(), mapToArmyWarning(statement.getWarnings()), rows);
            }
            return r;
        } catch (Exception e) {
            throw wrapError(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <R> R update(SimpleStmt stmt, SyncStmtOption option, Class<R> resultClass, Function<Option<?>, ?> optionFunc)
            throws DataAccessException {

        if (resultClass != Long.class && resultClass != ResultStates.class) {
            throw new IllegalArgumentException();
        }
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

            final R r;
            if (resultClass == Long.class) {
                r = (R) Long.valueOf(rows);
            } else if (optionFunc != Option.EMPTY_OPTION_FUNC
                    && Boolean.TRUE.equals(optionFunc.apply(SyncStmtCursor.SYNC_STMT_CURSOR))) {
                r = (R) createNamedCursor(statement, rows, optionFunc);
            } else {
                r = (R) new SingleUpdateStates(obtainTransaction(), mapToArmyWarning(statement.getWarnings()), rows);
            }
            return r;
        } catch (Exception e) {
            throw wrapError(e);
        }

    }


    @Override
    public final <R> List<R> batchUpdateList(BatchStmt stmt, IntFunction<List<R>> listConstructor,
                                             SyncStmtOption option, Class<R> elementClass,
                                             @Nullable TableMeta<?> domainTable, @Nullable List<R> rowsList)
            throws DataAccessException {
        if (elementClass != Long.class && elementClass != ResultStates.class) {
            throw new IllegalArgumentException("elementClass error");
        }
        final List<R> resultList;
        if (option.isParseBatchAsMultiStmt()) {
            resultList = executeMultiStmtBatchUpdate(stmt, listConstructor, option, elementClass, domainTable);
        } else {
            resultList = executeBatchUpdate(stmt, listConstructor, option, elementClass, domainTable, rowsList);
        }
        return resultList;
    }


    @Override
    public final <R> Stream<R> batchUpdate(BatchStmt stmt, SyncStmtOption option, Class<R> elementClass,
                                           @Nullable TableMeta<?> domainTable, @Nullable List<R> rowsList)
            throws DataAccessException {
        return batchUpdateList(stmt, _Collections::arrayList, option, elementClass, domainTable, rowsList)
                .stream();
    }

    @Nullable
    @Override
    public final <R> R queryOne(SimpleStmt stmt, Class<R> resultClass, SyncStmtOption option)
            throws DataAccessException {
        return executeQueryOne(stmt, option, beanReaderFunc(stmt, resultClass));
    }

    @Nullable
    @Override
    public final <R> R queryOneObject(SimpleStmt stmt, Supplier<R> constructor, SyncStmtOption option)
            throws DataAccessException {
        return this.executeQueryOne(stmt, option, objectReaderFunc(stmt, constructor));
    }

    @Nullable
    @Override
    public final <R> R queryOneRecord(SimpleStmt stmt, Function<CurrentRecord, R> function, SyncStmtOption option)
            throws DataAccessException {
        return this.executeQueryOne(stmt, option, recordReaderFunc(stmt.selectionList(), function));
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

    @Override
    public final <R> Stream<R> secondQuery(TwoStmtQueryStmt stmt, SyncStmtOption option, List<R> firstList)
            throws DataAccessException {

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = bindStatement(stmt, option);

            bindStatementOption(statement, stmt, option);

            resultSet = jdbcExecuteQuery(statement, stmt.sqlText());

            final SecondRowReader<R> rowReader;
            rowReader = new SecondRowReader<>(this, stmt.selectionList(), createSqlTypArray(resultSet.getMetaData()));

            final SimpleSecondSpliterator<R> spliterator;
            spliterator = new SimpleSecondSpliterator<>(statement, resultSet, rowReader, stmt, option, firstList);

            return assembleStream(spliterator, option);
        } catch (Exception e) {
            closeResultSetAndStatement(resultSet, statement);
            throw handleException(e);
        } catch (Throwable e) {
            closeResultSetAndStatement(resultSet, statement);
            throw e;
        }

    }

    @Override
    public final <R> Stream<R> pairBatchQuery(PairBatchStmt stmt, final @Nullable Class<R> resultClass,
                                              SyncStmtOption option, boolean firstIsQuery, ChildTableMeta<?> childTable)
            throws DataAccessException {
        if (resultClass == null) {
            throw new NullPointerException();
        }
        final BiFunction<BatchStmt, ResultSetMetaData, RowReader<R>> readerFunc;
        readerFunc = (queryStmt, meta) -> {
            try {
                return createBeanRowReader(meta, resultClass, queryStmt);
            } catch (Exception e) {
                throw handleException(e);
            }
        };
        return executePairBatchQuery(stmt, option, firstIsQuery, childTable, readerFunc);
    }

    @Override
    public final <R> Stream<R> pairBatchQueryObject(PairBatchStmt stmt, final @Nullable Supplier<R> constructor,
                                                    SyncStmtOption option, boolean firstIsQuery, ChildTableMeta<?> childTable)
            throws DataAccessException {
        if (constructor == null) {
            throw new NullPointerException();
        }
        final BiFunction<BatchStmt, ResultSetMetaData, RowReader<R>> readerFunc;
        readerFunc = (queryStmt, meta) -> {
            try {
                return new ObjectReader<>(this, queryStmt.selectionList(), queryStmt instanceof TwoStmtModeQuerySpec,
                        createSqlTypArray(meta), constructor
                );
            } catch (Exception e) {
                throw handleException(e);
            }
        };
        return executePairBatchQuery(stmt, option, firstIsQuery, childTable, readerFunc);
    }

    @Override
    public final <R> Stream<R> pairBatchQueryRecord(PairBatchStmt stmt, final @Nullable Function<CurrentRecord, R> function,
                                                    SyncStmtOption option, boolean firstIsQuery,
                                                    ChildTableMeta<?> childTable)
            throws DataAccessException {

        if (function == null) {
            throw new NullPointerException();
        }
        final BiFunction<BatchStmt, ResultSetMetaData, RowReader<R>> readerFunc;
        readerFunc = (queryStmt, meta) -> {
            try {
                return new RecordRowReader<>(this, queryStmt.selectionList(), createSqlTypArray(meta), function, meta);
            } catch (Exception e) {
                throw handleException(e);
            }
        };
        return executePairBatchQuery(stmt, option, firstIsQuery, childTable, readerFunc);
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
    abstract TransactionInfo obtainTransaction();

    /**
     * @see #update(SimpleStmt, SyncStmtOption, Class, Function)
     */
    @SuppressWarnings("unused")
    ResultStates createNamedCursor(Statement statement, long rows, Function<Option<?>, ?> optionFunc) {
        throw new UnsupportedOperationException();
    }

    final Stream<Xid> jdbcRecover(final String sql, Function<DataRecord, Xid> function, StreamOption option) {

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
            spliterator = new XidRowSpliterator(this, option, statement, resultSet, dataTypeArray, function);

            final Consumer<StreamCommander> consumer;
            consumer = option.streamCommanderConsumer();
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
        return this.factory.handleException(cause);
    }

    /*################################## blow private method ##################################*/


    /**
     * @see #batchUpdateList(BatchStmt, IntFunction, SyncStmtOption, Class, TableMeta, List)
     */
    private <R> List<R> executeBatchUpdate(final BatchStmt stmt, final IntFunction<List<R>> listConstructor,
                                           final SyncStmtOption option, final Class<R> elementClass,
                                           final @Nullable TableMeta<?> domainTable, final @Nullable List<R> rowsList) {
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

            final List<R> resultList;

            if (this.factory.useLargeUpdate) {
                final long[] affectedRows;
                affectedRows = statement.executeLargeBatch();
                resultList = handleBatchResult(statement.getWarnings(), elementClass, stmt.hasOptimistic(), affectedRows.length,
                        index -> affectedRows[index], listConstructor, domainTable, rowsList
                );
            } else {
                final int[] affectedRows;
                affectedRows = statement.executeBatch();
                resultList = handleBatchResult(statement.getWarnings(), elementClass, stmt.hasOptimistic(), affectedRows.length,
                        index -> affectedRows[index], listConstructor, domainTable, rowsList
                );
            }

            return resultList;
        } catch (Exception e) {
            throw wrapError(e);
        }
    }

    /**
     * @see #batchUpdateList(BatchStmt, IntFunction, SyncStmtOption, Class, TableMeta, List)
     */
    private <R> List<R> executeMultiStmtBatchUpdate(final BatchStmt stmt, final IntFunction<List<R>> listConstructor,
                                                    SyncStmtOption option, Class<R> elementClass,
                                                    final @Nullable TableMeta<?> domainTable) {
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
            final List<R> list = listConstructor.apply(stmtSize);
            if (list == null) {
                throw _Exceptions.listConstructorError();
            }

            if (domainTable instanceof ChildTableMeta) {
                handleChildMultiStmtBatchUpdate(statement, stmt, elementClass, (ChildTableMeta<?>) domainTable, list);
            } else {
                // SingleTableMeta batch update or multi-table batch update.
                handleSimpleMultiStmtBatchUpdate(statement, stmt, elementClass, domainTable, list);
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
     * @see #insert(SimpleStmt, SyncStmtOption, Class)
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
     * @see #update(SimpleStmt, SyncStmtOption, Class, Function)
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

        if (stmt.stmtType() == StmtType.QUERY && stmt.selectionList().size() > 0) {
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
     * @see #query(SingleSqlStmt, Class, SyncStmtOption)
     */
    private <R> Function<ResultSetMetaData, RowReader<R>> beanReaderFunc(final SingleSqlStmt stmt,
                                                                         final @Nullable Class<R> resultClass) {
        if (resultClass == null) {
            throw new NullPointerException();
        }
        return metaData -> {
            try {
                return createBeanRowReader(metaData, resultClass, stmt);
            } catch (Exception e) {
                throw handleException(e);
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
            } catch (Exception e) {
                throw handleException(e);
            }
        };
    }

    /**
     * @see #queryRecord(SingleSqlStmt, Function, SyncStmtOption)
     */
    private <R> Function<ResultSetMetaData, RowReader<R>> recordReaderFunc(
            final List<? extends Selection> selectionList,
            final @Nullable Function<CurrentRecord, R> function) {
        if (function == null) {
            throw new NullPointerException();
        }
        return meta -> {
            try {
                return new RecordRowReader<>(this, selectionList, createSqlTypArray(meta), function, meta);
            } catch (Exception e) {
                throw handleException(e);
            }
        };
    }


    /**
     * @see #insert(SimpleStmt, SyncStmtOption, Class)
     * @see #update(SimpleStmt, SyncStmtOption, Class, Function)
     * @see #executeSimpleQuery(SimpleStmt, SyncStmtOption, Function)
     * @see #executeBatchQuery(BatchStmt, SyncStmtOption, Function)
     * @see #executeBatchUpdate(BatchStmt, IntFunction, SyncStmtOption, Class, TableMeta, List)
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
     * @see #queryOne(SimpleStmt, Class, SyncStmtOption)
     * @see #queryOneObject(SimpleStmt, Supplier, SyncStmtOption)
     * @see #queryOneRecord(SimpleStmt, Function, SyncStmtOption)
     */
    @Nullable
    private <R> R executeQueryOne(final SimpleStmt stmt, final SyncStmtOption option,
                                  final Function<ResultSetMetaData, RowReader<R>> function)
            throws DataAccessException {

        try (Statement statement = bindStatement(stmt, option)) {

            try (ResultSet resultSet = jdbcExecuteQuery(statement, stmt.sqlText())) {

                final RowReader<R> rowReader;
                rowReader = function.apply(resultSet.getMetaData());

                final R row;

                if (resultSet.next()) {
                    if (stmt instanceof GeneratedKeyStmt) {
                        readOneInsertRowId(resultSet, rowReader, (GeneratedKeyStmt) stmt);
                    }
                    row = rowReader.readOneRow(resultSet);
                    if (resultSet.next()) {
                        throw new CriteriaException("Database response more than one row");
                    }
                } else {
                    row = null;
                }

                final Consumer<ResultStates> consumer;
                consumer = option.stateConsumer();
                if (consumer != ResultStates.IGNORE_STATES) {
                    final Warning w;
                    w = mapToArmyWarning(statement.getWarnings());
                    final long affectedRows;
                    if (stmt.stmtType() == StmtType.QUERY) {
                        affectedRows = 0L;
                    } else {
                        affectedRows = 1L;
                    }
                    consumer.accept(new SingleQueryStates(obtainTransaction(), w, 1L, false, affectedRows));
                }
                return row;
            }
        } catch (Exception e) {
            throw handleException(e);
        }

    }


    /**
     * @see #executeQueryOne(SimpleStmt, SyncStmtOption, Function)
     */
    private void readOneInsertRowId(final ResultSet resultSet, final RowReader<?> rowReader, final GeneratedKeyStmt stmt)
            throws SQLException {

        if (stmt.rowSize() != 1) {
            // no bug, never here
            String m = String.format("insert row number[%s] not 1", stmt.rowSize());
            throw new CriteriaException(m);
        }

        final PrimaryFieldMeta<?> idField = stmt.idField();
        final MappingType type = idField.mappingType();
        final MappingEnv env = this.factory.mappingEnv;
        final int idSelectionIndex = stmt.idSelectionIndex();

        final SqlType idSqlType = rowReader.sqlTypeArray[idSelectionIndex];

        Object idValue;
        // below read id value
        idValue = get(resultSet, idSelectionIndex + 1, idSqlType); // read id column
        if (idValue == null) {
            throw _Exceptions.idValueIsNull(0, idField);
        }
        idValue = type.afterGet(idSqlType, env, idValue); // MappingType convert id column
        stmt.setGeneratedIdValue(0, idValue);     // set id column

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

            final JdbcSimpleSpliterator<R> spliterator;
            if (stmt instanceof GeneratedKeyStmt) {
                spliterator = new InsertRowSpliterator<>(statement, resultSet, rowReader, (GeneratedKeyStmt) stmt, option);
            } else {
                spliterator = new SimpleRowSpliterator<>(statement, resultSet, rowReader, stmt, option);
            }

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
    private <R> Stream<R> assembleStream(final JdbcRowSpliterator<R> spliterator, final SyncStmtOption option) {
        final Consumer<StreamCommander> consumer;
        consumer = option.streamCommanderConsumer();
        if (consumer != null) {
            consumer.accept(spliterator::cancel); // cancel event
        }
        return StreamSupport.stream(spliterator, false)
                .onClose(spliterator::close); // close event
    }


    /**
     * @see #executeBatchUpdate(BatchStmt, IntFunction, SyncStmtOption, Class, TableMeta, List)
     */
    @SuppressWarnings("unchecked")
    private <R> List<R> handleBatchResult(final @Nullable SQLWarning warning, final Class<R> elementClass,
                                          final boolean optimistic, final int bathSize,
                                          final IntToLongFunction accessor, final IntFunction<List<R>> listConstructor,
                                          final @Nullable TableMeta<?> domainTable, final @Nullable List<R> rowsList) {
        assert rowsList == null || domainTable instanceof ChildTableMeta;

        final boolean longElement = elementClass == Long.class;

        final List<R> list;
        final boolean newList;
        if (rowsList == null) {
            list = listConstructor.apply(bathSize);
            if (list == null) {
                throw _Exceptions.listConstructorError();
            }
            newList = true;
        } else if (rowsList.size() != bathSize) { // here bathSize representing parent's bathSize ,because army update child and update parent
            throw _Exceptions.childBatchSizeError((ChildTableMeta<?>) domainTable, rowsList.size(), bathSize);
        } else if (longElement) {
            list = rowsList;
            newList = false;
        } else {
            list = listConstructor.apply(bathSize);
            if (list == null) {
                throw _Exceptions.listConstructorError();
            }
            newList = true;
        }

        final TransactionInfo info;
        final Warning armyWarning;
        if (longElement) {
            info = null;
            armyWarning = null;
        } else {
            info = obtainTransaction();
            armyWarning = mapToArmyWarning(warning);
        }

        long rows;
        for (int i = 0, resultNo = 1; i < bathSize; i++, resultNo++) {
            rows = accessor.applyAsLong(i);
            if (optimistic && rows == 0L) {
                throw _Exceptions.batchOptimisticLock(domainTable, resultNo, rows);
            }

            if (newList) {
                if (longElement) {
                    list.add((R) Long.valueOf(rows));
                } else {
                    list.add((R) new MultiResultUpdateStates(resultNo, info, armyWarning, rows, resultNo < bathSize));
                }
            }

            if (rowsList == null) {
                continue;
            }

            if (longElement) {
                if (rows != (Long) rowsList.get(i)) { // here rows representing parent's rows,because army update child and update parent
                    throw _Exceptions.batchChildUpdateRowsError((ChildTableMeta<?>) domainTable, i + 1, (Long) rowsList.get(i),
                            rows);
                }
            } else if (rows != ((ResultStates) rowsList.get(i)).affectedRows()) {
                throw _Exceptions.batchChildUpdateRowsError((ChildTableMeta<?>) domainTable, i + 1, (Long) rowsList.get(i),
                        rows);
            }

        }

        return list;
    }


    /**
     * @see #executeMultiStmtBatchUpdate(BatchStmt, IntFunction, SyncStmtOption, Class, TableMeta)
     */
    @SuppressWarnings("unchecked")
    private <R> void handleChildMultiStmtBatchUpdate(final Statement statement, final BatchStmt stmt,
                                                     final Class<R> elementClass, final ChildTableMeta<?> domainTable,
                                                     final List<R> list)
            throws SQLException {

        final boolean useLargeUpdate = this.factory.useLargeUpdate;
        final boolean optimistic = stmt.hasOptimistic();
        final boolean longElement = elementClass == Long.class;

        final TransactionInfo info;
        final Warning warning;
        if (longElement) {
            info = null;
            warning = null;
        } else {
            info = obtainTransaction();
            warning = mapToArmyWarning(statement.getWarnings());
        }

        final int itemSize, itemPairSize;
        itemSize = stmt.groupList().size();
        if ((itemSize & 1) != 0) {
            // no bug,never here
            throw new IllegalArgumentException(String.format("item count[%s] not event", itemSize));
        }
        itemPairSize = itemSize << 1;

        long itemRows = 0, rows;
        for (int i = 0, resultNo = 1; i < itemPairSize; i++) {

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
            }

            if ((i & 1) == 0) { // this code block representing child's update rows,because army update child and update parent
                itemRows = rows;
                if (longElement) {
                    list.add((R) Long.valueOf(rows));
                } else {
                    list.add((R) new MultiResultUpdateStates(resultNo, info, warning, rows, resultNo < itemPairSize));
                }
                resultNo++;
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
     * @see #executeMultiStmtBatchUpdate(BatchStmt, IntFunction, SyncStmtOption, Class, TableMeta)
     */
    @SuppressWarnings("unchecked")
    private <R> void handleSimpleMultiStmtBatchUpdate(final Statement statement, final BatchStmt stmt,
                                                      final Class<R> elementClass, final @Nullable TableMeta<?> domainTable,
                                                      final List<R> list) throws SQLException {

        assert domainTable == null || domainTable instanceof SingleTableMeta;

        final boolean useLargeUpdate = this.factory.useLargeUpdate;
        final boolean optimistic = stmt.hasOptimistic();
        final boolean longElement = elementClass == Long.class;
        final int itemSize = stmt.groupList().size();

        final TransactionInfo info;
        final Warning warning;
        if (longElement) {
            info = null;
            warning = null;
        } else {
            info = obtainTransaction();
            warning = mapToArmyWarning(statement.getWarnings());
        }

        long rows;
        for (int i = 0, resultNo = 1; i < itemSize; i++, resultNo++) {
            if (statement.getMoreResults()) {
                statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
                throw _Exceptions.batchUpdateReturnResultSet(domainTable, resultNo);
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
                throw _Exceptions.batchOptimisticLock(domainTable, resultNo, rows);
            }

            if (longElement) {
                list.add((R) Long.valueOf(rows));
            } else {
                list.add((R) new MultiResultUpdateStates(resultNo, info, warning, rows, resultNo < itemSize));
            }

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
     * @return row number
     * @see #insert(SimpleStmt, SyncStmtOption, Class)
     */
    private int readRowId(final ResultSet idResultSet, final GeneratedKeyStmt stmt) throws SQLException {

        try (ResultSet resultSet = idResultSet) {

            final PrimaryFieldMeta<?> idField = stmt.idField();
            final MappingType type = idField.mappingType();
            final int idSelectionIndex = stmt.idSelectionIndex();
            final int idColumnIndexBaseOne = idSelectionIndex + 1;

            final SqlType sqlType;
            sqlType = getSqlType(idResultSet.getMetaData(), idColumnIndexBaseOne);

            final MappingEnv env = this.factory.mappingEnv;

            final int rowSize = stmt.rowSize();
            Object idValue;
            int rowIndex = 0;
            for (; resultSet.next(); rowIndex++) {
                if (rowIndex == rowSize) {
                    throw insertedRowsAndGenerateIdNotMatch(rowSize, rowIndex);
                }
                idValue = get(resultSet, idColumnIndexBaseOne, sqlType);
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
     * @see #pairBatchQuery(PairBatchStmt, Class, SyncStmtOption, boolean, ChildTableMeta)
     * @see #pairBatchQueryObject(PairBatchStmt, Supplier, SyncStmtOption, boolean, ChildTableMeta)
     * @see #pairBatchQueryRecord(PairBatchStmt, Function, SyncStmtOption, boolean, ChildTableMeta)
     */
    private <R> Stream<R> executePairBatchQuery(Stmt.PairStmtSpec stmt, SyncStmtOption option, boolean firstIsQuery,
                                                ChildTableMeta<?> childTable,
                                                BiFunction<BatchStmt, ResultSetMetaData, RowReader<R>> readerFunc) {
        try {
            final Stream<R> stream;
            if (stmt.stmtType() == StmtType.INSERT) {
                stream = executeBatchInsertQuery((PairBatchStmt) stmt, option, firstIsQuery, childTable, readerFunc);
            } else {
                stream = executeBatchUpdateQuery((PairBatchStmt) stmt, option, firstIsQuery, childTable, readerFunc);
            }
            return stream;
        } catch (Exception e) {
            throw handleException(e);
        }
    }


    /**
     * @see #executePairBatchQuery(Stmt.PairStmtSpec, SyncStmtOption, boolean, ChildTableMeta, BiFunction)
     */
    @SuppressWarnings("unused")
    private <R> Stream<R> executeBatchInsertQuery(PairBatchStmt stmt, SyncStmtOption option, boolean firstIsQuery,
                                                  ChildTableMeta<?> childTable,
                                                  BiFunction<BatchStmt, ResultSetMetaData, RowReader<R>> readerFunc) {
        // TODO add for firebird
        throw new UnsupportedOperationException();
    }

    /**
     * @see #executePairBatchQuery(Stmt.PairStmtSpec, SyncStmtOption, boolean, ChildTableMeta, BiFunction)
     */
    @SuppressWarnings("unused")
    private <R> Stream<R> executeBatchUpdateQuery(PairBatchStmt stmt, SyncStmtOption option, boolean firstIsQuery,
                                                  ChildTableMeta<?> childTable,
                                                  BiFunction<BatchStmt, ResultSetMetaData, RowReader<R>> readerFunc) {
        // TODO add for firebird
        throw new UnsupportedOperationException();
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


    private static DataAccessException insertedRowsAndGenerateIdNotMatch(int insertedRows, int actualCount) {
        String m = String.format("insertedRows[%s] and generateKeys count[%s] not match.", insertedRows,
                actualCount);
        return new DataAccessException(m);
    }


    /*-------------------below static class -------------------*/


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
    private static abstract class RowReader<R> extends ArmyStmtCurrentRecord {

        final JdbcExecutor executor;

        final List<? extends Selection> selectionList;

        final SqlType[] sqlTypeArray;

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

        @Override
        protected Object[] copyValueArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long rowNumber() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ArmyResultRecordMeta getRecordMeta() {
            throw new UnsupportedOperationException();
        }

        @Nullable
        @Override
        public Object get(int indexBasedZero) {
            throw new UnsupportedOperationException();
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
                    if (this instanceof RecordRowReader) {
                        type = selection.typeMeta().mappingType();
                    } else {
                        type = compatibleTypeFrom(selection, this.resultClass, accessor, fieldName);
                    }
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
            return SINGLE_COLUMN_PSEUDO_ACCESSOR;
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


        @SuppressWarnings("unchecked")
        @Override
        R endOneRow() {
            R row = this.row;
            assert row != null;
            this.row = null;

            if (row instanceof Map && row instanceof ImmutableSpec && !this.twoStmtMode) {
                row = (R) _Collections.unmodifiableMapForDeveloper((Map<?, ?>) row);
            }
            return row;
        }


    }//ObjectReader


    private static final class RecordRowReader<R> extends RowReader<R> implements CurrentRecord {

        private final JdbcStmtRecordMeta meta;

        private final Function<CurrentRecord, R> function;

        private final Object[] valueArray;

        private long rowNumber = 0L;

        /**
         * @see JdbcExecutor#recordReaderFunc(List, Function)
         */
        private RecordRowReader(JdbcExecutor executor, List<? extends Selection> selectionList,
                                SqlType[] sqlTypeArray, Function<CurrentRecord, R> function, ResultSetMetaData meta) {
            super(executor, selectionList, sqlTypeArray, Object.class);
            this.function = function;
            this.valueArray = new Object[sqlTypeArray.length];
            this.meta = new JdbcStmtRecordMeta(1, executor, sqlTypeArray, selectionList, meta);
        }

        private RecordRowReader(RecordRowReader<R> r, ResultSetMetaData meta) {
            super(r.executor, r.selectionList, r.sqlTypeArray, Object.class);
            this.function = r.function;
            this.valueArray = new Object[r.sqlTypeArray.length];
            this.meta = new JdbcStmtRecordMeta(r.meta.getResultNo() + 1, r.executor, r.sqlTypeArray, r.selectionList, meta);
        }


        @Override
        public ArmyResultRecordMeta getRecordMeta() {
            return this.meta;
        }

        @Override
        public long rowNumber() {
            return this.rowNumber;
        }


        @Override
        public Object get(int indexBasedZero) {
            return this.valueArray[indexBasedZero];
        }

        /*-------------------below protected -------------------*/

        @Override
        protected Object[] copyValueArray() {
            final Object[] array = new Object[this.valueArray.length];
            System.arraycopy(this.valueArray, 0, array, 0, array.length);
            return array;
        }


        /*-------------------below package methods -------------------*/

        @Override
        ObjectAccessor createRow() {
            // just return accessor
            this.rowNumber++;
            return RECORD_PSEUDO_ACCESSOR;
        }

        @Override
        void acceptColumn(int indexBasedZero, String fieldName, @Nullable Object value) {
            this.valueArray[indexBasedZero] = value;
        }

        @Nullable
        @Override
        public R endOneRow() {
            final R r;
            r = this.function.apply(this);
            if (r instanceof CurrentRecord) {
                throw _Exceptions.recordFuncError(this.function, this);
            }
            return r;
        }


    } //RecordRowReader

    private static final class SecondRowReader<R> extends RowReader<R> {

        /**
         * @see SimpleSecondSpliterator#readRowStream(int, Consumer)
         */
        private R currentRow;

        /**
         * @see SimpleSecondSpliterator#readRowStream(int, Consumer)
         */
        private ObjectAccessor accessor;

        /**
         * @see JdbcExecutor#secondQuery(TwoStmtQueryStmt, SyncStmtOption, List)
         */
        private SecondRowReader(JdbcExecutor executor, List<? extends Selection> selectionList,
                                SqlType[] sqlTypeArray) {
            super(executor, selectionList, sqlTypeArray, Object.class);
        }

        /**
         * @see SimpleSecondSpliterator#readRowStream(int, Consumer)
         */
        @Override
        ObjectAccessor createRow() {
            if (this.currentRow == null) {
                // no bug,never here
                throw new NullPointerException();
            }
            final ObjectAccessor accessor = this.accessor;
            if (accessor == null) {
                throw new NullPointerException();
            }
            return accessor;
        }

        @Override
        void acceptColumn(final int indexBasedZero, String fieldName, @Nullable Object value) {
            final ObjectAccessor accessor = this.accessor;
            if (accessor != ExecutorSupport.SINGLE_COLUMN_PSEUDO_ACCESSOR) {
                accessor.set(this.currentRow, fieldName, value);
            } else if (this.currentRow.equals(value)) { // single id row
                assert indexBasedZero == 0;
            } else {
                String m = String.format("child and parent column[%s] id not equals", fieldName);
                throw new CriteriaException(m);
            }
        }


        @SuppressWarnings("unchecked")
        @Override
        R endOneRow() {
            R row = this.currentRow;
            assert row != null;
            this.currentRow = null;

            if (row instanceof Map && row instanceof ImmutableSpec) {
                row = (R) _Collections.unmodifiableMapForDeveloper((Map<?, ?>) row);
            }
            return row;
        }


    }//SecondRowReader


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
    private static abstract class JdbcRowSpliterator<R> implements Spliterator<R> {

        final Statement statement;

        final int fetchSize;
        private final SyncStmtOption option;

        private final StmtType stmtType;

        private boolean closed;

        boolean canceled;

        private JdbcRowSpliterator(Statement statement, StmtType stmtType, SyncStmtOption option) {
            this.statement = statement;
            this.option = option;
            this.stmtType = stmtType;
            this.fetchSize = option.fetchSize();
            assert this.fetchSize > -1;
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
            final int splitSize = this.option.splitSize();
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


        abstract boolean readRowStream(final int readSize, final Consumer<? super R> action) throws SQLException;

        abstract void doCloseStream();

        abstract ArmyException handleException(Exception cause);

        abstract void handleError(Error cause);

        final void emitSingleResultStates(RowReader<R> rowReader, final long rowCount) {
            final Consumer<ResultStates> consumer;
            consumer = this.option.stateConsumer();
            if (consumer == ResultStates.IGNORE_STATES) {
                return;
            }

            try {
                final long affectedRows;
                if (this.stmtType == StmtType.QUERY) {
                    affectedRows = 0;
                } else {
                    affectedRows = rowCount;
                }

                final ResultStates states;
                states = new SingleQueryStates(rowReader.executor.obtainTransaction(),
                        mapToArmyWarning(this.statement.getWarnings()),
                        rowCount, false, affectedRows
                );
                consumer.accept(states);
            } catch (Exception e) {
                throw handleException(e);
            } catch (Error e) {
                handleError(e);
            }
        }

        final void emitMultiResultStates(final int resultNo, RowReader<R> rowReader, final long rowCount,
                                         final boolean moreResult) {
            final Consumer<ResultStates> consumer;
            consumer = this.option.stateConsumer();
            if (consumer == ResultStates.IGNORE_STATES) {
                return;
            }

            try {
                final long affectedRows;
                if (this.stmtType == StmtType.QUERY) {
                    affectedRows = 0;
                } else {
                    affectedRows = rowCount;
                }

                final ResultStates states;
                states = new MultiResultQueryStates(resultNo, rowReader.executor.obtainTransaction(),
                        mapToArmyWarning(this.statement.getWarnings()),
                        rowCount, moreResult, affectedRows
                );
                consumer.accept(states);
            } catch (Exception e) {
                throw handleException(e);
            } catch (Error e) {
                handleError(e);
            }
        }

        final void emitMoreFetchStates(final RowReader<R> rowReader, int fetchRows, final boolean moreFetch) {
            final Consumer<ResultStates> consumer;
            consumer = this.option.stateConsumer();
            if (consumer == ResultStates.IGNORE_STATES) {
                return;
            }

            try {
                final ResultStates states;
                states = new SingleQueryStates(rowReader.executor.obtainTransaction(),
                        mapToArmyWarning(this.statement.getWarnings()),
                        fetchRows,
                        moreFetch,
                        0L);   // currently, update don't fetch size

                consumer.accept(states);
            } catch (Exception e) {
                throw handleException(e);
            } catch (Error e) {
                handleError(e);
            }
        }


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
         * @param readSize 0 or positive
         */
        final long readRowSet(final ResultSet resultSet, final RowReader<R> rowReader, final int readSize,
                              final Consumer<? super R> action) throws SQLException {
            assert this.fetchSize < 1;

            final int maxValue = Integer.MAX_VALUE;

            int readRowCount = 0;
            long bigReadCount = 0L;
            while (resultSet.next()) {

                action.accept(rowReader.readOneRow(resultSet));
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


        private void cancel() {
            this.canceled = true;
        }


        private static NullPointerException actionIsNull() {
            return new NullPointerException("Action consumer is null");
        }


    }//JdbcRowSpliterator


    private static abstract class JdbcSimpleSpliterator<R> extends JdbcRowSpliterator<R> {

        final Statement statement;

        final SimpleStmt stmt;

        final ResultSet resultSet;

        final RowReader<R> rowReader;

        final boolean hasOptimistic;


        private JdbcSimpleSpliterator(Statement statement, ResultSet resultSet, RowReader<R> rowReader,
                                      SimpleStmt stmt, SyncStmtOption option) {
            super(statement, stmt.stmtType(), option);
            this.statement = statement;
            this.stmt = stmt;
            this.resultSet = resultSet;
            this.rowReader = rowReader;

            this.hasOptimistic = stmt.hasOptimistic();

        }

        @Override
        public final int characteristics() {
            int bits = 0;
            if (this.rowReader instanceof BeanRowReader || this.rowReader instanceof ObjectReader) {
                bits |= NONNULL;
            }
            return bits;
        }

        @Override
        final void doCloseStream() {
            closeResultSetAndStatement(this.resultSet, this.statement);
        }


        @Override
        final ArmyException handleException(Exception cause) {
            close();
            return this.rowReader.executor.handleException(cause);
        }

        @Override
        final void handleError(Error cause) {
            close();
        }


    } // JdbcSimpleSpliterator


    private static final class SimpleRowSpliterator<R> extends JdbcSimpleSpliterator<R> {


        private long totalRowCount = 0L;

        private int currentFetchRows = 0;

        /**
         * @see #executeSimpleQuery(SimpleStmt, SyncStmtOption, Function)
         */
        private SimpleRowSpliterator(Statement statement, ResultSet resultSet, RowReader<R> rowReader, SimpleStmt stmt,
                                     SyncStmtOption option) {
            super(statement, resultSet, rowReader, stmt, option);
        }

        @Override
        boolean readRowStream(final int readSize, final Consumer<? super R> action) throws SQLException {
            final int fetchSize = this.fetchSize;
            final long readRowCount;
            if (fetchSize > 0) {
                readRowCount = readWithFetchSize(this.resultSet, this.rowReader, readSize, action);
            } else {
                readRowCount = readRowSet(this.resultSet, this.rowReader, readSize, action);
            }

            if (readRowCount > 0) {
                this.totalRowCount += readRowCount;
            } else if (this.hasOptimistic && this.totalRowCount == 0L) {
                throw _Exceptions.optimisticLock();
            }

            if (this.canceled) {
                close();
            } else if (fetchSize < 1
                    && (readSize == 0 || (readSize > 0 && readRowCount < readSize))) {
                // readRowSet() dont' emit ResultStates,so here emit
                emitSingleResultStates(this.rowReader, this.totalRowCount);
                close();
            }
            return readRowCount > 0;
        }


        private long readWithFetchSize(final ResultSet resultSet, final RowReader<R> rowReader, final int readSize,
                                       final Consumer<? super R> action) throws SQLException {

            final int fetchSize = this.fetchSize;
            assert fetchSize > 0;

            int currentFetchRows = this.currentFetchRows;
            long bigReadCount = 0L;
            boolean interrupt = false;
            while (resultSet.next()) {

                if (currentFetchRows == 0 && bigReadCount > 0L) {
                    // emit ResultStates
                    emitMoreFetchStates(rowReader, fetchSize, true);
                }

                action.accept(rowReader.readOneRow(resultSet));
                currentFetchRows++;

                if (readSize > 0 && currentFetchRows == readSize) {
                    interrupt = true;
                    break;
                }

                if (this.canceled) { // canceled must after readRowCount++; because of OptimisticLockException
                    interrupt = true;
                    break;
                }

                if (currentFetchRows == fetchSize) {
                    bigReadCount += currentFetchRows;
                    currentFetchRows = 0;
                }

            } // while loop

            if (!interrupt) {
                emitMoreFetchStates(rowReader, currentFetchRows, false);
                close();
            }

            bigReadCount += currentFetchRows;

            if (currentFetchRows == fetchSize) {
                this.currentFetchRows = 0;
            } else {
                this.currentFetchRows = currentFetchRows;
            }
            return bigReadCount;
        }


    } // SimpleRowSpliterator


    private static final class InsertRowSpliterator<R> extends JdbcSimpleSpliterator<R> {

        private int rowIndex;


        /**
         * @see #executeSimpleQuery(SimpleStmt, SyncStmtOption, Function)
         */
        private InsertRowSpliterator(Statement statement, ResultSet resultSet, RowReader<R> rowReader,
                                     GeneratedKeyStmt stmt, SyncStmtOption option) {
            super(statement, resultSet, rowReader, stmt, option);


        }

        /**
         * @see #tryAdvance(Consumer)
         * @see #forEachRemaining(Consumer)
         */
        boolean readRowStream(final int readSize, final Consumer<? super R> action) throws SQLException {

            final ResultSet resultSet = this.resultSet;
            final RowReader<R> rowReader = this.rowReader;
            final JdbcExecutor executor = rowReader.executor;
            final GeneratedKeyStmt stmt = (GeneratedKeyStmt) this.stmt;

            final PrimaryFieldMeta<?> idField = stmt.idField();
            final MappingType type = idField.mappingType();
            final MappingEnv env = executor.factory.mappingEnv;
            final int idSelectionIndex = stmt.idSelectionIndex();

            final SqlType idSqlType = rowReader.sqlTypeArray[idSelectionIndex];

            final int rowSize = stmt.rowSize(), idColumnIndexBaseOne = idSelectionIndex + 1;

            Object idValue;
            int readRowCount = 0, rowIndex = this.rowIndex;
            boolean interrupt = false;
            while (resultSet.next()) {

                if (rowIndex == rowSize) {
                    throw insertedRowsAndGenerateIdNotMatch(rowSize, rowIndex + 1);
                }

                // below read id value
                idValue = executor.get(resultSet, idColumnIndexBaseOne, idSqlType); // read id column
                if (idValue == null) {
                    throw _Exceptions.idValueIsNull(rowIndex, idField);
                }
                idValue = type.afterGet(idSqlType, env, idValue); // MappingType convert id column
                stmt.setGeneratedIdValue(rowIndex, idValue);     // set id column

                action.accept(rowReader.readOneRow(resultSet)); // read one row

                readRowCount++;
                rowIndex++;

                if (readSize > 0 && readRowCount == readSize) {
                    interrupt = true;
                    break;
                }

                if (this.canceled) { // canceled must after readRowCount++; because of OptimisticLockException
                    break;
                }

            } // while loop

            this.rowIndex = rowIndex;

            if (rowIndex == 0 && this.hasOptimistic) {
                throw _Exceptions.optimisticLock();
            }

            if (this.canceled) {
                close();
            } else if (!interrupt) {
                if (this.rowIndex != ((GeneratedKeyStmt) this.stmt).rowSize()) {
                    throw insertedRowsAndGenerateIdNotMatch(((GeneratedKeyStmt) this.stmt).rowSize(), rowIndex);
                }
                emitSingleResultStates(rowReader, rowIndex); // here ,rowIndex not rowIndex + 1
                close();
            }
            return readRowCount > 0;
        }


    } //InsertRowSpliterator


    private static final class SimpleSecondSpliterator<R> extends JdbcSimpleSpliterator<R> {

        private final ObjectAccessor accessor;

        private final Class<?> resultClass;

        private final List<R> firstList;

        private Map<Object, R> rowMap;

        private int rowIndex = 0;


        private int totalRowCount = 0;


        private SimpleSecondSpliterator(Statement statement, ResultSet resultSet, SecondRowReader<R> rowReader,
                                        TwoStmtQueryStmt stmt, SyncStmtOption option, List<R> firstList) {
            super(statement, resultSet, rowReader, stmt, option);
            final R row;
            row = firstList.get(0);
            if (row instanceof Map) {
                this.resultClass = Map.class;
            } else {
                this.resultClass = row.getClass();
            }
            if (row instanceof Map || stmt.maxColumnSize() > 1) {
                this.accessor = ObjectAccessorFactory.fromInstance(firstList.get(0));
            } else {
                this.accessor = ExecutorSupport.SINGLE_COLUMN_PSEUDO_ACCESSOR;
            }
            this.firstList = firstList;

        }

        @Override
        boolean readRowStream(final int readSize, final Consumer<? super R> action) throws SQLException {

            final ResultSet resultSet = this.resultSet;
            final SecondRowReader<R> rowReader = (SecondRowReader<R>) this.rowReader;
            final JdbcExecutor executor = rowReader.executor;
            final MappingEnv env = executor.factory.mappingEnv;

            final TwoStmtQueryStmt stmt = (TwoStmtQueryStmt) this.stmt;
            final ObjectAccessor accessor = this.accessor;
            final List<R> firstList = this.firstList;
            final int idSelectionIndex = stmt.idSelectionIndex();

            final Selection idSelection = rowReader.selectionList.get(idSelectionIndex);
            final SqlType idSqlType = rowReader.sqlTypeArray[idSelectionIndex];
            final String idLabel = idSelection.label();
            final MappingType type = compatibleTypeFrom(idSelection, this.resultClass, accessor, idLabel);

            final int idColumnIndexBasedOne = idSelectionIndex + 1;

            Map<Object, R> rowMap = this.rowMap;
            Object idValue;
            int readRowCount = 0, rowIndex = this.rowIndex;
            R row;
            boolean interrupt = false;
            while (resultSet.next()) {

                idValue = executor.get(resultSet, idColumnIndexBasedOne, idSqlType);
                if (idValue == null) {
                    throw _Exceptions.secondStmtIdIsNull(idSelection);
                }
                idValue = type.afterGet(idSqlType, env, idValue);

                if (rowMap == null) {
                    this.rowMap = rowMap = createIdToRowMap(firstList, idLabel, accessor);
                }

                row = rowMap.get(idValue);

                if (row == null) {
                    String m = String.format("Not found match row for %s(based 1) row id[%s] in first query ", rowIndex + 1, idValue);
                    throw new DataAccessException(m);
                }

                rowReader.currentRow = row;
                rowReader.accessor = accessor;

                action.accept(rowReader.readOneRow(resultSet));

                readRowCount++;
                rowIndex++;
                if (readSize > 0 && readRowCount == readSize) {
                    interrupt = true;
                    break;
                }

                if (rowIndex < 0) {
                    throw new CriteriaException("Second query row count greater than Integer.MAX_VALUE");
                }

                if (this.canceled) {
                    break;
                }

            }

            this.rowIndex = rowIndex;
            if (readRowCount > 0) {
                this.totalRowCount += readRowCount;
            }

            if (this.canceled) {
                close();
            } else if (!interrupt) {
                emitSingleResultStates(rowReader, this.totalRowCount);
                close();
            }
            return readRowCount > 0;
        }


    } // SimpleSecondSpliterator


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

        final BatchStmt stmt;

        final SyncStmtOption option;

        RowReader<R> rowReader;

        private ResultSet resultSet;

        private int resultNo = 1; // from 1 not 0

        private long currentResultTotalRows = 0L;

        private JdbcBatchSpliterator(Statement statement, RowReader<R> rowReader,
                                     BatchStmt stmt, SyncStmtOption option, ResultSet resultSet) {
            super(statement, stmt.stmtType(), option);

            this.statement = statement;
            this.rowReader = rowReader;
            this.stmt = stmt;
            this.option = option;

            this.resultSet = resultSet;

        }

        @Override
        public final int characteristics() {
            int bits = 0;
            if (this.rowReader instanceof BeanRowReader || this.rowReader instanceof ObjectReader) {
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
        final boolean readRowStream(final int readSize, final Consumer<? super R> action) throws SQLException {
            final boolean hasOptimistic = this.stmt.hasOptimistic();
            RowReader<R> rowReader = this.rowReader;

            ResultSet resultSet = this.resultSet;
            long readCount, multiSetRowCount = 0;
            for (int restReadSize = readSize; resultSet != null; ) {

                readCount = readRowSet(resultSet, rowReader, restReadSize, action);

                if (readCount > 0) {
                    multiSetRowCount += readCount;
                    this.currentResultTotalRows += readCount;
                    if (readSize > 0 && (restReadSize -= readCount) == 0) {
                        break;
                    }
                } else if (hasOptimistic && this.currentResultTotalRows == 0L) {
                    throw _Exceptions.optimisticLock();
                }

                this.resultSet = null; // firstly clear
                closeResource(resultSet); // secondly close

                this.resultSet = resultSet = nextResultSet();
                if (!this.canceled) {
                    // emit ResultStates
                    emitMultiResultStates(this.resultNo, rowReader, this.currentResultTotalRows, resultSet != null);
                }

                // reset for next result set
                this.resultNo++;
                this.currentResultTotalRows = 0L;
                if (resultSet != null && rowReader instanceof RecordRowReader) {
                    rowReader = new RecordRowReader<>((RecordRowReader<R>) rowReader, resultSet.getMetaData());
                    this.rowReader = rowReader;
                    assert rowReader.getResultNo() == this.resultNo;
                }

            }// for loop

            if (readSize == 0 || (readSize > 0 && multiSetRowCount < readSize)) {
                close();
            }
            return multiSetRowCount > 0;
        }


        @Nullable
        abstract ResultSet nextResultSet() throws SQLException, TimeoutException;


    } // BatchJdbcSpliterator


    private static final class BatchRowSpliterator<R> extends JdbcBatchSpliterator<R> {

        private int groupIndex = 1; // here from 1 not 0.

        /**
         * @see JdbcExecutor#executeBatchQuery(BatchStmt, SyncStmtOption, Function)
         */
        private BatchRowSpliterator(PreparedStatement statement, RowReader<R> rowReader,
                                    BatchStmt stmt, SyncStmtOption option, ResultSet resultSet) {
            super(statement, rowReader, stmt, option, resultSet);

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
            final JdbcExecutor executor = this.rowReader.executor;
            final PreparedStatement statement = (PreparedStatement) this.statement;

            statement.clearParameters();
            executor.bindParameter(statement, paramGroupList.get(groupIndex));
            executor.bindStatementOption(statement, stmt, this.option);

            return statement.executeQuery();
        }

        @Override
        ArmyException handleException(Exception cause) {
            close();
            return this.rowReader.executor.handleException(cause);
        }

        @Override
        void handleError(Error cause) {
            close();
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
            return this.rowReader.executor.handleException(cause);
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

        private final StreamOption option;

        private final Statement statement;

        private final ResultSet resultSet;

        private final Function<DataRecord, Xid> function;

        private final ArmyResultRecordMeta meta;

        private long rowCount;

        private boolean canceled;
        private boolean closed;

        /**
         * @see JdbcExecutor#jdbcRecover(String, Function, StreamOption)
         */
        private XidRowSpliterator(JdbcExecutor executor, StreamOption option, Statement statement, ResultSet resultSet,
                                  DataType[] dataTypeArray, Function<DataRecord, Xid> function) throws SQLException {
            this.executor = executor;
            this.option = option;
            this.statement = statement;
            this.resultSet = resultSet;

            this.function = function;
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
                return (T) this.resultSet.getObject(this.meta.checkIndexAndToBasedOne(indexBasedZero), columnClass);
            } catch (Exception e) {
                throw this.executor.handleException(e);
            }
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
                final ResultStates states;
                states = new SingleQueryStates(this.executor.obtainTransaction(),
                        mapToArmyWarning(this.statement.getWarnings()),
                        rowCount, false, 0L
                );
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


}
