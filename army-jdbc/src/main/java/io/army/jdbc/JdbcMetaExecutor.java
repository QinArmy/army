package io.army.jdbc;

import io.army.meta.ServerMeta;
import io.army.sync.executor.MetaExecutor;

import java.sql.Connection;
import java.sql.SQLException;

final class JdbcMetaExecutor implements MetaExecutor {

    static JdbcMetaExecutor create(Connection conn) {
        return new JdbcMetaExecutor(conn);
    }

    private final Connection conn;

    private JdbcMetaExecutor(Connection conn) {
        this.conn = conn;
    }

    @Override
    public ServerMeta serverMeta() {
        throw new UnsupportedOperationException("TODO ");
    }

    @Override
    public void close() throws SQLException {
        this.conn.close();
    }


}
