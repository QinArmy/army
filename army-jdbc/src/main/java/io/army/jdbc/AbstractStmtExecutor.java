package io.army.jdbc;

import io.army.ArmyException;
import io.army.beans.ObjectWrapper;
import io.army.codec.FieldCodec;
import io.army.codec.FieldCodecReturnException;
import io.army.generator.PostFieldGenerator;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;
import io.army.meta.ParamMeta;
import io.army.meta.ServerMeta;
import io.army.modelgen._MetaBridge;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;
import io.army.stmt.*;
import io.army.sync.executor.StmtExecutor;
import io.army.sync.utils.SyncExceptions;
import io.army.util._Exceptions;

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

abstract class AbstractStmtExecutor implements StmtExecutor {

    final Connection conn;

    ServerMeta serverMeta;

    Map<FieldMeta<?, ?>, FieldCodec> fieldCodecMap;

    AbstractStmtExecutor(Connection conn) {
        this.conn = conn;
    }

    @Override
    public final int valueInsert(final Stmt stmt, final int txTimeout) {
        final int timeout;
        if (txTimeout < 0) {
            timeout = stmt.getTimeout();
        } else {
            timeout = txTimeout;
        }
        try {
            final int insertRows;
            if (stmt instanceof SimpleStmt) {
                insertRows = this.executeInsert((SimpleStmt) stmt, timeout);
            } else if (stmt instanceof PairStmt) {
                insertRows = this.executePariInsert((PairStmt) stmt, timeout);
            } else if (stmt instanceof BatchStmt) {
                final int[] rows;
                int rowSum = 0;
                rows = this.executeBatchInsert((BatchStmt) stmt, timeout);
                for (int row : rows) {
                    rowSum += row;
                }
                insertRows = rowSum;
            } else if (stmt instanceof PairBatchStmt) {
                insertRows = this.executePairBatchInsert((PairBatchStmt) stmt, timeout);
            } else {
                throw _Exceptions.unexpectedStmt(stmt);
            }
            return insertRows;
        } catch (SQLException e) {
            throw SyncExceptions.wrapDataAccess(e);
        }
    }

    @Override
    public final <T> List<T> returnInsert(Stmt stmt, int txTimeout, Class<T> resultClass) {
        return null;
    }

    @Override
    public final int update(Stmt stmt, int txTimeout) {
        return 0;
    }


    @Override
    public final <T> List<T> select(Stmt stmt, int txTimeout, Class<T> resultClass) {
        return null;
    }

    @Override
    public final void close() throws DataAccessException {
        try {
            this.conn.close();
        } catch (SQLException e) {
            throw SyncExceptions.wrapDataAccess(e);
        }
    }




    /*################################## blow packet template ##################################*/

