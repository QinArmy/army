package io.army.sync;

import io.army.dialect._Dialect;
import io.army.tx.GenericSyncTransaction;

import java.sql.Connection;

interface InnerTxSession {

    Connection connection();

    _Dialect dialect();

    void closeTransaction(GenericSyncTransaction transaction);
}
