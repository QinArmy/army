package io.army.cache;

import io.army.session.GenericSession;
import io.army.session.GenericSessionFactory;

public interface SessionCacheFactory {

    SessionCache createSessionCache(GenericSession session);

    static SessionCacheFactory build(GenericSessionFactory sessionFactory) {
        return SessionCacheFactoryImpl.build(sessionFactory);
    }
}
