package io.army.boot;

import io.army.Session;
import io.army.codec.StatementType;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

interface InnerSession extends Session {

    PreparedStatement createStatement(String sql, boolean generatedKey) throws SQLException;

    PreparedStatement createStatement(String sql) throws SQLException;

    PreparedStatement createStatement(String sql, String[] columnNames) throws SQLException;

    InnerCodecContext codecContext();

    void codecContextStatementType(@Nullable StatementType statementType);

    Dialect dialect();
}