    abstract void bind(PreparedStatement stmt, int index, SqlType sqlDataType, Object nonNull)
            throws SQLException;


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
            throw SyncExceptions.timeout(timeout, restMills);
        }
        int restSeconds = (int) (restMills / 1000L);
        if (restSeconds == 0) {
            restSeconds++;
        }
        return restSeconds;
    }


    private int executePariInsert(final PairStmt stmt, final int timeout) throws SQLException {

        final SimpleStmt parentStmt = stmt.parentStmt(), childStmt = stmt.childStmt();
        if (parentStmt instanceof InsertStmt && !(childStmt instanceof ChildInsertStmt)) {
            throw pairInsertStmtError();
        }

        final long startMills = System.currentTimeMillis();
        final int parentRows, childRows;
        // firstly, insert parent
        parentRows = this.executeInsert(parentStmt, timeout);
        // secondly, insert child
        childRows = this.executeInsert(childStmt, getRestSeconds(timeout, startMills));
        if (parentRows != childRows) {
            throw parentChildRowsNotMatch(parentRows, childRows);
        }
        return parentRows;
    }

    private int executeInsert(final SimpleStmt stmt, final int timeout) throws SQLException {
        final boolean generateKeys, returnId;
        final int resultSetType;
        if (stmt instanceof InsertStmt) {
            returnId = ((InsertStmt) stmt).selectionList().size() == 1;
            generateKeys = !returnId;
            resultSetType = returnId ? Statement.NO_GENERATED_KEYS : Statement.RETURN_GENERATED_KEYS;
        } else {
            returnId = false;
            generateKeys = false;
            resultSetType = Statement.NO_GENERATED_KEYS;
        }
        try (PreparedStatement statement = this.conn.prepareStatement(stmt.sql(), resultSetType)) {

            bindParameter(statement, stmt.paramGroup(), stmt);

            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }
            final int rows;
            if (returnId) {
                rows = extractReturnId(statement, (InsertStmt) stmt);
            } else {
                rows = statement.executeUpdate();
                if (generateKeys) {
                    getGenerateKeys(statement, rows, (InsertStmt) stmt);
                }
            }

            return rows;
        }

    }


    private int executePairBatchInsert(final PairBatchStmt stmt, final int timeout) throws SQLException {

        final BatchStmt parentStmt = stmt.parentStmt(), childStmt = stmt.childStmt();
        if (parentStmt instanceof InsertStmt && !(childStmt instanceof ChildInsertStmt)) {
            throw pairInsertStmtError();
        }
        final long startMills = System.currentTimeMillis();
        final int[] parentRows, childRows;
        // firstly, insert parent
        parentRows = this.executeBatchInsert(parentStmt, timeout);
        // secondly, insert child
        childRows = this.executeBatchInsert(childStmt, getRestSeconds(timeout, startMills));
        if (parentRows.length != childRows.length) {
            throw parentChildRowsNotMatch(parentRows.length, childRows.length);
        }
        int rowSum = 0;
        for (int i = 0, rows; i < parentRows.length; i++) {
            rows = parentRows[i];
            if (rows != childRows[i]) {
                throw parentChildRowsNotMatch(rows, childRows[i]);
            }
            rowSum += rows;
        }
        return rowSum;
    }


    private int[] executeBatchInsert(final BatchStmt stmt, final int timeout) throws SQLException {
        final boolean generateKeys, returnId;
        final int resultSetType;
        if (stmt instanceof InsertStmt) {
            returnId = ((InsertStmt) stmt).selectionList().size() == 1;
            generateKeys = !returnId;
            resultSetType = returnId ? Statement.NO_GENERATED_KEYS : Statement.RETURN_GENERATED_KEYS;
        } else {
            returnId = false;
            generateKeys = false;
            resultSetType = Statement.NO_GENERATED_KEYS;
        }

        try (PreparedStatement statement = this.conn.prepareStatement(stmt.sql(), resultSetType)) {
            final int[] rows;
            if (returnId) {
                final List<List<ParamValue>> groupList = stmt.groupList();
                rows = new int[groupList.size()];
                final long startMills = System.currentTimeMillis();
                for (int i = 0; i < rows.length; i++) {
                    final List<ParamValue> group = groupList.get(i);
                    bindParameter(statement, group, stmt);
                    if (timeout > 0) {
                        statement.setQueryTimeout(getRestSeconds(timeout, startMills));
                    }
                    rows[i] = extractReturnId(statement, (InsertStmt) stmt);
                    statement.clearParameters();
                }
            } else {
                for (List<ParamValue> group : stmt.groupList()) {
                    bindParameter(statement, group, stmt);
                    statement.addBatch();
                }
                if (timeout > 0) {
                    statement.setQueryTimeout(timeout);
                }

                rows = statement.executeBatch();

                if (generateKeys) {
                    int rowSum = 0;
                    for (int row : rows) {
                        rowSum += row;
                    }
                    getGenerateKeys(statement, rowSum, (InsertStmt) stmt);
                }
            }
            return rows;
        }

    }


    /**
     * @see #executeInsert(SimpleStmt, int)
     */
    private void bindParameter(PreparedStatement statement, List<ParamValue> paramGroup, Stmt stmt)
            throws SQLException {
        final int size = paramGroup.size();
        final ServerMeta serverMeta = this.serverMeta;

        ParamValue paramValue;
        Object value;
        MappingType mappingType;
        ParamMeta paramMeta;
        SqlType sqlDataType;
        for (int i = 0; i < size; i++) {
            paramValue = paramGroup.get(i);
            value = paramValue.value();
            mappingType = paramValue.paramMeta().mappingType();
            if (value == null) {
                // bind null
                statement.setNull(i + 1, mappingType.jdbcType().getVendorTypeNumber());
                continue;
            }
            paramMeta = paramValue.paramMeta();
            if (paramMeta instanceof FieldMeta) {
                final FieldMeta<?, ?> fieldMeta = (FieldMeta<?, ?>) paramMeta;
                if (stmt instanceof ChildInsertStmt && fieldMeta.primary()
                        && !(paramMeta instanceof AutoIdParamValue)) {
                    throw childInsertStmtIdParamError();
                }
                if (fieldMeta.codec()) {
                    value = encodeField(fieldMeta, value);
                }
            }
            sqlDataType = mappingType.sqlType(serverMeta);
            value = mappingType.convertBeforeBind(sqlDataType, value);
            bind(statement, i + 1, sqlDataType, value);

        }

    }


    private Object encodeField(final FieldMeta<?, ?> fieldMeta, Object nonNull) {
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
    private void getGenerateKeys(PreparedStatement statement, final int insertedRows, InsertStmt stmt)
            throws SQLException {
        int index = 0;
        try (ResultSet resultSet = statement.getGeneratedKeys()) {
            if (!resultSet.next()) {
                throw new SQLException("No generate keys ");
            }
            final List<ObjectWrapper> domainList = stmt.domainList();
            if (insertedRows != domainList.size()) {
                throw valueInsertDomainWrapperSizeError(insertedRows, domainList.size());
            }
            final CacheFunction<ResultSet, Object> function = getAutoIdFunction(stmt.idMeta().javaType());
            while (resultSet.next()) {
                domainList.get(index).set(_MetaBridge.ID, function.apply(resultSet));
                index++;
            }
            if (index != insertedRows) {
                throw insertedRowsAndGenerateIdNotMatch(insertedRows, index);
            }
        } catch (IndexOutOfBoundsException e) {
            throw insertedRowsAndGenerateIdNotMatch(insertedRows, index);
        }

    }



    /*################################## blow static method ##################################*/

    /**
     * @see #executeInsert(SimpleStmt, int)
     */
    private static int extractReturnId(final PreparedStatement statement, final InsertStmt stmt) throws SQLException {
        final List<ObjectWrapper> domainList = stmt.domainList();
        int index = 0;
        try (ResultSet resultSet = statement.executeQuery()) {

            CacheFunction<ResultSet, Object> function = getAutoIdFunction((stmt).idMeta().javaType());
            while (resultSet.next()) {
                domainList.get(index).set(_MetaBridge.ID, function.apply(resultSet));
                index++;
            }
            if (index != domainList.size()) {
                throw insertedRowsAndGenerateIdNotMatch(domainList.size(), index);
            }
            return index;
        } catch (IndexOutOfBoundsException e) {
            throw insertedRowsAndGenerateIdNotMatch(domainList.size(), index);
        }
    }


    /**
     * @see #extractReturnId(PreparedStatement, InsertStmt)
     * @see #getGenerateKeys(PreparedStatement, int, InsertStmt)
     */
    private static CacheFunction<ResultSet, Object> getAutoIdFunction(Class<?> javaType) {
        final CacheFunction<ResultSet, Object> function;
        if (javaType == Long.class) {
            function = AbstractStmtExecutor::getLongAutoId;
        } else if (javaType == Integer.class) {
            function = AbstractStmtExecutor::getIntAutoId;
        } else if (javaType == BigInteger.class) {
            function = AbstractStmtExecutor::getBigIntegerAutoId;
        } else {
            String m = String.format("%s not support java type[%s]"
                    , PostFieldGenerator.class.getName(), javaType.getName());
            throw new ArmyException(m);
        }
        return function;
    }

    private static long getLongAutoId(ResultSet resultSet) throws SQLException {
        final long id;
        id = resultSet.getLong(1);
        if (id == 0) {
            throw new SQLException("Generate key is 0");
        }
        return id;
    }

    private static int getIntAutoId(ResultSet resultSet) throws SQLException {
        final int id;
        id = resultSet.getInt(1);
        if (id == 0) {
            throw new SQLException("Generate key is 0");
        }
        return id;
    }

    private static BigInteger getBigIntegerAutoId(ResultSet resultSet) throws SQLException {
        final BigInteger id;
        id = resultSet.getObject(1, BigInteger.class);
        if (id == null || id.compareTo(BigInteger.ONE) < 0) {
            throw new SQLException("Generate key is 0");
        }
        return id;
    }


    private static ArmyException convertBeforeBindMethodError(MappingType mappingType) {
        String m = String.format("%s.convertBeforeBind(Object nonNull) return error.", mappingType);
        throw new ArmyException(m);
    }

    private static FieldCodecReturnException createCodecReturnTypeException(FieldCodec fieldCodec
            , FieldMeta<?, ?> fieldMeta) {
        return new FieldCodecReturnException("FieldCodec[%s] return  error,FieldMeta[%s],"
                , fieldCodec, fieldMeta);
    }

    private static MetaException createNoFieldCodecException(FieldMeta<?, ?> fieldMeta) {
        return new MetaException("FieldMeta[%s] not found FieldCodec.", fieldMeta);
    }

    private static ArmyException valueInsertDomainWrapperSizeError(int insertedRows, int domainWrapperSize) {
        String m = String.format("InsertedRows[%s] and domainWrapperSize[%s] not match.", insertedRows, domainWrapperSize);
        throw new ArmyException(m);
    }

    private ArmyException parentChildRowsNotMatch(int parentRows, int childRows) {
        String m = String.format("Parent update rows[%s] and child update rows[%s] not match."
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


    /**
     * <p>
     * Can't use {@link java.util.function.Function},because throw {@link SQLException}
     * </p>
     *
     * @param <T>
     * @param <R>
     */
    interface CacheFunction<T, R> {

        R apply(T t) throws SQLException;

    }


}
