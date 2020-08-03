package io.army.sync;

import io.army.GenericProxySession;
import io.army.boot.sync.GenericSyncApiSession;

/**
 * This interface is a proxy of {@link TmSession} in current thread context.
 * Classic use case is than is used by DAO in spring application.
 */
public interface ProxyTmSession extends GenericProxySession, GenericSyncApiSession {

    @Override
    TmSessionFactory sessionFactory();

}
