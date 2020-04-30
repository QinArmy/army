package io.army;

public interface SessionFactory extends GenericSessionFactory {


    ProxySession proxySession();

    SessionBuilder builder();


    interface SessionBuilder {

        SessionBuilder currentSession();

        Session build() throws SessionException;

    }


}
