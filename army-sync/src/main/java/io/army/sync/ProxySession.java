package io.army.sync;

import io.army.session.GenericProxySession;

/**
 * This interface is a proxy of {@link Session} in current thread context.
 * Classic use case is than is used by DAO in spring application.
 */
public interface ProxySession extends GenericProxySession, SyncSession {

    @Override
    SessionFactory sessionFactory();

}
