package io.army.jdbc;

import io.army.ArmyException;
import io.army.bean.ObjectWrapper;
import io.army.codec.FieldCodec;
import io.army.codec.FieldCodecReturnException;
import io.army.criteria.Selection;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.session.DataAccessException;
import io.army.sqltype.SqlType;
import io.army.stmt.*;
import io.army.sync.executor.StmtExecutor;
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

abstract class JdbcStmtExecutor implements StmtExecutor {

    final Connection conn;

    ServerMeta serverMeta;

    Map<FieldMeta<?, ?>, FieldCodec> fieldCodecMap;

    JdbcStmtExecutor(JdbcExecutorFactory factory, Connection conn) {
        this.conn = conn;
    }

    @Override
    public final long insert(final Stmt stmt, final long txTimeout) {
        try {
            final int insertRows;
            if (stmt instanceof SimpleStmt) {
                insertRows = this.executeInsert((SimpleStmt) stmt, getSingleTimeout(txTimeout));
            } else if (stmt instanceof PairStmt) {
                insertRows = this.executePariInsert((PairStmt) stmt, txTimeout);
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


    @Override
    public final <T> List<T> select(Stmt stmt, int txTimeout, Class<T> resultClass) {
        return null;
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
            throw _Exceptions.timeout(timeout, restMills);
        }
        int restSeconds = (int) (restMills / 1000L);
        if (restSeconds == 0) {
            restSeconds++;
        }
        return restSeconds;
    }


    /**
     * @see #insert(Stmt, long)
     */
    private int executePariInsert(final PairStmt stmt, final long txTimeout) throws SQLException {
        int timeout;
        final long startTime;
        if (txTimeout > 0) {
            timeout = getSingleTimeout(txTimeout);
            startTime = System.currentTimeMillis();
        } else {
            timeout = 0;
            startTime = -1L;
        }
        final int insertRows;
        insertRows = this.executeInsert(stmt.parentStmt(), timeout);

        timeout = 0;
        if (txTimeout > 0) {
            final long restMillis;
            restMillis = txTimeout - (System.currentTimeMillis() - startTime);
            if (restMillis < 0L) {
                throw _Exceptions.transactionTimeout(restMillis);
            }
            timeout = getSingleTimeout(restMillis);
        }
        final int childRows;
        childRows = this.executeInsert(stmt.childStmt(), timeout);

        if (childRows != insertRows) {
            throw parentChildRowsNotMatch(insertRows, childRows);
        }
        return insertRows;
    }

    private int executeInsert(final SimpleStmt stmt, final int timeoutSeconds) throws SQLException {
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

            bindParameter(statement, stmt.paramGroup(), stmt);

            if (timeoutSeconds > 0) {
                statement.setQueryTimeout(timeoutSeconds);
            }
            final int rows;
            if (returningId) {
                rows = doExtractId(statement.executeQuery(), (GeneratedKeyStmt) stmt);
            } else {
                rows = statement.executeUpdate();
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
                statement.setNull(i + 1, 0);
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
            sqlDataType = mappingType.map(serverMeta);
            value = mappingType.beforeBind_(sqlDataType, value);
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
    private void getGenerateKeys(PreparedStatement statement, final int insertedRows, final GeneratedKeyStmt stmt)
            throws SQLException {
        final List<ObjectWrapper> domainList = stmt.domainList();
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


    /**
     * @see #executeInsert(SimpleStmt, int)
     * @see #getGenerateKeys(PreparedStatement, int, GeneratedKeyStmt)
     */
    private static int doExtractId(final ResultSet idResultSet, final GeneratedKeyStmt stmt) throws SQLException {
        final List<ObjectWrapper> domainList = stmt.domainList();
        int index = 0;
        try (ResultSet resultSet = idResultSet) {
            final String primaryKeyName = stmt.primaryKeyName();
            final PrimaryFieldMeta<?, ?> idField = stmt.idMeta();
            final Class<?> idJavaType = idField.javaType();
            ObjectWrapper wrapper;
            for (; resultSet.next(); index++) {
                wrapper = domainList.get(index);
                if (idJavaType == Long.class) {
                    wrapper.set(primaryKeyName, resultSet.getLong(0));
                } else if (idJavaType == Integer.class) {
                    wrapper.set(primaryKeyName, resultSet.getInt(0));
                } else if (idJavaType == BigInteger.class) {
                    wrapper.set(primaryKeyName, resultSet.getObject(0, BigInteger.class));
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
