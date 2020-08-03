package io.army.sync;

import io.army.GenericProxySession;

/**
 * This interface is a proxy of {@link Session} in current thread context.
 * Classic use case is than is used by DAO in spring application.
 */
public interface ProxySession extends GenericSingleDatabaseSyncSession, GenericProxySession {

    @Override
    SessionFactory sessionFactory();

}
