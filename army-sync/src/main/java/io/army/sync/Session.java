package io.army.sync;

import io.army.SessionException;
import io.army.lang.Nullable;
import io.army.tx.Isolation;
import io.army.tx.Transaction;
import io.army.tx.TransactionException;


public interface Session extends SyncSession, AutoCloseable {


    Transaction sessionTransaction() throws SessionException;


    TransactionBuilder builder();

    @Override
    void close() throws SessionException;

    interface TransactionBuilder {

        TransactionBuilder name(@Nullable String txName);

        TransactionBuilder isolation(Isolation isolation);

        TransactionBuilder readOnly(boolean readOnly);

        TransactionBuilder timeout(int timeout);

        Transaction build() throws TransactionException;

    }

}
