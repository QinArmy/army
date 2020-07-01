package io.army.tx;


 interface GenericTransaction {

     String name();

     Isolation isolation();

     boolean readOnly();

     long getTimeToLiveInMillis() throws TransactionTimeOutException;


}
