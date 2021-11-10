package io.army.session;

import io.army.tx.GenericTransaction;
import io.army.tx.NoSessionTransactionException;

/**
 *
 */
public interface GenericSession {


    boolean readonly();

    boolean closed();

    boolean hasTransaction();

    GenericTransaction sessionTransaction() throws NoSessionTransactionException;

    GenericSessionFactory sessionFactory();

}
