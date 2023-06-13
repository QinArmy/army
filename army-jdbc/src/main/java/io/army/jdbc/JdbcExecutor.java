package io.army.jdbc;

import io.army.ArmyException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.codec.FieldCodec;
import io.army.criteria.SQLParam;
import io.army.criteria.Selection;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;
import io.army.stmt.*;
import io.army.sync.StreamCommander;
import io.army.sync.StreamOptions;
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
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntToLongFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

abstract class JdbcExecutor implements StmtExecutor {


    final JdbcExecutorFactory factory;

    final Connection conn;

    Map<FieldMeta<?>, FieldCodec> fieldCodecMap;

    private Map<RowSpliterator<?>, Boolean> rowSpliteratorMap;

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
            throw JdbcExceptions.wrap(e);
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
            throw JdbcExceptions.wrap(e);
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
            throw JdbcExceptions.wrap(e);
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
            throw JdbcExceptions.wrap(e);
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
    public final <R> Stream<R> queryStream(SimpleStmt stmt, int timeout, Class<R> resultClass,
                                           final StreamOptions options) {
        return this.queryAsStream(stmt, timeout, options, this.createFuncForBean(stmt.selectionList(), resultClass));
    }


    @Override
    public Stream<Map<String, Object>> queryMapStream(SimpleStmt stmt, int timeout,
                                                      final Supplier<Map<String, Object>> mapConstructor,
                                                      StreamOptions options) {
        return this.queryAsStream(stmt, timeout, options, this.createFuncForMap(stmt.selectionList(), mapConstructor));
    }


    @Override
    public final Object createSavepoint() throws DataAccessException {
        try {
            return this.conn.setSavepoint();
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
        }
    }

    @Override
    public final void rollbackToSavepoint(Object savepoint) throws DataAccessException {
        try {
            this.conn.releaseSavepoint((Savepoint) savepoint);
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
        }
    }

    @Override
    public final void releaseSavepoint(Object savepoint) throws DataAccessException {
        try {
            this.conn.releaseSavepoint((Savepoint) savepoint);
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
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
            throw JdbcExceptions.wrap(e);
        }
    }

    @Override
    public final void execute(String stmt) throws DataAccessException {
        try (Statement statement = this.conn.createStatement()) {
            statement.executeUpdate(stmt);
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
        }
    }

