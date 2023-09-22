package io.army.session;

public interface StatementOptionSpec {

    boolean isPreferServerPrepare();


    boolean isSupportTimeout();


    int timeoutMillSeconds();

    /**
     * @return negative : no time out
     */
    int restMillSeconds() throws TimeoutException;


}
