package io.army.jdbc;

import io.army.sync.executor.StmtExecutor;

import java.sql.Connection;

final class JdbcStmtExecutor implements StmtExecutor {

    static JdbcStmtExecutor create(Connection conn) {
        return new JdbcStmtExecutor(conn);
    }

    private final Connection conn;

    private JdbcStmtExecutor(Connection conn) {
        this.conn = conn;
    }





    @Override
    public void close() throws Exception {
        this.conn.close();
    }


}
