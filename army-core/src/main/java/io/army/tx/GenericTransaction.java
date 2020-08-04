package io.army.tx;


import io.army.lang.Nullable;

public interface GenericTransaction {

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
