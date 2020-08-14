package io.army.sync;

/**
 * This interface is a proxy of {@link Session} in current thread context.
 * Classic use case is than is used by DAO in spring application.
 */
public interface ProxySession extends SingleDatabaseSyncSession, GenericSyncProxySession {

    @Override
    SessionFactory sessionFactory();

}
