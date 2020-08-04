package io.army.boot.sync;

import io.army.codec.StatementType;
import io.army.dialect.Dialect;
import io.army.lang.Nullable;
import io.army.sync.GenericSyncRmSession;
import io.army.tx.TransactionTimeOutException;

import java.sql.PreparedStatement;
import java.sql.SQLException;

interface InnerGenericRmSession extends GenericSyncRmSession, InnerTxSession {

    PreparedStatement createStatement(String sql, boolean generatedKey) throws SQLException;

    PreparedStatement createStatement(String sql) throws SQLException;

    PreparedStatement createStatement(String sql, String[] columnNames) throws SQLException;

    InnerCodecContext codecContext();

    void codecContextStatementType(@Nullable StatementType statementType);

    Dialect dialect();

    int timeToLiveInSeconds() throws TransactionTimeOutException;

    boolean supportSharding();
}
