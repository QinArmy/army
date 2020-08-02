package io.army.boot.sync;

import io.army.GenericProxySession;
import io.army.TmSessionFactory;
import io.army.sync.GenericSyncSession;

public interface ProxyTmSession extends GenericSyncSession, GenericProxySession {

    @Override
    TmSessionFactory sessionFactory();
}
