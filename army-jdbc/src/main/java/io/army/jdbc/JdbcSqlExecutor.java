package io.army.jdbc;

import io.army.sync.executor.SqlExecutor;

import java.sql.Connection;

final class JdbcSqlExecutor implements SqlExecutor {

    static JdbcSqlExecutor create(Connection conn) {
        return new JdbcSqlExecutor(conn);
    }

    private final Connection conn;

    private JdbcSqlExecutor(Connection conn) {
        this.conn = conn;
    }





    @Override
    public void close() throws Exception {
        this.conn.close();
    }


}
