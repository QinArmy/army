package io.army.sync;

import io.army.lang.Nullable;
import io.army.session.SessionException;
import io.army.tx.Isolation;
import io.army.tx.Transaction;
import io.army.tx.TransactionException;

/**
 * @see SessionFactory
 */
public interface Session extends SyncSession, AutoCloseable {


    Transaction sessionTransaction() throws SessionException;


    TransactionBuilder builder();

    @Override
    void close() throws SessionException;

    interface TransactionBuilder {

        TransactionBuilder name(@Nullable String txName);

        TransactionBuilder isolation(Isolation isolation);

        TransactionBuilder readonly(boolean readOnly);

        TransactionBuilder timeout(int timeoutSeconds);

        Transaction build() throws TransactionException;

    }

}
