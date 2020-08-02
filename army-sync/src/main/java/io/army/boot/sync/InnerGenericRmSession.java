package io.army.boot.sync;

import io.army.codec.StatementType;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.sync.GenericRmSession;

import java.sql.PreparedStatement;
import java.sql.SQLException;

interface InnerGenericRmSession extends GenericRmSession {

    PreparedStatement createStatement(String sql, boolean generatedKey) throws SQLException;

    PreparedStatement createStatement(String sql) throws SQLException;

    PreparedStatement createStatement(String sql, String[] columnNames) throws SQLException;

    InnerCodecContext codecContext();

    void codecContextStatementType(@Nullable StatementType statementType);

    Dialect dialect();
}
