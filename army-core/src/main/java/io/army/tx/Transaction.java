package io.army.tx;


import io.army.lang.Nullable;
import io.army.session.GenericSession;

public interface Transaction {

    GenericSession session();

    @Nullable
    String name();

    Isolation isolation();

    boolean readOnly();

    Enum<?> status();


    /**
     * @return the next query timeout limit in seconds; zero means there is no limit
     */
    int nextTimeout() throws TransactionTimeOutException;


    boolean rollbackOnly();

    void markRollbackOnly() throws TransactionException;

}
