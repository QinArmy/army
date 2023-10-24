package io.army.session;

public interface StatementOption {

    boolean isPreferServerPrepare();


    boolean isSupportTimeout();


    int timeoutMillSeconds();

    /**
     * @return negative : no time out
     */
    int restMillSeconds() throws TimeoutException;

    int fetchSize();

    MultiStmtMode multiStmtMode();


}
