package io.army;

import io.army.boot.RmSessionFactory;

/**
 *
 */
public interface SessionFactory extends RmSessionFactory {

    ProxySession proxySession();

    boolean hasCurrentSession();

    boolean currentSessionContextIsInstanceOf(Class<?> currentSessionContextClass);


    SessionBuilder builder();


    interface SessionBuilder {

        SessionBuilder currentSession(boolean current);

        SessionBuilder resetConnection(boolean reset);

        Session build() throws SessionException;

    }
}
