package io.army.jdbc;

import io.army.ArmyException;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.bean.PairBean;
import io.army.codec.FieldCodec;
import io.army.codec.FieldCodecReturnException;
import io.army.criteria.SQLParam;
import io.army.criteria.Selection;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnv;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.session.ChildInsertException;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;
import io.army.stmt.*;
import io.army.sync.Commander;
import io.army.sync.StreamOptions;
import io.army.sync.executor.StmtExecutor;
import io.army.type.ImmutableSpec;
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
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

abstract class JdbcExecutor implements StmtExecutor {


    final JdbcExecutorFactory factory;

    private final Connection conn;

    Map<FieldMeta<?>, FieldCodec> fieldCodecMap;

    JdbcExecutor(JdbcExecutorFactory factory, Connection conn) {
        this.factory = factory;
        this.conn = conn;
    }

    @Override
    public final long insert(final Stmt stmt, final int timeout) {
        try {
            final long insertRows;
            if (stmt instanceof SimpleStmt) {
                insertRows = this.executeInsert((SimpleStmt) stmt, timeout);
            } else if (stmt instanceof PairStmt) {
                insertRows = this.executePariInsert((PairStmt) stmt, timeout);
            } else {
                throw _Exceptions.unexpectedStmt(stmt);
            }
            return insertRows;
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
        }
    }

    @Override
    public final <T> List<T> returnInsert(Stmt stmt, int txTimeout, Class<T> resultClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> returningUpdate(Stmt stmt, int txTimeout, Class<T> resultClass) throws DataAccessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Map<String, Object>> returnInsertAsMap(Stmt stmt, int txTimeout,
                                                       Supplier<Map<String, Object>> mapConstructor)
            throws DataAccessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Map<String, Object>> returningUpdateAsMap(Stmt stmt, int txTimeout,
                                                          Supplier<Map<String, Object>> mapConstructor)
            throws DataAccessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final long update(final SimpleStmt stmt, final int timeout) {
        try (PreparedStatement statement = this.conn.prepareStatement(stmt.sqlText())) {

            bindParameter(statement, stmt.paramGroup());
            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }
            final long affectedRows;
            if (this.factory.useLargeUpdate) {
                affectedRows = statement.executeLargeUpdate();
            } else {
                affectedRows = statement.executeUpdate();
            }
            return affectedRows;
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
        }
    }

