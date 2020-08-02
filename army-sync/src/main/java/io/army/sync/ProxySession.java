package io.army.sync;

import io.army.GenericProxySession;

public interface ProxySession extends GenericSingleDatabaseSyncSession, GenericProxySession {

    @Override
    SessionFactory sessionFactory();

}
