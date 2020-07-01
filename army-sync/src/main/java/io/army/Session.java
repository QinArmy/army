package io.army;

import io.army.lang.Nullable;
import io.army.tx.Isolation;
import io.army.tx.NoSessionTransactionException;
import io.army.tx.Transaction;

import javax.transaction.TransactionalException;

public interface Session extends RmSession {

    @Override
    SessionFactory sessionFactory();

    @Override
    Transaction sessionTransaction() throws NoSessionTransactionException;


    TransactionBuilder builder(boolean readOnly, Isolation isolation, int timeoutSeconds)
            throws TransactionalException;

    interface TransactionBuilder {

        TransactionBuilder name(@Nullable String txName);

        Transaction build() throws TransactionalException;

    }
}
