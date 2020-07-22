package io.army.tx;


public interface GenericTransaction {

     String name();

     Isolation isolation();

     boolean readOnly();

     long getTimeToLiveInMillis() throws TransactionTimeOutException;


}
