package io.army.boot.sync;

import io.army.GenericProxySession;
import io.army.sync.GenericSyncSession;
import io.army.sync.TmSessionFactory;

public interface ProxyTmSession extends GenericSyncSession, GenericProxySession {

    @Override
    TmSessionFactory sessionFactory();
}
