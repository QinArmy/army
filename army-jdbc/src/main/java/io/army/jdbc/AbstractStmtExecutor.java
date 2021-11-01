package io.army.jdbc;

import io.army.beans.ObjectWrapper;
import io.army.codec.FieldCodec;
import io.army.codec.FieldCodecReturnException;
import io.army.codec.StatementType;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;
import io.army.meta.ParamMeta;
import io.army.meta.ServerMeta;
import io.army.meta.mapping.MappingType;
import io.army.sqldatatype.SQLDataType;
import io.army.stmt.*;
import io.army.sync.executor.StmtExecutor;
import io.army.sync.utils.SyncExceptions;
import io.army.util.ArmyException;
import io.army.util.Exceptions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        try {
            final int insertRows;
            final int timeout;
            if (txTimeout < 0) {
                timeout = stmt.getTimeout();
            } else {
                timeout = txTimeout;
            }
            if (stmt instanceof SimpleStmt) {
                insertRows = this.executeUpdate((SimpleStmt) stmt, timeout);
            } else if (stmt instanceof PairStmt) {
                final PairStmt pairStmt = (PairStmt) stmt;

                insertRows = this.executePariInsert(pairStmt, timeout);
            } else if (stmt instanceof BatchSimpleStmt) {

            } else if (stmt instanceof ChildBatchStmt) {

            } else {
                throw Exceptions.unexpectedStmt(stmt);
            }
            return insertRows;
        } catch (SQLException e) {
            throw SyncExceptions.wrapDataAccess(e);
        }
    }

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


    @Override
    public final void close() throws Exception {
        this.conn.close();
    }




    /*################################## blow packet template ##################################*/

    abstract void bind(PreparedStatement stmt, int index, SQLDataType sqlDataType, @Nullable Object value)
            throws SQLException;

    /**
     * @see #executeUpdate(SimpleStmt, int)
     */
    abstract void getGenerateKeys(PreparedStatement stmt, final int insertedRows, List<ObjectWrapper> wrapperList)
            throws SQLException;


    /*################################## blow private method ##################################*/


    private int executePariInsert(final PairStmt stmt, final int timeout) throws SQLException {
        final long startMills = System.currentTimeMillis();
        final int parentRows, childRows;
        // firstly, insert parent
        parentRows = this.executeUpdate(stmt.parentStmt(), timeout);

        final SimpleStmt childStmt = stmt.childStmt();
        if (childStmt instanceof ValueInsertStmt) {
            String m = String.format("stmt's child stmt is %s", ValueInsertStmt.class.getName());
            throw new IllegalArgumentException(m);
        }
        // secondly, insert child
        childRows = this.executeUpdate(childStmt, getRestSeconds(timeout, startMills));
        if (parentRows != childRows) {
            throw parentChildRowsNotMatch(parentRows, childRows);
        }

    }

    private int executeUpdate(final SimpleStmt stmt, final int timeout) throws SQLException {
        try (PreparedStatement statement = this.conn.prepareStatement(stmt.sql())) {

            bindParameter(statement, stmt.paramGroup(), stmt.statementType());

            if (timeout > 0) {
                statement.setQueryTimeout(timeout);
            }
            final int rows;
            rows = statement.executeUpdate();

            if (stmt instanceof ValueInsertStmt) {
                final List<ObjectWrapper> wrapperList = ((ValueInsertStmt) stmt).domainWrappers();
                if (wrapperList.size() != rows) {
                    throw valueInsertDomainWrapperSizeError(rows, wrapperList.size());
                }
                getGenerateKeys(statement, rows, wrapperList);
            }
            return rows;
        }

    }


    private void bindParameter(PreparedStatement statement, List<ParamValue> paramGroup, StatementType statementType)
            throws SQLException {
        final int size = paramGroup.size();
        final ServerMeta serverMeta = this.serverMeta;

        ParamValue paramValue;
        Object value;
        MappingType mappingType;
        ParamMeta paramMeta;
        for (int i = 0; i < size; i++) {
            paramValue = paramGroup.get(i);
            value = paramValue.value();
            mappingType = paramValue.paramMeta().mappingMeta();
            if (value == null) {
                // bind null
                bind(statement, i + 1, mappingType.sqlDataType(serverMeta), null);
                continue;
            }
            paramMeta = paramValue.paramMeta();
            if (paramMeta instanceof FieldMeta) {
                FieldMeta<?, ?> fieldMeta = (FieldMeta<?, ?>) paramMeta;
                if (fieldMeta.codec()) {
                    value = encodeField(fieldMeta, value, statementType);
                }
            }
            value = mappingType.convertBeforeBind(value);
            if (mappingType.singleton()) {
                if (value == null || value.getClass() != mappingType.javaType()) {
                    throw convertBeforeBindMethodError(mappingType);
                }
            } else if (!mappingType.javaType().isInstance(value)) {
                throw convertBeforeBindMethodError(mappingType);
            }
            bind(statement, i + 1, mappingType.sqlDataType(serverMeta), value);

        }

    }


    private Object encodeField(final FieldMeta<?, ?> fieldMeta, Object nonNull, final StatementType statementType) {
        final FieldCodec fieldCodec;
        fieldCodec = this.fieldCodecMap.get(fieldMeta);
        if (fieldCodec == null) {
            throw createNoFieldCodecException(fieldMeta);
        }
        nonNull = fieldCodec.encode(fieldMeta, nonNull, statementType);
        if (nonNull == null || nonNull.getClass() != fieldMeta.javaType()) {
            throw createCodecReturnTypeException(fieldCodec, fieldMeta);
        }
        return nonNull;
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


}
