package io.army;

import io.army.tx.NoSessionTransactionException;
import io.army.tx.TmTransaction;

public interface TmSession extends GenericSyncSession {

    @Override
    TmSessionFactory sessionFactory();

    @Override
    TmTransaction sessionTransaction() throws NoSessionTransactionException;


}
