package io.army.tx;


public interface GenericTransaction {

     String name();

     Isolation isolation();

     boolean readOnly();

     long getTimeToLiveInMillis() throws TransactionTimeOutException;

     boolean rollbackOnly();

     void markRollbackOnly() throws TransactionException;
}
