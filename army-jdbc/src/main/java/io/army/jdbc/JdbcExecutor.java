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
import io.army.sync.executor.StmtExecutor;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

abstract class JdbcExecutor implements StmtExecutor {


    final JdbcExecutorFactory factory;

    final Connection conn;

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
    public final long update(final SimpleStmt stmt, final int timeout) {
        try (PreparedStatement statement = this.conn.prepareStatement(stmt.sql())) {

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

        try (PreparedStatement statement = this.conn.prepareStatement(stmt.sql())) {
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
    public final <T> List<T> select(final SimpleStmt stmt, final int timeout, final Class<T> resultClass
            , final Supplier<List<T>> listConstructor) {

        final List<Selection> selectionList = stmt.selectionList();
        try (PreparedStatement statement = this.conn.prepareStatement(stmt.sql())) {

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
    public final List<Map<String, Object>> selectAsMap(SimpleStmt stmt, int timeout
            , Supplier<Map<String, Object>> mapConstructor, Supplier<List<Map<String, Object>>> listConstructor)
            throws DataAccessException {

        final List<Selection> selectionList = stmt.selectionList();
        try (PreparedStatement statement = this.conn.prepareStatement(stmt.sql())) {

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

    @Nullable
    abstract Object get(ResultSet resultSet, String alias, SqlType sqlType) throws SQLException;


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
        final List<Selection> selectionList = stmt.selectionList();
        final boolean returningId;
        returningId = selectionList.size() == 1 && selectionList.get(0) instanceof PrimaryFieldMeta;

        final int resultSetType;
        if (returningId || !(stmt instanceof GeneratedKeyStmt)) {
            resultSetType = Statement.NO_GENERATED_KEYS;
        } else {
            resultSetType = Statement.RETURN_GENERATED_KEYS;
        }
        try (PreparedStatement statement = this.conn.prepareStatement(stmt.sql(), resultSetType)) {

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

    @Nullable
    private Object getColumnValue(final ResultSet resultSet, final Selection selection)
            throws SQLException {
        final TypeMeta paramMeta = selection.typeMeta();
        final MappingType mappingType;
        if (paramMeta instanceof MappingType) {
            mappingType = (MappingType) paramMeta;
        } else {
            mappingType = paramMeta.mappingType();
        }
        final SqlType sqlType;
        sqlType = mappingType.map(this.factory.serverMeta);
        Object value;
        value = get(resultSet, selection.alias(), sqlType);
        if (value != null) {
            value = mappingType.afterGet(sqlType, this.factory.mappingEnv, value);
        }
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


}
