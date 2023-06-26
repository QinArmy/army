package io.army.jdbc;

import io.army.ArmyException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.criteria.SQLParam;
import io.army.criteria.Selection;
import io.army.criteria.impl.SqlTypeUtils;
import io.army.function.TeFunction;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.session.DataAccessException;
import io.army.session.ExecutorSupport;
import io.army.sqltype.SqlType;
import io.army.stmt.*;
import io.army.sync.*;
import io.army.sync.executor.StmtExecutor;
import io.army.type.ImmutableSpec;
import io.army.util._ClassUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

abstract class JdbcExecutor extends ExecutorSupport implements StmtExecutor {


    final JdbcExecutorFactory factory;

    final Connection conn;


    JdbcExecutor(JdbcExecutorFactory factory, Connection conn) {
        this.factory = factory;
        this.conn = conn;
    }


    @Override
    public final long insert(final SimpleStmt stmt, final int timeout) {
        if (timeout < 0) {
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

        final String sql = stmt.sqlText();
        final List<SQLParam> paramGroup = stmt.paramGroup();
        final int paramSize = paramGroup.size();

        try (Statement statement = this.createInsertStatement(sql, paramSize, generatedKeys)) {

            final boolean preparedStmt;
            if (statement instanceof PreparedStatement) {
                preparedStmt = true;
                bindParameter((PreparedStatement) statement, paramGroup);
            } else {
                assert paramSize == 0;
                preparedStmt = false;
            }

            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }
            final long rows;
            if (returningId) {
                if (preparedStmt) {
                    rows = doExtractId(((PreparedStatement) statement).executeQuery(), (GeneratedKeyStmt) stmt);
                } else {
                    rows = doExtractId(statement.executeQuery(sql), (GeneratedKeyStmt) stmt);
                }
            } else {
                if (this.factory.useLargeUpdate) {
                    if (preparedStmt) {
                        rows = ((PreparedStatement) statement).executeLargeUpdate();
                    } else {
                        rows = statement.executeLargeUpdate(sql, generatedKeys);
                    }
                } else if (preparedStmt) {
                    rows = ((PreparedStatement) statement).executeUpdate();
                } else {
                    rows = statement.executeUpdate(sql, generatedKeys);
                }

                if (generatedKeys == Statement.RETURN_GENERATED_KEYS) {
                    doExtractId(statement.getGeneratedKeys(), (GeneratedKeyStmt) stmt);
                }
            }
            return rows;
        } catch (ArmyException e) {
            throw e;
        } catch (Exception e) {
            throw wrapError(e);
        }

    }

    @Override
    public final long update(final SimpleStmt stmt, final int timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException();
        }
        final String sql = stmt.sqlText();
        final List<SQLParam> paramGroup = stmt.paramGroup();
        final int paramSize = paramGroup.size();

        try (Statement statement = this.createUpdateStatement(sql, paramSize)) {

            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }

