package io.army.sync;

import io.army.dialect.Dialect;
import io.army.tx.GenericSyncTransaction;

import java.sql.Connection;

interface InnerTxSession {

    Connection connection();

    Dialect dialect();

    void closeTransaction(GenericSyncTransaction transaction);
}
