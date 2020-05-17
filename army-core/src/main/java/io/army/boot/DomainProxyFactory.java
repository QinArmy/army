package io.army.boot;

import io.army.GenericSessionFactory;
import io.army.aop.DomainUpdateAdvice;
import io.army.beans.DomainWrapper;
import io.army.util.Pair;

interface DomainProxyFactory {

    Pair<Object, DomainUpdateAdvice> createDomainProxy(DomainWrapper domainWrapper);

    static DomainProxyFactory build(GenericSessionFactory sessionFactory) {
        return DomainProxyFactoryImpl.build(sessionFactory);
    }

}