    @Override
    public List<Long> batchUpdate(final BatchStmt stmt, final int timeout) throws DataAccessException {

        try (PreparedStatement statement = this.conn.prepareStatement(stmt.sqlText())) {
            final List<List<SQLParam>> paramGroupList = stmt.groupList();

            for (List<SQLParam> group : paramGroupList) {
                bindParameter(statement, group);
                statement.addBatch();
            }
            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }

            final int groupSize = paramGroupList.size();
            final List<Long> batchList = new ArrayList<>(groupSize);
            final boolean optimistic = stmt.hasOptimistic();
            if (this.factory.useLargeUpdate) {
                final long[] batchAffectedRows;
                batchAffectedRows = statement.executeLargeBatch();
                long rows;
                for (int i = 0; i < batchAffectedRows.length; i++) {
                    rows = batchAffectedRows[i];
                    if (optimistic && rows < 1) {
                        throw _Exceptions.batchOptimisticLock(i, rows);
                    }
                    batchList.add(rows);
                }
            } else {
                final int[] batchAffectedRows;
                batchAffectedRows = statement.executeBatch();
                for (int i = 0, rows; i < batchAffectedRows.length; i++) {
                    rows = batchAffectedRows[i];
                    if (optimistic && rows < 1) {
                        throw _Exceptions.batchOptimisticLock(i, rows);
                    }
                    batchList.add((long) rows);
                }
            }
            if (batchList.size() != groupSize) {
                throw _Exceptions.batchCountNotMatch(groupSize, batchList.size());
            }
            return Collections.unmodifiableList(batchList);
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> List<T> query(final SimpleStmt stmt, final int timeout, final Class<T> resultClass,
                                   final Supplier<List<T>> listConstructor) {

        final List<? extends Selection> selectionList = stmt.selectionList();
        try (PreparedStatement statement = this.conn.prepareStatement(stmt.sqlText())) {

            bindParameter(statement, stmt.paramGroup());
            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }
            final List<T> list = listConstructor.get();
            try (ResultSet resultSet = statement.executeQuery()) {
                final ObjectAccessor accessor;
                final Selection singleSelection;
                final Constructor<T> constructor;
                final int selectionSize = selectionList.size();
                if (selectionSize == 1) {
                    singleSelection = selectionList.get(0);
                    constructor = null;
                    accessor = null;
                } else if (selectionSize == 2 && PairBean.class.isAssignableFrom(resultClass)) {
                    singleSelection = null;
                    accessor = null;
                    constructor = ObjectAccessorFactory.getPairConstructor(resultClass);
                } else {
                    singleSelection = null;
                    constructor = ObjectAccessorFactory.getConstructor(resultClass);
                    accessor = ObjectAccessorFactory.forBean(resultClass);
                }
                T bean;
                for (Object columnValue; resultSet.next(); ) {
                    if (selectionSize == 1) {
                        columnValue = getColumnValue(resultSet, singleSelection);
                        if (columnValue != null && !resultClass.isInstance(columnValue)) {
                            throw _Exceptions.expectedTypeAndResultNotMatch(singleSelection, resultClass);
                        }
                        list.add((T) columnValue);
                    } else if (accessor == null) {
                        columnValue = getColumnValue(resultSet, selectionList.get(0));
                        bean = ObjectAccessorFactory.createPair(constructor, columnValue
                                , getColumnValue(resultSet, selectionList.get(1)));
                        list.add(bean);
                    } else {
                        bean = ObjectAccessorFactory.createBean(constructor);
                        for (Selection selection : selectionList) {
                            columnValue = getColumnValue(resultSet, selection);
                            accessor.set(bean, selection.alias(), columnValue);
                        }
                        list.add(bean);
                    }
                }
            }
            return list;
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
        }

    }


