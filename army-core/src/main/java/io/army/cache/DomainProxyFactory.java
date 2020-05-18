package io.army.cache;

import io.army.GenericSessionFactory;
import io.army.domain.IDomain;
import io.army.util.Pair;

interface DomainProxyFactory {

    Pair<IDomain, DomainUpdateAdvice> createDomainProxy(IDomain domain);

    static DomainProxyFactory build(GenericSessionFactory sessionFactory) {
        return DomainProxyFactoryImpl.build(sessionFactory);
    }

}
