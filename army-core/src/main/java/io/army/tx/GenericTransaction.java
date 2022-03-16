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


    int timeToLiveInSeconds() throws TransactionTimeOutException;

    long timeToLiveInMillis() throws TransactionTimeOutException;

    boolean rollbackOnly();

    void markRollbackOnly() throws TransactionException;

    boolean transactionEnded();
}
