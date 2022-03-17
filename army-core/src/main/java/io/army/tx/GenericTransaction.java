package io.army.tx;


import io.army.lang.Nullable;
import io.army.session.GenericSession;

public interface GenericTransaction {

    GenericSession session();

    @Nullable
    String name();

    Isolation isolation();

    boolean readOnly();

    Enum<?> status();

    boolean nonActive();

    /**
     * @return the next query timeout limit in seconds; zero means there is no limit
     */
    int nextTimeout() throws TransactionTimeOutException;


    boolean rollbackOnly();

    void markRollbackOnly() throws TransactionException;

    boolean transactionEnded();
}
