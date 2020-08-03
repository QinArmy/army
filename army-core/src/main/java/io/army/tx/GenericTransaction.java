package io.army.tx;


public interface GenericTransaction {

    String name();

    Isolation isolation();

    boolean readOnly();

    Enum<?> status();

    long getTimeToLiveInMillis() throws TransactionTimeOutException;

    boolean rollbackOnly();

    void markRollbackOnly() throws TransactionException;

    boolean transactionEnded();
}
