package io.army.jdbc;

import io.army.stmt.Stmt;
import io.army.sync.executor.StmtExecutor;

import java.sql.Connection;
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
    public void close() throws Exception {
        this.conn.close();
    }


}
