package io.army.jdbc;

import io.army.ArmyException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.codec.FieldCodec;
import io.army.criteria.SQLParam;
import io.army.criteria.Selection;
import io.army.criteria.impl.SqlTypeUtils;
import io.army.function.TeFunction;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.session.DataAccessException;
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
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

abstract class JdbcExecutor implements StmtExecutor {


    final JdbcExecutorFactory factory;

    final Connection conn;

    Map<FieldMeta<?>, FieldCodec> fieldCodecMap;

    JdbcExecutor(JdbcExecutorFactory factory, Connection conn) {
        this.factory = factory;
        this.conn = conn;
    }

    public static ArmyException wrapError(final Throwable error) {
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
                    extractGenerateKeys(statement, rows, (GeneratedKeyStmt) stmt);
                }
            }
            if (rows < 1) {
                throw new SQLException(String.format("insert statement affected %s rows", rows));
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
                                        final Supplier<List<Long>> listConstructor,
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
    public final List<Long> multiStmtBatchUpdate(final MultiStmt stmt, final int timeout,
                                                 final Supplier<List<Long>> listConstructor,
                                                 final @Nullable TableMeta<?> domainTable) {


        List<Long> list = listConstructor.get();
        if (list == null) {
            throw _Exceptions.listConstructorError();
        }
        try (Statement statement = this.conn.createStatement()) {

            if (statement.execute(stmt.multiSql())) {
                // sql error
                throw new DataAccessException("error,multi-statement batch update the first result is ResultSet");
            }
            if (domainTable instanceof ChildTableMeta) {
                handleChildMultiStmtBatchUpdate(statement, stmt, (ChildTableMeta<?>) domainTable, list);
            } else {
                // SingleTableMeta batch update or multi-table batch update.
                handleSimpleMultiStmtBatchUpdate(statement, stmt, domainTable, list);
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
        return this.doQuery(stmt, timeout, listConstructor, this.createFuncForBean(stmt.selectionList(), resultClass));

    }


    @Override
    public final List<Map<String, Object>> queryAsMap(SimpleStmt stmt, int timeout,
                                                      Supplier<Map<String, Object>> mapConstructor,
                                                      Supplier<List<Map<String, Object>>> listConstructor) {
        return this.doQuery(stmt, timeout, listConstructor, this.createFuncForMap(stmt.selectionList(), mapConstructor));
    }

    @Override
    public final <R> List<R> batchQuery(final BatchStmt stmt, final int timeout, final Class<R> resultClass,
                                        final @Nullable R terminator,
                                        final Supplier<List<R>> listConstructor) throws DataAccessException {
        if (terminator == null) {
            throw _Exceptions.terminatorIsNull();
        }

        final List<R> resultList = listConstructor.get();
        final RowReader<?>[] rowReaderHolder = new RowReader<?>[1];
        final List<? extends Selection> selectionList = stmt.selectionList();
        final boolean optimistic = stmt.hasOptimistic();

        final Consumer<ResultSet> resultSetConsumer;
        resultSetConsumer = resultSet -> {
            @SuppressWarnings("unchecked")
            RowReader<R> rowReader = (RowReader<R>) rowReaderHolder[0];
            if (rowReader == null) {
                try {
                    rowReader = this.createBeanRowReader(resultSet.getMetaData(), resultClass, selectionList);
                    rowReaderHolder[0] = rowReader;
                } catch (Throwable e) {
                    closeResource(resultSet);
                    throw wrapError(e);
                }
            }
            readRowToList(resultSet, rowReader, optimistic, resultList);
            resultList.add(terminator);

        };

        this.doBatchQuery(stmt, timeout, resultSetConsumer);


        List<R> list = resultList;
        if (list instanceof ImmutableSpec) {
            list = _Collections.unmodifiableListForDeveloper(list);
        }
        return list;
    }


    @Override
    public final List<Map<String, Object>> batchQueryAsMap(final BatchStmt stmt, final int timeout,
                                                           final Supplier<Map<String, Object>> mapConstructor,
                                                           final @Nullable Map<String, Object> terminator,
                                                           final Supplier<List<Map<String, Object>>> listConstructor)
            throws DataAccessException {
        if (terminator == null) {
            throw _Exceptions.terminatorIsNull();
        }

        final List<Map<String, Object>> resultList = listConstructor.get();
        final MapReader[] rowReaderHolder = new MapReader[1];
        final List<? extends Selection> selectionList = stmt.selectionList();
        final boolean optimistic = stmt.hasOptimistic();

        final Consumer<ResultSet> resultSetConsumer;
        resultSetConsumer = resultSet -> {
            MapReader rowReader = rowReaderHolder[0];
            if (rowReader == null) {
                try {
                    rowReader = this.createMapReader(resultSet.getMetaData(), mapConstructor, selectionList);
                    rowReaderHolder[0] = rowReader;
                } catch (Throwable e) {
                    closeResource(resultSet);
                    throw wrapError(e);
                }
            }
            readRowToList(resultSet, rowReader, optimistic, resultList);
            resultList.add(terminator);

        };

        this.doBatchQuery(stmt, timeout, resultSetConsumer);


        List<Map<String, Object>> list = resultList;
        if (list instanceof ImmutableSpec) {
            list = _Collections.unmodifiableListForDeveloper(list);
        }
        return list;
    }

    @Override
    public final <R> List<R> multiStmtBatchQuery(MultiStmt stmt, int timeout, Class<R> resultClass, R terminator,
                                                 Supplier<List<R>> listConstructor) throws DataAccessException {
        return null;
    }

    @Override
    public final List<Map<String, Object>> multiStmtBatchQueryAsMap(MultiStmt stmt, int timeout,
                                                                    Supplier<Map<String, Object>> mapConstructor,
                                                                    Map<String, Object> terminator,
                                                                    Supplier<List<Map<String, Object>>> listConstructor)
            throws DataAccessException {
        return null;
    }

    @Override
    public final <R> Stream<R> queryStream(SimpleStmt stmt, int timeout, Class<R> resultClass,
                                           final StreamOptions options) {
        return this.queryAsStream(stmt, timeout, options, this.createFuncForBean(stmt.selectionList(), resultClass));
    }


    @Override
    public final Stream<Map<String, Object>> queryMapStream(SimpleStmt stmt, int timeout,
                                                            final Supplier<Map<String, Object>> mapConstructor,
                                                            StreamOptions options) {
        return this.queryAsStream(stmt, timeout, options, this.createFuncForMap(stmt.selectionList(), mapConstructor));
    }

    @Override
    public final <R> Stream<R> batchQueryStream(BatchStmt stmt, int timeout, Class<R> resultClass, R terminator,
                                                StreamOptions options) throws DataAccessException {
        if (terminator == null) {
            throw _Exceptions.terminatorIsNull();
        }

        final List<R> resultList = listConstructor.get();
        final RowReader<?>[] rowReaderHolder = new RowReader<?>[1];
        final List<? extends Selection> selectionList = stmt.selectionList();
        final boolean optimistic = stmt.hasOptimistic();

        final Consumer<ResultSet> resultSetConsumer;
        resultSetConsumer = resultSet -> {
            @SuppressWarnings("unchecked")
            RowReader<R> rowReader = (RowReader<R>) rowReaderHolder[0];
            if (rowReader == null) {
                try {
                    rowReader = this.createBeanRowReader(resultSet.getMetaData(), resultClass, selectionList);
                    rowReaderHolder[0] = rowReader;
                } catch (Throwable e) {
                    closeResource(resultSet);
                    throw wrapError(e);
                }
            }
            readRowToList(resultSet, rowReader, optimistic, resultList);
            resultList.add(terminator);

        };

        this.doBatchQuery(stmt, timeout, resultSetConsumer);


        List<R> list = resultList;
        if (list instanceof ImmutableSpec) {
            list = _Collections.unmodifiableListForDeveloper(list);
        }
        return null;
    }

    @Override
    public final Stream<Map<String, Object>> batchQueryMapStream(BatchStmt stmt, int timeout,
                                                                 Supplier<Map<String, Object>> mapConstructor,
                                                                 Map<String, Object> terminator, StreamOptions options)
            throws DataAccessException {
        return null;
    }

    @Override
    public final <R> Stream<R> multiStmtBatchQueryStream(MultiStmt stmt, int timeout, Class<R> resultClass,
                                                         R terminator, StreamOptions options) throws DataAccessException {
        return null;
    }

    @Override
    public final Stream<Map<String, Object>> multiStmtBatchQueryMapStream(MultiStmt stmt, int timeout,
                                                                          Supplier<Map<String, Object>> mapConstructor,
                                                                          Map<String, Object> terminator,
                                                                          StreamOptions options)
            throws DataAccessException {
        return null;
    }


    @Override
    public final MultiResult multiStmt(final MultiStmt stmt, final int timeout, final @Nullable StreamOptions options) {
        if (timeout < 0) {
            throw new IllegalArgumentException();
        }

        return null;
    }

    @Override
    public final MultiStream multiStmtStream(MultiStmt stmt, int timeout, @Nullable StreamOptions options) {
        return null;
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
     * @see #query(SimpleStmt, int, Class, Supplier)
     * @see #queryStream(SimpleStmt, int, Class, StreamOptions)
     */
    private <R> Function<ResultSetMetaData, RowReader<R>> createFuncForBean(
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
     * @see #queryAsMap(SimpleStmt, int, Supplier, Supplier)
     * @see #queryMapStream(SimpleStmt, int, Supplier, StreamOptions)
     */
    private Function<ResultSetMetaData, RowReader<Map<String, Object>>> createFuncForMap(
            List<? extends Selection> selectionList, Supplier<Map<String, Object>> mapConstructor) {
        return metaData -> {
            try {
                return new MapReader(this, selectionList, createSqlTypArray(metaData),
                        mapConstructor
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
     * @see #insert(SimpleStmt, int)
     */
    private void extractGenerateKeys(final Statement statement, final long insertedRows, final GeneratedKeyStmt stmt)
            throws SQLException {
        if (insertedRows != stmt.rowSize()) {
            throw valueInsertDomainWrapperSizeError(insertedRows, stmt.rowSize());
        }

        final String primaryKeyName = stmt.idReturnAlias();
        if (!_MetaBridge.ID.equals(primaryKeyName)) {
            String m = String.format("%s primaryKeyName error", GeneratedKeyStmt.class.getName());
            throw new IllegalArgumentException(m);
        }
        doExtractId(statement.getGeneratedKeys(), stmt);
    }


    /**
     * @see #query(SimpleStmt, int, Class, Supplier)
     * @see #queryAsMap(SimpleStmt, int, Supplier, Supplier)
     */
    private <R> List<R> doQuery(final SimpleStmt stmt, final int timeout, final Supplier<List<R>> listConstructor,
                                final Function<ResultSetMetaData, RowReader<R>> function) {
        if (timeout < 0) {
            throw new IllegalArgumentException();
        }

        final String sql = stmt.sqlText();
        final List<SQLParam> paramGroup = stmt.paramGroup();
        final int paramSize = paramGroup.size();

        try (Statement statement = this.createQueryStatement(sql, paramSize)) {

            if (statement instanceof PreparedStatement) {
                bindParameter((PreparedStatement) statement, paramGroup);
            }
            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }

            final ResultSet rs;
            if (statement instanceof PreparedStatement) {
                rs = ((PreparedStatement) statement).executeQuery();
            } else {
                rs = statement.executeQuery(sql);
            }
            return readList(rs, function.apply(rs.getMetaData()), stmt.hasOptimistic(), listConstructor);
        } catch (ArmyException e) {
            throw e;
        } catch (Exception e) {
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

            final Consumer<StreamCommander> consumer = options.commanderConsumer;
            // 7. create Commander
            if (consumer != null) {
                if (options.parallel) {
                    consumer.accept(spliterator::parallelCancel);
                } else {
                    consumer.accept(spliterator::simpleCancel);
                }
            }

            return StreamSupport.stream(spliterator, options.parallel);
        } catch (Throwable e) {
            throw handleError(e, resultSet, statement);
        }
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

        } else if (options == StreamOptions.LIST_LIKE) {
            statement = this.conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        } else {
            statement = this.conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);
            switch (this.factory.serverDataBase) {
                case MySQL: {
                    if (this instanceof MySQLExecutor) {
                        statement.setFetchSize(Integer.MIN_VALUE);
                    } else {
                        statement.setFetchSize(options.fetchSize);
                    }
                }
                break;
                case Postgre:
                case Oracle:
                case H2:
                    statement.setFetchSize(options.fetchSize);
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(this.factory.serverDataBase);
            }
        }

        return statement;

    }


    /**
     * @see #batchUpdate(BatchStmt, int, Supplier, TableMeta, List)
     */
    private List<Long> handleBatchResult(final boolean optimistic, final int bathSize,
                                         final IntToLongFunction accessor,
                                         final Supplier<List<Long>> listConstructor,
                                         final @Nullable TableMeta<?> domainTable,
                                         final @Nullable List<Long> rowsList) {
        assert rowsList == null || domainTable instanceof ChildTableMeta;

        List<Long> list;
        if (rowsList == null) {
            list = listConstructor.get();
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
     * @see #multiStmtBatchUpdate(MultiStmt, int, Supplier, TableMeta)
     */
    private void handleChildMultiStmtBatchUpdate(final Statement statement, final MultiStmt stmt,
                                                 final ChildTableMeta<?> domainTable,
                                                 final List<Long> list) throws SQLException {

        final JdbcExecutorFactory factory = this.factory;
        final boolean optimistic = stmt.hasOptimistic();

        final int itemSize, itemPairSize;
        itemSize = stmt.stmtItemList().size();
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
     * @see #multiStmtBatchUpdate(MultiStmt, int, Supplier, TableMeta)
     */
    private void handleSimpleMultiStmtBatchUpdate(final Statement statement, final MultiStmt stmt,
                                                  final @Nullable TableMeta<?> domainTable,
                                                  final List<Long> list) throws SQLException {

        assert domainTable == null || domainTable instanceof SingleTableMeta;

        final JdbcExecutorFactory factory = this.factory;
        final boolean optimistic = stmt.hasOptimistic();
        final int itemSize = stmt.stmtItemList().size();

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
    private static <R> List<R> readList(final ResultSet set, final RowReader<R> rowReader, final boolean optimistic,
                                        final Supplier<List<R>> listConstructor) {

        List<R> list = listConstructor.get();
        if (list == null) {
            throw _Exceptions.listConstructorError();
        }
        readRowToList(set, rowReader, optimistic, list);
        if (list instanceof ImmutableSpec) {
            list = _Collections.unmodifiableListForDeveloper(list);
        }
        return list;
    }

    private static <R> void readRowToList(final ResultSet set, final RowReader<R> rowReader,
                                          final boolean optimistic, final List<R> list) {
        try (ResultSet resultSet = set) {
            while (resultSet.next()) {
                list.add(rowReader.readOneRow(resultSet));
            }
            if (optimistic && list.size() == 0) {
                throw _Exceptions.optimisticLock(0);
            }

        } catch (SQLException e) { // other error is handled by invoker
            throw wrapError(e);
        }
    }


    /**
     * @see #insert(SimpleStmt, int)
     * @see #extractGenerateKeys(Statement, long, GeneratedKeyStmt)
     */
    private static int doExtractId(final ResultSet idResultSet, final GeneratedKeyStmt stmt) throws SQLException {
        final int rowSize = stmt.rowSize();
        int rowIndex = 0;
        try (ResultSet resultSet = idResultSet) {
            final PrimaryFieldMeta<?> idField = stmt.idField();
            final Class<?> idJavaType = idField.javaType();
            for (; resultSet.next(); rowIndex++) {
                if (idJavaType == Integer.class) {
                    stmt.setGeneratedIdValue(rowIndex, resultSet.getInt(1));
                } else if (idJavaType == Long.class) {
                    stmt.setGeneratedIdValue(rowIndex, resultSet.getLong(1));
                } else if (idJavaType == BigInteger.class) {
                    stmt.setGeneratedIdValue(rowIndex, resultSet.getObject(1, BigInteger.class));
                } else {
                    throw _Exceptions.autoIdErrorJavaType(idField);
                }
            }
            if (rowIndex != rowSize) {
                throw insertedRowsAndGenerateIdNotMatch(rowSize, rowIndex);
            }
            return rowIndex;
        } catch (IndexOutOfBoundsException e) {
            throw insertedRowsAndGenerateIdNotMatch(rowSize, rowIndex);
        }
    }

    /**
     * @see #batchQuery(BatchStmt, int, Class, Object, Supplier)
     * @see #batchQueryAsMap(BatchStmt, int, Supplier, Map, Supplier)
     */
    private void doBatchQuery(final BatchStmt stmt, final int timeout, final Consumer<ResultSet> consumer) {
        final List<List<SQLParam>> paramGroupList = stmt.groupList();
        final int groupSize;
        if (timeout < 0 || (groupSize = paramGroupList.size()) == 0) {
            throw new IllegalArgumentException();
        }

        try (PreparedStatement statement = this.conn.prepareStatement(stmt.sqlText())) {
            final long startTime;
            if (timeout > 0) {
                startTime = System.currentTimeMillis();
            } else {
                startTime = 0;
            }

            long restMills;
            ResultSet resultSet;
            for (int i = 0, restSeconds = 0; i < groupSize; i++) {
                if (i > 0) {
                    statement.clearParameters();
                }
                if (timeout > 0) {
                    restMills = (timeout * 1000L) - (System.currentTimeMillis() - startTime);
                    if (restMills < 1) {
                        throw _Exceptions.timeout(timeout, restMills);
                    }
                    restSeconds = (int) (restMills / 1000L);
                    if (restMills % 1000L != 0) {
                        restSeconds++;
                    }
                }

                bindParameter(statement, paramGroupList.get(i));

                if (restSeconds > 0) {
                    statement.setQueryTimeout(restSeconds);
                }

                resultSet = statement.executeQuery(); // execute sql

                consumer.accept(resultSet);  // resultSet is closed by invoker

            }// for

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

    /**
     * @see #batchQueryAsMap(BatchStmt, int, Supplier, Map, Supplier)
     */
    private MapReader createMapReader(final ResultSetMetaData metaData, final Supplier<Map<String, Object>> mapConstructor,
                                      final List<? extends Selection> selectionList) throws SQLException {
        return new MapReader(this, selectionList, this.createSqlTypArray(metaData), mapConstructor);
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


    private static ArmyException valueInsertDomainWrapperSizeError(long insertedRows, int domainWrapperSize) {
        String m = String.format("InsertedRows[%s] and domainWrapperSize[%s] not match.", insertedRows, domainWrapperSize);
        throw new ArmyException(m);
    }


    private static SQLException insertedRowsAndGenerateIdNotMatch(int insertedRows, int generateIdCount) {
        String m = String.format("insertedRows[%s] and generateKeys count[%s] not match.", insertedRows, generateIdCount);
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
            MappingType mappingType;
            Selection selection;
            Object columnValue;
            SqlType sqlType;
            String fieldName;
            boolean compatible;
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

                typeMeta = selection.typeMeta();
                if (compatibleTypeArray == null || (mappingType = compatibleTypeArray[i]) == null) {
                    if (typeMeta instanceof MappingType) {
                        mappingType = (MappingType) typeMeta;
                    } else {
                        mappingType = typeMeta.mappingType();
                    }
                    if (row == null) {
                        compatible = this.resultClass.isAssignableFrom(mappingType.javaType());
                    } else {
                        compatible = accessor.isWritable(fieldName, mappingType.javaType());
                    }
                    if (!compatible) {
                        mappingType = mappingType.compatibleFor(accessor.getJavaType(fieldName));
                        if (compatibleTypeArray == null) {
                            compatibleTypeArray = new MappingType[sqlTypeArray.length];
                            this.compatibleTypeArray = compatibleTypeArray;
                        }
                        compatibleTypeArray[i] = mappingType;
                    }

                }

                columnValue = mappingType.afterGet(sqlType, env, columnValue);
                //TODO field codec
                if (row == null) {
                    row = (R) columnValue;
                } else {
                    accessor.set(row, fieldName, columnValue);
                }
            }

            if (row instanceof Map && row instanceof ImmutableSpec) {
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

        private MapReader(JdbcExecutor executor, List<? extends Selection> selectionList, SqlType[] sqlTypeArray,
                          Supplier<Map<String, Object>> mapConstructor) {
            super(executor, selectionList, sqlTypeArray, _ClassUtils.mapJavaClass(), ObjectAccessorFactory.forMap());
            this.mapConstructor = mapConstructor;
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
            return _Collections.unmodifiableMapForDeveloper(map);
        }


    }//MapReader


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
        abstract ResultSet nextResultSet(Consumer<? super R> action);

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

        private final ResultSet resultSet;


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
        ResultSet nextResultSet(Consumer<? super R> action) {
            // always null, single result, no-op
            return null;
        }


    }//RowSpliterator


    private static final class BatchRowSpliterator<R> extends JdbcRowSpliterator<R> {

        private final PreparedStatement statement;

        private final List<List<SQLParam>> paramGroupList;

        private final long startTime;

        private final int timeout;

        private final int groupSize;

        private final R terminaer;

        private ResultSet resultSet;

        private int groupIndex = 0;

        private BatchRowSpliterator(PreparedStatement statement, int timeout, RowReader<R> rowReader, R terminaer, BatchStmt stmt,
                                    StreamOptions options) {
            super(rowReader, stmt.hasOptimistic(), options);

            this.statement = statement;
            this.paramGroupList = stmt.groupList();
            if (timeout > 0) {
                this.startTime = System.currentTimeMillis();
            } else {
                this.startTime = 0;
            }
            this.timeout = timeout;

            this.groupSize = this.paramGroupList.size();

            this.terminaer = terminaer;
            assert this.groupSize > 0;
            Objects.requireNonNull(terminaer);

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
        ResultSet nextResultSet(final Consumer<? super R> action) {
            final int currentIndex = this.groupIndex++;
            if (currentIndex > this.groupSize) {
                throw new ConcurrentModificationException();
            }
            action.accept(this.terminaer);
            if (currentIndex == this.groupSize) {
                return null;
            }
            final List<SQLParam> paramGroup;
            paramGroup = this.paramGroupList.get(currentIndex);
            final PreparedStatement statement = this.statement;

            return null;
        }


    }//BatchRowSpliterator


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
            return this.doNextQuery((selectionList, sqlTypeArray) -> {
                final RowReader<T> rowReader;
                if (sqlTypeArray.length == 1) {
                    rowReader = new SingleColumnRowReader<>(this.executor, selectionList, sqlTypeArray,
                            resultClass);
                } else {
                    rowReader = new BeanRowReader<>(this.executor, selectionList, resultClass, sqlTypeArray);
                }
                return rowReader;
            }, function);
        }

        final <R> R mapQuery(final Supplier<Map<String, Object>> mapConstructor,
                             final TeFunction<ResultSet, RowReader<Map<String, Object>>, Boolean, R> function) {
            return this.doNextQuery((selectionList, sqlTypeArray) ->
                            new MapReader(this.executor, selectionList, sqlTypeArray, mapConstructor),
                    function
            );
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
                    sqlTypeArray = this.executor.createSqlTypArray(metaData, metaData.getColumnCount());
                } else if (!(currentItem instanceof MultiStmt.ProcedureItem && this.procedureItem)) {
                    // no bug,never here, MultiStmt bug
                    throw _Exceptions.unknownStmtItem(currentItem);
                } else if (((MultiStmt.ProcedureItem) currentItem).resultItemList().isEmpty()) {
                    sqlTypeArray = this.executor.createSqlTypArray(metaData, metaData.getColumnCount());
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
                    sqlTypeArray = this.executor.createSqlTypArray(metaData, metaData.getColumnCount());
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
                    readList(s, rowReader, optimistic, listConstructor)
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
                    readList(s, mapReader, optimistic, listConstructor)
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
