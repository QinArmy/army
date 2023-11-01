package io.army.session;


import javax.annotation.Nullable;

@Deprecated
public interface Transaction {

    Session session();

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
