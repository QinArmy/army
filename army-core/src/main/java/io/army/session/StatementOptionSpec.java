package io.army.session;

public interface StatementOptionSpec {

    boolean isPreferServerPrepare();

    boolean isSupportTimeout();

    int restMillSeconds() throws TimeoutException;


}