            final long rows;
            if (statement instanceof PreparedStatement) {

                bindParameter((PreparedStatement) statement, paramGroup);

                if (this.factory.useLargeUpdate) {
                    rows = ((PreparedStatement) statement).executeLargeUpdate();
                } else {
                    rows = ((PreparedStatement) statement).executeUpdate();
                }
            } else if (paramSize > 0) {
                //no bug,never here
                throw new IllegalStateException();
            } else if (this.factory.useLargeUpdate) {
                rows = statement.executeLargeUpdate(sql);
            } else {
                rows = statement.executeUpdate(sql);
            }
            return rows;
        } catch (ArmyException e) {
            throw e;
        } catch (Exception e) {
            throw wrapError(e);
        }

    }


    @Override
    public final List<Long> batchUpdate(final BatchStmt stmt, final int timeout,
                                        final IntFunction<List<Long>> listConstructor,
                                        final @Nullable TableMeta<?> domainTable,
                                        final @Nullable List<Long> rowsList) {
        if (timeout < 0 || !(rowsList == null || domainTable instanceof ChildTableMeta)) {
            throw new IllegalArgumentException();
        }
        try (PreparedStatement statement = this.conn.prepareStatement(stmt.sqlText())) {
            final List<List<SQLParam>> paramGroupList = stmt.groupList();

            for (List<SQLParam> group : paramGroupList) {
                bindParameter(statement, group);
                statement.addBatch();
            }
            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
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
        } catch (ArmyException e) {
            throw e;
        } catch (Exception e) {
            throw wrapError(e);
        }
    }

    @Override
    public final List<Long> multiStmtBatchUpdate(final BatchStmt stmt, final int timeout,
                                                 final IntFunction<List<Long>> listConstructor,
                                                 final @Nullable TableMeta<?> domainTable) {

        final int stmtSize;
        stmtSize = stmt.groupList().size();
        List<Long> list = listConstructor.apply(stmtSize);
        if (list == null) {
            throw _Exceptions.listConstructorError();
        }
        try (Statement statement = this.conn.createStatement()) {

            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }

            if (statement.execute(stmt.sqlText())) {
                // sql error
                throw new DataAccessException("error,multi-statement batch update the first result is ResultSet");
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

            if (list instanceof ImmutableSpec) {
                list = _Collections.unmodifiableListForDeveloper(list);
            }
            return list;
        } catch (ArmyException e) {
            throw e;
        } catch (Exception e) {
            throw wrapError(e);
        }

    }


    @Override
    public final <R> List<R> query(final SimpleStmt stmt, final int timeout, final Class<R> resultClass,
                                   final Supplier<List<R>> listConstructor) {
        return this.doQuery(stmt, timeout, listConstructor, this.beanReaderFunc(stmt.selectionList(), resultClass));

    }


    @Override
    public final List<Map<String, Object>> queryMap(SimpleStmt stmt, int timeout,
                                                    Supplier<Map<String, Object>> mapConstructor,
                                                    Supplier<List<Map<String, Object>>> listConstructor) {
        return this.doQuery(stmt, timeout, listConstructor, this.mapReaderFunc(stmt, mapConstructor));
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <R> int secondQuery(final TwoStmtQueryStmt stmt, final int timeout, final Class<R> resultClass,
                                     final List<R> resultList) {


        final List<? extends Selection> selectionList = stmt.selectionList();
        final int selectionSize = selectionList.size();

        if (timeout < 0 || selectionSize == 0) {
            throw new IllegalArgumentException();
        }

        final String sql = stmt.sqlText();
        final List<SQLParam> paramGroup = stmt.paramGroup();


        try (Statement statement = this.createQueryStatement(sql, paramGroup.size())) {

            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }
            final ResultSet rs;
            if (statement instanceof PreparedStatement) {
                bindParameter((PreparedStatement) statement, paramGroup);
                rs = ((PreparedStatement) statement).executeQuery();
            } else {
                rs = statement.executeQuery(sql);
            }

            try (ResultSet resultSet = rs) {

                final ObjectAccessor accessor;
                if (resultClass == Map.class) {
                    accessor = ObjectAccessorFactory.forMap();
                } else if (selectionSize > 1) {
                    accessor = ObjectAccessorFactory.forBean(resultClass);
                } else {
                    accessor = ObjectAccessorFactory.PSEUDO_ACCESSOR;
                }

                final SqlType[] sqlTypeArray;
                sqlTypeArray = createSqlTypArray(resultSet.getMetaData());

                final SecondRowReader<R> rowReader;
                rowReader = new SecondRowReader<>(this, selectionList, sqlTypeArray, resultClass, accessor);

                final int idSelectionIndex, firstStmtResultSize;
                idSelectionIndex = stmt.idSelectionIndex();
                firstStmtResultSize = resultList.size();

                final Selection idSelection = selectionList.get(idSelectionIndex);
                final SqlType idSqlType = sqlTypeArray[idSelectionIndex];
                final String idFieldName = idSelection.alias();

                final MappingType compatibleType;
                compatibleType = compatibleTypeFrom(idSelection, resultClass, accessor, idFieldName);

                final boolean columnResultSet = accessor == ObjectAccessorFactory.PSEUDO_ACCESSOR;
                final MappingEnv env = this.factory.mappingEnv;
                Object secondStmtId, firstStmtId;
                int rowIndex = 0;
                R rowOfFirstStmt;
                Map<Object, Integer> rowIndexMap = null;
                int rowIndexValue;
                for (final int idIndexBasedOne = idSelectionIndex + 1; resultSet.next(); rowIndex++) {
                    if (rowIndex == firstStmtResultSize) {
                        //here, error,invoker throw Exception
                        break;
                    }

                    secondStmtId = get(resultSet, idIndexBasedOne, idSqlType);

                    if (secondStmtId == null) {
                        throw _Exceptions.secondStmtIdIsNull();
                    }

                    secondStmtId = compatibleType.afterGet(idSqlType, env, secondStmtId);

                    if (rowIndexMap != null) {
                        rowIndexValue = rowIndexMap.getOrDefault(secondStmtId, -1);
                    } else {
                        rowOfFirstStmt = resultList.get(rowIndex);
                        if (columnResultSet) {
                            firstStmtId = rowOfFirstStmt;
                        } else {
                            firstStmtId = accessor.get(rowOfFirstStmt, idFieldName);
                        }

                        if (firstStmtId == null) {
                            throw _Exceptions.firstStmtIdIsNull();
                        }
                        if (firstStmtId.equals(secondStmtId)) {
                            rowIndexValue = rowIndex;
                        } else {
                            rowIndexMap = createFirstStmtRowMap(resultList, accessor, idFieldName);
                            rowIndexValue = rowIndexMap.getOrDefault(secondStmtId, -1);
                        }
                    }

                    if (rowIndexValue < 0) {
                        throw _Exceptions.noMatchFirstStmtRow(secondStmtId);
                    }
                    rowOfFirstStmt = resultList.get(rowIndexValue);
                    rowReader.currentRow = rowOfFirstStmt;
                    if (columnResultSet) {
                        if (!rowOfFirstStmt.equals(rowReader.readOneRow(resultSet))) {
                            throw _Exceptions.noMatchFirstStmtRow(secondStmtId);
                        }
                    } else if (rowReader.readOneRow(resultSet) != rowOfFirstStmt) {
                        // no bug,never here
                        throw new IllegalStateException("read row error");
                    }
                    if (rowOfFirstStmt instanceof Map && rowOfFirstStmt instanceof ImmutableSpec) {
                        rowOfFirstStmt = (R) _Collections.unmodifiableMapForDeveloper((Map<String, Object>) rowOfFirstStmt);
                        resultList.set(rowIndexValue, rowOfFirstStmt);
                    }

                }// for
                return rowIndex;
            }
        } catch (DataAccessException e) {
            throw e;
        } catch (Throwable e) {
            throw wrapError(e);
        }
    }


    @Override
    public final <R> List<R> batchQuery(final BatchStmt stmt, final int timeout, final Class<R> resultClass,
                                        final @Nullable R terminator,
                                        final Supplier<List<R>> listConstructor) throws DataAccessException {
        final Function<ResultSetMetaData, RowReader<R>> function;
        function = this.beanReaderFunc(stmt.selectionList(), resultClass);
        return this.doBatchQuery(stmt, timeout, terminator, listConstructor, function);
    }


    @Override
    public final List<Map<String, Object>> batchQueryAsMap(final BatchStmt stmt, final int timeout,
                                                           final Supplier<Map<String, Object>> mapConstructor,
                                                           final @Nullable Map<String, Object> terminator,
                                                           final Supplier<List<Map<String, Object>>> listConstructor)
            throws DataAccessException {
        final Function<ResultSetMetaData, RowReader<Map<String, Object>>> function;
        function = this.mapReaderFunc(stmt, mapConstructor);
        return this.doBatchQuery(stmt, timeout, terminator, listConstructor, function);
    }

    @Override
    public final <R> List<R> multiStmtBatchQuery(BatchStmt stmt, int timeout, Class<R> resultClass, R terminator,
                                                 Supplier<List<R>> listConstructor) throws DataAccessException {
        final Function<ResultSetMetaData, RowReader<R>> function;
        function = this.beanReaderFunc(stmt.selectionList(), resultClass);
        return this.doMultiStmtBatchQuery(stmt, timeout, terminator, listConstructor, function);
    }


    @Override
    public final List<Map<String, Object>> multiStmtBatchQueryAsMap(BatchStmt stmt, int timeout,
                                                                    Supplier<Map<String, Object>> mapConstructor,
                                                                    Map<String, Object> terminator,
                                                                    Supplier<List<Map<String, Object>>> listConstructor)
            throws DataAccessException {
        final Function<ResultSetMetaData, RowReader<Map<String, Object>>> function;
        function = this.mapReaderFunc(stmt, mapConstructor);
        return this.doMultiStmtBatchQuery(stmt, timeout, terminator, listConstructor, function);
    }

    @Override
    public final <R> Stream<R> queryStream(SimpleStmt stmt, int timeout, Class<R> resultClass,
                                           final StreamOptions options) {
        final Function<ResultSetMetaData, RowReader<R>> function;
        function = this.beanReaderFunc(stmt.selectionList(), resultClass);
        return this.queryAsStream(stmt, timeout, options, function);
    }


    @Override
    public final Stream<Map<String, Object>> queryMapStream(SimpleStmt stmt, int timeout,
                                                            final Supplier<Map<String, Object>> mapConstructor,
                                                            StreamOptions options) {
        final Function<ResultSetMetaData, RowReader<Map<String, Object>>> function;
        function = this.mapReaderFunc(stmt, mapConstructor);
        return this.queryAsStream(stmt, timeout, options, function);
    }


    @Override
    public final <R> Stream<R> batchQueryStream(final BatchStmt stmt, int timeout, final Class<R> resultClass,
                                                final R terminator, final StreamOptions options)
            throws DataAccessException {

        final Function<ResultSetMetaData, RowReader<R>> function;
        function = this.beanReaderFunc(stmt.selectionList(), resultClass);
        return this.doBatchQueryStream(stmt, timeout, terminator, options, function);
    }


    @Override
    public final Stream<Map<String, Object>> batchQueryMapStream(final BatchStmt stmt, final int timeout,
                                                                 final Supplier<Map<String, Object>> mapConstructor,
                                                                 final @Nullable Map<String, Object> terminator,
                                                                 final StreamOptions options)
            throws DataAccessException {
        final Function<ResultSetMetaData, RowReader<Map<String, Object>>> function;
        function = this.mapReaderFunc(stmt, mapConstructor);
        return this.doBatchQueryStream(stmt, timeout, terminator, options, function);
    }

    @Override
    public final <R> Stream<R> multiStmtBatchQueryStream(BatchStmt stmt, int timeout, Class<R> resultClass,
                                                         R terminator, StreamOptions options)
            throws DataAccessException {
        final Function<ResultSetMetaData, RowReader<R>> function;
        function = this.beanReaderFunc(stmt.selectionList(), resultClass);
        return this.doMultiStmtBatchQueryStream(stmt, timeout, terminator, options, function);
    }

    @Override
    public final Stream<Map<String, Object>> multiStmtBatchQueryMapStream(BatchStmt stmt, int timeout,
                                                                          Supplier<Map<String, Object>> mapConstructor,
                                                                          Map<String, Object> terminator,
                                                                          StreamOptions options)
            throws DataAccessException {
        final Function<ResultSetMetaData, RowReader<Map<String, Object>>> function;
        function = this.mapReaderFunc(stmt, mapConstructor);
        return this.doMultiStmtBatchQueryStream(stmt, timeout, terminator, options, function);
    }


    @Override
    public final MultiResult multiStmt(final MultiStmt stmt, final int timeout, final StreamOptions options) {
        if (timeout < 0) {
            throw new IllegalArgumentException();
        }
        Statement statement = null;
        try {
            statement = this.createMultiStmtStreamStatement(options);
            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }
            statement.execute(stmt.multiSql());

            return new JdbcMultiResult(this, statement, stmt.stmtItemList());
        } catch (Throwable e) {
            if (statement != null) {
                closeResource(statement);
            }
            throw wrapError(e);
        }

    }

    @Override
    public final MultiStream multiStmtStream(MultiStmt stmt, int timeout, StreamOptions options) {
        if (timeout < 0) {
            throw new IllegalArgumentException();
        }
        Statement statement = null;
        try {
            statement = this.createMultiStmtStreamStatement(options);
            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }
            statement.execute(stmt.multiSql());

            return new JdbcMultiStream(this, statement, stmt.stmtItemList(), options);
        } catch (Throwable e) {
            if (statement != null) {
                closeResource(statement);
            }
            throw wrapError(e);
        }
    }

    @Override
    public final Object createSavepoint() throws DataAccessException {
        try {
            return this.conn.setSavepoint();
        } catch (SQLException e) {
            throw wrapError(e);
        }
    }

    @Override
    public final void rollbackToSavepoint(Object savepoint) throws DataAccessException {
        try {
            this.conn.releaseSavepoint((Savepoint) savepoint);
        } catch (SQLException e) {
            throw wrapError(e);
        }
    }

    @Override
    public final void releaseSavepoint(Object savepoint) throws DataAccessException {
        try {
            this.conn.releaseSavepoint((Savepoint) savepoint);
        } catch (SQLException e) {
            throw wrapError(e);
        }
    }

    @Override
    public final void executeBatch(final List<String> stmtList) throws DataAccessException {
        try (Statement statement = this.conn.createStatement()) {
            for (String stmt : stmtList) {
                statement.addBatch(stmt);
            }
            final int[] batch;
            batch = statement.executeBatch();
            if (batch.length != stmtList.size()) {
                String m = String.format("execute batch[%s] and stmtList size[%s] not match."
                        , batch.length, stmtList.size());
                throw new DataAccessException(m);
            }
        } catch (SQLException e) {
            throw wrapError(e);
        }
    }

    @Override
    public final void execute(String stmt) throws DataAccessException {
        try (Statement statement = this.conn.createStatement()) {
            statement.executeUpdate(stmt);
        } catch (SQLException e) {
            throw wrapError(e);
        }
    }

    @Override
    public final void close() throws DataAccessException {

        try {
            this.conn.close();

        } catch (SQLException e) {
            throw wrapError(e);
        }

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

    /*################################## blow private method ##################################*/


    private SqlType[] createSqlTypArray(final ResultSetMetaData metaData) throws SQLException {
        final SqlType[] sqlTypeArray = new SqlType[metaData.getColumnCount()];
        for (int i = 0; i < sqlTypeArray.length; i++) {
            sqlTypeArray[i] = this.getSqlType(metaData, i + 1);
        }
        return sqlTypeArray;
    }


    /**
     * @see #insert(SimpleStmt, int)
     */
    private Statement createInsertStatement(final String sql, final int paramSize, final int autoGeneratedKeys)
            throws SQLException {
        final Statement statement;
        if (paramSize > 0) {
            statement = this.conn.prepareStatement(sql, autoGeneratedKeys);
        } else {
            statement = this.conn.createStatement();
        }
        return statement;
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
     * @see #doBatchQuery(BatchStmt, int, Object, Supplier, Function)
     * @see #batchQueryStream(BatchStmt, int, Class, Object, StreamOptions)
     */
    private PreparedStatement createBatchStreamStatement(final String sql, final StreamOptions options)
            throws SQLException {
        final PreparedStatement statement;
        if (options == StreamOptions.LIST_LIKE || options.serverStream == null) {
            statement = this.conn.prepareStatement(sql);
        } else {
            statement = this.conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);
            if (options.serverStream == Boolean.TRUE) {
                statement.setFetchSize(options.fetchSize);
            } else {
                setClientStreamFetchSize(statement, options);
            }
        }
        return statement;

    }

    /**
     * @see #doMultiStmtBatchQueryStream(BatchStmt, int, Object, StreamOptions, Function)
     * @see #multiStmt(MultiStmt, int, StreamOptions)
     * @see #multiStmtStream(MultiStmt, int, StreamOptions)
     */
    private Statement createMultiStmtStreamStatement(final StreamOptions options) throws SQLException {
        final Statement statement;

        if (options == StreamOptions.LIST_LIKE || options.serverStream == null) {
            statement = this.conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        } else {
            statement = this.conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);
            if (options.serverStream == Boolean.TRUE) {
                statement.setFetchSize(options.fetchSize);
            } else {
                setClientStreamFetchSize(statement, options);
            }
        }
        return statement;
    }


    /**
     * @see #query(SimpleStmt, int, Class, Supplier)
     * @see #queryStream(SimpleStmt, int, Class, StreamOptions)
     */
    private <R> Function<ResultSetMetaData, RowReader<R>> beanReaderFunc(
            final List<? extends Selection> selectionList, final Class<R> resultClass) {

        return metaData -> {
            try {
                return this.createBeanRowReader(metaData, resultClass, selectionList);
            } catch (SQLException e) {
                throw wrapError(e);
            }

        };
    }

    /**
     * @see #queryMap(SimpleStmt, int, Supplier, Supplier)
     * @see #queryMapStream(SimpleStmt, int, Supplier, StreamOptions)
     */
    private Function<ResultSetMetaData, RowReader<Map<String, Object>>> mapReaderFunc(
            final GenericSimpleStmt stmt, final Supplier<Map<String, Object>> mapConstructor) {
        return metaData -> {
            try {
                return new MapReader(this, stmt.selectionList(), stmt instanceof TwoStmtModeQuerySpec,
                        this.createSqlTypArray(metaData), mapConstructor
                );
            } catch (SQLException e) {
                throw wrapError(e);
            }
        };
    }


    /**
     * @see #insert(SimpleStmt, int)
     * @see #update(SimpleStmt, int)
     */
    private void bindParameter(final PreparedStatement statement, final List<SQLParam> paramGroup)
            throws SQLException {
        final int size = paramGroup.size();
        final ServerMeta serverMeta = this.factory.serverMeta;
        final MappingEnv mappingEnv = this.factory.mappingEnv;

        SQLParam sqlParam;
        Object value;
        MappingType mappingType;
        TypeMeta typeMeta;
        SqlType sqlType;
        Object attr = null;
        for (int i = 0; i < size; i++) {
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
                    statement.setNull(i + 1, Types.NULL);
                    continue;
                }
                value = mappingType.beforeBind(sqlType, mappingEnv, value);
                //TODO field codec
                attr = bind(statement, i + 1, attr, mappingType, sqlType, value);
                continue;
            }

            if (!(sqlParam instanceof MultiParam)) {
                throw _Exceptions.unexpectedSqlParam(sqlParam);
            }

            for (final Object element : ((MultiParam) sqlParam).valueList()) {
                if (element == null) {
                    // bind null
                    statement.setNull(i + 1, Types.NULL);
                    continue;
                }
                value = mappingType.beforeBind(sqlType, mappingEnv, element);
                //TODO field codec
                attr = bind(statement, i + 1, attr, mappingType, sqlType, value);
            }// inner for


        }//outer for

    }


    /**
     * @see #query(SimpleStmt, int, Class, Supplier)
     * @see #queryMap(SimpleStmt, int, Supplier, Supplier)
     */
    private <R> List<R> doQuery(final SimpleStmt stmt, final int timeout, final Supplier<List<R>> listConstructor,
                                final Function<ResultSetMetaData, RowReader<R>> function) {
        if (timeout < 0) {
            throw new IllegalArgumentException();
        }

        final String sql = stmt.sqlText();
        final List<SQLParam> paramGroup = stmt.paramGroup();

        ResultSet resultSet = null;
        RowReader<R> rowReader = null;
        try (Statement statement = this.createQueryStatement(sql, paramGroup.size())) {

            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }

            if (statement instanceof PreparedStatement) {
                bindParameter((PreparedStatement) statement, paramGroup);
                resultSet = ((PreparedStatement) statement).executeQuery();
            } else {
                resultSet = statement.executeQuery(sql);
            }

            rowReader = function.apply(resultSet.getMetaData());

            final List<R> resultList;
            if (stmt instanceof GeneratedKeyStmt) {
                resultList = readReturningInsert(resultSet, rowReader, (GeneratedKeyStmt) stmt, listConstructor);
            } else {
                resultList = readList(resultSet, rowReader, stmt, stmt.hasOptimistic(), listConstructor);
            }
            return resultList;
        } catch (Throwable e) {
            if (resultSet != null && rowReader == null) {
                // create rowReader error
                closeResource(resultSet);
            }
            throw wrapError(e);
        }

    }


    /**
     * @see #queryStream(SimpleStmt, int, Class, StreamOptions)
     * @see #queryMapStream(SimpleStmt, int, Supplier, StreamOptions)
     */
    private <R> Stream<R> queryAsStream(final SimpleStmt stmt, final int timeout, final StreamOptions options,
                                        final Function<ResultSetMetaData, RowReader<R>> function) {
        if (timeout < 0) {
            throw new IllegalArgumentException();
        }

        final List<? extends Selection> selectionList = stmt.selectionList();
        final int selectionSize;
        selectionSize = selectionList.size();
        if (selectionSize == 0) {
            throw new IllegalArgumentException();
        }
        final List<SQLParam> paramGroup = stmt.paramGroup();

        final String sql = stmt.sqlText();
        final int paramSize;
        paramSize = paramGroup.size();

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            // 1. create statement
            statement = createStreamStmt(sql, paramSize, options);
            // 2. bind parameter
            if (statement instanceof PreparedStatement) {
                bindParameter((PreparedStatement) statement, paramGroup);
            } else {
                assert paramSize == 0;
            }

            // 3. set timeout
            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }
            // 4. execute statement
            if (statement instanceof PreparedStatement) {
                resultSet = ((PreparedStatement) statement).executeQuery();
            } else {
                resultSet = statement.executeQuery(sql);
            }

            // 5. create RowReader
            final RowReader<R> rowReader;
            rowReader = function.apply(resultSet.getMetaData());

            // 6. create RowSpliterator
            final RowSpliterator<R> spliterator;
            spliterator = new RowSpliterator<>(statement, resultSet, rowReader, stmt.hasOptimistic(), options);

            // 7. create Commander
            this.invokeCommanderConsumer(options, spliterator);

            return StreamSupport.stream(spliterator, options.parallel);
        } catch (Throwable e) {
            throw handleError(e, resultSet, statement);
        }

    }


    /**
     * @see #multiStmtBatchQuery(BatchStmt, int, Class, Object, Supplier)
     * @see #multiStmtBatchQueryAsMap(BatchStmt, int, Supplier, Map, Supplier)
     */
    private <R> List<R> doMultiStmtBatchQuery(final BatchStmt stmt, final int timeout, final @Nullable R terminator,
                                              final Supplier<List<R>> listConstructor,
                                              final Function<ResultSetMetaData, RowReader<R>> function) {
        if (terminator == null) {
            throw _Exceptions.terminatorIsNull();
        } else if (timeout < 0 || terminator == Collections.EMPTY_MAP) {
            throw new IllegalArgumentException();
        }
        List<R> resultList = listConstructor.get();
        if (resultList == null) {
            throw _Exceptions.listConstructorError();
        }
        try (Statement statement = this.conn.createStatement()) {
            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }
            // execute multi-statement
            if (!statement.execute(stmt.sqlText())) {
                throw new DataAccessException("database don't return any result set.");
            }
            final int groupSize = stmt.groupList().size();
            RowReader<R> rowReader = null;
            int batchIndex;
            for (batchIndex = 0; statement.getMoreResults(); batchIndex++) {

                if (batchIndex >= groupSize) {
                    statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
                    throw _Exceptions.batchCountNotMatch(groupSize, batchIndex + 1);
                }

                try (ResultSet resultSet = statement.getResultSet()) {
                    if (rowReader == null) {
                        rowReader = function.apply(resultSet.getMetaData());
                    }
                    resultList.add(rowReader.readOneRow(resultSet));
                }

                resultList.add(terminator); // append terminator

            }// outer for

            if (statement.getUpdateCount() != -1) {
                // stmt no bug,never here
                statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
                throw _Exceptions.batchQueryReturnUpdate();
            }

            if (resultList instanceof ImmutableSpec) {
                resultList = _Collections.unmodifiableListForDeveloper(resultList);
            }
            return resultList;
        } catch (Throwable e) {
            throw wrapError(e);
        }
    }


    /**
     * @see #batchQueryStream(BatchStmt, int, Class, Object, StreamOptions)
     * @see #batchQueryMapStream(BatchStmt, int, Supplier, Map, StreamOptions)
     */
    private <R> Stream<R> doBatchQueryStream(final BatchStmt stmt, final int timeout, final @Nullable R terminator,
                                             final StreamOptions options,
                                             final Function<ResultSetMetaData, RowReader<R>> function) {
        if (terminator == null) {
            throw _Exceptions.terminatorIsNull();
        } else if (terminator == Collections.EMPTY_MAP) {
            throw new IllegalArgumentException();
        }
        final List<List<SQLParam>> paramGroupList;
        paramGroupList = stmt.groupList();
        if (paramGroupList.size() == 0) {
            throw new IllegalArgumentException();
        }
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            final long startTime;
            if (timeout > 0) {
                startTime = System.currentTimeMillis();
            } else {
                startTime = 0;
            }
            // 1. create statement
            statement = this.createBatchStreamStatement(stmt.sqlText(), options);

            // 2. bind parameter
            bindParameter(statement, paramGroupList.get(0));

            // 3. timeout

            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }
            // 4. execute
            resultSet = statement.executeQuery();

            // 5. create RowReader
            final RowReader<R> rowReader;
            rowReader = function.apply(resultSet.getMetaData());
            // 6. create BatchRowSpliterator
            final BatchRowSpliterator<R> rowSpliterator;
            rowSpliterator = new BatchRowSpliterator<>(statement, resultSet, timeout, startTime, rowReader, terminator,
                    stmt, options);
            // 7. invoke commander consumer
            invokeCommanderConsumer(options, rowSpliterator);

            // 8. create Stream
            return StreamSupport.stream(rowSpliterator, options.parallel);
        } catch (Throwable e) {
            closeResultSetAndStatement(resultSet, statement);
            throw wrapError(e);
        }
    }


    /**
     * @see #multiStmtBatchQueryStream(BatchStmt, int, Class, Object, StreamOptions)
     * @see #multiStmtBatchQueryMapStream(BatchStmt, int, Supplier, Map, StreamOptions)
     */
    private <R> Stream<R> doMultiStmtBatchQueryStream(final BatchStmt stmt, final int timeout,
                                                      final @Nullable R terminator, final StreamOptions options,
                                                      final Function<ResultSetMetaData, RowReader<R>> function) {
        if (terminator == null) {
            throw _Exceptions.terminatorIsNull();
        } else if (timeout < 0 || terminator == Collections.EMPTY_MAP) {
            throw new IllegalArgumentException();
        }
        final int groupSize = stmt.groupList().size();
        if (groupSize == 0) {
            throw new IllegalArgumentException();
        }
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            // 1. create statement
            statement = this.createMultiStmtStreamStatement(options);

            // 2. timeout
            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }
            // 3. execute
            if (!statement.execute(stmt.sqlText()) || !statement.getMoreResults()) {
                throw _Exceptions.notExistsAnyResultSet();
            }
            // 4. get result
            resultSet = statement.getResultSet();

            // 5. create RowReader
            final RowReader<R> rowReader;
            rowReader = function.apply(resultSet.getMetaData());

            // 6. create BatchRowSpliterator
            final MultiStmtBatchRowSpliterator<R> rowSpliterator;
            rowSpliterator = new MultiStmtBatchRowSpliterator<>(statement, groupSize, resultSet, rowReader, terminator,
                    stmt.hasOptimistic(), options);

            // 7. invoke commander consumer
            invokeCommanderConsumer(options, rowSpliterator);

            // 8. create Stream
            return StreamSupport.stream(rowSpliterator, options.parallel);
        } catch (Throwable e) {
            closeResultSetAndStatement(resultSet, statement);
            throw wrapError(e);
        }
    }

    /**
     * @see #doBatchQueryStream(BatchStmt, int, Object, StreamOptions, Function)
     */
    private void invokeCommanderConsumer(final StreamOptions options, final JdbcRowSpliterator<?> rowSpliterator) {
        if (options == StreamOptions.LIST_LIKE) {
            return;
        }
        final Consumer<StreamCommander> consumer = options.commanderConsumer;
        if (consumer != null) {
            if (options.parallel) {
                consumer.accept(rowSpliterator::parallelCancel);
            } else {
                consumer.accept(rowSpliterator::simpleCancel);
            }
        }

    }


    /**
     * @see #batchUpdate(BatchStmt, int, IntFunction, TableMeta, List)
     */
    private List<Long> handleBatchResult(final boolean optimistic, final int bathSize,
                                         final IntToLongFunction accessor,
                                         final IntFunction<List<Long>> listConstructor,
                                         final @Nullable TableMeta<?> domainTable,
                                         final @Nullable List<Long> rowsList) {
        assert rowsList == null || domainTable instanceof ChildTableMeta;

        List<Long> list;
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
            if (optimistic && rows < 1) {
                throw _Exceptions.batchOptimisticLock(domainTable, i + 1, rows);
            } else if (rowsList == null) {
                list.add(rows);
            } else if (rows != rowsList.get(i)) { // here rows representing parent's rows,because army update child and update parent
                throw _Exceptions.batchChildUpdateRowsError((ChildTableMeta<?>) domainTable, i + 1, rowsList.get(i),
                        rows);
            }
        }

        if (rowsList == null && list instanceof ImmutableSpec) {
            list = _Collections.unmodifiableListForDeveloper(list);
        }
        return list;
    }


    /**
     * @see #multiStmtBatchUpdate(BatchStmt, int, IntFunction, TableMeta)
     */
    private void handleChildMultiStmtBatchUpdate(final Statement statement, final BatchStmt stmt,
                                                 final ChildTableMeta<?> domainTable,
                                                 final List<Long> list) throws SQLException {

        final JdbcExecutorFactory factory = this.factory;
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
            if (factory.useLargeUpdate) {
                rows = statement.getLargeUpdateCount();
            } else {
                rows = statement.getUpdateCount();
            }

            if (rows == -1) {
                // no more result,no bug,never here
                break;
            }

            if (optimistic && rows < 1) {
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
            throw _Exceptions.multiStmtBatchUpdateResultCountError(itemPairSize);
        }

        if (itemSize != list.size()) {
            throw _Exceptions.multiStmtCountAndResultCountNotMatch(domainTable, itemSize, list.size());
        }

    }

    /**
     * @param domainTable <ul>
     *                    <li>null : multi-table batch update </li>
     *                    <li>{@link SingleTableMeta} : single table batch udpate</li>
     *
     *                    </ul>
     * @see #multiStmtBatchUpdate(BatchStmt, int, IntFunction, TableMeta)
     */
    private void handleSimpleMultiStmtBatchUpdate(final Statement statement, final BatchStmt stmt,
                                                  final @Nullable TableMeta<?> domainTable,
                                                  final List<Long> list) throws SQLException {

        assert domainTable == null || domainTable instanceof SingleTableMeta;

        final JdbcExecutorFactory factory = this.factory;
        final boolean optimistic = stmt.hasOptimistic();
        final int itemSize = stmt.groupList().size();

        long rows;
        for (int i = 0; i < itemSize; i++) {
            if (statement.getMoreResults()) {
                statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
                throw _Exceptions.batchUpdateReturnResultSet(domainTable, i + 1);
            }
            if (factory.useLargeUpdate) {
                rows = statement.getLargeUpdateCount();
            } else {
                rows = statement.getUpdateCount();
            }

            if (rows == -1) {
                // no more result
                break;
            }
            if (optimistic && rows < 1) {
                throw _Exceptions.batchOptimisticLock(domainTable, i + 1, rows);
            }
            list.add(rows);

        }

        if (statement.getMoreResults() || statement.getUpdateCount() != -1) {
            // sql error
            throw _Exceptions.multiStmtBatchUpdateResultCountError(itemSize);
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
                    && !(stmt instanceof TwoStmtModeQuerySpec && rowReader instanceof MapReader)) {
                list = _Collections.unmodifiableListForDeveloper(list);
            }
            return list;
        }
    }


    /**
     * @see #insert(SimpleStmt, int)
     */
    private int doExtractId(final ResultSet idResultSet, final GeneratedKeyStmt stmt) throws SQLException {

        try (ResultSet resultSet = idResultSet) {

            final PrimaryFieldMeta<?> idField = stmt.idField();
            final MappingType type = idField.mappingType();
            final SqlType sqlType;
            sqlType = getSqlType(resultSet.getMetaData(), 1);
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
     * @see #batchQuery(BatchStmt, int, Class, Object, Supplier)
     * @see #batchQueryAsMap(BatchStmt, int, Supplier, Map, Supplier)
     */
    private <R> List<R> doBatchQuery(final BatchStmt stmt, final int timeout, final @Nullable R terminator,
                                     final Supplier<List<R>> listConstructor,
                                     final Function<ResultSetMetaData, RowReader<R>> function) {

        if (terminator == null) {
            throw _Exceptions.terminatorIsNull();
        } else if (terminator == Collections.EMPTY_MAP) {
            throw new IllegalArgumentException();
        }

        final List<List<SQLParam>> paramGroupList = stmt.groupList();
        final int groupSize;
        if (timeout < 0 || (groupSize = paramGroupList.size()) == 0) {
            throw new IllegalArgumentException();
        }
        List<R> resultList = listConstructor.get();
        if (resultList == null) {
            throw _Exceptions.listConstructorError();
        }

        try (PreparedStatement statement = this.conn.prepareStatement(stmt.sqlText())) {
            final long startTime;
            if (timeout > 0) {
                startTime = System.currentTimeMillis();
            } else {
                startTime = 0;
            }
            final boolean optimistic = stmt.hasOptimistic();
            RowReader<R> rowReader = null;
            for (int i = 0, restSec = 0, readRows; i < groupSize; i++) {
                if (i > 0) {
                    statement.clearParameters();
                }
                if (timeout > 0) {
                    restSec = restSeconds(timeout, startTime);
                }

                bindParameter(statement, paramGroupList.get(i));

                if (restSec > 0) {
                    statement.setQueryTimeout(restSec);
                }
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (rowReader == null) {
                        rowReader = function.apply(resultSet.getMetaData());
                    }

                    for (readRows = 0; resultSet.next(); readRows++) {
                        resultList.add(rowReader.readOneRow(resultSet));
                    }

                    if (readRows == 0 && optimistic) {
                        throw _Exceptions.batchOptimisticLock(null, i + 1, readRows);
                    }

                    resultList.add(terminator);

                }// ResultSet


            }// outer for

            if (resultList instanceof ImmutableSpec) {
                resultList = _Collections.unmodifiableListForDeveloper(resultList);
            }
            return resultList;
        } catch (Throwable e) {
            throw wrapError(e);
        }
    }


    /**
     * @see #batchQuery(BatchStmt, int, Class, Object, Supplier)
     */
    private <T> RowReader<T> createBeanRowReader(final ResultSetMetaData metaData, final Class<T> resultClass,
                                                 final List<? extends Selection> selectionList) throws SQLException {
        final SqlType[] sqlTypeArray;
        sqlTypeArray = this.createSqlTypArray(metaData);
        final RowReader<T> rowReader;
        if (selectionList.size() == 1) {
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


    /**
     * @see #doQuery(SimpleStmt, int, Supplier, Function)
     * @see JdbcMultiResult#query(Class, Supplier)
     * @see JdbcMultiResult#queryMap(Supplier, Supplier)
     */
    private static <R> List<R> readList(final ResultSet set, final RowReader<R> rowReader,
                                        final @Nullable GenericSimpleStmt stmt, final boolean optimistic,
                                        final Supplier<List<R>> listConstructor) {


        try (ResultSet resultSet = set) {

            List<R> list = listConstructor.get();
            if (list == null) {
                throw _Exceptions.listConstructorError();
            }

            while (resultSet.next()) {
                list.add(rowReader.readOneRow(resultSet));
            }
            if (optimistic && list.size() == 0) {
                throw _Exceptions.optimisticLock(0);
            }
            if (list instanceof ImmutableSpec
                    && !(stmt instanceof TwoStmtModeQuerySpec && rowReader instanceof MapReader)) {
                list = _Collections.unmodifiableListForDeveloper(list);
            }
            return list;
        } catch (SQLException e) { // other error is handled by invoker
            throw wrapError(e);
        }

    }



    static ArmyException wrapError(final Throwable error) {
        final ArmyException e;
        if (error instanceof SQLException) {
            e = new DataAccessException(error);
        } else if (error instanceof ArmyException) {
            e = (ArmyException) error;
        } else {
            e = _Exceptions.unknownError(error.getMessage(), error);
        }
        return e;
    }

    /**
     * @see #secondQuery(TwoStmtQueryStmt, int, Class, List)
     */
    private static <R> Map<Object, Integer> createFirstStmtRowMap(final List<R> resultList, final ObjectAccessor accessor,
                                                                  final String idFieldName) {
        final int resultSize;
        resultSize = resultList.size();
        final Map<Object, Integer> map = _Collections.hashMap((int) (resultSize / 0.75f));
        final boolean columnResultSet = accessor == ObjectAccessorFactory.PSEUDO_ACCESSOR;
        Object id;
        R row;
        for (int i = 0; i < resultSize; i++) {
            row = resultList.get(i);
            if (columnResultSet) {
                id = row;
            } else {
                id = accessor.get(row, idFieldName);
            }
            if (id == null) {
                throw _Exceptions.firstStmtIdIsNull();
            }
            if (map.putIfAbsent(id, i) != null) {
                String m = String.format("id[%s] duplication", id);
                throw new DataAccessException(m);
            }
        }
        return _Collections.unmodifiableMap(map);
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
        } catch (Throwable e) {
            throw new ArmyException("unknown error", e);
        }

    }


    private static SQLException insertedRowsAndGenerateIdNotMatch(int insertedRows, int actualCount) {
        String m = String.format("insertedRows[%s] and generateKeys count[%s] not match.", insertedRows,
                actualCount);
        return new SQLException(m);
    }


    private static abstract class RowReader<R> {

        final JdbcExecutor executor;

        final List<? extends Selection> selectionList;


        private final SqlType[] sqlTypeArray;

        final ObjectAccessor accessor;

        private MappingType[] compatibleTypeArray;

        private final Class<R> resultClass;


        private RowReader(JdbcExecutor executor, List<? extends Selection> selectionList,
                          SqlType[] sqlTypeArray, Class<R> resultClass, ObjectAccessor accessor) {
            if (selectionList.size() != sqlTypeArray.length) {
                throw _Exceptions.columnCountAndSelectionCountNotMatch(sqlTypeArray.length, selectionList.size());
            }
            this.executor = executor;
            this.selectionList = selectionList;
            this.sqlTypeArray = sqlTypeArray;
            this.resultClass = resultClass;
            this.accessor = accessor;
        }

        @SuppressWarnings("unchecked")
        @Nullable
        final R readOneRow(final ResultSet resultSet) throws SQLException {
            final JdbcExecutor executor = this.executor;
            final MappingEnv env = executor.factory.mappingEnv;
            final SqlType[] sqlTypeArray = this.sqlTypeArray;
            final ObjectAccessor accessor = this.accessor;
            final List<? extends Selection> selectionList = this.selectionList;

            MappingType[] compatibleTypeArray = this.compatibleTypeArray;

            R row;
            if (accessor == ObjectAccessorFactory.PSEUDO_ACCESSOR) {
                assert sqlTypeArray.length == 1;
                row = null;
            } else {
                row = this.createRow();
            }
            TypeMeta typeMeta;
            MappingType type;
            Selection selection;
            Object columnValue;
            SqlType sqlType;
            String fieldName;
            for (int i = 0; i < sqlTypeArray.length; i++) {
                sqlType = sqlTypeArray[i];

                columnValue = executor.get(resultSet, i + 1, sqlType);

                selection = selectionList.get(i);
                fieldName = selection.alias();

                if (columnValue == null) {
                    if (row != null) {
                        accessor.set(row, fieldName, null);
                    }
                    continue;
                }

                if (compatibleTypeArray == null || (type = compatibleTypeArray[i]) == null) {
                    if (compatibleTypeArray == null) {
                        this.compatibleTypeArray = compatibleTypeArray = new MappingType[sqlTypeArray.length];
                    }
                    type = compatibleTypeFrom(selection, this.resultClass, accessor, fieldName);
                    compatibleTypeArray[i] = type;
                }

                columnValue = type.afterGet(sqlType, env, columnValue);
                //TODO field codec
                if (row == null) {
                    row = (R) columnValue;
                } else {
                    accessor.set(row, fieldName, columnValue);
                }
            }

            if (row instanceof Map && row instanceof ImmutableSpec && this instanceof MapReader) {
                row = this.unmodifiedMap(row);
            }
            return row;
        }

        abstract R createRow();

        R unmodifiedMap(R map) {
            throw new UnsupportedOperationException();
        }


    }//RowReader

    private static final class BeanRowReader<R> extends RowReader<R> {


        private final Constructor<R> constructor;


        private BeanRowReader(JdbcExecutor executor, List<? extends Selection> selectionList, Class<R> resultClass,
                              SqlType[] sqlTypeArray) {
            super(executor, selectionList, sqlTypeArray, resultClass, ObjectAccessorFactory.forBean(resultClass));
            this.constructor = ObjectAccessorFactory.getConstructor(resultClass);

        }

        @Override
        R createRow() {
            return ObjectAccessorFactory.createBean(this.constructor);
        }


    }//BeanRowReader

    private static final class SingleColumnRowReader<R> extends RowReader<R> {

        private SingleColumnRowReader(JdbcExecutor executor, List<? extends Selection> selectionList,
                                      SqlType[] sqlTypeArray, Class<R> resultClass) {
            super(executor, selectionList, sqlTypeArray, resultClass, ObjectAccessorFactory.PSEUDO_ACCESSOR);
        }

        @Override
        R createRow() {
            // no bug,never here
            throw new UnsupportedOperationException();
        }


    }//SingleColumnRowReader


    private static final class MapReader extends RowReader<Map<String, Object>> {

        private final Supplier<Map<String, Object>> mapConstructor;

        private final boolean twoStmtMode;

        private MapReader(JdbcExecutor executor, List<? extends Selection> selectionList, boolean twoStmtMode,
                          SqlType[] sqlTypeArray,
                          Supplier<Map<String, Object>> mapConstructor) {
            super(executor, selectionList, sqlTypeArray, _ClassUtils.mapJavaClass(),
                    ObjectAccessorFactory.forMap());
            this.mapConstructor = mapConstructor;
            this.twoStmtMode = twoStmtMode;
        }

        @Override
        Map<String, Object> createRow() {
            final Map<String, Object> map;
            map = this.mapConstructor.get();
            if (map == null) {
                throw _Exceptions.mapConstructorError();
            }
            return map;
        }


        @Override
        Map<String, Object> unmodifiedMap(final Map<String, Object> map) {
            if (this.twoStmtMode) {
                return map;
            }
            return _Collections.unmodifiableMapForDeveloper(map);
        }


    }//MapReader

    private static final class SecondRowReader<R> extends RowReader<R> {

        private R currentRow;

        /**
         * @see #secondQuery(TwoStmtQueryStmt, int, Class, List)
         */
        private SecondRowReader(JdbcExecutor executor, List<? extends Selection> selectionList,
                                SqlType[] sqlTypeArray, Class<R> resultClass, ObjectAccessor accessor) {
            super(executor, selectionList, sqlTypeArray, resultClass, accessor);
        }

        @Override
        R createRow() {
            final R row = this.currentRow;
            if (row == null) {
                // no bug,never here
                throw new NullPointerException();
            }
            return row;
        }

    }//SecondRowReader


    private static abstract class JdbcRowSpliterator<R> implements Spliterator<R> {

        private final RowReader<R> rowReader;

        private final boolean hasOptimistic;

        private final int fetchSize;

        private final int splitSize;

        private boolean closed;

        private int rowCount = 0;

        private boolean canceled;


        private JdbcRowSpliterator(RowReader<R> rowReader, boolean hasOptimistic, StreamOptions options) {
            this.rowReader = rowReader;
            this.hasOptimistic = hasOptimistic;
            this.fetchSize = options.fetchSize;
            this.splitSize = options.splitSize;

            assert this.fetchSize > 0;
        }


        @Override
        public boolean tryAdvance(final Consumer<? super R> action) {
            return this.doTryAdvance(this.fetchSize, action);
        }

        @Nullable
        @Override
        public final Spliterator<R> trySplit() {
            final int splitSize = this.splitSize;
            if (this.closed || this.canceled || splitSize < 1) {
                return null;
            }

            final List<R> rowList = _Collections.arrayList(Math.min(300, splitSize));
            this.doTryAdvance(splitSize, rowList::add);
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

        @Override
        public final int characteristics() {
            int bitSet;
            bitSet = this.fetchSize == 1 ? Spliterator.ORDERED : 0;
            if (this.rowReader.accessor != ObjectAccessorFactory.PSEUDO_ACCESSOR) {
                bitSet |= Spliterator.NONNULL;
            }
            return bitSet;
        }

        @Nullable
        abstract Statement getStatement();

        abstract ResultSet getCurrentResult();

        @Nullable
        abstract ResultSet tryGetCurrentResult();

        @Nullable
        abstract ResultSet nextResultSet(Consumer<? super R> action) throws SQLException;

        final void simpleCancel() {
            this.canceled = true;
        }

        final void parallelCancel() {
            if (this.canceled) {
                return;
            }
            synchronized (this) {
                this.canceled = true;
            }
        }


        private boolean doTryAdvance(final int expectedFetchSize, final @Nullable Consumer<? super R> action) {
            try {
                if (action == null) {
                    throw new NullPointerException();
                }

                ResultSet resultSet;
                resultSet = this.getCurrentResult();
                final RowReader<R> rowReader = this.rowReader;

                boolean hasMore = !this.closed && !this.canceled;

                final int actualFetchSize = hasMore ? expectedFetchSize : 0;
                final int oldRowCount = this.rowCount;
                int readRowCount = 0;
                for (int i = 0; i < actualFetchSize; i++) {
                    if (!resultSet.next()) {
                        resultSet = this.nextResultSet(action);
                        if (resultSet == null) {
                            hasMore = false;
                            break;
                        }
                        continue;
                    }
                    action.accept(rowReader.readOneRow(resultSet));
                    readRowCount++;

                    if (this.canceled) { // at least read one row
                        hasMore = false;
                        break;
                    }

                }

                if (this.rowCount != oldRowCount) {
                    throw new ConcurrentModificationException();
                } else if (actualFetchSize > 0 && oldRowCount == 0 && readRowCount == 0 && this.hasOptimistic) {
                    throw _Exceptions.optimisticLock(readRowCount);
                }

                this.rowCount += readRowCount; //  this.rowCount don't need to be long ,because this just for test whether ConcurrentModificationException or not.

                if (this.rowCount < 0) {
                    this.rowCount = 1; // for this.hasOptimistic
                }

                if (!hasMore) {
                    this.closeStream();
                }
                return readRowCount > 0;
            } catch (Throwable e) {
                this.closeStream();
                throw wrapError(e);
            }

        }

        private void closeStream() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            closeResultSetAndStatement(this.tryGetCurrentResult(), this.getStatement());
        }


    }//JdbcRowSpliterator


    private static final class RowSpliterator<R> extends JdbcRowSpliterator<R> {

        private final Statement statement;

        private ResultSet resultSet;


        private RowSpliterator(@Nullable Statement statement, ResultSet resultSet, RowReader<R> rowReader,
                               boolean hasOptimistic, StreamOptions options) {
            super(rowReader, hasOptimistic, options);
            this.statement = statement;
            this.resultSet = resultSet;

        }

        @Override
        Statement getStatement() {
            return this.statement;
        }

        @Override
        ResultSet getCurrentResult() {
            return this.resultSet;
        }

        @Override
        ResultSet tryGetCurrentResult() {
            return this.resultSet;
        }

        @Override
        ResultSet nextResultSet(Consumer<? super R> action) throws SQLException {
            final ResultSet prevResultSet = this.resultSet;
            if (prevResultSet == null) {
                throw new ConcurrentModificationException();
            }
            prevResultSet.close(); // close prevResultSet
            this.resultSet = null; // clear avoid close again
            return null;
        }


    }//RowSpliterator


    private static final class BatchRowSpliterator<R> extends JdbcRowSpliterator<R> {

        private final JdbcExecutor executor;

        private final PreparedStatement statement;

        private final List<List<SQLParam>> paramGroupList;

        private final long startTime;

        private final int timeout;

        private final int groupSize;

        private final R terminator;

        private ResultSet resultSet;

        private int groupIndex = 1; // here is 1 not 0

        /**
         * @see #doBatchQueryStream(BatchStmt, int, Object, StreamOptions, Function)
         */
        private BatchRowSpliterator(PreparedStatement statement, ResultSet resultSet,
                                    int timeout, long startTime, RowReader<R> rowReader, R terminator,
                                    BatchStmt stmt, StreamOptions options) {
            super(rowReader, stmt.hasOptimistic(), options);

            this.executor = rowReader.executor;
            this.statement = statement;
            this.resultSet = resultSet;
            this.paramGroupList = stmt.groupList();

            this.timeout = timeout;
            this.startTime = startTime;
            this.groupSize = this.paramGroupList.size();
            this.terminator = terminator;
            assert this.groupSize > 0;
            Objects.requireNonNull(terminator);

        }


        @Override
        Statement getStatement() {
            return this.statement;
        }

        @Override
        ResultSet getCurrentResult() {
            final ResultSet resultSet = this.resultSet;
            if (resultSet == null) {
                throw new ConcurrentModificationException();
            }
            return resultSet;
        }

        @Override
        ResultSet tryGetCurrentResult() {
            return this.resultSet;
        }

        @Override
        ResultSet nextResultSet(final Consumer<? super R> action) throws SQLException {

            final int currentIndex = this.groupIndex++;
            if (currentIndex > this.groupSize) {
                throw new ConcurrentModificationException();
            }
            // 1. append terminator
            action.accept(this.terminator); // append terminator

            // 2. close ResultSet
            final ResultSet prevResultSet = this.resultSet;
            if (prevResultSet == null) {
                throw new ConcurrentModificationException();
            }
            this.resultSet = null; // clear avoid close again
            prevResultSet.close(); // close prevResultSet

            if (currentIndex == this.groupSize) {
                return null;
            }
            // 3. execute next group
            final int restSec;
            restSec = restSeconds(this.timeout, this.startTime);

            final PreparedStatement statement = this.statement;
            statement.clearParameters();

            this.executor.bindParameter(statement, this.paramGroupList.get(currentIndex));

            if (restSec > 0) {
                statement.setQueryTimeout(restSec);
            }
            final ResultSet newResultSet;

            newResultSet = statement.executeQuery();

            this.resultSet = newResultSet;
            return newResultSet;
        }


    }//BatchRowSpliterator

    private static final class MultiStmtBatchRowSpliterator<R> extends JdbcRowSpliterator<R> {

        private final Statement statement;

        private final R terminator;
        private final int resultSize;
        private int resultIndex = 1; // 1 not 0

        private ResultSet resultSet;

        /**
         * @see JdbcExecutor#doMultiStmtBatchQueryStream(BatchStmt, int, Object, StreamOptions, Function)
         */
        private MultiStmtBatchRowSpliterator(Statement statement, final int resultSize, ResultSet resultSet,
                                             RowReader<R> rowReader, R terminator, boolean hasOptimistic,
                                             StreamOptions options) {
            super(rowReader, hasOptimistic, options);
            assert resultSize > 0;
            Objects.requireNonNull(terminator);
            this.statement = statement;
            this.terminator = terminator;
            this.resultSize = resultSize;
            this.resultSet = resultSet;
        }

        @Override
        Statement getStatement() {
            return this.statement;
        }

        @Override
        ResultSet getCurrentResult() {
            final ResultSet resultSet = this.resultSet;
            if (resultSet == null) {
                throw new ConcurrentModificationException();
            }
            return resultSet;
        }

        @Override
        ResultSet tryGetCurrentResult() {
            return this.resultSet;
        }

        @Override
        ResultSet nextResultSet(final Consumer<? super R> action) throws SQLException {

            final ResultSet prevResultSet = this.resultSet;
            if (prevResultSet == null) {
                throw new ConcurrentModificationException();
            }
            this.resultSet = null; // clear avoid close again
            prevResultSet.close(); // close prevResultSet

            final Statement statement = this.statement;

            final int currentIndex = this.resultIndex++;
            if (currentIndex >= this.resultSize) {
                statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
                throw _Exceptions.batchCountNotMatch(this.resultSize, currentIndex + 1);
            }

            //  append terminator
            action.accept(this.terminator); // append terminator


            final ResultSet newResultSet;
            if (statement.getMoreResults()) {
                newResultSet = statement.getResultSet();
            } else if (statement.getUpdateCount() == -1) {
                // end
                newResultSet = null;
            } else {
                // stmt no bug,never here
                statement.getMoreResults(Statement.CLOSE_ALL_RESULTS);
                throw _Exceptions.batchQueryReturnUpdate();
            }

            this.resultSet = newResultSet;
            return newResultSet;
        }


    }//MultiStmtBatchRowSpliterator

    private static abstract class JdbcMultiResultSpec implements MultiResultSpec {

        private final JdbcExecutor executor;

        private final Statement statement;

        private final List<MultiStmt.StmtItem> stmtItemList;

        private final int stmtItemSize;

        private final boolean procedureItem;

        private State state;

        private long updateCount;

        private int stmtItemIndex;

        private int procedureSubItemIndex = -1;

        private JdbcMultiResultSpec(JdbcExecutor executor, Statement statement, List<MultiStmt.StmtItem> stmtItemList) {
            this.executor = executor;
            this.statement = statement;
            this.stmtItemList = stmtItemList;
            this.stmtItemSize = stmtItemList.size();

            assert this.stmtItemSize > 0;

            if (this.stmtItemSize == 1 && stmtItemList.get(0) instanceof MultiStmt.ProcedureItem) {
                this.procedureItem = true;
                this.stmtItemIndex = 0; // always 0
            } else {
                this.procedureItem = false;
                this.stmtItemIndex = -1;
            }


        }


        @Override
        public final State next() {
            final State oldState = this.state;
            if (oldState == State.NONE) {
                return oldState;
            }
            try {
                final Statement statement = this.statement;
                final boolean hasResults;
                final int oldStmtIndex = this.stmtItemIndex, oldProcedureItemIndex = this.procedureSubItemIndex;

                if (oldState == State.QUERY) {
                    // developer don't deal current result set
                    hasResults = statement.getMoreResults(Statement.CLOSE_CURRENT_RESULT);
                } else {
                    hasResults = statement.getMoreResults();
                }
                final State newState;
                if (hasResults) {
                    this.updateCount = -1;
                    newState = State.QUERY;
                } else {
                    final long rows;
                    if (this.executor.factory.useLargeUpdate) {
                        rows = statement.getLargeUpdateCount();
                    } else {
                        rows = statement.getUpdateCount();
                    }
                    newState = rows == -1 ? State.NONE : State.UPDATE;
                    this.updateCount = rows;
                }

                if (this.state != oldState
                        || this.stmtItemIndex != oldStmtIndex
                        || oldProcedureItemIndex != this.procedureSubItemIndex) {
                    throw new ConcurrentModificationException();
                }
                if (this.procedureItem) {
                    this.procedureSubItemIndex = oldProcedureItemIndex + 1;
                    assert oldStmtIndex == 0;
                } else {
                    this.stmtItemIndex = oldStmtIndex + 1;
                }
                this.state = newState;
                return newState;
            } catch (Throwable e) {
                this.close();
                throw wrapError(e);
            }

        }


        @Override
        public final long updateCount() throws DataAccessException {
            final State currentState = this.state;
            if (currentState == null) {
                throw this.dontInvokeNext();
            }

            try {

                switch (currentState) {
                    case UPDATE:// no-op
                        break;
                    case QUERY:
                        throw _Exceptions.currentResultIsQuery();
                    case NONE:
                        throw _Exceptions.noMoreResult();
                    default:
                        throw _Exceptions.unexpectedEnum(currentState);
                }

                final long updateCount;
                updateCount = this.updateCount;
                if (updateCount == -1) {
                    throw new ConcurrentModificationException();
                }
                this.state = null;
                this.updateCount = -1;
                return updateCount;
            } catch (Throwable e) {
                this.close();
                throw wrapError(e);
            }
        }

        @Override
        public final <R> R queryOne(final Class<R> resultClass) throws ArmyException {
            return this.beanQuery(resultClass, (s, rowReader, optimistic) -> {
                        R result = null;
                        try (ResultSet resultSet = s) {
                            while (resultSet.next()) {
                                if (result != null) {
                                    throw _Exceptions.nonUnique(resultClass);
                                }
                                result = rowReader.readOneRow(resultSet);
                            }
                            if (result == null && optimistic) {
                                throw _Exceptions.optimisticLock(0);
                            }
                            return result;
                        } catch (SQLException e) {
                            throw wrapError(e);
                        }
                    }
            );
        }

        @Override
        public final Map<String, Object> queryOneMap() throws ArmyException {
            return this.queryOneMap(_Collections::hashMap);
        }

        @Override
        public final Map<String, Object> queryOneMap(final Supplier<Map<String, Object>> mapConstructor)
                throws ArmyException {
            return this.mapQuery(mapConstructor, (s, rowReader, optimistic) -> {
                Map<String, Object> result = null;
                try (ResultSet resultSet = s) {
                    while (resultSet.next()) {
                        if (result != null) {
                            throw _Exceptions.nonUnique(Map.class);
                        }
                        result = rowReader.readOneRow(resultSet);
                    }
                    if (result == null && optimistic) {
                        throw _Exceptions.optimisticLock(0);
                    }
                    return result;
                } catch (SQLException e) {
                    throw wrapError(e);
                }
            });
        }

        @Override
        public final void close() throws ArmyException {
            final State currentState = this.state;
            if (currentState == State.NONE) {
                return;
            }
            this.state = State.NONE;
            this.stmtItemIndex = this.stmtItemSize;
            Throwable error = null;
            if (currentState == State.QUERY) {
                try {
                    this.statement.getMoreResults(Statement.CLOSE_CURRENT_RESULT);
                } catch (Throwable e) {
                    error = e;
                }
            }

            try {
                this.statement.close();
                if (error != null) {
                    throw wrapError(error);
                }
            } catch (ArmyException e) {
                throw e;
            } catch (Throwable e) {
                throw wrapError(e);
            }


        }


        final <T, R> R beanQuery(final Class<T> resultClass,
                                 final TeFunction<ResultSet, RowReader<T>, Boolean, R> function) {
            final BiFunction<List<? extends Selection>, SqlType[], RowReader<T>> readerFunc;
            readerFunc = (selectionList, sqlTypeArray) -> {
                final RowReader<T> rowReader;
                if (sqlTypeArray.length == 1) {
                    rowReader = new SingleColumnRowReader<>(this.executor, selectionList, sqlTypeArray,
                            resultClass);
                } else {
                    rowReader = new BeanRowReader<>(this.executor, selectionList, resultClass, sqlTypeArray);
                }
                return rowReader;
            };
            return this.doNextQuery(readerFunc, function);
        }

        final <R> R mapQuery(final Supplier<Map<String, Object>> mapConstructor,
                             final TeFunction<ResultSet, RowReader<Map<String, Object>>, Boolean, R> function) {
            final BiFunction<List<? extends Selection>, SqlType[], RowReader<Map<String, Object>>> readerFunc;
            readerFunc = (selectionList, sqlTypeArray) ->
                    new MapReader(this.executor, selectionList, false, sqlTypeArray, mapConstructor); //TODO two stmt mode
            return this.doNextQuery(readerFunc, function);
        }

        private <T, R> R doNextQuery(final BiFunction<List<? extends Selection>, SqlType[], RowReader<T>> readerConstructor,
                                     final TeFunction<ResultSet, RowReader<T>, Boolean, R> function) {

            final State currentState = this.state;
            if (currentState == null) {
                throw this.dontInvokeNext();
            }
            try {
                switch (currentState) {
                    case QUERY: //no-op
                        break;
                    case UPDATE:
                        throw _Exceptions.currentResultIsUpdate();
                    case NONE:
                        throw _Exceptions.noMoreResult();
                    default:
                        throw _Exceptions.unexpectedEnum(currentState);
                }

                final int itemIndex = this.stmtItemIndex;
                assert itemIndex > -1 && itemIndex < this.stmtItemSize;

                // get ResultSet
                final ResultSet resultSet;
                resultSet = this.statement.getResultSet();
                final ResultSetMetaData metaData;
                metaData = resultSet.getMetaData();

                final MultiStmt.StmtItem currentItem = this.stmtItemList.get(itemIndex);

                final List<? extends Selection> selectionList;
                final SqlType[] sqlTypeArray;
                if (currentItem instanceof MultiStmt.UpdateStmt) {
                    throw _Exceptions.exceptedError(itemIndex + 1, currentItem, "query result");
                } else if (currentItem instanceof MultiStmt.QueryStmt) {
                    selectionList = ((MultiStmt.QueryStmt) currentItem).selectionList();
                    sqlTypeArray = this.executor.createSqlTypArray(metaData);
                } else if (!(currentItem instanceof MultiStmt.ProcedureItem && this.procedureItem)) {
                    // no bug,never here, MultiStmt bug
                    throw _Exceptions.unknownStmtItem(currentItem);
                } else if (((MultiStmt.ProcedureItem) currentItem).resultItemList().isEmpty()) {
                    sqlTypeArray = this.executor.createSqlTypArray(metaData);
                    final IntFunction<String> columnNameFunc;
                    columnNameFunc = index -> {
                        try {
                            return metaData.getColumnLabel(index);
                        } catch (SQLException e) {
                            throw wrapError(e);
                        }
                    };
                    selectionList = SqlTypeUtils.mapSelectionList(sqlTypeArray, columnNameFunc);
                } else {
                    final List<MultiStmt.StmtItem> procedureSubItemList;
                    procedureSubItemList = ((MultiStmt.ProcedureItem) currentItem).resultItemList();
                    final int subItemIndex = this.procedureSubItemIndex;
                    assert subItemIndex > -1;
                    if (subItemIndex >= procedureSubItemList.size()) {
                        throw new UnsupportedOperationException();
                    }
                    final MultiStmt.StmtItem subItem = procedureSubItemList.get(subItemIndex);
                    if (subItem instanceof MultiStmt.UpdateStmt) {
                        throw _Exceptions.exceptedError(subItemIndex + 1, subItem, "query result");
                    } else if (subItem instanceof MultiStmt.ProcedureItem) {
                        // no bug,never here, MultiStmt bug, army don't support nested
                        throw _Exceptions.unknownStmtItem(currentItem);
                    } else if (!(subItem instanceof MultiStmt.QueryStmt)) {
                        // no bug,never here, MultiStmt bug
                        throw _Exceptions.unknownStmtItem(currentItem);
                    }
                    selectionList = ((MultiStmt.QueryStmt) subItem).selectionList();
                    sqlTypeArray = this.executor.createSqlTypArray(metaData);
                }

                if (sqlTypeArray.length != selectionList.size()) {
                    throw _Exceptions.columnCountAndSelectionCountNotMatch(sqlTypeArray.length, selectionList.size());
                }

                final R result;
                result = function.apply(resultSet, readerConstructor.apply(selectionList, sqlTypeArray),
                        currentItem.hasOptimistic());
                this.state = null;
                return result;
            } catch (Throwable e) {
                this.close();
                throw wrapError(e);
            }
        }

        private DataAccessException dontInvokeNext() {
            this.close();
            return new DataAccessException("Don't invoke next method.");
        }


    }//JdbcMultiResultSpec

    private static final class JdbcMultiResult extends JdbcMultiResultSpec implements MultiResult {

        private JdbcMultiResult(JdbcExecutor executor, Statement statement, List<MultiStmt.StmtItem> stmtItemList) {
            super(executor, statement, stmtItemList);
        }

        @Override
        public <R> List<R> query(Class<R> resultClass) throws DataAccessException {
            return this.query(resultClass, _Collections::arrayList);
        }

        @Override
        public <R> List<R> query(final Class<R> resultClass, final Supplier<List<R>> listConstructor)
                throws DataAccessException {
            return this.beanQuery(resultClass, (s, rowReader, optimistic) ->
                    readList(s, rowReader, null, optimistic, listConstructor) //TODO
            );
        }


        @Override
        public List<Map<String, Object>> queryMap() throws DataAccessException {
            return this.queryMap(_Collections::hashMap, _Collections::arrayList);
        }

        @Override
        public List<Map<String, Object>> queryMap(Supplier<Map<String, Object>> mapConstructor)
                throws DataAccessException {
            return this.queryMap(mapConstructor, _Collections::arrayList);
        }


        @Override
        public List<Map<String, Object>> queryMap(final Supplier<Map<String, Object>> mapConstructor,
                                                  final Supplier<List<Map<String, Object>>> listConstructor)
                throws DataAccessException {
            return this.mapQuery(mapConstructor, (s, mapReader, optimistic) ->
                    readList(s, mapReader, null, optimistic, listConstructor) //TODO
            );
        }


    }//JdbcMultiResult


    private static final class JdbcMultiStream extends JdbcMultiResultSpec implements MultiStream {

        private final StreamOptions options;

        private JdbcMultiStream(JdbcExecutor executor, Statement statement, List<MultiStmt.StmtItem> stmtItemList,
                                StreamOptions options) {
            super(executor, statement, stmtItemList);
            this.options = options;
        }


        @Override
        public <R> Stream<R> query(Class<R> resultClass) throws DataAccessException {
            return this.query(resultClass, this.options);
        }

        @Override
        public <R> Stream<R> query(final Class<R> resultClass, final StreamOptions options)
                throws ArmyException {
            return this.beanQuery(resultClass, (set, rowReader, optimistic) ->
                    this.createPartStream(set, rowReader, optimistic, options)
            );
        }


        @Override
        public Stream<Map<String, Object>> queryMap() throws DataAccessException {
            return this.queryMap(_Collections::hashMap, this.options);
        }

        @Override
        public Stream<Map<String, Object>> queryMap(Supplier<Map<String, Object>> mapConstructor)
                throws DataAccessException {
            return this.queryMap(mapConstructor, this.options);
        }


        @Override
        public Stream<Map<String, Object>> queryMap(StreamOptions options) throws ArmyException {
            return this.queryMap(_Collections::hashMap, options);
        }

        @Override
        public Stream<Map<String, Object>> queryMap(final Supplier<Map<String, Object>> mapConstructor,
                                                    StreamOptions options) throws ArmyException {
            return this.mapQuery(mapConstructor, (set, rowReader, optimistic) ->
                    this.createPartStream(set, rowReader, optimistic, options)
            );
        }


        private <R> Stream<R> createPartStream(final ResultSet resultSet, final RowReader<R> rowReader,
                                               final boolean optimistic, final StreamOptions options) {

            final RowSpliterator<R> spliterator;
            // statement must pass null, because this spliterator representing only part.
            spliterator = new RowSpliterator<>(null, resultSet, rowReader, optimistic, options);
            final Consumer<StreamCommander> consumer;
            if (options == this.options) { //now consumer representing whole, so ignore.
                consumer = null;
            } else {
                consumer = options.commanderConsumer;
            }
            if (consumer != null) {
                if (options.parallel) {
                    consumer.accept(spliterator::parallelCancel);
                } else {
                    consumer.accept(spliterator::simpleCancel);
                }
            }
            return StreamSupport.stream(spliterator, options.parallel);
        }


    }//JdbcMultiStream


}