    @Override
    public final List<Map<String, Object>> queryAsMap(SimpleStmt stmt, int timeout
            , Supplier<Map<String, Object>> mapConstructor, Supplier<List<Map<String, Object>>> listConstructor)
            throws DataAccessException {

        final List<? extends Selection> selectionList = stmt.selectionList();
        try (PreparedStatement statement = this.conn.prepareStatement(stmt.sqlText())) {

            bindParameter(statement, stmt.paramGroup());
            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                final List<Map<String, Object>> list = listConstructor.get();
                Map<String, Object> rowMap;
                while (resultSet.next()) {
                    rowMap = mapConstructor.get();
                    for (Selection selection : selectionList) {
                        rowMap.put(selection.alias(), getColumnValue(resultSet, selection));
                    }
                    list.add(rowMap);
                }
                return list;
            }
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
        }

    }


    @Override
    public final <R> Stream<R> queryStream(SimpleStmt stmt, int timeout, Class<R> resultClass,
                                           final StreamOptions options, final @Nullable Consumer<Commander> consumer) {

        final List<SQLParam> paramGroup = stmt.paramGroup();

        final String sql = stmt.sqlText();
        final int paramSize;
        paramSize = paramGroup.size();

        Statement statement = null;
        ResultSet resultSet = null;
        try {

            statement = createStreamStmt(sql, paramSize, options);
            if (statement instanceof PreparedStatement) {
                bindParameter((PreparedStatement) statement, paramGroup);
            } else {
                assert paramSize == 0;
            }

            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }

            if (statement instanceof PreparedStatement) {
                resultSet = ((PreparedStatement) statement).executeQuery();
            } else {
                resultSet = statement.executeQuery(sql);
            }

            final RowSpliterator<R> spliterator;
            spliterator = new BeanRowSpliterator<>(this, statement, resultSet, stmt.selectionList(),
                    stmt.hasOptimistic(), resultClass, options
            );

            if (consumer != null) {
                final Commander commander;
                if (options.parallel) {
                    commander = new ParallelCommander(spliterator);
                } else {
                    commander = new SimpleCommander(spliterator);
                }
                consumer.accept(commander);
            }
            return StreamSupport.stream(spliterator, options.parallel);
        } catch (Throwable e) {
            throw handleError(e, resultSet, statement);
        }

    }


    @Override
    public Stream<Map<String, Object>> queryMapStream(SimpleStmt stmt, int timeout,
                                                      Supplier<Map<String, Object>> mapConstructor,
                                                      StreamOptions options, @Nullable Consumer<Commander> consumer) {
        return null;
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
        try {
            this.conn.close();
        } catch (SQLException e) {
            throw JdbcExceptions.wrap(e);
        }
    }




    /*################################## blow packet template ##################################*/

    abstract Logger getLogger();

    abstract void bind(PreparedStatement stmt, int index, SqlType sqlDataType, Object nonNull)
            throws SQLException;

    abstract SqlType getSqlType(ResultSetMetaData metaData, int indexBasedOne);

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


    private int getRestSeconds(final int timeout, final long startMills) {
        long currentMills = System.currentTimeMillis();
        long restMills = (timeout * 1000L - (currentMills - startMills));
        if (restMills <= 0) {
            throw _Exceptions.timeout(timeout, restMills);
        }
        int restSeconds = (int) (restMills / 1000L);
        if (restSeconds == 0) {
            restSeconds++;
        }
        return restSeconds;
    }


    /**
     * @see #insert(Stmt, int)
     */
    private long executePariInsert(final PairStmt stmt, final int timeout) throws SQLException {
        final long startTime = System.currentTimeMillis();

        final long insertRows;
        insertRows = this.executeInsert(stmt.firstStmt(), timeout);
        final int restSeconds;
        if (timeout > 0) {
            final long restMills = (timeout * 1000L) - (System.currentTimeMillis() - startTime);
            if (restMills < 1L) {
                String m = "Parent insert completion,but timeout,so no time insert child.";
                throw new ChildInsertException(m, _Exceptions.timeout(timeout, restMills));
            }
            if ((restMills % 1000L) == 0) {
                restSeconds = (int) (restMills / 1000L);
            } else {
                restSeconds = (int) (restMills / 1000L) + 1;
            }
        } else {
            restSeconds = 0;
        }
        try {
            final long childRows;
            childRows = this.executeInsert(stmt.secondStmt(), restSeconds);

            if (childRows != insertRows) {
                throw parentChildRowsNotMatch(insertRows, childRows);
            }
        } catch (Exception e) {
            throw new ChildInsertException("Parent insert completion,but child insert occur error.", e);
        }
        return insertRows;
    }

    private long executeInsert(final SimpleStmt stmt, final int timeoutSeconds) throws SQLException {
        final List<? extends Selection> selectionList = stmt.selectionList();
        final boolean returningId;
        returningId = selectionList.size() == 1 && selectionList.get(0) instanceof PrimaryFieldMeta;

        final int resultSetType;
        if (returningId || !(stmt instanceof GeneratedKeyStmt)) {
            resultSetType = Statement.NO_GENERATED_KEYS;
        } else {
            resultSetType = Statement.RETURN_GENERATED_KEYS;
        }
        try (PreparedStatement statement = this.conn.prepareStatement(stmt.sqlText(), resultSetType)) {

            bindParameter(statement, stmt.paramGroup());

            if (timeoutSeconds > 0) {
                statement.setQueryTimeout(timeoutSeconds);
            }
            final long rows;
            if (returningId) {
                rows = doExtractId(statement.executeQuery(), (GeneratedKeyStmt) stmt);
            } else {
                if (this.factory.useLargeUpdate) {
                    rows = statement.executeLargeUpdate();
                } else {
                    rows = statement.executeUpdate();
                }
                if (resultSetType == Statement.RETURN_GENERATED_KEYS) {
                    extractGenerateKeys(statement, rows, (GeneratedKeyStmt) stmt);
                }
            }
            if (rows < 1) {
                throw new SQLException(String.format("insert statement affected %s rows", rows));
            }
            return rows;
        }

    }


    /**
     * @see #executeInsert(SimpleStmt, int)
     */
    private void bindParameter(final PreparedStatement statement, final List<SQLParam> paramGroup)
            throws SQLException {
        final int size = paramGroup.size();
        final ServerMeta serverMeta = this.factory.serverMeta;
        final MappingEnv mappingEnv = this.factory.mappingEnv;

        SQLParam sqlParam;
        Object value;
        MappingType mappingType;
        TypeMeta paramMeta;
        SqlType sqlType;
        for (int i = 0; i < size; i++) {
            sqlParam = paramGroup.get(i);

            paramMeta = sqlParam.typeMeta();
            if (paramMeta instanceof MappingType) {
                mappingType = (MappingType) paramMeta;
            } else {
                mappingType = paramMeta.mappingType();
            }
            sqlType = mappingType.map(serverMeta);

            if (sqlParam instanceof SingleParam) {
                value = ((SingleParam) sqlParam).value();
                if (value == null) {
                    // bind null
                    statement.setNull(i + 1, Types.NULL);
                } else {
                    value = mappingType.beforeBind(sqlType, mappingEnv, value);
                    bind(statement, i + 1, sqlType, value);
                }
            } else if (sqlParam instanceof MultiParam) {
                for (Object element : ((MultiParam) sqlParam).valueList()) {
                    if (element == null) {
                        // bind null
                        statement.setNull(i + 1, Types.NULL);
                    } else {
                        value = mappingType.beforeBind(sqlType, mappingEnv, element);
                        bind(statement, i + 1, sqlType, value);
                    }
                }
            } else {
                throw _Exceptions.unexpectedSqlParam(sqlParam);
            }


        }

    }

    @Deprecated
    @Nullable
    private Object getColumnValue(final ResultSet resultSet, final Selection selection)
            throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Nullable
    private Object getColumnValue(final ResultSet resultSet, final int indexBasedOne, final SqlType sqlType,
                                  final MappingType type) throws SQLException {

        Object value;
        value = this.get(resultSet, indexBasedOne, sqlType);
        if (value == null) {
            return null;
        }
        value = type.afterGet(sqlType, this.factory.mappingEnv, value);
        //TODO codec
        return value;

    }


    private Object encodeField(final FieldMeta<?> fieldMeta, Object nonNull) {
        final FieldCodec fieldCodec;
        fieldCodec = this.fieldCodecMap.get(fieldMeta);
        if (fieldCodec == null) {
            throw createNoFieldCodecException(fieldMeta);
        }
        nonNull = fieldCodec.encode(fieldMeta, nonNull);
        if (nonNull == null || nonNull.getClass() != fieldMeta.javaType()) {
            throw createCodecReturnTypeException(fieldCodec, fieldMeta);
        }
        return nonNull;
    }


    /**
     * @see #executeInsert(SimpleStmt, int)
     */
    private void extractGenerateKeys(PreparedStatement statement, final long insertedRows, final GeneratedKeyStmt stmt)
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
     * @see #queryStream(SimpleStmt, int, Class, StreamOptions, Consumer)
     * @see #queryMapStream(SimpleStmt, int, Supplier, StreamOptions, Consumer)
     */
    private Statement createStreamStmt(final String sql, final int paramSize, final StreamOptions options)
            throws SQLException {
        final Statement statement;
        if (paramSize > 0) {
            statement = this.conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);
        } else {
            statement = this.conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                    ResultSet.CLOSE_CURSORS_AT_COMMIT);
        }
        if (options.serverStream) {
            statement.setFetchSize(options.fetchSize);
        } else switch (this.factory.serverDataBase) {
            case MySQL:
                statement.setFetchSize(Integer.MIN_VALUE);
                break;
            case Postgre:
            case Oracle:
            case H2:
                statement.setFetchSize(options.fetchSize);
                break;
            default:
                throw _Exceptions.unexpectedEnum(this.factory.serverDataBase);
        }
        return statement;

    }







    /*################################## blow static method ##################################*/

    static IllegalArgumentException beforeBindReturnError(SqlType sqlType, Object nonNull) {
        String m = String.format("%s beforeBind method return error type[%s] for %s.%s."
                , MappingType.class.getName(), nonNull.getClass().getName(), sqlType.database(), sqlType);
        return new IllegalArgumentException(m);
    }


    /**
     * @see #executeInsert(SimpleStmt, int)
     * @see #extractGenerateKeys(PreparedStatement, long, GeneratedKeyStmt)
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

    private static int getSingleTimeout(final long restMillis) {
        int timeout;
        if (restMillis < 1L) {
            timeout = 0;
        } else {
            timeout = (int) (restMillis / 1000L);
            if (restMillis % 1000L != 0) {
                timeout++;
            }
        }
        return timeout;
    }


    private static ArmyException handleError(final Throwable error, final @Nullable ResultSet resultSet,
                                             final @Nullable Statement statement)
            throws ArmyException {

        closeResultSetAndStatement(resultSet, statement);

        final ArmyException e;
        if (error instanceof ArmyException) {
            e = (ArmyException) error;
        } else if (error instanceof SQLException) {
            e = JdbcExceptions.wrap((SQLException) error);
        } else {
            e = _Exceptions.unknownError(error.getMessage(), error);
        }
        return e;

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

    private static ArmyException convertBeforeBindMethodError(MappingType mappingType) {
        String m = String.format("%s.convertBeforeBind(Object nonNull) return error.", mappingType);
        throw new ArmyException(m);
    }

    private static FieldCodecReturnException createCodecReturnTypeException(FieldCodec fieldCodec
            , FieldMeta<?> fieldMeta) {
        return new FieldCodecReturnException("FieldCodec[%s] return  error,FieldMeta[%s],"
                , fieldCodec, fieldMeta);
    }

    private static MetaException createNoFieldCodecException(FieldMeta<?> fieldMeta) {
        String m = String.format("FieldMeta[%s] not found FieldCodec.", fieldMeta);
        return new MetaException(m);
    }

    private static ArmyException valueInsertDomainWrapperSizeError(long insertedRows, int domainWrapperSize) {
        String m = String.format("InsertedRows[%s] and domainWrapperSize[%s] not match.", insertedRows, domainWrapperSize);
        throw new ArmyException(m);
    }

    private ArmyException parentChildRowsNotMatch(long parentRows, long childRows) {
        String m = String.format("Parent insert/update rows[%s] and child insert/update rows[%s] not match."
                , parentRows, childRows);
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

        private final ObjectAccessor accessor;

        private MappingType[] compatibleTypeArray;


        private RowReader(JdbcExecutor executor, List<? extends Selection> selectionList,
                          SqlType[] sqlTypeArray, ObjectAccessor accessor) {
            this.executor = executor;
            this.selectionList = selectionList;
            this.sqlTypeArray = sqlTypeArray;
            this.accessor = accessor;
        }

        final R readOneRow(final ResultSet resultSet) throws SQLException {
            final JdbcExecutor executor = this.executor;
            final MappingEnv env = executor.factory.mappingEnv;
            final SqlType[] sqlTypeArray = this.sqlTypeArray;
            final ObjectAccessor accessor = this.accessor;
            final List<? extends Selection> selectionList = this.selectionList;

            MappingType[] compatibleTypeArray = this.compatibleTypeArray;

            final R row;
            row = this.createRow();

            TypeMeta typeMeta;
            MappingType mappingType;
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
                    accessor.set(row, fieldName, null);
                    continue;
                }

                typeMeta = selection.typeMeta();
                if (compatibleTypeArray == null || (mappingType = compatibleTypeArray[i]) == null) {
                    if (typeMeta instanceof MappingType) {
                        mappingType = (MappingType) typeMeta;
                    } else {
                        mappingType = typeMeta.mappingType();
                    }
                    if (!accessor.isWritable(fieldName, mappingType.javaType())) {
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
                accessor.set(row, fieldName, columnValue);

            }

            final R result;
            if (row instanceof Map && row instanceof ImmutableSpec) {
                result = this.unmodifiedMap(row);
            } else {
                result = row;
            }
            return result;
        }

        abstract R createRow();

        R unmodifiedMap(R map) {
            throw new UnsupportedOperationException();
        }


    }//RowReader

    private static final class BeanRowReader<R> extends RowReader<R> {


        final Class<R> resultClass;
        private final Constructor<R> constructor;


        private BeanRowReader(JdbcExecutor executor, List<? extends Selection> selectionList, Class<R> resultClass,
                              SqlType[] sqlTypeArray) {
            super(executor, selectionList, sqlTypeArray, ObjectAccessorFactory.forBean(resultClass));
            this.resultClass = resultClass;
            this.constructor = ObjectAccessorFactory.getConstructor(resultClass);
        }

        @Override
        R createRow() {
            return ObjectAccessorFactory.createBean(this.constructor);
        }


    }//BeanRowReader


    private static final class MapReader extends RowReader<Map<String, Object>> {

        private final Supplier<Map<String, Object>> mapConstructor;

        private MapReader(JdbcExecutor executor, List<? extends Selection> selectionList, SqlType[] sqlTypeArray,
                          Supplier<Map<String, Object>> mapConstructor) {
            super(executor, selectionList, sqlTypeArray, ObjectAccessorFactory.forMap());
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
            Map<String, Object> result = null;
            if (map.size() == 1) {
                for (Map.Entry<String, Object> e : map.entrySet()) {
                    result = Collections.singletonMap(e.getKey(), e.getValue()); // here don't use _Collections
                    break;
                }
            } else {
                result = Collections.unmodifiableMap(map); // here don't use _Collections
            }
            return result;
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
            final ResultSet resultSet = this.resultSet;
            final RowReader<R> rowReader = this.rowReader;

            boolean hasMore = !this.closed;

            final int fetchSize = hasMore ? this.fetchSize : 0;
            try {
                for (int i = 0; i < fetchSize; i++) {
                    if (!resultSet.next()) {
                        hasMore = false;
                        break;
                    }
                    action.accept(rowReader.readOneRow(resultSet));
                }

                if (!hasMore) {
                    this.closeStream();
                }
                return hasMore;
            } catch (Throwable e) {
                throw handleError(e, resultSet, this.statement);
            }
        }

        @Nullable
        @Override
        public Spliterator<R> trySplit() {
            final ResultSet resultSet = this.resultSet;
            final RowReader<R> rowReader = this.rowReader;

            boolean hasMore = !this.closed;

            final int splitSize = hasMore ? this.splitSize : 0;
            List<R> rowList;
            if (splitSize == 0) {
                rowList = null;
            } else {
                rowList = _Collections.arrayList(splitSize);
            }
            try {
                for (int i = 0; i < splitSize; i++) {
                    if (!resultSet.next()) {
                        hasMore = false;
                        break;
                    }
                    rowList.add(rowReader.readOneRow(resultSet));
                }

                if (!hasMore) {
                    this.closeStream();
                }

                final Spliterator<R> spliterator;
                if (rowList == null || rowList.size() == 0) {
                    spliterator = null;
                } else {
                    spliterator = new RowListSpliterator<>(rowList);
                }
                return spliterator;
            } catch (Throwable e) {
                throw handleError(e, resultSet, this.statement);
            }
        }

        @Override
        public long estimateSize() {
            return 0;
        }

        @Override
        public int characteristics() {
            return 0;
        }

        private void closeStream() {
            if (!this.closed) {
                this.closed = true;
                closeResultSetAndStatement(this.resultSet, this.statement);
            }
        }


    }//RowSpliterator


    private static final class RowListSpliterator<R> implements Spliterator<R> {

        private final List<R> rowList;

        private boolean end;

        private RowListSpliterator(List<R> rowList) {
            this.rowList = rowList;
        }

        @Override
        public boolean tryAdvance(Consumer<? super R> action) {
            if (this.end) {
                return false;
            }
            this.rowList.forEach(action);
            this.end = true;
            return true;
        }

        @Nullable
        @Override
        public Spliterator<R> trySplit() {
            if (this.end) {
                return null;
            }
            final Spliterator<R> spliterator;
            spliterator = this.rowList.spliterator();
            this.end = true;
            return spliterator;
        }

        @Override
        public long estimateSize() {
            return 0;
        }

        @Override
        public int characteristics() {
            return 0;
        }


    }//RowListSpliterator


    private static final class SimpleCommander implements Commander {

        private final RowSpliterator<?> spliterator;

        private SimpleCommander(RowSpliterator<?> spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public void cancel() {

        }


    }//SimpleCommander

    private static final class ParallelCommander implements Commander {

        private final RowSpliterator<?> spliterator;


        private ParallelCommander(RowSpliterator<?> spliterator) {
            this.spliterator = spliterator;
        }

        @Override
        public void cancel() {

        }


    }//ParallelCommander


}