    @Override
    public final void close() throws DataAccessException {

        final Map<RowSpliterator<?>, Boolean> rowSpliteratorMap = this.rowSpliteratorMap;

        Throwable error = null;
        if (rowSpliteratorMap != null) {
            error = closeRowSpliterator(rowSpliteratorMap);
        }

        try {
            this.conn.close();

            if (error != null) {
                throw JdbcExceptions.wrap(error);
            }
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
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


    private SqlType[] createSqlTypArray(final ResultSetMetaData metaData, final int selectionSize) throws SQLException {
        final SqlType[] sqlTypeArray = new SqlType[selectionSize];
        for (int i = 0; i < selectionSize; i++) {
            sqlTypeArray[i] = this.getSqlType(metaData, i + 1);
        }
        return sqlTypeArray;
    }

    private void addRowSpliterator(final RowSpliterator<?> spliterator) {
        Map<RowSpliterator<?>, Boolean> rowSpliteratorMap = this.rowSpliteratorMap;
        if (rowSpliteratorMap == null) {
            rowSpliteratorMap = _Collections.hashMap();
            this.rowSpliteratorMap = rowSpliteratorMap;
        }
        rowSpliteratorMap.put(spliterator, Boolean.TRUE);
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
     * @see #doQuery(SimpleStmt, int, Supplier, FunctionWithError)
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
    private <R> FunctionWithError<ResultSetMetaData, RowReader<R>> createFuncForBean(
            final List<? extends Selection> selectionList, final Class<R> resultClass) {

        return metaData -> {
            final int selectionSize;
            selectionSize = selectionList.size();

            final RowReader<R> rowReader;
            if (selectionSize == 1) {
                rowReader = new SingleColumnRowReader<>(this, selectionList,
                        createSqlTypArray(metaData, selectionSize), resultClass);
            } else {
                rowReader = new BeanRowReader<>(this, selectionList, resultClass,
                        createSqlTypArray(metaData, selectionSize));
            }
            return rowReader;

        };
    }

    /**
     * @see #queryAsMap(SimpleStmt, int, Supplier, Supplier)
     * @see #queryMapStream(SimpleStmt, int, Supplier, StreamOptions)
     */
    private FunctionWithError<ResultSetMetaData, RowReader<Map<String, Object>>> createFuncForMap(
            List<? extends Selection> selectionList, Supplier<Map<String, Object>> mapConstructor) {
        return metaData -> new MapReader(this, selectionList, createSqlTypArray(metaData, selectionList.size()),
                mapConstructor
        );
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
                                final FunctionWithError<ResultSetMetaData, RowReader<R>> function) {
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
            try (ResultSet resultSet = rs) {
                List<R> list = listConstructor.get();
                if (list == null) {
                    throw new NullPointerException("listConstructor return null");
                }
                final RowReader<R> rowReader;
                rowReader = function.apply(resultSet.getMetaData());
                while (resultSet.next()) {
                    list.add(rowReader.readOneRow(resultSet));
                }

                if (list instanceof ImmutableSpec) {
                    list = _Collections.unmodifiableListForDeveloper(list);
                }
                return list;
            }
        } catch (ArmyException e) {
            throw e;
        } catch (Exception e) {
            throw JdbcExceptions.wrap(e);
        }

    }


    /**
     * @see #queryStream(SimpleStmt, int, Class, StreamOptions)
     * @see #queryMapStream(SimpleStmt, int, Supplier, StreamOptions)
     */
    private <R> Stream<R> queryAsStream(final SimpleStmt stmt, final int timeout, final StreamOptions options,
                                        final FunctionWithError<ResultSetMetaData, RowReader<R>> function) {
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

            // 5. create RowSpliterator
            final RowSpliterator<R> spliterator;
            spliterator = new RowSpliterator<>(statement, resultSet, function.apply(resultSet.getMetaData()),
                    stmt.hasOptimistic(), options
            );

            final Consumer<StreamCommander> consumer = options.commanderConsumer;
            // 6. create Commander
            if (consumer != null) {
                final StreamCommander commander;
                if (options.parallel) {
                    commander = new ParallelCommander(spliterator);
                } else {
                    commander = new SimpleCommander(spliterator);
                }
                consumer.accept(commander);
            }

            // 7. store RowSpliterator
            this.addRowSpliterator(spliterator);

            return StreamSupport.stream(spliterator, options.parallel);
        } catch (Throwable e) {
            throw handleError(e, resultSet, statement);
        }
    }


    /**
     * @see #queryAsStream(SimpleStmt, int, StreamOptions, FunctionWithError)
     */
    private Statement createStreamStmt(final String sql, final int paramSize, final StreamOptions options)
            throws SQLException {
        final Statement statement;
        if (paramSize > 0 || options.serverStream) {
            statement = this.conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);

            statement.setFetchSize(options.fetchSize);
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
     * @see #close()
     */
    @Nullable
    private Throwable closeRowSpliterator(final Map<RowSpliterator<?>, Boolean> rowSpliteratorMap) {
        Throwable error = null;

        for (RowSpliterator<?> spliterator : rowSpliteratorMap.keySet()) {
            if (spliterator.closed) {
                continue;
            }
            try {
                spliterator.closeStream();
            } catch (Throwable e) {
                error = e;
            }
        }
        rowSpliteratorMap.clear();
        this.rowSpliteratorMap = null;

        return error;
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
        itemSize = stmt.resultItemList().size();
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
        final int itemSize = stmt.resultItemList().size();

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


    private static ArmyException handleError(final Throwable error, final @Nullable ResultSet resultSet,
                                             final @Nullable Statement statement)
            throws ArmyException {

        closeResultSetAndStatement(resultSet, statement);

        return JdbcExceptions.wrap(error);
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
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
        } catch (Exception e) {
            throw _Exceptions.unknownError(e.getMessage(), e);
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
            this.executor = executor;
            this.selectionList = selectionList;
            this.sqlTypeArray = sqlTypeArray;
            this.resultClass = resultClass;
            this.accessor = accessor;
        }

        @SuppressWarnings("unchecked")
        @Nullable
        private R readOneRow(final ResultSet resultSet) throws SQLException {
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
                throw new NullPointerException("mapConstructor return null");
            }
            return map;
        }


        @Override
        Map<String, Object> unmodifiedMap(final Map<String, Object> map) {
            return _Collections.unmodifiableMapForDeveloper(map);
        }


    }//MapReader


    private static final class RowSpliterator<R> implements Spliterator<R> {

        private final Statement statement;

        private final ResultSet resultSet;

        private final RowReader<R> rowReader;

        private final boolean hasOptimistic;

        private final int fetchSize;

        private final int splitSize;

        private boolean closed;

        private int rowCount = 0;

        private boolean canceled;

        private RowSpliterator(Statement statement, ResultSet resultSet, RowReader<R> rowReader, boolean hasOptimistic,
                               StreamOptions options) {
            this.statement = statement;
            this.resultSet = resultSet;
            this.rowReader = rowReader;
            this.hasOptimistic = hasOptimistic;

            this.fetchSize = options.fetchSize;
            this.splitSize = options.splitSize;

            assert this.fetchSize > 0 && this.splitSize > 0;

        }

        @Override
        public boolean tryAdvance(final Consumer<? super R> action) {
            return this.doTryAdvance(this.fetchSize, action);
        }

        @Nullable
        @Override
        public Spliterator<R> trySplit() {
            if (this.closed || this.canceled) {
                return null;
            }

            final List<R> rowList = _Collections.arrayList(Math.min(300, this.splitSize));
            this.doTryAdvance(this.splitSize, rowList::add);
            final Spliterator<R> spliterator;
            if (rowList.size() == 0) {
                spliterator = null;
            } else {
                spliterator = rowList.spliterator();
            }
            return spliterator;
        }


        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            int bitSet;
            bitSet = this.fetchSize == 1 ? Spliterator.ORDERED : 0;
            if (this.rowReader.accessor != ObjectAccessorFactory.PSEUDO_ACCESSOR) {
                bitSet |= Spliterator.NONNULL;
            }
            return bitSet;
        }


        private boolean doTryAdvance(final int expectedFetchSize, final @Nullable Consumer<? super R> action) {
            try {
                if (action == null) {
                    throw new NullPointerException();
                }

                final ResultSet resultSet = this.resultSet;
                final RowReader<R> rowReader = this.rowReader;

                boolean hasMore = !this.closed && !this.canceled;

                final int actualFetchSize = hasMore ? expectedFetchSize : 0;
                final int oldRowCount = this.rowCount;
                int readRowCount = 0;
                for (int i = 0; i < actualFetchSize; i++) {
                    if (!resultSet.next()) {
                        hasMore = false;
                        break;
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
                throw JdbcExceptions.wrap(e);
            }

        }

        private void closeStream() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            final Map<RowSpliterator<?>, Boolean> mpa = this.rowReader.executor.rowSpliteratorMap;
            if (mpa != null) {
                mpa.remove(this);
            }
            closeResultSetAndStatement(this.resultSet, this.statement);

        }


    }//RowSpliterator


    private static final class SimpleCommander implements StreamCommander {

        private final RowSpliterator<?> spliterator;

        private SimpleCommander(RowSpliterator<?> spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public void cancel() {
            this.spliterator.canceled = true;
        }


    }//SimpleCommander

    private static final class ParallelCommander implements StreamCommander {

        private final RowSpliterator<?> spliterator;


        private ParallelCommander(RowSpliterator<?> spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public void cancel() {
            if (this.spliterator.canceled) {
                return;
            }
            synchronized (this.spliterator) {
                this.spliterator.canceled = true;
            }
        }


    }//ParallelCommander

    @FunctionalInterface
    private interface FunctionWithError<T, R> {

        R apply(T t) throws Exception;

    }


}
