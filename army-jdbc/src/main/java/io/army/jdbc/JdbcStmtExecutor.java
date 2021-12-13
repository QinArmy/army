package io.army.jdbc;

import io.army.session.DataAccessException;
import io.army.stmt.Stmt;
import io.army.sync.executor.StmtExecutor;
import io.army.sync.utils.SyncExceptions;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

final class JdbcStmtExecutor implements StmtExecutor {

    static JdbcStmtExecutor create(Connection conn) {
        return new JdbcStmtExecutor(conn);
    }

    private final Connection conn;

    private JdbcStmtExecutor(Connection conn) {
        this.conn = conn;
    }


    @Override
    public int valueInsert(Stmt stmt, int txTimeout) {
        return 0;
    }

    @Override
    public <T> List<T> returnInsert(Stmt stmt, int txTimeout, Class<T> resultClass) {
        return null;
    }

    @Override
    public int update(Stmt stmt, int txTimeout) {
        return 0;
    }

    @Override
    public void close() throws DataAccessException {
        try {
            this.conn.close();
        } catch (SQLException e) {
            throw SyncExceptions.wrapDataAccess(e);
        }
    }

    @Override
    public <T> List<T> select(Stmt stmt, int txTimeout, Class<T> resultClass) {
        return null;
    }
}
