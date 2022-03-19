package io.army.jdbc;

import io.army.ArmyException;
import io.army.ArmyKeys;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.codec.FieldCodec;
import io.army.codec.FieldCodecReturnException;
import io.army.criteria.Selection;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.mapping.MappingEnvironment;
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
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

abstract class JdbcStmtExecutor implements StmtExecutor {

    static final String SQL_LOG_FORMAT = "army-jdbc will execute sql:\n{}";

    final JdbcExecutorFactory factory;

    final Connection conn;

    Map<FieldMeta<?>, FieldCodec> fieldCodecMap;

    JdbcStmtExecutor(JdbcExecutorFactory factory, Connection conn) {
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
    public final int update(Stmt stmt, int txTimeout) {
        return 0;
    }


    @SuppressWarnings("unchecked")
    @Override
    public final <T> List<T> select(final SimpleStmt stmt, final int timeout, final Class<T> resultClass
            , final Supplier<List<T>> listConstructor) {
        final JdbcExecutorFactory factory = this.factory;
        final String sql = stmt.sql();

        if ((factory.sqlLogDynamic && factory.env.get(ArmyKeys.SQL_LOG_SHOW, Boolean.class, Boolean.FALSE))
                || factory.sqlLogShow) {
            getLogger().info(SQL_LOG_FORMAT, sql);
        }
        final List<Selection> selectionList = stmt.selectionList();
        try (PreparedStatement statement = this.conn.prepareStatement(sql)) {

            bindParameter(statement, stmt.paramGroup());
            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }
            final List<T> list = listConstructor.get();
            try (ResultSet resultSet = statement.executeQuery()) {
                final ObjectAccessor accessor;
                final Selection singleSelection;
                if (selectionList.size() == 1) {
                    accessor = null;
                    singleSelection = selectionList.get(0);
                } else {
                    singleSelection = null;
                    accessor = ObjectAccessorFactory.forBean(resultClass);
                }
                for (Object bean, columnValue; resultSet.next(); ) {
                    if (accessor == null) {
                        columnValue = getColumnValue(resultSet, singleSelection);
                        if (columnValue != null && !resultClass.isInstance(columnValue)) {
                            throw _Exceptions.expectedTypeAndResultNotMatch(singleSelection, resultClass);
                        }
                        list.add((T) columnValue);
                        continue;
                    }
                    bean = ObjectAccessorFactory.createBean(resultClass);
                    for (Selection selection : selectionList) {
                        columnValue = getColumnValue(resultSet, selection);
                        accessor.set(bean, selection.alias(), columnValue);
                    }
                    list.add((T) bean);
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
            for (String sql : stmtList) {
                statement.addBatch(sql);
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
        insertRows = this.executeInsert(stmt.parentStmt(), timeout);

        final long restMills = (timeout * 1000L) - (System.currentTimeMillis() - startTime);
        if (restMills < 1L) {
            String m = "Parent insert completion,but timeout,so no time insert child.";
            throw new ChildInsertException(m, _Exceptions.timeout(timeout, restMills));
        }
        final int restSeconds;
        if ((restMills % 1000L) == 0) {
            restSeconds = (int) (restMills / 1000L);
        } else {
            restSeconds = (int) (restMills / 1000L) + 1;
        }

        try {
            final long childRows;
            childRows = this.executeInsert(stmt.childStmt(), restSeconds);

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
        final JdbcExecutorFactory factory = this.factory;
        final String sql = stmt.sql();

        if ((factory.sqlLogDynamic && factory.env.get(ArmyKeys.SQL_LOG_SHOW, Boolean.class, Boolean.FALSE))
                || factory.sqlLogShow) {
            getLogger().info(SQL_LOG_FORMAT, sql);
        }
        try (PreparedStatement statement = this.conn.prepareStatement(sql, resultSetType)) {

            bindParameter(statement, stmt.paramGroup());

            if (timeoutSeconds > 0) {
                statement.setQueryTimeout(timeoutSeconds);
            }
            final long rows;
            if (returningId) {
                rows = doExtractId(statement.executeQuery(), (GeneratedKeyStmt) stmt);
            } else {
                if (factory.useLargeUpdate) {
                    rows = statement.executeLargeUpdate();
                } else {
                    rows = statement.executeUpdate();
                }
                if (resultSetType == Statement.RETURN_GENERATED_KEYS) {
                    getGenerateKeys(statement, rows, (GeneratedKeyStmt) stmt);
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
    private void bindParameter(final PreparedStatement statement, final List<ParamValue> paramGroup)
            throws SQLException {
        final int size = paramGroup.size();
        final ServerMeta serverMeta = this.factory.serverMeta;
        final MappingEnvironment mapEnv = this.factory.mapEnv;

        ParamValue paramValue;
        Object value;
        MappingType mappingType;
        ParamMeta paramMeta;
        SqlType sqlType;
        for (int i = 0; i < size; i++) {
            paramValue = paramGroup.get(i);
            value = paramValue.value();
            if (value == null) {
                // bind null
                statement.setNull(i + 1, Types.NULL);
                continue;
            }
            paramMeta = paramValue.paramMeta();
            if (paramMeta instanceof MappingType) {
                mappingType = (MappingType) paramMeta;
            } else {
                mappingType = paramMeta.mappingType();
            }
            sqlType = mappingType.map(serverMeta);
            value = mappingType.beforeBind(sqlType, mapEnv, value);
            bind(statement, i + 1, sqlType, value);

        }

    }

    @Nullable
    private Object getColumnValue(final ResultSet resultSet, final Selection selection)
            throws SQLException {
        final ParamMeta paramMeta = selection.paramMeta();
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
            value = mappingType.afterGet(sqlType, this.factory.mapEnv, value);
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
    private void getGenerateKeys(PreparedStatement statement, final long insertedRows, final GeneratedKeyStmt stmt)
            throws SQLException {
        final List<IDomain> domainList = stmt.domainList();
        if (insertedRows != domainList.size()) {
            throw valueInsertDomainWrapperSizeError(insertedRows, domainList.size());
        }

        final String primaryKeyName = stmt.primaryKeyName();
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
     * @see #getGenerateKeys(PreparedStatement, long, GeneratedKeyStmt)
     */
    private static int doExtractId(final ResultSet idResultSet, final GeneratedKeyStmt stmt) throws SQLException {
        final List<IDomain> domainList = stmt.domainList();
        int index = 0;
        try (ResultSet resultSet = idResultSet) {
            final String primaryKeyName = stmt.primaryKeyName();
            final PrimaryFieldMeta<?> idField = stmt.idMeta();
            final Class<?> idJavaType = idField.javaType();
            final ObjectAccessor accessor = stmt.domainAccessor();
            IDomain domain;
            for (; resultSet.next(); index++) {
                domain = domainList.get(index);
                if (idJavaType == Integer.class) {
                    accessor.set(domain, primaryKeyName, resultSet.getInt(1));
                } else if (idJavaType == Long.class) {
                    accessor.set(domain, primaryKeyName, resultSet.getLong(1));
                } else if (idJavaType == BigInteger.class) {
                    accessor.set(domain, primaryKeyName, resultSet.getObject(1, BigInteger.class));
                } else {
                    throw _Exceptions.autoIdErrorJavaType(idField);
                }
            }
            if (index != domainList.size()) {
                throw insertedRowsAndGenerateIdNotMatch(domainList.size(), index);
            }
            return index;
        } catch (IndexOutOfBoundsException e) {
            throw insertedRowsAndGenerateIdNotMatch(domainList.size(), index);
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

    private static ArmyException childInsertStmtIdParamError() {
        return new ArmyException("Child insert id param error.");
    }

    private static ArmyException pairInsertStmtError() {
        String m = String.format("stmt's child stmt is %s", ChildInsertStmt.class.getName());
        throw new ArmyException(m);
    }


    private static SQLException insertedRowsAndGenerateIdNotMatch(int insertedRows, int generateIdCount) {
        String m = String.format("insertedRows[%s] and generateKeys count[%s] not match.", insertedRows, generateIdCount);
        return new SQLException(m);
    }


}