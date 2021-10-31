package io.army.jdbc;


import java.sql.Connection;

final class MySQL57Executor extends AbstractSqlExecutor {

    static MySQL57Executor create(Connection conn) {
        return new MySQL57Executor(conn);
    }

    private MySQL57Executor(Connection conn) {
        super(conn);
    }







}
