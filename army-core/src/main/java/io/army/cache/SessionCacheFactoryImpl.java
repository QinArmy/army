package io.army.cache;

import io.army.session.GenericSession;
import io.army.session.GenericSessionFactory;

final class SessionCacheFactoryImpl implements SessionCacheFactory {

    static SessionCacheFactoryImpl build(GenericSessionFactory sessionFactory) {
        return new SessionCacheFactoryImpl(DomainProxyFactory.build(sessionFactory));
    }


    private final DomainProxyFactory domainProxyFactory;

    private SessionCacheFactoryImpl(DomainProxyFactory domainProxyFactory) {
        this.domainProxyFactory = domainProxyFactory;
    }

    @Override
    public SessionCache createSessionCache(GenericSession session) {
        return new SessionCacheImpl(this.domainProxyFactory, session);
    }
}
