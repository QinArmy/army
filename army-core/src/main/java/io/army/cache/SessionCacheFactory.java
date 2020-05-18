package io.army.cache;

import io.army.GenericSession;
import io.army.GenericSessionFactory;

public interface SessionCacheFactory {

    SessionCache createSessionCache(GenericSession session);

    static SessionCacheFactory build(GenericSessionFactory sessionFactory) {
        return SessionCacheFactoryImpl.build(sessionFactory);
    }
}
