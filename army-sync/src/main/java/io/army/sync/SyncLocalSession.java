package io.army.sync;

import io.army.lang.Nullable;
import io.army.session.SessionException;
import io.army.tx.Isolation;

/**
 * <p>
 * This interface representing blocking way local session.
 * </p>
 *
 * @see SyncLocalSessionFactory
 * @since 1.0
 */
public interface SyncLocalSession extends SyncSession, AutoCloseable {

    @Override
    SyncLocalSessionFactory sessionFactory();


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
