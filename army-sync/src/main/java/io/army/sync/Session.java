package io.army.sync;

import io.army.lang.Nullable;
import io.army.tx.Isolation;
import io.army.tx.NoSessionTransactionException;
import io.army.tx.Transaction;
import io.army.tx.TransactionException;


public interface Session extends GenericSingleDatabaseSyncSession, GenericRmSession {

    @Override
    SessionFactory sessionFactory();

    Transaction sessionTransaction() throws NoSessionTransactionException;


    TransactionBuilder builder();

    interface TransactionBuilder {

        TransactionBuilder name(@Nullable String txName);

        TransactionBuilder isolation(Isolation isolation);

        TransactionBuilder readOnly(boolean readOnly);

        TransactionBuilder timeout(int timeout);

        Transaction build() throws TransactionException;

    }
}
