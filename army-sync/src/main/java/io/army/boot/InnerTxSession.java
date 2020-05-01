package io.army.boot;

import io.army.Session;
import io.army.dialect.Dialect;
import io.army.tx.Transaction;

import java.sql.Connection;

interface InnerTxSession extends Session {

    Connection connection();

    Dialect dialect();

    void closeTransaction(Transaction transaction);
}
