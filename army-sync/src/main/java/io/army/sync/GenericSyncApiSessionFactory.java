package io.army.sync;

public interface GenericSyncApiSessionFactory extends GenericSyncSessionFactory {

    ProxySession proxySession();

    boolean hasCurrentSession();

    boolean currentSessionContextIsInstanceOf(Class<?> currentSessionContextClass);
}
