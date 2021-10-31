package io.army.jdbc;

import io.army.lang.Nullable;
import io.army.meta.ServerMeta;
import io.army.meta.mapping.MappingType;
import io.army.sqldatatype.SQLDataType;
import io.army.stmt.ChildStmt;
import io.army.stmt.ParamValue;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.sync.executor.SqlExecutor;
import io.army.util.ArmyException;
import io.army.util.Executions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

abstract class AbstractSqlExecutor implements SqlExecutor {

    final Connection conn;

    ServerMeta serverMeta;

    AbstractSqlExecutor(Connection conn) {
        this.conn = conn;
    }


    @Override
    public final int valueInsert(final List<Stmt> stmtList) {
        int insertCount = 0;
        for (Stmt stmt : stmtList) {
            if (stmt instanceof SimpleStmt) {
                insertCount += this.simpleValueInsert((SimpleStmt) stmt);
            } else if (stmt instanceof ChildStmt) {

            } else {
                throw Executions.unexpectedStmt(stmt);
            }
        }
        return insertCount;
    }

    @Override
    public final void close() throws Exception {
        this.conn.close();
    }


    /*################################## blow packet template ##################################*/

    abstract void bind(PreparedStatement stmt, int index, SQLDataType sqlDataType, @Nullable Object value);


    /*################################## blow private method ##################################*/

    private int simpleValueInsert(final SimpleStmt stmt) {
        try (PreparedStatement statement = this.conn.prepareStatement(stmt.sql())) {

            bindParameter(statement, stmt.paramGroup());

        } catch (Throwable e) {

        }
        return 0;
    }

    private void bindParameter(PreparedStatement statement, List<ParamValue> paramGroup) {
        final int size = paramGroup.size();
        final ServerMeta serverMeta = this.serverMeta;
        ParamValue paramValue;
        Object value;
        MappingType mappingType;
        for (int i = 0; i < size; i++) {
            paramValue = paramGroup.get(i);
            value = paramValue.value();
            mappingType = paramValue.paramMeta().mappingMeta();
            if (value != null) {
                value = mappingType.convertBeforeBind(value);
                if (!mappingType.javaType().isInstance(value)) {
                    throw convertBeforeBindMethodError(mappingType);
                }
            }

            bind(statement, i + 1, mappingType.sqlDataType(serverMeta), value);

        }

    }


    private static ArmyException convertBeforeBindMethodError(MappingType mappingType) {
        String m = String.format("%s.convertBeforeBind(Object nonNull) return error.", mappingType);
        throw new ArmyException(m);
    }


}
