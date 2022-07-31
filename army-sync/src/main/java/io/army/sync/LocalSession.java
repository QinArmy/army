package io.army.sync;

import io.army.lang.Nullable;
import io.army.session.SessionException;
import io.army.tx.Isolation;

/**
 * @see LocalSessionFactory
 */
public interface LocalSession extends SyncSession, AutoCloseable {


    LocalTransaction currentTransaction() throws SessionException;


    TransactionBuilder builder();

    @Override
    void close() throws SessionException;

    interface TransactionBuilder {

        TransactionBuilder name(@Nullable String txName);

        TransactionBuilder isolation(Isolation isolation);

        TransactionBuilder readonly(boolean readOnly);

        TransactionBuilder timeout(int timeoutSeconds);

        LocalTransaction build() throws SessionException;

    }

}
