package io.army.tx;


public interface GenericTransaction {

    TransactionStatus status();

    String name();

    Isolation isolation();

    boolean readOnly();

    long getTimeToLiveInMillis() throws TransactionTimeOutException;


}
